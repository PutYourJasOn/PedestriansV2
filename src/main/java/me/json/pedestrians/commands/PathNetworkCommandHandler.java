package me.json.pedestrians.commands;

import me.json.pedestrians.data.exporting.BackupPathNetwork;
import me.json.pedestrians.data.exporting.ExportPathNetwork;
import me.json.pedestrians.data.importing.ImportPathNetwork;
import me.json.pedestrians.objects.Skin;
import me.json.pedestrians.objects.framework.path.PathNetwork;
import me.json.pedestrians.objects.framework.pedestrian.Pedestrian;
import me.json.pedestrians.objects.PlayerPedestrianEntity;
import me.json.pedestrians.ui.EditorView;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PathNetworkCommandHandler implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(args.length == 1 || (args.length == 2 && args[1].equalsIgnoreCase("help"))){
            sender.sendMessage("[{Help}]");
            sender.sendMessage("/pedestrians pathnetwork create <name>");
            sender.sendMessage("    creates and loads a new pathnetwork");
            sender.sendMessage("/pedestrians pathnetwork toggleEditor <name>");
            sender.sendMessage("    opens or closes the editor for a pathnetwork");
            sender.sendMessage("/pedestrians pathnetwork export");
            sender.sendMessage("    exports the currently editing pathnetwork");
            sender.sendMessage("/pedestrians pathnetwork load <name>");
            sender.sendMessage("    loads a pathnetwork");
            sender.sendMessage("/pedestrians pathnetwork backup");
            sender.sendMessage("    backs up the currently editing pathnetwork");
            sender.sendMessage("\n");
            sender.sendMessage("/pedestrians pathnetwork addPedestrians <name> <count>");
            sender.sendMessage("    adds a specific amount of pedestrian to a pathnetwork");
            sender.sendMessage("/pedestrians pathnetwork removePedestrians <name> <count>");
            sender.sendMessage("    removes a specific amount of pedestrian to a pathnetwork");
            sender.sendMessage("[{----}]");

            return true;
        }

        //0 arg commands
        if(args[1].equalsIgnoreCase("export")) {

            EditorView editorView = EditorView.Registry.editorView((Player) sender);

            if(editorView == null) {
                sender.sendMessage("[{Error}] "+"You're not editing any Path Network at the moment");
            }else{
                new ExportPathNetwork(editorView.pathNetwork(), (v) -> {
                    sender.sendMessage("[{Success}] "+"Network exported.");
                }).start();
            }

            return true;
        }

        if(args[1].equalsIgnoreCase("backup")) {

            EditorView editorView = EditorView.Registry.editorView((Player) sender);

            if(editorView == null) {
                sender.sendMessage("[{Error}] "+"You're not editing any Path Network at the moment");
            }else{
                new BackupPathNetwork(editorView.pathNetwork(), (v) -> {
                    sender.sendMessage("[{Success}] "+"Network backup done.");
                }).start();
            }

            return true;
        }

        //1 arg commands
        if(args.length < 3) {
            sender.sendMessage("[{Error}] Wrong command or arguments.");
            return true;
        }

        if(args[1].equalsIgnoreCase("load")) {
            String name = args[2];
            new ImportPathNetwork(name, pn -> sender.sendMessage("[{Success}] "+name+" loaded with "+pn.nodes().size()+" nodes.")).start();

            return true;
        }

        //View editor
        if(args[1].equalsIgnoreCase("create")) {
            new PathNetwork(args[2]);
            sender.sendMessage("[{Success}] "+args[2]+" created.");

            return true;
        }

        if(args[1].equalsIgnoreCase("toggleEditor")) {

            EditorView editorView = EditorView.Registry.editorView((Player) sender);

            if(editorView != null) {
                editorView.stop();
            }else{
                PathNetwork pathNetwork = PathNetwork.Registry.pathNetwork(args[2]);
                new EditorView((Player) sender, pathNetwork);
                //EditorView.Registry.editorView(pathNetwork).registerEditor((Player) sender);
            }

            return true;
        }

        //2 arg commands
        if(args.length < 4) {
            sender.sendMessage("[{Error}] Wrong command or arguments.");
            return true;
        }

        if(args[1].equalsIgnoreCase("addpedestrians")) {

            PathNetwork pathNetwork = PathNetwork.Registry.pathNetwork(args[2]);
            int count = Integer.parseInt(args[3]);

            for (int i = 0; i < count; i++) {
                new Pedestrian(pathNetwork, new PlayerPedestrianEntity(Skin.Registry.randomSkin()), pathNetwork.randomNode());
            }

            sender.sendMessage("[{Success}] "+"Pedestrians added.");
            return true;
        }

        if(args[1].equalsIgnoreCase("removepedestrians")) {

            PathNetwork pathNetwork = PathNetwork.Registry.pathNetwork(args[2]);
            int count = Integer.parseInt(args[3]);

            for (Pedestrian pedestrian : pathNetwork.pedestrians(count)) {
                pedestrian.remove();
            }

            sender.sendMessage("[{Success}] "+"Pedestrians removed.");
            return true;
        }

        sender.sendMessage("[{Error}] Wrong command or arguments.");
        return true;
    }

}
