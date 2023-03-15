package me.json.pedestrians.ui;

import me.json.pedestrians.Main;
import me.json.pedestrians.Preferences;
import me.json.pedestrians.ui.tasks.Task;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

public class Listener implements org.bukkit.event.Listener {

    @EventHandler
    public void onItemSwitch(PlayerItemHeldEvent e) {

        if(!e.getPlayer().hasPermission(Preferences.MAIN_PERMISSION)) return;

        EditorView editorView = EditorView.Registry.editorView(e.getPlayer());
        if(editorView == null) return;

        ItemStack itemStack = e.getPlayer().getInventory().getItem(e.getNewSlot());
        Task task = Main.editorViewInventory().task(itemStack);

        editorView.task(task);
    }

}
