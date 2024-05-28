package me.json.pedestrians;

import com.comphenix.protocol.ProtocolLibrary;
import me.json.pedestrians.commands.CommandHandler;
import me.json.pedestrians.data.importing.ImportConfig;
import me.json.pedestrians.data.importing.ImportSkins;
import me.json.pedestrians.listeners.JoinListener;
import me.json.pedestrians.listeners.nodeactions.ForcedVelocityTagListener;
import me.json.pedestrians.listeners.nodeactions.HitboxTagListener;
import me.json.pedestrians.listeners.packets.InteractListener;
import me.json.pedestrians.objects.Skin;
import me.json.pedestrians.objects.framework.path.PathNetwork;
import me.json.pedestrians.ui.EditorView;
import me.json.pedestrians.ui.EditorViewInventory;
import me.json.pedestrians.ui.listeners.Listener;
import me.json.pedestrians.ui.listeners.PacketListener;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.io.File;

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
        initConfig();
        initListeners();
        initHiddenNamesTeam();

        this.getCommand("pedestrians").setExecutor(new CommandHandler());

    }

    @Override
    public void onDisable() {
        EditorView.Registry.editorViews().forEach(EditorView::stop);
        PathNetwork.Registry.pathNetworks().forEach(PathNetwork::removeAllPedestrians);
    }

    //Init
    private void initListeners() {
        Bukkit.getPluginManager().registerEvents(new Listener(), this);
        Bukkit.getPluginManager().registerEvents(new JoinListener(), this);

        Bukkit.getPluginManager().registerEvents(new ForcedVelocityTagListener(), this);
        Bukkit.getPluginManager().registerEvents(new HitboxTagListener(), this);

        ProtocolLibrary.getProtocolManager().addPacketListener(new InteractListener());
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketListener());

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

        //skins.txt
        saveResource("skins.txt", false);

        //config.yml
        saveResource("config.yml", false);
    }

    private void initSkins() {

        new ImportSkins(v -> {

            if(Skin.Registry.randomSkin() == null) {
                this.getLogger().severe("No skins loaded! Plugin will be disabled.");
                this.getPluginLoader().disablePlugin(this);
            }

        }).start();

    }

    private void initConfig() {
        new ImportConfig().start();
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
