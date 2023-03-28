package me.json.pedestrians.data.exporting;

import me.json.pedestrians.Main;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ExportAutoSpawn extends BukkitRunnable {

    private final String pathNetwork;
    private final int amount;
    private final Consumer<Void> callback;

    public ExportAutoSpawn(String pathNetwork, int amount, Consumer<Void> callback) {
        this.pathNetwork = pathNetwork;
        this.amount = amount;
        this.callback = callback;
    }

    public void start() {
        this.runTaskAsynchronously(Main.plugin());
    }

    @Override
    public void run() {

        File file = new File(Main.plugin().getDataFolder(), "config.yml");
        YamlConfiguration config = new YamlConfiguration();

        try {

            config.load(file);
            List<String> list = config.getStringList("AUTO_SPAWNS");

            for (String line : new ArrayList<>(list)) {
                if(line.startsWith(pathNetwork))
                    list.remove(line);
            }

            if(amount > 0) {
                list.add(pathNetwork+" "+amount);
            }

            config.set("AUTO_SPAWNS", list);
            config.save(file);
            callback.accept(null);

        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

    }
}
