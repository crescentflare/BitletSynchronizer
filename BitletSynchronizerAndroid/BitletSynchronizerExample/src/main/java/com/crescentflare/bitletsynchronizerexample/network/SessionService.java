package com.crescentflare.bitletsynchronizerexample.network;

import com.crescentflare.bitletsynchronizerexample.model.session.Session;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Network service: starts/delete session (logging in and out)
 */
public interface SessionService
{
    // ---
    // Log in
    // ---

    @POST("sessions")
    @FormUrlEncoded
    Call<Session> createSession(@Field("user") String username, @Field("password") String password);


    // ---
    // Log out
    // ---

    @DELETE("sessions/{sessionId}")
    Call<Session> endSession(@Path("sessionId") String sessionId);
}
