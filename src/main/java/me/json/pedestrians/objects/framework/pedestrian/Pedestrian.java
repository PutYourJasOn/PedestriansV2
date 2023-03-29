package me.json.pedestrians.objects.framework.pedestrian;

import me.json.pedestrians.Main;
import me.json.pedestrians.Preferences;
import me.json.pedestrians.objects.framework.path.Node;
import me.json.pedestrians.objects.framework.path.PathNetwork;
import me.json.pedestrians.objects.framework.path.connection.ConnectionHandler;
import me.json.pedestrians.objects.framework.path.connection.ConnectionHandler.ConnectionHandlerType;
import me.json.pedestrians.utils.InterpolationUtil;
import me.json.pedestrians.utils.Vector3;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Pedestrian {

    private final PathNetwork pathNetwork;
    private final PedestrianEntity pedestrianEntity;

    private Vector3 pos;
    private Vector3 rot = Vector3.ZERO();
    private Vector3 targetedPos; //Different from the pos of the targetedNode because of sideOffset
    private Player targetedPlayer;

    private final float sideOffset = generateRandomSideOffset();
    private float velocity = generateRandomVelocity();

    private Node targetNode1;
    private Node targetNode2;

    public Pedestrian(PathNetwork pathNetwork, PedestrianEntity pedestrianEntity, Node originNode) {
        this.pos= ConnectionHandlerType.DIRECT_CONNECTION_HANDLER.instance().targetPos(null, originNode, null, sideOffset);
        this.pathNetwork = pathNetwork;
        this.pedestrianEntity = pedestrianEntity.initialize(this).spawn(location());

        Node targetNode1 = originNode.generateNextNode(originNode);
        updateNode(originNode, targetNode1, targetNode1.generateNextNode(originNode));
    }

    //Getters
    private Location location() {
        Location location = new Location(Main.world(), pos.x(), pos.y(), pos.z());
        location.setYaw((float) rot.x());
        location.setPitch((float) rot.y());
        return location;
    }

    public float velocity() {
        return velocity;
    }

    public Vector3 pos() {
        return pos;
    }

    //Setters
    public void velocity(float velocity) {
        this.velocity = velocity;
    }

    public void targetedPlayer(Player player) {
        this.targetedPlayer = player;
    }

    //Functionality
    public void tick() {

        if(targetedPlayer != null && velocity == 0) {
            targetPlayer();
            return;
        }

        move();

    }

    private void move() {

        Vector3 directionToTarget = targetedPos.clone().subtract(pos);
        float distance = directionToTarget.magnitude();

        if (distance <= Preferences.PEDESTRIAN_REACHING_DISTANCE) {
            //Node reached!
            updateNode(targetNode1, targetNode2, targetNode2.generateNextNode(targetNode1));
        }

        //
        Location targetLocation = new Location(Main.world(),targetedPos.x(),targetedPos.y(),targetedPos.z()).setDirection(directionToTarget.toBukkitVector());

        float targetYaw = targetLocation.getYaw();
        if(targetedPlayer != null) {
            targetYaw = targetLocation.setDirection(targetedPlayer.getLocation().toVector().subtract(pos.toBukkitVector())).getYaw();
        }

        targetLocation.setYaw(InterpolationUtil.floatAngleLerp((float) rot.x(), targetYaw, Preferences.PEDESTRIAN_ROTATION_VELOCITY));
        Vector3 velocity = new Vector3(targetLocation.getDirection()).multiply(this.velocity);

        //
        pos.add(velocity);
        rot.x(targetLocation.getYaw());
        rot.y(targetLocation.getPitch());

        Location loc = location();
        loc.setY(groundHeightLock(loc));

        //Update to entity
        pedestrianEntity.asyncMove(loc);
    }

    private void targetPlayer() {

        Location loc = location();

        float targetYaw = loc.setDirection(targetedPlayer.getLocation().toVector().subtract(pos.toBukkitVector())).getYaw();
        loc.setYaw(InterpolationUtil.floatAngleLerp((float) rot.x(), targetYaw, Preferences.PEDESTRIAN_ROTATION_VELOCITY));

        rot.x(loc.getYaw());
        rot.y(loc.getPitch());

        loc.setY(groundHeightLock(loc));
        pedestrianEntity.asyncMove(loc);
    }

    public void remove() {
        pedestrianEntity.remove();
    }

    //Height
    private double groundHeightLock(Location loc) {

        int blockY = (int) Math.round(loc.getY());
        Block mainBlock = loc.getWorld().getBlockAt(loc.getBlockX(), blockY, loc.getBlockZ());

        if(mainBlock.isPassable()) {

            Block block = loc.getWorld().getBlockAt(loc.getBlockX(), blockY-1, loc.getBlockZ());

            if(block.isPassable()) {
                return blockY-1;
            } else {
                return blockY - (1-block.getBoundingBox().getHeight());
            }

        } else {

            Block block = loc.getWorld().getBlockAt(loc.getBlockX(), blockY+1, loc.getBlockZ());

            if(block.isPassable()) {
                return blockY + mainBlock.getBoundingBox().getHeight();
            } else {
                return blockY;
            }

        }

    }

    //When targetedNode gets reached
    private void updateNode(Node originNode, Node targetNode1, Node targetNode2) {
        this.targetNode1 = targetNode1;
        this.targetNode2 = targetNode2;

        //Connection Handling
        ConnectionHandler originToTarget1 = originNode.connection(targetNode1);
        ConnectionHandler target1ToTarget2 = targetNode1.connection(targetNode2);

        if(target1ToTarget2.hasToPrepare()) {
            targetedPos = target1ToTarget2.targetPos(originNode, targetNode1, targetNode2, sideOffset);
            return;
        }

        if(originToTarget1.hasToPrepare()) {
            ConnectionHandler directHandler = ConnectionHandlerType.DIRECT_CONNECTION_HANDLER.instance();
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