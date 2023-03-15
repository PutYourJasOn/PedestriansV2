package me.json.pedestrians.uiOLD.listeners;

import me.json.pedestrians.Main;
import me.json.pedestrians.uiOLD.Editor;
import me.json.pedestrians.uiOLD.EditorView;
import me.json.pedestrians.uiOLD.EditorViewInventory;
import me.json.pedestrians.uiOLD.StickFunction;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SelectFunctionListener implements Listener {

    //Select Node
    @EventHandler
    public void onSelect(PlayerInteractAtEntityEvent e) {

        if(!(e.getRightClicked() instanceof ArmorStand)) return;
        if(!stickInOffHand(e.getPlayer())) return;

        EditorView editorView = EditorView.Registry.editorView(e.getPlayer());
        if(editorView == null) return;

        Editor editor = editorView.editor(e.getPlayer());
        if(editor.selectStickFunction() != StickFunction.SELECT) return;

        editor.selectNode(e.getPlayer(), (ArmorStand) e.getRightClicked());
        String id = editor.selectedNode() != null ? editor.selectedNode().id()+"" : "None";

        e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(id+" selected!"));
    }

    //Deselect Node
    @EventHandler
    public void onDeselect(PlayerInteractEvent e) {

        if(!stickInOffHand(e.getPlayer())) return;

        EditorView editorView = EditorView.Registry.editorView(e.getPlayer());
        if(editorView == null) return;

        Editor editor = editorView.editor(e.getPlayer());
        if(editor.selectStickFunction() != StickFunction.SELECT) return;

        editor.selectNode(e.getPlayer(), null);
        String id = editor.selectedNode() != null ? editor.selectedNode().id()+"" : "None";

        e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(id+" selected!"));

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
