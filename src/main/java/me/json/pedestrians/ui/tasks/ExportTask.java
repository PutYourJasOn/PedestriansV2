package me.json.pedestrians.ui.tasks;

import me.json.pedestrians.Messages;
import me.json.pedestrians.data.exporting.ExportPathNetwork;
import me.json.pedestrians.objects.entities.NodeClientEntity;
import me.json.pedestrians.ui.EditorView;

public class ExportTask implements ITask{

    private EditorView editorView;
    private ExportPathNetwork exportPathNetworkTask;
    
    @Override
    public void init(EditorView editorView) {
        this.editorView = editorView;
    }

    @Override
    public void stop() {

    }

    @Override
    public void onRightClickNode(NodeClientEntity node) {

    }

    @Override
    public void onLeftClickNode(NodeClientEntity node) {

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
        return false;
    }

    @Override
    public void onScroll(int scrollDirection) {

    }

    @Override
    public void render() {

    }

    private void handleClick() {

        if(exportPathNetworkTask != null) return;

        exportPathNetworkTask = new ExportPathNetwork(editorView.pathNetwork(), (v) -> {

            Messages.sendActionBar(editorView.player(), Messages.PATHNETWORK_SAVED);
            exportPathNetworkTask = null;

        });

        exportPathNetworkTask.start();

    }

}
