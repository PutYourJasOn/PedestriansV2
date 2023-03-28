package me.json.pedestrians.data.importing;

import me.json.pedestrians.Main;
import me.json.pedestrians.Preferences;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;

public class ImportConfig extends BukkitRunnable {

    public void start() {
        this.runTaskAsynchronously(Main.plugin());
    }

    @Override
    public void run() {

        File file = new File(Main.plugin().getDataFolder(), "config.yml");
        YamlConfiguration config = new YamlConfiguration();

        try {

            config.load(file);

            Preferences.PEDESTRIAN_MIN_VELOCITY = (float) config.getDouble("PEDESTRIAN_MIN_VELOCITY");
            Preferences.PEDESTRIAN_MAX_VELOCITY = (float) config.getDouble("PEDESTRIAN_MAX_VELOCITY");
            Preferences.PEDESTRIAN_GROUP_SIZE   = config.getInt("PEDESTRIAN_GROUP_SIZE");
            Preferences.SPAWN_RADIUS            = config.getInt("SPAWN_RADIUS");

        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

    }
}
