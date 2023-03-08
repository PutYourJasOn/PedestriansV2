package me.json.pedestrians.commands;

import me.json.pedestrians.objects.Skin;
import me.json.pedestrians.objects.framework.path.Node;
import me.json.pedestrians.objects.framework.path.PathNetwork;
import me.json.pedestrians.objects.framework.pedestrian.Pedestrian;
import me.json.pedestrians.objects.framework.pedestrian.PedestrianGroup;
import me.json.pedestrians.objects.pedestrian_entities.PlayerPedestrianEntity;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class PedestrianGroupCommandHandler implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(args.length == 1 || (args.length == 2 && args[1].equalsIgnoreCase("help"))){
            sender.sendMessage("[{Help}]");
            sender.sendMessage("/pedestrians pedestriangroup create <pathnetworkName>");
            sender.sendMessage("    creates a new pedestriangroup for a pathnetwork");
            sender.sendMessage("/pedestrians pedestriangroup add <groupID> <nodeID>");
            sender.sendMessage("    add a new pedestrian to a pedestriangroup");
            sender.sendMessage("[{----}]");

            return true;
        }

        //1 arg commands
        if(args.length < 3) {
            sender.sendMessage("[{Error}] Wrong command or arguments.");
            return true;
        }

        if(args[1].equalsIgnoreCase("create")) {
            PedestrianGroup pedestrianGroup = new PedestrianGroup(PathNetwork.Registry.pathNetwork(args[2]));
            sender.sendMessage("[{Success}] "+"Pedestrian group created: "+pedestrianGroup.id()+".");

            return true;
        }

        //2 arg commands
        if(args.length < 4) {
            sender.sendMessage("[{Error}] Wrong command or arguments.");
            return true;
        }

        if(args[1].equalsIgnoreCase("add")) {
            PedestrianGroup pedestrianGroup = PedestrianGroup.Registry.pedestrianGroup(Integer.parseInt(args[2]));
            Node node = pedestrianGroup.pathNetwork().node(Integer.parseInt(args[3]));
            new Pedestrian(pedestrianGroup, new PlayerPedestrianEntity(Skin.Registry.randomSkin()), node);
            sender.sendMessage("[{Success}] "+"Pedestrian added.");

            return true;
        }

        sender.sendMessage("[{Error}] Wrong command or arguments.");
        return true;
    }

}
