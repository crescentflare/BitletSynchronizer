//
//  Server.swift
//  Bitlet Synchronizer example
//
//  Server model: a server
//  One server in a list of servers
//

import UIKit
import BitletSynchronizer
import Alamofire
import ObjectMapper
import AlamofireObjectMapper

class Server: Mappable {

    // --
    // MARK: Members
    // --
    
    var serverId: String?
    var name: String?
    var location: String?
    var description: String?
    var os: String?
    var osVersion: String?
    var dataTraffic: UsageItem?
    var serverLoad: UsageItem?
    var enabled: Bool?


    // --
    // MARK: Serialization
    // --
    
    required init?(map: Map) {
    }
    
    func mapping(map: Map) {
        serverId <- map["id"]
        name <- map["name"]
        location <- map["location"]
        description <- map["description"]
        os <- map["os"]
        osVersion <- map["os_version"]
        dataTraffic <- map["data_traffic"]
        serverLoad <- map["server_load"]
        enabled <- map["enabled"]
    }

    
    // --
    // MARK: Bitlet integration
    // --
    
    class func bitlet(serverId: String) -> BitletClass {
        return Settings.serverAddress?.count ?? 0 > 0 ? BitletClass(serverId) : MockedBitletClass(serverId)
    }
    
    class BitletClass: BitletHandler {
        
        typealias BitletData = Server
        
        let cacheKey: String
        
        private let serverId: String
        
        init(_ serverId: String) {
            cacheKey = "/servers/" + serverId
            self.serverId = serverId
        }
        
        func load(observer: BitletObserver<BitletData>) {
            if let serverAddress = Settings.serverAddress, serverAddress.count > 0 {
                Alamofire.request(serverAddress + "/servers/" + serverId).responseObject { (response: DataResponse<Server>) in
                    if let server = response.value {
                        observer.bitlet = server
                        observer.bitletExpireTime = .minutesFromNow(10)
                    } else if let error = response.error {
                        observer.error = error
                    }
                    observer.finish()
                }
            }
        }
        
    }

    class MockedBitletClass: BitletClass {
        
        override func load(observer: BitletObserver<BitletData>) {
            let mockedJson: [String: Any] = [
                "id": "mocked",
                "name": "Mock server",
                "description": "Internal mocked data",
                "os": "Unknown",
                "os_version": "1.0",
                "location": "Home",
                "data_traffic": [
                    "amount": 2.0,
                    "unit": "GB",
                    "label": "2.0 GB"
                ],
                "server_load": [
                    "amount": 10,
                    "unit": "percent",
                    "label": "10%"
                ],
                "enabled": true
            ]
            if let server = Mapper<Server>().map(JSONObject: mockedJson) {
                observer.bitlet = server
                observer.bitletExpireTime = .minutesFromNow(10)
            }
            observer.finish()
        }
        
    }

}
