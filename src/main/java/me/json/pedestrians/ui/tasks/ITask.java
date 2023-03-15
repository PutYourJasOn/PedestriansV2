package me.json.pedestrians.ui.tasks;

import me.json.pedestrians.objects.framework.path.Node;
import me.json.pedestrians.ui.EditorView;

public interface ITask {

    void init(EditorView editorView);

    void onRightClickNode(Node node);
    void onLeftClickNode(Node node);

    void onRightClick();
    void onLeftClick();

    void render();

}
