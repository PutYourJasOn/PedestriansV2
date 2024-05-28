package me.json.pedestrians.commands;

import me.json.pedestrians.Messages;
import me.json.pedestrians.Preferences;
import me.json.pedestrians.commands.subcommands.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CommandHandler implements CommandExecutor {

    private final static List<ISubCommand> subCommands = new ArrayList<>();

    static {
        subCommands.add(new CreateSubCommand());
        subCommands.add(new EditSubCommand());
        subCommands.add(new SetPedsSubCommand());
        subCommands.add(new AutoSpawnSubCommand());
        subCommands.add(new AddSkinSubCommand());
        subCommands.add(new ThreadsSubCommand());
        subCommands.add(new AddTagSubCommand());
        subCommands.add(new GetTagsSubCommand());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        //Perm
        if(!commandSender.hasPermission(Preferences.MAIN_PERMISSION)) {
            Messages.sendMessage(commandSender, Messages.NO_PERMISSION);
            return true;
        }

        if(args.length < 1) {
            sendHelpMenu(commandSender);
            return true;
        }

        for (ISubCommand subCommand : subCommands) {
            String name = subCommand.commandName();

            if(args[0].equalsIgnoreCase(name)) {

                List<String> subArgs = new ArrayList<>(Arrays.asList(args));
                subArgs.remove(0);

                //Checks
                if(subCommand.args().length > subArgs.size()) {
                    Messages.sendMessage(commandSender, Messages.WRONG_USAGE);
                    return true;
                }

                try {
                    subCommand.handle(commandSender, subArgs.toArray(new String[0]));
                } catch (ClassCastException e) {
                    Messages.sendMessage(commandSender, Messages.WRONG_COMMAND_SENDER);
                }

                return true;
            }

        }

        Messages.sendMessage(commandSender, Messages.WRONG_USAGE);
        return true;
    }


    private void sendHelpMenu(CommandSender sender) {

        Messages.sendMessage(sender, Messages.HELP_MENU, "+", "+");

        for (ISubCommand subCommand : subCommands) {
            Messages.sendMessage(sender, Messages.COMMAND_INFO, false, commandInfo(subCommand));
        }

        Messages.sendMessage(sender, Messages.HELP_MENU, "-", "-");
    }

    private String commandInfo(ISubCommand subCommand) {

        StringBuilder info = new StringBuilder(subCommand.commandName());

        for (String arg : subCommand.args()) {
            info.append(" <").append(arg).append(">");
        }

        return info.toString();
    }

}
