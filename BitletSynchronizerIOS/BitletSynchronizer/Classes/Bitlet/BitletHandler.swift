//
//  BitletHandler.swift
//  Bitlet Synchronizer Pod
//
//  Library: handle bitlet transfer
//  A protocol to handle transfer of a bitlet from an API
//

public protocol BaseBitletHandler {
    
    func loadData(observer: Any)
    
}

public protocol BitletHandler: BaseBitletHandler {
    
    associatedtype BitletData
    
    func load(observer: BitletObserver<BitletData>)
    
}

extension BitletHandler {
    
    public func loadData(observer: Any) {
        if let typedObserver = observer as? BitletObserver<BitletData> {
            load(observer: typedObserver)
        } else {
            load(observer: WrappedBitletObserver<BitletData>(originalObserver: observer))
        }
    }
    
}

fileprivate class WrappedBitletObserver<BitletData>: BitletObserver<BitletData> {
    
    private var originalObserver: BaseBitletObserver?
    
    override var bitlet: BitletData? {
        didSet {
            originalObserver?.bitletData = bitlet
        }
    }
    
    override var bitletHash: String? {
        didSet {
            originalObserver?.bitletHash = bitletHash
        }
    }
    
    override var bitletExpireTime: BitletExpireTime? {
        didSet {
            originalObserver?.bitletExpireTime = bitletExpireTime
        }
    }
    
    override var error: Error? {
        didSet {
            originalObserver?.error = error
        }
    }
    
    fileprivate init(originalObserver: Any) {
        self.originalObserver = originalObserver as? BaseBitletObserver
    }
    
    override func finish() {
        originalObserver?.finish()
    }
    
}
