package me.json.pedestrians.objects.framework.pedestrian;

import com.comphenix.protocol.wrappers.WrappedEnumEntityUseAction;
import me.json.pedestrians.Main;
import me.json.pedestrians.Preferences;
import me.json.pedestrians.objects.framework.events.NodeReachEvent;
import me.json.pedestrians.objects.framework.path.Node;
import me.json.pedestrians.objects.framework.path.connection.ConnectionHandler;
import me.json.pedestrians.objects.framework.path.connection.ConnectionHandler.ConnectionHandlerType;
import me.json.pedestrians.utils.InterpolationUtil;
import me.json.pedestrians.utils.Vector3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Fence;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Collection;

public abstract class Pedestrian {

    private Vector3 pos;
    private Vector3 rot = Vector3.ZERO();
    private Vector3 targetedPos; //Different from the pos of the targetedNode because of sideOffset
    private Player targetedPlayer;

    private final float sideOffset = generateRandomSideOffset();
    private float defaultVelocity = generateRandomVelocity();
    private @Nullable Float forcedVelocity = null;

    private Node targetNode1;
    private Node targetNode2;

    private boolean collision = false;

    private boolean colliding = false;
    private @Nullable Long pauseDelay;

    public Pedestrian(Node originNode) {
        this.pos = ConnectionHandlerType.DIRECT_CONNECTION_HANDLER.instance().targetPos(null, originNode, null, sideOffset);

        Node targetNode1 = originNode.generateNextNode(originNode);
        updateNode(originNode, targetNode1, targetNode1.generateNextNode(originNode));
    }

    //Getters
    protected Location location() {
        Location location = new Location(Main.world(), pos.x(), pos.y(), pos.z());
        location.setYaw((float) rot.x());
        location.setPitch((float) rot.y());
        return location;
    }

    protected float defaultVelocity() {
        return defaultVelocity;
    }

    protected float velocity() {
        return hasForcedVelocity() ? forcedVelocity : defaultVelocity;
    }

    public Vector3 pos() {
        return pos;
    }

    //Getters
    public boolean collision() {
        return collision;
    }

    public boolean hasForcedVelocity() {
        return this.forcedVelocity != null;
    }

    @Nullable
    public Float forcedVelocity() {
        return this.forcedVelocity;
    }

    protected void targetedPlayer(Player player) {
        this.targetedPlayer = player;
    }

    public void collision(boolean canCollide) {
        this.collision = canCollide;
    }

    public void forcedVelocity(@Nullable Float forcedVelocity) {
        this.forcedVelocity = forcedVelocity;
    }

    //Abstract
    protected abstract double hitboxRadius();
    protected abstract void move(Location location);
    public abstract void remove();
    public abstract void interact(Player player, WrappedEnumEntityUseAction entityUseAction);

    //Functionality
    public void tick() {

        Location newLoc;

        if(targetedPlayer != null && velocity() == 0) {
            newLoc = updateTargetDirection();
        } else {
            newLoc = updatePosition();
        }

        move(newLoc);
    }

    private Location updatePosition() {

        if(collision() && hasPedestriansInFront()) {
            colliding = true;
            return location();
        } else if(colliding) {
            colliding = false;
            pauseDelay = System.currentTimeMillis() + 1000L;
        }

        if(pauseDelay != null) {
            if(System.currentTimeMillis() < pauseDelay) {
                return location();
            } else {
                pauseDelay = null;
            }
        }

        Vector3 directionToTarget = targetedPos.clone().subtract(pos);
        float distance = directionToTarget.magnitude();

        if (distance <= Preferences.PEDESTRIAN_REACHING_DISTANCE) {
            //Node reached!
            handleNodeReach();
        }

        //
        Location targetLocation = new Location(Main.world(),targetedPos.x(),targetedPos.y(),targetedPos.z()).setDirection(directionToTarget.toBukkitVector());

        float targetYaw = targetLocation.getYaw();
        if(targetedPlayer != null) {
            targetYaw = targetLocation.setDirection(targetedPlayer.getLocation().toVector().subtract(pos.toBukkitVector())).getYaw();
        }

        targetLocation.setYaw(InterpolationUtil.floatAngleLerp((float) rot.x(), targetYaw, Preferences.PEDESTRIAN_ROTATION_VELOCITY));
        Vector3 velocity = new Vector3(targetLocation.getDirection()).multiply(velocity());

        //
        pos.add(velocity);
        rot.x(targetLocation.getYaw());
        rot.y(targetLocation.getPitch());

        Location loc = location();
        loc.setY(groundHeightLock(loc));
        return loc;
    }

