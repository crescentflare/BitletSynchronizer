package com.crescentflare.bitletsynchronizer.cache;

import com.crescentflare.bitletsynchronizer.bitlet.BitletHandler;
import com.crescentflare.bitletsynchronizer.bitlet.BitletObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * Bitlet Synchronizer cache: a cache entry
 * Manages the data and state of a single entry in the bitlet cache
 */
public class BitletCacheEntry
{
    // ---
    // Members
    // ---

    private State state = State.Unavailable;
    private Object bitletData;
    private long bitletExpireTime = -1;
    private BitletHandler handler;
    private BitletCacheObserver loadingObserver;


    // ---
    // Initialization
    // ---

    public BitletCacheEntry(BitletHandler handler)
    {
        this.handler = handler;
    }


    // ---
    // Loading
    // ---

    public void load(BitletObserver observer)
    {
        load(false, observer);
    }

    public void load(boolean forced, BitletObserver observer)
    {
        if (!forced && bitletData != null && !isExpired())
        {
            return;
        }
        boolean alreadyLoading = true;
        if (loadingObserver == null)
        {
            loadingObserver = new BitletCacheObserver(new Runnable()
            {
                @Override
                public void run()
                {
                    if (loadingObserver.getBitletData() == null && loadingObserver.getException() == null)
                    {
                        loadingObserver.setException(new Exception("Unknown bitlet error"));
                    }
                    if (loadingObserver.getException() == null)
                    {
                        if (loadingObserver.getBitletData() != null)
                        {
                            bitletData = loadingObserver.getBitletData();
                        }
                        if (loadingObserver.getBitletExpireTime() >= 0)
                        {
                            bitletExpireTime = loadingObserver.getBitletExpireTime();
                        }
                        state = State.Ready;
                    }
                    else
                    {
                        state = state == State.Refreshing ? State.Ready : State.Unavailable;
                    }
                    loadingObserver = null;
                }
            });
            alreadyLoading = false;
        }
        loadingObserver.includeObserver(observer);
        if (!alreadyLoading)
        {
            state = state == State.Unavailable ? State.Loading : State.Refreshing;
            handler.load(loadingObserver);
        }
    }


    // ---
    // Access state
    // ---

    public State getState()
    {
        return state;
    }

    public Object getBitletData()
    {
        return bitletData;
    }

    public boolean isExpired()
    {
        if (bitletExpireTime >= 0)
        {
            return bitletExpireTime <= System.currentTimeMillis();
        }
        return false;
    }

    public void forceExpiration()
    {
        bitletExpireTime = System.currentTimeMillis();
    }


    // ---
    // State enum
    // ---

    public enum State
    {
        Unavailable,
        Loading,
        Ready,
        Refreshing
    }


    // ---
    // Custom cache observer
    // ---

    private static class BitletCacheObserver implements BitletObserver
    {
        private Object bitletData = null;
        private String bitletHash = null;
        private long bitletExpireTime = -1;
        private Throwable exception = null;
        private List<BitletObserver> includedObservers = new ArrayList<>();
        private Runnable completionRunnable;

        public BitletCacheObserver(Runnable completionRunnable)
        {
            this.completionRunnable = completionRunnable;
        }

        @Override
        public void setBitlet(Object data)
        {
            bitletData = data;
            for (BitletObserver observer : includedObservers)
            {
                observer.setBitlet(data);
            }
        }

        @Override
        public void setBitletHash(String hash)
        {
            bitletHash = hash;
            for (BitletObserver observer : includedObservers)
            {
                observer.setBitletHash(hash);
            }
        }

        @Override
        public void setBitletExpireTime(long expireTime)
        {
            bitletExpireTime = expireTime;
            for (BitletObserver observer : includedObservers)
            {
                observer.setBitletExpireTime(expireTime);
            }
        }

        @Override
        public void setException(Throwable exception)
        {
            this.exception = exception;
            for (BitletObserver observer : includedObservers)
            {
                observer.setException(exception);
            }
        }

        @Override
        public void finish()
        {
            completionRunnable.run();
            for (BitletObserver observer : includedObservers)
            {
                observer.finish();
            }
        }

        public void includeObserver(BitletObserver observer)
        {
            includedObservers.add(observer);
            if (bitletData != null)
            {
                observer.setBitlet(bitletData);
            }
            if (bitletHash != null)
            {
                observer.setBitletHash(bitletHash);
            }
            if (bitletExpireTime >= 0)
            {
                observer.setBitletExpireTime(bitletExpireTime);
            }
            if (exception != null)
            {
                observer.setException(exception);
            }
        }

        public Object getBitletData()
        {
            return bitletData;
        }

        public String getBitletHash()
        {
            return bitletHash;
        }

        public long getBitletExpireTime()
        {
            return bitletExpireTime;
        }

        public Throwable getException()
        {
            return exception;
        }
    }
}
