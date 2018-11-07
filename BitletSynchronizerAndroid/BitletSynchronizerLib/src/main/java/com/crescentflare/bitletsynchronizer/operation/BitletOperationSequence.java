package com.crescentflare.bitletsynchronizer.operation;

import java.lang.ref.WeakReference;

/**
 * Bitlet Synchronizer operation: an operation sequence
 * Operation sequences do one call after the other (while waiting for the previous to finish)
 */
public class BitletOperationSequence extends BitletOperationBase
{
    // --
    // Members
    // --

    private int itemIndex = -1;


    // ---
    // Implementation
    // ---

    @Override
    protected void afterStart()
    {
        itemIndex = -1;
        next();
    }

    private void next()
    {
        if (running)
        {
            if (!requestCancel && itemIndex + 1 < items.size())
            {
                itemIndex++;
                if (items.get(itemIndex).isEnabled())
                {
                    items.get(itemIndex).run(bitletSynchronizer, forceAll, new BitletOperationItem.CompletionListener()
                    {
                        @Override
                        public void onComplete(Throwable exception)
                        {
                            BitletOperationSequence.this.exception = exception;
                            if (BitletOperationSequence.this.exception != null)
                            {
                                cancel();
                            }
                            next();
                        }
                    });
                }
                else
                {
                    next();
                }
            }
            else
            {
                complete();
            }
        }
    }
}
