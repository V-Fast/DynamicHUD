package com.tanishisherewith.dynamichud.internal;

import com.tanishisherewith.dynamichud.utils.DynamicValueRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class System {
    // A map to store all instances of DynamicValueRegistry by modId
    protected static final Map<String, List<DynamicValueRegistry>> instances = new ConcurrentHashMap<>();
    protected final String modId;

    public System(String modId) {
        this.modId = modId;
        instances.computeIfAbsent(modId, k -> new ArrayList<>()).add((DynamicValueRegistry) this);
    }

    public static List<DynamicValueRegistry> getInstances(String modId) {
        return instances.get(modId);
    }

    public String getModId() {
        return modId;
    }
}