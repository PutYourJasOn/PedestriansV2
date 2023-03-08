package me.json.pedestrians.commands;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.json.pedestrians.data.importing.ImportPathNetwork;
import me.json.pedestrians.objects.Skin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

public class SkinCommandHandler implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if(args.length == 1 || (args.length == 2 && args[1].equalsIgnoreCase("help"))){
            sender.sendMessage("[{Help}]");
            sender.sendMessage("/pedestrians skin add <UUID>");
            sender.sendMessage("    adds a skin to the system");
            sender.sendMessage("[{----}]");

            return true;
        }

        if(args.length < 3) {
            sender.sendMessage("[{Error}] Wrong command or arguments.");
            return true;
        }

        if(args[1].equalsIgnoreCase("add")) {
            UUID uuid = UUID.fromString(args[2]);

            try {

                URL url = new URL("https://api.ashcon.app/mojang/v2/user/" + uuid);
                URLConnection connection = url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
                jsonObject = jsonObject.get("textures").getAsJsonObject().get("raw").getAsJsonObject();
                String base64 = jsonObject.get("value").getAsString();
                String signature = jsonObject.get("signature").getAsString();

                new Skin(base64, signature);
                sender.sendMessage("[{Success}] "+"Skin added.");

                reader.close();

            } catch (IOException ex) {
                ex.printStackTrace();
            }

            return true;
        }

        sender.sendMessage("[{Error}] Wrong command or arguments.");
        return true;
    }
}
