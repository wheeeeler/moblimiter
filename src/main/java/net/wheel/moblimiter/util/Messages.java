package net.wheel.moblimiter.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Properties;

import net.minecraft.network.chat.Component;

import net.wheel.api.util.PathUtil;

public final class Messages {
    private static final String RESOURCE_PATH = "/assets/moblim/messages.properties";
    private static final String TARGET_NAME = "ml-messages.properties";
    private static final Properties PROPS = new Properties();
    private static File targetFile;

    private Messages() {
    }

    public static void init() {
        targetFile = new File(PathUtil.getBaseDir(), TARGET_NAME);
        if (!targetFile.exists()) {
            try (InputStream in = Messages.class.getResourceAsStream(RESOURCE_PATH);
                    FileOutputStream out = new FileOutputStream(targetFile)) {
                if (in != null)
                    in.transferTo(out);
            } catch (Throwable ignored) {
            }
        }
        reload();
    }

    public static void reload() {
        try (java.io.FileInputStream in = new java.io.FileInputStream(targetFile)) {
            PROPS.clear();
            PROPS.load(new java.io.InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (Throwable ignored) {
        }
    }

    public static String text(String key, Object... args) {
        String pattern = PROPS.getProperty(key, key);
        Object[] withPrefix = new Object[args.length + 1];
        withPrefix[0] = PROPS.getProperty("prefix", "");
        System.arraycopy(args, 0, withPrefix, 1, args.length);
        return String.format(Locale.ROOT, pattern, withPrefix);
    }

    public static Component component(String key, Object... args) {
        return MLColor.parse(text(key, args));
    }
}
