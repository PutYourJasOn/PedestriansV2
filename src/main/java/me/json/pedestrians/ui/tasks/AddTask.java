package me.json.pedestrians.ui.tasks;

import me.json.pedestrians.objects.framework.path.Node;
import me.json.pedestrians.ui.EditorView;

public class AddTask implements ITask {

    private EditorView editorView;

    @Override
    public void init(EditorView editorView) {
        this.editorView = editorView;
        editorView.player().sendMessage("TEST");
    }

    @Override
    public void onRightClickNode(Node node) {

    }

    @Override
    public void onLeftClickNode(Node node) {

    }

    @Override
    public void onRightClick() {

    }

    @Override
    public void onLeftClick() {

    }

    @Override
    public void onScroll(int scrollDirection) {

    }

    @Override
    public void render() {

    }

}
