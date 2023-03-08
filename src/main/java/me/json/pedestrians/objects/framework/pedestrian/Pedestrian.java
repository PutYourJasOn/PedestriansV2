package me.json.pedestrians.objects.framework.pedestrian;

import me.json.pedestrians.Main;
import me.json.pedestrians.Preferences;
import me.json.pedestrians.objects.framework.path.Node;
import me.json.pedestrians.objects.framework.path.PathNetwork;
import me.json.pedestrians.objects.framework.path.connection.ConnectionHandler;
import me.json.pedestrians.objects.framework.path.connection.ConnectionHandler.ConnectionHandlerEnum;
import me.json.pedestrians.utils.InterpolationUtil;
import me.json.pedestrians.utils.Vector3;
import org.bukkit.Location;

public class Pedestrian {

    private Vector3 pos;
    private Vector3 rot = Vector3.ZERO();
    private Vector3 targetedPos; //Different from the pos of the targetedNode because of sideOffset

    private final float velocity = generateRandomVelocity();
    private final float sideOffset = generateRandomSideOffset();

    private Node targetNode1;
    private Node targetNode2;

    private final PathNetwork pathNetwork;
    private final PedestrianEntity pedestrianEntity;

    public Pedestrian(PathNetwork pathNetwork, PedestrianEntity pedestrianEntity, Node originNode) {
        this.pos=ConnectionHandlerEnum.DIRECT_CONNECTION_HANDLER.connectionHandler().targetPos(null, originNode, null, sideOffset);
        this.pathNetwork = pathNetwork;
        this.pedestrianEntity = pedestrianEntity.initialize(this).spawn(location());

        pathNetwork.addPedestrian(this);
        Node targetNode1 = originNode.generateNextNode(originNode);
        updateNode(originNode, targetNode1, targetNode1.generateNextNode(originNode));
    }

    public Pedestrian(PathNetwork pathNetwork, PedestrianEntity pedestrianEntity, Node originNode, Node targetedNode) {
        this.pos=ConnectionHandlerEnum.DIRECT_CONNECTION_HANDLER.connectionHandler().targetPos(null, originNode, null, sideOffset);
        this.pathNetwork = pathNetwork;
        this.pedestrianEntity = pedestrianEntity.initialize(this).spawn(location());

        pathNetwork.addPedestrian(this);
        updateNode(originNode, targetedNode, targetedNode.generateNextNode(originNode));
    }

    private Location location() {
        Location location = new Location(Main.world(), pos.x(), pos.y(), pos.z());
        location.setYaw((float) rot.x());
        location.setPitch((float) rot.y());
        return location;
    }

    //Functionality
    public void move() {

        Vector3 directionToTarget = targetedPos.clone().subtract(pos);
        float distance = directionToTarget.magnitude();

        if (distance <= Preferences.PEDESTRIAN_REACHING_DISTANCE) {
            //Node reached!
            updateNode(targetNode1, targetNode2, targetNode2.generateNextNode(targetNode1));
        }

        Location targetLocation = new Location(Main.world(),targetedPos.x(),targetedPos.y(),targetedPos.z()).setDirection(directionToTarget.toBukkitVector());
        targetLocation.setYaw(InterpolationUtil.floatAngleLerp((float) rot.x(), targetLocation.getYaw(), Preferences.PEDESTRIAN_ROTATION_VELOCITY));

        Vector3 velocity = new Vector3(targetLocation.getDirection()).multiply(this.velocity);

        pos.add(velocity);
        rot.x(targetLocation.getYaw());
        rot.y(targetLocation.getPitch());

        //Update to entity
        pedestrianEntity.asyncMove(location());
    }

    public void remove() {
        pedestrianEntity.remove();
        pathNetwork.removePedestrian(this);
    }

    //When targetedNode gets reached
    private void updateNode(Node originNode, Node targetNode1, Node targetNode2) {
        this.targetNode1 = targetNode1;
        this.targetNode2 = targetNode2;

        //Connection Handling
        ConnectionHandler originToTarget1 = originNode.connection(targetNode1).connectionHandler();
        ConnectionHandler target1ToTarget2 = targetNode1.connection(targetNode2).connectionHandler();

        if(target1ToTarget2.hasToPrepare()) {
            targetedPos = target1ToTarget2.targetPos(originNode, targetNode1, targetNode2, sideOffset);
            return;
        }

        if(originToTarget1.hasToPrepare()) {
            ConnectionHandler directHandler = ConnectionHandler.ConnectionHandlerEnum.DIRECT_CONNECTION_HANDLER.connectionHandler();
            targetedPos = directHandler.targetPos(originNode, targetNode1, targetNode2, sideOffset);
            return;
        }

        targetedPos = originToTarget1.targetPos(originNode, targetNode1, targetNode2, sideOffset);

    }

    private float generateRandomVelocity() {
        return (float) (Preferences.PEDESTRIAN_MIN_VELOCITY + Math.random() * (Preferences.PEDESTRIAN_MAX_VELOCITY-Preferences.PEDESTRIAN_MIN_VELOCITY));
    }

    private float generateRandomSideOffset() {
        return (float) Math.random();
    }

}