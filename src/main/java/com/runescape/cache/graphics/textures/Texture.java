package com.runescape.cache.graphics.textures;

import com.runescape.cache.graphics.IndexedImage;
import net.runelite.rs.api.RSNode;
import net.runelite.rs.api.RSTexture;

public class Texture implements RSTexture {

    public final IndexedImage image;
    public final AnimatedTextureStore animatedTextureStore;
    public boolean loaded = false;

    public Texture(IndexedImage image, AnimatedTextureStore animatedTextureStore) {
        this.image = image;
        this.animatedTextureStore = animatedTextureStore;
        this.loaded = true;

    }

    public IndexedImage getImage() {
        return image;
    }


    private float textureU;
    private float textureV;

    public float getU() {
        return textureU;
    }
    public void setU(float u) {
        textureU = u;
    }

    public float getV() {
        return textureV;
    }
    public void setV(float v) {
        textureV = v;
    }

    public int[] getPixels() {
        return image.palette;
    }

    public int getAnimationSpeed() {
        return animatedTextureStore.getSpeed();
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    public int getAnimationDirection() {
        return animatedTextureStore.getDirection();
    }


    @Override
    public RSNode getNext() {
        return null;
    }

    @Override
    public long getHash() {
        return 0;
    }

    @Override
    public RSNode getPrevious() {
        return null;
    }

    @Override
    public void unlink() {

    }

    @Override
    public void onUnlink() {

    }


}
