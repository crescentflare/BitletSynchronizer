package com.crescentflare.bitletsynchronizer.bitlet;

/**
 * Bitlet Synchronizer bitlet: handle bitlet transfer
 * An interface to handle transfer of a bitlet from an API
 */
public interface BitletHandler<T>
{
    void load(BitletObserver<T> observer);
}
