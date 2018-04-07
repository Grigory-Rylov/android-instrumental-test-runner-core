package com.github.grishberg.tests.common;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by grishberg on 04.04.18.
 */
public class FileHelper {
    private FileHelper() {/* empty constructor for helper class */}

    public static void cleanFolder(File dir) throws IOException {
        FileUtils.deleteQuietly(dir);
        if (!dir.mkdirs()) {
            throw new IOException("Cant create folder " + dir.getAbsolutePath());
        }
    }
}
