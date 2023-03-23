package me.json.pedestrians.objects.framework.path.connection;

import me.json.pedestrians.objects.framework.path.Node;
import me.json.pedestrians.utils.Vector3;

public class JunctionConnectionHandler implements ConnectionHandler {

    private final boolean hasToPrepare;

    public JunctionConnectionHandler(boolean hasToPrepare) {
        this.hasToPrepare = hasToPrepare;
    }

    @Override
    public boolean hasToPrepare() {
        return hasToPrepare;
    }

    @Override
    public Vector3 targetPos(Node originNode, Node targetNode1, Node targetNode2, float sideOffset) {

        ConnectionHandler directHandler = ConnectionHandlerType.DIRECT_CONNECTION_HANDLER.connectionHandler();

        //Origin --> Target1
        Vector3 originPos1 = directHandler.targetPos(null, originNode, null, sideOffset);
        Vector3 originPos2 = originPos1.clone().add( originNode.direction().clone().multiply(10));
        Line line1 = new Line(originPos1, originPos2);

        //Target --> Target1
        Vector3 target2Pos1 = directHandler.targetPos(null, targetNode2, null, sideOffset);
        Vector3 target2Pos2 = target2Pos1.clone().add( targetNode2.direction().clone().multiply(10));
        Line line2 = new Line(target2Pos1, target2Pos2);

        //Intersection
        return findIntersection(line1, line2, targetNode1.pos().y());
    }

    /*
    Credit to: rosettacode.org
     */
    private class Line {
        Vector3 s, e;

        Line(Vector3 s, Vector3 e) {
            this.s = s;
            this.e = e;
        }
    }

    private Vector3 findIntersection(Line l1, Line l2, double y) {
        double a1 = l1.e.z() - l1.s.z();
        double b1 = l1.s.x() - l1.e.x();
        double c1 = a1 * l1.s.x() + b1 * l1.s.z();

        double a2 = l2.e.z() - l2.s.z();
        double b2 = l2.s.x() - l2.e.x();
        double c2 = a2 * l2.s.x() + b2 * l2.s.z();

        double delta = a1 * b2 - a2 * b1;
        return new Vector3((b2 * c1 - b1 * c2) / delta, y, (a1 * c2 - a2 * c1) / delta);
    }

}
