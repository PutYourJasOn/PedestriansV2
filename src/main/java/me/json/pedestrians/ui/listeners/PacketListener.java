package me.json.pedestrians.ui.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedEnumEntityUseAction;
import me.json.pedestrians.Main;
import me.json.pedestrians.Preferences;
import me.json.pedestrians.entities.ClientEntity;
import me.json.pedestrians.entities.NodeClientEntity;
import me.json.pedestrians.objects.framework.path.Node;
import me.json.pedestrians.ui.EditorView;
import org.bukkit.plugin.Plugin;

public class PacketListener implements com.comphenix.protocol.events.PacketListener {

    @Override
    public void onPacketSending(PacketEvent packetEvent) {

    }

    @Override
    public void onPacketReceiving(PacketEvent e) {

        if(!e.getPlayer().hasPermission(Preferences.MAIN_PERMISSION)) return;

        WrappedEnumEntityUseAction use = e.getPacket().getEnumEntityUseActions().read(0);
        if(use.getAction() == EnumWrappers.EntityUseAction.INTERACT) return;
        if(use.getAction() == EnumWrappers.EntityUseAction.INTERACT_AT && use.getHand() == EnumWrappers.Hand.OFF_HAND) return;

        EditorView editorView = EditorView.Registry.editorView(e.getPlayer());
        if(editorView == null) return;

        int id = e.getPacket().getIntegers().read(0);
        ClientEntity clientEntity = ClientEntity.Registry.clientEntity(id);
        if(clientEntity == null) return;

        if(!(clientEntity instanceof NodeClientEntity)) return;
        Node node = ((NodeClientEntity) clientEntity).node();

        switch (use.getAction()) {
            case ATTACK -> editorView.leftClickNode(node);
            case INTERACT_AT -> editorView.rightClickNode(node);
        }

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
