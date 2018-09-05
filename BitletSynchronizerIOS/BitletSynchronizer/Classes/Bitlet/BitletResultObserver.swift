//
//  BitletResultObserver.swift
//  Bitlet Synchronizer Pod
//
//  Library: observe a bitlet handler result
//  An implementation of a bitlet observer to be used when only observing the final result is needed
//

open class BitletResultObserver<BitletData>: BitletObserver<BitletData> {
    
    // --
    // MARK: Members
    // --

    private var successHandler: ((_ bitlet: BitletData) -> Void)?
    private var failureHandler: ((_ error: Error) -> Void)?
    private var completionHandler: ((_ bitlet: BitletData?, _ error: Error?) -> Void)?
    

    // --
    // MARK: Initialization
    // --
    
    public init(success: @escaping ((_ bitlet: BitletData) -> Void), failure: @escaping ((_ error: Error) -> Void)) {
        successHandler = success
        failureHandler = failure
    }
    
    public init(completion: @escaping ((_ bitlet: BitletData?, _ error: Error?) -> Void)) {
        completionHandler = completion
    }


    // --
    // MARK: BitletObserver
    // --

    override open func finish() {
        if let bitlet = bitlet, error == nil {
            successHandler?(bitlet)
            completionHandler?(bitlet, nil)
        } else {
            let defaultError = NSError(domain: "", code: 0, userInfo: [NSLocalizedDescriptionKey : "Unknown bitlet error"])
            failureHandler?(error ?? defaultError)
            completionHandler?(nil, error ?? defaultError)
        }
    }

}
