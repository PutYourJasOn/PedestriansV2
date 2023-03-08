package me.json.pedestrians.utils;

import java.util.HashSet;
import java.util.Set;

public class InterpolationUtil {

    public static float floatAngleLerp (float a, float b, float t) {
        float delta = ((b - a + 360 + 180) % 360) - 180;
        return (a + delta * t + 360) % 360;
    }

    public static Vector3 lerp(Vector3 a, Vector3 b, float t){

        Vector3 newVector = Vector3.ZERO();

        newVector.x(doubleLerp(a.x(),b.x(),t));
        newVector.y(doubleLerp(a.y(),b.y(),t));
        newVector.z(doubleLerp(a.z(),b.z(),t));

        return newVector;
    }

    public static double doubleLerp(double a, double b, float t){
        return a + ( b - a ) * t;
    }

    public static Set<Vector3> lineVertices(Vector3 from, Vector3 to, int vertexCount) {

        Set<Vector3> vertices = new HashSet<>();

        for(float i = 0; i < 1; i=i + (1/(float)vertexCount)){
            vertices.add(lerp(from, to, i));
        }

        return vertices;
    }


}
