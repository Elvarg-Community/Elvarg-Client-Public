package com.runescape.entity.model;

import com.runescape.Client;
import com.runescape.cache.anim.Frame;
import com.runescape.cache.anim.FrameBase;
import com.runescape.draw.Rasterizer2D;
import com.runescape.draw.Rasterizer3D;
import com.runescape.engine.impl.MouseHandler;
import com.runescape.entity.Renderable;
import com.runescape.io.Buffer;
import com.runescape.scene.SceneGraph;
import net.runelite.api.Perspective;
import net.runelite.api.hooks.DrawCallbacks;
import net.runelite.api.model.Jarvis;
import net.runelite.api.model.Triangle;
import net.runelite.api.model.Vertex;
import net.runelite.rs.api.RSFrames;
import net.runelite.rs.api.RSModel;
import net.runelite.rs.api.RSVertexNormal;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class Model extends Renderable implements RSModel {

    public static void clear() {
        modelHeaders = null;
        hasAnEdgeToRestrict = null;
        outOfReach = null;
        vertexScreenY = null;
        vertexScreenZ = null;
        vertexMovedX = null;
        vertexMovedY = null;
        vertexMovedZ = null;
        depth = null;
        faceLists = null;
        anIntArray1673 = null;
        anIntArrayArray1674 = null;
        anIntArray1675 = null;
        anIntArray1676 = null;
        anIntArray1677 = null;
        SINE = null;
        COSINE = null;
        modelColors = null;
        modelLocations = null;
    }

    private Model(int modelId) {
        byte[] data = modelHeaders[modelId].data;
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

    public static void loadModel(final byte[] modelData, final int modelId) {
        if (modelData == null) {
            final ModelHeader modelHeader = modelHeaders[modelId] = new ModelHeader();
            modelHeader.vertexCount = 0;
            modelHeader.triangleCount = 0;
            modelHeader.texturedTriangleCount = 0;
            return;
        }
        final Buffer stream = new Buffer(modelData);
        stream.currentPosition = modelData.length - 18;
        final ModelHeader modelHeader = modelHeaders[modelId] = new ModelHeader();
        modelHeader.data = modelData;
        modelHeader.vertexCount = stream.readUShort();
        modelHeader.triangleCount = stream.readUShort();
        modelHeader.texturedTriangleCount = stream.readUnsignedByte();
        final int useTextures = stream.readUnsignedByte();
        final int useTrianglePriority = stream.readUnsignedByte();
        final int useAlpha = stream.readUnsignedByte();
        final int useTriangleSkins = stream.readUnsignedByte();
        final int useVertexSkins = stream.readUnsignedByte();
        final int dataLengthX = stream.readUShort();
        final int dataLengthY = stream.readUShort();
        final int dataLengthZ = stream.readUShort();
        final int dataLengthTriangle = stream.readUShort();
        int offset = 0;
        modelHeader.vertexDirectionOffset = offset;
        offset += modelHeader.vertexCount;
        modelHeader.triangleTypeOffset = offset;
        offset += modelHeader.triangleCount;
        modelHeader.trianglePriorityOffset = offset;
        if (useTrianglePriority == 255) {
            offset += modelHeader.triangleCount;
        } else {
            modelHeader.trianglePriorityOffset = -useTrianglePriority - 1;
        }
        modelHeader.triangleSkinOffset = offset;
        if (useTriangleSkins == 1) {
            offset += modelHeader.triangleCount;
        } else {
            modelHeader.triangleSkinOffset = -1;
        }
        modelHeader.texturePointerOffset = offset;
        if (useTextures == 1) {
            offset += modelHeader.triangleCount;
        } else {
            modelHeader.texturePointerOffset = -1;
        }
        modelHeader.vertexSkinOffset = offset;
        if (useVertexSkins == 1) {
            offset += modelHeader.vertexCount;
        } else {
            modelHeader.vertexSkinOffset = -1;
        }
        modelHeader.triangleAlphaOffset = offset;
        if (useAlpha == 1) {
            offset += modelHeader.triangleCount;
        } else {
            modelHeader.triangleAlphaOffset = -1;
        }
        modelHeader.triangleDataOffset = offset;
        offset += dataLengthTriangle;
        modelHeader.colourDataOffset = offset;
        offset += modelHeader.triangleCount * 2;
        modelHeader.texturedTriangleOffset = offset;
        offset += modelHeader.texturedTriangleCount * 6;
        modelHeader.dataOffsetX = offset;
        offset += dataLengthX;
        modelHeader.dataOffsetY = offset;
        offset += dataLengthY;
        modelHeader.dataOffsetZ = offset;
        offset += dataLengthZ;
    }

    public static void init() {
        modelHeaders = new ModelHeader[90000];
    }

    public static void resetModel(final int model) {
        modelHeaders[model] = null;
    }

    public static Model getModel(int file) {
        if (modelHeaders == null) {
            return null;
        }
        ModelHeader class21 = modelHeaders[file];
        if (class21 == null) {
            Client.instance.resourceProvider.provide(0, file);
            return null;
        } else {
            return new Model(file);
        }
    }

    public static boolean isCached(int file) {
        if (modelHeaders == null) {
            return false;
        }

        ModelHeader class21 = modelHeaders[file];
        if (class21 == null) {
            Client.instance.resourceProvider.provide(0, file);
            return false;
        } else {
            return true;
        }
    }

    Model() {
        verticesCount = 0;
        trianglesCount = 0;
        texturesCount = 0;
        facePriority = 0;
        singleTile = true;
        xMidOffset = -1;
        yMidOffset = -1;
        zMidOffset = -1;
    }

    public Model(int length, Model[] model_segments) {
        try {
            singleTile = false;
            boolean type_flag = false;
            boolean priority_flag = false;
            boolean alpha_flag = false;
            boolean tSkin_flag = false;
            boolean color_flag = false;
            boolean texture_flag = false;
            boolean coordinate_flag = false;
            verticesCount = 0;
            trianglesCount = 0;
            texturesCount = 0;
            facePriority = -1;
            xMidOffset = -1;
            yMidOffset = -1;
            zMidOffset = -1;
            Model build;
            for (int segment_index = 0; segment_index < length; segment_index++) {
                build = model_segments[segment_index];
                if (build != null) {
                    verticesCount += build.verticesCount;
                    trianglesCount += build.trianglesCount;
                    texturesCount += build.texturesCount;
                    type_flag |= build.drawType != null;
                    alpha_flag |= build.triangleAlpha != null;
                    if (build.renderPriorities != null) {
                        priority_flag = true;
                    } else {
                        if (facePriority == -1)
                            facePriority = build.facePriority;

                        if (facePriority != build.facePriority)
                            priority_flag = true;
                    }
                    tSkin_flag |= build.triangleData != null;
                    color_flag |= build.colors != null;
                    texture_flag |= build.materials != null;
                    coordinate_flag |= build.textures != null;
                }
            }

            verticesX = new int[verticesCount];
            verticesY = new int[verticesCount];
            verticesZ = new int[verticesCount];
            vertexData = new int[verticesCount];
            trianglesX = new int[trianglesCount];
            trianglesY = new int[trianglesCount];
            trianglesZ = new int[trianglesCount];
            if (color_flag)
                colors = new short[trianglesCount];

            if (type_flag)
                drawType = new int[trianglesCount];

            if (priority_flag)
                renderPriorities = new byte[trianglesCount];

            if (alpha_flag)
                triangleAlpha = new byte[trianglesCount];

            if (tSkin_flag)
                triangleData = new int[trianglesCount];

            if (texture_flag)
                materials = new short[trianglesCount];

            if (coordinate_flag)
                textures = new byte[trianglesCount];

            if (texturesCount > 0) {
                textureTypes = new byte[texturesCount];
                texturesX = new short[texturesCount];
                texturesY = new short[texturesCount];
                texturesZ = new short[texturesCount];
            }
            verticesCount = 0;
            trianglesCount = 0;
            texturesCount = 0;
            int texture_face = 0;
            for (int segment_index = 0; segment_index < length; segment_index++) {
                build = model_segments[segment_index];
                if (build != null) {
                    for (int face = 0; face < build.trianglesCount; face++) {
                        if (type_flag && build.drawType != null)
                            drawType[trianglesCount] = build.drawType[face];

                        if (priority_flag)
                            if (build.renderPriorities == null)
                                renderPriorities[trianglesCount] = build.facePriority;
                            else
                                renderPriorities[trianglesCount] = build.renderPriorities[face];

                        if (alpha_flag && build.triangleAlpha != null)
                            triangleAlpha[trianglesCount] = build.triangleAlpha[face];

                        if (tSkin_flag && build.triangleData != null)
                            triangleData[trianglesCount] = build.triangleData[face];

                        if (texture_flag) {
                            if (build.materials != null)
                                materials[trianglesCount] = build.materials[face];
                            else
                                materials[trianglesCount] = -1;
                        }
                        if (coordinate_flag) {
                            if (build.textures != null && build.textures[face] != -1) {
                                textures[trianglesCount] = (byte) (build.textures[face] + texture_face);
                            } else {
                                textures[trianglesCount] = -1;
                            }
                        }

                        colors[trianglesCount] = build.colors[face];
                        trianglesX[trianglesCount] = getFirstIdenticalVertexId(build, build.trianglesX[face]);
                        trianglesY[trianglesCount] = getFirstIdenticalVertexId(build, build.trianglesY[face]);
                        trianglesZ[trianglesCount] = getFirstIdenticalVertexId(build, build.trianglesZ[face]);
                        trianglesCount++;
                    }
                    for (int texture_edge = 0; texture_edge < build.texturesCount; texture_edge++) {
                        texturesX[texturesCount] = (short) getFirstIdenticalVertexId(build, build.texturesX[texture_edge]);
                        texturesY[texturesCount] = (short) getFirstIdenticalVertexId(build, build.texturesY[texture_edge]);
                        texturesZ[texturesCount] = (short) getFirstIdenticalVertexId(build, build.texturesZ[texture_edge]);
                        texturesCount++;
                    }
                    texture_face += build.texturesCount;
                }
            }

            if (getFaceTextures() != null)
            {
                int count = getFaceCount();
                float[] uv = new float[count * 6];
                int idx = 0;

                for (int i = 0; i < length; ++i)
                {
                    RSModel model = model_segments[i];
                    if (model != null)
                    {
                        float[] modelUV = model.getFaceTextureUVCoordinates();

                        if (modelUV != null)
                        {
                            System.arraycopy(modelUV, 0, uv, idx, model.getFaceCount() * 6);
                        }

                        idx += model.getFaceCount() * 6;
                    }
                }

                setFaceTextureUVCoordinates(uv);
            }

            vertexNormals();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Model(Model[] amodel) {
        int modelCount = 2;
        singleTile = false;
        boolean flag1 = false;
        boolean flag2 = false;
        boolean flag3 = false;
        boolean flag4 = false;
        boolean textureFlag = false;
        boolean coordinateFlag = false;
        verticesCount = 0;
        trianglesCount = 0;
        texturesCount = 0;
        facePriority = -1;
        for (int count = 0; count < modelCount; count++) {
            Model model = amodel[count];
            if (model != null) {
                verticesCount += model.verticesCount;
                trianglesCount += model.trianglesCount;
                texturesCount += model.texturesCount;
                flag1 |= model.drawType != null;
                if (model.renderPriorities != null) {
                    flag2 = true;
                } else {
                    if (facePriority == -1)
                        facePriority = model.facePriority;
                    if (facePriority != model.facePriority)
                        flag2 = true;
                }
                flag3 |= model.triangleAlpha != null;
                flag4 |= model.colors != null;
                textureFlag |= model.materials != null;
                coordinateFlag |= model.textures != null;
            }
        }

        verticesX = new int[verticesCount];
        verticesY = new int[verticesCount];
        verticesZ = new int[verticesCount];
        trianglesX = new int[trianglesCount];
        trianglesY = new int[trianglesCount];
        trianglesZ = new int[trianglesCount];
        colorsX = new int[trianglesCount];
        colorsY = new int[trianglesCount];
        colorsZ = new int[trianglesCount];
        texturesX = new short[texturesCount];
        texturesY = new short[texturesCount];
        texturesZ = new short[texturesCount];
        if (flag1)
            drawType = new int[trianglesCount];
        if (flag2)
            renderPriorities = new byte[trianglesCount];
        if (flag3)
            triangleAlpha = new byte[trianglesCount];
        if (flag4)
            colors = new short[trianglesCount];
        if (textureFlag)
            materials = new short[trianglesCount];

        if (coordinateFlag)
            textures = new byte[trianglesCount];
        verticesCount = 0;
        trianglesCount = 0;
        texturesCount = 0;
        int i1 = 0;
        for (int currentModel = 0; currentModel < modelCount; currentModel++) {
            Model model = amodel[currentModel];
            if (model != null) {
                int vCount = verticesCount;
                for (int indexx = 0; indexx < model.verticesCount; indexx++) {
                    int x = model.verticesX[indexx];
                    int y = model.verticesY[indexx];
                    int z = model.verticesZ[indexx];
                    verticesX[verticesCount] = x;
                    verticesY[verticesCount] = y;
                    verticesZ[verticesCount] = z;
                    ++verticesCount;
                }

                for (int index = 0; index < model.trianglesCount; index++) {
                    trianglesX[trianglesCount] = model.trianglesX[index] + vCount;
                    trianglesY[trianglesCount] = model.trianglesY[index] + vCount;
                    trianglesZ[trianglesCount] = model.trianglesZ[index] + vCount;
                    colorsX[trianglesCount] = model.colorsX[index];
                    colorsY[trianglesCount] = model.colorsY[index];
                    colorsZ[trianglesCount] = model.colorsZ[index];
                    if (flag1)
                        if (model.drawType == null) {
                            drawType[trianglesCount] = 0;
                        } else {
                            int j2 = model.drawType[index];
                            if ((j2 & 2) == 2)
                                j2 += i1 << 2;
                            drawType[trianglesCount] = j2;
                        }
                    if (flag2)
                        if (model.renderPriorities == null)
                            renderPriorities[trianglesCount] = model.facePriority;
                        else
                            renderPriorities[trianglesCount] = model.renderPriorities[index];
                    if (flag3)
                        if (model.triangleAlpha == null)
                            triangleAlpha[trianglesCount] = 0;
                        else
                            triangleAlpha[trianglesCount] = model.triangleAlpha[index];
                    if (flag4 && model.colors != null)
                        colors[trianglesCount] = model.colors[index];

                    if (textureFlag) {
                        if (model.materials != null) {
                            materials[trianglesCount] = model.materials[trianglesCount];
                        } else {
                            materials[trianglesCount] = -1;
                        }
                    }

                    if (coordinateFlag) {
                        if (model.textures != null && model.textures[trianglesCount] != -1)
                            textures[trianglesCount] = (byte) (model.textures[trianglesCount] + texturesCount);
                        else
                            textures[trianglesCount] = -1;

                    }

                    trianglesCount++;
                }
                for (int index2 = 0; currentModel < model.texturesCount; index2++) {
                    texturesX[texturesCount] = (short) (model.texturesX[index2] + vCount);
                    texturesY[texturesCount] = (short) (model.texturesY[index2] + vCount);
                    texturesZ[texturesCount] = (short) (model.texturesZ[index2] + vCount);
                    texturesCount++;
                }
                i1 += model.texturesCount;
            }
        }
        calculateDiagonals();
    }

    public Model(boolean colorFlag, boolean alphaFlag, boolean animated, Model model) {
        this(colorFlag, alphaFlag, animated, false, model);
    }

    public Model(boolean colorFlag, boolean alphaFlag, boolean animated, boolean textureFlag, Model model) {
        singleTile = false;
        verticesCount = model.verticesCount;
        trianglesCount = model.trianglesCount;
        texturesCount = model.texturesCount;
        if (animated) {
            verticesX = model.verticesX;
            verticesY = model.verticesY;
            verticesZ = model.verticesZ;
        } else {
            verticesX = new int[verticesCount];
            verticesY = new int[verticesCount];
            verticesZ = new int[verticesCount];
            for (int j = 0; j < verticesCount; j++) {
                verticesX[j] = model.verticesX[j];
                verticesY[j] = model.verticesY[j];
                verticesZ[j] = model.verticesZ[j];
            }

        }
        if (colorFlag) {
            colors = model.colors;
        } else {
            colors = new short[trianglesCount];
            System.arraycopy(model.colors, 0, colors, 0, trianglesCount);
        }

        if (!textureFlag && model.materials != null) {
            materials = new short[trianglesCount];
            System.arraycopy(model.materials, 0, materials, 0, trianglesCount);
        } else {
            materials = model.materials;
        }

        if (alphaFlag) {
            triangleAlpha = model.triangleAlpha;
        } else {
            triangleAlpha = new byte[trianglesCount];
            if (model.triangleAlpha == null) {
                for (int l = 0; l < trianglesCount; l++) {
                    triangleAlpha[l] = 0;
                }

            } else {
                System.arraycopy(model.triangleAlpha, 0, triangleAlpha, 0, trianglesCount);

            }
        }
        vertexData = model.vertexData;
        triangleData = model.triangleData;
        drawType = model.drawType;
        trianglesX = model.trianglesX;
        trianglesY = model.trianglesY;
        trianglesZ = model.trianglesZ;
        renderPriorities = model.renderPriorities;
        facePriority = model.facePriority;
        texturesX = model.texturesX;
        texturesY = model.texturesY;
        texturesZ = model.texturesZ;
        textures = model.textures;
        textureTypes = model.textureTypes;
        normals = model.normals;
        vertexNormalsOffsets = model.vertexNormalsOffsets;
    }


    public Model(boolean resetVertices, boolean resetColors, Model model) {
        singleTile = false;
        verticesCount = model.verticesCount;
        trianglesCount = model.trianglesCount;
        texturesCount = model.texturesCount;

        if (resetVertices) {
            verticesY = new int[verticesCount];
            System.arraycopy(model.verticesY, 0, verticesY, 0, verticesCount);
        } else {
            verticesY = model.verticesY;
        }

        if (resetColors) {
            colorsX = new int[trianglesCount];
            colorsY = new int[trianglesCount];
            colorsZ = new int[trianglesCount];

            for (int k = 0; k < trianglesCount; k++) {
                colorsX[k] = model.colorsX[k];
                colorsY[k] = model.colorsY[k];
                colorsZ[k] = model.colorsZ[k];
            }

            drawType = new int[trianglesCount];
            if (model.drawType == null) {
                for (int l = 0; l < trianglesCount; l++) {
                    drawType[l] = 0;
                }
            } else {
                System.arraycopy(model.drawType, 0, drawType, 0, trianglesCount);
            }

            super.normals = new VertexNormal[verticesCount];
            for (int vertex = 0; vertex < verticesCount; vertex++) {
                VertexNormal vertexNormalNew = super.normals[vertex] = new VertexNormal();
                VertexNormal vertexNormalOld = model.normals[vertex];
                vertexNormalNew.x = vertexNormalOld.x;
                vertexNormalNew.y = vertexNormalOld.y;
                vertexNormalNew.z = vertexNormalOld.z;
                vertexNormalNew.magnitude = vertexNormalOld.magnitude;
            }

            vertexNormalsOffsets = model.vertexNormalsOffsets;
        } else {
            colorsX = model.colorsX;
            colorsY = model.colorsY;
            colorsZ = model.colorsZ;
            drawType = model.drawType;
        }

        verticesX = model.verticesX;
        verticesZ = model.verticesZ;
        colors = model.colors;
        triangleAlpha = model.triangleAlpha;
        renderPriorities = model.renderPriorities;
        facePriority = model.facePriority;
        trianglesX = model.trianglesX;
        trianglesY = model.trianglesY;
        trianglesZ = model.trianglesZ;
        texturesX = model.texturesX;
        texturesY = model.texturesY;
        texturesZ = model.texturesZ;
        super.modelBaseY = model.modelBaseY;
        textures = model.textures;
        materials = model.materials;
        diagonal2DAboveOrigin = model.diagonal2DAboveOrigin;
        diagonal3DAboveOrigin = model.diagonal3DAboveOrigin;
        diagonal3D = model.diagonal3D;
        minX = model.minX;
        maxZ = model.maxZ;
        minZ = model.minZ;
        maxX = model.maxX;

        vertexNormalsX = model.vertexNormalsX;
        vertexNormalsY = model.vertexNormalsY;
        vertexNormalsZ = model.vertexNormalsZ;
        faceTextureUVCoordinates = model.faceTextureUVCoordinates;

    }

    public void replaceModel(Model model, boolean replaceAlpha) {
        verticesCount = model.verticesCount;
        trianglesCount = model.trianglesCount;
        texturesCount = model.texturesCount;

        if (sharedVerticesX.length < verticesCount) {
            sharedVerticesX = new int[verticesCount + 100];
            sharedVerticesY = new int[verticesCount + 100];
            sharedVerticesZ = new int[verticesCount + 100];
        }

        verticesX = sharedVerticesX;
        verticesY = sharedVerticesY;
        verticesZ = sharedVerticesZ;
        for (int k = 0; k < verticesCount; k++) {
            verticesX[k] = model.verticesX[k];
            verticesY[k] = model.verticesY[k];
            verticesZ[k] = model.verticesZ[k];
        }

        if (replaceAlpha) {
            triangleAlpha = model.triangleAlpha;
        } else {
            if (sharedTriangleAlpha.length < trianglesCount) {
                sharedTriangleAlpha = new byte[trianglesCount + 100];
            }
            triangleAlpha = sharedTriangleAlpha;
            if (model.triangleAlpha == null) {
                for (int l = 0; l < trianglesCount; l++) {
                    triangleAlpha[l] = 0;
                }
            } else {
                System.arraycopy(model.triangleAlpha, 0, triangleAlpha, 0, trianglesCount);
            }
        }

        drawType = model.drawType;
        colors = model.colors;
        renderPriorities = model.renderPriorities;
        facePriority = model.facePriority;
        faceGroups = model.faceGroups;
        vertexGroups = model.vertexGroups;
        trianglesX = model.trianglesX;
        trianglesY = model.trianglesY;
        trianglesZ = model.trianglesZ;
        colorsX = model.colorsX;
        colorsY = model.colorsY;
        colorsZ = model.colorsZ;
        texturesX = model.texturesX;
        texturesY = model.texturesY;
        texturesZ = model.texturesZ;
        textures = model.textures;
        textureTypes = model.textureTypes;
        materials = model.materials;
        vertexNormalsOffsets = model.vertexNormalsOffsets;

        vertexNormalsX = model.vertexNormalsX;
        vertexNormalsY = model.vertexNormalsY;
        vertexNormalsZ = model.vertexNormalsZ;
        faceTextureUVCoordinates = model.faceTextureUVCoordinates;

        model.resetBounds();
    }

    private int getFirstIdenticalVertexId(final Model model, final int vertex) {
        int vertexId = -1;
        final int x = model.verticesX[vertex];
        final int y = model.verticesY[vertex];
        final int z = model.verticesZ[vertex];
        for (int v = 0; v < this.verticesCount; v++) {
            if (x != this.verticesX[v] || y != this.verticesY[v] || z != this.verticesZ[v]) {
                continue;
            }
            vertexId = v;
            break;
        }

        if (vertexId == -1) {
            this.verticesX[this.verticesCount] = x;
            this.verticesY[this.verticesCount] = y;
            this.verticesZ[this.verticesCount] = z;
            if (model.vertexData != null) {
                this.vertexData[this.verticesCount] = model.vertexData[vertex];
            }
            vertexId = this.verticesCount++;

        }
        return vertexId;
    }

    private int boundsType;

    public void calculateDiagonals() {
       
            this.boundsType = 1;
            super.modelBaseY = 0;
            diagonal2DAboveOrigin = 0;
            maxY = 0;
            for (int vertex = 0; vertex < verticesCount; vertex++) {
                final int x = verticesX[vertex];
                final int y = verticesY[vertex];
                final int z = verticesZ[vertex];
                if (-y > super.modelBaseY) {
                    super.modelBaseY = -y;
                }
                if (y > maxY) {
                    maxY = y;
                }
                final int bounds = x * x + z * z;
                if (bounds > diagonal2DAboveOrigin) {
                    diagonal2DAboveOrigin = bounds;
                }
            }
       

        diagonal2DAboveOrigin = (int)(Math.sqrt(diagonal2DAboveOrigin) + 0.98999999999999999);
        diagonal3DAboveOrigin = (int)(Math.sqrt(diagonal2DAboveOrigin * diagonal2DAboveOrigin + super.modelBaseY * super.modelBaseY) + 0.98999999999999999);
        diagonal3D = diagonal3DAboveOrigin + (int)(Math.sqrt(diagonal2DAboveOrigin * diagonal2DAboveOrigin + maxY * maxY) + 0.98999999999999999);
    }

    void method4391() {
        if (this.boundsType != 2) {
            this.boundsType = 2;
            this.diagonal2DAboveOrigin = 0;

            for (int count = 0; count < this.verticesCount; ++count) {
                int vertX = this.verticesX[count];
                int vertY = this.verticesY[count];
                int vertZ = this.verticesZ[count];
                int origin = vertX * vertX + vertZ * vertZ + vertY * vertY;
                if (origin > this.diagonal2DAboveOrigin) {
                    this.diagonal2DAboveOrigin = origin;
                }
            }

            this.diagonal2DAboveOrigin = (int)(Math.sqrt((double)this.diagonal2DAboveOrigin) + 0.99D);
            this.diagonal3DAboveOrigin = this.diagonal2DAboveOrigin;
            this.diagonal3D = this.diagonal2DAboveOrigin + this.diagonal2DAboveOrigin;
        }
    }

    public void normalise() {
        super.modelBaseY = 0;
        maxY = 0;
        for (int vertex = 0; vertex < verticesCount; vertex++) {
            final int y = verticesY[vertex];
            if (-y > super.modelBaseY) {
                super.modelBaseY = -y;
            }
            if (y > maxY) {
                maxY = y;
            }
        }

        this.diagonal3DAboveOrigin = (int)(Math.sqrt(diagonal2DAboveOrigin * diagonal2DAboveOrigin + super.modelBaseY * super.modelBaseY) + 0.98999999999999999);
        this.diagonal3D = diagonal3DAboveOrigin + (int)(Math.sqrt(diagonal2DAboveOrigin * diagonal2DAboveOrigin + maxY * maxY) + 0.98999999999999999);
    }

    public void calculateDiagonalsAndBounds(int i) {
        super.modelBaseY = 0;
        diagonal2DAboveOrigin = 0;
        maxY = 0;
        minX = 0xf423f;
        maxX = 0xfff0bdc1;
        maxZ = 0xfffe7961;
        minZ = 0x1869f;
        for (int vertex = 0; vertex < verticesCount; vertex++) {
            final int x = verticesX[vertex];
            final int y = verticesY[vertex];
            final int z = verticesZ[vertex];
            if (x < minX) {
                minX = x;
            }
            if (x > maxX) {
                maxX = x;
            }
            if (z < minZ) {
                minZ = z;
            }
            if (z > maxZ) {
                maxZ = z;
            }
            if (-y > super.modelBaseY) {
                super.modelBaseY = -y;
            }
            if (y > maxY) {
                maxY = y;
            }
            final int bounds = x * x + z * z;
            if (bounds > diagonal2DAboveOrigin) {
                diagonal2DAboveOrigin = bounds;
            }
        }

        diagonal2DAboveOrigin = (int) Math.sqrt(this.diagonal2DAboveOrigin);
        diagonal3DAboveOrigin = (int) Math.sqrt(this.diagonal2DAboveOrigin * diagonal2DAboveOrigin + super.modelBaseY * super.modelBaseY);
        diagonal3D = diagonal3DAboveOrigin + (int) Math.sqrt(diagonal2DAboveOrigin * diagonal2DAboveOrigin + maxY * maxY);
    }

    public void generateBones() {
        if (vertexData != null) {
            int ai[] = new int[256];
            int j = 0;
            for (int l = 0; l < verticesCount; l++) {
                int j1 = vertexData[l];
                ai[j1]++;
                if (j1 > j)
                    j = j1;
            }
            vertexGroups = new int[j + 1][];
            for (int k1 = 0; k1 <= j; k1++) {
                vertexGroups[k1] = new int[ai[k1]];
                ai[k1] = 0;
            }
            for (int j2 = 0; j2 < verticesCount; j2++) {
                int l2 = vertexData[j2];
                vertexGroups[l2][ai[l2]++] = j2;
            }
            vertexData = null;
        }
        if (triangleData != null) {
            int ai1[] = new int[256];
            int k = 0;
            for (int i1 = 0; i1 < trianglesCount; i1++) {
                int l1 = triangleData[i1];
                ai1[l1]++;
                if (l1 > k)
                    k = l1;
            }
            faceGroups = new int[k + 1][];
            for (int uid = 0; uid <= k; uid++) {
                faceGroups[uid] = new int[ai1[uid]];
                ai1[uid] = 0;
            }
            for (int k2 = 0; k2 < trianglesCount; k2++) {
                int i3 = triangleData[k2];
                faceGroups[i3][ai1[i3]++] = k2;
            }
            triangleData = null;
        }
    }


    private void transform(int animationType, int skin[], int x, int y, int z) {

        int i1 = skin.length;
        if (animationType == 0) {
            int j1 = 0;
            transformTempX = 0;
            transformTempY = 0;
            transformTempZ = 0;
            for (int k2 = 0; k2 < i1; k2++) {
                int l3 = skin[k2];
                if (l3 < vertexGroups.length) {
                    int ai5[] = vertexGroups[l3];
                    for (int i5 = 0; i5 < ai5.length; i5++) {
                        int j6 = ai5[i5];
                        transformTempX += verticesX[j6];
                        transformTempY += verticesY[j6];
                        transformTempZ += verticesZ[j6];
                        j1++;
                    }

                }
            }

            if (j1 > 0) {
                transformTempX = (int) (transformTempX / j1 + x);
                transformTempY = (int) (transformTempY / j1 + y);
                transformTempZ = (int) (transformTempZ / j1 + z);
                return;
            } else {
                transformTempX = (int) x;
                transformTempY = (int) y;
                transformTempZ = (int) z;
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
                        verticesX[j5] += x;
                        verticesY[j5] += y;
                        verticesZ[j5] += z;
                    }

                }
            }

            return;
        }
        if (animationType == 2) {
            for (int l1 = 0; l1 < i1; l1++) {
                int i3 = skin[l1];
                if (i3 < vertexGroups.length) {
                    int auid[] = vertexGroups[i3];
                    for (int j4 = 0; j4 < auid.length; j4++) {
                        int k5 = auid[j4];
                        verticesX[k5] -= transformTempX;
                        verticesY[k5] -= transformTempY;
                        verticesZ[k5] -= transformTempZ;
                        int k6 = (x & 0xff) * 8;
                        int l6 = (y & 0xff) * 8;
                        int i7 = (z & 0xff) * 8;
                        if (i7 != 0) {
                            int j7 = SINE[i7];
                            int i8 = COSINE[i7];
                            int l8 = verticesY[k5] * j7 + verticesX[k5] * i8 >> 16;
                            verticesY[k5] = verticesY[k5] * i8 - verticesX[k5] * j7 >> 16;
                            verticesX[k5] = l8;
                        }
                        if (k6 != 0) {
                            int k7 = SINE[k6];
                            int j8 = COSINE[k6];
                            int i9 = verticesY[k5] * j8 - verticesZ[k5] * k7 >> 16;
                            verticesZ[k5] = verticesY[k5] * k7 + verticesZ[k5] * j8 >> 16;
                            verticesY[k5] = i9;
                        }
                        if (l6 != 0) {
                            int l7 = SINE[l6];
                            int k8 = COSINE[l6];
                            int j9 = verticesZ[k5] * l7 + verticesX[k5] * k8 >> 16;
                            verticesZ[k5] = verticesZ[k5] * k8 - verticesX[k5] * l7 >> 16;
                            verticesX[k5] = j9;
                        }
                        verticesX[k5] += transformTempX;
                        verticesY[k5] += transformTempY;
                        verticesZ[k5] += transformTempZ;
                    }

                }
            }

            return;
        }
        if (animationType == 3) {
            for (int uid = 0; uid < i1; uid++) {
                int j3 = skin[uid];
                if (j3 < vertexGroups.length) {
                    int ai3[] = vertexGroups[j3];
                    for (int k4 = 0; k4 < ai3.length; k4++) {
                        int l5 = ai3[k4];
                        verticesX[l5] -= transformTempX;
                        verticesY[l5] -= transformTempY;
                        verticesZ[l5] -= transformTempZ;
                        verticesX[l5] = (int) ((verticesX[l5] * x) / 128);
                        verticesY[l5] = (int) ((verticesY[l5] * y) / 128);
                        verticesZ[l5] = (int) ((verticesZ[l5] * z) / 128);
                        verticesX[l5] += transformTempX;
                        verticesY[l5] += transformTempY;
                        verticesZ[l5] += transformTempZ;
                    }

                }
            }

            return;
        }
        if (animationType == 5 && faceGroups != null && triangleAlpha != null) {
            for (int j2 = 0; j2 < i1; j2++) {
                int k3 = skin[j2];
                if (k3 < faceGroups.length) {
                    int ai4[] = faceGroups[k3];
                    for (int l4 = 0; l4 < ai4.length; l4++) {
                        int var13 = ai4[l4];
                        int var14 = (this.triangleAlpha[var13] & 255) + x * 8;
                        if (var14 < 0) {
                            var14 = 0;
                        } else if (var14 > 255) {
                            var14 = 255;
                        }

                        this.triangleAlpha[var13] = (byte)var14;
                    }

                }
            }

        }
    }

    public void animate(int frameId) {
        if (vertexGroups == null)
            return;

        if (frameId == -1)
            return;

        Frame frame = Frame.method531(frameId);
        if (frame == null)
            return;

        FrameBase base = frame.base;
        transformTempX = 0;
        transformTempY = 0;
        transformTempZ = 0;

        for (int index = 0; index < frame.transformationCount; index++) {
            int pos = frame.transformationIndices[index];
            transform(base.transformationType[pos], base.skinList[pos], frame.transformX[index], frame.transformY[index], frame.transformZ[index]);
        }

        this.resetBounds();
    }


    public void animate2(int label[], int idle, int current) {
        if (current == -1)
            return;

        if (label == null || idle == -1) {
            animate(current);
            return;
        }
        Frame anim = Frame.method531(current);
        if (anim == null)
            return;

        Frame skin = Frame.method531(idle);
        if (skin == null) {
            animate(current);
            return;
        }
        FrameBase list = anim.base;
        transformTempX = 0;
        transformTempY = 0;
        transformTempZ = 0;
        int id = 0;
        int table = label[id++];
        for (int index = 0; index < anim.transformationCount; index++) {
            int condition;
            for (condition = anim.transformationIndices[index]; condition > table; table = label[id++]) {
            }

            if (condition != table || list.transformationType[condition] == 0) {
                transform(list.transformationType[condition], list.skinList[condition], skin.transformX[index], skin.transformY[index], skin.transformZ[index]);
            }
        }

        transformTempX = 0;
        transformTempY = 0;
        transformTempZ = 0;
        id = 0;
        table = label[id++];
        for (int index = 0; index < skin.transformationCount; index++) {
            int condition;
            for (condition = skin.transformationIndices[index]; condition > table; table = label[id++]) {
            }

            if (condition == table || list.transformationType[condition] == 0) {
                transform(list.transformationType[condition], list.skinList[condition], skin.transformX[index], skin.transformY[index], skin.transformZ[index]);
            }
        }

        this.resetBounds();
    }


    public void rotate90Degrees() {
        for (int vertex = 0; vertex < verticesCount; vertex++) {
            final int vertexX = verticesX[vertex];
            verticesX[vertex] = verticesZ[vertex];
            verticesZ[vertex] = -vertexX;
        }

        this.resetBounds();
    }

    public void rotateZ(final int degrees) {
        final int sine = SINE[degrees];
        final int cosine = COSINE[degrees];
        for (int vertex = 0; vertex < verticesCount; vertex++) {
            final int newVertexY = verticesY[vertex] * cosine - verticesZ[vertex] * sine >> 16;
            verticesZ[vertex] = verticesY[vertex] * sine + verticesZ[vertex] * cosine >> 16;
            verticesY[vertex] = newVertexY;
        }

        this.resetBounds();
    }

    public void offsetBy(final int x, final int z, final int y) {
        for (int vertex = 0; vertex < this.verticesCount; vertex++) {
            verticesX[vertex] += x;
            verticesY[vertex] += y;
            verticesZ[vertex] += z;
        }

        this.resetBounds();
    }

    public void scale(final int x, final int z, final int y) {
        for (int vertex = 0; vertex < this.verticesCount; vertex++) {
            verticesX[vertex] = (verticesX[vertex] * x) / 128;
            verticesY[vertex] = (verticesY[vertex] * y) / 128;
            verticesZ[vertex] = (verticesZ[vertex] * z) / 128;
        }

        this.resetBounds();
    }

    public void recolor(int found, int replace) {
        if (colors != null) {
            for (int face = 0; face < trianglesCount; face++) {
                if (colors[face] == (short) found) {
                    colors[face] = (short) replace;
                }
            }
        }
    }

    public void retexture(short found, short replace) {
        if (materials != null) {
            for (int face = 0; face < trianglesCount; face++) {
                if (materials[face] == found) {
                    materials[face] = replace;
                }
            }
        }
    }

    public void mirror() {
        for (int vertex = 0; vertex < verticesCount; vertex++) {
            verticesZ[vertex] = -verticesZ[vertex];
        }

        for (int triangle = 0; triangle < trianglesCount; triangle++) {
            final int newTriangleC = trianglesX[triangle];
            trianglesX[triangle] = trianglesZ[triangle];
            trianglesZ[triangle] = newTriangleC;
        }
    }

    private void calculateVertexNormals() {
        if (normals == null) {
            normals = new VertexNormal[verticesCount];

            int var1;
            for (var1 = 0; var1 < verticesCount; ++var1) {
                normals[var1] = new VertexNormal();
            }

            for (var1 = 0; var1 < trianglesCount; ++var1) {
                int var2 = trianglesX[var1];
                int var3 = trianglesY[var1];
                int var4 = trianglesZ[var1];
                int var5 = verticesX[var3] - verticesX[var2];
                int var6 = verticesY[var3] - verticesY[var2];
                int var7 = verticesZ[var3] - verticesZ[var2];
                int var8 = verticesX[var4] - verticesX[var2];
                int var9 = verticesY[var4] - verticesY[var2];
                int var10 = verticesZ[var4] - verticesZ[var2];
                int var11 = var6 * var10 - var9 * var7;
                int var12 = var7 * var8 - var10 * var5;

                int var13;
                for (var13 = var5 * var9 - var8 * var6; var11 > 8192 || var12 > 8192 || var13 > 8192 || var11 < -8192 || var12 < -8192 || var13 < -8192; var13 >>= 1) {
                    var11 >>= 1;
                    var12 >>= 1;
                }

                int var14 = (int) Math.sqrt(var11 * var11 + var12 * var12 + var13 * var13);
                if (var14 <= 0) {
                    var14 = 1;
                }

                var11 = var11 * 256 / var14;
                var12 = var12 * 256 / var14;
                var13 = var13 * 256 / var14;
                int var15;
                if (drawType == null) {
                    var15 = 0;
                } else {
                    var15 = drawType[var1];
                }

                if (var15 == 0) {
                    VertexNormal var16 = normals[var2];
                    var16.x += var11;
                    var16.y += var12;
                    var16.z += var13;
                    ++var16.magnitude;
                    var16 = normals[var3];
                    var16.x += var11;
                    var16.y += var12;
                    var16.z += var13;
                    ++var16.magnitude;
                    var16 = normals[var4];
                    var16.x += var11;
                    var16.y += var12;
                    var16.z += var13;
                    ++var16.magnitude;
                } else if (var15 == 1) {
                    if (faceNormals == null) {
                        faceNormals = new FaceNormal[trianglesCount];
                    }

                    FaceNormal var17 = faceNormals[var1] = new FaceNormal();
                    var17.x = var11;
                    var17.y = var12;
                    var17.z = var13;
                }
            }
        }
    }

    public void light(int ambient, int contrast, int x, int y, int z, boolean flat) {

        calculateVertexNormals();
        int magnitude = (int) Math.sqrt(x * x + y * y + z * z);
        int k1 = contrast * magnitude >> 8;
        colorsX = new int[trianglesCount];
        colorsY = new int[trianglesCount];
        colorsZ = new int[trianglesCount];

        for (int var16 = 0; var16 < trianglesCount; ++var16) {
            int var17;
            if (drawType == null) {
                var17 = 0;
            } else {
                var17 = drawType[var16];
            }

            short var12;
            if (materials == null) {
                var12 = -1;
            } else {
                var12 = materials[var16];
            }

            VertexNormal var13;
            int var14;
            FaceNormal var19;
            if (var12 == -1) {
                if (var17 == 0) {
                    int var15 = colors[var16];
                    if (vertexNormalsOffsets != null && vertexNormalsOffsets[trianglesX[var16]] != null) {
                        var13 = vertexNormalsOffsets[trianglesX[var16]];
                    } else {
                        var13 = normals[trianglesX[var16]];
                    }

                    var14 = (y * var13.y + z * var13.z + x * var13.x) / (k1 * var13.magnitude) + ambient;
                    colorsX[var16] = applyLight(var15, var14);
                    if (vertexNormalsOffsets != null && vertexNormalsOffsets[trianglesY[var16]] != null) {
                        var13 = vertexNormalsOffsets[trianglesY[var16]];
                    } else {
                        var13 = normals[trianglesY[var16]];
                    }

                    var14 = (y * var13.y + z * var13.z + x * var13.x) / (k1 * var13.magnitude) + ambient;
                    colorsY[var16] = applyLight(var15, var14);
                    if (vertexNormalsOffsets != null && vertexNormalsOffsets[trianglesZ[var16]] != null) {
                        var13 = vertexNormalsOffsets[trianglesZ[var16]];
                    } else {
                        var13 = normals[trianglesZ[var16]];
                    }

                    var14 = (y * var13.y + z * var13.z + x * var13.x) / (k1 * var13.magnitude) + ambient;
                    colorsZ[var16] = applyLight(var15, var14);
                } else if (var17 == 1) {
                    var19 = faceNormals[var16];
                    var14 = (y * var19.y + z * var19.z + x * var19.x) / (k1 / 2 + k1) + ambient;
                    colorsX[var16] = applyLight(colors[var16], var14);
                    colorsZ[var16] = -1;
                } else if (var17 == 3) {
                    colorsX[var16] = 128;
                    colorsZ[var16] = -1;
                } else {
                    colorsZ[var16] = -2;
                }
            } else if (var17 == 0) {
                if (vertexNormalsOffsets != null && vertexNormalsOffsets[trianglesX[var16]] != null) {
                    var13 = vertexNormalsOffsets[trianglesX[var16]];
                } else {
                    var13 = normals[trianglesX[var16]];
                }

                var14 = (y * var13.y + z * var13.z + x * var13.x) / (k1 * var13.magnitude) + ambient;
                colorsX[var16] = applyLight(var14);
                if (vertexNormalsOffsets != null && vertexNormalsOffsets[trianglesY[var16]] != null) {
                    var13 = vertexNormalsOffsets[trianglesY[var16]];
                } else {
                    var13 = normals[trianglesY[var16]];
                }

                var14 = (y * var13.y + z * var13.z + x * var13.x) / (k1 * var13.magnitude) + ambient;
                colorsY[var16] = applyLight(var14);
                if (vertexNormalsOffsets != null && vertexNormalsOffsets[trianglesZ[var16]] != null) {
                    var13 = vertexNormalsOffsets[trianglesZ[var16]];
                } else {
                    var13 = normals[trianglesZ[var16]];
                }

                var14 = (y * var13.y + z * var13.z + x * var13.x) / (k1 * var13.magnitude) + ambient;
                colorsZ[var16] = applyLight(var14);
            } else if (var17 == 1) {
                var19 = faceNormals[var16];
                var14 = (y * var19.y + z * var19.z + x * var19.x) / (k1 / 2 + k1) + ambient;
                colorsX[var16] = applyLight(var14);
                colorsZ[var16] = -1;
            } else {
                colorsZ[var16] = -2;
            }
        }

        if (flat) {
            this.calculateDiagonals();
        } else {
            this.calculateDiagonalsAndBounds(22);
        }

        resetBounds();

        if (faceTextureUVCoordinates == null)
        {
            computeTextureUvCoordinates();
        }

        RSVertexNormal[] vertexNormals = super.normals;
        RSVertexNormal[] vertexVertices = vertexNormalsOffsets;

        if (vertexNormals != null && vertexNormalsX == null)
        {
            int verticesCount = getVerticesCount();

            vertexNormalsX = new int[verticesCount];
            vertexNormalsY = new int[verticesCount];
            vertexNormalsZ = new int[verticesCount];

            for (int i = 0; i < verticesCount; ++i)
            {
                RSVertexNormal vertexNormal;

                if (vertexVertices != null && (vertexNormal = vertexVertices[i]) != null)
                {
                    vertexNormalsX[i] = vertexNormal.getX();
                    vertexNormalsY[i] = vertexNormal.getY();
                    vertexNormalsZ[i] = vertexNormal.getZ();
                }
                else if ((vertexNormal = vertexNormals[i]) != null)
                {
                    vertexNormalsX[i] = vertexNormal.getX();
                    vertexNormalsY[i] = vertexNormal.getY();
                    vertexNormalsZ[i] = vertexNormal.getZ();
                }
            }
        }

    }

    private int applyLight(int var0) {
        if (var0 < 2) {
            var0 = 2;
        } else if (var0 > 126) {
            var0 = 126;
        }

        return var0;
    }

    private int applyLight(int var0, int var1) {
        var1 = (var0 & 127) * var1 >> 7;
        if (var1 < 2) {
            var1 = 2;
        } else if (var1 > 126) {
            var1 = 126;
        }

        return (var0 & '\uff80') + var1;
    }

    public void setLighting(int intensity, int diffusion, int lightX, int lightY, int lightZ) {
        for (int j1 = 0; j1 < trianglesCount; j1++) {
            int k1 = trianglesX[j1];
            int i2 = trianglesY[j1];
            int j2 = trianglesZ[j1];
            if (drawType == null) {
                int i3 = colors[j1];
                VertexNormal vertexNormal = super.normals[k1];
                int k2 = intensity + (lightX * vertexNormal.x + lightY * vertexNormal.y + lightZ * vertexNormal.z) / (diffusion * vertexNormal.magnitude);
                colorsX[j1] = mixLightness(i3, k2, 0);
                vertexNormal = super.normals[i2];
                k2 = intensity + (lightX * vertexNormal.x + lightY * vertexNormal.y + lightZ * vertexNormal.z) / (diffusion * vertexNormal.magnitude);
                colorsY[j1] = mixLightness(i3, k2, 0);
                vertexNormal = super.normals[j2];
                k2 = intensity + (lightX * vertexNormal.x + lightY * vertexNormal.y + lightZ * vertexNormal.z) / (diffusion * vertexNormal.magnitude);
                colorsZ[j1] = mixLightness(i3, k2, 0);
            } else if ((drawType[j1] & 1) == 0) {
                int j3 = colors[j1];
                int k3 = drawType[j1];
                VertexNormal vertexNormal = super.normals[k1];
                int l2 = intensity + (lightX * vertexNormal.x + lightY * vertexNormal.y + lightZ * vertexNormal.z) / (diffusion * vertexNormal.magnitude);
                colorsX[j1] = mixLightness(j3, l2, k3);
                vertexNormal = super.normals[i2];
                l2 = intensity + (lightX * vertexNormal.x + lightY * vertexNormal.y + lightZ * vertexNormal.z) / (diffusion * vertexNormal.magnitude);
                colorsY[j1] = mixLightness(j3, l2, k3);
                vertexNormal = super.normals[j2];
                l2 = intensity + (lightX * vertexNormal.x + lightY * vertexNormal.y + lightZ * vertexNormal.z) / (diffusion * vertexNormal.magnitude);
                colorsZ[j1] = mixLightness(j3, l2, k3);
            }
        }

        super.normals = null;
        vertexNormalsOffsets = null;
        vertexData = null;
        triangleData = null;
        if (drawType != null) {
            for (int l1 = 0; l1 < trianglesCount; l1++)
                if ((drawType[l1] & 2) == 2)
                    return;

        }
        colors = null;
    }

    private static int light(int light) {
        if (light >= 2) {
            if (light > 126) {
                light = 126;
            }
        } else {
            light = 2;
        }
        return light;
    }

    private static int light(int hsl, int light) {
        light = light * (hsl & 127) >> 7;
        if (light < 2) {
            light = 2;
        } else if (light > 126) {
            light = 126;
        }
        return (hsl & '\uff80') + light;
    }

    public static int light(int hsl, int light, int type) {
        if ((type & 2) == 2)
            return light(light);

        return light(hsl, light);
    }

    private static int mixLightness(int i, int j, int k) {
        if (i == 65535) {
            return 0;
        }
        if ((k & 2) == 2) {
            if (j < 0) {
                j = 0;
            } else if (j > 127) {
                j = 127;
            }
            j = 127 - j;
            return j;
        }

        j = j * (i & 0x7f) >> 7;
        if (j < 2) {
            j = 2;
        } else if (j > 126) {
            j = 126;
        }
        return (i & 0xff80) + j;
    }


    public void renderModel(final int rotationY, final int rotationZ, final int rotationXW, final int translationX, final int translationY, final int translationZ) {

        if (this.boundsType != 2 && this.boundsType != 1) {
            this.method4391();
        }

        final int centerX = Rasterizer3D.originViewX;
        final int centerY = Rasterizer3D.originViewY;
        final int sineY = SINE[rotationY];
        final int cosineY = COSINE[rotationY];
        final int sineZ = SINE[rotationZ];
        final int cosineZ = COSINE[rotationZ];
        final int sineXW = SINE[rotationXW];
        final int cosineXW = COSINE[rotationXW];
        final int transformation = translationY * sineXW + translationZ * cosineXW >> 16;
        for (int vertex = 0; vertex < verticesCount; vertex++) {
            int x = this.verticesX[vertex];
            int y = this.verticesY[vertex];
            int z = this.verticesZ[vertex];
            if (rotationZ != 0) {
                final int newX = y * sineZ + x * cosineZ >> 16;
                y = y * cosineZ - x * sineZ >> 16;
                x = newX;
            }
            if (rotationY != 0) {
                final int newX = z * sineY + x * cosineY >> 16;
                z = z * cosineY - x * sineY >> 16;
                x = newX;
            }
            x += translationX;
            y += translationY;
            z += translationZ;
            final int newY = y * cosineXW - z * sineXW >> 16;
            z = y * sineXW + z * cosineXW >> 16;
            y = newY;
            vertexScreenZ[vertex] = z - transformation;
            vertexScreenX[vertex] = centerX + (x << 9) / z;
            vertexScreenY[vertex] = centerY + (y << 9) / z;
            if (texturesCount > 0) {
                vertexMovedX[vertex] = x;
                vertexMovedY[vertex] = y;
                vertexMovedZ[vertex] = z;
            }
        }

        try {
            this.withinObject(false, false, 0);
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }

    private static boolean mouseInViewport = false;

    public static void cursorCalculations() {
        int mouseX = MouseHandler.mouseX;
        int mouseY = MouseHandler.mouseY;
        if (MouseHandler.lastButton != 0) {
            mouseX = MouseHandler.saveClickX;
            mouseY = MouseHandler.saveClickY;
        }

        if (mouseX >= Client.instance.getViewportXOffset() && mouseX < Client.instance.getViewportXOffset() + Client.instance.getViewportWidth() && mouseY >= Client.instance.getViewportYOffset() && mouseY < Client.instance.getViewportHeight() + Client.instance.getViewportYOffset()) {
            cursorX = mouseX - Client.instance.getViewportXOffset();
            cursorY = mouseY - Client.instance.getViewportYOffset();
            mouseInViewport = true;
            objectsHovering = 0;
        } else {
            mouseInViewport = false;
            objectsHovering = 0;
        }
    }

    public static boolean method322(long var0) {
        boolean var2 = var0 != 0L;
        if (var2) {
            boolean var3 = (int)(var0 >>> 16 & 1L) == 1;
            var2 = !var3;
        }

        return var2;
    }

    private void calculateBoundingBox(int size) {
        if (this.xMidOffset == -1) {
            int minxX = 0;
            int minZ = 0;
            int minY = 0;
            int maxX = 0;
            int maxZ = 0;
            int maxY = 0;
            int var8 = COSINE[size];
            int var9 = SINE[size];

            for (int var10 = 0; var10 < this.verticesCount; ++var10) {
                int x = Rasterizer3D.method4045(this.verticesX[var10], this.verticesZ[var10], var8, var9);
                int z = this.verticesY[var10];
                int y = Rasterizer3D.method4046(this.verticesX[var10], this.verticesZ[var10], var8, var9);
                if (x < minxX) {
                    minxX = x;
                }

                if (x > maxX) {
                    maxX = x;
                }

                if (z < minZ) {
                    minZ = z;
                }

                if (z > maxZ) {
                    maxZ = z;
                }

                if (y < minY) {
                    minY = y;
                }

                if (y > maxY) {
                    maxY = y;
                }
            }

            this.xMid = (maxX + minxX) / 2;
            this.yMid = (maxZ + minZ) / 2;
            this.zMid = (maxY + minY) / 2;
            this.xMidOffset = (maxX - minxX + 1) / 2;
            this.yMidOffset = (maxZ - minZ + 1) / 2;
            this.zMidOffset = (maxY - minY + 1) / 2;
            if (this.xMidOffset < 32) {
                this.xMidOffset = 32;
            }

            if (this.zMidOffset < 32) {
                this.zMidOffset = 32;
            }

            if (this.singleTile) {
                this.xMidOffset += 8;
                this.zMidOffset += 8;
            }
        }
    }

   
    @Override
    public final void renderAtPoint(int orientation, int pitchSine, int pitchCos, int yawSin, int yawCos, int offsetX, int offsetY, int offsetZ, long uid) {
        if (this.boundsType != 1) {
            this.calculateBoundsCylinder();
        }

        calculateBoundingBox(orientation);
        int sceneX = offsetZ * yawCos - offsetX * yawSin >> 16;
        int sceneY = offsetY * pitchSine + sceneX * pitchCos >> 16;
        int dimensionSinY = diagonal2DAboveOrigin * pitchCos >> 16;
        int pos = sceneY + dimensionSinY;
        final boolean gpu = Client.instance.isGpu() && Rasterizer3D.world;
        if (pos <= 50 || (sceneY >= 3500 && !gpu))
            return;
        int xRot = offsetZ * yawSin + offsetX * yawCos >> 16;
        int objX = (xRot - diagonal2DAboveOrigin) * Rasterizer3D.fieldOfView;
        if (objX / pos >= Rasterizer2D.viewportCenterX)
            return;

        int objWidth = (xRot + diagonal2DAboveOrigin) * Rasterizer3D.fieldOfView;
        if (objWidth / pos <= -Rasterizer2D.viewportCenterX)
            return;

        int yRot = offsetY * pitchCos - sceneX * pitchSine >> 16;
        int dimensionCosY = diagonal2DAboveOrigin * pitchSine >> 16;
       
        int var20 = (pitchCos * this.maxY >> 16) + dimensionCosY;
        int objHeight = (yRot + var20) * Rasterizer3D.fieldOfView;
        if (objHeight / pos <= -Rasterizer2D.viewportCenterY)
            return;

        int offset = dimensionCosY + (super.modelBaseY * pitchCos >> 16);
        int objY = (yRot - offset) * Rasterizer3D.fieldOfView;
        if (objY / pos >= Rasterizer2D.viewportCenterY)
            return;

        int size = dimensionSinY + (super.modelBaseY * pitchSine >> 16);

        boolean var25 = false;
        boolean nearSight = false;//wrong
        if (sceneY - size <= 50)
            nearSight = true;

        boolean inView = nearSight || this.texturesCount > 0;

        boolean var32 = method322(uid);
        boolean highlighted = false;

        int var52;
        int var53;
        int var54;
        if (var32 && mouseInViewport) {
            boolean withinBounds = false;

//			if (!ViewportMouse.ViewportMouse_false0) {
            byte distanceMin = 50;
            short distanceMax = 3500;
            int var43 = (cursorX - Rasterizer3D.originViewX) * distanceMin / Rasterizer3D.fieldOfView;
            int var44 = (cursorY - Rasterizer3D.originViewY) * distanceMin / Rasterizer3D.fieldOfView;
            int var45 = (cursorX - Rasterizer3D.originViewX) * distanceMax / Rasterizer3D.fieldOfView;
            int var46 = (cursorY - Rasterizer3D.originViewY) * distanceMax / Rasterizer3D.fieldOfView;
            int var47 = Rasterizer3D.method4045(var44, distanceMin, SceneGraph.camUpDownX, SceneGraph.camUpDownY);
            var53 = Rasterizer3D.method4046(var44, distanceMin, SceneGraph.camUpDownX, SceneGraph.camUpDownY);
            var44 = var47;
            var47 = Rasterizer3D.method4045(var46, distanceMax, SceneGraph.camUpDownX, SceneGraph.camUpDownY);
            var54 = Rasterizer3D.method4046(var46, distanceMax, SceneGraph.camUpDownX, SceneGraph.camUpDownY);
            var46 = var47;
            var47 = Rasterizer3D.method4025(var43, var53, SceneGraph.camLeftRightX, SceneGraph.camLeftRightY);
            var53 = Rasterizer3D.method4044(var43, var53, SceneGraph.camLeftRightX, SceneGraph.camLeftRightY);
            var43 = var47;
            var47 = Rasterizer3D.method4025(var45, var54, SceneGraph.camLeftRightX, SceneGraph.camLeftRightY);
            var54 = Rasterizer3D.method4044(var45, var54, SceneGraph.camLeftRightX, SceneGraph.camLeftRightY);
            int ViewportMouse_field2588 = (var43 + var47) / 2;
            int GZipDecompressor_field4821 = (var46 + var44) / 2;
            int class340_field4138 = (var54 + var53) / 2;
            int ViewportMouse_field2589 = (var47 - var43) / 2;
            int ItemComposition_field2148 = (var46 - var44) / 2;
            int User_field4308 = (var54 - var53) / 2;
            int class421_field4607 = Math.abs(ViewportMouse_field2589);
            int ViewportMouse_field2590 = Math.abs(ItemComposition_field2148);
            int class136_field1612 = Math.abs(User_field4308);
//			}

            int var37 = this.xMid + offsetX;
            int var38 = offsetY + this.yMid;
            int var39 = offsetZ + this.zMid;
            int var40 = this.xMidOffset;
            var53 = this.yMidOffset;
            var54 = this.zMidOffset;
            var43 = ViewportMouse_field2588 - var37;
            var44 = GZipDecompressor_field4821 - var38;
            var45 = class340_field4138 - var39;
            if (Math.abs(var43) > var40 + class421_field4607) {
                withinBounds = false;
            } else if (Math.abs(var44) > var53 + ViewportMouse_field2590) {
                withinBounds = false;
            } else if (Math.abs(var45) > var54 + class136_field1612) {
                withinBounds = false;
            } else if (Math.abs(var45 * ItemComposition_field2148 - var44 * User_field4308) > var53 * class136_field1612 + var54 * ViewportMouse_field2590) {
                withinBounds = false;
            } else if (Math.abs(var43 * User_field4308 - var45 * ViewportMouse_field2589) > var54 * class421_field4607 + var40 * class136_field1612) {
                withinBounds = false;
            } else if (Math.abs(var44 * ViewportMouse_field2589 - var43 * ItemComposition_field2148) > var40 * ViewportMouse_field2590 + var53 * class421_field4607) {
                withinBounds = false;
            } else {
                withinBounds = true;
            }
//          }

            if (withinBounds) {
                if (this.singleTile) {
                   
                    hoveringObjects[objectsHovering++] = uid;
                    if (gpu) {
                        Client.instance.getDrawCallbacks().draw(this, orientation, pitchSine, pitchCos, yawSin, yawCos, offsetX, offsetY, offsetZ, uid);
                        return;
                    }
                } else {
                    highlighted = true;
                }
            }
        }

        int var51 = Rasterizer3D.originViewX;
        var52 = Rasterizer3D.originViewY;
        int sineX = 0;
        int cosineX = 0;
        if (orientation != 0) {
            sineX = SINE[orientation];
            cosineX = COSINE[orientation];
        }

        for (int index = 0; index < this.verticesCount; ++index) {
            int positionX = this.verticesX[index];
            int rasterY = this.verticesY[index];
            int positionZ = this.verticesZ[index];
            if (orientation != 0) {
                int rotatedX = positionZ * sineX + positionX * cosineX >> 16;
                positionZ = positionZ * cosineX - positionX * sineX >> 16;
                positionX = rotatedX;
            }

            positionX += offsetX;
            rasterY += offsetY;
            positionZ += offsetZ;
            int positionY = positionZ * yawSin + yawCos * positionX >> 16;
            positionZ = yawCos * positionZ - positionX * yawSin >> 16;
            positionX = positionY;
            positionY = pitchCos * rasterY - positionZ * pitchSine >> 16;
            positionZ = rasterY * pitchSine + pitchCos * positionZ >> 16;
            vertexScreenZ[index] = positionZ - sceneY;
            if (positionZ >= 50) {
                vertexScreenX[index] = positionX * Rasterizer3D.fieldOfView / positionZ + var51;
                vertexScreenY[index] = positionY * Rasterizer3D.fieldOfView / positionZ + var52;
            } else {
                vertexScreenX[index] = -5000;
                var25 = true;
            }

            if (inView) {
                vertexMovedX[index] = positionX;
                vertexMovedY[index] = positionY;
                vertexMovedZ[index] = positionZ;
            }
        }

        try {
           
            if (!gpu || (highlighted && !(Math.sqrt(offsetX * offsetX + offsetZ * offsetZ) > 35 * Perspective.LOCAL_TILE_SIZE))) {
                withinObject(nearSight, highlighted, uid);
            }
            if (gpu) {
                Client.instance.getDrawCallbacks().draw(this, orientation, pitchSine, pitchCos, yawSin, yawCos, offsetX, offsetY, offsetZ, uid);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void withinObject(boolean flag, boolean hoverObject, long uid) {

        for (int diagonalIndex = 0; diagonalIndex < diagonal3D; diagonalIndex++) {
            depth[diagonalIndex] = 0;
        }

        for (int currentTriangle = 0; currentTriangle < trianglesCount; currentTriangle++) {
            if (drawType == null || drawType[currentTriangle] != -1) {
                int triX = trianglesX[currentTriangle];
                int triY = trianglesY[currentTriangle];
                int triZ = trianglesZ[currentTriangle];
                int screenXX = vertexScreenX[triX];
                int screenXY = vertexScreenX[triY];
                int screenXZ = vertexScreenX[triZ];

                if (flag && (screenXX == -5000 || screenXY == -5000 || screenXZ == -5000)) {
                    outOfReach[currentTriangle] = true;
                    int faceIndex = (vertexScreenZ[triX] + vertexScreenZ[triY] + vertexScreenZ[triZ]) / 3 + diagonal3DAboveOrigin;
                    faceLists[faceIndex][depth[faceIndex]++] = currentTriangle;
                } else {
                    if (hoverObject && inBounds(cursorX, cursorY, vertexScreenY[triX], vertexScreenY[triY], vertexScreenY[triZ], screenXX, screenXY, screenXZ)) {
                        hoveringObjects[objectsHovering++] = uid;
                        hoverObject = false;
                    }
                    if ((screenXX - screenXY) * (vertexScreenY[triZ] - vertexScreenY[triY]) - (vertexScreenY[triX] - vertexScreenY[triY]) * (screenXZ - screenXY) > 0) {
                        outOfReach[currentTriangle] = false;
                        hasAnEdgeToRestrict[currentTriangle] = screenXX < 0 || screenXY < 0 || screenXZ < 0 || screenXX > Rasterizer2D.lastX || screenXY > Rasterizer2D.lastX || screenXZ > Rasterizer2D.lastX;
                        int faceIndex = (vertexScreenZ[triX] + vertexScreenZ[triY] + vertexScreenZ[triZ]) / 3 + diagonal3DAboveOrigin;
                        faceLists[faceIndex][depth[faceIndex]++] = currentTriangle;
                    }
                }
            }
        }

        if (renderPriorities == null) {
            for (int i1 = diagonal3D - 1; i1 >= 0; i1--) {
                int l1 = depth[i1];
                if (l1 > 0) {
                    int[] ai = faceLists[i1];
                    for (int j3 = 0; j3 < l1; j3++) {
                        drawFace(ai[j3]);
                    }

                }
            }
            return;
        }

        for (int currentIndex = 0; currentIndex < 12; currentIndex++) {
            anIntArray1673[currentIndex] = 0;
            anIntArray1677[currentIndex] = 0;
        }

        for (int depthIndex = diagonal3D - 1; depthIndex >= 0; depthIndex--) {
            int k2 = depth[depthIndex];
            if (k2 > 0) {
                int[] ai1 = faceLists[depthIndex];
                for (int i4 = 0; i4 < k2; i4++) {
                    int l4 = ai1[i4];
                    int l5 = renderPriorities[l4];
                    int j6 = anIntArray1673[l5]++;
                    anIntArrayArray1674[l5][j6] = l4;
                    if (l5 < 10) {
                        anIntArray1677[l5] += depthIndex;
                    } else if (l5 == 10) {
                        anIntArray1675[j6] = depthIndex;
                    } else {
                        anIntArray1676[j6] = depthIndex;
                    }
                }

            }
        }

        int l2 = 0;
        if (anIntArray1673[1] > 0 || anIntArray1673[2] > 0) {
            l2 = (anIntArray1677[1] + anIntArray1677[2]) / (anIntArray1673[1] + anIntArray1673[2]);
        }
        int k3 = 0;
        if (anIntArray1673[3] > 0 || anIntArray1673[4] > 0) {
            k3 = (anIntArray1677[3] + anIntArray1677[4]) / (anIntArray1673[3] + anIntArray1673[4]);
        }
        int j4 = 0;
        if (anIntArray1673[6] > 0 || anIntArray1673[8] > 0) {
            j4 = (anIntArray1677[6] + anIntArray1677[8]) / (anIntArray1673[6] + anIntArray1673[8]);
        }
        int i6 = 0;
        int k6 = anIntArray1673[10];
        int[] ai2 = anIntArrayArray1674[10];
        int[] ai3 = anIntArray1675;
        if (i6 == k6) {
            k6 = anIntArray1673[11];
            ai2 = anIntArrayArray1674[11];
            ai3 = anIntArray1676;
        }
        int i5;
        if (i6 < k6) {
            i5 = ai3[i6];
        } else {
            i5 = -1000;
        }
        for (int l6 = 0; l6 < 10; l6++) {
            while (l6 == 0 && i5 > l2) {
                drawFace(ai2[i6++]);
                if (i6 == k6 && ai2 != anIntArrayArray1674[11]) {
                    i6 = 0;
                    k6 = anIntArray1673[11];
                    ai2 = anIntArrayArray1674[11];
                    ai3 = anIntArray1676;
                }
                if (i6 < k6) {
                    i5 = ai3[i6];
                } else {
                    i5 = -1000;
                }
            }
            while (l6 == 3 && i5 > k3) {
                drawFace(ai2[i6++]);
                if (i6 == k6 && ai2 != anIntArrayArray1674[11]) {
                    i6 = 0;
                    k6 = anIntArray1673[11];
                    ai2 = anIntArrayArray1674[11];
                    ai3 = anIntArray1676;
                }
                if (i6 < k6) {
                    i5 = ai3[i6];
                } else {
                    i5 = -1000;
                }
            }
            while (l6 == 5 && i5 > j4) {
                drawFace(ai2[i6++]);
                if (i6 == k6 && ai2 != anIntArrayArray1674[11]) {
                    i6 = 0;
                    k6 = anIntArray1673[11];
                    ai2 = anIntArrayArray1674[11];
                    ai3 = anIntArray1676;
                }
                if (i6 < k6) {
                    i5 = ai3[i6];
                } else {
                    i5 = -1000;
                }
            }
            int i7 = anIntArray1673[l6];
            int[] ai4 = anIntArrayArray1674[l6];
            for (int j7 = 0; j7 < i7; j7++) {
                drawFace(ai4[j7]);
            }

        }

        while (i5 != -1000) {
            drawFace(ai2[i6++]);
            if (i6 == k6 && ai2 != anIntArrayArray1674[11]) {
                i6 = 0;
                ai2 = anIntArrayArray1674[11];
                k6 = anIntArray1673[11];
                ai3 = anIntArray1676;
            }
            if (i6 < k6) {
                i5 = ai3[i6];
            } else {
                i5 = -1000;
            }
        }

    }


    @Override
    public void drawFace(int face) {
        DrawCallbacks callbacks = Client.instance.getDrawCallbacks();
        if (callbacks == null || !callbacks.drawFace(this, face))
        {
            if (outOfReach[face]) {
                faceRotation(face);
                return;
            }
            int triX = trianglesX[face];
            int triY = trianglesY[face];
            int triZ = trianglesZ[face];
            Rasterizer3D.textureOutOfDrawingBounds = hasAnEdgeToRestrict[face];
            if (triangleAlpha == null) {
                Rasterizer3D.alpha = 0;
            } else {
                Rasterizer3D.alpha = triangleAlpha[face] & 255;
            }
            int type;
            if (drawType == null) {
                type = 0;
            } else {
                type = drawType[face] & 3;
            }

            if (materials != null && materials[face] != -1) {
                int textureA = triX;
                int textureB = triY;
                int textureC = triZ;
                if (textures != null && textures[face] != -1) {
                    int coordinate = textures[face] & 0xff;
                    textureA = texturesX[coordinate];
                    textureB = texturesY[coordinate];
                    textureC = texturesZ[coordinate];
                }
                if (colorsZ[face] == -1 || type == 3) {
                    Rasterizer3D.drawTexturedTriangle(
                            vertexScreenY[triX], vertexScreenY[triY], vertexScreenY[triZ],
                            vertexScreenX[triX], vertexScreenX[triY], vertexScreenX[triZ],
                            colorsX[face], colorsX[face], colorsX[face],
                            vertexMovedX[textureA], vertexMovedX[textureB], vertexMovedX[textureC],
                            vertexMovedY[textureA], vertexMovedY[textureB], vertexMovedY[textureC],
                            vertexMovedZ[textureA], vertexMovedZ[textureB], vertexMovedZ[textureC],
                            materials[face]);
                } else {
                    Rasterizer3D.drawTexturedTriangle(
                            vertexScreenY[triX], vertexScreenY[triY], vertexScreenY[triZ],
                            vertexScreenX[triX], vertexScreenX[triY], vertexScreenX[triZ],
                            colorsX[face], colorsY[face], colorsZ[face],
                            vertexMovedX[textureA], vertexMovedX[textureB], vertexMovedX[textureC],
                            vertexMovedY[textureA], vertexMovedY[textureB], vertexMovedY[textureC],
                            vertexMovedZ[textureA], vertexMovedZ[textureB], vertexMovedZ[textureC],
                            materials[face]);
                }
            } else {
                if (type == 0) {
                    Rasterizer3D.drawShadedTriangle(vertexScreenY[triX], vertexScreenY[triY],
                            vertexScreenY[triZ], vertexScreenX[triX], vertexScreenX[triY],
                            vertexScreenX[triZ], colorsX[face], colorsY[face], colorsZ[face]);
                }
                if (type == 1) {
                    Rasterizer3D.drawFlatTriangle(vertexScreenY[triX], vertexScreenY[triY],
                            vertexScreenY[triZ], vertexScreenX[triX], vertexScreenX[triY],
                            vertexScreenX[triZ], modelColors[colorsX[face]]);
                }
            }

        }
    }


    private final void faceRotation(int triangle) {
        int centreX = Rasterizer3D.originViewX;
        int centreY = Rasterizer3D.originViewY;
        int counter = 0;
        int x = trianglesX[triangle];
        int y = trianglesY[triangle];
        int z = trianglesZ[triangle];
        int movedX = vertexMovedZ[x];
        int movedY = vertexMovedZ[y];
        int movedZ = vertexMovedZ[z];
        if (movedX >= 50) {
            xPosition[counter] = vertexScreenX[x];
            yPosition[counter] = vertexScreenY[x];
            zPosition[counter++] = colorsX[triangle];
        } else {
            int movedX2 = vertexMovedX[x];
            int movedY2 = vertexMovedY[x];
            int colour = colorsX[triangle];
            if (movedZ >= 50) {
                int k5 = (50 - movedX) * modelLocations[movedZ - movedX];
                xPosition[counter] = centreX + (movedX2 + ((vertexMovedX[z] - movedX2) * k5 >> 16)) * Rasterizer3D.fieldOfView / 50;
                yPosition[counter] = centreY + (movedY2 + ((vertexMovedY[z] - movedY2) * k5 >> 16)) * Rasterizer3D.fieldOfView / 50;
                zPosition[counter++] = colour + ((colorsZ[triangle] - colour) * k5 >> 16);
            }
            if (movedY >= 50) {
                int l5 = (50 - movedX) * modelLocations[movedY - movedX];
                xPosition[counter] = centreX + (movedX2 + ((vertexMovedX[y] - movedX2) * l5 >> 16)) * Rasterizer3D.fieldOfView / 50;
                yPosition[counter] = centreY + (movedY2 + ((vertexMovedY[y] - movedY2) * l5 >> 16)) * Rasterizer3D.fieldOfView / 50;
                zPosition[counter++] = colour + ((colorsY[triangle] - colour) * l5 >> 16);
            }
        }
        if (movedY >= 50) {
            xPosition[counter] = vertexScreenX[y];
            yPosition[counter] = vertexScreenY[y];
            zPosition[counter++] = colorsY[triangle];
        } else {
            int movedX2 = vertexMovedX[y];
            int movedY2 = vertexMovedY[y];
            int colour = colorsY[triangle];
            if (movedX >= 50) {
                int i6 = (50 - movedY) * modelLocations[movedX - movedY];
                xPosition[counter] = centreX + (movedX2 + ((vertexMovedX[x] - movedX2) * i6 >> 16)) * Rasterizer3D.fieldOfView / 50;
                yPosition[counter] = centreY + (movedY2 + ((vertexMovedY[x] - movedY2) * i6 >> 16)) * Rasterizer3D.fieldOfView / 50;
                zPosition[counter++] = colour + ((colorsX[triangle] - colour) * i6 >> 16);
            }
            if (movedZ >= 50) {
                int j6 = (50 - movedY) * modelLocations[movedZ - movedY];
                xPosition[counter] = centreX + (movedX2 + ((vertexMovedX[z] - movedX2) * j6 >> 16)) * Rasterizer3D.fieldOfView / 50;
                yPosition[counter] = centreY + (movedY2 + ((vertexMovedY[z] - movedY2) * j6 >> 16)) * Rasterizer3D.fieldOfView / 50;
                zPosition[counter++] = colour + ((colorsZ[triangle] - colour) * j6 >> 16);
            }
        }
        if (movedZ >= 50) {
            xPosition[counter] = vertexScreenX[z];
            yPosition[counter] = vertexScreenY[z];
            zPosition[counter++] = colorsZ[triangle];
        } else {
            int movedX2 = vertexMovedX[z];
            int movedY2 = vertexMovedY[z];
            int colour = colorsZ[triangle];
            if (movedY >= 50) {
                int k6 = (50 - movedZ) * modelLocations[movedY - movedZ];
                xPosition[counter] = centreX + (movedX2 + ((vertexMovedX[y] - movedX2) * k6 >> 16)) * Rasterizer3D.fieldOfView / 50;
                yPosition[counter] = centreY + (movedY2 + ((vertexMovedY[y] - movedY2) * k6 >> 16)) * Rasterizer3D.fieldOfView / 50;
                zPosition[counter++] = colour + ((colorsY[triangle] - colour) * k6 >> 16);
            }
            if (movedX >= 50) {
                int l6 = (50 - movedZ) * modelLocations[movedX - movedZ];
                xPosition[counter] = centreX + (movedX2 + ((vertexMovedX[x] - movedX2) * l6 >> 16)) * Rasterizer3D.fieldOfView / 50;
                yPosition[counter] = centreY + (movedY2 + ((vertexMovedY[x] - movedY2) * l6 >> 16)) * Rasterizer3D.fieldOfView / 50;
                zPosition[counter++] = colour + ((colorsX[triangle] - colour) * l6 >> 16);
            }
        }
        int xA = xPosition[0];
        int xB = xPosition[1];
        int xC = xPosition[2];
        int yA = yPosition[0];
        int yB = yPosition[1];
        int yC = yPosition[2];
        if ((xA - xB) * (yC - yB) - (yA - yB) * (xC - xB) > 0) {
            Rasterizer3D.textureOutOfDrawingBounds = false;
            int textureA = x;
            int textureB = y;
            int textureC = z;
            if (counter == 3) {
                if (xA < 0 || xB < 0 || xC < 0 || xA > Rasterizer2D.lastX || xB > Rasterizer2D.lastX || xC > Rasterizer2D.lastX) {
                    Rasterizer3D.textureOutOfDrawingBounds = true;
                }

                int drawType;
                if (this.drawType == null) {
                    drawType = 0;
                } else {
                    drawType = this.drawType[triangle] & 3;
                }

                if (materials != null && materials[triangle] != -1) {

                    if (textures != null && textures[triangle] != -1) {
                        int coordinate = textures[triangle] & 0xff;
                        textureA = texturesX[coordinate];
                        textureB = texturesY[coordinate];
                        textureC = texturesZ[coordinate];
                    }

                    if (colorsZ[triangle] == -1) {
                        Rasterizer3D.drawTexturedTriangle(
                                yA, yB, yC,
                                xA, xB, xC,
                                colorsX[triangle], colorsX[triangle], colorsX[triangle],
                                vertexMovedX[textureA], vertexMovedX[textureB], vertexMovedX[textureC],
                                vertexMovedY[textureA], vertexMovedY[textureB], vertexMovedY[textureC],
                                vertexMovedZ[textureA], vertexMovedZ[textureB], vertexMovedZ[textureC],
                                materials[triangle]);
                    } else {
                        Rasterizer3D.drawTexturedTriangle(
                                yA, yB, yC,
                                xA, xB, xC,
                                zPosition[0], zPosition[1], zPosition[2],
                                vertexMovedX[textureA], vertexMovedX[textureB], vertexMovedX[textureC],
                                vertexMovedY[textureA], vertexMovedY[textureB], vertexMovedY[textureC],
                                vertexMovedZ[textureA], vertexMovedZ[textureB], vertexMovedZ[textureC],
                                materials[triangle]);
                    }
                } else {
                    if (drawType == 0) {
                        Rasterizer3D.drawShadedTriangle(yA, yB, yC, xA, xB, xC, zPosition[0], zPosition[1], zPosition[2]);
                    } else if (drawType == 1) {
                        Rasterizer3D.drawFlatTriangle(yA, yB, yC, xA, xB, xC, modelColors[colorsX[triangle]]);
                    }
                }
            }
            if (counter == 4) {
                if (xA < 0 || xB < 0 || xC < 0 || xA > Rasterizer2D.lastX || xB > Rasterizer2D.lastX || xC > Rasterizer2D.lastX || xPosition[3] < 0 || xPosition[3] > Rasterizer2D.lastX) {
                    Rasterizer3D.textureOutOfDrawingBounds = true;
                }
                int drawType;
                if (this.drawType == null) {
                    drawType = 0;
                } else {
                    drawType = this.drawType[triangle] & 3;
                }

                if (materials != null && materials[triangle] != -1) {
                    if (textures != null && textures[triangle] != -1) {
                        int coordinate = textures[triangle] & 0xff;
                        textureA = texturesX[coordinate];
                        textureB = texturesY[coordinate];
                        textureC = texturesZ[coordinate];
                    }
                    if (colorsZ[triangle] == -1) {
                        Rasterizer3D.drawTexturedTriangle(
                                yA, yB, yC,
                                xA, xB, xC,
                                colorsX[triangle], colorsX[triangle], colorsX[triangle],
                                vertexMovedX[textureA], vertexMovedX[textureB], vertexMovedX[textureC],
                                vertexMovedY[textureA], vertexMovedY[textureB], vertexMovedY[textureC],
                                vertexMovedZ[textureA], vertexMovedZ[textureB], vertexMovedZ[textureC],
                                materials[triangle]);
                        Rasterizer3D.drawTexturedTriangle(
                                yA, yC, yPosition[3],
                                xA, xC, xPosition[3],
                                colorsX[triangle], colorsX[triangle], colorsX[triangle],
                                vertexMovedX[textureA], vertexMovedX[textureB], vertexMovedX[textureC],
                                vertexMovedY[textureA], vertexMovedY[textureB], vertexMovedY[textureC],
                                vertexMovedZ[textureA], vertexMovedZ[textureB], vertexMovedZ[textureC],
                                materials[triangle]);
                    } else {
                        Rasterizer3D.drawTexturedTriangle(
                                yA, yB, yC,
                                xA, xB, xC,
                                zPosition[0], zPosition[1], zPosition[2],
                                vertexMovedX[textureA], vertexMovedX[textureB], vertexMovedX[textureC],
                                vertexMovedY[textureA], vertexMovedY[textureB], vertexMovedY[textureC],
                                vertexMovedZ[textureA], vertexMovedZ[textureB], vertexMovedZ[textureC],
                                materials[triangle]);
                        Rasterizer3D.drawTexturedTriangle(
                                yA, yC, yPosition[3],
                                xA, xC, xPosition[3],
                                zPosition[0], zPosition[2], zPosition[3],
                                vertexMovedX[textureA], vertexMovedX[textureB], vertexMovedX[textureC],
                                vertexMovedY[textureA], vertexMovedY[textureB], vertexMovedY[textureC],
                                vertexMovedZ[textureA], vertexMovedZ[textureB], vertexMovedZ[textureC],
                                materials[triangle]);
                    }
                } else {
                    if (drawType == 0) {
                        Rasterizer3D.drawShadedTriangle(yA, yB, yC, xA, xB, xC, zPosition[0], zPosition[1], zPosition[2]);
                        Rasterizer3D.drawShadedTriangle(yA, yC, yPosition[3], xA, xC, xPosition[3], zPosition[0], zPosition[2], zPosition[3]);
                        return;
                    }
                    if (drawType == 1) {
                        int l8 = modelColors[colorsX[triangle]];
                        Rasterizer3D.drawFlatTriangle(yA, yB, yC, xA, xB, xC, l8);
                        Rasterizer3D.drawFlatTriangle(yA, yC, yPosition[3], xA, xC, xPosition[3], l8);
                    }
                }
            }
        }
    }

    private boolean inBounds(int cursorX, int cursorY, int z1, int z2, int z3, int screenXX, int screenXY, int screenXZ) {
        return (cursorY >= z1 || cursorY >= z2 || cursorY >= z3) && (cursorY <= z1 || cursorY <= z2 || cursorY <= z3) && (cursorX >= screenXX || cursorX >= screenXY || cursorX >= screenXZ) && (cursorX <= screenXX || cursorX <= screenXY || cursorX <= screenXZ);
    }

    public int animayaGroups[][];

    public int animayaScales[][];

    private int xMid;
    private int yMid;
    private int zMid;
    private int xMidOffset;
    private int yMidOffset;
    private int zMidOffset;

    private float[] faceTextureUVCoordinates;
    private int[] vertexNormalsX, vertexNormalsY, vertexNormalsZ;

    public short[] materials;
    public byte[] textures;
    public byte[] textureTypes;

    public static int anInt1620;
    public static Model emptyModel = new Model();
    private static int sharedVerticesX[] = new int[2000];
    private static int sharedVerticesY[] = new int[2000];
    private static int sharedVerticesZ[] = new int[2000];
    private static byte sharedTriangleAlpha[] = new byte[2000];
    public int verticesCount;
    public int verticesX[];
    public int verticesY[];
    public int verticesZ[];
    public int trianglesCount;
    public int trianglesX[];
    public int trianglesY[];
    public int trianglesZ[];
    public int colorsX[];
    public int colorsY[];
    public int colorsZ[];
    public int drawType[];
    public byte[] renderPriorities;
    public byte triangleAlpha[];
    public short colors[];
    public byte facePriority = 0;
    public int texturesCount;
    public short texturesX[];
    public short texturesY[];
    public short texturesZ[];
    public int minX;
    public int maxX;
    public int maxZ;
    public int minZ;
    public int diagonal2DAboveOrigin;
    public int maxY;
    public int diagonal3D;
    public int diagonal3DAboveOrigin;
    public int itemDropHeight;
    public int vertexData[];
    public int triangleData[];
    public int vertexGroups[][];
    public int faceGroups[][];
    public boolean singleTile;
    public VertexNormal vertexNormalsOffsets[];
    private FaceNormal[] faceNormals;
    static ModelHeader modelHeaders[];
    static boolean hasAnEdgeToRestrict[] = new boolean[6500];
    static boolean outOfReach[] = new boolean[6500];
    static int vertexScreenX[] = new int[6500];
    static int vertexScreenY[] = new int[6500];
    static int vertexScreenZ[] = new int[6500];
    static int vertexMovedX[] = new int[6500];
    static int vertexMovedY[] = new int[6500];
    static int vertexMovedZ[] = new int[6500];
    static int depth[] = new int[1600];
    static int faceLists[][] = new int[1600][512];
    static int anIntArray1673[] = new int[12];
    static int anIntArrayArray1674[][] = new int[12][2000];
    static int anIntArray1676[] = new int[2000];
    static int anIntArray1675[] = new int[2000];
    static int anIntArray1677[] = new int[12];
    static int xPosition[] = new int[10];
    static int yPosition[] = new int[10];
    static int zPosition[] = new int[10];
    static int transformTempX;
    static int transformTempY;
    static int transformTempZ;
    public static boolean objectExist;
    public static int cursorX;
    public static int cursorY;
    public static int objectsHovering;
    public static long hoveringObjects[] = new long[1000];
    public static int SINE[];
    public static int COSINE[];
    static int modelColors[];
    static int modelLocations[];

    static {
        SINE = Rasterizer3D.SINE;
        COSINE = Rasterizer3D.COSINE;
        modelColors = Rasterizer3D.hslToRgb;
        modelLocations = Rasterizer3D.anIntArray1469;
    }


    public int bufferOffset;
    public int uvBufferOffset;

    public java.util.List<net.runelite.api.model.Vertex> getVertices() {
        int[] verticesX = getVerticesX();
        int[] verticesY = getVerticesY();
        int[] verticesZ = getVerticesZ();
        ArrayList<net.runelite.api.model.Vertex> vertices = new ArrayList<>(getVerticesCount());
        for (int i = 0; i < getVerticesCount(); i++) {
            net.runelite.api.model.Vertex vertex = new net.runelite.api.model.Vertex(verticesX[i], verticesY[i], verticesZ[i]);
            vertices.add(vertex);
        }
        return vertices;
    }


    @Override
    public List<Triangle> getTriangles() {
        int[] trianglesX = getFaceIndices1();
        int[] trianglesY = getFaceIndices2();
        int[] trianglesZ = getFaceIndices3();

        List<Vertex> vertices = getVertices();
        List<Triangle> triangles = new ArrayList<>(getFaceCount());

        for (int i = 0; i < getFaceCount(); ++i)
        {
            int triangleX = trianglesX[i];
            int triangleY = trianglesY[i];
            int triangleZ = trianglesZ[i];

            Triangle triangle = new Triangle(vertices.get(triangleX),vertices.get(triangleY),vertices.get(triangleZ));
            triangles.add(triangle);
        }

        return triangles;
    }


    @Override
    public int getVerticesCount() {
        return verticesCount;
    }

    @Override
    public int[] getVerticesX() {
        return verticesX;
    }

    @Override
    public int[] getVerticesY() {
        return verticesY;
    }

    @Override
    public int[] getVerticesZ() {
        return verticesZ;
    }

    @Override
    public int getFaceCount() {
        return this.trianglesCount;
    }

    @Override
    public int[] getFaceIndices1() {
        return trianglesX;
    }

    @Override
    public int[] getFaceIndices2() {
        return trianglesY;
    }

    @Override
    public int[] getFaceIndices3() {
        return trianglesZ;
    }

    @Override
    public int[] getFaceColors1() {
        return this.colorsX;
    }

    @Override
    public int[] getFaceColors2() {
        return colorsY;
    }

    @Override
    public int[] getFaceColors3() {
        return colorsZ;
    }

    @Override
    public byte[] getFaceTransparencies() {
        return triangleAlpha;
    }

    private int sceneId;
    @Override
    public int getSceneId() {
        return sceneId;
    }

    @Override
    public void setSceneId(int sceneId) {
        this.sceneId = sceneId;
    }

    public int getBufferOffset() {
        return bufferOffset;
    }

    public void setBufferOffset(int bufferOffset) {
        this.bufferOffset = bufferOffset;
    }

    public int getUvBufferOffset() {
        return uvBufferOffset;
    }

    public void setUvBufferOffset(int uvBufferOffset) {
        this.uvBufferOffset = uvBufferOffset;
    }

    @Override
    public int getModelHeight() {
        return modelBaseY;
    }

    @Override
    public void animate(int type, int[] list, int x, int y, int z) {

    }

    @Override
    public void calculateBoundsCylinder() {
        calculateDiagonals();
    }

    @Override
    public byte[] getFaceRenderPriorities() {
        return this.renderPriorities;
    }

    @Override
    public int[][] getVertexGroups() {
        return new int[0][];
    }

    @Override
    public int getRadius() {
        return diagonal3DAboveOrigin;
    }

    @Override
    public int getDiameter() {
        return diagonal3D;
    }

    @Override
    public short[] getFaceTextures() {
        return materials;
    }

    @Override
    public void calculateExtreme(int orientation) {

    }

    @Override
    public void resetBounds() {
        this.boundsType = 0;
        this.xMidOffset = -1;
    }

    @Override
    public RSModel toSharedModel(boolean b) {
        return null;
    }

    @Override
    public RSModel toSharedSpotAnimModel(boolean b) {
        return null;
    }

    @Override
    public void rotateY90Ccw() {
        rotate90Degrees();
    }

    @Override
    public void rotateY180Ccw() {
        for (int var1 = 0; var1 < this.verticesCount; ++var1)
        {
            this.verticesX[var1] = -this.verticesX[var1];
            this.verticesZ[var1] = -this.verticesZ[var1];
        }

        this.resetBounds();
    }

    @Override
    public void rotateY270Ccw() {
        for (int var1 = 0; var1 < this.verticesCount; ++var1)
        {
            int var2 = this.verticesZ[var1];
            this.verticesZ[var1] = this.verticesX[var1];
            this.verticesX[var1] = -var2;
        }

        this.resetBounds();
    }

    @Override
    public int getCenterX() {
        return xMid;
    }

    @Override
    public int getCenterY() {
        return yMid;
    }

    @Override
    public int getCenterZ() {
        return zMid;
    }

    @Override
    public int getExtremeX() {
        return xMidOffset;
    }

    @Override
    public int getExtremeY() {
        return yMidOffset;
    }

    @Override
    public int getExtremeZ() {
        return zMidOffset;
    }

    @Override
    public int getXYZMag() {
        return diagonal2DAboveOrigin;
    }

    @Override
    public boolean isClickable() {
        return singleTile;
    }

    @Override
    public void interpolateFrames(RSFrames frames, int frameId, RSFrames nextFrames, int nextFrameId, int interval, int intervalCount) {

    }

    @Override
    public int[] getVertexNormalsX() {
        if(vertexNormalsX == null)
            return getVerticesX();
        return vertexNormalsX;
    }

    @Override
    public void setVertexNormalsX(int[] vertexNormalsX) {
        this.vertexNormalsX = vertexNormalsX;
    }

    @Override
    public int[] getVertexNormalsY() {
        if(vertexNormalsY == null)
            return getVerticesY();
        return vertexNormalsY;
    }

    @Override
    public void setVertexNormalsY(int[] vertexNormalsY) {
        this.vertexNormalsY = vertexNormalsY;
    }

    @Override
    public int[] getVertexNormalsZ() {
        if(vertexNormalsZ == null)
            return getVerticesZ();
        return vertexNormalsZ;
    }

    @Override
    public void setVertexNormalsZ(int[] vertexNormalsZ) {
        this.vertexNormalsZ = vertexNormalsZ;
    }

    @Override
    public byte getOverrideAmount() {
        return 0;
    }

    @Override
    public byte getOverrideHue() {
        return 0;
    }

    @Override
    public byte getOverrideSaturation() {
        return 0;
    }

    @Override
    public byte getOverrideLuminance() {
        return 0;
    }

    @Override
    public Shape getConvexHull(int localX, int localY, int orientation, int tileHeight) {
        int[] x2d = new int[getVerticesCount()];
        int[] y2d = new int[getVerticesCount()];

        Perspective.modelToCanvas(Client.instance, getVerticesCount(), localX, localY, tileHeight, orientation, getVerticesX(), getVerticesZ(), getVerticesY(), x2d, y2d);

        return Jarvis.convexHull(x2d, y2d);
    }


    @Override
    public float[] getFaceTextureUVCoordinates() {
        if (faceTextureUVCoordinates == null) {
            computeTextureUvCoordinates();
        }
        return faceTextureUVCoordinates;
    }

    @Override
    public void setFaceTextureUVCoordinates(float[] faceTextureUVCoordinates) {
        this.faceTextureUVCoordinates = faceTextureUVCoordinates;
    }

    @Override
    public int getBottomY() {
        return modelBaseY;
    }

    private void vertexNormals()
    {

        if (vertexNormalsX == null)
        {
            int verticesCount = getVerticesCount();

            vertexNormalsX = new int[verticesCount];
            vertexNormalsY = new int[verticesCount];
            vertexNormalsZ = new int[verticesCount];

            int[] trianglesX = getFaceIndices1();
            int[] trianglesY = getFaceIndices2();
            int[] trianglesZ = getFaceIndices3();
            int[] verticesX = getVerticesX();
            int[] verticesY = getVerticesY();
            int[] verticesZ = getVerticesZ();

            for (int i = 0; i < trianglesCount; ++i)
            {
                int var9 = trianglesX[i];
                int var10 = trianglesY[i];
                int var11 = trianglesZ[i];

                int var12 = verticesX[var10] - verticesX[var9];
                int var13 = verticesY[var10] - verticesY[var9];
                int var14 = verticesZ[var10] - verticesZ[var9];
                int var15 = verticesX[var11] - verticesX[var9];
                int var16 = verticesY[var11] - verticesY[var9];
                int var17 = verticesZ[var11] - verticesZ[var9];

                int var18 = var13 * var17 - var16 * var14;
                int var19 = var14 * var15 - var17 * var12;

                int var20;
                for (var20 = var12 * var16 - var15 * var13; var18 > 8192 || var19 > 8192 || var20 > 8192 || var18 < -8192 || var19 < -8192 || var20 < -8192; var20 >>= 1)
                {
                    var18 >>= 1;
                    var19 >>= 1;
                }

                int var21 = (int) Math.sqrt(var18 * var18 + var19 * var19 + var20 * var20);
                if (var21 <= 0)
                {
                    var21 = 1;
                }

                var18 = var18 * 256 / var21;
                var19 = var19 * 256 / var21;
                var20 = var20 * 256 / var21;

                vertexNormalsX[var9] += var18;
                vertexNormalsY[var9] += var19;
                vertexNormalsZ[var9] += var20;

                vertexNormalsX[var10] += var18;
                vertexNormalsY[var10] += var19;
                vertexNormalsZ[var10] += var20;

                vertexNormalsX[var11] += var18;
                vertexNormalsY[var11] += var19;
                vertexNormalsZ[var11] += var20;
            }
        }
    }

    public void computeTextureUvCoordinates()
    {
        final short[] faceTextures = getFaceTextures();
        if (faceTextures == null)
        {
            return;
        }

        final int[] vertexPositionsX = getVertexNormalsX();
        final int[] vertexPositionsY = getVertexNormalsY();
        final int[] vertexPositionsZ = getVertexNormalsZ();

        final int[] trianglePointsX = getFaceIndices1();
        final int[] trianglePointsY = getFaceIndices2();
        final int[] trianglePointsZ = getFaceIndices3();

        final short[] texTriangleX = texturesX;
        final short[] texTriangleY = texturesY;
        final short[] texTriangleZ = texturesZ;

        final byte[] textureCoords = textures;

        int faceCount = trianglesCount;
        float[] faceTextureUCoordinates = new float[faceCount * 6];

        for (int i = 0; i < faceCount; i++)
        {
            int trianglePointX = trianglePointsX[i];
            int trianglePointY = trianglePointsY[i];
            int trianglePointZ = trianglePointsZ[i];

            short textureIdx = faceTextures[i];

            if (textureIdx != -1)
            {
                int triangleVertexIdx1;
                int triangleVertexIdx2;
                int triangleVertexIdx3;

                if (textureCoords != null && textureCoords[i] != -1)
                {
                    int textureCoordinate = textureCoords[i] & 255;
                    triangleVertexIdx1 = texTriangleX[textureCoordinate];
                    triangleVertexIdx2 = texTriangleY[textureCoordinate];
                    triangleVertexIdx3 = texTriangleZ[textureCoordinate];
                }
                else
                {
                    triangleVertexIdx1 = trianglePointX;
                    triangleVertexIdx2 = trianglePointY;
                    triangleVertexIdx3 = trianglePointZ;
                }

                float triangleX = (float) vertexPositionsX[triangleVertexIdx1];
                float triangleY = (float) vertexPositionsY[triangleVertexIdx1];
                float triangleZ = (float) vertexPositionsZ[triangleVertexIdx1];

                float f_882_ = (float) vertexPositionsX[triangleVertexIdx2] - triangleX;
                float f_883_ = (float) vertexPositionsY[triangleVertexIdx2] - triangleY;
                float f_884_ = (float) vertexPositionsZ[triangleVertexIdx2] - triangleZ;
                float f_885_ = (float) vertexPositionsX[triangleVertexIdx3] - triangleX;
                float f_886_ = (float) vertexPositionsY[triangleVertexIdx3] - triangleY;
                float f_887_ = (float) vertexPositionsZ[triangleVertexIdx3] - triangleZ;
                float f_888_ = (float) vertexPositionsX[trianglePointX] - triangleX;
                float f_889_ = (float) vertexPositionsY[trianglePointX] - triangleY;
                float f_890_ = (float) vertexPositionsZ[trianglePointX] - triangleZ;
                float f_891_ = (float) vertexPositionsX[trianglePointY] - triangleX;
                float f_892_ = (float) vertexPositionsY[trianglePointY] - triangleY;
                float f_893_ = (float) vertexPositionsZ[trianglePointY] - triangleZ;
                float f_894_ = (float) vertexPositionsX[trianglePointZ] - triangleX;
                float f_895_ = (float) vertexPositionsY[trianglePointZ] - triangleY;
                float f_896_ = (float) vertexPositionsZ[trianglePointZ] - triangleZ;

                float f_897_ = f_883_ * f_887_ - f_884_ * f_886_;
                float f_898_ = f_884_ * f_885_ - f_882_ * f_887_;
                float f_899_ = f_882_ * f_886_ - f_883_ * f_885_;
                float f_900_ = f_886_ * f_899_ - f_887_ * f_898_;
                float f_901_ = f_887_ * f_897_ - f_885_ * f_899_;
                float f_902_ = f_885_ * f_898_ - f_886_ * f_897_;
                float f_903_ = 1.0F / (f_900_ * f_882_ + f_901_ * f_883_ + f_902_ * f_884_);

                float u0 = (f_900_ * f_888_ + f_901_ * f_889_ + f_902_ * f_890_) * f_903_;
                float u1 = (f_900_ * f_891_ + f_901_ * f_892_ + f_902_ * f_893_) * f_903_;
                float u2 = (f_900_ * f_894_ + f_901_ * f_895_ + f_902_ * f_896_) * f_903_;

                f_900_ = f_883_ * f_899_ - f_884_ * f_898_;
                f_901_ = f_884_ * f_897_ - f_882_ * f_899_;
                f_902_ = f_882_ * f_898_ - f_883_ * f_897_;
                f_903_ = 1.0F / (f_900_ * f_885_ + f_901_ * f_886_ + f_902_ * f_887_);

                float v0 = (f_900_ * f_888_ + f_901_ * f_889_ + f_902_ * f_890_) * f_903_;
                float v1 = (f_900_ * f_891_ + f_901_ * f_892_ + f_902_ * f_893_) * f_903_;
                float v2 = (f_900_ * f_894_ + f_901_ * f_895_ + f_902_ * f_896_) * f_903_;

                int idx = i * 6;
                faceTextureUCoordinates[idx] = u0;
                faceTextureUCoordinates[idx + 1] = v0;
                faceTextureUCoordinates[idx + 2] = u1;
                faceTextureUCoordinates[idx + 3] = v1;
                faceTextureUCoordinates[idx + 4] = u2;
                faceTextureUCoordinates[idx + 5] = v2;
            }
        }

        faceTextureUVCoordinates = faceTextureUCoordinates;
    }


}