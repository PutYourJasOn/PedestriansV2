package me.json.pedestrians.ui;

import me.json.pedestrians.Main;
import me.json.pedestrians.entities.NodeClientEntity;
import me.json.pedestrians.objects.framework.path.Node;
import me.json.pedestrians.objects.framework.path.PathNetwork;
import me.json.pedestrians.ui.tasks.AddTask;
import me.json.pedestrians.ui.tasks.ITask;
import me.json.pedestrians.ui.tasks.TaskType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EditorView {

    private final Player player;
    private final PathNetwork pathNetwork;
    private final EditorViewRenderer editorViewRenderer;

    private ItemStack[] prevInventory;
    private ITask task;

    public EditorView(Player player, PathNetwork pathNetwork) {
        this.player = player;
        this.pathNetwork = pathNetwork;
        this.editorViewRenderer = new EditorViewRenderer(this);

        this.prevInventory = player.getInventory().getContents();
        Main.editorViewInventory().pushToPlayer(player);

        Registry.register(player, this);
    }

    //Functions
    public void stop() {
        player.getInventory().setContents(prevInventory);
        editorViewRenderer.stop();
    }

    public void task(TaskType taskType) {

        if(taskType == null) {

            if(this.task != null)
                this.task.stop();

            this.task = null;
            return;
        }

        if(this.task != null && this.task.getClass() == taskType.iTaskClass()) return;

        if(this.task != null)
            this.task.stop();

        this.task = taskType.newInstance();
        this.task.init(this);
    }

    //Clicks
    public void leftClick() {
        if(task != null)
            task.onLeftClick();
    }

    public void rightClick() {
        if(task != null)
            task.onRightClick();
    }

    public void leftClickNode(NodeClientEntity node) {
        if(task != null)
            task.onLeftClickNode(node);
    }

    public void rightClickNode(NodeClientEntity node) {
        if(task != null)
            task.onRightClickNode(node);
    }

    public void addTaskScroll(int scrollDirection) {
        if(task != null && task instanceof AddTask)
            ((AddTask) task).onScroll(scrollDirection);
    }


    //Getters
    public Player player() {
        return player;
    }

    public PathNetwork pathNetwork() {
        return pathNetwork;
    }

    public EditorViewRenderer editorViewRenderer() {
        return editorViewRenderer;
    }

    public ITask task() {
        return task;
    }

    //Registry
    public static class Registry {

        private final static Map<Player, EditorView> editorViews = new HashMap<>();

        private static void register(Player player, EditorView editorView) {
            editorViews.put(player, editorView);
        }

        @Nullable
        public static EditorView editorView(Player player) {
            return editorViews.getOrDefault(player, null);
        }

        public static Set<EditorView> editorViews() {
            return new HashSet<>(editorViews.values());
        }

    }

}
