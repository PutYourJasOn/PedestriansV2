package me.json.pedestrians.objects.entities;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import me.json.pedestrians.Main;
import me.json.pedestrians.objects.framework.path.Node;
import me.json.pedestrians.utils.RotationUtil;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class NodeClientEntity extends ClientEntity{

    private final Node node;
    private final Player player;
    private boolean glowing = false;

    public NodeClientEntity(Location location, Node node, Player player) {
        super(location);

        this.node = node;
        this.player = player;
    }

    public Node node() {
        return node;
    }

    public void glowing(boolean glowing) {

        if(this.glowing == glowing) return;
        this.glowing = glowing;

        //Packet
        if(viewers.isEmpty()) return;
        this.broadcastToViewers(metadataPacket());

    }

    @Override
    protected PacketContainer[] spawnPackets(Player player) {

        PacketContainer[] packets = new PacketContainer[4];

        //0.
        packets[0] = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
        packets[0].getIntegers().write(0, this.entityID);
        packets[0].getUUIDs().write(0, UUID.randomUUID());
        packets[0].getEntityTypeModifier().write(0, EntityType.ARMOR_STAND);
        packets[0].getDoubles().write(0, location.getX());
        packets[0].getDoubles().write(1, location.getY()-1.45);
        packets[0].getDoubles().write(2, location.getZ());
        packets[0].getBytes().write(0, RotationUtil.floatToByte(location.getYaw()));
        packets[0].getBytes().write(1, RotationUtil.floatToByte(location.getPitch()));

        //1.
        packets[1] = metadataPacket();

        //2.
        packets[2] = new PacketContainer(PacketType.Play.Server.ENTITY_EQUIPMENT);

        List<Pair<EnumWrappers.ItemSlot, ItemStack>> list = new ArrayList<>();
        list.add(new Pair<>(EnumWrappers.ItemSlot.HEAD, Main.editorViewInventory().nodeArrowHead()));

        packets[2].getIntegers().write(0, entityID);
        packets[2].getSlotStackPairLists().write(0, list);

        //3.
        packets[3] = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT);
        packets[3].getIntegers().write(0, entityID);
        packets[3].getDoubles().write(0, location.getX());
        packets[3].getDoubles().write(1, location.getY()-1.45);
        packets[3].getDoubles().write(2, location.getZ());
        packets[3].getBytes().write(0, RotationUtil.floatToByte(location.getYaw()));
        packets[3].getBytes().write(1, RotationUtil.floatToByte(location.getPitch()));
        packets[3].getBooleans().write(0, true);

        //
        return packets;
    }

    @Override
    protected PacketContainer[] movePackets(short dx, short dy, short dz) {
        return new PacketContainer[0];
    }

    @Override
    protected boolean mayView(Player player) {
        return player.equals(this.player);
    }

    private PacketContainer metadataPacket() {

        PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        Optional<?> opt = Optional.of(WrappedChatComponent.fromChatMessage("Node: "+node.id())[0].getHandle());

        List<WrappedDataValue> wrappedDataValues = new ArrayList<>();
        if(glowing) {
            wrappedDataValues.add(new WrappedDataValue(0, WrappedDataWatcher.Registry.get(Byte.class), (byte) 0b01100000));
        } else {
            wrappedDataValues.add(new WrappedDataValue(0, WrappedDataWatcher.Registry.get(Byte.class), (byte) 0b00100000));
        }

        wrappedDataValues.add(new WrappedDataValue(2, WrappedDataWatcher.Registry.getChatComponentSerializer(true), opt));
        wrappedDataValues.add(new WrappedDataValue(3, WrappedDataWatcher.Registry.get(Boolean.class), true));

        packet.getIntegers().write(0, entityID);
        packet.getDataValueCollectionModifier().write(0, wrappedDataValues);
        return packet;
    }

}
