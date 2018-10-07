package com.crescentflare.bitletsynchronizerexample.model.servers;

import com.crescentflare.bitletsynchronizerexample.MapUtil;
import com.crescentflare.bitletsynchronizerexample.model.shared.SimpleArrayBitlet;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Server model: simple server list
 * Stores an overview of all servers without details
 */
public class ServerList implements SimpleArrayBitlet.ArrayModel<Server>
{
    // ---
    // Members
    // ---

    private List<Server> items;


    // ---
    // Bitlet instance
    // ---

    public static String cacheKey()
    {
        return "/servers";
    }

    public static SimpleArrayBitlet<ServerList, Server> bitletInstance()
    {
        List<Map<String, Object>> mockedData = Collections.singletonList(MapUtil.newMap(
                "id", "mocked",
                "name", "Mock server",
                "location", "Home",
                "enabled", true
        ));
        return new SimpleArrayBitlet<>("/servers", 10 * 60 * 1000, mockedData, ServerList.class, new TypeToken<List<Server>>()
        {
        }.getType());
    }


    // ---
    // Generated code
    // ---

    public List<Server> getItemList()
    {
        return items;
    }

    public void setItemList(List<Server> items)
    {
        this.items = items;
    }

    @Override
    public String toString()
    {
        return "ServerList{" +
                "items=" + items +
                '}';
    }
}
