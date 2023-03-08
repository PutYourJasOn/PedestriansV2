package me.json.pedestrians.objects.framework.pedestrian;

import me.json.pedestrians.Main;
import me.json.pedestrians.objects.framework.path.PathNetwork;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class PedestrianGroup extends BukkitRunnable {

    private final int id = Registry.atomic.incrementAndGet();

    private final PathNetwork pathNetwork;
    private final List<Pedestrian> pedestrians = new ArrayList<>();

    private boolean cancelled = false;

    public PedestrianGroup(PathNetwork pathNetwork) {
        this.pathNetwork=pathNetwork;

        Registry.register(this);
        this.runTaskAsynchronously(Main.plugin());
    }

    //Getters
    public int id() {
        return this.id;
    }

    public PathNetwork pathNetwork() {
        return pathNetwork;
    }

    public List<Pedestrian> pedestrians() {
        return new ArrayList<>(this.pedestrians);
    }

    public int id(Pedestrian pedestrian) {
        return pedestrians.indexOf(pedestrian);
    }

    //Registers
    public void registerPedestrian(Pedestrian pedestrian) {
        this.pedestrians.add(pedestrian);
    }
    public void removePedestrian(Pedestrian pedestrian) {this.pedestrians.remove(pedestrian);}

    //Functionality
    public void remove() {
        cancelled = true;
        new ArrayList<>(pedestrians).forEach(Pedestrian::remove);
    }

    //Tick
    public void tick() {
        pedestrians.forEach(Pedestrian::move);
    }

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

    //Registry
    public static class Registry {

        private final static AtomicInteger atomic = new AtomicInteger(-1);
        private final static Set<PedestrianGroup> groups = new HashSet<>();

        private static void register(PedestrianGroup pedestrianGroup) {
            groups.add(pedestrianGroup);
        }

        @Nullable
        public static PedestrianGroup pedestrianGroup(int id) {
            return groups.stream().filter(g -> g.id()==id).findFirst().orElse(null);
        }

        @Nullable
        public static List<PedestrianGroup> pedestrianGroups(PathNetwork pathNetwork) {
            return groups.stream().filter(g -> g.pathNetwork()==pathNetwork).collect(Collectors.toList());
        }

        public static int latestID() {
            return atomic.get();
        }

        public static Set<PedestrianGroup> pedestrianGroups() {
            return new HashSet<>(groups);
        }

    }

}
