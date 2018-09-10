package com.crescentflare.bitletsynchronizerexample.model.servers;

import android.text.TextUtils;

import com.crescentflare.bitletsynchronizer.bitlet.BitletHandler;
import com.crescentflare.bitletsynchronizer.bitlet.BitletObserver;
import com.crescentflare.bitletsynchronizerexample.Settings;
import com.crescentflare.bitletsynchronizerexample.network.Api;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Server model: simple server list
 * Stores an overview of all servers without details
 */
public class ServerList
{
    // ---
    // Members
    // ---

    private List<Server> servers;


    // ---
    // Bitlet instance
    // ---

    public static String cacheKey()
    {
        return "/servers";
    }

    public static BitletHandler<ServerList> bitletInstance()
    {
        if (TextUtils.isEmpty(Settings.instance.getServerAddress()))
        {
            return mockedBitletInstance();
        }
        return new BitletHandler<ServerList>()
        {
            @Override
            public void load(final BitletObserver<ServerList> observer)
            {
                Api.getInstance(Settings.instance.getServerAddress()).servers().getServerList().enqueue(new Callback<List<Server>>()
                {
                    @Override
                    public void onResponse(Call<List<Server>> call, Response<List<Server>> response)
                    {
                        ServerList serverList = new ServerList();
                        serverList.setServers(response.body());
                        observer.setBitlet(serverList);
                        observer.setBitletExpireTime(System.currentTimeMillis() + 10 * 60 * 1000);
                        observer.finish();
                    }

                    @Override
                    public void onFailure(Call<List<Server>> call, Throwable exception)
                    {
                        observer.setException(exception);
                        observer.finish();
                    }
                });
            }
        };
    }

    private static BitletHandler<ServerList> mockedBitletInstance()
    {
        return new BitletHandler<ServerList>()
        {
            @Override
            public void load(final BitletObserver<ServerList> observer)
            {
                // Mock object
                ServerList serverList = new ServerList();
                List<Server> servers = new ArrayList<>();
                Server server = new Server();
                server.setServerId("mocked");
                server.setName("Mock server");
                server.setLocation("Home");
                server.setEnabled(true);
                servers.add(server);
                serverList.setServers(servers);

                // Inform observer
                observer.setBitlet(serverList);
                observer.setBitletExpireTime(System.currentTimeMillis() + 10 * 60 * 1000);
                observer.finish();
            }
        };
    }


    // ---
    // Generated code
    // ---

    public List<Server> getServers()
    {
        return servers;
    }

    public void setServers(List<Server> servers)
    {
        this.servers = servers;
    }

    @Override
    public String toString()
    {
        return "ServerList{" +
                "servers=" + servers +
                '}';
    }
}
