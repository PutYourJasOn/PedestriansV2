package me.json.pedestrians.objects.framework.path;

import me.json.pedestrians.Preferences;
import me.json.pedestrians.objects.framework.pedestrian.Pedestrian;
import me.json.pedestrians.objects.framework.pedestrian.PedestrianThread;
import me.json.pedestrians.utils.Vector3;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class PathNetwork {

    private final String name;
    private final Set<Node> nodes = new HashSet<>();

    private final Set<PedestrianThread> pedestrianThreads = new HashSet<>();

    public PathNetwork(String name) {
        this.name=name;
        Registry.register(name, this);
    }

    //Node registering
    public void addNode(Node node) {
        nodes.add(node);
    }
    public void removeNode(Node node) {
        nodes.remove(node);
        for (Node otherNode : nodes) {
            if(!otherNode.connectedNodes().contains(node)) continue;
            otherNode.removeConnection(node);
        }
    }

    //Node Getters
    public Set<Node> nodes() {
        return new HashSet<>(nodes);
    }
    public Node randomNode() {
        return nodes.stream().skip(new Random().nextInt(nodes.size())).findFirst().orElse(null);
    }
    public String name() {
        return this.name;
    }

    @Nullable
    public Node node(int id) {
        return nodes.stream().filter(n -> n.id()==id).findFirst().orElse(null);
    }
    public int nextNodeID() {
        int currentHighestID = -1;
        for (Node node : nodes) {
            currentHighestID = Math.max(node.id(), currentHighestID);
        }
        return currentHighestID+1;
    }

    //Pedestrians
    public void createPedestrian(Class<? extends Pedestrian> pedestrianClass, Object... args) {
        createPedestrian(pedestrianClass, randomNode(), args);
    }

    public void createPedestrian(Class<? extends Pedestrian> pedestrianClass, Node node, Object... args) {

        try {

            Object[] completeArgs = new Object[]{node};
            completeArgs = ArrayUtils.addAll(completeArgs, args);

            Pedestrian pedestrian = (Pedestrian) pedestrianClass.getConstructors()[0].newInstance(completeArgs);
            addPedestrian(pedestrian);

        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void addPedestrian(Pedestrian pedestrian) {

        //If no threads add one
        if(pedestrianThreads.isEmpty())
            pedestrianThreads.add(new PedestrianThread());

        List<PedestrianThread> threads = pedestrianThreads.stream().sorted(Comparator.comparing(PedestrianThread::size)).collect(Collectors.toList());

        //If smallest thread is at max capacity, create new one. Else: add to the smallest thread
        if(threads.get(0).size() >= Preferences.PEDESTRIAN_GROUP_SIZE){

            PedestrianThread thread = new PedestrianThread();
            pedestrianThreads.add(thread);
            thread.safeAdd(pedestrian);

        } else {
            threads.get(0).safeAdd(pedestrian);
        }

    }

    public Collection<Pedestrian> getClosePedestrians(Vector3 base, double radius) {

        List<Pedestrian> pedestrians = new ArrayList<>();

        for (PedestrianThread pedestrianThread : pedestrianThreads) {
            for (Pedestrian pedestrian : pedestrianThread.pedestrians()) {

                //System.out.println(pedestrian.pos() + " " + base + " " + radius);
                //System.out.println(pedestrian.pos().distanceTo(base));

                if(pedestrian.pos().distanceTo(base) <= radius) {
                    pedestrians.add(pedestrian);
                }

            }
        }

        return pedestrians;
    }

    public void removePedestrian(Pedestrian pedestrian) {

        PedestrianThread thread = pedestrianThreads.stream().filter(t -> t.contains(pedestrian)).findFirst().orElse(null);
        if(thread == null) return;

        pedestrian.remove();
        thread.safeRemove(pedestrian);

        if(thread.size() == 0) {
            thread.safeStop();
            pedestrianThreads.remove(thread);
        }

    }

    public void removeAllPedestrians() {
        pedestrians(Integer.MAX_VALUE).forEach(Pedestrian::remove);
    }

    //Pedestrians getters
    public Set<Pedestrian> pedestrians(int count) {

        Set<Pedestrian> pedestrians = new HashSet<>();
        List<PedestrianThread> threads = pedestrianThreads.stream().sorted(Comparator.comparing(PedestrianThread::size)).collect(Collectors.toList());

        for (PedestrianThread thread : threads) {

            for (Pedestrian pedestrian : thread.pedestrians()) {

                if(pedestrians.size() == count) return pedestrians;
                pedestrians.add(pedestrian);

            }

        }

        return pedestrians;
    }

    public Set<PedestrianThread> pedestrianThreads() {
        return pedestrianThreads;
    }

    //Registry
    public static class Registry {

        private static final Map<String, PathNetwork> entries = new HashMap<>();

        public static void register(String name, PathNetwork pathNetwork) {
            entries.put(name, pathNetwork);
        }

        @Nullable
        public static PathNetwork pathNetwork(String name) {
            return entries.getOrDefault(name, null);
        }

        public static Set<PathNetwork> pathNetworks() {
            return new HashSet<>(entries.values());
        }

    }

}
