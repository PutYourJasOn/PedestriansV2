package me.json.pedestrians.data.exporting;

import me.json.pedestrians.Main;
import me.json.pedestrians.objects.framework.path.PathNetwork;
import org.apache.commons.io.FileUtils;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

public class BackupPathNetwork extends BukkitRunnable {

    private final PathNetwork pathNetwork;
    private final Consumer<Void> callback;

    public BackupPathNetwork(PathNetwork pathNetwork, Consumer<Void> callback) {
        this.pathNetwork=pathNetwork;
        this.callback=callback;
    }

    public void start() {
        this.runTaskAsynchronously(Main.plugin());
    }

    @Override
    public void run() {

        File original = new File(Main.plugin().getDataFolder(),"pathnetworks/"+pathNetwork.name()+".json");
        File copy = new File(Main.plugin().getDataFolder(),"pathnetworks/backups/"+pathNetwork.name()+"_"+System.currentTimeMillis()+".json");

        try {
            FileUtils.copyFile(original, copy);

            callback.accept(null);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
