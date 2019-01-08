package com.crescentflare.bitletsynchronizer.operation;

/**
 * Bitlet Synchronizer operation: an operation group
 * Operation groups do multiple calls at once
 */
public class BitletOperationGroup extends BitletOperationBase
{
    // ---
    // Implementation
    // ---

    @Override
    protected void afterStart()
    {
        for (BitletOperationItem item : items)
        {
            if (item.isEnabled())
            {
                item.run(bitletSynchronizer, forceAll, new BitletOperationItem.CompletionListener()
                {
                    @Override
                    public void onComplete(Throwable exception)
                    {
                        if (!BitletOperationGroup.this.requestCancel)
                        {
                            BitletOperationGroup.this.exception = exception;
                        }
                        if (BitletOperationGroup.this.exception != null)
                        {
                            cancel();
                        }
                        checkCompletion();
                    }
                });
            }
        }
        if (items.size() == 0)
        {
            complete();
        }
    }

    private void checkCompletion()
    {
        if (running)
        {
            boolean itemRunning = false;
            for (BitletOperationItem item : items)
            {
                if (item.isRunning())
                {
                    itemRunning = true;
                    break;
                }
            }
            if (!itemRunning)
            {
                complete();
            }
        }
    }


    // ---
    // Duplication
    // ---

    @Override
    public BitletOperation duplicate()
    {
        BitletOperationGroup group = new BitletOperationGroup();
        duplicateBaseProperties(group);
        return group;
    }
}
