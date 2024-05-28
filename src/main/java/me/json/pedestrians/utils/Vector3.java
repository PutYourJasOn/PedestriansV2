package me.json.pedestrians.utils;

import me.json.pedestrians.Main;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.joml.Vector3d;

public class Vector3 {

    private Vector3d vector3d;

    public Vector3(double x, double y, double z) {
        vector3d = new Vector3d(x, y, z);
    }

    public Vector3(Vector vector) {
        this(vector.getX(), vector.getY(), vector.getZ());
    }

    public Vector3(String string) {
        String[] args = string.trim().split(",");
        vector3d = new Vector3d(Double.parseDouble(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]));
    }

    //Getters
    public Vector3d vector3d() {
        return vector3d;
    }

    public double x() {
        return vector3d.x;
    }
    public double y() {
        return vector3d.y;
    }
    public double z() {
        return vector3d.z;
    }

    //Setters
    public void x(double x) {
        this.vector3d.x=x;
    }
    public void y(double y) {
        this.vector3d.y=y;
    }
    public void z(double z) {
        this.vector3d.z=z;
    }

    //Functionality
    public Vector3 subtract(double subtractor) {
        this.vector3d.sub(subtractor, subtractor, subtractor);
        return this;
    }

    public Vector3 subtract(Vector3 subtractor) {
        this.vector3d.sub(subtractor.vector3d);
        return this;
    }

    public Vector3 multiply(double multiplier) {
        this.vector3d.mul(multiplier);
        return this;
    }

    public Vector3 normalize() {
        this.vector3d.normalize();
        return this;
    }

    public Vector3 toRadians() {
        this.vector3d.mul(0.017453292519943295);
        return this;
    }

    public Vector3 divide(double divider) {
        this.vector3d.div(divider);
        return this;
    }

    public Vector3 add(double adder) {
        this.vector3d.add(adder, adder, adder);
        return this;
    }

    public Vector3 add(Vector3 adder) {
        this.vector3d.add(adder.vector3d);
        return this;
    }

    public float magnitude() {
        return (float) vector3d.length();
    }

    public Vector3 perpendicular() {
        return new Vector3(-this.z(), this.y(), this.x());
    }

    public Vector3 clone() {
        return new Vector3(this.x(), this.y(), this.z());
    }

    public Vector toBukkitVector() {
        return new Vector(x(),y(),z());
    }

    public Location toLocation() {
        return new Location(Main.world(), x(), y(), z());
    }

    public String toString() {
        return x()+", "+y()+", "+z();
    }

    public double distanceTo(Vector3 otherPos) {
        return vector3d.distance(otherPos.vector3d);
    }

    //+ Static
    public static Vector3 ZERO() {
        return new Vector3(0,0,0);
    }
    //-

}
