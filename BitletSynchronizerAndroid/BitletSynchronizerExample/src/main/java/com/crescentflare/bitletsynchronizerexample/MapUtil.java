package com.crescentflare.bitletsynchronizerexample;

import java.util.HashMap;
import java.util.Map;

/**
 * An easy way to generate a map
 */
public class MapUtil
{
    public static Map<String, Object> newMap(Object... items)
    {
        Map<String, Object> map = new HashMap<>();
        if (items.length % 2 == 1)
        {
            return null;
        }
        for (int i = 0; i < items.length; i += 2)
        {
            Object key = items[i];
            Object value = items[i + 1];
            if (!(key instanceof String))
            {
                return null;
            }
            map.put((String)key, value);
        }
        return map;
    }
}
