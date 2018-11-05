//
//  BitletOperationItem.swift
//  Bitlet Synchronizer Pod
//
//  Library: a bitlet operation item
//  A task item in a bitlet operation
//

import Foundation

public protocol BitletOperationItem {
    
    var enabled: Bool { get set }
    
    func run(bitletSynchronizer: BitletSynchronizer, completion: @escaping (_ error: Error?) -> Void)
    func isRunning() -> Bool
    
}

protocol BitletOperationCacheItem: BitletOperationItem {
    
    var cacheKey: String? { get }
    
}

class BitletOperationLoadItem<Handler: BitletHandler>: BitletOperationCacheItem {
    
    // --
    // MARK: Load item members
    // --

    private let bitletHandler: Handler
    private let forced: Bool
    private let itemCompletion: ((_ bitletItem: Handler.BitletData?, _ error: Error?) -> Void)?
    private var running = false
    let cacheKey: String?
    var enabled = true
    

    // --
    // MARK: Load item initialization
    // --

    init(bitletHandler: Handler, cacheKey: String? = nil, forced: Bool = false, completion: ((_ bitletItem: Handler.BitletData?, _ error: Error?) -> Void)?) {
        self.bitletHandler = bitletHandler
        self.cacheKey = cacheKey
        self.forced = forced
        self.itemCompletion = completion
    }
    

    // --
    // MARK: Load item running
    // --

    func run(bitletSynchronizer: BitletSynchronizer, completion: @escaping (Error?) -> Void) {
        var skipsLoading = false
        if let cacheKey = cacheKey {
            let cacheEntry = bitletSynchronizer.cacheEntry(forKey: cacheKey, andType: Handler.BitletData.self)
            skipsLoading = !forced && cacheEntry.bitletData != nil && !cacheEntry.expired()
        }
        if skipsLoading {
            completion(nil)
        } else {
            running = true
            bitletSynchronizer.loadBitlet(bitletHandler, cacheKey: cacheKey, forced: true, completion: { [weak self] bitlet, error in
                self?.running = false
                self?.itemCompletion?(bitlet, error)
                completion(error)
            })
        }
    }
    
    func isRunning() -> Bool {
        return running
    }

}

class BitletOperationNestedItem: BitletOperationItem {
    
    // --
    // MARK: Nested item members
    // --

    let operation: BitletOperation
    private let itemCompletion: ((_ error: Error?, _ canceled: Bool) -> Void)?
    private var running = false
    var enabled = true
    

    // --
    // MARK: Nested item initialization
    // --

    init(operation: BitletOperation, completion: ((_ error: Error?, _ canceled: Bool) -> Void)?) {
        self.operation = operation
        self.itemCompletion = completion
    }
    

    // --
    // MARK: Nested item running
    // --

    func run(bitletSynchronizer: BitletSynchronizer, completion: @escaping (_ error: Error?) -> Void) {
        running = true
        let canStart = operation.start(bitletSynchronizer: bitletSynchronizer, completion: { [weak self] error, canceled in
            var completeError = error
            self?.running = false
            if error == nil && canceled {
                completeError = NSError(domain: "", code: 0, userInfo: [NSLocalizedDescriptionKey : "Nested operation canceled"])
            }
            self?.itemCompletion?(error, canceled)
            completion(completeError)
        })
        if !canStart {
            let error = NSError(domain: "", code: 0, userInfo: [NSLocalizedDescriptionKey : "Nested operation could not be started"])
            running = false
            itemCompletion?(error, false)
            completion(error)
        }
    }
    
    func isRunning() -> Bool {
        return running
    }
    
}
