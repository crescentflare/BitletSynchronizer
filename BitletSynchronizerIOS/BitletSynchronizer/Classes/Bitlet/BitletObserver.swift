//
//  BitletObserver.swift
//  Bitlet Synchronizer Pod
//
//  Library: observe a bitlet transfer
//  A class and protocol to receive notifications during a bitlet transfer (like receiving a bitlet, an error, etc.)
//

public protocol BaseBitletObserver: AnyObject {
    
    var bitletData: Any? { get set }
    var bitletHash: String? { get set }
    var bitletExpireTime: BitletExpireTime? { get set }
    var error: Error? { get set }
    
    func finish()

}

open class BitletObserver<BitletData>: BaseBitletObserver {
    
    open var bitlet: BitletData?
    open var bitletHash: String?
    open var bitletExpireTime: BitletExpireTime?
    open var error: Error?
    
    public init() {
        // Can be overridden
    }
    
    open func finish() {
        // Must be overridden
    }
    
}

extension BitletObserver {
    
    public var bitletData: Any? {
        get {
            return bitlet as Any
        }
        set {
            if let bitlet = newValue as? BitletData {
                self.bitlet = bitlet
            }
        }
    }
    
}
