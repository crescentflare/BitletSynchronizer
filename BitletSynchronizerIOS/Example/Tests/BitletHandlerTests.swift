//
//  BitletHandlerTests.swift
//  Bitlet Synchronizer tests
//
//  Tests the bitlet handler protocol
//

import XCTest
import BitletSynchronizer

class Tests: XCTestCase {
    
    func testLoad() {
        BitletHandlerSample(generateValue: "Test").load(observer: BitletResultObserver { bitlet, error in
            XCTAssertEqual(bitlet, "Test")
        })
    }
    
}

fileprivate class BitletHandlerSample: BitletHandler {

    typealias BitletData = String
    
    private let generateValue: String
    
    init(generateValue: String) {
        self.generateValue = generateValue
    }

    func load(observer: BitletObserver<BitletData>) {
        observer.bitlet = "Test"
        observer.finish()
    }
    
}
