package net.wheel.api.util;

import java.io.File;

import net.neoforged.fml.loading.FMLPaths;

public final class PathUtil {
    private PathUtil() {
    }

    public static File getBaseDir() {
        File configRoot;
        try {
            configRoot = FMLPaths.CONFIGDIR.get().toFile();
        } catch (Throwable t) {
            configRoot = new File(".");
        }

        File dir = new File(configRoot, "moblimiter");
        if (!dir.exists())
            dir.mkdirs();
        return dir;
    }
}
