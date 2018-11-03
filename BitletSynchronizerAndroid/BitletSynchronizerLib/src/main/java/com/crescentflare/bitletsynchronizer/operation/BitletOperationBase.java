package com.crescentflare.bitletsynchronizer.operation;

import com.crescentflare.bitletsynchronizer.bitlet.BitletHandler;
import com.crescentflare.bitletsynchronizer.synchronizer.BitletSynchronizer;

import java.util.ArrayList;
import java.util.List;

/**
 * Bitlet Synchronizer operation: an operation base class
 * Used as a base class to contain shared code for operation sequence and group
 */
public abstract class BitletOperationBase implements BitletOperation
{
    // --
    // Members
    // --

    protected boolean running = false;
    protected boolean requestCancel = false;
    protected BitletSynchronizer bitletSynchronizer;
    private BitletOperation.CompletionListener completionListener;
    protected List<BitletOperationItem> items = new ArrayList<>();
    protected Throwable exception;


    // ---
    // Implementation
    // ---

    @Override
    public boolean start(BitletSynchronizer bitletSynchronizer, BitletOperation.CompletionListener listener)
    {
        if (running)
        {
            return false;
        }
        this.bitletSynchronizer = bitletSynchronizer;
        this.completionListener = listener;
        requestCancel = false;
        running = true;
        afterStart();
        return true;
    }

    @Override
    public void cancel()
    {
        requestCancel = true;
    }


    // --
    // MARK: Base lifecycle
    // --

    protected abstract void afterStart();

    protected void complete()
    {
        if (running)
        {
            running = false;
            if (completionListener != null)
            {
                completionListener.onComplete(exception, requestCancel);
            }
            completionListener = null;
            bitletSynchronizer = null;
        }
    }


    // --
    // MARK: Add operation items
    // --

    public <T> void addBitletLoad(BitletHandler<T> bitletHandler, final LoadCompletionListener<T> listener)
    {
        addBitletLoad(bitletHandler, null, false, listener);
    }

    public <T> void addBitletLoad(BitletHandler<T> bitletHandler, String cacheKey, final LoadCompletionListener<T> listener)
    {
        addBitletLoad(bitletHandler, cacheKey, false, listener);
    }

    public <T> void addBitletLoad(BitletHandler<T> bitletHandler, String cacheKey, boolean forced, final LoadCompletionListener<T> listener)
    {
        items.add(new BitletOperationLoadItem<T>(bitletHandler, cacheKey, forced, new BitletOperationLoadItem.CompletionListener<T>()
        {
            @Override
            public void onComplete(T bitlet, Throwable exception)
            {
                if (listener != null)
                {
                    listener.onComplete(bitlet, exception, BitletOperationBase.this);
                }
            }
        }));
    }

    public void addOperation(BitletOperation operation, final NestedCompletionListener listener)
    {
        items.add(new BitletOperationNestedItem(operation, new BitletOperationNestedItem.CompletionListener()
        {
            @Override
            public void onComplete(Throwable exception, boolean canceled)
            {
                if (listener != null)
                {
                    listener.onComplete(exception, canceled, BitletOperationBase.this);
                }
            }
        }));
    }


    // ---
    // Cache check
    // ---

    public List<String> getAllCacheKeys()
    {
        List<String> cacheKeys = new ArrayList<>();
        for (BitletOperationItem item : items)
        {
            if (item instanceof BitletOperationLoadItem)
            {
                String cacheKey = ((BitletOperationLoadItem)item).getCacheKey();
                if (cacheKey != null)
                {
                    cacheKeys.add(cacheKey);
                }
            }
            else if (item instanceof BitletOperationNestedItem)
            {
                BitletOperation operation = ((BitletOperationNestedItem)item).getOperation();
                if (operation instanceof BitletOperationBase)
                {
                    cacheKeys.addAll(((BitletOperationBase)operation).getAllCacheKeys());
                }
            }
        }
        return cacheKeys;
    }

    public List<String> getIncludedCacheKeys()
    {
        List<String> cacheKeys = new ArrayList<>();
        for (BitletOperationItem item : items)
        {
            if (item.isEnabled())
            {
                if (item instanceof BitletOperationLoadItem)
                {
                    String cacheKey = ((BitletOperationLoadItem)item).getCacheKey();
                    if (cacheKey != null)
                    {
                        cacheKeys.add(cacheKey);
                    }
                }
                else if (item instanceof BitletOperationNestedItem)
                {
                    BitletOperation operation = ((BitletOperationNestedItem)item).getOperation();
                    if (operation instanceof BitletOperationBase)
                    {
                        cacheKeys.addAll(((BitletOperationBase)operation).getAllCacheKeys());
                    }
                }
            }
        }
        return cacheKeys;
    }

    public void setCacheKeyIncluded(String cacheKey, boolean included)
    {
        for (BitletOperationItem item : items)
        {
            if (item instanceof BitletOperationLoadItem)
            {
                String checkCacheKey = ((BitletOperationLoadItem)item).getCacheKey();
                if (checkCacheKey != null && checkCacheKey.equals(cacheKey))
                {
                    item.setEnabled(included);
                }
            }
            else if (item instanceof BitletOperationNestedItem)
            {
                BitletOperation operation = ((BitletOperationNestedItem) item).getOperation();
                if (operation instanceof BitletOperationBase)
                {
                    ((BitletOperationBase)operation).setCacheKeyIncluded(cacheKey, included);
                }
            }
        }
    }


    // ---
    // Completion listeners
    // ---

    public interface LoadCompletionListener<T>
    {
        void onComplete(T bitlet, Throwable exception, BitletOperationBase operation);
    }

    public interface NestedCompletionListener
    {
        void onComplete(Throwable exception, boolean canceled, BitletOperationBase operation);
    }
}
