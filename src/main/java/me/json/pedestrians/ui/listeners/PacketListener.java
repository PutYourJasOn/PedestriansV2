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
import me.json.pedestrians.ui.EditorView;
import me.json.pedestrians.ui.tasks.AddTask;
import org.bukkit.plugin.Plugin;

public class PacketListener implements com.comphenix.protocol.events.PacketListener {

    @Override
    public void onPacketSending(PacketEvent packetEvent) {

    }

    @Override
    public void onPacketReceiving(PacketEvent e) {

        if(e.getPacketType() == PacketType.Play.Client.USE_ENTITY) handleUsePacket(e);
        if(e.getPacketType() == PacketType.Play.Client.HELD_ITEM_SLOT) handleHeldItemPacket(e);

    }

    private void handleHeldItemPacket(PacketEvent e) {

        if(!e.getPlayer().hasPermission(Preferences.MAIN_PERMISSION)) return;

        EditorView editorView = EditorView.Registry.editorView(e.getPlayer());
        if(editorView == null) return;

        if(editorView.task() == null || !(editorView.task() instanceof AddTask)) return;

        int slot = e.getPacket().getIntegers().read(0);
        switch (slot) {
            case 1 -> editorView.addTaskScroll(1);
            case 8 -> editorView.addTaskScroll(-1);
        }

    }

    private void handleUsePacket(PacketEvent e) {

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
        NodeClientEntity nodeClientEntity = ((NodeClientEntity) clientEntity);

        if(!editorView.pathNetwork().nodes().contains(nodeClientEntity.node())) return;

        switch (use.getAction()) {
            case ATTACK -> editorView.leftClickNode(nodeClientEntity);
            case INTERACT_AT -> editorView.rightClickNode(nodeClientEntity);
        }
    }

    @Override
    public ListeningWhitelist getSendingWhitelist() {
        return null;
    }

    @Override
    public ListeningWhitelist getReceivingWhitelist() {
        return ListeningWhitelist.newBuilder().types(PacketType.Play.Client.USE_ENTITY, PacketType.Play.Client.HELD_ITEM_SLOT).build();
    }

    @Override
    public Plugin getPlugin() {
        return Main.plugin();
    }

}
