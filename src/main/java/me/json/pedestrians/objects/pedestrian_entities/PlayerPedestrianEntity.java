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

        /*
        Bukkit.getScheduler().runTaskAsynchronously(Main.plugin(), () -> {

            try {

                URL url = new URL("https://api.ashcon.app/mojang/v2/user/" + uuid.toString());
                URLConnection connection = url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
                jsonObject = jsonObject.get("textures").getAsJsonObject().get("raw").getAsJsonObject();
                base64 = jsonObject.get("value").getAsString();
                signature = jsonObject.get("signature").getAsString();

                reader.close();

            } catch (IOException ex) {
                ex.printStackTrace();
            }

        });

         */

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