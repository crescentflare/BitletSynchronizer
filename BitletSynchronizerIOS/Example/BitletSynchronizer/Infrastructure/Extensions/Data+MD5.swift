//
//  Data+MD5.swift
//  Bitlet Synchronizer example
//
//  Infrastructure utility: data extension
//  Extends Data to return its MD5 hash
//

import UIKit
import CommonCrypto

extension Data {

    func md5() -> String {
        // Setup data variable to hold the md5 hash
        var digest = Data(count: Int(CC_MD5_DIGEST_LENGTH))
        
        // Generate hash
        _ = digest.withUnsafeMutableBytes { (digestBytes: UnsafeMutablePointer<UInt8>) in
            self.withUnsafeBytes { (messageBytes: UnsafePointer<UInt8>) in
                let length = CC_LONG(self.count)
                CC_MD5(messageBytes, length, digestBytes)
            }
        }
        
        // Return md5 hash string formatted as hexadecimal
        return digest.map { String(format: "%02hhx", $0) }.joined()
    }

}
