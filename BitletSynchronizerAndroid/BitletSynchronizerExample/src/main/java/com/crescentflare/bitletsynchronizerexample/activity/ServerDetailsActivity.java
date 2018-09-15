package com.crescentflare.bitletsynchronizerexample.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.crescentflare.bitletsynchronizer.bitlet.BitletResultObserver;
import com.crescentflare.bitletsynchronizer.cache.BitletCacheEntry;
import com.crescentflare.bitletsynchronizer.synchronizer.BitletSynchronizer;
import com.crescentflare.bitletsynchronizerexample.R;
import com.crescentflare.bitletsynchronizerexample.model.servers.Server;
import com.crescentflare.bitletsynchronizerexample.view.ListItem;
import com.crescentflare.bitletsynchronizerexample.view.NotifyingScrollView;

/**
 * The server details activity shows a demo of an detail page of a server with information
 */
public class ServerDetailsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener
{
    // ---
    // Constants
    // ---

    private static final String ARG_SERVER_ID = "arg_server_id";


    // ---
    // Members
    // ---

    private SwipeRefreshLayout refresher = null;
    private String serverId = "undefined";


    // ---
    // Initialization
    // ---

    static public Intent newInstance(Context context, String serverId)
    {
        Intent intent = new Intent(context, ServerDetailsActivity.class);
        intent.putExtra(ARG_SERVER_ID, serverId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // Set up action bar
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_details);
        setTitle(getString(R.string.server_details_title));
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        // Obtain server ID to load from arguments
        serverId = getIntent().getStringExtra(ARG_SERVER_ID);
        if (serverId == null)
        {
            serverId = "undefined";
        }

        // Set up pull to refresh
        refresher = (SwipeRefreshLayout)findViewById(R.id.activity_server_details_refresher);
        refresher.setEnabled(true);
        refresher.setOnRefreshListener(this);
        ((NotifyingScrollView)findViewById(R.id.activity_server_details_scroller)).setScrollPositionChangedListener(new NotifyingScrollView.ScrollPositionChangedListener()
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
        refreshView();
    }


    // ---
    // Data loading
    // ---

    private Server cachedServer()
    {
        Object cachedObject = BitletSynchronizer.instance.getCachedBitlet(Server.cacheKey(serverId));
        if (cachedObject instanceof Server)
        {
            return (Server)cachedObject;
        }
        return null;
    }

    @Override
    public void onRefresh()
    {
        loadData(true);
        if (BitletSynchronizer.instance.getCacheState(Server.cacheKey(serverId)) == BitletCacheEntry.State.Loading)
        {
            refreshView();
        }
    }

    private void loadData(final boolean forced)
    {
        BitletSynchronizer.instance.load(Server.bitletInstance(serverId), Server.cacheKey(serverId), forced, new BitletResultObserver.SimpleCompletionListener<Server>()
        {
            @Override
            public void onFinish(Server server, Throwable exception)
            {
                if (!isFinishing())
                {
                    if (exception != null)
                    {
                        Toast.makeText(ServerDetailsActivity.this, getString(R.string.error_generic_title) + ":\n" + exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                    refreshView();
                    if (forced)
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

    public void refreshView()
    {
        Server cachedServer = cachedServer();
        BitletCacheEntry.State cacheState = BitletSynchronizer.instance.getCacheState(Server.cacheKey(serverId));
        findViewById(R.id.activity_server_details_loading).setVisibility(cachedServer == null && cacheState == BitletCacheEntry.State.Loading ? View.VISIBLE : View.GONE);
        findViewById(R.id.activity_server_details_error).setVisibility(cachedServer == null && cacheState != BitletCacheEntry.State.Loading ? View.VISIBLE : View.GONE);
        findViewById(R.id.activity_server_details_list_container).setVisibility(cachedServer != null ? View.VISIBLE : View.GONE);
        if (cachedServer != null)
        {
            ((ListItem)findViewById(R.id.activity_server_details_name)).setValue(cachedServer.getName());
            ((ListItem)findViewById(R.id.activity_server_details_description)).setValue(cachedServer.getDescription());
            ((ListItem)findViewById(R.id.activity_server_details_operating_system)).setValue(cachedServer.getOs() + " " + cachedServer.getOsVersion());
            ((ListItem)findViewById(R.id.activity_server_details_location)).setValue(cachedServer.getLocation());
            ((ListItem)findViewById(R.id.activity_server_details_data_traffic)).setValue(cachedServer.getDataTraffic().getLabel());
            ((ListItem)findViewById(R.id.activity_server_details_server_load)).setValue(cachedServer.getServerLoad().getLabel());
            ((ListItem)findViewById(R.id.activity_server_details_enabled)).setValue(getString(cachedServer.isEnabled() ? R.string.server_details_enabled_on : R.string.server_details_enabled_off));
        }
    }
}
