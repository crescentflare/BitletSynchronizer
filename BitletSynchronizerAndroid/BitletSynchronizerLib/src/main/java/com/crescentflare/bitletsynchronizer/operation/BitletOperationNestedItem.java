package com.crescentflare.bitletsynchronizer.operation;

import com.crescentflare.bitletsynchronizer.synchronizer.BitletSynchronizer;

/**
 * Bitlet Synchronizer operation: a bitlet operation nested item
 * An operation task item that starts another operation as a nested item
 */
public class BitletOperationNestedItem implements BitletOperationItem
{
    // --
    // Members
    // --

    private BitletOperation operation;
    private CompletionListener itemCompletionListener;
    private boolean running = false;
    private boolean enabled = true;


    // ---
    // Initialization
    // ---

    public BitletOperationNestedItem(BitletOperation operation, CompletionListener listener)
    {
        this.operation = operation;
        this.itemCompletionListener = listener;
    }


    // ---
    // Implementation
    // ---

    @Override
    public void run(BitletSynchronizer bitletSynchronizer, final BitletOperationItem.CompletionListener listener)
    {
        running = true;
        boolean canStart = operation.start(bitletSynchronizer, new BitletOperation.CompletionListener()
        {
            @Override
            public void onComplete(Throwable exception, boolean canceled)
            {
                Throwable completeException = exception;
                running = false;
                if (exception == null && canceled)
                {
                    completeException = new Exception("Nested operation canceled");
                }
                if (itemCompletionListener != null)
                {
                    itemCompletionListener.onComplete(exception, canceled);
                }
                if (listener != null)
                {
                    listener.onComplete(completeException);
                }
            }
        });
        if (!canStart)
        {
            Throwable exception = new Exception("Nested operation could not be started");
            running = false;
            if (itemCompletionListener != null)
            {
                itemCompletionListener.onComplete(exception, false);
            }
            if (listener != null)
            {
                listener.onComplete(exception);
            }
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

    public BitletOperation getOperation()
    {
        return operation;
    }


    // ---
    // Completion listener
    // ---

    interface CompletionListener
    {
        void onComplete(Throwable exception, boolean canceled);
    }
}
