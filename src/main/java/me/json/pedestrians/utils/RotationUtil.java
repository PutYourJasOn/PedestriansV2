package me.json.pedestrians.utils;

public class RotationUtil {

    public static byte floatToByte(float f) {
        return (byte)((int)(f * 256.0F / 360.0F));
    }

}
