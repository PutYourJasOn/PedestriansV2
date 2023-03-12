package me.json.pedestrians;

import me.json.pedestrians.commands.MainCommandHandler;
import me.json.pedestrians.data.importing.ImportSkins;
import me.json.pedestrians.listeners.JoinListener;
import me.json.pedestrians.objects.Skin;
import me.json.pedestrians.objects.framework.path.PathNetwork;
import me.json.pedestrians.ui.EditorView;
import me.json.pedestrians.ui.EditorViewInventory;
import me.json.pedestrians.ui.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.io.IOException;

public class Main extends JavaPlugin {

    private static JavaPlugin plugin;
    private static World world;
    private static EditorViewInventory editorViewInventory;

    @Override
    public void onEnable() {
        plugin = this;
        world = Bukkit.getWorlds().get(0);

        editorViewInventory = new EditorViewInventory();
        initFiles();
        initSkins();
        initListeners();
        initHiddenNamesTeam();

        this.getCommand("pedestrians").setExecutor(new MainCommandHandler());

    }

    @Override
    public void onDisable() {
        EditorView.Registry.editorViews().forEach(EditorView::stop);
        PathNetwork.Registry.pathNetworks().forEach(p -> p.removeAllPedestrians());
    }

    //Init
    private void initListeners() {
        Bukkit.getPluginManager().registerEvents(new NodeStandManipulateListener(), this);
        Bukkit.getPluginManager().registerEvents(new SelectFunctionListener(), this);
        Bukkit.getPluginManager().registerEvents(new SwitchFunctionListener(), this);
        Bukkit.getPluginManager().registerEvents(new AddFunctionListener(), this);
        Bukkit.getPluginManager().registerEvents(new RemoveFunctionListener(), this);
        Bukkit.getPluginManager().registerEvents(new ConnectionFunctionListener(), this);
        Bukkit.getPluginManager().registerEvents(new JoinListener(), this);
    }

    private void initHiddenNamesTeam() {

        ScoreboardManager manager = Bukkit.getScoreboardManager();

        Team team = manager.getMainScoreboard().getTeam("HIDDEN_NAMES");
        if(team == null)
            team = manager.getMainScoreboard().registerNewTeam("HIDDEN_NAMES");

        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
    }

    private void initFiles() {

        //Create folder
        File path = new File(Main.plugin().getDataFolder(),"pathnetworks");
        path.mkdirs();

        //Skins.yml
        saveResource("skins.txt", false);

    }

    private void initSkins() {

        new ImportSkins(v -> {

            if(Skin.Registry.randomSkin() == null) {
                this.getLogger().severe("No skins loaded! Plugin will be disabled.");
                this.getPluginLoader().disablePlugin(this);
            }

        }).start();

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
