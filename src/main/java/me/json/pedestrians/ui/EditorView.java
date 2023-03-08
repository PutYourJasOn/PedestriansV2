package me.json.pedestrians.ui;

import me.json.pedestrians.Main;
import me.json.pedestrians.objects.framework.path.Node;
import me.json.pedestrians.objects.framework.path.PathNetwork;
import me.json.pedestrians.objects.framework.path.connection.Connection;
import me.json.pedestrians.objects.framework.path.connection.ConnectionHandler;
import me.json.pedestrians.objects.framework.path.connection.DirectConnectionHandler;
import me.json.pedestrians.objects.framework.path.connection.JunctionConnectionHandler;
import me.json.pedestrians.utils.InterpolationUtil;
import me.json.pedestrians.utils.Vector3;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class EditorView extends BukkitRunnable {

    private final PathNetwork pathNetwork;

    private final Set<Editor> editors = new HashSet<>();
    private final Set<ArmorStand> nodeStands = new HashSet<>();

    public EditorView(PathNetwork pathNetwork) {
        this.pathNetwork = pathNetwork;

        spawnNodeStands();
        this.runTaskTimerAsynchronously(Main.plugin(), 1L, 1L);
        Registry.register(this);
    }

    //General
    public void stop() {
        nodeStands.forEach(Entity::remove);
        editors.forEach(e -> {
            e.player().getInventory().setContents(e.savedInventory());
        });
        this.cancel();
        Registry.remove(this);
    }

    //Editor Stuff
    public void registerEditor(Player player) {

        if(players().contains(player)) throw new IllegalArgumentException("Player is already an editor!");
        Editor editor = new Editor(this, player);

        editor.savedInventory(player.getInventory().getContents().clone());
        Main.editorViewInventory().pushToPlayer(player);

        editors.add(editor);
    }

    public PathNetwork pathNetwork() {
        return pathNetwork;
    }

    @Nullable
    public Editor editor(Player player) {
        return editors.stream().filter(e -> e.player().equals(player)).findAny().orElse(null);
    }

    public Set<Player> players() {
        return editors.stream().map(Editor::player).collect(Collectors.toSet());
    }

    public void removeEditor(Player player) {

        handleNotAnEditor(player);
        Editor editor = editor(player);

        player.getInventory().setContents(editor.savedInventory());

        editors.remove(editor);

        if(editors.size()==0) stop();
    }

    //Exception
    public void handleNotAnEditor(Player player) {
        if(!players().contains(player)) throw new IllegalArgumentException("Player is not an editor of this view!");
    }

    //Node Registry Stuff
    @Nullable
    public Node node(ArmorStand armorStand) {
        if(armorStand == null) return null;
        if(!nodeStands.contains(armorStand)) return null;

        int nodeID = Integer.parseInt(armorStand.getCustomName().replace("Node: ",""));
        return pathNetwork.node(nodeID);
    }

    @Nullable
    public ArmorStand stand(Node node) {
        if(node == null) return null;
        System.out.println(1);
        if(!pathNetwork.nodes().contains(node)) return null;
        System.out.println(2);
        return nodeStands.stream().filter(s -> Integer.parseInt(s.getCustomName().replace("Node: ",""))==node.id()).findAny().get();
    }

    //Entity Stuff
    private void spawnNodeStands() {
        pathNetwork.nodes().forEach(n -> spawnNodeArmorStand(n));
    }

    private void spawnNodeArmorStand(Node node) {

        Location location = node.pos().clone().subtract(new Vector3(0, 1.45, 0)).toLocation();
        location.setDirection(node.direction().toBukkitVector());

        ItemStack stack = new ItemStack(Main.editorViewInventory().arrowHead());

        ArmorStand stand = Main.world().spawn(location, ArmorStand.class);
        stand.setGravity(false);
        stand.setCustomName("Node: "+node.id());
        stand.setCustomNameVisible(true);
        stand.setVisible(false);
        stand.getEquipment().setHelmet(stack);

        nodeStands.add(stand);
    }

    //Edit Functionality
    public Node addNode(Vector3 pos, Vector3 dir, double width) {
        Node node = new Node(pathNetwork.nextNodeID(), pos, width, dir);
        pathNetwork.addNode(node);
        spawnNodeArmorStand(node);
        return node;
    }

    public Node addNode(Vector3 pos, Vector3 dir, double width, Node selectedNode) {
        Node node = new Node(pathNetwork.nextNodeID(), pos, width, dir);

        pathNetwork.addNode(node);
        spawnNodeArmorStand(node);

        node.registerConnectedNode(selectedNode, new Connection(50, ConnectionHandler.ConnectionHandlerEnum.DIRECT_CONNECTION_HANDLER.connectionHandler()));
        selectedNode.registerConnectedNode(node, new Connection(50, ConnectionHandler.ConnectionHandlerEnum.DIRECT_CONNECTION_HANDLER.connectionHandler()));

        return node;
    }

    public void removeNode(Node node) {

        ArmorStand stand = stand(node);

        if(stand != null) {
            stand.getEquipment().clear();
            stand.remove();
        }

        pathNetwork.removeNode(node);
        nodeStands.remove(stand);
    }

    public void removeConnection(Editor editor, Node targetNode) {
        editor.selectedNode().removeConnection(targetNode);
    }

    public void addConnection(Editor editor, Node targetNode, ConnectionHandler connectionHandler, int probability) {
        if(editor.selectedNode().connectedNodes().contains(targetNode)) return;
        editor.selectedNode().registerConnectedNode(targetNode, new Connection(probability, connectionHandler));
    }

    //Rendering
    @Override
    public void run() {

        for (Editor editor : editors) {

            Set<Vector3> greenVertices = new HashSet<>();
            Set<Vector3> yellowVertices = new HashSet<>();

            if(editor.selectedNode() == null) {
                //Global
                for (Node node : pathNetwork.nodes()) {
                    renderConnections(node, greenVertices);
                }
            }else {
                //Selected
                Vector3[] nodePositions = nodePositions(editor.selectedNode());

                renderConnections(editor.selectedNode(), greenVertices);
                yellowVertices.addAll(InterpolationUtil.lineVertices(nodePositions[0],nodePositions[1],15));
                yellowVertices.addAll(Arrays.stream(nodePositions(editor.selectedNode())).toList());
            }

            //Show particles
            for (Vector3 vertex : greenVertices) {
                editor.player().spawnParticle(Particle.COMPOSTER,vertex.toLocation(),1);
            }
            for (Vector3 vertex : yellowVertices) {
                editor.player().spawnParticle(Particle.FLAME,vertex.toLocation(),1,0,0,0,0);
            }

        }

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

    //Registry
    public static class Registry {

        private final static Set<EditorView> editorViews = new HashSet<>();

        private static void register(EditorView editorView) {
            editorViews.add(editorView);
        }
        private static void remove(EditorView editorView) {
            editorViews.remove(editorView);
        }

        public static Set<EditorView> editorViews() {
            return new HashSet<>(editorViews);
        }

        public final static EditorView editorView(PathNetwork pathNetwork) {
            return editorViews.stream().filter(ev -> ev.pathNetwork.equals(pathNetwork)).findAny().orElse(new EditorView(pathNetwork));
        }

        @Nullable
        public static EditorView editorView(Player player) {
            return editorViews.stream().filter(ev -> ev.players().contains(player)).findAny().orElse(null);
        }

        @Nullable
        public static EditorView editorView(ArmorStand stand) {
            return editorViews.stream().filter(ev -> ev.nodeStands.contains(stand)).findAny().orElse(null);
        }

    }

}