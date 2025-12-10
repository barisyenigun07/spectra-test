package com.spectra.commons.util;

import java.util.Map;

public final class SafeConvert {
    private SafeConvert() {}

    public static String toString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return (value == null) ? null : String.valueOf(value);
    }

    public static String toString(Map<String, Object> map, String key, String defaultValue) {
        String result = toString(map, key);
        return (result == null) ? defaultValue : result;
    }

    public static Integer toInt(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof Number n) return n.intValue();
        try {
            return Integer.parseInt(value.toString());
        } catch (Exception e) {
            return null;
        }
    }

    public static Integer toInt(Map<String, Object> map, String key, int defaultValue) {
        Integer result = toInt(map, key);
        return (result == null) ? defaultValue : result;
    }

    public static Double toDouble(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof Number n) return n.doubleValue();
        try {
            return Double.parseDouble(value.toString());
        } catch (Exception e) {
            return null;
        }
    }

    public static Double toDouble(Map<String, Object> map, String key, double defaultValue) {
        Double result = toDouble(map, key);
        return (result == null) ? defaultValue : result;
    }

    public static Boolean toBoolean(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof Boolean b) return b;
        try {
            return Boolean.parseBoolean(value.toString());
        } catch (Exception e) {
            return null;
        }
    }

    public static Boolean toBoolean(Map<String, Object> map, String key, boolean defaultValue) {
        Boolean result = toBoolean(map, key);
        return (result == null) ? defaultValue : result;
    }
}
