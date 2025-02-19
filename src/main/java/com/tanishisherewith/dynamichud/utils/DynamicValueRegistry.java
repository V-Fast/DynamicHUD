package com.tanishisherewith.dynamichud.utils;

import com.tanishisherewith.dynamichud.internal.System;

import java.util.*;
import java.util.function.Supplier;

/**
 * This class is responsible for managing dynamic values for widgets.
 * It maintains a global and local registry of suppliers for these dynamic values.
 * <p>
 * To use a local registry, simple create an object of the class.
 * <pre>
 *     {@code
 *     DynamicValueRegistry dvr = new DynamicValueRegistry("mod_id");
 *     dvr.registerLocal("ABC",//YourSupplier);
 *     Supplier<?> result = dvr.get("ABC");
 *     }
 * </pre>
 * </p>
 */
public class DynamicValueRegistry extends System {
    /**
     * A map that holds the global registry of suppliers.
     *
     * @see #localRegistry
     */
    private static final Map<String, Supplier<?>> GLOBAL_REGISTRY = new HashMap<>();

    /**
     * A map that holds the local registry of suppliers.
     *
     * @see #GLOBAL_REGISTRY
     */
    private final Map<String, Supplier<?>> localRegistry = new HashMap<>();

    /**
     * Constructor for the DynamicValueRegistry class.
     *
     * @param modId The ID of the mod for which this registry is being created. Doesn't need to be modId, it can simply be used as a standard unique identifier string.
     */
    public DynamicValueRegistry(String modId) {
        super(modId);
    }

    /**
     * Registers a supplier in the global registry.
     *
     * @param key      The key under which the supplier is to be registered.
     * @param supplier The supplier to be registered.
     */
    public static void registerGlobal(String key, Supplier<?> supplier) {
        GLOBAL_REGISTRY.put(key, supplier);
    }

    /**
     * Retrieves a supplier from the global registry.
     *
     * @param key The key of the supplier to be retrieved.
     * @return The supplier registered under the given key, or null if no such supplier exists.
     */
    public static Supplier<?> getGlobal(String key) {
        return GLOBAL_REGISTRY.get(key);
    }

    /**
     * Registers a supplier in the local registry.
     *
     * @param key      The key under which the supplier is to be registered.
     * @param supplier The supplier to be registered.
     */
    public void registerLocal(String key, Supplier<?> supplier) {
        localRegistry.put(key, supplier);
    }

    /**
     * Retrieves a supplier from the local registry, falling back to the global registry if necessary.
     *
     * @param key The key of the supplier to be retrieved.
     * @return The supplier registered under the given key, or null if no such supplier exists.
     */
    public Supplier<?> get(String key) {
        return localRegistry.getOrDefault(key, GLOBAL_REGISTRY.get(key));
    }

    /**
     * Sets the local registry to the given map.
     *
     * @param map The map to be set as the local registry.
     */
    public void setLocalRegistry(Map<String, Supplier<?>> map) {
        localRegistry.clear();
        localRegistry.putAll(map);
    }
    /**
     * Retrieves all instances of DynamicValueRegistry for a specific mod ID.
     *
     * @param modId The mod ID to search for.
     * @return A list of DynamicValueRegistry instances, or an empty list if none exist.
     */
    public static List<DynamicValueRegistry> getInstances(String modId) {
        return instances.getOrDefault(modId, Collections.emptyList());
    }

    /**
     * Removes a supplier from the global registry.
     *
     * @param key The key of the supplier to remove.
     */
    public static void removeGlobal(String key) {
        GLOBAL_REGISTRY.remove(key);
    }

    /**
     * Removes a supplier from the local registry.
     *
     * @param key The key of the supplier to remove.
     */
    public void removeLocal(String key) {
        localRegistry.remove(key);
    }
}
