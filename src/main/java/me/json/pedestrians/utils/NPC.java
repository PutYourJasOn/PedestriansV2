package me.json.pedestrians.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import me.json.pedestrians.Main;
import me.json.pedestrians.listeners.JoinListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class NPC {

    private static final int npcSpawnRadius = 35;
    private static final int npcSpawnRadiusSquared = npcSpawnRadius*npcSpawnRadius;

    private final int entityID = new Random().nextInt();
    private final Set<Player> viewers = new HashSet<>();
    private final PlayerInfoData playerInfoData;

    private Location location;

    public NPC(String name, Location location, String skinBase64, String skinSignature) {

        this.location = location;

        WrappedGameProfile gameProfile = new WrappedGameProfile(UUID.randomUUID(), name);
        gameProfile.getProperties().put("textures", new WrappedSignedProperty("textures", skinBase64, skinSignature));

        playerInfoData = new PlayerInfoData(
                gameProfile,
                0,
                EnumWrappers.NativeGameMode.CREATIVE,
                WrappedChatComponent.fromText(gameProfile.getName())
        );

        //spawning
        updateViewers();

    }

    public void updateViewers() {

        Set<Player> processedPlayers = new HashSet<>();

        //Are there nonViewers who should be viewing?
        List<Player> nonViewers = location.getWorld().getPlayers();
        nonViewers.removeAll(JoinListener.justJoinedPlayers); //To decrease wrong skin bug, don't spawn npc's to just joined players
        nonViewers.removeAll(viewers);

        for (Player player : nonViewers) {

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

        //1.
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.REL_ENTITY_MOVE_LOOK);
        packet.getIntegers().write(0, entityID);
        packet.getShorts().write(0, dx);
        packet.getShorts().write(1, dy);
        packet.getShorts().write(2, dz);
        packet.getBytes().write(0, RotationUtil.floatToByte(location.getYaw()));
        packet.getBytes().write(1, RotationUtil.floatToByte(location.getPitch()));
        packet.getBooleans().write(0, true);


        //2.
        PacketContainer packet1 = new PacketContainer(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
        packet1.getIntegers().write(0, entityID);
        packet1.getBytes().write(0, RotationUtil.floatToByte(location.getYaw()));

        //
        this.location = to;
        broadcastToViewers(packet, packet1);

    }

    public void remove() {
        viewers.forEach(v -> despawn(v));
        viewers.clear();
    }

    private void spawn(Player player) {

        //0.
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
        packet.getPlayerInfoActions().write(0, EnumSet.of(EnumWrappers.PlayerInfoAction.ADD_PLAYER));
        packet.getPlayerInfoDataLists().write(1, Collections.singletonList(playerInfoData));

        //1.
        PacketContainer packet1 = new PacketContainer(PacketType.Play.Server.NAMED_ENTITY_SPAWN);
        packet1.getIntegers().write(0, entityID);
        packet1.getUUIDs().write(0, playerInfoData.getProfile().getUUID());
        packet1.getDoubles().write(0, location.getX());
        packet1.getDoubles().write(1, location.getY());
        packet1.getDoubles().write(2, location.getZ());
        packet1.getBytes().write(0, RotationUtil.floatToByte(location.getYaw()));
        packet1.getBytes().write(1, RotationUtil.floatToByte(location.getPitch()));

        //2.
        PacketContainer packet2 = new PacketContainer(PacketType.Play.Server.PLAYER_INFO_REMOVE);
        packet2.getUUIDLists().write(0, Collections.singletonList(playerInfoData.getProfileId()));

        //3.
        PacketContainer packet3 = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);

        WrappedDataWatcher dataWatcher = new WrappedDataWatcher();
        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(17, WrappedDataWatcher.Registry.get(Byte.class)), (byte) 0b01111110);

            //(1.19.3 shit ugh)
            List<WrappedDataValue> wrappedDataValues = new ArrayList<>();
            for (WrappedWatchableObject watchableObject : dataWatcher.getWatchableObjects()) {

                if(watchableObject == null) continue;

                WrappedDataWatcher.WrappedDataWatcherObject watcherObject = watchableObject.getWatcherObject();
                wrappedDataValues.add(new WrappedDataValue(watcherObject.getIndex(), watcherObject.getSerializer(), watchableObject.getRawValue()));
            }

        packet3.getIntegers().write(0, entityID);
        packet3.getDataValueCollectionModifier().write(0, wrappedDataValues);

        //Send
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet1);
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet3);

        sendPacketLater(player, packet2, 10);

    }

    private void sendPacketLater(Player player, PacketContainer packet, long delay) {

        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.plugin(), () -> {

            if(Main.plugin().isEnabled())
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);

        }, delay);

    }

    private void despawn(Player player) {

        PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        List<Integer> entityIDs = new ArrayList<>();
        entityIDs.add(entityID);
        packet.getIntLists().write(0, entityIDs);

        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
    }

    private void broadcastToViewers(PacketContainer... packets) {

        viewers.forEach(v -> {

            for (PacketContainer packet : packets) {
                ProtocolLibrary.getProtocolManager().sendServerPacket(v, packet);
            }

        });
    }

    private boolean shouldBeSpawned(Player player) {

        double distance;

        try {
            distance = player.getLocation().distanceSquared(this.location);
        } catch (IllegalArgumentException ex) {
            return false;
        }

        return distance <= npcSpawnRadiusSquared;
    }

}
