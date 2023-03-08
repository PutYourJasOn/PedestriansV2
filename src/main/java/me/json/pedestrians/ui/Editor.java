package me.json.pedestrians.ui;

import me.json.pedestrians.objects.framework.path.Node;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

public class Editor {

    private final EditorView editorView;
    private final Player player;

    private Node selectedNode;
    private StickFunction selectedStickFunction = StickFunction.defaultStickFunction();
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
    public Node selectedNode() {
        return this.selectedNode;
    }

    public StickFunction selectStickFunction() {
        return this.selectedStickFunction;
    }

    @Nullable
    public ItemStack[] savedInventory() {
        return this.savedInventory;
    }

    //Setters
    public void selectedNode(@Nullable Node selectedNode) {
        this.selectedNode=selectedNode;
    }

    public void selectNode(Player player, ArmorStand stand) {
        editorView.handleNotAnEditor(player);
        selectedNode(editorView.node(stand));
    }

    public void selectStickFunction(StickFunction selectedStickFunction) {
        this.selectedStickFunction=selectedStickFunction;
    }

    public void savedInventory(ItemStack[] savedInventory) {
        this.savedInventory=savedInventory;
    }

}