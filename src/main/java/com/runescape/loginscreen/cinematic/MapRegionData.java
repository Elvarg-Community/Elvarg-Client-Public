package com.runescape.loginscreen.cinematic;

import com.runescape.cache.ResourceProvider;
import com.runescape.scene.MapRegion;
import lombok.Builder;


@Builder
public class MapRegionData {

    public int getRegionX() {
        return regionX;
    }

    public void setRegionX(int regionX) {
        this.regionX = regionX;
    }

    public int getRegionY() {
        return regionY;
    }

    public void setRegionY(int regionY) {
        this.regionY = regionY;
    }

    public int getHash() {
        return hash;
    }

    public void setHash(int hash) {
        this.hash = hash;
    }

    public int getLandscape() {
        return landscape;
    }

    public void setLandscape(int landscape) {
        this.landscape = landscape;
    }

    public int getObjects() {
        return objects;
    }

    public void setObjects(int objects) {
        this.objects = objects;
    }

    public byte[] getLandscapeData() {
        return landscapeData;
    }

    public void setLandscapeData(byte[] landscapeData) {
        this.landscapeData = landscapeData;
    }

    public byte[] getObjectsData() {
        return objectsData;
    }

    private int regionX, regionY, hash;
    private int landscape, objects;
    private byte[] landscapeData, objectsData;

    public void requestFiles(ResourceProvider resourceProvider) {
        resourceProvider.provide(3, objects);
        resourceProvider.provide(3, landscape);

    }

    public void setObjectsData(byte[] objectsData) {
        this.objectsData = objectsData;
        MapRegion.requestModelPreload(objectsData);
    }
}