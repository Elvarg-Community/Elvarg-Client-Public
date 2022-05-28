package com.runescape.entity.model;

import com.runescape.Client;
import com.runescape.cache.anim.Frame;
import com.runescape.cache.anim.FrameBase;
import com.runescape.draw.Rasterizer2D;
import com.runescape.draw.Rasterizer3D;
import com.runescape.entity.Renderable;
import com.runescape.io.Buffer;
import com.runescape.scene.SceneGraph;

public class Model extends Renderable {

    public static int anInt1620;
    public static Model EMPTY_MODEL = new Model(true);
    public static boolean aBoolean1684;
    public static int anInt1685;
    public static int anInt1686;
    public static int obj_loaded;
    public static long obj_key[] = new long[1000];
    public static int SINE[];
    public static int COSINE[];
    static ModelHeader modelHeader[];
    static boolean hasAnEdgeToRestrict[] = new boolean[4700];
    static boolean outOfReach[] = new boolean[4700];
    static int projected_vertex_x[] = new int[4700];
    static int projected_vertex_y[] = new int[4700];
    static int projected_vertex_z[] = new int[4700];
    static int camera_vertex_x[] = new int[4700];
    static int camera_vertex_y[] = new int[4700];
    static int camera_vertex_z[] = new int[4700];
    static int depthListIndices[] = new int[1600];

    static int faceLists[][] = new int[1600][512];
    static int anIntArray1673[] = new int[12];
    static int anIntArrayArray1674[][] = new int[12][2000];
    static int anIntArray1675[] = new int[2000];
    static int anIntArray1676[] = new int[2000];
    static int anIntArray1677[] = new int[12];
    static int anIntArray1678[] = new int[10];
    static int anIntArray1679[] = new int[10];
    static int anIntArray1680[] = new int[10];
    static int xAnimOffset;
    static int yAnimOffset;
    static int zAnimOffset;
    static int modelIntArray3[];
    static int modelIntArray4[];
    private static int anIntArray1622[] = new int[2000];
    private static int anIntArray1623[] = new int[2000];
    private static int anIntArray1624[] = new int[2000];
    private static byte anIntArray1625[] = new byte[2000];

    static {
        SINE = Rasterizer3D.anIntArray1470;
        COSINE = Rasterizer3D.COSINE;
        modelIntArray3 = Rasterizer3D.hslToRgb;
        modelIntArray4 = Rasterizer3D.anIntArray1469;
    }


    public int numVertices;
    public int vertexX[];
    public int vertexY[];
    public short[] materials;
    public byte[] textures;
    public byte[] textureTypes;
    public int animayaGroups[][];
    public int animayaScales[][];
    public int vertexZ[];
    public int trianglesCount;
    public int facePointA[];
    public int facePointB[];
    public int facePointC[];
    public int faceHslA[];
    public int faceHslB[];
    public int faceHslC[];
    public int faceDrawType[];
    public byte face_render_priorities[];
    public byte[] face_alpha;
    public short triangleColours[];
    public byte face_priority = 0;
    public int numberOfTexturesFaces;
    public short textures_face_a[];
    public short textures_face_b[];
    public short textures_face_c[];
    public int minimumXVertex;
    public int maximumXVertex;
    public int maximumZVertex;
    public int minimumZVertex;
    public int maxVertexDistanceXZPlane;
    public int maximumYVertex;
    public int maxRenderDepth;
    public int diagonal3DAboveOrigin;
    public int itemDropHeight;
    public int vertexVSkin[];
    public int triangleTSkin[];
    public int vertexGroups[][];
    public int faceGroups[][];
    public boolean fits_on_single_square;
    public VertexNormal alsoVertexNormals[];
    private boolean aBoolean1618;

    public Model(int modelId) {
        byte[] data = modelHeader[modelId].aByteArray368;

        if (data[data.length - 1] == -3 && data[data.length - 2] == -1) {
            ModelLoader.decodeType3(this, data);
        } else if (data[data.length - 1] == -2 && data[data.length - 2] == -1) {
            ModelLoader.decodeType2(this, data);
        } else if (data[data.length - 1] == -1 && data[data.length - 2] == -1) {
            ModelLoader.decodeType1(this, data);
        } else {
            ModelLoader.decodeOldFormat(this, data);
        }
    }

    private Model(boolean flag) {
        aBoolean1618 = true;
        fits_on_single_square = false;
        if (!flag)
            aBoolean1618 = !aBoolean1618;
    }

