package com.tanishisherewith.dynamichud.newTrial.utils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class System {
    protected final String modId;

    // A map to store all instances of DynamicValueRegistry by modId
    protected static final Map<String, List<DynamicValueRegistry>> instances = new ConcurrentHashMap<>();

    public System(String modId) {
        this.modId = modId;
    }

    public String getModId() {
        return modId;
    }

    public static List<DynamicValueRegistry> getInstances(String modId) {
        return instances.get(modId);
    }
}