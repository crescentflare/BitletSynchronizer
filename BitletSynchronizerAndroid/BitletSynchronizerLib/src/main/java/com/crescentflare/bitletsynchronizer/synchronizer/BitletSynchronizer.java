package com.crescentflare.bitletsynchronizer.synchronizer;

import com.crescentflare.bitletsynchronizer.bitlet.BitletHandler;
import com.crescentflare.bitletsynchronizer.bitlet.BitletResultObserver;
import com.crescentflare.bitletsynchronizer.cache.BitletCache;
import com.crescentflare.bitletsynchronizer.cache.BitletCacheEntry;
import com.crescentflare.bitletsynchronizer.cache.BitletMemoryCache;

/**
 * Bitlet Synchronizer synchronizer: synchronizes bitlets
 * A singleton to access and handle bitlets easily
 */
public class BitletSynchronizer
{
    // ---
    // Singleton instance
    // ---

    public static BitletSynchronizer instance = new BitletSynchronizer();


    // --
    // Members
    // --

    private BitletCache cache = new BitletMemoryCache();


    // ---
    // Loading
    // ---

    public <T> void load(BitletHandler<T> bitletHandler, BitletResultObserver.CompletionListener<T> completionListener)
    {
        load(bitletHandler, null, false, completionListener);
    }

    public <T> void load(BitletHandler<T> bitletHandler, String cacheKey, BitletResultObserver.CompletionListener<T> completionListener)
    {
        load(bitletHandler, cacheKey, false, completionListener);
    }

    public <T> void load(BitletHandler<T> bitletHandler, String cacheKey, boolean forced, final BitletResultObserver.CompletionListener<T> completionListener)
    {
        if (cacheKey != null)
        {
            cache.createEntryIfNeeded(cacheKey, bitletHandler);
            BitletCacheEntry entry = cache.getEntry(cacheKey);
            entry.load(forced, new BitletResultObserver<>(new BitletResultObserver.CompletionListener<T>()
            {
                @Override
                public void onSuccess(T bitlet)
                {
                    completionListener.onSuccess(bitlet);
                }

                @Override
                public void onError(Throwable exception)
                {
                    completionListener.onError(exception);
                }
            }));
        }
        else
        {
            bitletHandler.load(new BitletResultObserver<>(completionListener));
        }
    }

    public <T> void load(BitletHandler<T> bitletHandler, BitletResultObserver.SimpleCompletionListener<T> completionListener)
    {
        load(bitletHandler, null, false, completionListener);
    }

    public <T> void load(BitletHandler<T> bitletHandler, String cacheKey, BitletResultObserver.SimpleCompletionListener<T> completionListener)
    {
        load(bitletHandler, cacheKey, false, completionListener);
    }

    public <T> void load(BitletHandler<T> bitletHandler, String cacheKey, boolean forced, final BitletResultObserver.SimpleCompletionListener<T> completionListener)
    {
        if (cacheKey != null)
        {
            cache.createEntryIfNeeded(cacheKey, bitletHandler);
            BitletCacheEntry entry = cache.getEntry(cacheKey);
            entry.load(forced, new BitletResultObserver<>(new BitletResultObserver.SimpleCompletionListener<T>()
            {
                @Override
                public void onFinish(T bitlet, Throwable exception)
                {
                    completionListener.onFinish(bitlet, exception);
                }
            }));
        }
        else
        {
            bitletHandler.load(new BitletResultObserver<>(completionListener));
        }
    }


    // ---
    // Cache control
    // ---

    public void clearCache()
    {
        cache.clear("*", true);
    }

    public void clearCache(String filter)
    {
        cache.clear(filter, true);
    }

    public void clearCache(String filter, boolean recursive)
    {
        cache.clear(filter, recursive);
    }

    public BitletCache getCache()
    {
        return cache;
    }

    public void setCache(BitletCache cache)
    {
        this.cache = cache;
    }
}
