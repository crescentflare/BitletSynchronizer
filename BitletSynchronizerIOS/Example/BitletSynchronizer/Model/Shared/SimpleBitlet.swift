//
//  SimpleBitlet.swift
//  Bitlet Synchronizer example
//
//  Shared model: simple bitlet implementation
//  Provides a base class for a bitlet implementation for a simple model
//

import UIKit
import BitletSynchronizer
import Alamofire
import ObjectMapper
import AlamofireObjectMapper

class SimpleBitlet<SimpleBitletData>: BitletHandler where SimpleBitletData: BaseMappable {

    // --
    // MARK: Bitlet data type
    // --

    typealias BitletData = SimpleBitletData


    // --
    // MARK: Members
    // --
    
    private let path: String
    private let expireTime: BitletExpireTime
    private let mockedJson: [String: Any]

    
    // --
    // MARK: Initialization
    // --
    
    init(path: String, expireTime: BitletExpireTime, mockedJson: [String: Any]) {
        self.path = path
        self.expireTime = expireTime
        self.mockedJson = mockedJson
    }

    
    // --
    // MARK: Implementation
    // --

    func load(observer: BitletObserver<BitletData>) {
        if let serverAddress = Settings.serverAddress, serverAddress.count > 0 {
            Alamofire.request(serverAddress + path).responseObject { (response: DataResponse<SimpleBitletData>) in
                if let bitlet = response.value {
                    observer.bitlet = bitlet
                    observer.bitletExpireTime = .now() + self.expireTime.rawValue
                } else if let error = response.error {
                    observer.error = error
                }
                self.hashAndFinish(observer: observer, data: response.data)
            }
        } else {
            if let bitlet = Mapper<SimpleBitletData>().map(JSONObject: mockedJson) {
                observer.bitlet = bitlet
                observer.bitletExpireTime = .now() + expireTime.rawValue
            }
            hashAndFinish(observer: observer, data: "mocked".data(using: .utf8))
        }
    }
    
    private func hashAndFinish(observer: BitletObserver<BitletData>, data: Data?) {
        if observer.bitlet != nil, let data = data {
            DispatchQueue.global(qos: .background).async {
                let md5 = data.md5()
                DispatchQueue.main.async { () -> Void in
                    observer.bitletHash = md5
                    observer.finish()
                }
            }
        } else {
            observer.finish()
        }
    }

}
