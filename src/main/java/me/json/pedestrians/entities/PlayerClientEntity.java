package me.json.pedestrians.entities;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import me.json.pedestrians.listeners.JoinListener;
import me.json.pedestrians.objects.PlayerPedestrianEntity;
import me.json.pedestrians.utils.RotationUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerClientEntity extends ClientEntity {

    private final PlayerPedestrianEntity playerPedestrianEntity;
    private final PlayerInfoData playerInfoData;

    public PlayerClientEntity(Location location, PlayerPedestrianEntity playerPedestrianEntity, String skinBase64, String skinSignature) {
        super(location);

        this.playerPedestrianEntity = playerPedestrianEntity;

        //
        WrappedGameProfile gameProfile = new WrappedGameProfile(UUID.randomUUID(), " ");
        gameProfile.getProperties().put("textures", new WrappedSignedProperty("textures", skinBase64, skinSignature));

        playerInfoData = new PlayerInfoData(
                gameProfile,
                0,
                EnumWrappers.NativeGameMode.CREATIVE,
                WrappedChatComponent.fromText(gameProfile.getName())
        );

    }

    public PlayerPedestrianEntity playerPedestrianEntity() {
        return playerPedestrianEntity;
    }

    @Override
    public PacketContainer[] spawnPackets(Player player) {

        PacketContainer[] packets = new PacketContainer[3];

        //0.
        packets[0] = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
        packets[0].getPlayerInfoActions().write(0, EnumSet.of(EnumWrappers.PlayerInfoAction.ADD_PLAYER));
        packets[0].getPlayerInfoDataLists().write(1, Collections.singletonList(playerInfoData));

        //1.
        packets[1] = new PacketContainer(PacketType.Play.Server.NAMED_ENTITY_SPAWN);
        packets[1].getIntegers().write(0, entityID);
        packets[1].getUUIDs().write(0, playerInfoData.getProfile().getUUID());
        packets[1].getDoubles().write(0, location.getX());
        packets[1].getDoubles().write(1, location.getY());
        packets[1].getDoubles().write(2, location.getZ());
        packets[1].getBytes().write(0, RotationUtil.floatToByte(location.getYaw()));
        packets[1].getBytes().write(1, RotationUtil.floatToByte(location.getPitch()));

        //2.
        PacketContainer delayedPacket = new PacketContainer(PacketType.Play.Server.PLAYER_INFO_REMOVE);
        delayedPacket.getUUIDLists().write(0, Collections.singletonList(playerInfoData.getProfileId()));

        //3.
        packets[2] = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);

        WrappedDataWatcher dataWatcher = new WrappedDataWatcher();
        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(17, WrappedDataWatcher.Registry.get(Byte.class)), (byte) 0b01111110);

        //(1.19.3 shit ugh)
        List<WrappedDataValue> wrappedDataValues = new ArrayList<>();
        for (WrappedWatchableObject watchableObject : dataWatcher.getWatchableObjects()) {

            if (watchableObject == null) continue;

            WrappedDataWatcher.WrappedDataWatcherObject watcherObject = watchableObject.getWatcherObject();
            wrappedDataValues.add(new WrappedDataValue(watcherObject.getIndex(), watcherObject.getSerializer(), watchableObject.getRawValue()));
        }

        packets[2].getIntegers().write(0, entityID);
        packets[2].getDataValueCollectionModifier().write(0, wrappedDataValues);

        //Send
        this.sendDelayedPacketToViewer(player, delayedPacket, 10);
        return packets;


    }

    @Override
    public PacketContainer[] movePackets(short dx, short dy, short dz) {

        PacketContainer[] packets = new PacketContainer[2];

        //1.
        packets[0] = new PacketContainer(PacketType.Play.Server.REL_ENTITY_MOVE_LOOK);
        packets[0].getIntegers().write(0, entityID);
        packets[0].getShorts().write(0, dx);
        packets[0].getShorts().write(1, dy);
        packets[0].getShorts().write(2, dz);
        packets[0].getBytes().write(0, RotationUtil.floatToByte(location.getYaw()));
        packets[0].getBytes().write(1, RotationUtil.floatToByte(location.getPitch()));
        packets[0].getBooleans().write(0, true);


        //2.
        packets[1] = new PacketContainer(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
        packets[1].getIntegers().write(0, entityID);
        packets[1].getBytes().write(0, RotationUtil.floatToByte(location.getYaw()));

        return packets;
    }

    @Override
    protected boolean mayView(Player player) {
        return !JoinListener.justJoinedPlayers.contains(player);
    }

}
