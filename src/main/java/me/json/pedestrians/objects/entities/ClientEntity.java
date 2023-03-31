package me.json.pedestrians.objects.entities;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import me.json.pedestrians.Main;
import me.json.pedestrians.Preferences;
import me.json.pedestrians.utils.EntityIDUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.*;

public abstract class ClientEntity {

    private static final int entitySpawnRadius = Preferences.SPAWN_RADIUS;
    private static final int entitySpawnRadiusSquared = entitySpawnRadius*entitySpawnRadius;

    protected final int entityID = EntityIDUtil.newEntityID();
    protected final Set<Player> viewers = new HashSet<>();
    protected Location location;

    public ClientEntity(Location location) {
        this.location = location;
        Registry.register(this);
    }

    //Functions
    public void updateViewers() {

        Set<Player> processedPlayers = new HashSet<>();

        //Are there nonViewers who should be viewing?
        List<Player> nonViewers = location.getWorld().getPlayers();
        nonViewers.removeAll(viewers);

        for (Player player : nonViewers) {

            if(!mayView(player)) continue;

            if(shouldBeSpawned(player)) {
                spawn(player);
                viewers.add(player);
                processedPlayers.add(player);
            }
        }

        //Are there viewers who should not be viewing?
        Set<Player> nonProcessedViewers = new HashSet<>(this.viewers);
        nonProcessedViewers.removeAll(processedPlayers);

        for (Player player : nonProcessedViewers) {

            if(!shouldBeSpawned(player)) {
                despawn(player);
                viewers.remove(player);
            }
        }

    }

    public void move(Location to) {

        if(viewers.isEmpty()) {
            this.location = to;
            return;
        }

        short dx = (short)((int)((to.getX() - location.getX()) * 4096.0D));
        short dy = (short)((int)((to.getY() - location.getY()) * 4096.0D));
        short dz = (short)((int)((to.getZ() - location.getZ()) * 4096.0D));

        PacketContainer[] packets = movePackets(dx, dy, dz);

        //
        this.location = to;
        broadcastToViewers(packets);

    }

    public void remove() {
        viewers.forEach(this::despawn);
        viewers.clear();
        Registry.remove(entityID);
    }

    protected void sendDelayedPacketToViewer(Player v, PacketContainer packet, long delay) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.plugin(), () -> {

            if(Main.plugin().isEnabled())
                ProtocolLibrary.getProtocolManager().sendServerPacket(v, packet);

        }, delay);
    }

    private boolean shouldBeSpawned(Player player) {

        double distance;

        try {
            distance = player.getLocation().distanceSquared(this.location);
        } catch (IllegalArgumentException ex) {
            return false;
        }

        return distance <= entitySpawnRadiusSquared;
    }

    private void spawn(Player player) {
        sendPacketsToViewer(player, spawnPackets(player));
    }

    private void despawn(Player player) {
        sendPacketsToViewer(player, despawnPackets());
    }

    private PacketContainer[] despawnPackets() {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        List<Integer> entityIDs = new ArrayList<>();
        entityIDs.add(entityID);
        packet.getIntLists().write(0, entityIDs);
        return new PacketContainer[]{packet};
    }

    protected void broadcastToViewers(PacketContainer... packets) {

        viewers.forEach(v -> {

            for (PacketContainer packet : packets) {
                if(packet == null) continue;
                ProtocolLibrary.getProtocolManager().sendServerPacket(v, packet);
            }

        });
    }

    protected void sendPacketsToViewer(Player v, PacketContainer... packets) {

        for (PacketContainer packet : packets) {
            if(packet == null) continue;
            ProtocolLibrary.getProtocolManager().sendServerPacket(v, packet);
        }

    }

    public boolean isViewer(Player player) {
        return viewers.contains(player);
    }

    //Abstract
    protected abstract PacketContainer[] spawnPackets(Player player);
    protected abstract PacketContainer[] movePackets(short dx, short dy, short dz);
    protected abstract boolean mayView(Player player);

    //Getters
    public int entityID() {
        return entityID;
    }

    //Registry
    public static class Registry {

        private static final Map<Integer, ClientEntity> entities = new HashMap<>();

        private static void register(ClientEntity clientEntity) {
            entities.put(clientEntity.entityID, clientEntity);
        }

        public static boolean exists(Integer entityID) {
            return entities.containsKey(entityID);
        }

        public static void remove(Integer entityID) {
            entities.remove(entityID);
        }

        @Nullable
        public static ClientEntity clientEntity(Integer entityID) {
            return entities.getOrDefault(entityID, null);
        }

    }

}
