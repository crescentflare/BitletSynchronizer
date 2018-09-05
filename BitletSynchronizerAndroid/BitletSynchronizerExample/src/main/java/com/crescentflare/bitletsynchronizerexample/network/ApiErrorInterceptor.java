package com.crescentflare.bitletsynchronizerexample.network;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Network interceptor: checks for invalid responses and throws a generic exception
 */
public class ApiErrorInterceptor implements Interceptor
{
    @Override
    public Response intercept(Chain chain) throws IOException
    {
        Response response = chain.proceed(chain.request());
        if (response.code() < 200 || response.code() >= 400)
        {
            throw new IOException("No valid response");
        }
        return response;
    }
}
