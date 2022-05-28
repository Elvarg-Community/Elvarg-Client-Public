package com.runescape.cache.anim;

import com.runescape.cache.FileArchive;
import com.runescape.collection.ReferenceCache;
import com.runescape.entity.model.Model;
import com.runescape.io.Buffer;

public final class Graphic {

    public static Graphic cache[];
    public static ReferenceCache models = new ReferenceCache(30);
    private int[] recolorToFind;
    private int[] recolorToReplace;
    public Animation animationSequence;
    public int resizeXY;
    public int resizeZ;
    public int rotation;
    public int modelBrightness;
    public int modelShadow;
    private int anInt404;
    private int modelId;
    private int animationId;

    private Graphic() {
        animationId = -1;
        recolorToFind = new int[6];
        recolorToReplace = new int[6];
        resizeXY = 128;
        resizeZ = 128;
    }

    public static void init(FileArchive archive) {
        Buffer stream = new Buffer(archive.readFile("spotanim.dat"));
        int length = stream.readUShort();
        if (cache == null)
            cache = new Graphic[length + 1];
        for (int index = 0; index < length; index++) {
            if (cache[index] == null)
                cache[index] = new Graphic();
            cache[index].anInt404 = index;
            cache[index].readValues(stream);
        }

        System.out.println("Loaded: " + length + " graphics");
    }

    public short[] textureReplace;
    public short[] textureFind;

    public void readValues(Buffer buffer) {
        while(true) {
            final int opcode = buffer.readUnsignedByte();

            if (opcode == 0) {
                return;
            } else if (opcode == 1) {
                modelId = buffer.readUShort();
            } else if (opcode == 2) {
                animationId = buffer.readUShort();

                if (Animation.animations != null)
                    animationSequence = Animation.animations[animationId];
            } else if (opcode == 4) {
                resizeXY = buffer.readUShort();
            } else if (opcode == 5) {
                resizeZ = buffer.readUShort();
            } else if (opcode == 6) {
                rotation = buffer.readUShort();
            } else if (opcode == 7) {
                modelBrightness = buffer.readUnsignedByte();
            } else if (opcode == 8) {
                modelShadow = buffer.readUnsignedByte();
            } else if (opcode == 40) {
                int len = buffer.readUnsignedByte();
                recolorToFind = new int[len];
                recolorToReplace = new int[len];
                for (int i = 0; i < len; i++) {
                    recolorToFind[i] = buffer.readUShort();
                    recolorToReplace[i] = buffer.readUShort();
                }
            } else if (opcode == 41) { // re-texture
                int len = buffer.readUnsignedByte();
                textureFind = new short[len];
                textureReplace = new short[len];
                for (int i = 0; i < len; i++) {
                    textureFind[i] = (short) buffer.readUShort();
                    textureReplace[i] = (short) buffer.readUShort();
                }
            } else {
                System.out.println("gfx invalid opcode: " + opcode);
            }
        }
    }

    public Model getModel() {
        Model model = (Model) models.get(anInt404);
        if (model != null)
            return model;
        model = Model.getModel(modelId);
        if (model == null)
            return null;
        for (int i = 0; i < 6; i++)
            if (recolorToFind[0] != 0)
                model.recolor(recolorToFind[i], recolorToReplace[i]);

        models.put(model, anInt404);
        return model;
    }
}
