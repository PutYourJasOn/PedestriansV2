package me.json.pedestrians;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class Messages {

    public static final ChatColor WHITE                         = ChatColor.of("#FCF8E8");
    public static final ChatColor SAGE                          = ChatColor.of("#94B49F");
    public static final ChatColor RED                           = ChatColor.of("#DF7861");
    public static final ChatColor LIGHT_RED                     = ChatColor.of("#ECB390");

    public static final String PRECISE_MODE_TOGGLE       = textComponent("Precise mode: %s", RED, true);

    private static String textComponent(String string, ChatColor color, boolean italic) {
        TextComponent textComponent = new TextComponent(string);
        textComponent.setColor(color);
        textComponent.setItalic(italic);
        return textComponent.toLegacyText();
    }

}
