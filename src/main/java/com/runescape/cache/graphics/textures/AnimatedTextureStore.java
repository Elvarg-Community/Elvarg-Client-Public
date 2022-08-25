package com.runescape.cache.graphics.textures;

public enum AnimatedTextureStore {

    WATER_DROPLETS(17, 2,1),
    WATER(24, 2,1),
    MAGIC_TREE_STARS(34, 2,1),
    LAVA(40, 2,1),
    CRIMSON_LAVA(56, 2,1),
    GRAY_LAVA(57, 2,1),
    INFERNAL_LAVA(59, 1,1),
    SMOKE(103, 2,4),
    SMOKE_LOTS(104, 1,4),
    NONE(0,0,0);

    private final int materialId;
    private final int speed;
    private final int direction;
    AnimatedTextureStore(int materialId, int speed, int direction) {
        this.materialId = materialId;
        this.speed = speed;
        this.direction = direction;
    }

    public int getId() {
        return materialId;
    }

    public int getSpeed() {
        return speed;
    }

    public int getDirection() {
        return direction;
    }

    public static AnimatedTextureStore get(int id) {
        for(AnimatedTextureStore data : AnimatedTextureStore.values()) {
            if(data.getId() == id) {
                return data;
            }
        }
        return AnimatedTextureStore.NONE;
    }

}