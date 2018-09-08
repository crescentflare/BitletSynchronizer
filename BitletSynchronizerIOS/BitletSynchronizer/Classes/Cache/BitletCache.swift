//
//  BitletCache.swift
//  Bitlet Synchronizer Pod
//
//  Library: caches bitlets
//  Used by the bitlet synchronizer to cache and optimize bitlet loading
//

public protocol BitletCache {
    
    func createEntryIfNeeded(key: String, handler: BaseBitletHandler)
    func getEntry(key: String, createIfNeeded: Bool) -> BitletCacheEntry?
    func clear()
    
}

public class BitletMemoryCache: BitletCache {
    
    // --
    // MARK: Members
    // --

    private var cacheEntries = [String: BitletCacheEntry]()
    

    // --
    // MARK: Cache access
    // --

    public func createEntryIfNeeded(key: String, handler: BaseBitletHandler) {
        if cacheEntries[key] == nil {
            cacheEntries[key] = BitletCacheEntry(handler: handler)
        }
    }
    
    public func getEntry(key: String, createIfNeeded: Bool = false) -> BitletCacheEntry? {
        return cacheEntries[key]
    }
    
    public func clear() {
        cacheEntries = [String: BitletCacheEntry]()
    }
    
}
