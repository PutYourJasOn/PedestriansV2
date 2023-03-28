package me.json.pedestrians.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import me.json.pedestrians.Main;
import me.json.pedestrians.data.importing.ImportAutoSpawns;
import me.json.pedestrians.data.importing.ImportPathNetwork;
import me.json.pedestrians.objects.PlayerPedestrianEntity;
import me.json.pedestrians.objects.Skin;
import me.json.pedestrians.objects.framework.pedestrian.Pedestrian;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class JoinListener implements Listener {

    //Static +++
    public static final Set<Player> justJoinedPlayers = new HashSet<>();
    public static ImportAutoSpawns importAutoSpawns;
    //Static ---

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        //Send packet to add all players named " " to the HIDDEN_NAMES team
        sendAddToTeamPacket(e.getPlayer());

        //Handle just joined
        justJoinedPlayers.add(e.getPlayer());
        Bukkit.getScheduler().runTaskLater(Main.plugin(), () -> justJoinedPlayers.remove(e.getPlayer()), 20*3);

        //Spawn autospawn network on first join
        if(importAutoSpawns == null) {

            importAutoSpawns = new ImportAutoSpawns(map -> {

                for (String name : map.keySet()) {

                    new ImportPathNetwork(name, p -> {
                        for (int i = 0; i < map.get(name); i++) {
                            new Pedestrian(p, new PlayerPedestrianEntity(Skin.Registry.randomSkin()), p.randomNode());
                        }
                    }, true).start();

                }

            }, false);

            importAutoSpawns.start();
        }

    }

    private void sendAddToTeamPacket(Player player) {
        PacketContainer packet4 = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
        packet4.getStrings().write(0, "HIDDEN_NAMES");
        packet4.getIntegers().write(0, 3);

        Collection<String> entityIDs = new ArrayList<>();
        entityIDs.add(" ");
        packet4.getSpecificModifier(Collection.class).write(0, entityIDs);

        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet4);
    }

}