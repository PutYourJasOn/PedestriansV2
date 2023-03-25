package me.json.pedestrians.data.importing;

import me.json.pedestrians.Main;
import me.json.pedestrians.objects.framework.path.Node;
import me.json.pedestrians.objects.framework.path.PathNetwork;
import me.json.pedestrians.objects.framework.path.connection.Connection;
import me.json.pedestrians.objects.framework.path.connection.ConnectionHandler;
import me.json.pedestrians.objects.framework.path.connection.ConnectionHandler.ConnectionHandlerType;
import me.json.pedestrians.utils.Vector3;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.function.Consumer;

public class ImportPathNetwork extends BukkitRunnable {

    private final String name;
    private final Consumer<PathNetwork> callback;

    public ImportPathNetwork(String name, Consumer<PathNetwork> callback) {
        this.name=name;
        this.callback=callback;
    }

    public void start() {
        this.runTaskAsynchronously(Main.plugin());
    }

    @Override
    public void run() {

        PathNetwork pathNetwork = new PathNetwork(name);
        File path = new File(Main.plugin().getDataFolder(),"pathnetworks/"+name+".json");

        if(!path.exists()) {
            callback.accept(null);
            return;
        }

        try (Reader reader = new FileReader(path)) {

            JSONParser parser = new JSONParser();

            JSONObject jsonObject = (JSONObject) parser.parse(reader);
            JSONArray jsonNodes = (JSONArray) jsonObject.get("nodes");

            //Init the Nodes
            for (Object objectNode : jsonNodes) {
                JSONObject jsonNode = (JSONObject) objectNode;
                pathNetwork.addNode(createNode(jsonNode));
            }

            //Init the Connections
            for (Object objectNode : jsonNodes) {
                JSONObject jsonNode = (JSONObject) objectNode;
                Node node = pathNetwork.node((int) (long) jsonNode.get("id"));
                if(node == null) throw new NullPointerException("Node "+jsonNode.get("id")+" in PathNetwork: "+pathNetwork.name()+" doesn't exist.");

                JSONArray jsonConnections = (JSONArray) jsonNode.get("connections");

                for (Object objectConnection : jsonConnections) {
                    JSONObject jsonConnection = (JSONObject) objectConnection;

                    int connectedNodeID = (int) (long) jsonConnection.get("id");
                    //int probability = (int) (long) jsonConnection.get("probability");
                    String connectionHandlerTitle = (String) jsonConnection.get("connection_handler");

                    ConnectionHandler connectionHandler = ConnectionHandlerType.valueOf(connectionHandlerTitle).connectionHandler();
                    Node connectedNode = pathNetwork.node(connectedNodeID);
                    Connection connection = new Connection(connectionHandler);

                    node.registerConnectedNode(connectedNode, connection);
                }
            }

            //Done
            callback.accept(pathNetwork);

        } catch (IOException | ParseException ex) {
            ex.printStackTrace();
        }

    }

    private Node createNode(JSONObject jsonNode) {

        int id = (int) (long) jsonNode.get("id");
        Vector3 position = new Vector3((String) jsonNode.get("position"));
        float width = (float) (double) jsonNode.get("width");
        Vector3 direction = new Vector3((String) jsonNode.get("direction"));

        return new Node(id, position, width, direction);
    }

}
