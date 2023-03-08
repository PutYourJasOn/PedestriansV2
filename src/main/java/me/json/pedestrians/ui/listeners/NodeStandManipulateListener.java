package me.json.pedestrians.ui.listeners;

import me.json.pedestrians.ui.EditorView;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;

public class NodeStandManipulateListener implements Listener {

    //NodeStand safety listener
    @EventHandler
    public void onNodeStandManipulate(PlayerArmorStandManipulateEvent e) {
        if(EditorView.Registry.editorView(e.getRightClicked()) != null) e.setCancelled(true);
    }


}
