package com.crescentflare.bitletsynchronizer.operation;

import com.crescentflare.bitletsynchronizer.bitlet.BitletHandler;
import com.crescentflare.bitletsynchronizer.bitlet.BitletResultObserver;
import com.crescentflare.bitletsynchronizer.synchronizer.BitletSynchronizer;

/**
 * Bitlet Synchronizer operation: a bitlet operation load item
 * An operation task item that loads a bitlet through the bitlet synchronizer
 */
public class BitletOperationLoadItem<T> implements BitletOperationItem
{
    // --
    // Members
    // --

    private BitletHandler<T> bitletHandler;
    private boolean forced;
    private CompletionListener<T> itemCompletionListener;
    private boolean running = false;
    private String cacheKey;
    private boolean enabled = true;


    // ---
    // Initialization
    // ---

    public BitletOperationLoadItem(BitletHandler<T> handler, String cacheKey, boolean forced, CompletionListener<T> listener)
    {
        this.bitletHandler = handler;
        this.cacheKey = cacheKey;
        this.forced = forced;
        this.itemCompletionListener = listener;
    }


    // ---
    // Implementation
    // ---

    @Override
    public void run(BitletSynchronizer bitletSynchronizer, final BitletOperationItem.CompletionListener listener)
    {
        if (cacheKey != null && !bitletSynchronizer.getCacheEntry(cacheKey, Object.class).isExpired() && !forced)
        {
            if (listener != null)
            {
                listener.onComplete(null);
            }
        }
        else
        {
            running = true;
            bitletSynchronizer.load(bitletHandler, cacheKey, true, new BitletResultObserver.SimpleCompletionListener<T>()
            {
                @Override
                public void onFinish(T bitlet, Throwable exception)
                {
                    running = false;
                    if (itemCompletionListener != null)
                    {
                        itemCompletionListener.onComplete(bitlet, exception);
                    }
                    if (listener != null)
                    {
                        listener.onComplete(exception);
                    }
                }
            });
        }
    }

    @Override
    public boolean isRunning()
    {
        return running;
    }

    @Override
    public boolean isEnabled()
    {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }


    // ---
    // Obtain data
    // ---

    public String getCacheKey()
    {
        return cacheKey;
    }


    // ---
    // Completion listener
    // ---

    interface CompletionListener<T>
    {
        void onComplete(T bitlet, Throwable exception);
    }
}
