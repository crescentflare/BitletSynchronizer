package com.crescentflare.bitletsynchronizer.bitlet;

/**
 * Bitlet Synchronizer bitlet: handle bitlet transfer
 * An interface to handle transfer of a bitlet from an API
 */
public interface BitletObserver<T>
{
    void setBitlet(T data);
    void setBitletHash(String hash);
    void setException(Throwable exception);
    void finish();
}
