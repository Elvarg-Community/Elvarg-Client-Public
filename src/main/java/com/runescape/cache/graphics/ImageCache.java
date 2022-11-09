package com.runescape.cache.graphics;

import com.runescape.Client;
import com.runescape.cache.graphics.sprite.Sprite;

import java.util.HashMap;

public final class ImageCache {

    private final static HashMap<Integer,Sprite> imageCache = new HashMap<>();

    private final static Sprite nulledImage = new Sprite(0, 0);

    public static Sprite get(int id) {
        if(id == -1) {
            return Sprite.EMPTY_SPRITE;
        }
        return get(id, true);
    }

    private static Sprite get(int id, boolean urgent) {
        Sprite image = imageCache.get(id);
        if(image == null) {
            if(Client.instance.resourceProvider != null) {
                Client.instance.resourceProvider.provide(4, id);
                if(urgent) {
                    Client.instance.processOnDemandQueue();
                }
            }
            return nulledImage;
        }

        return image;
    }

    public static synchronized void setImage(Sprite image, int id) {
        imageCache.put(id, image);
    }

    public static synchronized void clear() {
        imageCache.clear();
    }



}