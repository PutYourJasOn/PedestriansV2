package me.json.pedestrians.objects.framework.path.connection;

import me.json.pedestrians.objects.framework.path.Node;
import me.json.pedestrians.utils.InterpolationUtil;
import me.json.pedestrians.utils.Vector3;

public class DirectConnectionHandler implements ConnectionHandler {

    @Override
    public Vector3 targetPos(Node originNode, Node node, Node targetNode2, float sideOffset) {

        Vector3 perpendicularDirection = node.direction().perpendicular();
        Vector3 halvedPerpendicularLine = perpendicularDirection.clone().multiply(node.width()).divide(2);

        Vector3 maxPos = node.pos().clone().add(halvedPerpendicularLine);
        Vector3 minPos = node.pos().clone().subtract(halvedPerpendicularLine);

        return InterpolationUtil.lerp(minPos, maxPos, sideOffset);
    }

    public Vector3 targetPos(Vector3 pos, Vector3 dir, double width, float sideOffset) {

        Vector3 perpendicularDirection = dir.perpendicular();
        Vector3 halvedPerpendicularLine = perpendicularDirection.clone().multiply(width).divide(2);

        Vector3 maxPos = pos.clone().add(halvedPerpendicularLine);
        Vector3 minPos = pos.clone().subtract(halvedPerpendicularLine);

        return InterpolationUtil.lerp(minPos, maxPos, sideOffset);
    }

    @Override
    public boolean hasToPrepare() {
        return false;
    }
}
