package me.json.pedestrians.commands;

import me.json.pedestrians.Messages;
import me.json.pedestrians.Preferences;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CommandHandler implements CommandExecutor {

    private final static Set<ISubCommand> subCommands = new HashSet<>();

    static {
        subCommands.add(new CreateSubCommand());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        //Perm
        if(!commandSender.hasPermission(Preferences.MAIN_PERMISSION)) {
            commandSender.spigot().sendMessage(TextComponent.fromLegacyText(String.format(Messages.CONNECTION_TYPE_SELECT)));
            return true;
        }

        if(args.length < 1) {
            commandSender.spigot().sendMessage(TextComponent.fromLegacyText(String.format(Messages.WRONG_USAGE)));
            return true;
        }

        for (ISubCommand subCommand : subCommands) {
            String name = subCommand.commandName();

            if(args[0].equalsIgnoreCase(name)) {
                List<String> subArgs = new ArrayList<>(Arrays.asList(args));
                subArgs.remove(0);

                if(subCommand.args().length > subArgs.size()) {
                    commandSender.spigot().sendMessage(TextComponent.fromLegacyText(String.format(Messages.WRONG_USAGE)));
                } else {
                    subCommand.handle(commandSender, subArgs.toArray(new String[subArgs.size()]));
                }

                return true;
            }

        }

        commandSender.spigot().sendMessage(TextComponent.fromLegacyText(String.format(Messages.WRONG_USAGE)));
        return true;
    }

}
