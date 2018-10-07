package com.crescentflare.bitletsynchronizerexample.model.servers;

import android.text.TextUtils;

import com.crescentflare.bitletsynchronizer.bitlet.BitletHandler;
import com.crescentflare.bitletsynchronizer.bitlet.BitletObserver;
import com.crescentflare.bitletsynchronizerexample.MapUtil;
import com.crescentflare.bitletsynchronizerexample.Settings;
import com.crescentflare.bitletsynchronizerexample.model.shared.SimpleBitlet;
import com.crescentflare.bitletsynchronizerexample.model.usage.UsageItem;
import com.crescentflare.bitletsynchronizerexample.model.usage.UsageUnit;
import com.crescentflare.bitletsynchronizerexample.network.Api;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Server model: a server
 * One server in a list of servers
 */
public class Server
{
    // ---
    // Members
    // ---

    @SerializedName("id")
    private String serverId;

    @SerializedName("name")
    private String name;

    @SerializedName("location")
    private String location;

    @SerializedName("description")
    private String description;

    @SerializedName("os")
    private String os;

    @SerializedName("os_version")
    private String osVersion;

    @SerializedName("data_traffic")
    private UsageItem dataTraffic;

    @SerializedName("server_load")
    private UsageItem serverLoad;

    @SerializedName("enabled")
    private boolean enabled;


    // ---
    // Bitlet instance
    // ---

    public static String cacheKey(String serverId)
    {
        return "/servers/" + serverId;
    }

    public static SimpleBitlet<Server> bitletInstance(final String serverId)
    {
        Map<String, Object> mockedData = MapUtil.newMap(
                "id", "mocked",
                "name", "Mock server",
                "description", "Internal mocked data",
                "os", "Unknown",
                "os_version", "1.0",
                "location", "Home",
                "data_traffic", MapUtil.newMap(
                        "amount", 2.0,
                        "unit", "GB",
                        "label", "2.0 GB"
                ),
                "server_load", MapUtil.newMap(
                        "amount", 10,
                        "unit", "percent",
                        "label", "10%"
                ),
                "enabled", true
        );
        return new SimpleBitlet<>("/servers/" + serverId, 10 * 60 * 1000, mockedData, Server.class);
    }


    // ---
    // Generated code
    // ---

    public String getServerId()
    {
        return serverId;
    }

    public void setServerId(String serverId)
    {
        this.serverId = serverId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getOs()
    {
        return os;
    }

    public void setOs(String os)
    {
        this.os = os;
    }

    public String getOsVersion()
    {
        return osVersion;
    }

    public void setOsVersion(String osVersion)
    {
        this.osVersion = osVersion;
    }

    public UsageItem getDataTraffic()
    {
        return dataTraffic;
    }

    public void setDataTraffic(UsageItem dataTraffic)
    {
        this.dataTraffic = dataTraffic;
    }

    public UsageItem getServerLoad()
    {
        return serverLoad;
    }

    public void setServerLoad(UsageItem serverLoad)
    {
        this.serverLoad = serverLoad;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    @Override
    public String toString()
    {
        return "Server{" +
                "serverId='" + serverId + '\'' +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", description='" + description + '\'' +
                ", os='" + os + '\'' +
                ", osVersion='" + osVersion + '\'' +
                ", dataTraffic=" + dataTraffic +
                ", serverLoad=" + serverLoad +
                ", enabled=" + enabled +
                '}';
    }
}
