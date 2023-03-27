package me.json.pedestrians.objects.framework.pedestrian;

import me.json.pedestrians.Main;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PedestrianThread extends BukkitRunnable {

    private final Queue<Pedestrian> toAdd = new ConcurrentLinkedQueue<>();
    private final Queue<Pedestrian> toRemove = new ConcurrentLinkedQueue<>();

    private final Set<Pedestrian> pedestrians = new HashSet<>();

    private boolean mayCancel = false;
    private boolean cancelled = false;

    public PedestrianThread() {
        this.runTaskAsynchronously(Main.plugin());
    }

    //Functions
    public void safeStop() {
        this.mayCancel = true;
    }

    //Registry
    public void safeAdd(Pedestrian pedestrian) {
        toAdd.offer(pedestrian);
    }

    public void safeRemove(Pedestrian pedestrian) {
        toRemove.offer(pedestrian);
    }

    public Set<Pedestrian> pedestrians() {
        Set<Pedestrian> set = new HashSet<>(pedestrians);
        set.removeAll(toRemove);
        return set;
    }

    public boolean contains(Pedestrian pedestrian) {
        return pedestrians.contains(pedestrian);
    }

    //Getters
    public int size() {
        return pedestrians.size() + toAdd.size() - toRemove.size();
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

        pedestrians.forEach(Pedestrian::tick);

        while(!toAdd.isEmpty()) {
            pedestrians.add(toAdd.poll());
        }

        while(!toRemove.isEmpty()) {
            Pedestrian p = toRemove.poll();
            pedestrians.remove(p);
        }

        if(pedestrians.isEmpty() && toAdd.isEmpty() && toRemove.isEmpty() && mayCancel) {
            cancelled = true;
        }

    }

}