    private void handleNodeReach() {

        NodeReachEvent nodeReachEvent = new NodeReachEvent(this, targetNode1);
        Bukkit.getPluginManager().callEvent(nodeReachEvent);

        Node targetNode1 = nodeReachEvent.getFirstForcedTargetNode() != null ? nodeReachEvent.getFirstForcedTargetNode() : this.targetNode2;
        Node targetNode2 = nodeReachEvent.getSecondForcedTargetNode() != null ? nodeReachEvent.getSecondForcedTargetNode() : this.targetNode2.generateNextNode(this.targetNode1);

        updateNode(this.targetNode1, targetNode1, targetNode2);
    }

    private boolean hasPedestriansInFront() {

        double radians = Math.toRadians(rot.x());
        double xOffset = -Math.sin(radians);
        double zOffset = Math.cos(radians);

        Vector3 front = pos.clone();
        front.add(new Vector3(xOffset * hitboxRadius(), 0 ,zOffset * hitboxRadius()));

        Collection<Pedestrian> closePedestrians = targetNode1.pathNetwork().getClosePedestrians(front, hitboxRadius());
        closePedestrians.remove(this);
        return !closePedestrians.isEmpty();
    }

    private Location updateTargetDirection() {

        Location loc = location();

        float targetYaw = loc.setDirection(targetedPlayer.getLocation().toVector().subtract(pos.toBukkitVector())).getYaw();
        loc.setYaw(InterpolationUtil.floatAngleLerp((float) rot.x(), targetYaw, Preferences.PEDESTRIAN_ROTATION_VELOCITY));

        rot.x(loc.getYaw());
        rot.y(loc.getPitch());

        loc.setY(groundHeightLock(loc));
        return loc;
    }

    //Height
    private double groundHeightLock(Location loc) {

        int blockY = (int) Math.round(loc.getY());
        Block mainBlock = loc.getWorld().getBlockAt(loc.getBlockX(), blockY, loc.getBlockZ());

        if(mainBlock.isPassable()) {

            Block block = loc.getWorld().getBlockAt(loc.getBlockX(), blockY-1, loc.getBlockZ());

            if(block.isPassable()) {
                return blockY-1;
            } else if(isTopTrapdoor(block)) {
                return blockY;
            } else {
                return blockY - (1-block.getBoundingBox().getHeight());
            }

        } else {

            Block block = loc.getWorld().getBlockAt(loc.getBlockX(), blockY+1, loc.getBlockZ());

            if(block.isPassable() && !isFence(mainBlock)) {
                return blockY + mainBlock.getBoundingBox().getHeight();
            } else {
                return blockY;
            }

        }

    }

    private boolean isFence(Block block) {
        return block.getBlockData() instanceof Fence;
    }

    private boolean isTopTrapdoor(Block block) {
        return block.getBlockData() instanceof TrapDoor && ((TrapDoor)block.getBlockData()).getHalf() == Bisected.Half.TOP;
    }

    //When targetedNode gets reached
    private void updateNode(Node originNode, Node targetNode1, Node targetNode2) {
        this.targetNode1 = targetNode1;
        this.targetNode2 = targetNode2;

        //Connection Handling
        ConnectionHandler originToTarget1 = originNode.connection(targetNode1);
        ConnectionHandler target1ToTarget2 = targetNode1.connection(targetNode2);

        if(target1ToTarget2 != null && target1ToTarget2.hasToPrepare()) {
            targetedPos = target1ToTarget2.targetPos(originNode, targetNode1, targetNode2, sideOffset);
            return;
        }

        if(originToTarget1 != null && originToTarget1.hasToPrepare()) {
            ConnectionHandler directHandler = ConnectionHandlerType.DIRECT_CONNECTION_HANDLER.instance();
            targetedPos = directHandler.targetPos(originNode, targetNode1, targetNode2, sideOffset);
            return;
        }

        if(originToTarget1 != null) {
            targetedPos = originToTarget1.targetPos(originNode, targetNode1, targetNode2, sideOffset);
            return;
        }

        targetedPos = originNode.pos();

    }

    private float generateRandomVelocity() {
        return (float) (Preferences.PEDESTRIAN_MIN_VELOCITY + Math.random() * (Preferences.PEDESTRIAN_MAX_VELOCITY-Preferences.PEDESTRIAN_MIN_VELOCITY));
    }

    private float generateRandomSideOffset() {
        return (float) Math.random();
    }

}