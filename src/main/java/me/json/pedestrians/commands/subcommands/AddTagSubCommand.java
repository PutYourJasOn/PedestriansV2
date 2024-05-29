package me.json.pedestrians.commands.subcommands;

import me.json.pedestrians.Messages;
import me.json.pedestrians.objects.framework.path.Node;
import me.json.pedestrians.objects.framework.path.PathNetwork;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;

public class AddTagSubCommand implements ISubCommand<CommandSender> {

    @Override
    public void handle(CommandSender sender, String[] args) {

        if(args.length == 3 && StringUtils.isNumeric(args[1])) {

            int nodeId = Integer.parseInt(args[1]);

            PathNetwork pathNetwork = PathNetwork.Registry.pathNetwork(args[0]);
            if(pathNetwork != null) {

                Node node = pathNetwork.node(nodeId);
                if(node != null) {

                    node.registerTag(args[2]);
                    Messages.sendMessage(sender, Messages.TAG_ADDED);

                } else {
                    Messages.sendMessage(sender, Messages.NODE_DOESNT_EXIST);
                }

            } else {
                Messages.sendMessage(sender, Messages.PATHNETWORK_DOESNT_EXIST);
            }

        } else {
            Messages.sendMessage(sender, Messages.WRONG_USAGE);
        }

    }

    @Override
    public String commandName() {
        return "addtag";
    }

    @Override
    public String[] args() {
        return new String[] {"name", "node", "tag"};
    }
}
