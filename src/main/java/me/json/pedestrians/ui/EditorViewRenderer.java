package me.json.pedestrians.ui;

import me.json.pedestrians.Main;
import me.json.pedestrians.objects.framework.path.Node;
import me.json.pedestrians.objects.framework.path.connection.ConnectionHandler;
import me.json.pedestrians.objects.framework.path.connection.DirectConnectionHandler;
import me.json.pedestrians.objects.framework.path.connection.JunctionConnectionHandler;
import me.json.pedestrians.utils.InterpolationUtil;
import me.json.pedestrians.utils.Vector3;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class EditorViewRenderer extends BukkitRunnable {

    private final EditorView editorView;

    public EditorViewRenderer(EditorView editorView) {
        this.editorView = editorView;

        start();
    }

    private void start() {
        this.runTaskTimerAsynchronously(Main.plugin(), 1, 1);
    }

    public void stop() {
        this.stop();
    }

    @Override
    public void run() {

        Set<Vector3> greenVertices = new HashSet<>();
        Set<Vector3> yellowVertices = new HashSet<>();

        for (Node node : editorView.pathNetwork().nodes()) {
            renderConnections(node, greenVertices);
        }

        for (Node selectedNode : editorView.selectedNodes()) {
            Vector3[] nodePositions = nodePositions(selectedNode);
            yellowVertices.addAll(InterpolationUtil.lineVertices(nodePositions[0],nodePositions[1],15));
            yellowVertices.addAll(Arrays.stream(nodePositions(selectedNode)).toList());
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

    }

    private void renderConnections(Node node, Set<Vector3> vertices) {

        Vector3[] originNodePosition = nodePositions(node);

        for (Node connectedNode : node.connectedNodes()) {
            ConnectionHandler handler = node.connection(connectedNode).connectionHandler();

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

        ConnectionHandler handler = ConnectionHandler.ConnectionHandlerEnum.DIRECT_CONNECTION_HANDLER.connectionHandler();
        vector3Array[0] = handler.targetPos(null, node, null, 0);
        vector3Array[1] = handler.targetPos(null, node, null, 1);

        return vector3Array;
    }

}
