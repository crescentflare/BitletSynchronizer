//
//  BitletSynchronizer.swift
//  Bitlet Synchronizer Pod
//
//  Library: synchronizes bitlets
//  A singleton to access and handle bitlets easily
//

public class BitletSynchronizer {
    
    // --
    // MARK: Singleton
    // --

    public static let shared = BitletSynchronizer()

    
    // --
    // MARK: Loading
    // --

    public func loadBitlet<Handler: BitletHandler>(_ bitletHandler: Handler, success: @escaping ((_ bitlet: Handler.BitletData) -> Void), failure: @escaping ((_ error: Error) -> Void)) {
        bitletHandler.load(observer: BitletResultObserver<Handler.BitletData>(success: { data in
            success(data)
        }, failure: { error in
            failure(error)
        }))
    }
    
    public func loadBitlet<Handler: BitletHandler>(_ bitletHandler: Handler, completion: @escaping ((_ bitlet: Handler.BitletData?, _ error: Error?) -> Void)) {
        bitletHandler.load(observer: BitletResultObserver<Handler.BitletData>(completion: { data, error in
            completion(data, error)
        }))
    }

}
