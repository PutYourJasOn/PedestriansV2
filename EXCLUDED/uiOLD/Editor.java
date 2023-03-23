package me.json.pedestrians.uiOLD;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

public class Editor {

    private final EditorView editorView;
    private final Player player;
    private ItemStack[] savedInventory;

    public Editor(EditorView editorView, Player player) {
        this.editorView = editorView;
        this.player=player;
    }

    //Getters
    public Player player() {
        return this.player;
    }

    @Nullable
    public ItemStack[] savedInventory() {
        return this.savedInventory;
    }

    //Setters
    public void savedInventory(ItemStack[] savedInventory) {
        this.savedInventory=savedInventory;
    }

}