package com.crescentflare.bitletsynchronizerexample.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Network service: obtain generic models
 */
public interface ModelService
{
    @GET
    Call<ResponseBody> getModel(@Url String url);
}
