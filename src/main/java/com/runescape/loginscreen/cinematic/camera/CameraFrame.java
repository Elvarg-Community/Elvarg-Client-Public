package com.runescape.loginscreen.cinematic.camera;

import com.runescape.loginscreen.cinematic.CameraLocation;

public class CameraFrame {

    public CameraFrame(CameraLocation targetLocation, int rotation, int tilt, int frames, boolean moveScene) {
        this.moveScene = moveScene;
        this.targetLocation = targetLocation;
        this.rotation = rotation;
        this.tilt = tilt;
        this.frames = frames;
    }

    public CameraFrame(CameraLocation targetLocation, int rotation, int tilt, int frames) {
        this.targetLocation = targetLocation;
        this.rotation = rotation;
        this.tilt = tilt;
        this.frames = frames;
    }

    public void setMoveScene(boolean moveScene) {
        this.moveScene = moveScene;
    }

    private boolean moveScene;

    private CameraLocation targetLocation;
    private CameraLocation jumpLocation;
    public int rotation = -1;
    public int tilt = -1;
    private int frames = 500;

    public void setStart(int start) {
        this.start = start;
    }

    public boolean isMoveScene() {
        return moveScene;
    }

    public CameraLocation getTargetLocation() {
        return targetLocation;
    }

    public CameraLocation getJumpLocation() {
        return jumpLocation;
    }

    public int getRotation() {
        return rotation;
    }

    public int getTilt() {
        return tilt;
    }

    public int getFrames() {
        return frames;
    }

    public int getStart() {
        return start;
    }

    private int start;

    public boolean hasFrame(int frameNum) {
        return frameNum >= start && frameNum <= getEnd();
    }

    public int getEnd() {
        return start + frames;
    }

}