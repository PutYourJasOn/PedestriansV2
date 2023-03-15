package me.json.pedestrians.objects.pedestrian_entities;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedEnumEntityUseAction;
import me.json.pedestrians.Main;
import me.json.pedestrians.Preferences;
import me.json.pedestrians.objects.Skin;
import me.json.pedestrians.objects.framework.pedestrian.Pedestrian;
import me.json.pedestrians.objects.framework.pedestrian.PedestrianEntity;
import me.json.pedestrians.utils.NPC;
import me.json.pedestrians.utils.Vector3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PlayerPedestrianEntity implements PedestrianEntity {

    private Pedestrian pedestrian;
    private Skin skin;
    private NPC npc;
    private boolean isInsideInteraction = false;

    public PlayerPedestrianEntity(Skin skin) {
        this.skin = skin;
    }

    @Override
    public PedestrianEntity initialize(Pedestrian pedestrian) {
        this.pedestrian = pedestrian;
        return this;
    }

    @Override
    public PedestrianEntity spawn(Location location) {

        int id = Registry.newEntityID();
        Registry.register(id, this);
        npc = new NPC(id, location, skin.base64(), skin.signature());

        return this;
    }

    @Override
    public void remove() {
        npc.remove();
    }

    @Override
    public void asyncMove(Location location) {

        if(npc != null) {
            npc.move(location);
            npc.updateViewers();
        }

    }

    @Override
    public void interact(Player player, WrappedEnumEntityUseAction entityUseAction) {

        if(isInsideInteraction) return;

        if(entityUseAction.getAction() != EnumWrappers.EntityUseAction.ATTACK && entityUseAction.getHand() == EnumWrappers.Hand.OFF_HAND) return;

        //Interact
        if(entityUseAction.getAction() == EnumWrappers.EntityUseAction.INTERACT_AT) {

            player.playSound(player, Sound.ENTITY_VILLAGER_CELEBRATE, 1, 1);
            float originalVel = pedestrian.velocity();
            pedestrian.velocity(0);
            pedestrian.targetedPlayer(player);
            isInsideInteraction = true;

            Bukkit.getScheduler().runTaskLater(Main.plugin(), () -> {

                pedestrian.velocity(originalVel);
                pedestrian.targetedPlayer(null);
                isInsideInteraction = false;

            }, 20*2);

            //Skin details
            if(player.hasPermission(Preferences.MAIN_PERMISSION)) {
                player.sendMessage("Skin: "+skin.name());
            }

        }

        //Attack
        if(entityUseAction.getAction() == EnumWrappers.EntityUseAction.ATTACK) {

            player.playSound(player, Sound.ENTITY_VILLAGER_HURT, 1, 1);
            player.spawnParticle(Particle.VILLAGER_ANGRY, pedestrian.pos().clone().add(new Vector3(0,1.5,0)).toLocation(), 1);

            float originalVel = pedestrian.velocity();
            pedestrian.velocity(Preferences.PEDESTRIAN_MAX_RUN_VELOCITY);
            pedestrian.targetedPlayer(null);
            isInsideInteraction = true;

            Bukkit.getScheduler().runTaskLater(Main.plugin(), () -> {

                pedestrian.velocity(originalVel);
                isInsideInteraction = false;

            }, 20*2);

        }

    }

    public Pedestrian pedestrian() {
        return this.pedestrian;
    }

    public static class Registry {

        private final static Map<Integer, PlayerPedestrianEntity> pedestrianEntities = new HashMap<>();

        private static boolean exists(Integer id) {
            return pedestrianEntities.containsKey(id);
        }

        private static void register(Integer entityID, PlayerPedestrianEntity pedestrianEntity) {
            pedestrianEntities.put(entityID, pedestrianEntity);
        }

        @Nullable
        public static PlayerPedestrianEntity pedestrianEntity(Integer id) {
            return pedestrianEntities.getOrDefault(id, null);
        }

        public static int newEntityID() {
            int id = new Random().nextInt();
            while(Registry.exists(id) || entityExists(id)) {
                id = new Random().nextInt();
            }
            return id;
        }

        private static boolean entityExists(int id) {
            return Main.world().getEntities().stream().filter(e -> e.getEntityId() == id).findFirst().isPresent();
        }

    }


}