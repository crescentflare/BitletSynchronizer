package com.crescentflare.bitletsynchronizer.cache;

import com.crescentflare.bitletsynchronizer.bitlet.BitletHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Bitlet Synchronizer cache: default memory cache implementation
 * Provides a default cache implementation for the bitlet synchronizer, stores entries in memory
 */
public class BitletMemoryCache implements BitletCache
{
    // ---
    // Members
    // ---

    private Map<String, BitletCacheEntry<Object>> cacheEntries = new HashMap<>();


    // ---
    // Implementation
    // ---

    @SuppressWarnings("unchecked")
    @Override
    public void createEntryIfNeeded(String key, BitletHandler handler)
    {
        if (cacheEntries.get(key) == null)
        {
            cacheEntries.put(key, new BitletCacheEntry(handler));
        }
    }

    @Override
    public BitletCacheEntry<Object> getEntry(String key)
    {
        return cacheEntries.get(key);
    }

    public void clear()
    {
        clear("*", true);
    }

    public void clear(String filter)
    {
        clear(filter, true);
    }

    @Override
    public void clear(String filter, boolean recursive)
    {
        List<String> entriesToDelete = new ArrayList<>();
        String[] filterComponents = filter.split("\\/");
        for (String key : cacheEntries.keySet())
        {
            if (matchesFilter(filterComponents, key, recursive))
            {
                entriesToDelete.add(key);
            }
        }
        for (String key : entriesToDelete)
        {
            cacheEntries.remove(key);
        }
    }


    // ---
    // Helper
    // ---

    private boolean matchesFilter(String[] filter, String item, boolean recursive)
    {
        String[] itemComponents = item.split("\\/");
        for (int index = 0; index < itemComponents.length; index++)
        {
            if (index < filter.length)
            {
                if (!filter[index].equals("*") && !itemComponents[index].equals(filter[index]))
                {
                    return false;
                }
            }
            else if (recursive)
            {
                break;
            }
            else
            {
                return false;
            }
        }
        return true;
    }
}
