package me.json.pedestrians.commands.subcommands;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.json.pedestrians.Main;
import me.json.pedestrians.Messages;
import me.json.pedestrians.data.exporting.ExportSkin;
import me.json.pedestrians.objects.Skin;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

public class AddSkinSubCommand implements ISubCommand<CommandSender>{

    @Override
    public void handle(CommandSender sender, String[] args) {

        UUID uuid = UUID.fromString(args[1]);
        Bukkit.getScheduler().runTaskAsynchronously(Main.plugin(), () -> {

            try {

                URL url = new URL("https://api.ashcon.app/mojang/v2/user/" + uuid);
                URLConnection connection = url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
                jsonObject = jsonObject.get("textures").getAsJsonObject().get("raw").getAsJsonObject();
                String base64 = jsonObject.get("value").getAsString();
                String signature = jsonObject.get("signature").getAsString();

                String name = args[0];

                new ExportSkin(name, new Skin(name, base64, signature), v -> Messages.sendMessage(sender, Messages.SKIN_ADDED)).start();

                reader.close();

            } catch (IOException ex) {
                ex.printStackTrace();
            }

        });

    }

    @Override
    public String commandName() {
        return "addskin";
    }

    @Override
    public String[] args() {
        return new String[]{"name", "uuid"};
    }
}
