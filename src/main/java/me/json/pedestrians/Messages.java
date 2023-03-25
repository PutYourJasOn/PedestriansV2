package me.json.pedestrians;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class Messages {

    public static final ChatColor WHITE                 = ChatColor.of("#FCF8E8");
    public static final ChatColor SAGE                  = ChatColor.of("#94B49F");
    public static final ChatColor RED                   = ChatColor.of("#DF7861");
    public static final ChatColor LIGHT_RED             = ChatColor.of("#ECB390");

    //Editor
    public static final String PRECISE_MODE_TOGGLE      = textComponent("Precise mode: %s", RED, true);
    public static final String NODE_SELECTED            = textComponent("Node: %s selected", RED, true);
    public static final String FIELD_SET                = textComponent("%s set", RED, true);
    public static final String NODE_CREATED             = textComponent("Node created", RED, true);
    public static final String CLICK_AGAIN_REMOVE       = textComponent("Click again to remove this node", RED, true);
    public static final String NODE_REMOVED             = textComponent("Node removed", RED, true);
    public static final String CONNECTION_TYPE_SELECT   = textComponent("ConnectionType: %s", RED, true);
    public static final String CONNECTION_CREATED       = textComponent("Connection set", RED, true);
    public static final String PATHNETWORK_SAVED        = textComponent("PathNetwork has been saved", RED, true);
    public static final String PATHNETWORK_BACKUP       = textComponent("PathNetwork's backup has been made", RED, true);

    //Commands
    public static final String NO_PERMISSION            = textComponent("You don't have the sufficient permissions", RED, true);
    public static final String WRONG_USAGE              = textComponent("Wrong usage", RED, true);
    public static final String PATHNETWORK_EXISTS       = textComponent("This PathNetwork already exists", RED, true);
    public static final String PATHNETWORK_CREATED      = textComponent("PathNetwork created", RED, true);

    private static String textComponent(String string, ChatColor color, boolean italic) {
        TextComponent textComponent = new TextComponent(string);
        textComponent.setColor(color);
        textComponent.setItalic(italic);
        return textComponent.toLegacyText();
    }

}
