
package com.clicktracker.entity;

import java.util.ArrayList;
import java.util.List;

public enum Platform {
    IPHONE,
    ANDROID;

    public static Platform fromString(String platform) {
        return valueOf(platform.toUpperCase());
    }

    public static String toString(Platform platform) {
        return platform.toString();
    }


    public static List<String> enumListToStringList(List<Platform> platforms) {
        if (platforms == null) {
            return null;
        }
        List<String> enumList = new ArrayList<>();
        for (Platform platform : platforms) {
            enumList.add(platform.toString());
        }
        return enumList;
    }
    public static List<Platform> stringListToEnumList(List<String> platforms) {
        if (platforms == null) {
            return null;
        }
        List<Platform> enumList = new ArrayList<>();
        for (String platform : platforms) {
            enumList.add(Platform.fromString(platform));
        }
        return enumList;
    }
}
