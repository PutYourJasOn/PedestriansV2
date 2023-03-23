package me.json.pedestrians.uiOLD.listeners;

import me.json.pedestrians.Main;
import me.json.pedestrians.uiOLD.Editor;
import me.json.pedestrians.uiOLD.EditorView;
import me.json.pedestrians.uiOLD.EditorViewInventory;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class SwitchFunctionListener implements Listener {

    //Switch Stick
    @EventHandler
    public void onSwitch(PlayerInteractEvent e) {

        if(!stickInMainHand(e.getPlayer())) return;

        EditorView editorView = EditorView.Registry.editorView(e.getPlayer());
        if(editorView == null) return;

        Editor editor = editorView.editor(e.getPlayer());
        editor.selectStickFunction(editor.selectStickFunction().next());

        e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(editor.selectStickFunction().name()+" selected!"));
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
