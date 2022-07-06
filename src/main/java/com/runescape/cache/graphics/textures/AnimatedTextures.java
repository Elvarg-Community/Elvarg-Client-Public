package com.runescape.cache.graphics.textures;

import com.runescape.Client;
import com.runescape.cache.graphics.IndexedImage;
import com.runescape.draw.Rasterizer3D;

public class AnimatedTextures {

    public static void render(int loaded) {
        for(AnimatedTextureStore data : AnimatedTextureStore.values()) {
            if(Rasterizer3D.textureLastUsed[data.getId()] >= loaded) {
                animate(data.getId(), data.getSpeed());
            }
        }
    }

    private static void animate(int id, int speed) {
        IndexedImage image = Rasterizer3D.textures[id].getImage();
        int size = (image.width * image.height) - 1;
        int step = (image.width * Client.instance.tickDelta * speed);
        byte[] raster = image.palettePixels;
        byte[] target = Client.instance.aByteArray912;
        for(int y = 0; y <= size; y++) {
            target[y] = raster[y - step & size];
        }
        image.palettePixels = target;
        Client.instance.aByteArray912 = raster;
        Rasterizer3D.requestTextureUpdate(id);
    }

}
