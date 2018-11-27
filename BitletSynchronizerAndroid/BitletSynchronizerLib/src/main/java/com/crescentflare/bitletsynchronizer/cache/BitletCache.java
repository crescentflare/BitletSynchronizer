package com.crescentflare.bitletsynchronizer.cache;

import com.crescentflare.bitletsynchronizer.bitlet.BitletHandler;

/**
 * Bitlet Synchronizer cache: the cache interface
 * Provides an interface for a bitlet synchronizer cache implementation
 */
public interface BitletCache
{
    void createEntryIfNeeded(String key, BitletHandler handler);
    void updateEntry(String key, BitletHandler handler);
    BitletCacheEntry<Object> getEntry(String key);
    void clear(String filter, boolean recursive);
}
