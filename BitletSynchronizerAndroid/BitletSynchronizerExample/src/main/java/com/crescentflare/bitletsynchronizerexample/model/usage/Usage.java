package com.crescentflare.bitletsynchronizerexample.model.usage;

import com.crescentflare.bitletsynchronizerexample.MapUtil;
import com.crescentflare.bitletsynchronizerexample.model.shared.SimpleBitlet;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.Map;

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

    public static String cacheKey()
    {
        return "/usage";
    }

    public static SimpleBitlet<Usage> bitletInstance()
    {
        Map<String, Object> mockedData = MapUtil.newMap(
                "data_traffic", MapUtil.newMap(
                        "amount", 2.0,
                        "unit", "GB",
                        "label", "2.0 GB"
                ),
                "server_load", MapUtil.newMap(
                        "amount", 10,
                        "unit", "percent",
                        "label", "10%"
                ),
                "last_update", "2001-01-01T00:00:00.000Z"
        );
        return new SimpleBitlet<>("/usage", 30 * 1000, mockedData, Usage.class);
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
