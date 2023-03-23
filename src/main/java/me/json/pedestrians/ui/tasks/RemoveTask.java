package me.json.pedestrians.ui.tasks;

import me.json.pedestrians.Messages;
import me.json.pedestrians.entities.NodeClientEntity;
import me.json.pedestrians.ui.EditorView;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class RemoveTask implements ITask{

    private EditorView editorView;
    private NodeClientEntity nodeEntity;

    @Override
    public void init(EditorView editorView) {
        this.editorView = editorView;
    }

    @Override
    public void stop() {

        if(nodeEntity != null)
            nodeEntity.glowing(false);

    }

    @Override
    public void onRightClickNode(NodeClientEntity nodeEntity) {

        if(this.nodeEntity != null && this.nodeEntity.equals(nodeEntity)) {

            editorView.pathNetwork().removeNode(nodeEntity.node());
            editorView.editorViewRenderer().removeNodeEntity(nodeEntity);
            this.nodeEntity = null;

            editorView.player().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(String.format(Messages.NODE_REMOVED)));

        } else {

            if(this.nodeEntity != null)
                this.nodeEntity.glowing(false);

            this.nodeEntity = nodeEntity;
            this.nodeEntity.glowing(true);
            editorView.player().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(String.format(Messages.CLICK_AGAIN_REMOVE)));

        }

    }

    @Override
    public void onLeftClickNode(NodeClientEntity node) {

    }

    @Override
    public void onRightClick() {

    }

    @Override
    public void onLeftClick() {

    }

    @Override
    public boolean scrollLock() {
        return false;
    }

    @Override
    public void onScroll(int scrollDirection) {

    }

    @Override
    public void render() {

    }
}
