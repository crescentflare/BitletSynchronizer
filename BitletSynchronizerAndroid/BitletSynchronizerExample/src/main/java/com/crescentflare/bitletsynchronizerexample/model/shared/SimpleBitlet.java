package com.crescentflare.bitletsynchronizerexample.model.shared;

import android.os.Handler;
import android.text.TextUtils;

import com.crescentflare.bitletsynchronizer.bitlet.BitletHandler;
import com.crescentflare.bitletsynchronizer.bitlet.BitletObserver;
import com.crescentflare.bitletsynchronizerexample.HashUtil;
import com.crescentflare.bitletsynchronizerexample.Settings;
import com.crescentflare.bitletsynchronizerexample.network.Api;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import java.io.IOException;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Shared model: simple bitlet implementation
 * Provides a base class for a bitlet implementation for a simple model
 */
public class SimpleBitlet<T> implements BitletHandler<T>
{
    // ---
    // Members
    // ---

    private String path;
    private long expireTime;
    private Map<String, Object> mockedJsonMap;
    private Class<T> classObject;


    // ---
    // Initialization
    // ---

    public SimpleBitlet(String path, long expireTime, Map<String, Object> mockedJsonMap, Class<T> classObject)
    {
        this.path = path;
        this.expireTime = expireTime;
        this.mockedJsonMap = mockedJsonMap;
        this.classObject = classObject;
    }


    // ---
    // Implementation
    // ---

    public void load(final BitletObserver<T> observer)
    {
        String serverAddress = Settings.instance.getServerAddress();
        if (!TextUtils.isEmpty(serverAddress))
        {
            Api.getInstance(serverAddress).model().getModel(serverAddress + path).enqueue(new Callback<ResponseBody>()
            {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
                {
                    String stringBody = null;
                    try
                    {
                        stringBody = response.body().string();
                    }
                    catch (IOException ignored)
                    {
                    }
                    observer.setBitletExpireTime(System.currentTimeMillis() + expireTime);
                    parseAndFinish(stringBody, null, observer);
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable exception)
                {
                    observer.setException(exception);
                    observer.finish();
                }
            });
        }
        else
        {
            observer.setBitletExpireTime(System.currentTimeMillis() + expireTime);
            parseAndFinish(null, mockedJsonMap, observer);
        }
    }


    // ---
    // Asynchronous parsing
    // ---

    private void parseAndFinish(final String stringJson, final Map<String, Object> mapJson, final BitletObserver<T> observer)
    {
        final Handler handler = new Handler();
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                // Parse
                Gson gson = new GsonBuilder().create();
                T parsedBitlet = null;
                String parsedHash = null;
                if (stringJson != null)
                {
                    parsedBitlet = gson.fromJson(stringJson, classObject);
                    parsedHash = HashUtil.generateMD5(stringJson);
                }
                else if (mapJson != null)
                {
                    JsonElement jsonElement = gson.toJsonTree(mockedJsonMap);
                    parsedBitlet = gson.fromJson(jsonElement, classObject);
                    parsedHash = HashUtil.generateMD5(mapJson.toString());
                }

                // Finalize and inform observer on main thread
                final T bitlet = parsedBitlet;
                final String bitletHash = parsedHash;
                handler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (bitlet != null)
                        {
                            observer.setBitlet(bitlet);
                        }
                        if (bitletHash != null)
                        {
                            observer.setBitletHash(bitletHash);
                        }
                        observer.finish();
                    }
                });
            }
        }).start();
    }
}
