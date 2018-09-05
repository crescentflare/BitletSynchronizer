package com.crescentflare.bitletsynchronizerexample.model.usage;

import android.text.TextUtils;

import com.crescentflare.bitletsynchronizer.bitlet.BitletHandler;
import com.crescentflare.bitletsynchronizer.bitlet.BitletObserver;
import com.crescentflare.bitletsynchronizerexample.Settings;
import com.crescentflare.bitletsynchronizerexample.network.Api;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Usage model: usage overview
 * Stores the main usage overview data
 */
public class Usage
{
    // ---
    // Members
    // ---

    @SerializedName("last_update")
    private Date lastUpdate;

    @SerializedName("data_traffic")
    private UsageItem dataTraffic;

    @SerializedName("server_load")
    private UsageItem serverLoad;


    // ---
    // Bitlet instance
    // ---

    public static BitletHandler<Usage> bitletInstance()
    {
        return new BitletHandler<Usage>()
        {
            @Override
            public void load(final BitletObserver<Usage> observer)
            {
                if (!TextUtils.isEmpty(Settings.instance.getServerAddress()))
                {
                    Api.getInstance(Settings.instance.getServerAddress()).usage().getUsage().enqueue(new Callback<Usage>()
                    {
                        @Override
                        public void onResponse(Call<Usage> call, Response<Usage> response)
                        {
                            observer.setBitlet(response.body());
                            observer.finish();
                        }

                        @Override
                        public void onFailure(Call<Usage> call, Throwable exception)
                        {
                            observer.setException(exception);
                            observer.finish();
                        }
                    });
                }
                else
                {
                    // Mock object
                    Usage usage = new Usage();
                    UsageItem dataTraffic = new UsageItem();
                    UsageItem serverLoad = new UsageItem();
                    dataTraffic.setAmount(2.0f);
                    dataTraffic.setUnit(UsageUnit.GB);
                    dataTraffic.setLabel("2.0 GB");
                    serverLoad.setAmount(10.0f);
                    serverLoad.setUnit(UsageUnit.Percent);
                    serverLoad.setLabel("10%");
                    usage.setLastUpdate(new Date());
                    usage.setDataTraffic(dataTraffic);
                    usage.setServerLoad(serverLoad);

                    // Inform observer
                    observer.setBitlet(usage);
                    observer.finish();
                }
            }
        };
    }


    // ---
    // Generated code
    // ---

    public Date getLastUpdate()
    {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate)
    {
        this.lastUpdate = lastUpdate;
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

    @Override
    public String toString()
    {
        return "Usage{" +
                "lastUpdate=" + lastUpdate +
                ", dataTraffic=" + dataTraffic +
                ", serverLoad=" + serverLoad +
                '}';
    }
}
