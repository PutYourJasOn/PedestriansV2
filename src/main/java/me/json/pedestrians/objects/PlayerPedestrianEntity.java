package me.json.pedestrians.objects;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedEnumEntityUseAction;
import me.json.pedestrians.Main;
import me.json.pedestrians.Preferences;
import me.json.pedestrians.objects.entities.PlayerClientEntity;
import me.json.pedestrians.objects.framework.pedestrian.Pedestrian;
import me.json.pedestrians.objects.framework.pedestrian.PedestrianEntity;
import me.json.pedestrians.utils.Vector3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class PlayerPedestrianEntity implements PedestrianEntity {

    private Pedestrian pedestrian;
    private Skin skin;
    private PlayerClientEntity npc;
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

        npc = new PlayerClientEntity(location, this, skin.base64(), skin.signature());
        return this;
    }

    @Override
    public void remove() {
        npc.remove();
        npc = null;
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
            pedestrian.velocity(Preferences.PEDESTRIAN_MAX_VELOCITY*2);
            pedestrian.targetedPlayer(null);
            isInsideInteraction = true;

            Bukkit.getScheduler().runTaskLater(Main.plugin(), () -> {

                pedestrian.velocity(originalVel);
                isInsideInteraction = false;

            }, 20*2);

        }

    }

}