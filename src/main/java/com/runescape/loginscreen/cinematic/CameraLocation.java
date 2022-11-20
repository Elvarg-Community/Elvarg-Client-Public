package com.runescape.loginscreen.cinematic;

public class CameraLocation {

    public CameraLocation(double x, double y, double z) {
        
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setZ(int z) {
        this.z = z;
    }

    double x;
    double y;
    double z;

    public int getX() {
        return (int) Math.ceil(x);
    }

    public int getY() {
        return (int) Math.ceil(y);
    }

    public int getZ() {
        return (int) Math.ceil(z);
    }

}
