package me.json.pedestrians.commands.subcommands;

import me.json.pedestrians.Main;
import me.json.pedestrians.Messages;
import me.json.pedestrians.data.importing.ImportPathNetwork;
import me.json.pedestrians.objects.framework.path.PathNetwork;
import me.json.pedestrians.ui.EditorView;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class EditSubCommand implements ISubCommand<Player> {

    private final Set<Player> players = new HashSet<>();

    @Override
    public void handle(Player sender, String[] args) {

        if(players.contains(sender)) return;

        EditorView editorView = EditorView.Registry.editorView(sender);
        if(editorView != null) {
            Messages.sendMessage(sender, Messages.ALREADY_EDITING);
            return;
        }

        editorView = EditorView.Registry.editorView(args[0]);
        if(editorView != null) {
            Messages.sendMessage(sender, Messages.ALREADY_BEING_EDITED);
            return;
        }

        PathNetwork pathNetwork = PathNetwork.Registry.pathNetwork(args[0]);
        if(pathNetwork == null) {

            //Exists?
            File path = new File(Main.plugin().getDataFolder(),"pathnetworks/"+args[0]+".json");
            if(!path.exists()) {
                Messages.sendMessage(sender, Messages.PATHNETWORK_DOESNT_EXIST);
                return;
            }

            //Import
            players.add(sender);

            new ImportPathNetwork(args[0], (p) -> {
                players.remove(sender);
                new EditorView(sender, p);
            }, true).start();

        } else {
            new EditorView(sender, pathNetwork);
        }

    }

    @Override
    public String commandName() {
        return "edit";
    }

    @Override
    public String[] args() {
        return new String[]{"name"};
    }
}
