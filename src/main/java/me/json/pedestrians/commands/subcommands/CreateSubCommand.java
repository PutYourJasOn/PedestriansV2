package me.json.pedestrians.commands.subcommands;

import me.json.pedestrians.Main;
import me.json.pedestrians.Messages;
import me.json.pedestrians.objects.framework.path.PathNetwork;
import org.bukkit.command.CommandSender;

import java.io.File;

public class CreateSubCommand implements ISubCommand<CommandSender> {

    @Override
    public void handle(CommandSender sender, String[] args) {

        File path = new File(Main.plugin().getDataFolder(),"pathnetworks/"+args[0]+".json");
        if(path.exists()) {
            Messages.sendMessage(sender, Messages.PATHNETWORK_EXISTS);
            return;
        }

        new PathNetwork(args[0]);
        Messages.sendMessage(sender, Messages.PATHNETWORK_CREATED);
    }

    @Override
    public String commandName() {
        return "create";
    }

    @Override
    public String[] args() {
        return new String[]{"name"};
    }

}
