package com.crescentflare.bitletsynchronizer.cache;

import org.junit.Assert;
import org.junit.Test;

/**
 * Bitlet test: memory cache
 */
public class BitletMemoryCacheTest
{
    // ---
    // Clear test
    // ---

    @Test
    public void testClear() throws Exception
    {
        BitletMemoryCache cache = new BitletMemoryCache();
        cache.createEntryIfNeeded("/test", null);
        cache.createEntryIfNeeded("/test/subtest", null);
        cache.clear("/test/subtest");
        Assert.assertNull(cache.getEntry("/test/subtest"));
        Assert.assertNotNull(cache.getEntry("/test"));
    }
}
