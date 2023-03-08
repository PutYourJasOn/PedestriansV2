package me.json.pedestrians.ui.listeners;

import me.json.pedestrians.Main;
import me.json.pedestrians.objects.framework.path.Node;
import me.json.pedestrians.ui.Editor;
import me.json.pedestrians.ui.EditorView;
import me.json.pedestrians.ui.EditorViewInventory;
import me.json.pedestrians.ui.StickFunction;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import java.util.HashMap;
import java.util.Map;

public class RemoveFunctionListener implements Listener {

    private final Map<Player, Node> toRemove = new HashMap<>();

    @EventHandler
    public void onClick(PlayerInteractAtEntityEvent e) {

        if(!(e.getRightClicked() instanceof ArmorStand)) return;
        if(!stickInOffHand(e.getPlayer())) return;

        EditorView editorView = EditorView.Registry.editorView(e.getPlayer());
        if(editorView == null) return;

        Editor editor = editorView.editor(e.getPlayer());
        if(editor.selectStickFunction() != StickFunction.REMOVE) return;

        Node selectedNode = editorView.node((ArmorStand) e.getRightClicked());
        if(selectedNode == null) return;
        int id = selectedNode.id();

        if(toRemove.containsKey(e.getPlayer()) && toRemove.get(e.getPlayer()).equals(selectedNode)) {

            editorView.editor(e.getPlayer()).selectedNode(null);

            editorView.removeNode(selectedNode);
            e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(id+" removed!"));
            return;
        }

        Bukkit.getScheduler().runTaskLater(Main.plugin(), () -> toRemove.put(e.getPlayer(), selectedNode), 10L);
        Bukkit.getScheduler().runTaskLater(Main.plugin(), () -> toRemove.remove(e.getPlayer(), selectedNode), 50);
        e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("Click again if you're sure!"));
    }

    //
    private final EditorViewInventory editorInv = Main.editorViewInventory();

    private boolean stickInMainHand(Player player) {
        return player.getInventory().getItemInMainHand().equals(editorInv.stick());
    }

    private boolean stickInOffHand(Player player) {
        return player.getInventory().getItemInOffHand().equals(editorInv.stick());
    }


}
