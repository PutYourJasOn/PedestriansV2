package me.json.pedestrians.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MainCommandHandler implements CommandExecutor {

    private static final PathNetworkCommandHandler pathNetworkCommandHandler = new PathNetworkCommandHandler();
    private static final SkinCommandHandler skinCommandHandler = new SkinCommandHandler();
    private static final DebugCommandHandler debugCommandHandler = new DebugCommandHandler();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if(!sender.hasPermission("Pedestrians")) return true;


        if(args.length == 0 || (args.length==1 && args[0].equalsIgnoreCase("help"))) {

            sender.sendMessage("[{Help}] ");
            sender.sendMessage("/pedestrians pathnetwork");
            sender.sendMessage("    opens up a help menu");
            sender.sendMessage("/pedestrians skin");
            sender.sendMessage("    opens up a help menu");
            sender.sendMessage("/pedestrians debug");
            sender.sendMessage("    opens up a help menu");
            sender.sendMessage("[{----}] ");

            return true;
        }

        if(args.length > 0) {

            if(args[0].equalsIgnoreCase("pathnetwork")) {
                pathNetworkCommandHandler.onCommand(sender, command, s, args);
                return true;
            }

            if(args[0].equalsIgnoreCase("skin")) {
                skinCommandHandler.onCommand(sender, command, s, args);
                return true;
            }

            if(args[0].equalsIgnoreCase("debug")) {
                debugCommandHandler.onCommand(sender, command, s, args);
                return true;
            }

        }

        sender.sendMessage("[{Error}] Wrong command or arguments.");
        return true;
    }

}
