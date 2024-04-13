package com.tanishisherewith.dynamichud.utils;

import java.util.Random;

public class UID {
    private static final String ALPHANUMERIC = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_";
    private static final int LENGTH = 6;
    private static final Random RANDOM = new Random();
    public String uniqueID;

    public UID(String id) {
        this.uniqueID = id;
    }

    public static UID generate() {
        StringBuilder sb = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            sb.append(ALPHANUMERIC.charAt(RANDOM.nextInt(ALPHANUMERIC.length())));
        }
        return new UID(sb.toString());
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }
}