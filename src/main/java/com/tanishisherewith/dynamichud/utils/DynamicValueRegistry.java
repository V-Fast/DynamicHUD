package com.tanishisherewith.dynamichud.utils;

import com.tanishisherewith.dynamichud.internal.System;

import java.util.*;
import java.util.function.Supplier;

/**
 * A type-safe registry for managing dynamic values for widgets.
 * Supports both global and local registries with unique identifiers.
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
public class DynamicValueRegistry {
    private static final Map<String, DynamicValueRegistry> REGISTRY_BY_ID = new HashMap<>();
    private static final Map<String, Supplier<?>> GLOBAL_REGISTRY = new HashMap<>();

    private final String id; // Unique identifier for this registry instance
    private final Map<String, Supplier<?>> localRegistry = new HashMap<>();

    /**
     * Constructor for a local registry with a unique ID.
     * @param modId The mod ID or unique identifier for grouping registries.
     * @param registryId A unique ID for this registry instance.
     */
    public DynamicValueRegistry(String modId, String registryId) {
        this.id = registryId;
        System.registerInstance(this, modId);
        REGISTRY_BY_ID.put(registryId, this);
    }
    /**
     * Constructor for a local registry using with registryId as modID.
     * @param modId The mod ID or unique identifier for grouping registries.
     */
    public DynamicValueRegistry(String modId) {
        this.id = modId;
        System.registerInstance(this, modId);
        REGISTRY_BY_ID.put(modId, this);
    }

    /**
     * Registers a supplier in the global registry.
     * @param key The key for the supplier.
     * @param supplier The supplier providing values of type T.
     */
    public static <T> void registerGlobal(String key, Supplier<T> supplier) {
        GLOBAL_REGISTRY.put(key, supplier);
    }

    /**
     * Retrieves a supplier from the global registry.
     * @param key The key of the supplier.
     * @return The supplier, or null if not found.
     */
    public static Supplier<?> getGlobal(String key) {
        return GLOBAL_REGISTRY.get(key);
    }

    /**
     * Registers a supplier in the local registry.
     * @param key The key for the supplier.
     * @param supplier The supplier providing values of type T.
     */
    public void registerLocal(String key, Supplier<?> supplier) {
        localRegistry.put(key, supplier);
    }

    /**
     * Retrieves a supplier from the local or global registry.
     * @param key The key of the supplier.
     * @return The supplier, or null if not found.
     */
    public Supplier<?> get(String key) {
        return localRegistry.getOrDefault(key, null);
    }

    /**
     * Gets the registry instance by its unique ID.
     * @param registryId The unique ID of the registry.
     * @return The registry instance, or null if not found.
     */
    public static  DynamicValueRegistry getById(String registryId) {
        return REGISTRY_BY_ID.get(registryId);
    }

    /**
     * Retrieves all registry instances for a mod ID.
     * @param modId The mod ID.
     * @return A list of registries for the mod.
     */
    public static List<DynamicValueRegistry> getInstances(String modId) {
        return System.getInstances(DynamicValueRegistry.class, modId);
    }

    /**
     * Removes a supplier from the global registry.
     * @param key The key of the supplier.
     */
    public static void removeGlobal(String key) {
        GLOBAL_REGISTRY.remove(key);
    }

    /**
     * Removes a supplier from the local registry.
     * @param key The key of the supplier.
     */
    public void removeLocal(String key) {
        localRegistry.remove(key);
    }

    /**
     * Gets the unique ID of this registry.
     * @return The registry ID.
     */
    public String getId() {
        return id;
    }
}