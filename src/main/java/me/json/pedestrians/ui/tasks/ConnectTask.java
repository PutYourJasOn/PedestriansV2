package me.json.pedestrians.ui.tasks;

import me.json.pedestrians.Messages;
import me.json.pedestrians.objects.entities.NodeClientEntity;
import me.json.pedestrians.objects.framework.path.connection.ConnectionHandler.ConnectionHandlerType;
import me.json.pedestrians.ui.EditorView;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConnectTask implements ITask{

    private final List<NodeClientEntity> selectedNodeEntities = new ArrayList<>();
    private EditorView editorView;
    private List<ConnectionHandlerType> connectionHandlerTypes;

    private ConnectionHandlerType connectionHandlerType = ConnectionHandlerType.DIRECT_CONNECTION_HANDLER;


    @Override
    public void init(EditorView editorView) {
        this.editorView = editorView;

        connectionHandlerTypes = new ArrayList<>(Arrays.asList(ConnectionHandlerType.values()));
        connectionHandlerTypes.add(null);
    }

    @Override
    public void stop() {
        selectedNodeEntities.forEach(e -> e.glowing(false));
    }

    @Override
    public void onRightClickNode(NodeClientEntity node) {
        handleNodeClick(node);
    }

    @Override
    public void onLeftClickNode(NodeClientEntity node) {
        handleNodeClick(node);
    }

    @Override
    public void onRightClick() {
        handleClick();
    }

    @Override
    public void onLeftClick() {
        handleClick();
    }

    @Override
    public boolean scrollLock() {
        return selectedNodeEntities.size()==2;
    }

    @Override
    public void onScroll(int scrollDirection) {

        int currentIndex = connectionHandlerTypes.indexOf(connectionHandlerType);
        int nextIndex = currentIndex + 1 >= connectionHandlerTypes.size() ? 0 : currentIndex + 1;
        connectionHandlerType = connectionHandlerTypes.get(nextIndex);

        if(connectionHandlerType == null) {
            editorView.player().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(String.format(Messages.CONNECTION_TYPE_SELECT,"NONE")));
        } else {
            editorView.player().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(String.format(Messages.CONNECTION_TYPE_SELECT,connectionHandlerType.strippedName())));
        }

    }

    @Override
    public void render() {

    }

    private void handleClick() {

        if(selectedNodeEntities.size() != 2) return;

        if(connectionHandlerType == null) {
            selectedNodeEntities.get(0).node().removeConnection(selectedNodeEntities.get(1).node());
        } else {
            selectedNodeEntities.get(0).node().registerConnectedNode(selectedNodeEntities.get(1).node(), connectionHandlerType.instance());
        }

        editorView.editorViewRenderer().updateNodeTexts();
        editorView.player().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Messages.CONNECTION_CREATED));

        //Reset
        selectedNodeEntities.forEach(e -> e.glowing(false));
        selectedNodeEntities.clear();

    }

    private void handleNodeClick(NodeClientEntity nodeEntity) {

        if(selectedNodeEntities.size() > 1) return;
        if(selectedNodeEntities.contains(nodeEntity)) return;

        selectedNodeEntities.add(nodeEntity);
        nodeEntity.glowing(true);

        if(selectedNodeEntities.size() > 0) {
            editorView.player().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(String.format(Messages.CONNECTION_TYPE_SELECT,connectionHandlerType.strippedName())));
        } else {
            editorView.player().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(String.format(Messages.NODE_SELECTED, nodeEntity.node().id())));
        }
    }

}
