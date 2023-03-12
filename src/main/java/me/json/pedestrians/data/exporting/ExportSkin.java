package me.json.pedestrians.data.exporting;

import me.json.pedestrians.Main;
import me.json.pedestrians.objects.Skin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.function.Consumer;

public class ExportSkin extends BukkitRunnable {

    private final String name;
    private final Skin skin;
    private final Consumer<Void> callback;

    public ExportSkin(String name, Skin skin, Consumer<Void> callback) {
        this.name=name;
        this.skin=skin;
        this.callback=callback;
    }

    public void start() {
        this.runTaskAsynchronously(Main.plugin());
    }

    @Override
    public void run() {

        File file = new File(Main.plugin().getDataFolder(), "skins.txt");

        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardOpenOption.APPEND)) {

            writer.write("\n");
            writer.write(name.replaceAll("\\s+","")+" "+skin.base64()+" "+skin.signature());
            writer.flush();

            callback.accept(null);

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

}
