package com.crescentflare.bitletsynchronizer.bitlet;

/**
 * Bitlet Synchronizer bitlet: observe a bitlet handler result
 * An implementation of a bitlet observer to be used when only observing the final result is needed
 */
public class BitletResultObserver<T> implements BitletObserver<T>
{
    // ---
    // Members
    // ---

    private T bitletData;
    private String bitletHash;
    private Throwable exception;
    private CompletionListener<T> completionListener;
    private SimpleCompletionListener<T> simpleCompletionListener;


    // ---
    // Initialization
    // ---

    public BitletResultObserver(CompletionListener<T> listener)
    {
        this.completionListener = listener;
    }

    public BitletResultObserver(SimpleCompletionListener<T> listener)
    {
        this.simpleCompletionListener = listener;
    }


    // ---
    // BitletObserver implementation
    // ---

    @Override
    public void setBitlet(T data)
    {
        this.bitletData = data;
    }

    @Override
    public void setBitletHash(String hash)
    {
        this.bitletHash = hash;
    }

    @Override
    public void setException(Throwable exception)
    {
        this.exception = exception;
    }

    @Override
    public void finish()
    {
        Throwable defaultException = new Exception("Unknown bitlet error");
        if (bitletData != null && exception == null)
        {
            if (completionListener != null)
            {
                completionListener.onSuccess(bitletData);
            }
            if (simpleCompletionListener != null)
            {
                simpleCompletionListener.onFinish(bitletData, null);
            }
        }
        else
        {
            Throwable errorException = this.exception;
            if (errorException == null)
            {
                errorException = defaultException;
            }
            if (completionListener != null)
            {
                completionListener.onError(errorException);
            }
            if (simpleCompletionListener != null)
            {
                simpleCompletionListener.onFinish(null, errorException);
            }
        }
    }


    // ---
    // Completion listener interfaces
    // ---

    public interface CompletionListener<T>
    {
        void onSuccess(T bitlet);
        void onError(Throwable exception);
    }

    public interface SimpleCompletionListener<T>
    {
        void onFinish(T bitlet, Throwable exception);
    }
}
