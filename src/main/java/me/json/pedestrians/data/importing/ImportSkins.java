package me.json.pedestrians.data.importing;

import me.json.pedestrians.Main;
import me.json.pedestrians.objects.Skin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.util.function.Consumer;

public class ImportSkins extends BukkitRunnable {

    private final Consumer<Void> callback;

    public ImportSkins(Consumer<Void> callback) {
        this.callback=callback;
    }

    public void start() {
        this.runTaskAsynchronously(Main.plugin());
    }

    @Override
    public void run() {

        File file = new File(Main.plugin().getDataFolder(), "skins.txt");

        try(BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line = reader.readLine();

            while (line != null) {
                String[] args = line.split(" ");
                String base64 = args[1];
                String signature = args[2];

                new Skin(base64, signature);

                line = reader.readLine();
            }

            callback.accept(null);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
