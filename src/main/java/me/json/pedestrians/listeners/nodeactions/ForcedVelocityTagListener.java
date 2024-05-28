package me.json.pedestrians.listeners.nodeactions;

import me.json.pedestrians.objects.framework.events.NodeReachEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ForcedVelocityTagListener implements Listener {

    //forcedVelocity_1.5 || forcedVelocity_none

    @EventHandler
    public void onReach(NodeReachEvent e) {

        String tagValue = e.getTagValueByPrefix("forcedVelocity");
        if(tagValue != null) {

            if(tagValue.equalsIgnoreCase("none")) {
                e.getPedestrian().forcedVelocity(null);
            } else {

                try {
                    e.getPedestrian().forcedVelocity(Float.parseFloat(tagValue));
                } catch (NumberFormatException ex) {
                    Bukkit.getLogger().warning("Node forcedVelocity tag wasn't formatted correctly. Current value: "+tagValue);
                }
            }

        }

    }

}
