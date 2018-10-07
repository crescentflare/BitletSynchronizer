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
    
    class func cacheKey(_ serverId: String) -> String {
        return "/servers/" + serverId
    }
    
    class func bitlet(serverId: String) -> SimpleBitlet<Server> {
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
        return SimpleBitlet<Server>(path: "/servers/\(serverId)", expireTime: .init(withMinutes: 10), mockedJson: mockedJson)
    }

}
