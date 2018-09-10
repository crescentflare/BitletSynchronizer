package com.crescentflare.bitletsynchronizerexample.model.session;

import android.text.TextUtils;

import com.crescentflare.bitletsynchronizer.bitlet.BitletHandler;
import com.crescentflare.bitletsynchronizer.bitlet.BitletObserver;
import com.crescentflare.bitletsynchronizerexample.Settings;
import com.crescentflare.bitletsynchronizerexample.network.Api;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Session model: main session
 * Stores the authenticated session
 */
public class Session
{
    // ---
    // Members
    // ---

    private String cookie;
    private SessionFeatures features;


    // ---
    // Bitlet instance
    // ---

    public static BitletHandler<Session> bitletInstance(final String username, final String password)
    {
        return new BitletHandler<Session>()
        {
            @Override
            public void load(final BitletObserver<Session> observer)
            {
                if (!TextUtils.isEmpty(Settings.instance.getServerAddress()))
                {
                    Api.getInstance(Settings.instance.getServerAddress()).session().createSession(username, password).enqueue(new Callback<Session>()
                    {
                        @Override
                        public void onResponse(Call<Session> call, Response<Session> response)
                        {
                            observer.setBitlet(response.body());
                            observer.setBitletExpireTime(-1);
                            observer.finish();
                        }

                        @Override
                        public void onFailure(Call<Session> call, Throwable exception)
                        {
                            observer.setException(exception);
                            observer.finish();
                        }
                    });
                }
                else
                {
                    // Mock object
                    Session session = new Session();
                    SessionFeatures features = new SessionFeatures();
                    features.setUsage(SessionPermission.View);
                    features.setServers(SessionPermission.View);
                    session.setCookie("mocked");
                    session.setFeatures(features);

                    // Inform observer
                    observer.setBitlet(session);
                    observer.setBitletExpireTime(-1);
                    observer.finish();
                }
            }
        };
    }


    // ---
    // Generated code
    // ---

    public String getCookie()
    {
        return cookie;
    }

    public void setCookie(String cookie)
    {
        this.cookie = cookie;
    }

    public SessionFeatures getFeatures()
    {
        return features;
    }

    public void setFeatures(SessionFeatures features)
    {
        this.features = features;
    }

    @Override
    public String toString()
    {
        return "Session{" +
                "cookie='" + cookie + '\'' +
                ", features=" + features +
                '}';
    }
}
