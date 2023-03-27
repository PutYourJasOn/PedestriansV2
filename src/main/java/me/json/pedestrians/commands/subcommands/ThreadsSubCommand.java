package me.json.pedestrians.commands.subcommands;

import me.json.pedestrians.objects.framework.path.PathNetwork;
import me.json.pedestrians.objects.framework.pedestrian.PedestrianThread;
import org.bukkit.command.CommandSender;

public class ThreadsSubCommand implements ISubCommand <CommandSender> {

    @Override
    public void handle(CommandSender sender, String[] args) {

        sender.sendMessage("[{Threads}]");
        for (PathNetwork pathNetwork : PathNetwork.Registry.pathNetworks()) {

            for (PedestrianThread pedestrianThread : pathNetwork.pedestrianThreads()) {
                sender.sendMessage("Network: "+pathNetwork.name());
                sender.sendMessage("    "+pedestrianThread.size());
            }

        }
        sender.sendMessage("[{------}]");

    }

    @Override
    public String commandName() {
        return "threads";
    }

    @Override
    public String[] args() {
        return new String[0];
    }



}
