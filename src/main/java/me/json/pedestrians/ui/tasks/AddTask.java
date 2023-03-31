package me.json.pedestrians.ui.tasks;

import me.json.pedestrians.Main;
import me.json.pedestrians.Messages;
import me.json.pedestrians.objects.entities.NodeClientEntity;
import me.json.pedestrians.objects.framework.path.Node;
import me.json.pedestrians.objects.framework.path.connection.ConnectionHandler;
import me.json.pedestrians.objects.framework.path.connection.DirectConnectionHandler;
import me.json.pedestrians.ui.EditorView;
import me.json.pedestrians.utils.InterpolationUtil;
import me.json.pedestrians.utils.RayTraceUtil;
import me.json.pedestrians.utils.Vector3;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class AddTask implements ITask {

    private EditorView editorView;

    private boolean preciseMode = false;
    private NodeClientEntity selectedNodeEntity;

    private Vector3 pos;
    private Vector3 dir;
    private double width = 8;

    @Override
    public void init(EditorView editorView) {
        this.editorView = editorView;
    }

    @Override
    public void stop() {
        if(selectedNodeEntity != null)
            selectedNodeEntity.glowing(false);
    }

    @Override
    public void onRightClickNode(NodeClientEntity nodeEntity) {
        handleNodeClick(nodeEntity);
    }

    @Override
    public void onLeftClickNode(NodeClientEntity nodeEntity) {
        handleNodeClick(nodeEntity);
    }

    private void handleNodeClick(NodeClientEntity nodeEntity) {

        if(selectedNodeEntity == nodeEntity) {
            selectedNodeEntity.glowing(false);
            this.selectedNodeEntity = null;
            return;
        }

        if(selectedNodeEntity != null)
            selectedNodeEntity.glowing(false);

        selectedNodeEntity = nodeEntity;
        nodeEntity.glowing(true);
        Node node = nodeEntity.node();
        editorView.player().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(String.format(Messages.NODE_SELECTED,node.id())));
    }

    @Override
    public void onRightClick() {

        if(pos == null) {

            Vector raytrace;

            if(preciseMode) {
                raytrace = RayTraceUtil.rayTraceResult(editorView.player());
            }else{
                raytrace = RayTraceUtil.roundedRayTraceResult(editorView.player());
            }

            if(raytrace == null) return;

            pos = new Vector3(raytrace);
            editorView.player().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(String.format(Messages.FIELD_SET, "Position")));

            return;
        }

        if(dir == null) {

            this.dir = direction(editorView.player(), preciseMode);
            editorView.player().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(String.format(Messages.FIELD_SET, "Direction")));

            return;
        }

        //Create
        Node node = new Node(editorView.pathNetwork().nextNodeID(), pos, width, dir);

        editorView.pathNetwork().addNode(node);
        NodeClientEntity nodeClientEntity =  editorView.editorViewRenderer().spawnNodeEntity(node);

        if(selectedNodeEntity != null) {
            Node connectedNode = selectedNodeEntity.node();
            connectedNode.registerConnectedNode(node, ConnectionHandler.ConnectionHandlerType.DIRECT_CONNECTION_HANDLER.instance());
            node.registerConnectedNode(connectedNode, ConnectionHandler.ConnectionHandlerType.DIRECT_CONNECTION_HANDLER.instance());

            selectedNodeEntity.glowing(false);
            selectedNodeEntity = nodeClientEntity;
            selectedNodeEntity.glowing(true);
        }

        editorView.editorViewRenderer().updateNodeTexts();
        editorView.player().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Messages.NODE_CREATED));

        //Reset
        pos = null;
        dir = null;
        width = 8;

    }

    @Override
    public void onLeftClick() {
        preciseMode = !preciseMode;
        editorView.player().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(String.format(Messages.PRECISE_MODE_TOGGLE, preciseMode)));
    }

    @Override
    public void onScroll(int scrollDirection) {

        if(dir == null) return;

        double newWidth;
        if(preciseMode) {
            newWidth = width+scrollDirection*0.5;
        }else {
            newWidth = width+scrollDirection*2;
        }

        if(newWidth > 0)
            width = newWidth;

    }

    @Override
    public boolean scrollLock() {
        return dir != null;
    }

    @Override
    public void render() {

        //pos
        if(pos == null) {

            Vector raytrace;

            if(preciseMode) {
                raytrace = RayTraceUtil.rayTraceResult(editorView.player());
            }else{
                raytrace = RayTraceUtil.roundedRayTraceResult(editorView.player());
            }

            if(raytrace != null)
                editorView.player().spawnParticle(Particle.END_ROD,raytrace.toLocation(Main.world()),1, 0, 0, 0, 0);

            return;
        }

        //dir
        if(dir == null) {
            editorView.player().spawnParticle(Particle.ELECTRIC_SPARK, pos.toLocation(),1, 0, 0, 0, 0);

            Vector3[] positions = nodePositions(pos, direction(editorView.player(), preciseMode), width);
            Set<Vector3> vertices = new HashSet<>(InterpolationUtil.lineVertices(positions[0], positions[1], 15));

            vertices.forEach(v -> editorView.player().spawnParticle(Particle.COMPOSTER, v.toLocation(), 1));
            return;
        }

        //width
        if(true) {
            editorView.player().spawnParticle(Particle.ELECTRIC_SPARK, pos.toLocation(), 1, 0, 0, 0, 0);

            Vector3[] positions = nodePositions(pos, dir, width);
            Set<Vector3> vertices = new HashSet<>(InterpolationUtil.lineVertices(positions[0], positions[1], 15));

            vertices.forEach(v -> editorView.player().spawnParticle(Particle.COMPOSTER, v.toLocation(), 1));
            editorView.player().spawnParticle(Particle.ELECTRIC_SPARK, positions[0].toLocation().add(0,0.4,0),1, 0, 0, 0, 0);
            editorView.player().spawnParticle(Particle.ELECTRIC_SPARK,  positions[1].toLocation().add(0,0.4,0),1, 0, 0, 0, 0);
        }

    }

    private Vector3[] nodePositions(Vector3 pos, Vector3 dir, double width) {

        Vector3[] vector3Array = new Vector3[2];

        dir.y(0);

        DirectConnectionHandler handler = (DirectConnectionHandler) ConnectionHandler.ConnectionHandlerType.DIRECT_CONNECTION_HANDLER.instance();
        vector3Array[0] = handler.targetPos(pos, dir, width, 0);
        vector3Array[1] = handler.targetPos(pos, dir, width, 1);

        return vector3Array;
    }

    private Vector3 direction(Player player, boolean noRounding) {
        float yaw = player.getEyeLocation().getYaw();

        if(!noRounding) {
            float fraction = 1 / 22.5f;
            yaw = Math.round(yaw * fraction) / fraction;
        }

        Location newLoc = new Location(Main.world(), 0,0,0);
        newLoc.setYaw(yaw);
        return new Vector3(newLoc.getDirection());
    }

}
