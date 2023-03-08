package me.json.pedestrians.ui.listeners;

import me.json.pedestrians.Main;
import me.json.pedestrians.objects.framework.path.Node;
import me.json.pedestrians.objects.framework.path.connection.ConnectionHandler;
import me.json.pedestrians.objects.framework.path.connection.DirectConnectionHandler;
import me.json.pedestrians.utils.InterpolationUtil;
import me.json.pedestrians.utils.Vector3;
import me.json.pedestrians.ui.Editor;
import me.json.pedestrians.ui.EditorView;
import me.json.pedestrians.ui.EditorViewInventory;
import me.json.pedestrians.ui.StickFunction;
import me.json.pedestrians.utils.RayTraceUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

//TODO: make pretty
public class AddFunctionListener extends BukkitRunnable implements Listener {

    public AddFunctionListener() {
        this.runTaskTimer(Main.plugin(), 1L, 1L);
    }

    private final Set<Player> interactTimeout = new HashSet<>();
    private final Map<Player, AddTask> addTasks = new HashMap<>();
    private static final Set<Player> noRounding = new HashSet<>();

    @EventHandler
    public void onHandSwitch(PlayerSwapHandItemsEvent e) {

        if(!e.getOffHandItem().equals(editorInv.stick())) {
            addTasks.remove(e.getPlayer());
            noRounding.remove(e.getPlayer());
            return;
        }

        EditorView editorView = EditorView.Registry.editorView(e.getPlayer());
        if(editorView == null) return;

        Editor editor = editorView.editor(e.getPlayer());
        if(editor.selectStickFunction() != StickFunction.ADD) return;

        addTasks.put(e.getPlayer(), new AddTask(e.getPlayer()));

    }

    @EventHandler
    public void onScroll(PlayerItemHeldEvent e) {

        if(!addTasks.containsKey(e.getPlayer())) return;
        AddTask addTask = addTasks.get(e.getPlayer());
        if(addTask.currentStep() != 3) return;

        int scrollDirection = scrollDirection(e.getPreviousSlot(), e.getNewSlot());
        double newWidth;
        if(noRounding.contains(e.getPlayer())) {
            newWidth = addTask.width+scrollDirection*0.5;
        }else {
            newWidth = addTask.width+scrollDirection*2;
        }
        addTask.width(newWidth);
        
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {

        if(!addTasks.containsKey(e.getPlayer())) return;
        if(!stickInOffHand(e.getPlayer())) return;

        if(e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
            if(noRounding.contains(e.getPlayer())) {
                noRounding.remove(e.getPlayer());
            }else {
                noRounding.add(e.getPlayer());
            }

            e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("Precise edit mode: "+noRounding.contains(e.getPlayer())));

            return;
        }

        if(interactTimeout.contains(e.getPlayer())) return;

        AddTask addTask = addTasks.get(e.getPlayer());
        int currentStep = addTask.currentStep();

        if(currentStep == 1) {

            if(noRounding.contains(e.getPlayer())) {
                addTask.pos(new Vector3(RayTraceUtil.rayTraceResult(e.getPlayer())));
            } else{
                addTask.pos(new Vector3(RayTraceUtil.roundedRayTraceResult(e.getPlayer())));
            }

            e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("Position set."));
        }

