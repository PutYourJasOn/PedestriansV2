package me.json.pedestrians.ui;

import me.json.pedestrians.Main;
import me.json.pedestrians.Messages;
import me.json.pedestrians.objects.entities.NodeClientEntity;
import me.json.pedestrians.objects.framework.path.Node;
import me.json.pedestrians.objects.framework.path.connection.ConnectionHandler;
import me.json.pedestrians.objects.framework.path.connection.DirectConnectionHandler;
import me.json.pedestrians.objects.framework.path.connection.JunctionConnectionHandler;
import me.json.pedestrians.utils.InterpolationUtil;
import me.json.pedestrians.utils.Vector3;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class EditorViewRenderer extends BukkitRunnable {

    private final Set<NodeClientEntity> nodeEntities = new HashSet<>();

    private final EditorView editorView;

    public EditorViewRenderer(EditorView editorView) {
        this.editorView = editorView;

        spawnNodeStands();
        updateNodeTexts();
        start();
    }

    //Functions
    public NodeClientEntity spawnNodeEntity(Node node) {
        Location location = node.pos().toLocation().setDirection(node.direction().toBukkitVector());

        NodeClientEntity nodeClientEntity = new NodeClientEntity(location, node, editorView.player());
        nodeEntities.add(nodeClientEntity);

        return nodeClientEntity;
    }

    public void updateNodeTexts() {
        nodeEntities.forEach(this::updateNodeText);
    }

    private void updateNodeText(NodeClientEntity nodeClientEntity) {

        StringBuilder text = new StringBuilder(Messages.A.toString() + "§l§oNode: " + nodeClientEntity.node().id() + "§x§f§f§f§f§f§f");

        for (Node connectedNode : nodeClientEntity.node().connectedNodes()) {

            String connectionName = ConnectionHandler.ConnectionHandlerType.strippedName(nodeClientEntity.node().connection(connectedNode));
            text.append("\n→").append(connectedNode.id()).append(": ").append(connectionName);
        }

        if(!nodeClientEntity.text().equals(text.toString()))
            nodeClientEntity.text(text.toString());
    }

    public void removeNodeEntity(NodeClientEntity nodeEntity) {
        nodeEntities.remove(nodeEntity);
        nodeEntity.remove();
    }

    private void spawnNodeStands() {
        editorView.pathNetwork().nodes().forEach(this::spawnNodeEntity);
    }

    private void start() {
        this.runTaskTimerAsynchronously(Main.plugin(), 1, 1);
    }

    public void stop() {

        nodeEntities.forEach(NodeClientEntity::remove);
        nodeEntities.clear();

        this.cancel();
    }

    //Timer
    @Override
    public void run() {

        Set<Vector3> greenVertices = new HashSet<>();
        Set<Vector3> yellowVertices = new HashSet<>();

        for (NodeClientEntity nodeEntity : nodeEntities) {

            if(!nodeEntity.isViewer(editorView.player())) continue;

            renderConnections(nodeEntity.node(), greenVertices);

            Vector3[] nodePositions = nodePositions(nodeEntity.node());
            yellowVertices.addAll(InterpolationUtil.lineVertices(nodePositions[0],nodePositions[1],15));
            yellowVertices.addAll(Arrays.stream(nodePositions(nodeEntity.node())).toList());
        }

        //Render
        for (Vector3 vertex : greenVertices) {
            editorView.player().spawnParticle(Particle.COMPOSTER,vertex.toLocation(),1);
        }
        for (Vector3 vertex : yellowVertices) {
            editorView.player().spawnParticle(Particle.FLAME,vertex.toLocation(),1,0,0,0,0);
        }

        //Task render
        if(editorView.task() != null)
            editorView.task().render();

        //Entities
        nodeEntities.forEach(NodeClientEntity::updateViewers);

    }

    private void renderConnections(Node node, Set<Vector3> vertices) {

        Vector3[] originNodePosition = nodePositions(node);

        for (Node connectedNode : node.connectedNodes()) {
            ConnectionHandler handler = node.connection(connectedNode);

            //Direct Connection
            if (handler instanceof DirectConnectionHandler) {

                Vector3[] connectedNodePositions = nodePositions(connectedNode);

                vertices.addAll(InterpolationUtil.lineVertices(originNodePosition[0], connectedNodePositions[0], 15));
                vertices.addAll(InterpolationUtil.lineVertices(originNodePosition[1], connectedNodePositions[1], 15));
            }

            //Junction Connection
            if (handler instanceof JunctionConnectionHandler) {
                vertices.addAll(InterpolationUtil.lineVertices(node.pos(), connectedNode.pos(), 15));
            }

        }

    }

    private Vector3[] nodePositions(Node node) {

        Vector3[] vector3Array = new Vector3[2];

        ConnectionHandler handler = ConnectionHandler.ConnectionHandlerType.DIRECT_CONNECTION_HANDLER.instance();
        vector3Array[0] = handler.targetPos(null, node, null, 0);
        vector3Array[1] = handler.targetPos(null, node, null, 1);

        return vector3Array;
    }

    //Registry


}
