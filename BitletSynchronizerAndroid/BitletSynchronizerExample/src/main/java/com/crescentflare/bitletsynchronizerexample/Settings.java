package com.crescentflare.bitletsynchronizerexample;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * An easy way to handle settings for the Bitlet Synchronizer example
 */
public class Settings
{
    // ---
    // Prefences keys
    // ---

    private static final String KEY_SERVER_ADDRESS = "PREF_SERVER_ADDRESS";
    private static final String KEY_LAST_LOGGED_IN_USER = "PREF_LAST_LOGGED_IN_USER";
    private static final String KEY_SESSION_COOKIE = "PREF_SESSION_COOKIE";


    // ---
    // Members
    // ---

    private String serverAddress = "";
    private String lastLoggedInUser = "";
    private String sessionCookie = "";
    private boolean settingsLoaded = false;


    // ---
    // Singleton instance
    // ---

    public static Settings instance = new Settings();

    private Settings()
    {
    }


    // ---
    // Settings access
    // ---

    public String getServerAddress()
    {
        loadSettingsIfNeeded();
        if (serverAddress.length() > 0 && !serverAddress.startsWith("http"))
        {
            return "http://" + serverAddress;
        }
        return serverAddress;
    }

    public void setServerAddress(String serverAddress)
    {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(ExampleApplication.context).edit();
        this.serverAddress = serverAddress;
        editor.putString(KEY_SERVER_ADDRESS, serverAddress);
        editor.apply();
    }

    public String getLastLoggedInUser()
    {
        loadSettingsIfNeeded();
        return lastLoggedInUser;
    }

    public void setLastLoggedInUser(String lastLoggedInUser)
    {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(ExampleApplication.context).edit();
        this.lastLoggedInUser = lastLoggedInUser;
        editor.putString(KEY_LAST_LOGGED_IN_USER, lastLoggedInUser);
        editor.apply();
    }

    public String getSessionCookie()
    {
        loadSettingsIfNeeded();
        return sessionCookie;
    }

    public void setSessionCookie(String sessionCookie)
    {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(ExampleApplication.context).edit();
        this.sessionCookie = sessionCookie;
        editor.putString(KEY_SESSION_COOKIE, sessionCookie);
        editor.apply();
    }


    // ---
    // Helper
    // ---

    public void loadSettingsIfNeeded()
    {
        if (!settingsLoaded)
        {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ExampleApplication.context);
            serverAddress = preferences.getString(KEY_SERVER_ADDRESS, "");
            lastLoggedInUser = preferences.getString(KEY_LAST_LOGGED_IN_USER, "");
            sessionCookie = preferences.getString(KEY_SESSION_COOKIE, "");
            settingsLoaded = true;
        }
    }
}
