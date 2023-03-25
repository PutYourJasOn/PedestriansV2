package me.json.pedestrians.commands;

import me.json.pedestrians.Main;
import me.json.pedestrians.Messages;
import me.json.pedestrians.objects.framework.path.PathNetwork;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;

import java.io.File;

public class CreateSubCommand implements ISubCommand<CommandSender> {

    @Override
    public void handle(CommandSender sender, String[] args) {

        File path = new File(Main.plugin().getDataFolder(),"pathnetworks/"+args[0]+".json");
        if(path.exists()) {
            sender.spigot().sendMessage(TextComponent.fromLegacyText(String.format(Messages.PATHNETWORK_EXISTS)));
            return;
        }

        new PathNetwork(args[0]);
        sender.spigot().sendMessage(TextComponent.fromLegacyText(String.format(Messages.PATHNETWORK_CREATED)));

    }

    @Override
    public String commandName() {
        return "create";
    }

    @Override
    public String[] args() {
        return new String[]{"<name>"};
    }


}
