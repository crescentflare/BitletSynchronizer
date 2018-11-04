package com.crescentflare.bitletsynchronizer.cache;

import com.crescentflare.bitletsynchronizer.bitlet.BitletHandler;
import com.crescentflare.bitletsynchronizer.bitlet.BitletObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * Bitlet Synchronizer cache: a cache entry
 * Manages the data and state of a single entry in the bitlet cache
 */
public class BitletCacheEntry<T>
{
    // ---
    // Members
    // ---

    private State state = State.Unavailable;
    private T bitletData;
    private String bitletHash;
    private long bitletExpireTime = -1;
    private long bitletHashUpdatedTime = -1;
    private BitletHandler<T> handler;
    private BitletCacheObserver<T> loadingObserver;


    // ---
    // Initialization
    // ---

    public BitletCacheEntry(BitletHandler<T> handler)
    {
        this.handler = handler;
    }


    // ---
    // Loading
    // ---

    public void load(BitletObserver<T> observer)
    {
        load(false, observer);
    }

    public void load(boolean forced, BitletObserver<T> observer)
    {
        if (!forced && bitletData != null && !isExpired())
        {
            return;
        }
        boolean alreadyLoading = true;
        if (loadingObserver == null)
        {
            loadingObserver = new BitletCacheObserver<>(new Runnable()
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
                        bitletHash = loadingObserver.getBitletHash();
                        if (loadingObserver.getBitletExpireTime() >= 0)
                        {
                            bitletExpireTime = loadingObserver.getBitletExpireTime();
                        }
                        bitletHashUpdatedTime = bitletHash != null ? System.currentTimeMillis() : -1;
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
        if (observer != null)
        {
            loadingObserver.includeObserver(observer);
        }
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

    public void setState(State state)
    {
        this.state = state;
    }

    public T getBitletData()
    {
        return bitletData;
    }

    public void setBitletData(T bitletData)
    {
        this.bitletData = bitletData;
    }

    public String getBitletHash()
    {
        return bitletHash;
    }

    public void setBitletHash(String bitletHash)
    {
        this.bitletHash = bitletHash;
    }

    public long getBitletExpireTime()
    {
        return bitletExpireTime;
    }

    public void setBitletExpireTime(long bitletExpireTime)
    {
        this.bitletExpireTime = bitletExpireTime;
    }

    public long getBitletHashUpdatedTime()
    {
        return bitletHashUpdatedTime;
    }

    public void setBitletHashUpdatedTime(long bitletHashUpdatedTime)
    {
        this.bitletHashUpdatedTime = bitletHashUpdatedTime;
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
        // Used to store state
        Unavailable,
        Loading,
        Ready,
        Refreshing,

        // Used only for checking
        LoadingOrRefreshing
    }


    // ---
    // Custom cache observer
    // ---

    private static class BitletCacheObserver<T> implements BitletObserver<T>
    {
        private T bitletData = null;
        private String bitletHash = null;
        private long bitletExpireTime = -1;
        private Throwable exception = null;
        private List<BitletObserver<T>> includedObservers = new ArrayList<>();
        private Runnable completionRunnable;

        public BitletCacheObserver(Runnable completionRunnable)
        {
            this.completionRunnable = completionRunnable;
        }

        @Override
        public void setBitlet(T data)
        {
            bitletData = data;
            for (BitletObserver<T> observer : includedObservers)
            {
                observer.setBitlet(data);
            }
        }

        @Override
        public void setBitletHash(String hash)
        {
            bitletHash = hash;
            for (BitletObserver<T> observer : includedObservers)
            {
                observer.setBitletHash(hash);
            }
        }

        @Override
        public void setBitletExpireTime(long expireTime)
        {
            bitletExpireTime = expireTime;
            for (BitletObserver<T> observer : includedObservers)
            {
                observer.setBitletExpireTime(expireTime);
            }
        }

        @Override
        public void setException(Throwable exception)
        {
            this.exception = exception;
            for (BitletObserver<T> observer : includedObservers)
            {
                observer.setException(exception);
            }
        }

        @Override
        public void finish()
        {
            completionRunnable.run();
            for (BitletObserver<T> observer : includedObservers)
            {
                observer.finish();
            }
        }

        public void includeObserver(BitletObserver<T> observer)
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

        public T getBitletData()
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
