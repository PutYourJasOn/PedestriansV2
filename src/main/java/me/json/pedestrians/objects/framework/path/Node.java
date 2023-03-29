package me.json.pedestrians.objects.framework.path;

import me.json.pedestrians.objects.framework.path.connection.ConnectionHandler;
import me.json.pedestrians.utils.Vector3;

import java.util.*;

public class Node {

    private final int id;

    private final Vector3 pos;
    private final double width;
    private final Vector3 direction; //Path direction
    private Integer forcedAttractiveness; //TODO:

    private final Map<Node, ConnectionHandler> connectedNodes = new HashMap<>();

    public Node(int id, Vector3 pos, double width, Vector3 direction) {
        this.id=id; this.pos=pos; this.width=width; this.direction=direction;
    }

    //Getters
    public Vector3 pos() {
        return this.pos;
    }

    public double width() {
        return this.width;
    }

    public Set<Node> connectedNodes() {
        return new HashSet<>(connectedNodes.keySet());
    }

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
        return forcedAttractiveness == null ? (int) width*10 : forcedAttractiveness;
    }

    //Registers Setters
    public void registerConnectedNode(Node node, ConnectionHandler connection) {
        this.connectedNodes.put(node, connection);
    }

    public void removeConnection(Node connectedNode) {
        this.connectedNodes.remove(connectedNode);
    }

    public void forcedAttractiveness(Integer forcedAttractiveness) {
        this.forcedAttractiveness=forcedAttractiveness;
    }

    //Functionality
    public Node generateNextNode(Node originNode) {

        Map<Node, ConnectionHandler> nodeCandidates = new HashMap<>(connectedNodes);

        nodeCandidates.remove(originNode);
        if(nodeCandidates.keySet().size() < 1) return originNode;

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
