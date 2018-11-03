package com.crescentflare.bitletsynchronizer.operation;

/**
 * Bitlet Synchronizer operation: a bitlet operation item
 * Defines the interface for a task item in a bitlet operation
 */
public interface BitletOperationItem
{
    void run(CompletionListener listener);
    boolean isRunning();
    boolean isEnabled();
    void setEnabled(boolean enabled);

    interface CompletionListener
    {
        void onComplete(Throwable exception);
    }
}
