package com.crescentflare.bitletsynchronizerexample.network;

import android.text.TextUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Network service: the main interface between app and network
 */
public class Api
{
    // ---
    // Singleton instance (is recreated if the base URL changes)
    // ---

    private static String lastBaseUrl = null;
    private static Api lastInstance = null;

    public static Api getInstance(String baseUrl)
    {
        if (baseUrl.equals(lastBaseUrl))
        {
            return lastInstance;
        }
        lastInstance = new Api(baseUrl);
        lastBaseUrl = baseUrl;
        return lastInstance;
    }


    // ---
    // Members
    // ---

    private Retrofit retrofit = null;
    private SessionService sessionService = null;
    private UsageService usage = null;
    private ServersService servers = null;
    private ModelService model = null;
    private String cookie = null;


    // ---
    // Initialization
    // ---

    private Api(String baseUrl)
    {
        // Skip network setup if base URL is empty
        if (TextUtils.isEmpty(baseUrl))
        {
            return;
        }

        // Set up okhttp client with logging
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder builder = new OkHttpClient.Builder().addInterceptor(logInterceptor).addInterceptor(new ApiErrorInterceptor());
        builder.addInterceptor(getCookieAuthorizationInterceptor());
        OkHttpClient client = builder.build();

        // Create retrofit object and services
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        sessionService = retrofit.create(SessionService.class);
        usage = retrofit.create(UsageService.class);
        servers = retrofit.create(ServersService.class);
        model = retrofit.create(ModelService.class);
    }


    // ---
    // Obtain services
    // ---

    public SessionService session()
    {
        return sessionService;
    }

    public UsageService usage()
    {
        return usage;
    }

    public ServersService servers()
    {
        return servers;
    }

    public ModelService model()
    {
        return model;
    }


    // ---
    // Handle authentication cookie
    // ---

    public void setCookie(String cookie)
    {
        this.cookie = cookie;
    }

    public void clearCookie()
    {
        cookie = "";
    }

    private Interceptor getCookieAuthorizationInterceptor()
    {
        return new Interceptor()
        {
            @Override
            public Response intercept(Chain chain) throws IOException
            {
                // Fetch the original request
                Request original = chain.request();

                // Add additional headers
                Request.Builder builder = original.newBuilder()
                        .method(original.method(), original.body());
                if (!TextUtils.isEmpty(cookie))
                {
                    builder = builder.header("Cookie", "SESSION_COOKIE=" + cookie);
                }

                // Continue with response
                return chain.proceed(builder.build());
            }
        };
    }
}
