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
        }
    }
    
}
