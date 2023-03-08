package me.json.pedestrians.utils;

import me.json.pedestrians.Main;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class Vector3 {

    private double x,y,z;

    public Vector3(double x, double y, double z) {
        this.x=x; this.y=y; this.z=z;
    }

    public Vector3(Vector vector) {
        this(vector.getX(), vector.getY(), vector.getZ());
    }

    public Vector3(String string) {
        String[] args = string.trim().split(",");
        x=Double.parseDouble(args[0]); y=Double.parseDouble(args[1]); z=Double.parseDouble(args[2]);
    }

    //Getters
    public double x() {
        return x;
    }
    public double y() {
        return y;
    }
    public double z() {
        return z;
    }

    //Setters
    public void x(double x) {
        this.x=x;
    }
    public void y(double y) {
        this.y=y;
    }
    public void z(double z) {
        this.z=z;
    }

    //Functionality
    public Vector3 subtract(double subtractor) {
        this.x -= subtractor;
        this.y -= subtractor;
        this.z -= subtractor;
        return this;
    }

    public Vector3 subtract(Vector3 subtractor) {
        this.x -= subtractor.x;
        this.y -= subtractor.y;
        this.z -= subtractor.z;
        return this;
    }

    public Vector3 multiply(double multiplier) {
        this.x *= multiplier;
        this.y *= multiplier;
        this.z *= multiplier;
        return this;
    }

    public Vector3 divide(double divider) {
        this.x /= divider;
        this.y /= divider;
        this.z /= divider;
        return this;
    }

    public Vector3 add(double adder) {
        this.x += adder;
        this.y += adder;
        this.z += adder;
        return this;
    }

    public Vector3 add(Vector3 adder) {
        this.x += adder.x;
        this.y += adder.y;
        this.z += adder.z;
        return this;
    }

    public float magnitude() {
        return (float) Math.sqrt(x*x+y*y+z*z);
    }

    public Vector3 perpendicular() {
        return new Vector3(-this.z, this.y, this.x);
    }

    public Vector3 clone() {
        return new Vector3(this.x, this.y, this.z);
    }

    public Vector toBukkitVector() {
        return new Vector(x,y,z);
    }

    public Location toLocation() {
        return new Location(Main.world(), x, y, z);
    }

    public String toString() {
        return x+", "+y+", "+z;
    }

    //+ Static
    public static Vector3 ZERO() {
        return new Vector3(0,0,0);
    }
    //-

}
