package com.crescentflare.bitletsynchronizer.synchronizer;

import com.crescentflare.bitletsynchronizer.bitlet.BitletHandler;
import com.crescentflare.bitletsynchronizer.bitlet.BitletObserver;
import com.crescentflare.bitletsynchronizer.bitlet.BitletResultObserver;

/**
 * Bitlet Synchronizer synchronizer: synchronizes bitlets
 * A singleton to access and handle bitlets easily
 */
public class BitletSynchronizer
{
    // ---
    // Singleton instance
    // ---

    public static BitletSynchronizer instance = new BitletSynchronizer();


    // ---
    // Loading
    // ---

    public <T> void load(BitletHandler<T> bitletHandler, BitletResultObserver.CompletionListener<T> completionListener)
    {
        bitletHandler.load(new BitletResultObserver<T>(completionListener));
    }

    public <T> void load(BitletHandler<T> bitletHandler, BitletResultObserver.SimpleCompletionListener<T> completionListener)
    {
        bitletHandler.load(new BitletResultObserver<T>(completionListener));
    }
}
