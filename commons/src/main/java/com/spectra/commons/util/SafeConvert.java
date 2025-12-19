package com.spectra.commons.util;

import com.spectra.commons.dto.locator.LocatorDTO;

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

    public static LocatorDTO toLocator(Map<String, Object> map, String key, String defaultType) {
        Object data = map.get(key);
        if (data == null) return null;

        if (data instanceof LocatorDTO dto) {
            String value = normalizeString(dto.value());
            if (value == null) return null;
            String type = normalizeString(dto.type());
            return new LocatorDTO(type != null ? type : defaultType, value);
        }

        if (data instanceof Map<?,?> raw) {
            Object valueObj = raw.get("value");
            String value = normalizeString(valueObj);
            if (value == null) return null;
            Object typeObj = raw.get("type");
            String type = normalizeString(typeObj);
            return new LocatorDTO(type != null ? type : defaultType, value);
        }

        throw new IllegalArgumentException(
                "Invalid locator format for key='" + key + "'. Expected LocatorDTO or Map{type, value} but got: " +
                        data.getClass().getName()
        );
    }

    public static LocatorDTO toLocator(Map<String, Object> map, String key) {
        return toLocator(map, key, "auto");
    }

    private static String normalizeString(Object o) {
        if (o == null) return null;
        String s = o.toString().trim();
        return s.isEmpty() ? null : s;
    }
}
