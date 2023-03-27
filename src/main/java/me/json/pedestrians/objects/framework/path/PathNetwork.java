package me.json.pedestrians.objects.framework.path;

import me.json.pedestrians.Preferences;
import me.json.pedestrians.objects.framework.pedestrian.Pedestrian;
import me.json.pedestrians.objects.framework.pedestrian.PedestrianThread;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class PathNetwork {

    private final String name;
    private final Set<Node> nodes = new HashSet<>();

    private final Set<PedestrianThread> pedestrianThreads = new HashSet<>();
    private Integer defaultPedestrians;

    public PathNetwork(String name) {
        this.name=name;
        Registry.register(name, this);
    }

    //properties
    public int defaultPedestrians() {
        return defaultPedestrians == null ? 0 : defaultPedestrians;
    }

    public void defaultPedestrians(int defaultPedestrians) {
        this.defaultPedestrians = defaultPedestrians;
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
            currentHighestID = node.id() > currentHighestID ? node.id() : currentHighestID;
        }
        return currentHighestID+1;
    }

    //Pedestrians
    //(Will be called by pedestrian)
    public void addPedestrian(Pedestrian pedestrian) {

        //If no threads add one
        if(pedestrianThreads.isEmpty())
            pedestrianThreads.add(new PedestrianThread());

        List<PedestrianThread> threads = pedestrianThreads.stream().sorted(Comparator.comparing(t -> t.size())).collect(Collectors.toList());

        //If smallest thread is at max capacity, create new one. Else: add to smallest thread
        if(threads.get(0).size() >= Preferences.PEDESTRIAN_GROUP_SIZE){
            pedestrianThreads.add(new PedestrianThread(pedestrian));
        } else {
            threads.get(0).add(pedestrian);
        }

    }

    //(Will be called by pedestrian)
    public void removePedestrian(Pedestrian pedestrian) {

        for (PedestrianThread pedestrianThread : new HashSet<>(pedestrianThreads)) {

            if(pedestrianThread.remove(pedestrian) && pedestrianThread.size() == 0) {
                pedestrianThreads.remove(pedestrianThread);
            }

        }
    }

    public void removeAllPedestrians() {
        pedestrians(Integer.MAX_VALUE).forEach(p -> p.remove());
    }

    //Pedestrians getters
    public Set<Pedestrian> pedestrians(int count) {

        Set<Pedestrian> pedestrians = new HashSet<>();
        List<PedestrianThread> threads = pedestrianThreads.stream().sorted(Comparator.comparing(t -> t.size())).collect(Collectors.toList());

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
