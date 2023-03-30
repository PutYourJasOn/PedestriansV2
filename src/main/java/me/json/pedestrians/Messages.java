package me.json.pedestrians;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Messages {

    public static final ChatColor A                     = ChatColor.of("#255f85"); //prefix
    public static final ChatColor B                     = ChatColor.of("#ed6a5a"); //errors
    public static final ChatColor C                     = ChatColor.of("#ffffff"); //commands help menu

    public static final String PREFIX                   = textComponent("{Ped3str1ans} ", A, false);

    //Editor
    public static final String PRECISE_MODE_TOGGLE      = textComponent("Precise mode: %s", C, true);
    public static final String NODE_SELECTED            = textComponent("Node: %s selected", C, true);
    public static final String FIELD_SET                = textComponent("%s set", C, true);
    public static final String NODE_CREATED             = textComponent("Node created", C, true);
    public static final String CLICK_AGAIN_REMOVE       = textComponent("Click again to remove this node", C, true);
    public static final String NODE_REMOVED             = textComponent("Node removed", C, true);
    public static final String CONNECTION_TYPE_SELECT   = textComponent("ConnectionType: %s", C, true);
    public static final String CONNECTION_CREATED       = textComponent("Connection set", C, true);
    public static final String PATHNETWORK_SAVED        = textComponent("PathNetwork has been saved", C, true);

    //Commands
    public static final String COMMAND_INFO             = textComponent("- /peds %s", C, false);
    public static final String NO_PERMISSION            = textComponent("You don't have the sufficient permissions", B, true);
    public static final String WRONG_USAGE              = textComponent("Wrong usage", B, true);
    public static final String WRONG_COMMAND_SENDER     = textComponent("This command can't be executed as the current command sender", B, true);
    public static final String PATHNETWORK_EXISTS       = textComponent("This PathNetwork already exists", B, true);
    public static final String PATHNETWORK_DOESNT_EXIST = textComponent("This PathNetwork doesn't exist", B, true);
    public static final String PATHNETWORK_CREATED      = textComponent("PathNetwork created", C, true);
    public static final String SKIN_ADDED               = textComponent("Skin added", C, true);
    public static final String ALREADY_EDITING          = textComponent("You're already editing a PathNetwork", B, true);
    public static final String ALREADY_BEING_EDITED     = textComponent("This PathNetwork is already being edited", B, true);
    public static final String PEDESTRIANS_SET          = textComponent("The pedestrian amount has been set", C, true);
    public static final String HELP_MENU                = textComponent("%s Help menu %s", C, true);
    public static final String THREADS                  = textComponent("%s Threads %s", C, true);
    public static final String THREAD_INFO              = textComponent("- Network: %s, peds: %s", C, true);
    public static final String AUTO_SPAWN_SET           = textComponent("This PathNetwork's autospawn has been set", C, true);

    private static String textComponent(String string, ChatColor color, boolean italic) {
        TextComponent textComponent = new TextComponent(string);
        textComponent.setColor(color);
        textComponent.setItalic(italic);
        return textComponent.toLegacyText();
    }

    //Util
    public static void sendMessage(CommandSender sender, String message, String... args) {
        sendMessage(sender, message, true, args);
    }

    public static void sendMessage(Player player, String message, String... args) {
        sendMessage(player, message, true, args);
    }

    public static void sendMessage(Player player, String message, boolean usePrefix, String... args) {
        String prefix = usePrefix ? PREFIX : "";
        player.spigot().sendMessage(TextComponent.fromLegacyText(String.format(prefix+message, args)));
    }

    public static void sendMessage(CommandSender player, String message, boolean usePrefix, String... args) {
        String prefix = usePrefix ? PREFIX : "";
        player.spigot().sendMessage(TextComponent.fromLegacyText(String.format(prefix+message, args)));
    }

    public static void sendActionBar(Player player, String message, String... args) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(String.format(message, args)));
    }

}
