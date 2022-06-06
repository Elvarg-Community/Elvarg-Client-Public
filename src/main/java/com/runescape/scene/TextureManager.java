package com.runescape.scene;

import com.runescape.draw.Rasterizer3D;
import net.runelite.rs.api.RSTexture;
import net.runelite.rs.api.RSTextureProvider;

public class TextureManager implements RSTextureProvider {

    @Override
    public double getBrightness() {
        return Rasterizer3D.brightness;
    }

    @Override
    public void setBrightness(double brightness) {
        Rasterizer3D.setBrightness(brightness);
    }

    @Override
    public RSTexture[] getTextures() {
        return Rasterizer3D.getTextures();
    }

    @Override
    public int[] load(int textureId) {
        return Rasterizer3D.getTexturePixels(textureId);
    }

    @Override
    public void setMaxSize(int maxSize) {
    }

    @Override
    public void setSize(int size) {
    }
}