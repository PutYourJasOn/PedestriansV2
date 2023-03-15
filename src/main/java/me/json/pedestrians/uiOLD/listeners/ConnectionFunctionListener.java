package me.json.pedestrians.uiOLD.listeners;

import me.json.pedestrians.Main;
import me.json.pedestrians.objects.framework.path.Node;
import me.json.pedestrians.objects.framework.path.connection.ConnectionHandler;
import me.json.pedestrians.uiOLD.Editor;
import me.json.pedestrians.uiOLD.EditorView;
import me.json.pedestrians.uiOLD.EditorViewInventory;
import me.json.pedestrians.uiOLD.StickFunction;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import java.util.HashMap;
import java.util.Map;

public class ConnectionFunctionListener implements Listener {

    private final Map<Player, AddTask> addTasks = new HashMap<>();

    @EventHandler
    public void onClick(PlayerInteractAtEntityEvent e) {

        if(!(e.getRightClicked() instanceof ArmorStand)) return;
        if(!stickInOffHand(e.getPlayer())) return;

        EditorView editorView = EditorView.Registry.editorView(e.getPlayer());
        if(editorView == null) return;

        Editor editor = editorView.editor(e.getPlayer());
        if(editor.selectStickFunction() != StickFunction.CONNECTION) return;

        Node originNode = editor.selectedNode();
        Node targetNode = editorView.node((ArmorStand) e.getRightClicked());

        if(originNode == null) e.getPlayer().sendMessage("No node selected, use the select stick!");
        if(originNode == null || targetNode == null) return;

        if(originNode.connectedNodes().contains(targetNode)) {
            editorView.removeConnection(editor, targetNode);
            e.getPlayer().sendMessage("Connection removed");
        }else if(!addTasks.containsKey(e.getPlayer())) {
            addTasks.put(e.getPlayer(), new AddTask(editorView, editor, targetNode));

            e.getPlayer().sendMessage("Enter a connection type");
            for (int i = 0; i < ConnectionHandler.ConnectionHandlerEnum.values().length; i++) {
                e.getPlayer().sendMessage(i+": "+ConnectionHandler.ConnectionHandlerEnum.values()[i].name());
            }
        }

    }

    @EventHandler
    public void onHandSwitch(PlayerSwapHandItemsEvent e) {

        if(!e.getOffHandItem().equals(editorInv.stick())) {
            addTasks.remove(e.getPlayer());
        }

    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {

        if(!addTasks.containsKey(e.getPlayer())) return;
        AddTask addTask = addTasks.get(e.getPlayer());

        e.setCancelled(true);

        if(addTask.currentStep() == 1) {

            //Connection Type
            try {

                int type = Integer.parseInt(e.getMessage());
                if(type < 0 || type > ConnectionHandler.ConnectionHandlerEnum.values().length-1) {
                    e.getPlayer().sendMessage("Wrong input!");
                    return;
                }

                addTask.type(type);

                Bukkit.getScheduler().runTask(Main.plugin(), () -> {
                    addTask.add();
                    addTasks.remove(e.getPlayer(), addTask);
                    e.getPlayer().sendMessage("Created a new connection!");
                });

            } catch (NumberFormatException ex) {
                e.getPlayer().sendMessage("Wrong input!");
            }

        }

    }

    //
    private final EditorViewInventory editorInv = Main.editorViewInventory();

    private boolean stickInMainHand(Player player) {
        return player.getInventory().getItemInMainHand().equals(editorInv.stick());
    }

    private boolean stickInOffHand(Player player) {
        return player.getInventory().getItemInOffHand().equals(editorInv.stick());
    }

    //
    private static class AddTask {

        private final EditorView editorView;
        private final Editor editor;
        private final Node targetNode;
        private Integer type;

        private AddTask(EditorView editorView, Editor editor, Node targetNode) {
            this.editorView = editorView;
            this.editor = editor;
            this.targetNode = targetNode;
        }

        public int currentStep() {
            if(type == null) return 1;
            return 2;
        }

        public void type(int type) {
            this.type=type;
        }

        public void add() {
            editorView.addConnection(editor, targetNode, ConnectionHandler.ConnectionHandlerEnum.values()[type].connectionHandler());
        }

    }

}
