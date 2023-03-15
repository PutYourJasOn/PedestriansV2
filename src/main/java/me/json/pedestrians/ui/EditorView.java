package me.json.pedestrians.ui;

import me.json.pedestrians.Main;
import me.json.pedestrians.objects.framework.path.Node;
import me.json.pedestrians.objects.framework.path.PathNetwork;
import me.json.pedestrians.ui.tasks.ITask;
import me.json.pedestrians.ui.tasks.Task;
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

    private final Set<Node> selectedNodes = new HashSet<>();
    private final EditorViewRenderer editorViewRenderer = new EditorViewRenderer(this);

    private ItemStack[] prevInventory;
    private ITask task;

    public EditorView(Player player, PathNetwork pathNetwork) {
        this.player = player;
        this.pathNetwork = pathNetwork;

        this.prevInventory = player.getInventory().getContents();
        Main.editorViewInventory().pushToPlayer(player);

        Registry.register(player, this);
    }

    //Functions
    public void stop() {
        player.getInventory().setContents(prevInventory);
        editorViewRenderer.stop();
    }

    public void selectNode(Node node) {
        selectedNodes.add(node);
    }

    public void task(Task task) {

        if(task == null) {
            this.task = null;
            return;
        }

        if(this.task != null && this.task.getClass() == task.iTaskClass()) return;

        selectedNodes.clear();

        this.task = task.newInstance();
        this.task.init(this);
    }

    //Getters
    public Player player() {
        return player;
    }

    public PathNetwork pathNetwork() {
        return pathNetwork;
    }

    public Set<Node> selectedNodes() {
        return new HashSet<>(selectedNodes);
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
