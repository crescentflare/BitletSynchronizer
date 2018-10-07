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
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Shared model: simple array bitlet implementation
 * Provides a base class for a bitlet implementation for a simple model containing an array
 */
public class SimpleArrayBitlet<T extends SimpleArrayBitlet.ArrayModel<E>, E> implements BitletHandler<T>
{
    // ---
    // Members
    // ---

    private String path;
    private long expireTime;
    private List<Map<String, Object>> mockedJsonMapList;
    private Class<T> classObject;
    private Type itemTypeObject;


    // ---
    // Initialization
    // ---

    public SimpleArrayBitlet(String path, long expireTime, List<Map<String, Object>> mockedJsonMapList, Class<T> classObject, Type itemTypeObject)
    {
        this.path = path;
        this.expireTime = expireTime;
        this.mockedJsonMapList = mockedJsonMapList;
        this.classObject = classObject;
        this.itemTypeObject = itemTypeObject;
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
            parseAndFinish(null, mockedJsonMapList, observer);
        }
    }


    // ---
    // Asynchronous parsing
    // ---

    private void parseAndFinish(final String stringJson, final List<Map<String, Object>> listMapJson, final BitletObserver<T> observer)
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
                    List<E> parsedArray = new Gson().fromJson(stringJson, itemTypeObject);
                    try
                    {
                        parsedBitlet = classObject.newInstance();
                        parsedBitlet.setItemList(parsedArray);
                        parsedHash = HashUtil.generateMD5(stringJson);
                    }
                    catch (Exception ignored)
                    {
                    }
                }
                else if (listMapJson != null)
                {
                    JsonElement jsonElement = gson.toJsonTree(mockedJsonMapList);
                    List<E> parsedArray = new Gson().fromJson(jsonElement, itemTypeObject);
                    try
                    {
                        parsedBitlet = classObject.newInstance();
                        parsedBitlet.setItemList(parsedArray);
                        parsedHash = HashUtil.generateMD5(listMapJson.toString());
                    }
                    catch (Exception ignored)
                    {
                    }
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


    // ---
    // Interface for models wrapping an array
    // ---

    public interface ArrayModel<T>
    {
        List<T> getItemList();
        void setItemList(List<T> items);
    }
}
