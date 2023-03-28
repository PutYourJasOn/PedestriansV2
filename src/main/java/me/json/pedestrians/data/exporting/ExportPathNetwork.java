package me.json.pedestrians.data.exporting;

import me.json.pedestrians.Main;
import me.json.pedestrians.objects.framework.path.Node;
import me.json.pedestrians.objects.framework.path.PathNetwork;
import me.json.pedestrians.objects.framework.path.connection.Connection;
import me.json.pedestrians.objects.framework.path.connection.ConnectionHandler;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.Consumer;

public class ExportPathNetwork extends BukkitRunnable {

    private final PathNetwork pathNetwork;
    private final Consumer<Void> callback;

    public ExportPathNetwork(PathNetwork pathNetwork, Consumer<Void> callback) {
        this.pathNetwork=pathNetwork;
        this.callback=callback;
    }

    public void start() {
        this.runTaskAsynchronously(Main.plugin());
    }

    @Override
    public void run() {

        File path = new File(Main.plugin().getDataFolder(),"pathnetworks/"+pathNetwork.name()+".json");
        JSONObject json = new JSONObject();
        JSONArray jsonNodes = new JSONArray();

        for (Node node : pathNetwork.nodes()) {
            JSONObject jsonNode = new JSONObject();
            jsonNode.put("id",node.id());
            jsonNode.put("position", node.pos().toString());
            jsonNode.put("direction", node.direction().toString());
            jsonNode.put("width", node.width());

            JSONArray jsonConnections = new JSONArray();
            for (Node connectedNode : node.connectedNodes()) {
                Connection connection = node.connection(connectedNode);

                JSONObject jsonConnection = new JSONObject();
                jsonConnection.put("id",connectedNode.id());
                //jsonConnection.put("probability",connection.probability());
                jsonConnection.put("connection_handler", ConnectionHandler.ConnectionHandlerType.connectionHandlerEnum(connection.connectionHandler()).name());
                jsonConnections.add(jsonConnection);
            }

            jsonNode.put("connections", jsonConnections);
            jsonNodes.add(jsonNode);
        }

        json.put("nodes", jsonNodes);

        try (FileWriter writer = new FileWriter(path)) {
            writer.write(json.toJSONString());

            callback.accept(null);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
