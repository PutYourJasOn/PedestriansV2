package me.json.pedestrians.commands.subcommands;

import me.json.pedestrians.Main;
import me.json.pedestrians.Messages;
import me.json.pedestrians.data.exporting.ExportAutoSpawn;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;

import java.io.File;

public class AutoSpawnSubCommand implements ISubCommand<CommandSender> {

    @Override
    public void handle(CommandSender sender, String[] args) {

        if(!StringUtils.isNumeric(args[1])) {
            Messages.sendMessage(sender, Messages.WRONG_USAGE);
            return;
        }

        File path = new File(Main.plugin().getDataFolder(),"pathnetworks/"+args[0]+".json");
        if(!path.exists()) {
            Messages.sendMessage(sender, Messages.PATHNETWORK_DOESNT_EXIST);
            return;
        }

        new ExportAutoSpawn(args[0], Integer.parseInt(args[1]), (v) -> {
            Messages.sendMessage(sender, Messages.AUTO_SPAWN_SET);
        }).start();

    }

    @Override
    public String commandName() {
        return "autospawn";
    }

    @Override
    public String[] args() {
        return new String[]{"name", "amount"};
    }
}
