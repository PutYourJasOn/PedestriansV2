package me.json.pedestrians.objects;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedEnumEntityUseAction;
import me.json.pedestrians.Main;
import me.json.pedestrians.Preferences;
import me.json.pedestrians.objects.entities.PlayerClientEntity;
import me.json.pedestrians.objects.framework.path.Node;
import me.json.pedestrians.objects.framework.pedestrian.Pedestrian;
import me.json.pedestrians.utils.Vector3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class PlayerPedestrian extends Pedestrian {

    private final Skin skin;

    private PlayerClientEntity npc;
    private boolean isInsideInteraction = false;

    public PlayerPedestrian(Node originNode, Skin skin) {
        super(originNode);

        this.skin = skin;
        npc = new PlayerClientEntity(this.location(), this, skin.base64(), skin.signature());
    }

    @Override
    public void remove() {
        npc.remove();
        npc = null;
    }

    @Override
    public void move(Location location) {

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
            float originalVel = velocity();
            velocity(0);
            targetedPlayer(player);
            isInsideInteraction = true;

            Bukkit.getScheduler().runTaskLater(Main.plugin(), () -> {

                velocity(originalVel);
                targetedPlayer(null);
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
            player.spawnParticle(Particle.VILLAGER_ANGRY, pos().clone().add(new Vector3(0,1.5,0)).toLocation(), 1);

            float originalVel = velocity();
            velocity(Preferences.PEDESTRIAN_MAX_VELOCITY*2);
            targetedPlayer(null);
            isInsideInteraction = true;

            Bukkit.getScheduler().runTaskLater(Main.plugin(), () -> {

                velocity(originalVel);
                isInsideInteraction = false;

            }, 20*2);

        }

    }

}