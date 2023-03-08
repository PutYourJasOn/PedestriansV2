package me.json.pedestrians.objects.framework.pedestrian;

import me.json.pedestrians.Main;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public class PedestrianThread extends BukkitRunnable {

    private final Set<Pedestrian> pedestrians = new HashSet<>();
    private boolean cancelled = false;

    public PedestrianThread() {
        this.runTaskAsynchronously(Main.plugin());
    }

    public PedestrianThread(Pedestrian pedestrian) {
        pedestrians.add(pedestrian);
        this.runTaskAsynchronously(Main.plugin());
    }

    //Functions
    public void stop() {
        if(!pedestrians.isEmpty()) throw new IllegalStateException("Tried to stop a PedestrianThread while there are still pedestrians in this thread");
        this.cancelled = true;
    }

    //Registry
    public void add(Pedestrian pedestrian) {
        this.pedestrians.add(pedestrian);
    }

    public boolean remove(Pedestrian pedestrian) {
        return pedestrians.remove(pedestrian);
    }

    public Set<Pedestrian> pedestrians() {
        return new HashSet<>(pedestrians);
    }

    //Getters
    public int size() {
        return pedestrians.size();
    }

    //Timer
    @Override
    public void run() {

        if(Bukkit.isPrimaryThread()){
            throw new IllegalThreadStateException("MovementTimer may only run async!");
        }

        while (Main.plugin().isEnabled() && !cancelled) {
            try {
                Thread.sleep(50);
                tick();
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    private void tick() {
        pedestrians.forEach(Pedestrian::move);
    }

}
