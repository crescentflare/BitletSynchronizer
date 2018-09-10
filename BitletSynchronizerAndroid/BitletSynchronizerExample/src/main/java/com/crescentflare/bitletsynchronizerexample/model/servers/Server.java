package com.crescentflare.bitletsynchronizerexample.model.servers;

import android.text.TextUtils;

import com.crescentflare.bitletsynchronizer.bitlet.BitletHandler;
import com.crescentflare.bitletsynchronizer.bitlet.BitletObserver;
import com.crescentflare.bitletsynchronizerexample.Settings;
import com.crescentflare.bitletsynchronizerexample.model.usage.UsageItem;
import com.crescentflare.bitletsynchronizerexample.model.usage.UsageUnit;
import com.crescentflare.bitletsynchronizerexample.network.Api;
import com.google.gson.annotations.SerializedName;

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

    public static BitletHandler<Server> bitletInstance(final String serverId)
    {
        return new BitletHandler<Server>()
        {
            @Override
            public void load(final BitletObserver<Server> observer)
            {
                if (!TextUtils.isEmpty(Settings.instance.getServerAddress()))
                {
                    Api.getInstance(Settings.instance.getServerAddress()).servers().getServerDetails(serverId).enqueue(new Callback<Server>()
                    {
                        @Override
                        public void onResponse(Call<Server> call, Response<Server> response)
                        {
                            observer.setBitlet(response.body());
                            observer.setBitletExpireTime(System.currentTimeMillis() + 10 * 60 * 1000);
                            observer.finish();
                        }

                        @Override
                        public void onFailure(Call<Server> call, Throwable exception)
                        {
                            observer.setException(exception);
                            observer.finish();
                        }
                    });
                }
                else
                {
                    // Mock object
                    Server server = new Server();
                    UsageItem dataTraffic = new UsageItem();
                    UsageItem serverLoad = new UsageItem();
                    dataTraffic.setAmount(2.0f);
                    dataTraffic.setUnit(UsageUnit.GB);
                    dataTraffic.setLabel("2.0 GB");
                    serverLoad.setAmount(10.0f);
                    serverLoad.setUnit(UsageUnit.Percent);
                    serverLoad.setLabel("10%");
                    server.setServerId("mocked");
                    server.setName("Mock server");
                    server.setDescription("Internal mocked data");
                    server.setOs("Unknown");
                    server.setOsVersion("1.0");
                    server.setLocation("Home");
                    server.setDataTraffic(dataTraffic);
                    server.setServerLoad(serverLoad);
                    server.setEnabled(true);

                    // Inform observer
                    observer.setBitlet(server);
                    observer.setBitletExpireTime(System.currentTimeMillis() + 10 * 60 * 1000);
                    observer.finish();
                }
            }
        };
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
