package com.runescape.loginscreen.cinematic.camera;

import com.runescape.loginscreen.cinematic.CameraLocation;

public class Camera {

    public Camera(CameraLocation position, double rotation, double tilt) {
        this.position = position;
        this.rotation = rotation;
        this.tilt = tilt;
    }

    private CameraLocation position;
    public double rotation, tilt;

    public Camera copy() {
        return new Camera(this.position,this.rotation,this.tilt);
    }

    public int getRotation() {
        return (int) Math.ceil(rotation);
    }

    public int getTilt() {
        return (int) Math.ceil(tilt);
    }

    public CameraLocation getPosition() {
        return position;
    }

    public void setPosition(CameraLocation position) {
        this.position = position;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    public void setTilt(double tilt) {
        this.tilt = tilt;
    }

}
