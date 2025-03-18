package com.tanishisherewith.dynamichud.internal;

import com.tanishisherewith.dynamichud.utils.DynamicValueRegistry;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class System {
    // A map to store all instances of DynamicValueRegistry by modId
    private static final Map<Class<?>, Map<String, Set<Object>>> instanceRegistry = new HashMap<>();

    public static void registerInstance(Object instance, String modId) {
        Class<?> cls = instance.getClass();
        Map<String, Set<Object>> modMap = instanceRegistry.computeIfAbsent(cls, k -> new HashMap<>());
        Set<Object> list = modMap.computeIfAbsent(modId, k -> new HashSet<>());
        list.add(instance);
    }

    public static <T> List<T> getInstances(Class<T> cls, String modId) {
        Map<String, Set<Object>> modMap = instanceRegistry.get(cls);
        if (modMap == null) return Collections.emptyList();
        Set<Object> list = modMap.get(modId);
        if (list == null) return Collections.emptyList();
        List<T> typedList = new ArrayList<>();
        for (Object obj : list) {
            if (cls.isInstance(obj)) {
                typedList.add(cls.cast(obj));
            }
        }
        return typedList;
    }
}