package com.crescentflare.bitletsynchronizer.operation;

import com.crescentflare.bitletsynchronizer.synchronizer.BitletSynchronizer;

/**
 * Bitlet Synchronizer operation: a bitlet operation item
 * Defines the interface for a task item in a bitlet operation
 */
public interface BitletOperationItem
{
    void run(BitletSynchronizer bitletSynchronizer, CompletionListener listener);
    boolean isRunning();
    boolean isEnabled();
    void setEnabled(boolean enabled);

    interface CompletionListener
    {
        void onComplete(Throwable exception);
    }
}
