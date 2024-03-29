package me.json.pedestrians.ui.tasks;

import me.json.pedestrians.objects.entities.NodeClientEntity;
import me.json.pedestrians.ui.EditorView;

public interface ITask {

    void init(EditorView editorView);
    void stop();

    void onRightClickNode(NodeClientEntity node);
    void onLeftClickNode(NodeClientEntity node);

    void onRightClick();
    void onLeftClick();

    boolean scrollLock();
    void onScroll(int scrollDirection);

    void render();

}
