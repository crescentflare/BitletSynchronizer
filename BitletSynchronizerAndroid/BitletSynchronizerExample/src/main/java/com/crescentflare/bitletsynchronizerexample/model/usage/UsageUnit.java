package com.crescentflare.bitletsynchronizerexample.model.usage;

import com.google.gson.annotations.SerializedName;

/**
 * Usage enum: item unit
 * Indicates the type of value of a usage item
 */
public enum UsageUnit
{
    @SerializedName("none")
    None,

    @SerializedName("MB")
    MB,

    @SerializedName("GB")
    GB,

    @SerializedName("percent")
    Percent
}
