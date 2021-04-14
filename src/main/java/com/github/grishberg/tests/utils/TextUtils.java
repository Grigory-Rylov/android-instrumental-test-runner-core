package com.github.grishberg.tests.utils;

public class TextUtils {
    private TextUtils() {/* Utility class */}

    /**
     * @param s string to be checked.
     * @return {@code true} when s is empty or null.
     */
    public static boolean isEmpty(CharSequence s) {
        return s == null || s.length() == 0;
    }
}
