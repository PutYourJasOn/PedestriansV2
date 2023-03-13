package me.json.pedestrians.objects.framework.pedestrian;

import com.comphenix.protocol.wrappers.WrappedEnumEntityUseAction;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface PedestrianEntity {

    PedestrianEntity initialize(Pedestrian pedestrian);

    PedestrianEntity spawn(Location location);

    void remove();

    void asyncMove(Location location);

    void interact(Player player, WrappedEnumEntityUseAction entityUseAction);

}
