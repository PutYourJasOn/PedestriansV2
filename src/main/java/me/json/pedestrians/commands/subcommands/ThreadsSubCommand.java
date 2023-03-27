package me.json.pedestrians.commands.subcommands;

import me.json.pedestrians.Messages;
import me.json.pedestrians.objects.framework.path.PathNetwork;
import me.json.pedestrians.objects.framework.pedestrian.PedestrianThread;
import org.bukkit.command.CommandSender;

public class ThreadsSubCommand implements ISubCommand <CommandSender> {

    @Override
    public void handle(CommandSender sender, String[] args) {

        Messages.sendMessage(sender, Messages.THREADS, "+", "+");

        for (PathNetwork pathNetwork : PathNetwork.Registry.pathNetworks()) {

            for (PedestrianThread pedestrianThread : pathNetwork.pedestrianThreads()) {
                Messages.sendMessage(sender, Messages.THREAD_INFO, false, pathNetwork.name(), ""+pedestrianThread.size());
            }

        }
        Messages.sendMessage(sender, Messages.THREADS, "-", "-");

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
