package me.json.pedestrians.utils;

import me.json.pedestrians.Main;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class RayTraceUtil {

    public static Vector roundedRayTraceResult(Player player) {
        Vector v = rayTraceResult(player);
        if(v == null) return null;

        float x = Math.round(v.getX()*2)/2f;
        float z = Math.round(v.getZ()*2)/2f;
        return new Vector(x,v.getY(), z);
    }

    public static Vector rayTraceResult(Player player) {

        RayTraceResult rayTraceResult = Main.world().rayTraceBlocks(player.getEyeLocation(), player.getEyeLocation().getDirection(), 20);
        return rayTraceResult == null ? null : rayTraceResult.getHitPosition();
    }

}
