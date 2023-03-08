package me.json.pedestrians.objects.framework.pedestrian;

import org.bukkit.Location;

public interface PedestrianEntity {

    PedestrianEntity initialize(Pedestrian pedestrian);

    PedestrianEntity spawn(Location location);

    void remove();

    void asyncMove(Location location);


}
