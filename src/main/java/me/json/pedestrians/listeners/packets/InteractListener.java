package me.json.pedestrians.listeners.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.wrappers.WrappedEnumEntityUseAction;
import me.json.pedestrians.Main;
import me.json.pedestrians.objects.PlayerPedestrianEntity;
import org.bukkit.plugin.Plugin;

public class InteractListener implements PacketListener {

    @Override
    public void onPacketSending(PacketEvent packetEvent) {

    }

    @Override
    public void onPacketReceiving(PacketEvent e) {

        int id = e.getPacket().getIntegers().read(0);
        PlayerPedestrianEntity pedestrianEntity = PlayerPedestrianEntity.Registry.pedestrianEntity(id);
        if(pedestrianEntity == null) return;

        WrappedEnumEntityUseAction use = e.getPacket().getEnumEntityUseActions().read(0);
        pedestrianEntity.interact(e.getPlayer(), use);

    }

    @Override
    public ListeningWhitelist getSendingWhitelist() {
        return null;
    }

    @Override
    public ListeningWhitelist getReceivingWhitelist() {
        return ListeningWhitelist.newBuilder().types(PacketType.Play.Client.USE_ENTITY).build();
    }

    @Override
    public Plugin getPlugin() {
        return Main.plugin();
    }
}
