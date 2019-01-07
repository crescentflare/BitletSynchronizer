//
//  BitletCache.swift
//  Bitlet Synchronizer Pod
//
//  Library: caches bitlets
//  Used by the bitlet synchronizer to cache and optimize bitlet loading
//

public protocol BitletCache {
    
    func createEntryIfNeeded(forKey: String, handler: BaseBitletHandler)
    func updateEntry(forKey: String, handler: BaseBitletHandler)
    func getEntry(forKey: String) -> BitletCacheEntry<Any>?
    func clear(filter: String, recursive: Bool)
    
}

public class BitletMemoryCache: BitletCache {
    
    // --
    // MARK: Members
    // --

    private var cacheEntries = [String: BitletCacheEntry<Any>]()
    
    
    // --
    // MARK: Initialization
    // --

    public init() {
    }
    
    
    // --
    // MARK: Cache access
    // --

    public func createEntryIfNeeded(forKey: String, handler: BaseBitletHandler) {
        if cacheEntries[forKey] == nil {
            cacheEntries[forKey] = BitletCacheEntry(handler: handler)
        }
    }
    
    public func updateEntry(forKey: String, handler: BaseBitletHandler) {
        if let cacheEntry = cacheEntries[forKey] {
            cacheEntry.updateHandler(handler)
        }
    }
    
    public func getEntry(forKey: String) -> BitletCacheEntry<Any>? {
        return cacheEntries[forKey]
    }
    
    public func clear(filter: String = "*", recursive: Bool = true) {
        var entriesToDelete = [String]()
        let filterComponents = filter.split(separator: "/").map(String.init)
        for key in cacheEntries.keys {
            if matchesFilter(filterComponents, item: key, recursive: recursive) {
                entriesToDelete.append(key)
            }
        }
        for key in entriesToDelete {
            cacheEntries.removeValue(forKey: key)
        }
    }
    

    // --
    // MARK: Helper
    // --

    private func matchesFilter(_ filter: [String], item: String, recursive: Bool) -> Bool {
        let itemComponents = item.split(separator: "/").map(String.init)
        for index in itemComponents.indices {
            if index < filter.count {
                if filter[index] != "*" && itemComponents[index] != filter[index] {
                    return false
                }
            } else if recursive {
                break
            } else {
                return false
            }
        }
        return filter.count <= itemComponents.count
    }
    
}