    public Model(int length, Model model_segments[], boolean preset) {
        try {
            aBoolean1618 = true;
            fits_on_single_square = false;

            anInt1620++;
            boolean render_type_flag = false;
            boolean priority_flag = false;
            boolean alpha_flag = false;
            boolean muscle_skin_flag = false;
            boolean color_flag = false;
            boolean texture_flag = false;
            boolean coordinate_flag = false;
            numVertices = 0;
            trianglesCount = 0;
            numberOfTexturesFaces = 0;
            face_priority = -1;
            Model build;
            for (int segment_index = 0; segment_index < length; segment_index++) {
                build = model_segments[segment_index];
                if (build != null) {
                    numVertices += build.numVertices;
                    trianglesCount += build.trianglesCount;
                    numberOfTexturesFaces += build.numberOfTexturesFaces;
                    if (build.face_render_priorities != null) {
                        priority_flag = true;
                    } else {
                        if (face_priority == -1)
                            face_priority = build.face_priority;

                        if (face_priority != build.face_priority)
                            priority_flag = true;
                    }
                    render_type_flag |= build.faceDrawType != null;
                    alpha_flag |= build.face_alpha != null;
                    muscle_skin_flag |= build.triangleTSkin != null;
                    color_flag |= build.triangleColours != null;
                    texture_flag |= build.materials != null;
                    coordinate_flag |= build.textures != null;
                }
            }
            vertexX = new int[numVertices];
            vertexY = new int[numVertices];
            vertexZ = new int[numVertices];
            vertexVSkin = new int[numVertices];
            facePointA = new int[trianglesCount];
            facePointB = new int[trianglesCount];
            facePointC = new int[trianglesCount];
            if (color_flag) {
                triangleColours = new short[trianglesCount];
            }

            if (render_type_flag)
                faceDrawType = new int[trianglesCount];

            if (priority_flag)
                face_render_priorities = new byte[trianglesCount];

            if (alpha_flag)
                face_alpha = new byte[trianglesCount];

            if (muscle_skin_flag)
                triangleTSkin = new int[trianglesCount];

            if (texture_flag)
                materials = new short[trianglesCount];

            if (coordinate_flag)
                textures = new byte[trianglesCount];

            if (numberOfTexturesFaces > 0) {
                textureTypes = new byte[numberOfTexturesFaces];
                textures_face_a = new short[numberOfTexturesFaces];
                textures_face_b = new short[numberOfTexturesFaces];
                textures_face_c = new short[numberOfTexturesFaces];
            }

            numVertices = 0;
            trianglesCount = 0;
            numberOfTexturesFaces = 0;
            for (int segment_index = 0; segment_index < length; segment_index++) {
                build = model_segments[segment_index];
                if (build != null) {
                    for (int face = 0; face < build.trianglesCount; face++) {
                        if (render_type_flag && build.faceDrawType != null)
                            faceDrawType[trianglesCount] = build.faceDrawType[face];

                        if (priority_flag)
                            if (build.face_render_priorities == null)
                                face_render_priorities[trianglesCount] = build.face_priority;
                            else
                                face_render_priorities[trianglesCount] = build.face_render_priorities[face];

                        if (alpha_flag && build.face_alpha != null)
                            face_alpha[trianglesCount] = build.face_alpha[face];

                        if (muscle_skin_flag && build.triangleTSkin != null)
                            triangleTSkin[trianglesCount] = build.triangleTSkin[face];

                        if (texture_flag) {
                            if (build.materials != null)
                                materials[trianglesCount] = build.materials[face];
                            else
                                materials[trianglesCount] = -1;
                        }
                        if (coordinate_flag) {
                            if (build.textures != null && build.textures[face] != -1) {
                                textures[trianglesCount] = (byte) (build.textures[face] + numberOfTexturesFaces);
                            } else {
                                textures[trianglesCount] = -1;
                            }
                        }

                        if (color_flag && build.triangleColours != null)
                            triangleColours[trianglesCount] = build.triangleColours[face];

                        facePointA[trianglesCount] = method465(build, build.facePointA[face]);
                        facePointB[trianglesCount] = method465(build, build.facePointB[face]);
                        facePointC[trianglesCount] = method465(build, build.facePointC[face]);
                        trianglesCount++;
                    }
                    for (int texture_edge = 0; texture_edge < build.numberOfTexturesFaces; texture_edge++) {
                        byte opcode = textureTypes[numberOfTexturesFaces] = build.textureTypes[texture_edge];
                        if (opcode == 0) {
                            textures_face_a[numberOfTexturesFaces] = (short) method465(build, build.textures_face_a[texture_edge]);
                            textures_face_b[numberOfTexturesFaces] = (short) method465(build, build.textures_face_b[texture_edge]);
                            textures_face_c[numberOfTexturesFaces] = (short) method465(build, build.textures_face_c[texture_edge]);
                        }
                        if (opcode >= 1 && opcode <= 3) {
                            textures_face_a[numberOfTexturesFaces] = build.textures_face_a[texture_edge];
                            textures_face_b[numberOfTexturesFaces] = build.textures_face_b[texture_edge];
                            textures_face_c[numberOfTexturesFaces] = build.textures_face_c[texture_edge];
                        }
                        if (opcode == 2) {

                        }
                        numberOfTexturesFaces++;
                    }
                    if (!preset) //for models that don't have preset textured_faces
                        numberOfTexturesFaces++;

                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public Model(Model amodel[]) {
        int i = 2;
        aBoolean1618 = true;
        fits_on_single_square = false;
        anInt1620++;
        boolean flag1 = false;
        boolean flag2 = false;
        boolean flag3 = false;
        boolean flag4 = false;
        boolean texture_flag = false;
        boolean coordinate_flag = false;
        numVertices = 0;
        trianglesCount = 0;
        numberOfTexturesFaces = 0;
        face_priority = -1;
        for (int k = 0; k < i; k++) {
            Model model = amodel[k];
            if (model != null) {
                numVertices += model.numVertices;
                trianglesCount += model.trianglesCount;
                numberOfTexturesFaces += model.numberOfTexturesFaces;
                flag1 |= model.faceDrawType != null;
                if (model.face_render_priorities != null) {
                    flag2 = true;
                } else {
                    if (face_priority == -1)
                        face_priority = model.face_priority;
                    if (face_priority != model.face_priority)
                        flag2 = true;
                }
                flag3 |= model.face_alpha != null;
                flag4 |= model.triangleColours != null;
                texture_flag |= model.materials != null;
                coordinate_flag |= model.textures != null;
            }
        }

        vertexX = new int[numVertices];
        vertexY = new int[numVertices];
        vertexZ = new int[numVertices];
        facePointA = new int[trianglesCount];
        facePointB = new int[trianglesCount];
        facePointC = new int[trianglesCount];
        faceHslA = new int[trianglesCount];
        faceHslB = new int[trianglesCount];
        faceHslC = new int[trianglesCount];
        textures_face_a = new short[numberOfTexturesFaces];
        textures_face_b = new short[numberOfTexturesFaces];
        textures_face_c = new short[numberOfTexturesFaces];
        if (flag1)
            faceDrawType = new int[trianglesCount];
        if (flag2)
            face_render_priorities = new byte[trianglesCount];
        if (flag3)
            face_alpha = new byte[trianglesCount];
        if (flag4) {
            triangleColours = new short[trianglesCount];
        }
        if (texture_flag)
            materials = new short[trianglesCount];

        if (coordinate_flag)
            textures = new byte[trianglesCount];
        numVertices = 0;
        trianglesCount = 0;
        numberOfTexturesFaces = 0;
        int i1 = 0;
        for (int j1 = 0; j1 < i; j1++) {
            Model model_1 = amodel[j1];
            if (model_1 != null) {
                int k1 = numVertices;
                for (int l1 = 0; l1 < model_1.numVertices; l1++) {
                    int x = model_1.vertexX[l1];
                    int y = model_1.vertexY[l1];
                    int z = model_1.vertexZ[l1];
                    vertexX[numVertices] = x;
                    vertexY[numVertices] = y;
                    vertexZ[numVertices] = z;
                    ++numVertices;
                }

                for (int uid = 0; uid < model_1.trianglesCount; uid++) {
                    facePointA[trianglesCount] = model_1.facePointA[uid] + k1;
                    facePointB[trianglesCount] = model_1.facePointB[uid] + k1;
                    facePointC[trianglesCount] = model_1.facePointC[uid] + k1;
                    faceHslA[trianglesCount] = model_1.faceHslA[uid];
                    faceHslB[trianglesCount] = model_1.faceHslB[uid];
                    faceHslC[trianglesCount] = model_1.faceHslC[uid];
                    if (flag1)
                        if (model_1.faceDrawType == null) {
                            faceDrawType[trianglesCount] = 0;
                        } else {
                            int j2 = model_1.faceDrawType[uid];
                            if ((j2 & 2) == 2)
                                j2 += i1 << 2;
                            faceDrawType[trianglesCount] = j2;
                        }
                    if (flag2)
                        if (model_1.face_render_priorities == null)
                            face_render_priorities[trianglesCount] = model_1.face_priority;
                        else
                            face_render_priorities[trianglesCount] = model_1.face_render_priorities[uid];
                    if (flag3) {
                        if (model_1.face_alpha == null)
                            face_alpha[trianglesCount] = 0;
                        else
                            face_alpha[trianglesCount] = model_1.face_alpha[uid];

                    }
                    if (flag4 && model_1.triangleColours != null) {
                        triangleColours[trianglesCount] = model_1.triangleColours[uid];
                    }

                    if (texture_flag) {
                        if (model_1.materials != null) {
                            materials[trianglesCount] = model_1.materials[trianglesCount];
                        } else {
                            materials[trianglesCount] = -1;
                        }
                    }

                    if (coordinate_flag) {
                        if (model_1.textures != null && model_1.textures[trianglesCount] != -1)
                            textures[trianglesCount] = (byte) (model_1.textures[trianglesCount] + numberOfTexturesFaces);
                        else
                            textures[trianglesCount] = -1;

                    }

                    trianglesCount++;
                }

                for (int k2 = 0; k2 < model_1.numberOfTexturesFaces; k2++) {
                    textures_face_a[numberOfTexturesFaces] = (short) (model_1.textures_face_a[k2] + k1);
                    textures_face_b[numberOfTexturesFaces] = (short) (model_1.textures_face_b[k2] + k1);
                    textures_face_c[numberOfTexturesFaces] = (short) (model_1.textures_face_c[k2] + k1);
                    numberOfTexturesFaces++;
                }

                i1 += model_1.numberOfTexturesFaces;
            }
        }

        calculateDistances();
    }

    public Model(boolean color_flag, boolean alpha_flag, boolean animated, Model model) {
        this(color_flag, alpha_flag, animated, false, model);
    }

    public void replace(Model model, boolean alpha_flag) {
        numVertices = model.numVertices;
        trianglesCount = model.trianglesCount;
        numberOfTexturesFaces = model.numberOfTexturesFaces;
        if (anIntArray1622.length < numVertices) {
            anIntArray1622 = new int[numVertices + 10000];
            anIntArray1623 = new int[numVertices + 10000];
            anIntArray1624 = new int[numVertices + 10000];
        }
        vertexX = anIntArray1622;
        vertexY = anIntArray1623;
        vertexZ = anIntArray1624;
        for (int point = 0; point < numVertices; point++) {
            vertexX[point] = model.vertexX[point];
            vertexY[point] = model.vertexY[point];
            vertexZ[point] = model.vertexZ[point];
        }
        if (alpha_flag) {
            face_alpha = model.face_alpha;
        } else {
            if (anIntArray1625.length < trianglesCount)
                anIntArray1625 = new byte[trianglesCount + 100];

            face_alpha = anIntArray1625;
            if (model.face_alpha == null) {
                for (int face = 0; face < trianglesCount; face++)
                    face_alpha[face] = 0;

            } else {
                for (int face = 0; face < trianglesCount; face++)
                    face_alpha[face] = model.face_alpha[face];

            }
        }
        faceDrawType = model.faceDrawType;
        triangleColours = model.triangleColours;
        face_render_priorities = model.face_render_priorities;
        face_priority = model.face_priority;
        faceGroups = model.faceGroups;
        vertexGroups = model.vertexGroups;
        facePointA = model.facePointA;
        facePointB = model.facePointB;
        facePointC = model.facePointC;
        faceHslA = model.faceHslA;
        faceHslB = model.faceHslB;
        faceHslC = model.faceHslC;
        textures_face_a = model.textures_face_a;
        textures_face_b = model.textures_face_b;
        textures_face_c = model.textures_face_c;
        textures = model.textures;
        textureTypes = model.textureTypes;
        materials = model.materials;
    }

    public Model(boolean color_flag, boolean alpha_flag, boolean animated, boolean texture_flag, Model model) {
        aBoolean1618 = true;
        fits_on_single_square = false;
        anInt1620++;
        numVertices = model.numVertices;
        trianglesCount = model.trianglesCount;
        numberOfTexturesFaces = model.numberOfTexturesFaces;
        if (animated) {
            vertexX = model.vertexX;
            vertexY = model.vertexY;
            vertexZ = model.vertexZ;
        } else {
            vertexX = new int[numVertices];
            vertexY = new int[numVertices];
            vertexZ = new int[numVertices];
            for (int point = 0; point < numVertices; point++) {
                vertexX[point] = model.vertexX[point];
                vertexY[point] = model.vertexY[point];
                vertexZ[point] = model.vertexZ[point];
            }

        }

        if (color_flag) {
            triangleColours = model.triangleColours;
        } else {
            triangleColours = new short[trianglesCount];
            for (int face = 0; face < trianglesCount; face++) {
                triangleColours[face] = model.triangleColours[face];
            }

        }

        if(!texture_flag && model.materials != null) {
            materials = new short[trianglesCount];
            for(int face = 0; face < trianglesCount; face++) {
                materials[face] = model.materials[face];
            }
        } else {
            materials = model.materials;
        }

        if (alpha_flag) {
            face_alpha = model.face_alpha;
        } else {
            face_alpha = new byte[trianglesCount];
            if (model.face_alpha == null) {
                for (int l = 0; l < trianglesCount; l++)
                    face_alpha[l] = 0;

            } else {
                for (int i1 = 0; i1 < trianglesCount; i1++)
                    face_alpha[i1] = model.face_alpha[i1];

            }
        }
        vertexVSkin = model.vertexVSkin;
        triangleTSkin = model.triangleTSkin;
        faceDrawType = model.faceDrawType;
        facePointA = model.facePointA;
        facePointB = model.facePointB;
        facePointC = model.facePointC;
        face_render_priorities = model.face_render_priorities;
        textures = model.textures;
        textureTypes = model.textureTypes;
        face_priority = model.face_priority;
        textures_face_a = model.textures_face_a;
        textures_face_b = model.textures_face_b;
        textures_face_c = model.textures_face_c;
    }

    public Model(boolean adjust_elevation, boolean gouraud_shading, Model model) {
        aBoolean1618 = true;
        fits_on_single_square = false;
        anInt1620++;
        numVertices = model.numVertices;
        trianglesCount = model.trianglesCount;
        numberOfTexturesFaces = model.numberOfTexturesFaces;
        if (adjust_elevation) {
            vertexY = new int[numVertices];
            for (int point = 0; point < numVertices; point++)
                vertexY[point] = model.vertexY[point];

        } else {
            vertexY = model.vertexY;
        }
        if (gouraud_shading) {
            faceHslA = new int[trianglesCount];
            faceHslB = new int[trianglesCount];
            faceHslC = new int[trianglesCount];
            for (int face = 0; face < trianglesCount; face++) {
                faceHslA[face] = model.faceHslA[face];
                faceHslB[face] = model.faceHslB[face];
                faceHslC[face] = model.faceHslC[face];
            }

            faceDrawType = new int[trianglesCount];
            if (model.faceDrawType == null) {
                for (int face = 0; face < trianglesCount; face++)
                    faceDrawType[face] = 0;

            } else {
                for (int face = 0; face < trianglesCount; face++)
                    faceDrawType[face] = model.faceDrawType[face];

            }
            super.vertexNormals = new VertexNormal[numVertices];
            for (int point = 0; point < numVertices; point++) {
                VertexNormal class33 = super.vertexNormals[point] = new VertexNormal();
                VertexNormal class33_1 = model.vertexNormals[point];
                class33.normalX = class33_1.normalX;
                class33.normalY = class33_1.normalY;
                class33.normalZ = class33_1.normalZ;
                class33.magnitude = class33_1.magnitude;
            }
            alsoVertexNormals = model.alsoVertexNormals;

        } else {
            faceHslA = model.faceHslA;
            faceHslB = model.faceHslB;
            faceHslC = model.faceHslC;
            faceDrawType = model.faceDrawType;
        }
        vertexX = model.vertexX;
        vertexZ = model.vertexZ;
        facePointA = model.facePointA;
        facePointB = model.facePointB;
        facePointC = model.facePointC;
        face_render_priorities = model.face_render_priorities;
        face_alpha = model.face_alpha;
        textures = model.textures;
        triangleColours = model.triangleColours;
        materials = model.materials;
        face_priority = model.face_priority;
        textureTypes = model.textureTypes;
        textures_face_a = model.textures_face_a;
        textures_face_b = model.textures_face_b;
        textures_face_c = model.textures_face_c;
        super.modelBaseY = model.modelBaseY;
        maxVertexDistanceXZPlane = model.maxVertexDistanceXZPlane;
        diagonal3DAboveOrigin = model.diagonal3DAboveOrigin;
        maxRenderDepth = model.maxRenderDepth;
        minimumXVertex = model.minimumXVertex;
        maximumZVertex = model.maximumZVertex;
        minimumZVertex = model.minimumZVertex;
        maximumXVertex = model.maximumXVertex;
    }


    public static void clear() {
        modelHeader = null;
        hasAnEdgeToRestrict = null;
        outOfReach = null;
        projected_vertex_y = null;
        projected_vertex_z = null;
        camera_vertex_x = null;
        camera_vertex_y = null;
        camera_vertex_z = null;
        depthListIndices = null;
        faceLists = null;
        anIntArray1673 = null;
        anIntArrayArray1674 = null;
        anIntArray1675 = null;
        anIntArray1676 = null;
        anIntArray1677 = null;
        SINE = null;
        COSINE = null;
        modelIntArray3 = null;
        modelIntArray4 = null;
    }

    public static void method460(byte abyte0[], int j) {
        try {
            if (abyte0 == null) {
                ModelHeader class21 = modelHeader[j] = new ModelHeader();
                class21.anInt369 = 0;
                class21.anInt370 = 0;
                class21.anInt371 = 0;
                return;
            }
            Buffer stream = new Buffer(abyte0);
            stream.currentPosition = abyte0.length - 18;
            ModelHeader class21_1 = modelHeader[j] = new ModelHeader();
            class21_1.aByteArray368 = abyte0;
            class21_1.anInt369 = stream.readUShort();
            class21_1.anInt370 = stream.readUShort();
            class21_1.anInt371 = stream.readUnsignedByte();
            int k = stream.readUnsignedByte();
            int l = stream.readUnsignedByte();
            int i1 = stream.readUnsignedByte();
            int j1 = stream.readUnsignedByte();
            int k1 = stream.readUnsignedByte();
            int l1 = stream.readUShort();
            int i2 = stream.readUShort();
            int j2 = stream.readUShort();
            int k2 = stream.readUShort();
            int l2 = 0;
            class21_1.anInt372 = l2;
            l2 += class21_1.anInt369;
            class21_1.anInt378 = l2;
            l2 += class21_1.anInt370;
            class21_1.anInt381 = l2;
            if (l == 255)
                l2 += class21_1.anInt370;
            else
                class21_1.anInt381 = -l - 1;
            class21_1.anInt383 = l2;
            if (j1 == 1)
                l2 += class21_1.anInt370;
            else
                class21_1.anInt383 = -1;
            class21_1.anInt380 = l2;
            if (k == 1)
                l2 += class21_1.anInt370;
            else
                class21_1.anInt380 = -1;
            class21_1.anInt376 = l2;
            if (k1 == 1)
                l2 += class21_1.anInt369;
            else
                class21_1.anInt376 = -1;
            class21_1.anInt382 = l2;
            if (i1 == 1)
                l2 += class21_1.anInt370;
            else
                class21_1.anInt382 = -1;
            class21_1.anInt377 = l2;
            l2 += k2;
            class21_1.anInt379 = l2;
            l2 += class21_1.anInt370 * 2;
            class21_1.anInt384 = l2;
            l2 += class21_1.anInt371 * 6;
            class21_1.anInt373 = l2;
            l2 += l1;
            class21_1.anInt374 = l2;
            l2 += i2;
            class21_1.anInt375 = l2;
            l2 += j2;
        } catch (Exception _ex) {
        }
    }

    public static void init() {
        modelHeader = new ModelHeader[90000];
    }

    public static void method461(int file) {
        modelHeader[file] = null;
    }

    public static Model getModel(int file) {
        if (modelHeader == null)
            return null;

        ModelHeader class21 = modelHeader[file];
        if (class21 == null) {
            Client.instance.resourceProvider.provide(0, file);
            return null;
        } else {
            return new Model(file);
        }
    }

    public static boolean isCached(int file) {
        if (modelHeader == null)
            return false;

        ModelHeader class21 = modelHeader[file];
        if (class21 == null) {
            Client.instance.resourceProvider.provide(0, file);
            return false;
        } else {
            return true;
        }
    }

    public static final int method481(int i, int j, int k) {
        if (i == 65535)
            return 0;

        if ((k & 2) == 2) {
            if (j < 0)
                j = 0;
            else if (j > 127)
                j = 127;

            j = 127 - j;
            return j;
        }

        j = j * (i & 0x7f) >> 7;
        if (j < 2)
            j = 2;
        else if (j > 126)
            j = 126;

        return (i & 0xff80) + j;
    }


    private final int method465(Model model, int face) {
        int vertex = -1;
        int x = model.vertexX[face];
        int y = model.vertexY[face];
        int z = model.vertexZ[face];
        for (int index = 0; index < numVertices; index++) {
            if (x != vertexX[index] || y != vertexY[index] || z != vertexZ[index])
                continue;
            vertex = index;
            break;
        }
        if (vertex == -1) {
            vertexX[numVertices] = x;
            vertexY[numVertices] = y;
            vertexZ[numVertices] = z;
            if (model.vertexVSkin != null)
                vertexVSkin[numVertices] = model.vertexVSkin[face];

            vertex = numVertices++;
        }
        return vertex;
    }

    public void calculateDistances() {
        super.modelBaseY = 0;
        maxVertexDistanceXZPlane = 0;
        maximumYVertex = 0;
        for (int i = 0; i < numVertices; i++) {
            int j = vertexX[i];
            int k = vertexY[i];
            int l = vertexZ[i];
            if (-k > super.modelBaseY)
                super.modelBaseY = -k;
            if (k > maximumYVertex)
                maximumYVertex = k;
            int i1 = j * j + l * l;
            if (i1 > maxVertexDistanceXZPlane)
                maxVertexDistanceXZPlane = i1;
        }
        maxVertexDistanceXZPlane = (int) (Math.sqrt(maxVertexDistanceXZPlane) + 0.98999999999999999D);
        diagonal3DAboveOrigin = (int) (Math.sqrt(maxVertexDistanceXZPlane * maxVertexDistanceXZPlane + super.modelBaseY
                * super.modelBaseY) + 0.98999999999999999D);
        maxRenderDepth = diagonal3DAboveOrigin
                + (int) (Math.sqrt(maxVertexDistanceXZPlane * maxVertexDistanceXZPlane + maximumYVertex
                * maximumYVertex) + 0.98999999999999999D);
    }

    public void computeSphericalBounds() {
        super.modelBaseY = 0;
        maximumYVertex = 0;
        for (int i = 0; i < numVertices; i++) {
            int j = vertexY[i];
            if (-j > super.modelBaseY)
                super.modelBaseY = -j;
            if (j > maximumYVertex)
                maximumYVertex = j;
        }

        diagonal3DAboveOrigin = (int) (Math.sqrt(maxVertexDistanceXZPlane * maxVertexDistanceXZPlane + super.modelBaseY
                * super.modelBaseY) + 0.98999999999999999D);
        maxRenderDepth = diagonal3DAboveOrigin
                + (int) (Math.sqrt(maxVertexDistanceXZPlane * maxVertexDistanceXZPlane + maximumYVertex
                * maximumYVertex) + 0.98999999999999999D);
    }

    public void calculateVertexData(int i) {
        super.modelBaseY = 0;
        maxVertexDistanceXZPlane = 0;
        maximumYVertex = 0;
        minimumXVertex = 0xf423f;
        maximumXVertex = 0xfff0bdc1;
        maximumZVertex = 0xfffe7961;
        minimumZVertex = 0x1869f;
        for (int j = 0; j < numVertices; j++) {
            int x = vertexX[j];
            int y = vertexY[j];
            int z = vertexZ[j];
            if (x < minimumXVertex)
                minimumXVertex = x;
            if (x > maximumXVertex)
                maximumXVertex = x;
            if (z < minimumZVertex)
                minimumZVertex = z;
            if (z > maximumZVertex)
                maximumZVertex = z;
            if (-y > super.modelBaseY)
                super.modelBaseY = -y;
            if (y > maximumYVertex)
                maximumYVertex = y;
            int j1 = x * x + z * z;
            if (j1 > maxVertexDistanceXZPlane)
                maxVertexDistanceXZPlane = j1;
        }
        maxVertexDistanceXZPlane = (int) Math.sqrt(maxVertexDistanceXZPlane);
        diagonal3DAboveOrigin = (int) Math.sqrt(maxVertexDistanceXZPlane * maxVertexDistanceXZPlane + super.modelBaseY * super.modelBaseY);
        if (i != 21073) {
            return;
        } else {
            maxRenderDepth = diagonal3DAboveOrigin + (int) Math.sqrt(maxVertexDistanceXZPlane * maxVertexDistanceXZPlane + maximumYVertex * maximumYVertex);
            return;
        }
    }

    public void skin() {
        if (vertexVSkin != null) {
            int ai[] = new int[256];
            int j = 0;
            for (int l = 0; l < numVertices; l++) {
                int j1 = vertexVSkin[l];
                ai[j1]++;
                if (j1 > j)
                    j = j1;
            }
            vertexGroups = new int[j + 1][];
            for (int k1 = 0; k1 <= j; k1++) {
                vertexGroups[k1] = new int[ai[k1]];
                ai[k1] = 0;
            }
            for (int j2 = 0; j2 < numVertices; j2++) {
                int l2 = vertexVSkin[j2];
                vertexGroups[l2][ai[l2]++] = j2;
            }
            vertexVSkin = null;
        }
        if (triangleTSkin != null) {
            int ai1[] = new int[256];
            int k = 0;
            for (int i1 = 0; i1 < trianglesCount; i1++) {
                int l1 = triangleTSkin[i1];
                ai1[l1]++;
                if (l1 > k)
                    k = l1;
            }
            faceGroups = new int[k + 1][];
            for (int i2 = 0; i2 <= k; i2++) {
                faceGroups[i2] = new int[ai1[i2]];
                ai1[i2] = 0;
            }
            for (int k2 = 0; k2 < trianglesCount; k2++) {
                int i3 = triangleTSkin[k2];
                faceGroups[i3][ai1[i3]++] = k2;
            }
            triangleTSkin = null;
        }
    }

    private void transformSkin(int animationType, int skin[], int x, int y, int z) {

        int i1 = skin.length;
        if (animationType == 0) {
            int j1 = 0;
            xAnimOffset = 0;
            yAnimOffset = 0;
            zAnimOffset = 0;
            for (int k2 = 0; k2 < i1; k2++) {
                int l3 = skin[k2];
                if (l3 < vertexGroups.length) {
                    int ai5[] = vertexGroups[l3];
                    for (int i5 = 0; i5 < ai5.length; i5++) {
                        int j6 = ai5[i5];
                        xAnimOffset += vertexX[j6];
                        yAnimOffset += vertexY[j6];
                        zAnimOffset += vertexZ[j6];
                        j1++;
                    }

                }
            }

            if (j1 > 0) {
                xAnimOffset = (int) (xAnimOffset / j1 + x);
                yAnimOffset = (int) (yAnimOffset / j1 + y);
                zAnimOffset = (int) (zAnimOffset / j1 + z);
                return;
            } else {
                xAnimOffset = (int) x;
                yAnimOffset = (int) y;
                zAnimOffset = (int) z;
                return;
            }
        }
        if (animationType == 1) {
            for (int k1 = 0; k1 < i1; k1++) {
                int l2 = skin[k1];
                if (l2 < vertexGroups.length) {
                    int ai1[] = vertexGroups[l2];
                    for (int i4 = 0; i4 < ai1.length; i4++) {
                        int j5 = ai1[i4];
                        vertexX[j5] += x;
                        vertexY[j5] += y;
                        vertexZ[j5] += z;
                    }

                }
            }

            return;
        }
        if (animationType == 2) {
            for (int l1 = 0; l1 < i1; l1++) {
                int i3 = skin[l1];
                if (i3 < vertexGroups.length) {
                    int ai2[] = vertexGroups[i3];
                    for (int j4 = 0; j4 < ai2.length; j4++) {
                        int k5 = ai2[j4];
                        vertexX[k5] -= xAnimOffset;
                        vertexY[k5] -= yAnimOffset;
                        vertexZ[k5] -= zAnimOffset;
                        int k6 = (x & 0xff) * 8;
                        int l6 = (y & 0xff) * 8;
                        int i7 = (z & 0xff) * 8;
                        if (i7 != 0) {
                            int j7 = SINE[i7];
                            int i8 = COSINE[i7];
                            int l8 = vertexY[k5] * j7 + vertexX[k5] * i8 >> 16;
                            vertexY[k5] = vertexY[k5] * i8 - vertexX[k5] * j7 >> 16;
                            vertexX[k5] = l8;
                        }
                        if (k6 != 0) {
                            int k7 = SINE[k6];
                            int j8 = COSINE[k6];
                            int i9 = vertexY[k5] * j8 - vertexZ[k5] * k7 >> 16;
                            vertexZ[k5] = vertexY[k5] * k7 + vertexZ[k5] * j8 >> 16;
                            vertexY[k5] = i9;
                        }
                        if (l6 != 0) {
                            int l7 = SINE[l6];
                            int k8 = COSINE[l6];
                            int j9 = vertexZ[k5] * l7 + vertexX[k5] * k8 >> 16;
                            vertexZ[k5] = vertexZ[k5] * k8 - vertexX[k5] * l7 >> 16;
                            vertexX[k5] = j9;
                        }
                        vertexX[k5] += xAnimOffset;
                        vertexY[k5] += yAnimOffset;
                        vertexZ[k5] += zAnimOffset;
                    }

                }
            }

            return;
        }
        if (animationType == 3) {
            for (int i2 = 0; i2 < i1; i2++) {
                int j3 = skin[i2];
                if (j3 < vertexGroups.length) {
                    int ai3[] = vertexGroups[j3];
                    for (int k4 = 0; k4 < ai3.length; k4++) {
                        int l5 = ai3[k4];
                        vertexX[l5] -= xAnimOffset;
                        vertexY[l5] -= yAnimOffset;
                        vertexZ[l5] -= zAnimOffset;
                        vertexX[l5] = (int) ((vertexX[l5] * x) / 128);
                        vertexY[l5] = (int) ((vertexY[l5] * y) / 128);
                        vertexZ[l5] = (int) ((vertexZ[l5] * z) / 128);
                        vertexX[l5] += xAnimOffset;
                        vertexY[l5] += yAnimOffset;
                        vertexZ[l5] += zAnimOffset;
                    }

                }
            }

            return;
        }
        if (animationType == 5 && faceGroups != null && face_alpha != null) {
            for (int j2 = 0; j2 < i1; j2++) {
                int k3 = skin[j2];
                if (k3 < faceGroups.length) {
                    int ai4[] = faceGroups[k3];
                    for (int l4 = 0; l4 < ai4.length; l4++) {
                        int var13 = ai4[l4]; // L: 441
                        int var14 = this.face_alpha[var13] & 255; // L: 442
                        if (var14 < 0) {
                            var14 = 0;
                        } else if (var14 > 255) {
                            var14 = 255;
                        }

                        this.face_alpha[var13] = (byte)var14; // L: 445

                    }

                }
            }

        }
    }

    public void applyTransform(int frameId) {
        if (vertexGroups == null)
            return;
        if (frameId == -1)
            return;
        Frame animationFrame = Frame.method531(frameId);
        if (animationFrame == null)
            return;
        FrameBase class18 = animationFrame.base;
        xAnimOffset = 0;
        yAnimOffset = 0;
        zAnimOffset = 0;
        for (int k = 0; k < animationFrame.transformationCount; k++) {
            int l = animationFrame.transformationIndices[k];
            transformSkin(class18.transformationType[l], class18.skinList[l],
                    animationFrame.transformX[k], animationFrame.transformY[k],
                    animationFrame.transformZ[k]);
        }

    }

    public void applyAnimationFrames(int ai[], int j, int k) {
        if (k == -1)
            return;
        if (ai == null || j == -1) {
            applyTransform(k);
            return;
        }
        Frame class36 = Frame.method531(k);
        if (class36 == null)
            return;
        Frame class36_1 = Frame.method531(j);
        if (class36_1 == null) {
            applyTransform(k);
            return;
        }
        FrameBase class18 = class36.base;
        xAnimOffset = 0;
        yAnimOffset = 0;
        zAnimOffset = 0;
        int l = 0;
        int i1 = ai[l++];
        for (int j1 = 0; j1 < class36.transformationCount; j1++) {
            int k1;
            for (k1 = class36.transformationIndices[j1]; k1 > i1; i1 = ai[l++])
                ;
            if (k1 != i1 || class18.transformationType[k1] == 0)
                transformSkin(class18.transformationType[k1], class18.skinList[k1], class36.transformX[j1], class36.transformY[j1], class36.transformZ[j1]);
        }

        xAnimOffset = 0;
        yAnimOffset = 0;
        zAnimOffset = 0;
        l = 0;
        i1 = ai[l++];
        for (int l1 = 0; l1 < class36_1.transformationCount; l1++) {
            int i2;
            for (i2 = class36_1.transformationIndices[l1]; i2 > i1; i1 = ai[l++])
                ;
            if (i2 == i1 || class18.transformationType[i2] == 0)
                transformSkin(class18.transformationType[i2], class18.skinList[i2], class36_1.transformX[l1], class36_1.transformY[l1], class36_1.transformZ[l1]);
        }
    }

    public void rotate90Degrees() {
        for (int point = 0; point < numVertices; point++) {
            int k = vertexX[point];
            vertexX[point] = vertexZ[point];
            vertexZ[point] = -k;
        }
    }

    public void leanOverX(int i) {
        int k = SINE[i];
        int l = COSINE[i];
        for (int point = 0; point < numVertices; point++) {
            int j1 = vertexY[point] * l - vertexZ[point] * k >> 16;
            vertexZ[point] = vertexY[point] * k + vertexZ[point] * l >> 16;
            vertexY[point] = j1;
        }
    }

    public void translate(int x, int y, int z) {
        for (int point = 0; point < numVertices; point++) {
            vertexX[point] += x;
            vertexY[point] += y;
            vertexZ[point] += z;
        }
    }

    public void recolor(int found, int replace) {
        if (triangleColours != null)
            for (int face = 0; face < trianglesCount; face++)
                if (triangleColours[face] == (short) found)
                    triangleColours[face] = (short) replace;
    }

    public void retexture(short found, short replace) {
        if(materials != null) {
            for (int face = 0; face < trianglesCount; face++) {
                if (materials[face] == found) {
                    materials[face] = replace;
                }
            }
        }
    }

    public void method477() {
        for (int index = 0; index < numVertices; index++)
            vertexZ[index] = -vertexZ[index];

        for (int face = 0; face < trianglesCount; face++) {
            int l = facePointA[face];
            facePointA[face] = facePointC[face];
            facePointC[face] = l;
        }
    }

    public void scale(int i, int j, int l) {
        for (int index = 0; index < numVertices; index++) {
            vertexX[index] = (vertexX[index] * i) / 128;
            vertexY[index] = (vertexY[index] * l) / 128;
            vertexZ[index] = (vertexZ[index] * j) / 128;
        }
    }

    public void light(int i, int j, int k, int l, int i1, boolean flag) {
        int j1 = (int) Math.sqrt(k * k + l * l + i1 * i1);
        int k1 = j * j1 >> 8;
        faceHslA = new int[trianglesCount];
        faceHslB = new int[trianglesCount];
        faceHslC = new int[trianglesCount];
        if (super.vertexNormals == null) {
            super.vertexNormals = new VertexNormal[numVertices];
            for (int index = 0; index < numVertices; index++)
                super.vertexNormals[index] = new VertexNormal();

        }
        for (int face = 0; face < trianglesCount; face++) {
            int j2 = facePointA[face];
            int l2 = facePointB[face];
            int i3 = facePointC[face];
            int j3 = vertexX[l2] - vertexX[j2];
            int k3 = vertexY[l2] - vertexY[j2];
            int l3 = vertexZ[l2] - vertexZ[j2];
            int i4 = vertexX[i3] - vertexX[j2];
            int j4 = vertexY[i3] - vertexY[j2];
            int k4 = vertexZ[i3] - vertexZ[j2];
            int l4 = k3 * k4 - j4 * l3;
            int i5 = l3 * i4 - k4 * j3;
            int j5;
            for (j5 = j3 * j4 - i4 * k3; l4 > 8192 || i5 > 8192 || j5 > 8192 || l4 < -8192 || i5 < -8192 || j5 < -8192; j5 >>= 1) {
                l4 >>= 1;
                i5 >>= 1;
            }
            int k5 = (int) Math.sqrt(l4 * l4 + i5 * i5 + j5 * j5);
            if (k5 <= 0)
                k5 = 1;

            l4 = (l4 * 256) / k5;
            i5 = (i5 * 256) / k5;
            j5 = (j5 * 256) / k5;

            short texture_id;
            int type;
            if (faceDrawType != null)
                type = faceDrawType[face];
            else
                type = 0;

            if (materials == null) {
                texture_id = -1;
            } else {
                texture_id = materials[face];
            }

            if (faceDrawType == null || (faceDrawType[face] & 1) == 0) {
                VertexNormal class33_2 = super.vertexNormals[j2];
                class33_2.normalX += l4;
                class33_2.normalY += i5;
                class33_2.normalZ += j5;
                class33_2.magnitude++;
                class33_2 = super.vertexNormals[l2];
                class33_2.normalX += l4;
                class33_2.normalY += i5;
                class33_2.normalZ += j5;
                class33_2.magnitude++;
                class33_2 = super.vertexNormals[i3];
                class33_2.normalX += l4;
                class33_2.normalY += i5;
                class33_2.normalZ += j5;
                class33_2.magnitude++;
            } else {
                if (texture_id != -1) {
                    type = 2;
                }
                int light = i + (k * l4 + l * i5 + i1 * j5) / (k1 + k1 / 2);
                faceHslA[face] = method481(triangleColours[face], light, type);
            }
        }
        if (flag) {
            doShading(i, k1, k, l, i1);
            calculateDistances();
        } else {
            alsoVertexNormals = new VertexNormal[numVertices];
            for (int point = 0; point < numVertices; point++) {
                VertexNormal class33 = super.vertexNormals[point];
                VertexNormal class33_1 = alsoVertexNormals[point] = new VertexNormal();
                class33_1.normalX = class33.normalX;
                class33_1.normalY = class33.normalY;
                class33_1.normalZ = class33.normalZ;
                class33_1.magnitude = class33.magnitude;
            }
            calculateVertexData(21073);
        }
    }

    public final void doShading(int i, int j, int k, int l, int i1) {

        for (int j1 = 0; j1 < trianglesCount; j1++) {
            int k1 = facePointA[j1];
            int i2 = facePointB[j1];
            int j2 = facePointC[j1];
            short texture_id;
            if (materials == null) {
                texture_id = -1;
            } else {
                texture_id = materials[j1];
            }

            if (faceDrawType == null) {
                int type;
                if (texture_id != -1) {
                    type = 2;
                } else {
                    type = 1;
                }

                int hsl = triangleColours[j1] & 0xffff;
                VertexNormal vertex = super.vertexNormals[k1];
                int light = i + (k * vertex.normalX + l * vertex.normalY + i1 * vertex.normalZ) / (j * vertex.magnitude);
                faceHslA[j1] = method481(hsl, light, type);
                vertex = super.vertexNormals[i2];
                light = i + (k * vertex.normalX + l * vertex.normalY + i1 * vertex.normalZ) / (j * vertex.magnitude);
                faceHslB[j1] = method481(hsl, light, type);
                vertex = super.vertexNormals[j2];
                light = i + (k * vertex.normalX + l * vertex.normalY + i1 * vertex.normalZ) / (j * vertex.magnitude);
                faceHslC[j1] = method481(hsl, light, type);
            } else if ((faceDrawType[j1] & 1) == 0) {
                int type = faceDrawType[j1];
                if (texture_id != -1) {
                    type = 2;
                }
                int hsl = triangleColours[j1] & 0xffff;
                VertexNormal vertex = super.vertexNormals[k1];
                int light = i + (k * vertex.normalX + l * vertex.normalY + i1 * vertex.normalZ) / (j * vertex.magnitude);
                faceHslA[j1] = method481(hsl, light, type);
                vertex = super.vertexNormals[i2];
                light = i + (k * vertex.normalX + l * vertex.normalY + i1 * vertex.normalZ) / (j * vertex.magnitude);
                faceHslB[j1] = method481(hsl, light, type);
                vertex = super.vertexNormals[j2];
                light = i + (k * vertex.normalX + l * vertex.normalY + i1 * vertex.normalZ) / (j * vertex.magnitude);
                faceHslC[j1] = method481(hsl, light, type);
            }
        }
        super.vertexNormals = null;
        alsoVertexNormals = null;
        vertexVSkin = null;
        triangleTSkin = null;
        //triangleColours = null; -> Fix for 'Show Equipment' interface
    }

    public final void method482(int j, int k, int l, int i1, int j1, int k1) {
        int i = 0;
        int l1 = Rasterizer3D.originViewX;
        int i2 = Rasterizer3D.originViewY;
        int j2 = SINE[i];
        int k2 = COSINE[i];
        int l2 = SINE[j];
        int i3 = COSINE[j];
        int j3 = SINE[k];
        int k3 = COSINE[k];
        int l3 = SINE[l];
        int i4 = COSINE[l];
        int j4 = j1 * l3 + k1 * i4 >> 16;
        for (int k4 = 0; k4 < numVertices; k4++) {
            int l4 = vertexX[k4];
            int i5 = vertexY[k4];
            int j5 = vertexZ[k4];
            if (k != 0) {
                int k5 = i5 * j3 + l4 * k3 >> 16;
                i5 = i5 * k3 - l4 * j3 >> 16;
                l4 = k5;
            }
            if (i != 0) {
                int l5 = i5 * k2 - j5 * j2 >> 16;
                j5 = i5 * j2 + j5 * k2 >> 16;
                i5 = l5;
            }
            if (j != 0) {
                int i6 = j5 * l2 + l4 * i3 >> 16;
                j5 = j5 * i3 - l4 * l2 >> 16;
                l4 = i6;
            }
            l4 += i1;
            i5 += j1;
            j5 += k1;
            int j6 = i5 * i4 - j5 * l3 >> 16;
            j5 = i5 * l3 + j5 * i4 >> 16;
            i5 = j6;
            projected_vertex_z[k4] = j5 - j4;
            projected_vertex_x[k4] = l1 + (l4 << 9) / j5;
            projected_vertex_y[k4] = i2 + (i5 << 9) / j5;
            if (numberOfTexturesFaces > 0) {
                camera_vertex_x[k4] = l4;
                camera_vertex_y[k4] = i5;
                camera_vertex_z[k4] = j5;
            }
        }

        try {
            method483(false, false, 0);
            return;
        } catch (Exception _ex) {
            return;
        }
    }

    @Override
    public final void renderAtPoint(int i, int j, int k, int l, int i1, int j1, int k1, int l1, long i2) {

        int j2 = l1 * i1 - j1 * l >> 16;
        int k2 = k1 * j + j2 * k >> 16;
        int l2 = maxVertexDistanceXZPlane * k >> 16;
        int i3 = k2 + l2;
        if (i3 <= 50 || k2 >= 4500)
            return;

        int j3 = l1 * l + j1 * i1 >> 16;
        int k3 = j3 - maxVertexDistanceXZPlane << SceneGraph.viewDistance;
        if (k3 / i3 >= Rasterizer2D.viewportCenterX)
            return;

        int l3 = j3 + maxVertexDistanceXZPlane << SceneGraph.viewDistance;
        if (l3 / i3 <= -Rasterizer2D.viewportCenterX)
            return;

        int i4 = k1 * k - j2 * j >> 16;
        int j4 = maxVertexDistanceXZPlane * j >> 16;
        int k4 = i4 + j4 << SceneGraph.viewDistance;
        if (k4 / i3 <= -Rasterizer2D.viewportCenterY)
            return;

        int l4 = j4 + (super.modelBaseY * k >> 16);
        int i5 = i4 - l4 << SceneGraph.viewDistance;
        if (i5 / i3 >= Rasterizer2D.viewportCenterY)
            return;

        int j5 = l2 + (super.modelBaseY * j >> 16);
        boolean flag = false;
        if (k2 - j5 <= 50)
            flag = true;

        boolean flag1 = false;
        if (i2 > 0 && aBoolean1684) {
            int k5 = k2 - l2;
            if (k5 <= 50)
                k5 = 50;
            if (j3 > 0) {
                k3 /= i3;
                l3 /= k5;
            } else {
                l3 /= i3;
                k3 /= k5;
            }
            if (i4 > 0) {
                i5 /= i3;
                k4 /= k5;
            } else {
                k4 /= i3;
                i5 /= k5;
            }
            int i6 = anInt1685 - Rasterizer3D.originViewX;
            int k6 = anInt1686 - Rasterizer3D.originViewY;
            if (i6 > k3 && i6 < l3 && k6 > i5 && k6 < k4)
                if (fits_on_single_square)
                    obj_key[obj_loaded++] = i2;
                else
                    flag1 = true;
        }
        int l5 = Rasterizer3D.originViewX;
        int j6 = Rasterizer3D.originViewY;
        int l6 = 0;
        int i7 = 0;
        if (i != 0) {
            l6 = SINE[i];
            i7 = COSINE[i];
        }
        for (int j7 = 0; j7 < numVertices; j7++) {
            int k7 = vertexX[j7];
            int l7 = vertexY[j7];
            int i8 = vertexZ[j7];
            if (i != 0) {
                int j8 = i8 * l6 + k7 * i7 >> 16;
                i8 = i8 * i7 - k7 * l6 >> 16;
                k7 = j8;
            }
            k7 += j1;
            l7 += k1;
            i8 += l1;
            int position = i8 * l + k7 * i1 >> 16;
            i8 = i8 * i1 - k7 * l >> 16;
            k7 = position;

            position = l7 * k - i8 * j >> 16;
            i8 = l7 * j + i8 * k >> 16;
            l7 = position;

            projected_vertex_z[j7] = i8 - k2;
            if (i8 >= 50) {
                projected_vertex_x[j7] = l5 + (k7 << SceneGraph.viewDistance) / i8;
                projected_vertex_y[j7] = j6 + (l7 << SceneGraph.viewDistance) / i8;
            } else {
                projected_vertex_x[j7] = -5000;
                flag = true;
            }
            if (flag || numberOfTexturesFaces > 0) {
                camera_vertex_x[j7] = k7;
                camera_vertex_y[j7] = l7;
                camera_vertex_z[j7] = i8;
            }
        }
        try {
            method483(flag, flag1, i2);
            return;
        } catch (Exception _ex) {
            return;
        }
    }

    private final void method483(boolean flag, boolean flag1, long i) {
        for (int j = 0; j < maxRenderDepth; j++)
            depthListIndices[j] = 0;

        for (int face = 0; face < trianglesCount; face++) {
            if (faceDrawType == null || faceDrawType[face] != -1) {
                int a = facePointA[face];
                int b = facePointB[face];
                int c = facePointC[face];
                int x_a = projected_vertex_x[a];
                int x_b = projected_vertex_x[b];
                int x_c = projected_vertex_x[c];
                if (flag && (x_a == -5000 || x_b == -5000 || x_c == -5000)) {
                    outOfReach[face] = true;
                    int j5 = (projected_vertex_z[a] + projected_vertex_z[b] + projected_vertex_z[c]) / 3 + diagonal3DAboveOrigin;
                    faceLists[j5][depthListIndices[j5]++] = face;
                } else {
                    if (flag1 && method486(anInt1685, anInt1686, projected_vertex_y[a], projected_vertex_y[b], projected_vertex_y[c], x_a, x_b, x_c)) {
                        obj_key[obj_loaded++] = i;
                        flag1 = false;
                    }
                    if ((x_a - x_b) * (projected_vertex_y[c] - projected_vertex_y[b]) - (projected_vertex_y[a] - projected_vertex_y[b]) * (x_c - x_b) > 0) {
                        outOfReach[face] = false;
                        if (x_a < 0 || x_b < 0 || x_c < 0 || x_a > Rasterizer2D.lastX || x_b > Rasterizer2D.lastX || x_c > Rasterizer2D.lastX)
                            hasAnEdgeToRestrict[face] = true;
                        else
                            hasAnEdgeToRestrict[face] = false;

                        int k5 = (projected_vertex_z[a] + projected_vertex_z[b] + projected_vertex_z[c]) / 3 + diagonal3DAboveOrigin;
                        faceLists[k5][depthListIndices[k5]++] = face;
                    }
                }
            }
        }
        if (face_render_priorities == null) {
            for (int i1 = maxRenderDepth - 1; i1 >= 0; i1--) {
                int l1 = depthListIndices[i1];
                if (l1 > 0) {
                    int ai[] = faceLists[i1];
                    for (int j3 = 0; j3 < l1; j3++)
                        rasterize(ai[j3]);

                }
            }
            return;
        }
        for (int j1 = 0; j1 < 12; j1++) {
            anIntArray1673[j1] = 0;
            anIntArray1677[j1] = 0;
        }
        for (int i2 = maxRenderDepth - 1; i2 >= 0; i2--) {
            int k2 = depthListIndices[i2];
            if (k2 > 0) {
                int ai1[] = faceLists[i2];
                for (int i4 = 0; i4 < k2; i4++) {
                    int l4 = ai1[i4];
                    byte l5 = face_render_priorities[l4];
                    int j6 = anIntArray1673[l5]++;
                    anIntArrayArray1674[l5][j6] = l4;
                    if (l5 < 10)
                        anIntArray1677[l5] += i2;
                    else if (l5 == 10)
                        anIntArray1675[j6] = i2;
                    else
                        anIntArray1676[j6] = i2;
                }

            }
        }

        int l2 = 0;
        if (anIntArray1673[1] > 0 || anIntArray1673[2] > 0)
            l2 = (anIntArray1677[1] + anIntArray1677[2]) / (anIntArray1673[1] + anIntArray1673[2]);
        int k3 = 0;
        if (anIntArray1673[3] > 0 || anIntArray1673[4] > 0)
            k3 = (anIntArray1677[3] + anIntArray1677[4]) / (anIntArray1673[3] + anIntArray1673[4]);
        int j4 = 0;
        if (anIntArray1673[6] > 0 || anIntArray1673[8] > 0)
            j4 = (anIntArray1677[6] + anIntArray1677[8]) / (anIntArray1673[6] + anIntArray1673[8]);

        int i6 = 0;
        int k6 = anIntArray1673[10];
        int ai2[] = anIntArrayArray1674[10];
        int ai3[] = anIntArray1675;
        if (i6 == k6) {
            i6 = 0;
            k6 = anIntArray1673[11];
            ai2 = anIntArrayArray1674[11];
            ai3 = anIntArray1676;
        }
        int i5;
        if (i6 < k6)
            i5 = ai3[i6];
        else
            i5 = -1000;

        for (int l6 = 0; l6 < 10; l6++) {
            while (l6 == 0 && i5 > l2) {
                rasterize(ai2[i6++]);
                if (i6 == k6 && ai2 != anIntArrayArray1674[11]) {
                    i6 = 0;
                    k6 = anIntArray1673[11];
                    ai2 = anIntArrayArray1674[11];
                    ai3 = anIntArray1676;
                }
                if (i6 < k6)
                    i5 = ai3[i6];
                else
                    i5 = -1000;
            }
            while (l6 == 3 && i5 > k3) {
                rasterize(ai2[i6++]);
                if (i6 == k6 && ai2 != anIntArrayArray1674[11]) {
                    i6 = 0;
                    k6 = anIntArray1673[11];
                    ai2 = anIntArrayArray1674[11];
                    ai3 = anIntArray1676;
                }
                if (i6 < k6)
                    i5 = ai3[i6];
                else
                    i5 = -1000;
            }
            while (l6 == 5 && i5 > j4) {
                rasterize(ai2[i6++]);
                if (i6 == k6 && ai2 != anIntArrayArray1674[11]) {
                    i6 = 0;
                    k6 = anIntArray1673[11];
                    ai2 = anIntArrayArray1674[11];
                    ai3 = anIntArray1676;
                }
                if (i6 < k6)
                    i5 = ai3[i6];
                else
                    i5 = -1000;
            }
            int i7 = anIntArray1673[l6];
            int ai4[] = anIntArrayArray1674[l6];
            for (int j7 = 0; j7 < i7; j7++)
                rasterize(ai4[j7]);

        }
        while (i5 != -1000) {
            rasterize(ai2[i6++]);
            if (i6 == k6 && ai2 != anIntArrayArray1674[11]) {
                i6 = 0;
                ai2 = anIntArrayArray1674[11];
                k6 = anIntArray1673[11];
                ai3 = anIntArray1676;
            }
            if (i6 < k6)
                i5 = ai3[i6];
            else
                i5 = -1000;
        }
    }

    private final void rasterize(int face) {
        if (outOfReach[face]) {
            method485(face);
            return;
        }
        int j = facePointA[face];
        int k = facePointB[face];
        int l = facePointC[face];
        Rasterizer3D.textureOutOfDrawingBounds = hasAnEdgeToRestrict[face];
        if (face_alpha == null)
            Rasterizer3D.alpha = 0;
        else
            Rasterizer3D.alpha = face_alpha[face] & 0xff;

        int type;
        if (faceDrawType == null)
            type = 0;
        else
            type = faceDrawType[face] & 3;

        if (materials != null && materials[face] != -1) {
            int texture_a = j;
            int texture_b = k;
            int texture_c = l;
            if (textures != null && textures[face] != -1) {
                int coordinate = textures[face] & 0xff;
                texture_a = textures_face_a[coordinate];
                texture_b = textures_face_b[coordinate];
                texture_c = textures_face_c[coordinate];
            }

            if (faceHslC[face] == -1 || type == 3) {
                Rasterizer3D.drawTexturedTriangle(
                        projected_vertex_y[j], projected_vertex_y[k], projected_vertex_y[l],
                        projected_vertex_x[j], projected_vertex_x[k], projected_vertex_x[l],
                        faceHslA[face], faceHslA[face], faceHslA[face],
                        camera_vertex_x[texture_a], camera_vertex_x[texture_b], camera_vertex_x[texture_c],
                        camera_vertex_y[texture_a], camera_vertex_y[texture_b], camera_vertex_y[texture_c],
                        camera_vertex_z[texture_a], camera_vertex_z[texture_b], camera_vertex_z[texture_c],
                        materials[face]);
            } else {
                Rasterizer3D.drawTexturedTriangle(
                        projected_vertex_y[j], projected_vertex_y[k], projected_vertex_y[l],
                        projected_vertex_x[j], projected_vertex_x[k], projected_vertex_x[l],
                        faceHslA[face], faceHslB[face], faceHslC[face],
                        camera_vertex_x[texture_a], camera_vertex_x[texture_b], camera_vertex_x[texture_c],
                        camera_vertex_y[texture_a], camera_vertex_y[texture_b], camera_vertex_y[texture_c],
                        camera_vertex_z[texture_a], camera_vertex_z[texture_b], camera_vertex_z[texture_c],
                        materials[face]);
            }
        } else {
            if (type == 0) {
                Rasterizer3D.drawGouraudTriangle(projected_vertex_y[j], projected_vertex_y[k],
                        projected_vertex_y[l], projected_vertex_x[j], projected_vertex_x[k],
                        projected_vertex_x[l], faceHslA[face], faceHslB[face], faceHslC[face]);
                return;
            }
            if (type == 1) {
                Rasterizer3D.drawFlatTriangle(projected_vertex_y[j], projected_vertex_y[k], projected_vertex_y[l], projected_vertex_x[j], projected_vertex_x[k], projected_vertex_x[l], modelIntArray3[faceHslA[face]]);
                return;
            }
        }
    }

    private final void method485(int i) {
        int j = Rasterizer3D.originViewX;
        int k = Rasterizer3D.originViewY;
        int l = 0;
        int i1 = facePointA[i];
        int j1 = facePointB[i];
        int k1 = facePointC[i];
        int l1 = camera_vertex_z[i1];
        int i2 = camera_vertex_z[j1];
        int j2 = camera_vertex_z[k1];
        if (l1 >= 50) {
            anIntArray1678[l] = projected_vertex_x[i1];
            anIntArray1679[l] = projected_vertex_y[i1];
            anIntArray1680[l++] = faceHslA[i];
        } else {
            int k2 = camera_vertex_x[i1];
            int k3 = camera_vertex_y[i1];
            int k4 = faceHslA[i];
            if (j2 >= 50) {
                int k5 = (50 - l1) * modelIntArray4[j2 - l1];
                anIntArray1678[l] = j + (k2 + ((camera_vertex_x[k1] - k2) * k5 >> 16) << SceneGraph.viewDistance) / 50;
                anIntArray1679[l] = k + (k3 + ((camera_vertex_y[k1] - k3) * k5 >> 16) << SceneGraph.viewDistance) / 50;
                anIntArray1680[l++] = k4 + ((faceHslC[i] - k4) * k5 >> 16);
            }
            if (i2 >= 50) {
                int l5 = (50 - l1) * modelIntArray4[i2 - l1];
                anIntArray1678[l] = j + (k2 + ((camera_vertex_x[j1] - k2) * l5 >> 16) << SceneGraph.viewDistance) / 50;
                anIntArray1679[l] = k + (k3 + ((camera_vertex_y[j1] - k3) * l5 >> 16) << SceneGraph.viewDistance) / 50;
                anIntArray1680[l++] = k4 + ((faceHslB[i] - k4) * l5 >> 16);
            }
        }
        if (i2 >= 50) {
            anIntArray1678[l] = projected_vertex_x[j1];
            anIntArray1679[l] = projected_vertex_y[j1];
            anIntArray1680[l++] = faceHslB[i];
        } else {
            int l2 = camera_vertex_x[j1];
            int l3 = camera_vertex_y[j1];
            int l4 = faceHslB[i];
            if (l1 >= 50) {
                int i6 = (50 - i2) * modelIntArray4[l1 - i2];
                anIntArray1678[l] = j + (l2 + ((camera_vertex_x[i1] - l2) * i6 >> 16) << SceneGraph.viewDistance) / 50;
                anIntArray1679[l] = k + (l3 + ((camera_vertex_y[i1] - l3) * i6 >> 16) << SceneGraph.viewDistance) / 50;
                anIntArray1680[l++] = l4 + ((faceHslA[i] - l4) * i6 >> 16);
            }
            if (j2 >= 50) {
                int j6 = (50 - i2) * modelIntArray4[j2 - i2];
                anIntArray1678[l] = j + (l2 + ((camera_vertex_x[k1] - l2) * j6 >> 16) << SceneGraph.viewDistance) / 50;
                anIntArray1679[l] = k + (l3 + ((camera_vertex_y[k1] - l3) * j6 >> 16) << SceneGraph.viewDistance) / 50;
                anIntArray1680[l++] = l4 + ((faceHslC[i] - l4) * j6 >> 16);
            }
        }
        if (j2 >= 50) {
            anIntArray1678[l] = projected_vertex_x[k1];
            anIntArray1679[l] = projected_vertex_y[k1];
            anIntArray1680[l++] = faceHslC[i];
        } else {
            int i3 = camera_vertex_x[k1];
            int i4 = camera_vertex_y[k1];
            int i5 = faceHslC[i];
            if (i2 >= 50) {
                int k6 = (50 - j2) * modelIntArray4[i2 - j2];
                anIntArray1678[l] = j + (i3 + ((camera_vertex_x[j1] - i3) * k6 >> 16) << SceneGraph.viewDistance) / 50;
                anIntArray1679[l] = k + (i4 + ((camera_vertex_y[j1] - i4) * k6 >> 16) << SceneGraph.viewDistance) / 50;
                anIntArray1680[l++] = i5 + ((faceHslB[i] - i5) * k6 >> 16);
            }
            if (l1 >= 50) {
                int l6 = (50 - j2) * modelIntArray4[l1 - j2];
                anIntArray1678[l] = j + (i3 + ((camera_vertex_x[i1] - i3) * l6 >> 16) << SceneGraph.viewDistance) / 50;
                anIntArray1679[l] = k + (i4 + ((camera_vertex_y[i1] - i4) * l6 >> 16) << SceneGraph.viewDistance) / 50;
                anIntArray1680[l++] = i5 + ((faceHslA[i] - i5) * l6 >> 16);
            }
        }
        int j3 = anIntArray1678[0];
        int j4 = anIntArray1678[1];
        int j5 = anIntArray1678[2];
        int i7 = anIntArray1679[0];
        int j7 = anIntArray1679[1];
        int k7 = anIntArray1679[2];
        if ((j3 - j4) * (k7 - j7) - (i7 - j7) * (j5 - j4) > 0) {
            Rasterizer3D.textureOutOfDrawingBounds = false;
            int texture_a = i1;
            int texture_b = j1;
            int texture_c = k1;
            if (l == 3) {
                if (j3 < 0 || j4 < 0 || j5 < 0 || j3 > Rasterizer2D.lastX || j4 > Rasterizer2D.lastX || j5 > Rasterizer2D.lastX)
                    Rasterizer3D.textureOutOfDrawingBounds = true;

                int l7;
                if (faceDrawType == null)
                    l7 = 0;
                else
                    l7 = faceDrawType[i] & 3;

                if (materials != null && materials[i] != -1) {
                    if (textures != null && textures[i] != -1) {
                        int coordinate = textures[i] & 0xff;
                        texture_a = textures_face_a[coordinate];
                        texture_b = textures_face_b[coordinate];
                        texture_c = textures_face_c[coordinate];
                    }
                    if (faceHslC[i] == -1) {
                        Rasterizer3D.drawTexturedTriangle(
                                i7, j7, k7,
                                j3, j4, j5,
                                faceHslA[i], faceHslA[i], faceHslA[i],
                                camera_vertex_x[texture_a], camera_vertex_x[texture_b], camera_vertex_x[texture_c],
                                camera_vertex_y[texture_a], camera_vertex_y[texture_b], camera_vertex_y[texture_c],
                                camera_vertex_z[texture_a], camera_vertex_z[texture_b], camera_vertex_z[texture_c],
                                materials[i]);
                    } else {
                        Rasterizer3D.drawTexturedTriangle(
                                i7, j7, k7,
                                j3, j4, j5,
                                anIntArray1680[0], anIntArray1680[1], anIntArray1680[2],
                                camera_vertex_x[texture_a], camera_vertex_x[texture_b], camera_vertex_x[texture_c],
                                camera_vertex_y[texture_a], camera_vertex_y[texture_b], camera_vertex_y[texture_c],
                                camera_vertex_z[texture_a], camera_vertex_z[texture_b], camera_vertex_z[texture_c],
                                materials[i]);
                    }
                } else {
                    if (l7 == 0)
                        Rasterizer3D.drawGouraudTriangle(i7, j7, k7, j3, j4, j5, anIntArray1680[0], anIntArray1680[1], anIntArray1680[2]);

                    else if (l7 == 1)
                        Rasterizer3D.drawFlatTriangle(i7, j7, k7, j3, j4, j5, modelIntArray3[faceHslA[i]]);
                }
            }
            if (l == 4) {
                if (j3 < 0 || j4 < 0 || j5 < 0 || j3 > Rasterizer2D.lastX || j4 > Rasterizer2D.lastX || j5 > Rasterizer2D.lastX || anIntArray1678[3] < 0 || anIntArray1678[3] > Rasterizer2D.lastX)
                    Rasterizer3D.textureOutOfDrawingBounds = true;
                int type;
                if (faceDrawType == null)
                    type = 0;
                else
                    type = faceDrawType[i] & 3;

                if (materials != null && materials[i] != -1) {
                    if (textures != null && textures[i] != -1) {
                        int coordinate = textures[i] & 0xff;
                        texture_a = textures_face_a[coordinate];
                        texture_b = textures_face_b[coordinate];
                        texture_c = textures_face_c[coordinate];
                    }
                    if (faceHslC[i] == -1) {
                        Rasterizer3D.drawTexturedTriangle(
                                i7, j7, k7,
                                j3, j4, j5,
                                faceHslA[i], faceHslA[i], faceHslA[i],
                                camera_vertex_x[texture_a], camera_vertex_x[texture_b], camera_vertex_x[texture_c],
                                camera_vertex_y[texture_a], camera_vertex_y[texture_b], camera_vertex_y[texture_c],
                                camera_vertex_z[texture_a], camera_vertex_z[texture_b], camera_vertex_z[texture_c],
                                materials[i]);
                        Rasterizer3D.drawTexturedTriangle(
                                i7, k7, anIntArray1679[3],
                                j3, j5, anIntArray1678[3],
                                faceHslA[i], faceHslA[i], faceHslA[i],
                                camera_vertex_x[texture_a], camera_vertex_x[texture_b], camera_vertex_x[texture_c],
                                camera_vertex_y[texture_a], camera_vertex_y[texture_b], camera_vertex_y[texture_c],
                                camera_vertex_z[texture_a], camera_vertex_z[texture_b], camera_vertex_z[texture_c],
                                materials[i]);
                    } else {
                        Rasterizer3D.drawTexturedTriangle(
                                i7, j7, k7,
                                j3, j4, j5,
                                anIntArray1680[0], anIntArray1680[1], anIntArray1680[2],
                                camera_vertex_x[texture_a], camera_vertex_x[texture_b], camera_vertex_x[texture_c],
                                camera_vertex_y[texture_a], camera_vertex_y[texture_b], camera_vertex_y[texture_c],
                                camera_vertex_z[texture_a], camera_vertex_z[texture_b], camera_vertex_z[texture_c],
                                materials[i]);
                        Rasterizer3D.drawTexturedTriangle(
                                i7, k7, anIntArray1679[3],
                                j3, j5, anIntArray1678[3],
                                anIntArray1680[0], anIntArray1680[2], anIntArray1680[3],
                                camera_vertex_x[texture_a], camera_vertex_x[texture_b], camera_vertex_x[texture_c],
                                camera_vertex_y[texture_a], camera_vertex_y[texture_b], camera_vertex_y[texture_c],
                                camera_vertex_z[texture_a], camera_vertex_z[texture_b], camera_vertex_z[texture_c],
                                materials[i]);
                        return;
                    }
                } else {
                    if (type == 0) {
                        Rasterizer3D.drawGouraudTriangle(i7, j7, k7, j3, j4, j5, anIntArray1680[0], anIntArray1680[1], anIntArray1680[2]);
                        Rasterizer3D.drawGouraudTriangle(i7, k7, anIntArray1679[3], j3, j5, anIntArray1678[3], anIntArray1680[0], anIntArray1680[2], anIntArray1680[3]);
                        return;
                    }
                    if (type == 1) {
                        int l8 = modelIntArray3[faceHslA[i]];
                        Rasterizer3D.drawFlatTriangle(i7, j7, k7, j3, j4, j5, l8);
                        Rasterizer3D.drawFlatTriangle(i7, k7, anIntArray1679[3], j3, j5, anIntArray1678[3], l8);
                        return;
                    }
                }
            }
        }
    }

    private final boolean method486(int i, int j, int k, int l, int i1, int x_a, int x_b, int x_c) {
        if (j < k && j < l && j < i1)
            return false;
        if (j > k && j > l && j > i1)
            return false;
        if (i < x_a && i < x_b && i < x_c)
            return false;
        return i <= x_a || i <= x_b || i <= x_c;
    }
}