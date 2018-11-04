//
//  BitletOperation.swift
//  Bitlet Synchronizer Pod
//
//  Library: a bitlet operation
//  Used to execute one or more tasks operating on the bitlet synchronizer, useful when multiple calls are needed
//  Operation sequences do one call after the other (while waiting for the previous to finish)
//  Operation groups do multiple calls at once
//  Operations can also be nested in each other
//

public protocol BitletOperation {
    
    func start(bitletSynchronizer: BitletSynchronizer, completion: ((_ error: Error?, _ canceled: Bool) -> Void)?) -> Bool
    func cancel()
    
}

public class BitletOperationBase: BitletOperation {
    
    // --
    // MARK: Base members
    // --

    var running = false
    var requestCancel = false
    var bitletSynchronizer: BitletSynchronizer?
    var completion: ((_ error: Error?, _ canceled: Bool) -> Void)?
    var items = [BitletOperationItem]()
    var retainSelfWhileRunning: BitletOperation?
    var error: Error?

    
    // --
    // MARK: Base implementation
    // --

    public func start(bitletSynchronizer: BitletSynchronizer, completion: ((_ error: Error?, _ canceled: Bool) -> Void)?) -> Bool {
        if running {
            return false
        }
        retainSelfWhileRunning = self
        self.bitletSynchronizer = bitletSynchronizer
        self.completion = completion
        requestCancel = false
        running = true
        afterStart()
        return true
    }
    
    public func cancel() {
        requestCancel = true
    }
    

    // --
    // MARK: Base lifecycle
    // --

    func afterStart() {
        // Should be overridden
    }
    
    func complete() {
        if running {
            running = false
            completion?(error, requestCancel)
            completion = nil
            bitletSynchronizer = nil
            retainSelfWhileRunning = nil
        }
    }
    

    // --
    // MARK: Add operation items
    // --

    public func addBitletLoad<Handler: BitletHandler>(_ bitletHandler: Handler, cacheKey: String? = nil, forced: Bool = false, completion: ((_ bitlet: Handler.BitletData?, _ error: Error?, _ operation: BitletOperationBase) -> Void)?) {
        items.append(BitletOperationLoadItem(bitletHandler: bitletHandler, cacheKey: cacheKey, forced: forced, completion: { [weak self] bitlet, error in
            if let operation = self {
                completion?(bitlet, error, operation)
            }
        }))
    }
    
    public func add(operation: BitletOperation, completion: ((_ error: Error?, _ canceled: Bool, _ operation: BitletOperationBase) -> Void)?) {
        items.append(BitletOperationNestedItem(operation: operation, completion: { [weak self] error, canceled in
            if let operation = self {
                completion?(error, canceled, operation)
            }
        }))
    }
    

    // --
    // MARK: Cache check
    // --

    public func allCacheKeys() -> [String] {
        var cacheKeys = [String]()
        for item in items {
            if let cacheItem = item as? BitletOperationCacheItem, let cacheKey = cacheItem.cacheKey {
                cacheKeys.append(cacheKey)
            } else if let nestedItem = item as? BitletOperationNestedItem, let baseOperation = nestedItem.operation as? BitletOperationBase {
                cacheKeys.append(contentsOf: baseOperation.allCacheKeys())
            }
        }
        return cacheKeys
    }
    
    public func includedCacheKeys() -> [String] {
        var cacheKeys = [String]()
        for item in items {
            if item.enabled {
                if let cacheItem = item as? BitletOperationCacheItem, let cacheKey = cacheItem.cacheKey {
                    cacheKeys.append(cacheKey)
                } else if let nestedItem = item as? BitletOperationNestedItem, let baseOperation = nestedItem.operation as? BitletOperationBase {
                    cacheKeys.append(contentsOf: baseOperation.includedCacheKeys())
                }
            }
        }
        return cacheKeys
    }
    
    public func setCacheKeyIncluded(_ cacheKey: String, _ included: Bool) {
        for var item in items {
            if let cacheItem = item as? BitletOperationCacheItem, cacheItem.cacheKey == cacheKey {
                item.enabled = included
            } else if let nestedItem = item as? BitletOperationNestedItem, let baseOperation = nestedItem.operation as? BitletOperationBase {
                baseOperation.setCacheKeyIncluded(cacheKey, included)
            }
        }
    }
    
}

public class BitletOperationSequence: BitletOperationBase {
    
    // --
    // MARK: Sequence member
    // --

    private var itemIndex = -1
    

    // --
    // MARK: Sequence initialization
    // --

    public override init() {
    }
    

    // --
    // MARK: Sequence implementation
    // --

    override func afterStart() {
        itemIndex = -1
        next()
    }
    
    private func next() {
        if running {
            if !requestCancel && itemIndex + 1 < items.count {
                itemIndex += 1
                if let bitletSynchronizer = bitletSynchronizer, items[itemIndex].enabled {
                    items[itemIndex].run(bitletSynchronizer: bitletSynchronizer, completion: { [weak self] error in
                        self?.error = error
                        if self?.error != nil {
                            self?.cancel()
                        }
                        self?.next()
                    })
                } else {
                    next()
                }
            } else {
                complete()
            }
        }
    }
    
}

public class BitletOperationGroup: BitletOperationBase {
    
    // --
    // MARK: Group initialization
    // --

    public override init() {
    }


    // --
    // MARK: Group implementation
    // --

    override func afterStart() {
        for item in items {
            if let bitletSynchronizer = bitletSynchronizer, item.enabled {
                item.run(bitletSynchronizer: bitletSynchronizer, completion: { [weak self] error in
                    self?.error = error
                    if self?.error != nil {
                        self?.cancel()
                    }
                    self?.checkCompletion()
                })
            }
        }
        if items.count == 0 || bitletSynchronizer == nil {
            complete()
        }
    }
    
    private func checkCompletion() {
        if running {
            var itemRunning = false
            for item in items {
                if item.isRunning() {
                    itemRunning = true
                    break
                }
            }
            if requestCancel || !itemRunning {
                complete()
            }
        }
    }
    
}
