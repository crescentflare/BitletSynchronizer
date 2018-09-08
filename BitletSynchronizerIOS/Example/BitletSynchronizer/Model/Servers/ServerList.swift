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

class ServerList {

    // --
    // MARK: Members
    // --
    
    var servers: [Server] = []

    
    // --
    // MARK: Bitlet integration
    // --
    
    class func bitlet() -> BitletClass {
        return BitletClass()
    }
    
    class BitletClass: BitletHandler {

        typealias BitletData = ServerList
        
        let cacheKey = "/servers"

        func load(observer: BitletObserver<BitletData>) {
            if let serverAddress = Settings.serverAddress, serverAddress.count > 0 {
                Alamofire.request(serverAddress + "/servers").responseArray { (response: DataResponse<[Server]>) in
                    if let servers = response.value {
                        let serverList = ServerList()
                        serverList.servers = servers
                        observer.bitlet = serverList
                        observer.bitletExpireTime = .minutesFromNow(10)
                    } else if let error = response.error {
                        observer.error = error
                    }
                    observer.finish()
                }
            } else {
                let mockedJson: [String: Any] = [
                    "id": "mocked",
                    "name": "Mock server",
                    "location": "Home",
                    "enabled": true
                ]
                if let server = Mapper<Server>().map(JSONObject: mockedJson) {
                    let serverList = ServerList()
                    serverList.servers = [server]
                    observer.bitlet = serverList
                    observer.bitletExpireTime = .minutesFromNow(10)
                }
                observer.finish()
            }
        }
        
    }
    
}
