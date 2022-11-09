package com.runescape.loginscreen.flames;

public enum RuneImages {
    BAT(34),
    SKULL(36),
    MOON(37),
    PUMP(35);

    public int getId() {
        return id;
    }

    private final int id;

    RuneImages(int id) {
        this.id = id;
    }


}
