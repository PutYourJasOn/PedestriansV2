package me.json.pedestrians.listeners.nodeactions;

import me.json.pedestrians.objects.framework.events.NodeReachEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class HitboxTagListener implements Listener {

    //collision_true || collision_false

    @EventHandler
    public void onReach(NodeReachEvent e) {

        String tagValue = e.getTagValueByPrefix("collision");
        if(tagValue != null) {
            e.getPedestrian().collision(Boolean.parseBoolean(tagValue));
        }

    }

}
