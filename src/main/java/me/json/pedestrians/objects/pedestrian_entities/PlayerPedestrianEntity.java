package me.json.pedestrians.objects.pedestrian_entities;

import me.json.pedestrians.objects.Skin;
import me.json.pedestrians.objects.framework.pedestrian.Pedestrian;
import me.json.pedestrians.objects.framework.pedestrian.PedestrianEntity;
import me.json.pedestrians.utils.NPC;
import org.bukkit.Location;

public class PlayerPedestrianEntity implements PedestrianEntity {

    private Skin skin;
    private NPC npc;

    public PlayerPedestrianEntity(Skin skin) {
        this.skin = skin;
    }

    @Override
    public PedestrianEntity initialize(Pedestrian pedestrian) {
        return this;
    }

    @Override
    public PedestrianEntity spawn(Location location) {

        npc = new NPC("", location, skin.base64(), skin.signature());
        skin = null;
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
}