//
//  Settings.swift
//  Bitlet Synchronizer example
//
//  Infrastructure utility: handles settings
//  Store and retrieve global app settings, stored in UserDefaults
//

import UIKit

class Settings {

    // --
    // MARK: Members
    // --
    
    class var serverAddress: String? {
        set {
            if let stringValue = newValue {
                UserDefaults.standard.set(stringValue, forKey: "serverAddress")
            } else {
                UserDefaults.standard.removeObject(forKey: "serverAddress")
            }
        }
        get {
            if let address = UserDefaults.standard.string(forKey: "serverAddress"), address.count > 0 {
                if address.hasPrefix("http://") || address.hasPrefix("https://") {
                    return address
                }
                return "http://" + address
            }
            return nil
        }
    }
    
    class var lastLoggedInUser: String? {
        set {
            if let stringValue = newValue {
                UserDefaults.standard.set(stringValue, forKey: "lastLoggedInUser")
            } else {
                UserDefaults.standard.removeObject(forKey: "lastLoggedInUser")
            }
        }
        get { return UserDefaults.standard.string(forKey: "lastLoggedInUser") }
    }
    
    class var sessionCookie: String? {
        set {
            if let stringValue = newValue {
                UserDefaults.standard.set(stringValue, forKey: "sessionCookie")
            } else {
                UserDefaults.standard.removeObject(forKey: "sessionCookie")
            }
        }
        get { return UserDefaults.standard.string(forKey: "sessionCookie") }
    }

}
