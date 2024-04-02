package com.tanishisherewith.dynamichud.newTrial.utils;

import java.util.*;
import java.util.function.Supplier;

public class DynamicValueRegistry {
    private static final Map<String, Supplier<?>> globalRegistry = new HashMap<>();
    private final Map<String, Supplier<?>> localRegistry = new HashMap<>();

    public static void registerGlobal(String key, Supplier<?> supplier) {
        globalRegistry.put(key, supplier);
    }

    public void registerLocal(String key, Supplier<?> supplier) {
        localRegistry.put(key, supplier);
    }
    public static Supplier<?> getGlobal(String key) {
        return globalRegistry.get(key);
    }

    public Supplier<?> get(String key) {
        // First, try to get the supplier from the local registry
        Supplier<?> supplier = localRegistry.get(key);

        // If the supplier is not in the local registry, try the global registry
        if (supplier == null) {
            supplier = globalRegistry.get(key);
        }

        return supplier;
    }
}
