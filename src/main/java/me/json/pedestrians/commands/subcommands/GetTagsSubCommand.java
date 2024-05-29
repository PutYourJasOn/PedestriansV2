package me.json.pedestrians.commands.subcommands;

import me.json.pedestrians.Messages;
import me.json.pedestrians.objects.framework.path.Node;
import me.json.pedestrians.objects.framework.path.PathNetwork;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;

public class GetTagsSubCommand implements ISubCommand<CommandSender> {

    @Override
    public void handle(CommandSender sender, String[] args) {

        if(args.length > 0 && args.length < 3) {

            PathNetwork pathNetwork = PathNetwork.Registry.pathNetwork(args[0]);
            if(pathNetwork != null) {

                if(args.length == 2 && StringUtils.isNumeric(args[1])) {

                    int nodeId = Integer.parseInt(args[1]);

                    Node node = pathNetwork.node(nodeId);
                    if(node != null) {

                        Messages.sendMessage(sender, "Tags for "+pathNetwork.name()+" "+node.id()+":", true);
                        for (String tag : node.tags()) {
                            Messages.sendMessage(sender, tag, false);
                        }

                    } else {
                        Messages.sendMessage(sender, Messages.NODE_DOESNT_EXIST);
                    }

                } else {

                    for (Node node : pathNetwork.nodes()) {

                        if(!node.tags().isEmpty()) {
                            Messages.sendMessage(sender, "Tags for "+pathNetwork.name()+" "+node.id()+":", true);
                            for (String tag : node.tags()) {
                                Messages.sendMessage(sender, tag, false);
                            }
                        }

                    }

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
        return "gettags";
    }

    @Override
    public String[] args() {
        return new String[] {"name"};
    }
}
