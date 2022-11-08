package com.runescape.cache.def;

import com.runescape.Client;
import com.runescape.cache.FileArchive;
import com.runescape.cache.anim.Frame;
import com.runescape.cache.config.VariableBits;
import com.runescape.collection.ReferenceCache;
import com.runescape.entity.model.Model;
import com.runescape.io.Buffer;
import net.runelite.api.IterableHashTable;
import net.runelite.api.Node;
import net.runelite.api.ObjectComposition;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class ObjectDefinition implements ObjectComposition {

    public static final Model[] aModelArray741s = new Model[4];
    private static final int[] OBELISK_IDS = {14829, 14830, 14827, 14828, 14826, 14831};
    public static boolean lowMemory;
    public static Buffer stream;
    public static int[] streamIndices;
    public static Client clientInstance;
    public static int cacheIndex;
    public static ReferenceCache models = new ReferenceCache(30);
    public static ObjectDefinition[] cache;
    public static ReferenceCache baseModels = new ReferenceCache(500);
    public static int TOTAL_OBJECTS;
    public boolean obstructsGround;
    public byte ambient;
    public int translateX;
    public String name;
    public int scaleZ;
    public int contrast;
    public int sizeX;
    public int translateY;
    public int minimapFunction;
    public int[] recolorToReplace;
    public int scaleX;
    public int varpID;
    public boolean inverted;
    public int type;
    public boolean blocksProjectile;
    public int mapscene;
    public int configs[];
    public int supportItems;
    public int sizeY;
    public boolean contouredGround;
    public boolean occludes;
    public boolean removeClipping;
    public boolean interactType;
    public int surroundings;
    public boolean mergeNormals;
    public int scaleY;
    public int[] objectModels;
    public int varbitID;
    public int decorDisplacement;
    public int[] objectTypes;
    public String description;
    public boolean isInteractive;
    public boolean castsShadow;
    public int animation;
    public int translateZ;
    public int anInt2083;
    public int ambientSoundID;
    public int category;
    public int[] ambientSoundIds;
    private int ambientSoundId;
    private int int2083;
    private int anInt2112;
    private int anInt2113;
    private boolean randomAnimStart;
    private Map<Integer, Object> params = null;
    public int[] recolorToFind;
    public String actions[];
    private short[] textureReplace;
    private short[] textureFind;

    public ObjectDefinition() {
        type = -1;
    }

    public static void dumpNames() throws Exception {
        BufferedWriter writer = new BufferedWriter(new FileWriter("./Cache/object_names.txt"));
        for (int i = 0; i < TOTAL_OBJECTS; i++) {
            ObjectDefinition def = lookup(i);
            String name = def == null ? "null" : def.name;
            writer.write("ID: " + i + ", name: " + name + "");
            writer.newLine();
        }
        writer.close();
    }

    public static ObjectDefinition lookup(int id) {
        if (id > streamIndices.length)
            id = streamIndices.length - 1;
        for (int index = 0; index < 20; index++)
            if (cache[index].type == id)
                return cache[index];

		if (id == 25913)
			id = 15552;

		if (id == 25916 || id == 25926)
			id = 15553;

		if (id == 25917)
			id = 15554;

        cacheIndex = (cacheIndex + 1) % 20;
        ObjectDefinition objectDef = cache[cacheIndex];
        stream.currentPosition = streamIndices[id];
        objectDef.type = id;
        objectDef.reset();
        objectDef.decode(stream);

        for (int obelisk : OBELISK_IDS) {
            if (id == obelisk) {
                objectDef.actions = new String[]{"Activate", null, null, null, null};
            }
        }

        if (id == 29241) {
            objectDef.actions = new String[5];
            objectDef.actions[0] = "Restore-stats";
        }
        if (id == 4150) {
            objectDef.name = "Bank portal";
        } else if (id == 4151) {
            objectDef.name = "Ditch portal";
        }

        if (id == 26756) {
            objectDef.name = "Information";
            objectDef.actions = null;
        }

        if (id == 29150) {
            objectDef.actions = new String[5];
            objectDef.actions[0] = "Venerate";
            objectDef.actions[1] = "Switch-normal";
            objectDef.actions[2] = "Switch-ancient";
            objectDef.actions[3] = "Switch-lunar";
            objectDef.name = "Magical altar";
        }

        if (id == 6552) {
            objectDef.actions = new String[5];
            objectDef.actions[0] = "Toggle-spells";
            objectDef.name = "Ancient altar";
        }

        if (id == 14911) {
            objectDef.actions = new String[5];
            objectDef.actions[0] = "Toggle-spells";
            objectDef.name = "Lunar altar";
        }
        if (id == 2164) {
            objectDef.isInteractive = true;
            objectDef.actions = new String[5];
            objectDef.actions[0] = "Fix";
            objectDef.actions[1] = null;
            objectDef.name = "Trawler Net";
        }
        if (id == 1293) {
            objectDef.isInteractive = true;
            objectDef.actions = new String[5];
            objectDef.actions[0] = "Teleport";
            objectDef.actions[1] = null;
            objectDef.name = "Spirit Tree";
        }

        if (id == 2452) {
            objectDef.isInteractive = true;
            objectDef.actions = new String[5];
            objectDef.actions[0] = "Go Through";
            objectDef.name = "Passage";
        }
        switch (id) {
            case 10638:
                objectDef.isInteractive = true;
                return objectDef;
        }


        return objectDef;
    }

    public static void clear() {
        baseModels = null;
        models = null;
        streamIndices = null;
        cache = null;
        stream = null;
    }

    public static void init(FileArchive archive) throws IOException {
        stream = new Buffer(archive.readFile("loc.dat"));
        Buffer stream = new Buffer(archive.readFile("loc.idx"));
        TOTAL_OBJECTS = stream.readUShort();
        streamIndices = new int[TOTAL_OBJECTS];
        int offset = 2;
        for (int index = 0; index < TOTAL_OBJECTS; index++) {
            streamIndices[index] = offset;
            offset += stream.readUShort();
        }
        cache = new ObjectDefinition[20];
        for (int index = 0; index < 20; index++)
            cache[index] = new ObjectDefinition();

        System.out.println("Loaded: " + TOTAL_OBJECTS + " objects");
    }

    public void reset() {
        objectModels = null;
        objectTypes = null;
        name = null;
        description = null;
        recolorToFind = null;
        recolorToReplace = null;
        textureFind = null;
        textureReplace = null;
        sizeX = 1;
        sizeY = 1;
        interactType = true;
        blocksProjectile = true;
        isInteractive = false;
        contouredGround = false;
        mergeNormals = false;
        occludes = false;
        animation = -1;
        decorDisplacement = 16;
        ambient = 0;
        contrast = 0;
        actions = null;
        minimapFunction = -1;
        mapscene = -1;
        inverted = false;
        castsShadow = true;
        scaleX = 128;
        scaleY = 128;
        scaleZ = 128;
        surroundings = 0;
        translateX = 0;
        translateY = 0;
        translateZ = 0;
        obstructsGround = false;
        removeClipping = false;
        supportItems = -1;
        varbitID = -1;
        varpID = -1;
        configs = null;
    }

    public boolean method577(int i) {
        if (objectTypes == null) {
            if (objectModels == null)
                return true;
            if (i != 10)
                return true;
            boolean flag1 = true;
            for (int k = 0; k < objectModels.length; k++)
                flag1 &= Model.isCached(objectModels[k] & 0xffff);

            return flag1;
        }
        for (int j = 0; j < objectTypes.length; j++)
            if (objectTypes[j] == i)
                return Model.isCached(objectModels[j] & 0xffff);

        return true;
    }

    public Model modelAt(int type, int orientation, int aY, int bY, int cY, int dY, int frameId) {
        Model model = model(type, frameId, orientation);
        if (model == null)
            return null;
        if (contouredGround || mergeNormals)
            model = new Model(contouredGround, mergeNormals, model);
        if (contouredGround) {
            int y = (aY + bY + cY + dY) / 4;
            for (int vertex = 0; vertex < model.verticesCount; vertex++) {
                int x = model.verticesX[vertex];
                int z = model.verticesZ[vertex];
                int l2 = aY + ((bY - aY) * (x + 64)) / 128;
                int i3 = dY + ((cY - dY) * (x + 64)) / 128;
                int j3 = l2 + ((i3 - l2) * (z + 64)) / 128;
                model.verticesY[vertex] += j3 - y;
            }

            model.normalise();
        }
        return model;
    }

    public boolean method579() {
        if (objectModels == null)
            return true;
        boolean flag1 = true;
        for (int i = 0; i < objectModels.length; i++)
            flag1 &= Model.isCached(objectModels[i] & 0xffff);
        return flag1;
    }

    public ObjectDefinition method580() {
        int i = -1;
        if (varbitID != -1) {
            VariableBits varBit = VariableBits.varbits[varbitID];
            int j = varBit.getSetting();
            int k = varBit.getLow();
            int l = varBit.getHigh();
            int i1 = Client.BIT_MASKS[l - k];
            i = clientInstance.settings[j] >> k & i1;
        } else if (varpID != -1)
            i = clientInstance.settings[varpID];
        if (i < 0 || i >= configs.length || configs[i] == -1)
            return null;
        else
            return lookup(configs[i]);
    }

    public Model model(int j, int k, int l) {
        Model model = null;
        long l1;
        if (objectTypes == null) {
            if (j != 10)
                return null;
            l1 = (long) ((type << 6) + l) + ((long) (k + 1) << 32);
            Model model_1 = (Model) models.get(l1);
            if (model_1 != null) {
                return model_1;
            }
            if (objectModels == null)
                return null;
            boolean flag1 = inverted ^ (l > 3);
            int k1 = objectModels.length;
            for (int i2 = 0; i2 < k1; i2++) {
                int l2 = objectModels[i2];
                if (flag1)
                    l2 += 0x10000;
                model = (Model) baseModels.get(l2);
                if (model == null) {
                    model = Model.getModel(l2 & 0xffff);
                    if (model == null)
                        return null;
                    if (flag1)
                        model.mirror();
                    baseModels.put(model, l2);
                }
                if (k1 > 1)
                    aModelArray741s[i2] = model;
            }

            if (k1 > 1)
                model = new Model(k1, aModelArray741s);
        } else {
            int i1 = -1;
            for (int j1 = 0; j1 < objectTypes.length; j1++) {
                if (objectTypes[j1] != j)
                    continue;
                i1 = j1;
                break;
            }

            if (i1 == -1)
                return null;
            l1 = (long) ((type << 8) + (i1 << 3) + l) + ((long) (k + 1) << 32);
            Model model_2 = (Model) models.get(l1);
            if (model_2 != null) {
                return model_2;
            }
            if (objectModels == null) {
                return null;
            }
            int j2 = objectModels[i1];
            boolean flag3 = inverted ^ (l > 3);
            if (flag3)
                j2 += 0x10000;
            model = (Model) baseModels.get(j2);
            if (model == null) {
                model = Model.getModel(j2 & 0xffff);
                if (model == null)
                    return null;
                if (flag3)
                    model.mirror();
                baseModels.put(model, j2);
            }
        }
        boolean flag;
        flag = scaleX != 128 || scaleY != 128 || scaleZ != 128;
        boolean flag2;
        flag2 = translateX != 0 || translateY != 0 || translateZ != 0;
        Model model_3 = new Model(recolorToFind == null,
                Frame.noAnimationInProgress(k), l == 0 && k == -1 && !flag
                && !flag2, textureFind == null, model);
        if (k != -1) {
            model_3.generateBones();
            model_3.animate(k);
            model_3.faceGroups = null;
            model_3.vertexGroups = null;
        }
        while (l-- > 0)
            model_3.rotate90Degrees();

        if (recolorToFind != null) {
            for (int k2 = 0; k2 < recolorToFind.length; k2++)
                model_3.recolor(recolorToFind[k2],
                        recolorToReplace[k2]);

        }
        if (textureFind != null) {
            for (int k2 = 0; k2 < textureFind.length; k2++)
                model_3.retexture(textureFind[k2],
                        textureReplace[k2]);

        }
        if (flag)
            model_3.scale(scaleX, scaleZ, scaleY);
        if (flag2)
            model_3.offsetBy(translateX, translateY, translateZ);
        model_3.light(85 + ambient, 768 + contrast, -50, -10, -50, !mergeNormals);
        if (supportItems == 1)
            model_3.itemDropHeight = model_3.modelBaseY;
        models.put(model_3, l1);
        return model_3;
    }


    public void decode(Buffer buffer) {
        while(true) {
            int opcode = buffer.readUnsignedByte();

            if (opcode == 0) {
                break;
            } else if (opcode == 1) {
                int len = buffer.readUnsignedByte();
                if (len > 0) {
                    if (objectModels == null) {
                        objectTypes = new int[len];
                        objectModels = new int[len];

                        for (int i = 0; i < len; i++) {
                            objectModels[i] = buffer.readUShort();
                            objectTypes[i] = buffer.readUnsignedByte();
                        }
                    } else {
                        buffer.currentPosition += len * 3;
                    }
                }
            } else if (opcode == 2) {
                name = buffer.readJagexString();
            } else if (opcode == 5) {
                int len = buffer.readUnsignedByte();
                if (len > 0) {
                    if (objectModels == null) {
                        objectTypes = null;
                        objectModels = new int[len];
                        for (int i = 0; i < len; i++) {
                            objectModels[i] = buffer.readUShort();
                        }
                    } else {
                        buffer.currentPosition += len * 2;
                    }
                }
            } else if (opcode == 14) {
                sizeX = buffer.readUnsignedByte();
            } else if (opcode == 15) {
                sizeY = buffer.readUnsignedByte();
            } else if (opcode == 17) {
                interactType = false;
                blocksProjectile = false;
            } else if (opcode == 18) {
                blocksProjectile = false;
            } else if (opcode == 19) {
                isInteractive = (buffer.readUnsignedByte() == 1);
            } else if (opcode == 21) {
                contouredGround = true;
            } else if (opcode == 22) {
                mergeNormals = true;
            } else if (opcode == 23) {
                occludes = true;
            } else if (opcode == 24) {
                animation = buffer.readUShort();
                if (animation == 0xFFFF) {
                    animation = -1;
                }
            } else if (opcode == 27) {
                interactType = true;
            } else if (opcode == 28) {
                decorDisplacement = buffer.readUnsignedByte();
            } else if (opcode == 29) {
                ambient = buffer.readSignedByte();
            } else if (opcode == 39) {
                contrast = buffer.readSignedByte() * 25;
            } else if (opcode >= 30 && opcode < 35) {
                if (actions == null) {
                    actions = new String[5];
                }
                actions[opcode - 30] = buffer.readString();
                if (actions[opcode - 30].equalsIgnoreCase("Hidden")) {
                    actions[opcode - 30] = null;
                }
            } else if (opcode == 40) {
                int len = buffer.readUnsignedByte();
                recolorToFind = new int[len];
                recolorToReplace = new int[len];
                for (int i = 0; i < len; i++) {
                    recolorToFind[i] = buffer.readUShort();
                    recolorToReplace[i] = buffer.readUShort();
                }
            } else if (opcode == 41) {
                int len = buffer.readUnsignedByte();
                textureFind = new short[len];
                textureReplace = new short[len];
                for (int i = 0; i < len; i++) {
                    textureFind[i] = (short) buffer.readUShort();
                    textureReplace[i] = (short) buffer.readUShort();
                }
            } else if (opcode == 61) {
                category = buffer.readUShort();
            } else if (opcode == 62) {
                inverted = true;
            } else if (opcode == 64) {
                castsShadow = false;
            } else if (opcode == 65) {
                scaleX = buffer.readUShort();
            } else if (opcode == 66) {
                scaleY = buffer.readUShort();
            } else if (opcode == 67) {
                scaleZ = buffer.readUShort();
            } else if (opcode == 68) {
                mapscene = buffer.readUShort();
            } else if (opcode == 69) {
                surroundings = buffer.readUnsignedByte();
            } else if (opcode == 70) {
                translateX = buffer.readUShort();
            } else if (opcode == 71) {
                translateY = buffer.readUShort();
            } else if (opcode == 72) {
                translateZ = buffer.readUShort();
            } else if (opcode == 73) {
                obstructsGround = true;
            } else if (opcode == 74) {
                removeClipping = true;
            } else if (opcode == 75) {
                supportItems = buffer.readUnsignedByte();
            } else if (opcode == 78) {
                ambientSoundID = buffer.readUShort(); // ambient sound id
                anInt2083 = buffer.readUnsignedByte();
            } else if (opcode == 79) {
                anInt2112 = buffer.readUShort();
                anInt2113 = buffer.readUShort();
                anInt2083 = buffer.readUShort();

                int length = buffer.readUnsignedByte();
                int[] anims = new int[length];

                for (int index = 0; index < length; ++index)
                {
                    anims[index] = buffer.readUShort();
                }
                ambientSoundIds = anims;
            } else if (opcode == 81) {
                buffer.readUnsignedByte();
            } else if (opcode == 82) {
                minimapFunction = buffer.readUShort();
            } else if (opcode == 89) {
                randomAnimStart = true;
            } else if (opcode == 77 || opcode == 92) {
                varpID = buffer.readUShort();

                if (varpID == 0xFFFF) {
                    varpID = -1;
                }

                varbitID = buffer.readUShort();

                if (varbitID == 0xFFFF) {
                    varbitID = -1;
                }

                int value = -1;

                if (opcode == 92) {
                    value = buffer.readUShort();

                    if (value == 0xFFFF) {
                        value = -1;
                    }
                }

                int len = buffer.readUnsignedByte();

                configs = new int[len + 2];
                for (int i = 0; i <= len; ++i) {
                    configs[i] = buffer.readUShort();
                    if (configs[i] == 0xFFFF) {
                        configs[i] = -1;
                    }
                }
                configs[len + 1] = value;
            } else if (opcode == 249) {
                int length = buffer.readUnsignedByte();

                Map<Integer, Object> params = new HashMap<>(length);
                for (int i = 0; i < length; i++)
                {
                    boolean isString = buffer.readUnsignedByte() == 1;
                    int key = buffer.read24Int();
                    Object value;

                    if (isString) {
                        value = buffer.readString();
                        System.out.println(value);
                    } else {
                        value = buffer.readInt();
                    }

                    params.put(key, value);
                }

                this.params = params;
            } else {

            }

        }

        if (name != null && !name.equals("null")) {
            isInteractive = objectModels != null && (objectTypes == null || objectTypes[0] == 10);
            if (actions != null)
                isInteractive = true;
        }

        if (removeClipping) {
            interactType = false;
            blocksProjectile = false;
        }

        if (supportItems == -1) {
            supportItems = interactType ? 1 : 0;
        }
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String[] getActions() {
        return new String[0];
    }

    @Override
    public int getMapSceneId() {
        return 0;
    }

    @Override
    public int getMapIconId() {
        return 0;
    }

    @Override
    public int[] getImpostorIds() {
        return new int[0];
    }

    @Override
    public ObjectComposition getImpostor() {
        return null;
    }

    @Override
    public int getAccessBitMask() {
        return 0;
    }

    @Override
    public IterableHashTable<Node> getParams() {
        return null;
    }

    @Override
    public void setParams(IterableHashTable<Node> params) {

    }

    @Override
    public int getIntValue(int paramID) {
        return 0;
    }

    @Override
    public void setValue(int paramID, int value) {

    }

    @Override
    public String getStringValue(int paramID) {
        return null;
    }

    @Override
    public void setValue(int paramID, String value) {

    }
}