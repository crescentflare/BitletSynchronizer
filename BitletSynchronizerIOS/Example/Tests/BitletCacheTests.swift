//
//  BitletCacheTests.swift
//  Bitlet Synchronizer tests
//
//  Tests the bitlet cache
//

import XCTest
import BitletSynchronizer

class BitletCacheTests: XCTestCase {
    
    func testClear() {
        let cache = BitletMemoryCache()
        cache.createEntryIfNeeded(forKey: "/test", handler: nil)
        cache.createEntryIfNeeded(forKey: "/test/subtest", handler: nil)
        cache.clear(filter: "/test/subtest")
        XCTAssertNil(cache.getEntry(forKey: "/test/subtest"))
        XCTAssertNotNil(cache.getEntry(forKey: "/test"))
    }
    
}
