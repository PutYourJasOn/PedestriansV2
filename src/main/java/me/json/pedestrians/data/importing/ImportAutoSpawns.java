package me.json.pedestrians.data.importing;

import me.json.pedestrians.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ImportAutoSpawns extends BukkitRunnable {

    private final Consumer<Map<String, Integer>> callback;
    private final boolean syncExit;

    public ImportAutoSpawns(Consumer<Map<String, Integer>> callback, boolean syncExit) {
        this.callback = callback;
        this.syncExit = syncExit;
    }

    public void start() {
        this.runTaskAsynchronously(Main.plugin());
    }

    @Override
    public void run() {

        File file = new File(Main.plugin().getDataFolder(), "config.yml");
        YamlConfiguration config = new YamlConfiguration();
        Map<String, Integer> pathNetworks = new HashMap<>();

        try {

            config.load(file);

            for (String line : config.getStringList("AUTO_SPAWNS")) {

                String[] args = line.split(" ");

                pathNetworks.put(args[0], Integer.parseInt(args[1]));
            }

            if(syncExit) {
                Bukkit.getScheduler().runTask(Main.plugin(), () -> callback.accept(pathNetworks));
            } else {
                callback.accept(pathNetworks);
            }

        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

    }
}
