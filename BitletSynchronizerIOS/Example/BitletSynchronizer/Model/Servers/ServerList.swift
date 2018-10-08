//
//  ServerList.swift
//  Bitlet Synchronizer example
//
//  Server model: simple server list
//  Stores an overview of all servers without details
//

import UIKit
import BitletSynchronizer
import Alamofire
import ObjectMapper
import AlamofireObjectMapper

class ServerList: ArrayModel {

    // --
    // MARK: Set type of array
    // --
    
    typealias ArrayType = Server


    // --
    // MARK: Members
    // --
    
    var itemList: [Server] = []

    
    // --
    // MARK: Bitlet integration
    // --
    
    static let cacheKey = "/servers"
    
    class func bitlet() -> SimpleArrayBitlet<ServerList> {
        let mockedJson: [[String: Any]] = [
            [
                "id": "mocked",
                "name": "Mock server",
                "location": "Home",
                "enabled": true
            ]
        ]
        return SimpleArrayBitlet<ServerList>(path: "/servers", expireTime: .init(withMinutes: 10), mockedJson: mockedJson)
    }

    
    // --
    // MARK: Initialization
    // --
    
    required init() {
    }

}
