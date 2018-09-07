package com.crescentflare.bitletsynchronizerexample.network;

import com.crescentflare.bitletsynchronizerexample.model.servers.Server;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Network service: obtain server list and details
 */
public interface ServersService
{
    @GET("servers")
    Call<List<Server>> getServerList();

    @GET("servers/{serverId}")
    Call<Server> getServerDetails(@Path("serverId") String serverId);
}
