package com.tanishisherewith.dynamichud.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
    private static final Map<String, Supplier<?>> globalRegistry = new HashMap<>();

    /**
     * A map that holds the local registry of suppliers.
     *
     * @see #globalRegistry
     */
    private final Map<String, Supplier<?>> localRegistry = new HashMap<>();

    /**
     * Constructor for the DynamicValueRegistry class.
     *
     * @param modId The ID of the mod for which this registry is being created. Doesn't need to be modId, it can simply be used as a standard unique identifier string.
     */
    public DynamicValueRegistry(String modId) {
        super(modId);
        instances.computeIfAbsent(modId, k -> new ArrayList<>()).add(this);
    }

    /**
     * Registers a supplier in the global registry.
     *
     * @param key      The key under which the supplier is to be registered.
     * @param supplier The supplier to be registered.
     */
    public static void registerGlobal(String key, Supplier<?> supplier) {
        globalRegistry.put(key, supplier);
    }

    /**
     * Retrieves a supplier from the global registry.
     *
     * @param key The key of the supplier to be retrieved.
     * @return The supplier registered under the given key, or null if no such supplier exists.
     */
    public static Supplier<?> getGlobal(String key) {
        return globalRegistry.get(key);
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
        // First, try to get the supplier from the local registry
        Supplier<?> supplier = localRegistry.get(key);

        // If the supplier is not in the local registry, try the global registry
        if (supplier == null) {
            supplier = globalRegistry.get(key);
        }

        return supplier;
    }

    /**
     * Sets the local registry to the given map.
     *
     * @param map The map to be set as the local registry.
     */
    public void setLocalRegistry(Map<String, Supplier<?>> map) {
        localRegistry.putAll(map);
    }
}
