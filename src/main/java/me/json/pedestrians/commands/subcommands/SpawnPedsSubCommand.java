package me.json.pedestrians.commands.subcommands;

import me.json.pedestrians.Messages;
import me.json.pedestrians.objects.PlayerPedestrian;
import me.json.pedestrians.objects.Skin;
import me.json.pedestrians.objects.framework.path.Node;
import me.json.pedestrians.objects.framework.path.PathNetwork;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;

public class SpawnPedsSubCommand implements ISubCommand<CommandSender>  {

    @Override
    public void handle(CommandSender sender, String[] args) {

        PathNetwork pathNetwork = PathNetwork.Registry.pathNetwork(args[0]);
        if(pathNetwork != null) {

            if(StringUtils.isNumeric(args[1])) {

                int nodeId = Integer.parseInt(args[1]);
                Node node = pathNetwork.node(nodeId);
                if(node != null) {

                    if(StringUtils.isNumeric(args[2])) {

                        int amount = Integer.parseInt(args[2]);
                        for (int i = 0; i < amount; i++) {
                            pathNetwork.createPedestrian(PlayerPedestrian.class, node, Skin.Registry.randomSkin());
                        }

                    }else {
                        Messages.sendMessage(sender, Messages.WRONG_USAGE);
                    }

                } else {
                    Messages.sendMessage(sender, Messages.NODE_DOESNT_EXIST);
                }

            } else {
                Messages.sendMessage(sender, Messages.WRONG_USAGE);
            }

        } else {
            Messages.sendMessage(sender, Messages.PATHNETWORK_DOESNT_EXIST);
        }

    }

    @Override
    public String commandName() {
        return "spawnpeds";
    }

    @Override
    public String[] args() {
        return new String[] {"name", "node", "amount"};
    }
}
