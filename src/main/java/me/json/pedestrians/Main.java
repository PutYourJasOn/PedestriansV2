package me.json.pedestrians;

import me.json.pedestrians.commands.MainCommandHandler;
import me.json.pedestrians.objects.Skin;
import me.json.pedestrians.objects.framework.pedestrian.PedestrianGroup;
import me.json.pedestrians.ui.EditorView;
import me.json.pedestrians.ui.EditorViewInventory;
import me.json.pedestrians.ui.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static JavaPlugin plugin;
    private static World world;
    private static EditorViewInventory editorViewInventory;

    @Override
    public void onEnable() {
        plugin = this;
        world = Bukkit.getWorlds().get(0);

        editorViewInventory = new EditorViewInventory();
        initListeners();

        this.getCommand("pedestrians").setExecutor(new MainCommandHandler());

        //TODO: import from file
        new Skin("ewogICJ0aW1lc3RhbXAiIDogMTY3ODExNTA3NDI2MiwKICAicHJvZmlsZUlkIiA6ICI0NzQ3YWVmMjAxNjY0ZDk3YWRhMzQzMzg1ZGU2NTE5NSIsCiAgInByb2ZpbGVOYW1lIiA6ICJQdXRZb3VySmFzT24iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmMzNTdhZWM5MWViZGE5YTFjMmJiNDFmMjBkNmRjMmIzZDQ3OTlkYjFkY2NlODU3NzJhMWU2NWFmMjdkYTY5ZSIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9",
                "oZiqlBEt+WG2FKPY890cHE4diqPWsL80oue7acsDD9vHj+ykckwOitN4/28awOKrqul5N+Tp17x7lBztoAN7j7ob28mpIZiKToWFvMwUGMUhRpGqc0++HhNqENBqzpjWGqbUzaNmKF0EGUMPfb7U13uXrcgW4Afu6NddIdXKe6ISgw2j4Wog2ydOVuwV3C1jlpo5wFKyuOLTfSV+uMiAoKpGUMWNZmhRKAf7aS2o+zw/0HpwC2TnHuo+dVblS4L9tXozsmSA/eLVMSdYxLg/T7YzCZFaHEYuHtod9UtkySHr+Y/PBxtiPImemRBf9VTZHEp8vkDfeewdRpzqPUfPXR93A5OcjWVyqujb+ElU2rXfeHgNNuyUXA7htATb/LTD/f8Ohng7Ogjs7Vr07vtG8RbUK8+Fs4zpzHigR95wGyin5HJhs9Vv514ggO2iUHHqAHepRbu9H2Wqf58+8CzVF6BYPjZQavOIfBanIgEwxGITRv3x6WL3+9CJBvO7sJGfEPCGLn2d+Zqs3hQTpiy+iUM+Ph6YNYSrSblYv3FfvN2j6hLPcRsJ5uDLoQy9KdizIWSPmiaNDFs1JbuLgfZD4+xmUseOpJWcMjDIOJ5kirb9vXE+Iba+pSP8PEkaXx5pr365nxp8awfRWlJAKZidNLoDCA+QAhUGxSOJldcyLxQ=");

    }

    @Override
    public void onDisable() {
        EditorView.Registry.editorViews().forEach(EditorView::stop);
        PedestrianGroup.Registry.pedestrianGroups().forEach(PedestrianGroup::remove);
    }

    //Init
    private void initListeners() {
        Bukkit.getPluginManager().registerEvents(new NodeStandManipulateListener(), this);
        Bukkit.getPluginManager().registerEvents(new SelectFunctionListener(), this);
        Bukkit.getPluginManager().registerEvents(new SwitchFunctionListener(), this);
        Bukkit.getPluginManager().registerEvents(new AddFunctionListener(), this);
        Bukkit.getPluginManager().registerEvents(new RemoveFunctionListener(), this);
        Bukkit.getPluginManager().registerEvents(new ConnectionFunctionListener(), this);

    }

    //Getters
    public static JavaPlugin plugin() {
        return plugin;
    }
    public static World world() {
        return world;
    }
    public static EditorViewInventory editorViewInventory() {
        return editorViewInventory;
    }

}