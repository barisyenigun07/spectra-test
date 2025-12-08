package com.spectra.commons.util;

import java.util.Map;

public final class SafeConvert {
    private SafeConvert() {}

    public static String toString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        return String.valueOf(value);
    }

    public static Integer toInt(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof Number n) return n.intValue();
        return Integer.parseInt(value.toString());
    }

    public static Double toDouble(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof Number n) return n.doubleValue();
        return Double.parseDouble(value.toString());
    }

    public static Boolean toBoolean(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof Boolean b) return b;
        return Boolean.parseBoolean(value.toString());
    }
}
