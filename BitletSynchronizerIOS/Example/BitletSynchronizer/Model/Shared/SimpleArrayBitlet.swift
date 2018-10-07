//
//  SimpleArrayBitlet.swift
//  Bitlet Synchronizer example
//
//  Shared model: simple array bitlet implementation
//  Provides a base class for a bitlet implementation for a simple model containing an array
//

import UIKit
import BitletSynchronizer
import Alamofire
import ObjectMapper
import AlamofireObjectMapper

protocol ArrayModel: class {
    
    associatedtype ArrayType: Mappable
    
    var itemList: [ArrayType] { get set }
    
    init()
    
}

class SimpleArrayBitlet<SimpleBitletData>: BitletHandler where SimpleBitletData: ArrayModel {

    // --
    // MARK: Bitlet data type
    // --

    typealias BitletData = SimpleBitletData


    // --
    // MARK: Members
    // --
    
    private let path: String
    private let expireTime: BitletExpireTime
    private let mockedJson: [[String: Any]]

    
    // --
    // MARK: Initialization
    // --
    
    init(path: String, expireTime: BitletExpireTime, mockedJson: [[String: Any]]) {
        self.path = path
        self.expireTime = expireTime
        self.mockedJson = mockedJson
    }

    
    // --
    // MARK: Implementation
    // --

    func load(observer: BitletObserver<BitletData>) {
        if let serverAddress = Settings.serverAddress, serverAddress.count > 0 {
            Alamofire.request(serverAddress + path).responseArray { (response: DataResponse<[SimpleBitletData.ArrayType]>) in
                if let bitletItemList = response.value {
                    let bitlet = SimpleBitletData.init()
                    bitlet.itemList = bitletItemList
                    observer.bitlet = bitlet
                    observer.bitletExpireTime = .now() + self.expireTime.rawValue
                } else if let error = response.error {
                    observer.error = error
                }
                self.hashAndFinish(observer: observer, data: response.data)
            }
        } else {
            let bitlet = SimpleBitletData.init()
            var failedItem = false
            for mockedJsonItem in mockedJson {
                if let bitletItem = Mapper<SimpleBitletData.ArrayType>().map(JSONObject: mockedJsonItem) {
                    bitlet.itemList.append(bitletItem)
                } else {
                    failedItem = true
                    break
                }
            }
            if !failedItem {
                observer.bitlet = bitlet
                observer.bitletExpireTime = .now() + expireTime.rawValue
            }
            hashAndFinish(observer: observer, data: "mocked".data(using: .utf8))
        }
    }
    
    private func hashAndFinish(observer: BitletObserver<BitletData>, data: Data?) {
        if observer.bitlet != nil, let data = data {
            DispatchQueue.global(qos: .background).async {
                let md5 = data.md5()
                DispatchQueue.main.async { () -> Void in
                    observer.bitletHash = md5
                    observer.finish()
                }
            }
        } else {
            observer.finish()
        }
    }

}
