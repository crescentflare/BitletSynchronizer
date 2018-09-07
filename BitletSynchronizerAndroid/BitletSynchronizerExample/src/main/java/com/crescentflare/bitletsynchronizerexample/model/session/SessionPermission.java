package com.crescentflare.bitletsynchronizerexample.model.session;

import com.google.gson.annotations.SerializedName;

/**
 * Session enum: feature permission
 * Indicates what permission the session has on a feature
 */
public enum SessionPermission
{
    @SerializedName("none")
    None,

    @SerializedName("view")
    View,

    @SerializedName("change")
    Change
}
