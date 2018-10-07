//
//  Usage.swift
//  Bitlet Synchronizer example
//
//  Usage model: usage overview
//  Stores the main usage overview data
//

import UIKit
import BitletSynchronizer
import Alamofire
import ObjectMapper
import AlamofireObjectMapper

class Usage: Mappable {

    // --
    // MARK: Members
    // --
    
    var lastUpdate: Date?
    var dataTraffic: UsageItem?
    var serverLoad: UsageItem?

    
    // --
    // MARK: Serialization
    // --

    required init?(map: Map) {
    }
    
    func mapping(map: Map) {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
        dataTraffic <- map["data_traffic"]
        serverLoad <- map["server_load"]
        lastUpdate <- (map["last_update"], DateFormatterTransform(dateFormatter: formatter))
    }
    

    // --
    // MARK: Bitlet integration
    // --
    
    static let cacheKey = "/usage"
    
    class func bitlet() -> SimpleBitlet<Usage> {
        let mockedJson: [String: Any] = [
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
            "last_update": "2001-01-01T00:00:00.000Z"
        ]
        return SimpleBitlet<Usage>(path: "/usage", expireTime: .init(withSeconds: 30), mockedJson: mockedJson)
    }

}
