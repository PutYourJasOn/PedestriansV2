package me.json.pedestrians.commands;

import org.bukkit.permissions.ServerOperator;

public interface ISubCommand <E extends ServerOperator> {

    public void handle (E sender, String[] args);

    public String commandName();
    public String[] args();

}
