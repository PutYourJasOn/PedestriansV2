package me.json.pedestrians.objects.framework.path.connection;

import me.json.pedestrians.objects.framework.path.Node;
import me.json.pedestrians.utils.Vector3;

public interface ConnectionHandler {

    //+ Static
    enum ConnectionHandlerEnum {

        DIRECT_CONNECTION_HANDLER(new DirectConnectionHandler()),
        TO_JUNCTION_HANDLER(new JunctionConnectionHandler(false)),
        FROM_JUNCTION_HANDLER(new JunctionConnectionHandler(true));

        private final ConnectionHandler connectionHandler;

        ConnectionHandlerEnum(ConnectionHandler connectionHandler) {
            this.connectionHandler=connectionHandler;
        }

        public static ConnectionHandlerEnum connectionHandlerEnum(ConnectionHandler connectionHandler) {
            for (ConnectionHandlerEnum value : ConnectionHandlerEnum.values()) {
                if (value.connectionHandler.equals(connectionHandler)) {
                    return value;
                }
            }
            return null;
        }

        public ConnectionHandler connectionHandler() {
            return this.connectionHandler;
        }

    }

    //-
    Vector3 targetPos(Node originNode, Node targetNode1, Node targetNode2, float sideOffset);

    boolean hasToPrepare();

}