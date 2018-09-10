//
//  BitletCache.swift
//  Bitlet Synchronizer Pod
//
//  Library: caches bitlets
//  Used by the bitlet synchronizer to cache and optimize bitlet loading
//

public protocol BitletCache {
    
    func createEntryIfNeeded(key: String, handler: BaseBitletHandler)
    func getEntry(key: String) -> BitletCacheEntry?
    func clear(filter: String, recursive: Bool)
    
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
    
    public func getEntry(key: String) -> BitletCacheEntry? {
        return cacheEntries[key]
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
        return true
    }
    
}
