package me.json.pedestrians.objects;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Skin {

    private final String name;
    private final String base64;
    private final String signature;

    public Skin(String name, String base64, String signature) {
        this.name = name;
        this.base64 = base64;
        this.signature = signature;

        Registry.register(this);
    }

    public String base64() {
        return base64;
    }

    public String signature() {
        return signature;
    }

    public String name() {
        return name;
    }

    public static class Registry {

        private final static Set<Skin> skins = new HashSet<>();

        public static void register(Skin skin) {
            skins.add(skin);
        }

        public static Skin randomSkin() {
            return skins.isEmpty() ? null : skins.stream().skip(new Random().nextInt(skins.size())).findFirst().orElse(null);
        }

    }

}
