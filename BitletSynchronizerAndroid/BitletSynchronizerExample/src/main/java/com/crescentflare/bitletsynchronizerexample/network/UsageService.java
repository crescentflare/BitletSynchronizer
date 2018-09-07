package com.crescentflare.bitletsynchronizerexample.network;

import com.crescentflare.bitletsynchronizerexample.model.usage.Usage;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Network service: obtain usage
 */
public interface UsageService
{
    @GET("usage")
    Call<Usage> getUsage();
}
