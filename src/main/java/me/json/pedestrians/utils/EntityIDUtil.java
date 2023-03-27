package me.json.pedestrians.utils;

import me.json.pedestrians.Main;
import me.json.pedestrians.objects.entities.ClientEntity;

import java.util.Random;

public class EntityIDUtil {

    public static int newEntityID() {
        int id = new Random().nextInt();
        while(ClientEntity.Registry.exists(id) || entityExists(id)) {
            id = new Random().nextInt();
        }
        return id;
    }

    private static boolean entityExists(int id) {
        return Main.world().getEntities().stream().filter(e -> e.getEntityId() == id).findFirst().isPresent();
    }

}
