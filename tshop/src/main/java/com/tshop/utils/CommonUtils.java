package com.tshop.utils;

import java.util.UUID;

public class CommonUtils {
    public static boolean isValidUUID(String str) {
        try {
            UUID.fromString(str);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private CommonUtils() {
    }
}
