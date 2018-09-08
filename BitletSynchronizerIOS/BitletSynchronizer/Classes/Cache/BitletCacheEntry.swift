//
//  BitletCacheEntry.swift
//  Bitlet Synchronizer Pod
//
//  Library: a bitlet cache entry
//  Manages the data and state of a single entry in the bitlet cache
//

public enum BitletCacheState {
    
    case unavailable
    case loading
    case ready
    case refreshing
    
}

public class BitletCacheEntry {
    
    // --
    // MARK: Members
    // --

    public var state = BitletCacheState.unavailable
    public var bitletData: Any?
    public var bitletExpireTime: BitletExpireTime?
    private let handler: BaseBitletHandler
    private var loadingObserver: BitletCacheObserver?
    

    // --
    // MARK: Initialization
    // --

    init(handler: BaseBitletHandler) {
        self.handler = handler
    }
    

    // --
    // MARK: Loading
    // --

    func load(forced: Bool = false, observer: BaseBitletObserver) {
        if !forced && bitletData != nil && !expired() {
            return
        }
        var alreadyLoading = true
        if loadingObserver == nil {
            loadingObserver = BitletCacheObserver(completion: {
                if self.loadingObserver?.bitletData == nil && self.loadingObserver?.error == nil {
                    self.loadingObserver?.error = NSError(domain: "", code: 0, userInfo: [NSLocalizedDescriptionKey : "Unknown bitlet error"])
                }
                if self.loadingObserver?.error == nil {
                    self.bitletData = self.loadingObserver?.bitletData ?? self.bitletData
                    self.bitletExpireTime = self.loadingObserver?.bitletExpireTime ?? self.bitletExpireTime
                    self.state = .ready
                } else {
                    self.state = self.state == .refreshing ? .ready : .unavailable
                }
                self.loadingObserver = nil
            })
            alreadyLoading = false
        }
        loadingObserver?.include(observer: observer)
        if let loadingObserver = loadingObserver, !alreadyLoading {
            state = state == .unavailable ? .loading : .refreshing
            handler.loadData(observer: loadingObserver)
        }
    }
    
    
    // --
    // MARK: Access state
    // --
    
    public func expired() -> Bool {
        if let expireTime = bitletExpireTime {
            return expireTime <= BitletExpireTime.now()
        }
        return false
    }
    
    public func forceExpiration() {
        bitletExpireTime = .now()
    }
    
}

fileprivate class BitletCacheObserver: BaseBitletObserver {
    
    private var didSetBitletData = false
    private var didSetBitletHash = false
    private var didSetBitletExpireTime = false
    private var didSetError = false
    private var includedObservers = [BaseBitletObserver]()
    private let completionHandler: (() -> Void)

    var bitletData: Any? = nil {
        didSet {
            didSetBitletData = true
            for observer in includedObservers {
                observer.bitletData = bitletData
            }
        }
    }

    var bitletHash: String? {
        didSet {
            didSetBitletHash = true
            for observer in includedObservers {
                observer.bitletHash = bitletHash
            }
        }
    }
    
    var bitletExpireTime: BitletExpireTime? {
        didSet {
            didSetBitletExpireTime = true
            for observer in includedObservers {
                observer.bitletExpireTime = bitletExpireTime
            }
        }
    }
    
    var error: Error? {
        didSet {
            didSetError = true
            for observer in includedObservers {
                observer.error = error
            }
        }
    }
    
    init(completion: @escaping (() -> Void)) {
        completionHandler = completion
    }
    
    func finish() {
        completionHandler()
        for observer in includedObservers {
            observer.finish()
        }
    }
    
    func include(observer: BaseBitletObserver) {
        includedObservers.append(observer)
        if didSetBitletData {
            observer.bitletData = bitletData
        }
        if didSetBitletHash {
            observer.bitletHash = bitletHash
        }
        if didSetBitletExpireTime {
            observer.bitletExpireTime = bitletExpireTime
        }
        if didSetError {
            observer.error = error
        }
    }
    
}
