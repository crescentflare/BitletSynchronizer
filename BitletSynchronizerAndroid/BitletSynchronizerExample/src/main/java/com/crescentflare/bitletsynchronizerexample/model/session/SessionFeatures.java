package com.crescentflare.bitletsynchronizerexample.model.session;

/**
 * Session model: features of a session
 * Stores the features available of the session, and their permissions
 */
public class SessionFeatures
{
    // ---
    // Members
    // ---

    private SessionPermission usage;
    private SessionPermission servers;


    // ---
    // Generated code
    // ---

    public SessionPermission getUsage()
    {
        return usage;
    }

    public void setUsage(SessionPermission usage)
    {
        this.usage = usage;
    }

    public SessionPermission getServers()
    {
        return servers;
    }

    public void setServers(SessionPermission servers)
    {
        this.servers = servers;
    }

    @Override
    public String toString()
    {
        return "SessionFeatures{" +
                "usage=" + usage +
                ", servers=" + servers +
                '}';
    }
}
