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

class BitletOperationClosureItem : BitletOperationItem {
    
    // --
    // MARK: Closure members
    // --

    private let itemClosure: (@escaping (_ error: Error?) -> Void) -> Void
    private var running = false
    let cacheKey: String?
    var enabled = true
    

    // --
    // MARK: Closure initialization
    // --

    init(_ itemClosure: @escaping (@escaping (_ error: Error?) -> Void) -> Void, cacheKey: String? = nil) {
        self.itemClosure = itemClosure
        self.cacheKey = cacheKey
    }
    

    // --
    // MARK: Closure running
    // --

    func run(completion: @escaping (_ error: Error?) -> Void) {
        running = true
        itemClosure { error in
            self.running = false
            completion(error)
        }
    }
    
    func isRunning() -> Bool {
        return running
    }
    
}

class BitletOperationNestedItem : BitletOperationItem {
    
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
        let canStart = operation.start(bitletSynchronizer: bitletSynchronizer, completion: { error, canceled in
            var completeError = error
            self.running = false
            if error == nil && canceled {
                completeError = NSError(domain: "", code: 0, userInfo: [NSLocalizedDescriptionKey : "Nested operation canceled"])
            }
            self.itemCompletion(error, canceled)
            completion(completeError)
        })
        if !canStart {
            let error = NSError(domain: "", code: 0, userInfo: [NSLocalizedDescriptionKey : "Nested operation could not be started"])
            itemCompletion(error, false)
            completion(error)
        }
    }
    
    func isRunning() -> Bool {
        return running
    }
    
}
