//
//  UsageItem.swift
//  Bitlet Synchronizer example
//
//  Usage model: usage value item
//  An item in the list of usage overview values
//

import UIKit
import ObjectMapper

class UsageItem: Mappable {

    // --
    // MARK: Members
    // --
    
    var amount: Float?
    var unit: UsageUnit?
    var label: String?


    // --
    // MARK: Serialization
    // --
    
    required init?(map: Map) {
    }
    
    func mapping(map: Map) {
        amount <- map["amount"]
        unit <- map["unit"]
        label <- map["label"]
    }

}
