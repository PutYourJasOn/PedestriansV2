package me.json.pedestrians.objects.framework.path;

import javax.annotation.Nullable;
import java.util.*;

public class PathNetwork {

    private final String name;
    private final Set<Node> nodes = new HashSet<>();

    public PathNetwork(String name) {
        this.name=name;
        Registry.register(name, this);
    }

    //Registers
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

    //Getters
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

    public int nextID() {
        int currentHighestID = -1;
        for (Node node : nodes) {
            currentHighestID = node.id() > currentHighestID ? node.id() : currentHighestID;
        }
        return currentHighestID+1;
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

    }

}
