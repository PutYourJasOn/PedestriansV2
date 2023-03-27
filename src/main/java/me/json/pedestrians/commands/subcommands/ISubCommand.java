package me.json.pedestrians.commands.subcommands;

import org.bukkit.permissions.ServerOperator;

public interface ISubCommand <E extends ServerOperator> {

    void handle (E sender, String[] args);

    String commandName();
    String[] args();

}
