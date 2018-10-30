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
    
    func run(completion: @escaping (_ error: Error?) -> Void)
    func isRunning() -> Bool
    
}

protocol BitletOperationCacheItem: BitletOperationItem {
    
    var cacheKey: String? { get }
    
}

class BitletOperationLoadItem<Handler: BitletHandler>: BitletOperationCacheItem {
    
    // --
    // MARK: Load item members
    // --

    private let bitletSynchronizer: BitletSynchronizer
    private let bitletHandler: Handler
    private let forced: Bool
    private let itemCompletion: (_ bitletItem: Handler.BitletData?, _ error: Error?) -> Void
    private var running = false
    let cacheKey: String?
    var enabled = true
    

    // --
    // MARK: Load item initialization
    // --

    init(bitletHandler: Handler, cacheKey: String? = nil, forced: Bool = false, bitletSynchronizer: BitletSynchronizer, completion: @escaping (_ bitletItem: Handler.BitletData?, _ error: Error?) -> Void) {
        self.bitletHandler = bitletHandler
        self.cacheKey = cacheKey
        self.forced = forced
        self.bitletSynchronizer = bitletSynchronizer
        self.itemCompletion = completion
    }
    

    // --
    // MARK: Load item running
    // --

    func run(completion: @escaping (Error?) -> Void) {
        if let cacheKey = cacheKey, !bitletSynchronizer.cacheEntry(forKey: cacheKey, andType: Handler.BitletData.self).expired() && !forced {
            completion(nil)
        } else {
            running = true
            bitletSynchronizer.loadBitlet(bitletHandler, cacheKey: cacheKey, forced: true, completion: { [weak self] bitlet, error in
                self?.running = false
                self?.itemCompletion(bitlet, error)
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
    private let bitletSynchronizer: BitletSynchronizer
    private let itemCompletion: (_ error: Error?, _ canceled: Bool) -> Void
    private var running = false
    var enabled = true
    

    // --
    // MARK: Nested item initialization
    // --

    init(operation: BitletOperation, bitletSynchronizer: BitletSynchronizer, completion: @escaping (_ error: Error?, _ canceled: Bool) -> Void) {
        self.operation = operation
        self.bitletSynchronizer = bitletSynchronizer
        self.itemCompletion = completion
    }
    

    // --
    // MARK: Nested item running
    // --

    func run(completion: @escaping (_ error: Error?) -> Void) {
        running = true
        let canStart = operation.start(bitletSynchronizer: bitletSynchronizer, completion: { [weak self] error, canceled in
            var completeError = error
            self?.running = false
            if error == nil && canceled {
                completeError = NSError(domain: "", code: 0, userInfo: [NSLocalizedDescriptionKey : "Nested operation canceled"])
            }
            self?.itemCompletion(error, canceled)
            completion(completeError)
        })
        if !canStart {
            let error = NSError(domain: "", code: 0, userInfo: [NSLocalizedDescriptionKey : "Nested operation could not be started"])
            running = false
            itemCompletion(error, false)
            completion(error)
        }
    }
    
    func isRunning() -> Bool {
        return running
    }
    
}
