package me.json.pedestrians.ui.listeners;

import me.json.pedestrians.Main;
import me.json.pedestrians.Preferences;
import me.json.pedestrians.ui.EditorView;
import me.json.pedestrians.ui.tasks.TaskType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class Listener implements org.bukkit.event.Listener {

    @EventHandler
    public void onItemSwitch(PlayerItemHeldEvent e) {

        if(!e.getPlayer().hasPermission(Preferences.MAIN_PERMISSION)) return;

        EditorView editorView = EditorView.Registry.editorView(e.getPlayer());
        if(editorView == null) return;

        ItemStack itemStack = e.getPlayer().getInventory().getItem(e.getNewSlot());
        TaskType taskType = Main.editorViewInventory().task(itemStack);

        editorView.task(taskType);
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {

        if(!e.getPlayer().hasPermission(Preferences.MAIN_PERMISSION)) return;
        if(e.getHand() != EquipmentSlot.HAND) return;

        EditorView editorView = EditorView.Registry.editorView(e.getPlayer());
        if(editorView == null) return;

        if(e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK)
            editorView.leftClick();

        if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
            editorView.rightClick();
    }

    @EventHandler
    public void onScroll(PlayerItemHeldEvent e) {

        if(!e.getPlayer().hasPermission(Preferences.MAIN_PERMISSION)) return;

        EditorView editorView = EditorView.Registry.editorView(e.getPlayer());
        if(editorView == null) return;

        int scrollDirection = scrollDirection(e.getPreviousSlot(), e.getNewSlot());
        editorView.scroll(scrollDirection);

    }

    private int scrollDirection(int oldSlot, int newSlot) {
        int direction = oldSlot < newSlot ? 1 : -1;
        if(oldSlot==9 && newSlot==0) direction = 1;
        if(oldSlot==0 && newSlot==9) direction = -1;
        return direction;
    }

}