        if(currentStep == 2) {
            addTask.updateDir();
            e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("Direction set."));
        }

        if(currentStep == 3) {
            boolean shouldStartNewTask = addTask.add();

            if(shouldStartNewTask) {
                addTasks.put(e.getPlayer(), new AddTask(e.getPlayer()));
            } else {
                e.getPlayer().getEquipment().setItemInOffHand(e.getPlayer().getEquipment().getItemInMainHand());
                e.getPlayer().getEquipment().setItemInMainHand(editorInv.stick());
                addTasks.remove(e.getPlayer());
                noRounding.remove(e.getPlayer());
            }

            e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("Width set --> Done."));
        }

        interactTimeout.add(e.getPlayer());
        Bukkit.getScheduler().runTaskLater(Main.plugin(), () -> interactTimeout.remove(e.getPlayer()), 10);

    }

    @Override
    public void run() {

        if(addTasks.isEmpty()) return;

        for (Player player : addTasks.keySet()) {
            if(!stickInOffHand(player)) continue;
            addTasks.get(player).renderParticles();
        }

    }

    private int scrollDirection(int oldSlot, int newSlot) {
        int direction = oldSlot < newSlot ? 1 : -1;
        if(oldSlot==9 && newSlot==0) direction = 1;
        if(oldSlot==0 && newSlot==9) direction = -1;
        return direction;
    }

    //
    private final EditorViewInventory editorInv = Main.editorViewInventory();

    public boolean stickInOffHand(Player player) {
        return player.getInventory().getItemInOffHand().equals(editorInv.stick());
    }

    //
    private static class AddTask {

        private final Player player;
        private Vector3 pos;
        private Vector3 dir;
        private double width = 8f;

        private AddTask(Player player) {
            this.player = player;
        }

        public void pos(Vector3 pos) {
            this.pos = pos;
        }

        public void updateDir() {
            this.dir = direction(player, noRounding.contains(player));
        }

        public void width(double width) {
            this.width = width;
        }

        public boolean add() {
            EditorView editorView = EditorView.Registry.editorView(player);
            Node selectedNode = editorView.editor(player).selectedNode();

            if(selectedNode != null) {
                editorView.editor(player).selectedNode(editorView.addNode(pos, dir, width, selectedNode));
            } else {
                editorView.addNode(pos, dir, width);
            }

            return selectedNode != null;
        }

        public int currentStep() {
            if(pos == null) return 1;
            if(dir == null) return 2;
            return 3;
        }

        public void renderParticles() {

            //1.
            if(pos == null) {

                Vector raytrace;

                if(noRounding.contains(player)) {
                    raytrace = RayTraceUtil.rayTraceResult(player);
                }else{
                    raytrace = RayTraceUtil.roundedRayTraceResult(player);
                }

                if(raytrace != null)
                    player.spawnParticle(Particle.END_ROD,raytrace.toLocation(Main.world()),1, 0, 0, 0, 0);

                return;
            }

            //2.
            if(dir == null) {
                player.spawnParticle(Particle.END_ROD, pos.toLocation(),1, 0, 0, 0, 0);

                Set<Vector3> vertices = new HashSet<>();
                Vector3[] positions = nodePositions(pos, direction(player, noRounding.contains(player)), width);
                vertices.addAll(InterpolationUtil.lineVertices(positions[0],positions[1], 15));

                vertices.forEach(v -> player.spawnParticle(Particle.COMPOSTER, v.toLocation(), 1));
                player.spawnParticle(Particle.END_ROD, positions[0].toLocation(),1, 0, 0, 0, 0);
                player.spawnParticle(Particle.END_ROD,  positions[1].toLocation(),1, 0, 0, 0, 0);

                return;
            }

            //3.
            if(true) {
                player.spawnParticle(Particle.END_ROD, pos.toLocation(), 1, 0, 0, 0, 0);

                Set<Vector3> vertices = new HashSet<>();
                Vector3[] positions = nodePositions(pos, dir, width);
                vertices.addAll(InterpolationUtil.lineVertices(positions[0], positions[1], 15));

                vertices.forEach(v -> player.spawnParticle(Particle.COMPOSTER, v.toLocation(), 1));
                player.spawnParticle(Particle.END_ROD, positions[0].toLocation().add(0,0.5,0),1, 0, 0, 0, 0);
                player.spawnParticle(Particle.END_ROD,  positions[1].toLocation().add(0,0.5,0),1, 0, 0, 0, 0);
            }

        }

        private Vector3[] nodePositions(Vector3 pos, Vector3 dir, double width) {

            Vector3[] vector3Array = new Vector3[2];

            dir.y(0);

            DirectConnectionHandler handler = (DirectConnectionHandler) ConnectionHandler.ConnectionHandlerEnum.DIRECT_CONNECTION_HANDLER.connectionHandler();
            vector3Array[0] = handler.targetPos(pos, dir, width, 0);
            vector3Array[1] = handler.targetPos(pos, dir, width, 1);

            return vector3Array;
        }

        private Vector3 direction(Player player, boolean noRounding) {
            float yaw = player.getEyeLocation().getYaw();

            if(!noRounding) {
                float fraction = 1 / 22.5f;
                yaw = Math.round(yaw * fraction) / fraction;
            }

            Location newLoc = new Location(Main.world(), 0,0,0);
            newLoc.setYaw(yaw);
            return new Vector3(newLoc.getDirection());
        }

    }

}
