package me.json.pedestrians.ui.tasks;

import me.json.pedestrians.Messages;
import me.json.pedestrians.objects.entities.NodeClientEntity;
import me.json.pedestrians.ui.EditorView;

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

            editorView.editorViewRenderer().updateNodeTexts();
            Messages.sendActionBar(editorView.player(), Messages.NODE_REMOVED);

        } else {

            if(this.nodeEntity != null)
                this.nodeEntity.glowing(false);

            this.nodeEntity = nodeEntity;
            this.nodeEntity.glowing(true);

            Messages.sendActionBar(editorView.player(), Messages.CLICK_AGAIN_REMOVE);
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
