package com.huanshare.tools.mvc.utils;

import com.huanshare.tools.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 *  2017/5/17.
 */
public class MapUtils extends org.apache.commons.collections.MapUtils {

    public static Object getValue(Map valuesMap, String key) {
        if (isEmpty(valuesMap) || StringUtils.isBlank(key)) {
            return null;
        }

        Map map = valuesMap;
        Object value = null;
        String[] strings = StringUtils.split(key, ".");
        for (int i = 0; i < strings.length; i++) {
            value = map.get(strings[i]);
            if (value instanceof Map) {
                map = (Map) value;
            }
        }

        return value;
    }


    public static void main(String[] args) {
        Map map = new HashMap();
        map.put("username", "hu");
        Map map1 = new HashMap();
        map1.put("age", "18");
        Map map2 = new HashMap();
        map2.put("sex", "1");
        map1.put("map2", map2);
        map.put("map1", map1);

        System.out.println(MapUtils.getValue(map, "map1.map2.sex"));
    }
}
