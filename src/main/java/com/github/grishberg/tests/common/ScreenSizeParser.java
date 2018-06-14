package com.github.grishberg.tests.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by grishberg on 28.04.18.
 */
public class ScreenSizeParser {
    public static int[] parseScreenSize(String dumpsisWindow) {
        int width = 0;
        int height = 0;
        String pattern = "mSystem=\\(\\d*,\\d*\\)-\\((\\d*),(\\d*)\\)";

        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);

        // Now create matcher object.
        Matcher m = r.matcher(dumpsisWindow);
        if (m.find()) {
            width = Integer.parseInt(m.group(1));
            height = Integer.parseInt(m.group(2));
        }
        return new int[]{width, height};
    }
}
