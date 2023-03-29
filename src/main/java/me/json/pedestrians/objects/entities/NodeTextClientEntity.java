package me.json.pedestrians.objects.entities;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import me.json.pedestrians.utils.RotationUtil;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NodeTextClientEntity extends ClientEntity {

    private final NodeClientEntity nodeClientEntity;
    private String text = "";

    public NodeTextClientEntity(Location location, NodeClientEntity nodeClientEntity) {
        super(location);
        this.nodeClientEntity = nodeClientEntity;
    }

    public void text(String text) {
        this.text = text;
        this.broadcastToViewers(metadataPacket(text));
    }

    @Override
    protected PacketContainer[] spawnPackets(Player player) {

        PacketContainer[] packets = new PacketContainer[2];

        //0.
        packets[0] = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
        packets[0].getIntegers().write(0, this.entityID);
        packets[0].getUUIDs().write(0, UUID.randomUUID());
        packets[0].getEntityTypeModifier().write(0, EntityType.TEXT_DISPLAY);
        packets[0].getDoubles().write(0, location.getX());
        packets[0].getDoubles().write(1, location.getY()+1.5);
        packets[0].getDoubles().write(2, location.getZ());
        packets[0].getBytes().write(0, RotationUtil.floatToByte(location.getYaw()));
        packets[0].getBytes().write(1, RotationUtil.floatToByte(location.getPitch()));

        //1.
        packets[1] = metadataPacket(text);

        //
        return packets;

    }

    @Override
    protected PacketContainer[] movePackets(short dx, short dy, short dz) {
        return new PacketContainer[0];
    }

    @Override
    protected boolean mayView(Player player) {
        return player.equals(nodeClientEntity.player());
    }

    private PacketContainer metadataPacket(String text) {

        PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        Object chat = WrappedChatComponent.fromText(text).getHandle();

        List<WrappedDataValue> wrappedDataValues = new ArrayList<>();
        wrappedDataValues.add(new WrappedDataValue(14, WrappedDataWatcher.Registry.get(Byte.class), (byte) 3));
        wrappedDataValues.add(new WrappedDataValue(22, WrappedDataWatcher.Registry.getChatComponentSerializer(), chat));

        packet.getIntegers().write(0, entityID);
        packet.getDataValueCollectionModifier().write(0, wrappedDataValues);
        return packet;
    }

}