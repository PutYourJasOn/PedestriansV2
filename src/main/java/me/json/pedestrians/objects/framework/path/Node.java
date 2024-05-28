package me.json.pedestrians.objects.framework.path;

import me.json.pedestrians.objects.framework.path.connection.ConnectionHandler;
import me.json.pedestrians.utils.Vector3;

import javax.annotation.Nullable;
import java.util.*;

public class Node {

    private final int id;

    private final PathNetwork pathNetwork;
    private final Vector3 pos;
    private final double width;
    private final Vector3 direction; //Path direction
    private Integer forcedAttractiveness; //TODO:

    private final Map<Node, ConnectionHandler> connectedNodes = new HashMap<>();
    private final Set<String> tags = new HashSet<>();

    public Node(PathNetwork pathNetwork, int id, Vector3 pos, double width, Vector3 direction) {
        this.pathNetwork = pathNetwork; this.id=id; this.pos=pos; this.width=width; this.direction=direction;
    }

    //Getters
    public PathNetwork pathNetwork() {
        return this.pathNetwork;
    }

    public Vector3 pos() {
        return this.pos;
    }

    public double width() {
        return this.width;
    }

    public Set<Node> connectedNodes() {
        return new HashSet<>(connectedNodes.keySet());
    }

    public Set<String> tags() {
        return new HashSet<>(tags);
    }

    @Nullable
    public ConnectionHandler connection(Node node) {
        return connectedNodes.get(node);
    }
    public int id() {
        return this.id;
    }

    public Vector3 direction() {
        return this.direction;
    }

    public int attractiveness() {
        return forcedAttractiveness == null ? (int) (width*10) : forcedAttractiveness;
    }

    //Registers Setters
    public void registerConnectedNode(Node node, ConnectionHandler connection) {
        this.connectedNodes.put(node, connection);
    }

    public void removeConnection(Node connectedNode) {
        this.connectedNodes.remove(connectedNode);
    }

    public void registerTag(String tag) {
        this.tags.add(tag);
    }

    public void forcedAttractiveness(Integer forcedAttractiveness) {
        this.forcedAttractiveness=forcedAttractiveness;
    }

    //Functionality
    public Node generateNextNode(Node originNode) {

        Map<Node, ConnectionHandler> nodeCandidates = new HashMap<>(connectedNodes);

        nodeCandidates.remove(originNode);
        if(nodeCandidates.keySet().isEmpty()) return originNode;

        return randomNode(nodeCandidates);
    }

    private Node randomNode(Map<Node, ConnectionHandler> nodeCandidates) {

        List<Node> candidates = new ArrayList<>();

        for (Node node : nodeCandidates.keySet()) {

            for (int i = 0; i < node.attractiveness(); i++) {
                candidates.add(node);
            }

        }

        Collections.shuffle(candidates);
        return candidates.get(0);

    }

}
