package net.wheel.moblimiter.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class MLColor {
    public static Component parse(String input) {
        MutableComponent result = Component.empty();
        StringBuilder buffer = new StringBuilder();
        ChatFormatting current = null;

        for (int i = 0, len = input.length(); i < len; i++) {
            char c = input.charAt(i);

            if (c == '&' && i + 1 < len) {
                if (buffer.length() > 0) {
                    result.append(Component.literal(buffer.toString()).withStyle(current));
                    buffer.setLength(0);
                }

                ChatFormatting format = ChatFormatting.getByCode(Character.toLowerCase(input.charAt(++i)));
                current = (format == ChatFormatting.RESET) ? null : format != null ? format : current;

                if (format == null) {
                    buffer.append('&').append(input.charAt(i));
                }
            } else {
                buffer.append(c);
            }
        }

        if (buffer.length() > 0) {
            result.append(Component.literal(buffer.toString()).withStyle(current));
        }

        return result;
    }
}
