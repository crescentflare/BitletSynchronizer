package com.crescentflare.bitletsynchronizerexample.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.crescentflare.bitletsynchronizer.bitlet.BitletResultObserver;
import com.crescentflare.bitletsynchronizer.cache.BitletCacheEntry;
import com.crescentflare.bitletsynchronizer.synchronizer.BitletSynchronizer;
import com.crescentflare.bitletsynchronizerexample.R;
import com.crescentflare.bitletsynchronizerexample.Settings;
import com.crescentflare.bitletsynchronizerexample.model.servers.Server;
import com.crescentflare.bitletsynchronizerexample.model.servers.ServerList;
import com.crescentflare.bitletsynchronizerexample.model.session.Session;
import com.crescentflare.bitletsynchronizerexample.model.usage.Usage;
import com.crescentflare.bitletsynchronizerexample.network.Api;
import com.crescentflare.bitletsynchronizerexample.view.ListItem;
import com.crescentflare.bitletsynchronizerexample.view.NotifyingScrollView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * The overview activity shows a demo of an account usage and server list
 */
public class OverviewActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener
{
    // ---
    // Members
    // ---

    private SwipeRefreshLayout refresher = null;
    private int refreshCallsBusy = 0;


    // ---
    // Initialization
    // ---

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // Set up action bar
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        setTitle(getString(R.string.overview_title));
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        // Set up pull to refresh
        refresher = (SwipeRefreshLayout)findViewById(R.id.activity_overview_refresher);
        refresher.setEnabled(true);
        refresher.setOnRefreshListener(this);
        ((NotifyingScrollView)findViewById(R.id.activity_overview_scroller)).setScrollPositionChangedListener(new NotifyingScrollView.ScrollPositionChangedListener()
        {
            @Override
            public void onScrollPositionChanged(int x, int y)
            {
                refresher.setEnabled(y == 0);
            }
        });
    }


    // ---
    // State handling
    // ---

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        loadData(false);
        refreshUsage();
        refreshServerList();
    }

    @Override
    public void finish()
    {
        super.finish();
        if (!TextUtils.isEmpty(Settings.instance.getServerAddress()))
        {
            Api apiInstance = Api.getInstance(Settings.instance.getServerAddress());
            apiInstance.session().endSession(Settings.instance.getSessionCookie()).enqueue(new Callback<Session>()
            {
                @Override
                public void onResponse(Call<Session> call, Response<Session> response)
                {
                    // No implementation
                }

                @Override
                public void onFailure(Call<Session> call, Throwable t)
                {
                    // No implementation
                }
            });
            apiInstance.clearCookie();
        }
        BitletSynchronizer.instance.clearCache();
        Settings.instance.setSessionCookie("");
    }


    // ---
    // Data loading
    // ---

    @Override
    public void onRefresh()
    {
        loadData(true);
        if (BitletSynchronizer.instance.getCacheState(Usage.cacheKey()) == BitletCacheEntry.State.Loading)
        {
            refreshUsage();
        }
        if (BitletSynchronizer.instance.getCacheState(ServerList.cacheKey()) == BitletCacheEntry.State.Loading)
        {
            refreshServerList();
        }
    }

    private void loadData(final boolean forced)
    {
        // Load usage
        refreshCallsBusy = 2;
        BitletSynchronizer.instance.load(Usage.bitletInstance(), Usage.cacheKey(), forced, new BitletResultObserver.SimpleCompletionListener<Usage>()
        {
            @Override
            public void onFinish(Usage usage, Throwable exception)
            {
                if (!isFinishing())
                {
                    if (exception != null)
                    {
                        Toast.makeText(OverviewActivity.this, getString(R.string.error_generic_title) + ":\n" + exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                    refreshUsage();
                    refreshCallsBusy--;
                    if (refreshCallsBusy <= 0 && forced)
                    {
                        refresher.setRefreshing(false);
                    }
                }
            }
        });

        // Load server list
        BitletSynchronizer.instance.load(ServerList.bitletInstance(), ServerList.cacheKey(), forced, new BitletResultObserver.SimpleCompletionListener<ServerList>()
        {
            @Override
            public void onFinish(ServerList serverList, Throwable exception)
            {
                if (!isFinishing())
                {
                    if (exception != null)
                    {
                        Toast.makeText(OverviewActivity.this, getString(R.string.error_generic_title) + ":\n" + exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                    refreshServerList();
                    refreshCallsBusy--;
                    if (refreshCallsBusy <= 0 && forced)
                    {
                        refresher.setRefreshing(false);
                    }
                }
            }
        });
    }


    // ---
    // Menu handling
    // ---

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // ---
    // Data handling
    // ---

    public void refreshUsage()
    {
        Usage cachedUsage = BitletSynchronizer.instance.getCachedBitlet(Usage.cacheKey(), Usage.class);
        BitletCacheEntry.State cacheState = BitletSynchronizer.instance.getCacheState(Usage.cacheKey());
        findViewById(R.id.activity_overview_usage_loading).setVisibility(cachedUsage == null && cacheState == BitletCacheEntry.State.Loading ? View.VISIBLE : View.GONE);
        findViewById(R.id.activity_overview_usage_error).setVisibility(cachedUsage == null && cacheState != BitletCacheEntry.State.Loading ? View.VISIBLE : View.GONE);
        findViewById(R.id.activity_overview_usage_list_container).setVisibility(cachedUsage != null ? View.VISIBLE : View.GONE);
        if (cachedUsage != null)
        {
            ((ListItem)findViewById(R.id.activity_overview_usage_data_traffic)).setValue(cachedUsage.getDataTraffic().getLabel());
            ((ListItem)findViewById(R.id.activity_overview_usage_server_load)).setValue(cachedUsage.getServerLoad().getLabel());
        }
    }

    public void refreshServerList()
    {
        ServerList cachedServerList = BitletSynchronizer.instance.getCachedBitlet(ServerList.cacheKey(), ServerList.class);
        BitletCacheEntry.State cacheState = BitletSynchronizer.instance.getCacheState(ServerList.cacheKey());
        findViewById(R.id.activity_overview_servers_loading).setVisibility(cachedServerList == null && cacheState == BitletCacheEntry.State.Loading ? View.VISIBLE : View.GONE);
        findViewById(R.id.activity_overview_servers_error).setVisibility(cachedServerList == null && cacheState != BitletCacheEntry.State.Loading ? View.VISIBLE : View.GONE);
        findViewById(R.id.activity_overview_servers_list_container).setVisibility(cachedServerList != null ? View.VISIBLE : View.GONE);
        if (cachedServerList != null)
        {
            LinearLayout listContainer = (LinearLayout)findViewById(R.id.activity_overview_servers_list_container);
            int horizontalPadding = getResources().getDimensionPixelSize(R.dimen.activity_margin);
            int verticalPadding = getResources().getDimensionPixelSize(R.dimen.item_vertical_padding);
            int minHeight = getResources().getDimensionPixelOffset(R.dimen.item_min_height);
            boolean firstItem = true;
            listContainer.removeAllViews();
            for (Server server : cachedServerList.getServers())
            {
                // Add dividers in between item views
                if (!firstItem)
                {
                    View divider = new View(this);
                    divider.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                    divider.setBackgroundResource(R.color.divider);
                    listContainer.addView(divider);
                }

                // Add an item view with the server
                ListItem listItem = new ListItem(this);
                listItem.setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding);
                listItem.setMinimumHeight(minHeight);
                listItem.setLabel(server.getName());
                listItem.setAdditional(server.getLocation());
                listItem.setValue(getString(server.isEnabled() ? R.string.overview_servers_enabled : R.string.overview_servers_disabled));
                listContainer.addView(listItem);
                firstItem = false;

                // Add a click listener for showing the server details
                final String serverId = server.getServerId();
                listItem.setBackgroundResource(R.drawable.background_selectable_transparent);
                listItem.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        startActivity(ServerDetailsActivity.newInstance(OverviewActivity.this, serverId));
                    }
                });
            }
        }
    }
}
