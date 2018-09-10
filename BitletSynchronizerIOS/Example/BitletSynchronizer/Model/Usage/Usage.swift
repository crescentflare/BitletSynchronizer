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
    
    class func bitlet() -> BitletClass {
        return Settings.serverAddress?.count ?? 0 > 0 ? BitletClass() : MockedBitletClass()
    }
    
    class BitletClass: BitletHandler {

        typealias BitletData = Usage
        
        let cacheKey = "/usage"
        
        func load(observer: BitletObserver<BitletData>) {
            if let serverAddress = Settings.serverAddress, serverAddress.count > 0 {
                Alamofire.request(serverAddress + "/usage").responseObject { (response: DataResponse<Usage>) in
                    if let usage = response.value {
                        observer.bitlet = usage
                        observer.bitletExpireTime = .secondsFromNow(30)
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
            if let usage = Mapper<Usage>().map(JSONObject: mockedJson) {
                observer.bitlet = usage
                observer.bitletExpireTime = .secondsFromNow(30)
            }
            observer.finish()
        }
        
    }

}
