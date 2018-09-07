//
//  SessionFeatures.swift
//  Bitlet Synchronizer example
//
//  Session model: features of a session
//  Stores the features available of the session, and their permissions
//

import UIKit
import ObjectMapper

class SessionFeatures: Mappable {

    // --
    // MARK: Members
    // --
    
    var usage: SessionPermission?
    var servers: SessionPermission?


    // --
    // MARK: Serialization
    // --
    
    required init?(map: Map) {
    }
    
    func mapping(map: Map) {
        usage <- map["usage"]
        servers <- map["servers"]
    }

}
