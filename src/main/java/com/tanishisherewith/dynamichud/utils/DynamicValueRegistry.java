package com.tanishisherewith.dynamichud.utils;

import com.tanishisherewith.dynamichud.internal.System;
import net.minecraft.resources.Identifier;

import java.util.*;
import java.util.function.Supplier;

/**
 * A flat, static registry for dynamic widget values keyed by {@link net.minecraft.resources.Identifier}.
 * <p>
 * The namespace acts as the mod/group ID, the path is the value key
 * <pre>
 *     DynamicValueRegistry.register(Identifier.of("mymod", "health"), player::getHealth);
 *     Supplier&lt;?&gt; health = DynamicValueRegistry.get(Identifier.of("mymod", "health"));
 * </pre>
 */
public class DynamicValueRegistry {
    private static final Map<Identifier, Supplier<?>> REGISTRY = new HashMap<>();

    private DynamicValueRegistry() {}

    /**
     * Registers a supplier under the given identifier.
     *
     * @throws IllegalArgumentException if id or supplier is null, or if id is already registered
     */
    public static <T> void register(Identifier id, Supplier<T> supplier) {
        if (id == null) throw new IllegalArgumentException("Identifier cannot be null");
        if (supplier == null) throw new IllegalArgumentException("Supplier cannot be null");
        if (REGISTRY.containsKey(id)) {
            throw new IllegalStateException("Duplicate registration for " + id);
        }
        REGISTRY.put(id, supplier);
    }

    public static <T> void register(String namespace, String path, Supplier<T> supplier) {
        register(Identifier.fromNamespaceAndPath(namespace, path), supplier);
    }

    /**
     * Retrieves the raw supplier.
     *
     * @return the supplier, or null if not found
     */
    @SuppressWarnings("unchecked")
    public static <T> Supplier<T> get(Identifier id) {
        if (id == null) return null;
        return (Supplier<T>) REGISTRY.get(id);
    }

    /**
     * Retrieves the current value directly.
     *
     * @return the value, or null if not found
     */
    public static <T> T getValue(Identifier id) {
        Supplier<T> supplier = get(id);
        return supplier != null ? supplier.get() : null;
    }

    /**
     * Retrieves the current value directly.
     *
     * @return the value, or null if not found
     */
    public static <T> T getValue(String namespace, String path) {
        Supplier<T> supplier = get(Identifier.fromNamespaceAndPath(namespace, path));
        return supplier != null ? supplier.get() : null;
    }


    /**
     * Checks if an identifier is registered.
     */
    public static boolean has(Identifier id) {
        return id != null && REGISTRY.containsKey(id);
    }

    /**
     * Removes a registration.
     */
    public static void remove(Identifier id) {
        if (id != null) REGISTRY.remove(id);
    }

    /**
     * Removes all registrations under a namespace (e.g., when a mod unloads).
     */
    public static void removeNamespace(String namespace) {
        if (namespace == null || namespace.isEmpty()) return;
        REGISTRY.keySet().removeIf(id -> id.getNamespace().equals(namespace));
    }

    /**
     * @return unmodifiable view of all registered identifiers
     */
    public static Set<Identifier> keys() {
        return Collections.unmodifiableSet(REGISTRY.keySet());
    }
}