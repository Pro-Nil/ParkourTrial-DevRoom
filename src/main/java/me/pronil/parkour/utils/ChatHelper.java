package me.pronil.parkour.utils;

import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Collectors;

public class ChatHelper {


    public static String t(String t) {
        return ChatColor.translateAlternateColorCodes('&', t);
    }


    public static List<String> t(List<String> t) {
        return t.stream().map(ChatHelper::t).collect(Collectors.toList());
    }

    public static String[] splitTeamText(String input) {
        final int inputLength = input.length();
        if (inputLength > 16) {
            String prefix = input.substring(0, 16);

            final int lastColorIndex = prefix.lastIndexOf(ChatColor.COLOR_CHAR);

            String suffix;

            if (lastColorIndex >= 14) {
                prefix = prefix.substring(0, lastColorIndex);
                suffix = ChatColor.getLastColors(input.substring(0, 17)) + input.substring(lastColorIndex + 2);
            } else {
                suffix = ChatColor.getLastColors(prefix) + input.substring(16);
            }

            if (suffix.length() > 16) {
                suffix = suffix.substring(0, 16);
            }

            return new String[]{prefix, suffix};
        } else {
            return new String[]{input, ""};
        }
    }
}