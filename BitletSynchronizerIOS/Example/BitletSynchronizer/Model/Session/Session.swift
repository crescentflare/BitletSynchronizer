//
//  Session.swift
//  Bitlet Synchronizer example
//
//  Session model: main session
//  Stores the authenticated session
//

import UIKit
import BitletSynchronizer
import Alamofire
import ObjectMapper
import AlamofireObjectMapper

class Session: Mappable {

    // --
    // MARK: Members
    // --
    
    var cookie: String?
    var features: SessionFeatures?

    
    // --
    // MARK: Serialization
    // --

    required init?(map: Map) {
    }
    
    func mapping(map: Map) {
        cookie <- map["cookie"]
        features <- map["features"]
    }
    

    // --
    // MARK: Bitlet integration
    // --
    
    class func bitlet(username: String, password: String) -> BitletClass {
        return BitletClass(username: username, password: password)
    }

    class BitletClass: BitletHandler {
        
        typealias BitletData = Session
        
        private let username: String
        private let password: String

        init(username: String, password: String) {
            self.username = username
            self.password = password
        }

        func load(observer: BitletObserver<BitletData>) {
            if let serverAddress = Settings.serverAddress, serverAddress.count > 0 {
                Alamofire.request(serverAddress + "/sessions", method: .post, parameters: [ "user": username, "password": password ]).responseObject { (response: DataResponse<Session>) in
                    if let session = response.value {
                        observer.bitlet = session
                        observer.bitletExpireTime = .distantFuture
                    } else if let error = response.error {
                        observer.error = error
                    }
                    observer.finish()
                }
            } else {
                let mockedJson: [String: Any] = [
                    "cookie": "mocked",
                    "features": [
                        "usage": "view",
                        "servers": "view"
                    ]
                ]
                if let session = Mapper<Session>().map(JSONObject: mockedJson) {
                    observer.bitlet = session
                    observer.bitletExpireTime = .distantFuture
                }
                observer.finish()
            }
        }

    }
    
}
