package com.crescentflare.bitletsynchronizer.operation;

import com.crescentflare.bitletsynchronizer.synchronizer.BitletSynchronizer;

/**
 * Bitlet Synchronizer operation: a bitlet operation
 * Defines the interface for executing one or more tasks operating on the bitlet synchronizer, useful when multiple calls are needed
 * Operations can also be nested in each other
 */
public interface BitletOperation
{
    boolean start(BitletSynchronizer bitletSynchronizer, boolean forceAll, CompletionListener listener);
    void cancel();

    interface CompletionListener
    {
        void onComplete(Throwable exception, boolean canceled);
    }
}
