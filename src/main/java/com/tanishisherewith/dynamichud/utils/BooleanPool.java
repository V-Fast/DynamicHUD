package com.tanishisherewith.dynamichud.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BooleanPool {
    private static final Map<String, Boolean> pool = new ConcurrentHashMap<>();

    public static void put(String key, boolean value) {
        pool.put(key, value);
    }
    public static void remove(String key) {
        pool.remove(key);
    }

    public static boolean get(String key) {
        return pool.getOrDefault(key, false);
    }
}