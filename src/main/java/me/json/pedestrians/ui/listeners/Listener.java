package me.json.pedestrians.ui.listeners;

import me.json.pedestrians.Main;
import me.json.pedestrians.Preferences;
import me.json.pedestrians.ui.EditorView;
import me.json.pedestrians.ui.tasks.TaskType;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
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

        if(editorView.task() != null && editorView.task().scrollLock() && !e.getPlayer().isSneaking()) {
            e.setCancelled(true);
        } else {
            editorView.task(taskType);
        }

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
    public void onPlace(BlockPlaceEvent e) {

        if(!e.getPlayer().hasPermission(Preferences.MAIN_PERMISSION)) return;
        if(e.getBlock().getType() != Material.PLAYER_HEAD) return;

        EditorView editorView = EditorView.Registry.editorView(e.getPlayer());
        if(editorView == null) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onPlace(BlockBreakEvent e) {

        if(!e.getPlayer().hasPermission(Preferences.MAIN_PERMISSION)) return;

        EditorView editorView = EditorView.Registry.editorView(e.getPlayer());
        if(editorView == null) return;

        e.setCancelled(true);
    }

    private int scrollDirection(int oldSlot, int newSlot) {
        int direction = oldSlot < newSlot ? 1 : -1;
        if(oldSlot==9 && newSlot==0) direction = 1;
        if(oldSlot==0 && newSlot==9) direction = -1;

        return direction;
    }

}
