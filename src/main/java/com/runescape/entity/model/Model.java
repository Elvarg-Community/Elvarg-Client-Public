package com.runescape.entity.model;

import com.runescape.Client;
import com.runescape.cache.anim.Frame;
import com.runescape.cache.anim.FrameBase;
import com.runescape.draw.Rasterizer2D;
import com.runescape.draw.Rasterizer3D;
import com.runescape.entity.Renderable;
import com.runescape.io.Buffer;
import net.runelite.api.Perspective;
import net.runelite.api.model.Jarvis;
import net.runelite.api.model.Triangle;
import net.runelite.api.model.Vertex;
import net.runelite.rs.api.RSFrames;
import net.runelite.rs.api.RSModel;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class Model extends Renderable implements RSModel {

    private static final int method4626(int var0, int var1, int var2, int var3) {
        return var0 * var2 + var3 * var1 >> 16;
    }

    private static final int method4663(int var0, int var1, int var2, int var3) {
        return var2 * var1 - var3 * var0 >> 16;
    }

    private void calculateBoundingBox(int var1) {
        if (this.xMidOffset == -1) {
            int var2 = 0;
            int var3 = 0;
            int var4 = 0;
            int var5 = 0;
            int var6 = 0;
            int var7 = 0;
            int var8 = COSINE[var1];
            int var9 = SINE[var1];

            for (int var10 = 0; var10 < this.getVerticesCount(); ++var10) {
                int var11 = method4626(this.getVerticesX()[var10], this.getVerticesZ()[var10], var8, var9);
                int var12 = this.getVerticesY()[var10];
                int var13 = method4663(this.getVerticesX()[var10], this.getVerticesZ()[var10], var8, var9);
                if (var11 < var2) {
                    var2 = var11;
                }

                if (var11 > var5) {
                    var5 = var11;
                }

                if (var12 < var3) {
                    var3 = var12;
                }

                if (var12 > var6) {
                    var6 = var12;
                }

                if (var13 < var4) {
                    var4 = var13;
                }

                if (var13 > var7) {
                    var7 = var13;
                }
            }

            this.xMid = (var5 + var2) / 2;
            this.yMid = (var6 + var3) / 2;
            this.zMid = (var7 + var4) / 2;
            this.xMidOffset = (var5 - var2 + 1) / 2;
            this.yMidOffset = (var6 - var3 + 1) / 2;
            this.zMidOffset = (var7 - var4 + 1) / 2;
            if (this.xMidOffset < 32) {
                this.xMidOffset = 32;
            }

            if (this.zMidOffset < 32) {
                this.zMidOffset = 32;
            }

            if (this.fits_on_single_square) {
                this.xMidOffset += 8;
                this.zMidOffset += 8;
            }
        }
    }

    private int xMid;
    private int yMid;
    private int zMid;
    private int xMidOffset;
    private int yMidOffset;
    private int zMidOffset;

    public static void clear() {
        aClass21Array1661 = null;
        hasAnEdgeToRestrict = null;
        outOfReach = null;
        projected_vertex_y = null;
        anIntArray1668 = null;
        camera_vertex_y = null;
        camera_vertex_x = null;
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

    public void decode_old(byte[] data, int model_id) {
        boolean has_face_type = false;
        boolean has_texture_type = false;
        Buffer first = new Buffer(data);
        Buffer second = new Buffer(data);
        Buffer third = new Buffer(data);
        Buffer fourth = new Buffer(data);
        Buffer fifth = new Buffer(data);
        first.currentPosition = data.length - 18;
        numVertices = first.readUShort();
        trianglesCount = first.readUShort();
        numberOfTexturesFaces = first.readUnsignedByte();
        int model_render_type_opcode = first.readUnsignedByte();
        int model_render_priority_opcode = first.readUnsignedByte();
        int model_alpha_opcode = first.readUnsignedByte();
        int model_muscle_opcode = first.readUnsignedByte();
        int model_bones_opcode = first.readUnsignedByte();
        int model_vertex_x = first.readUShort();
        int model_vertex_y = first.readUShort();
        int model_vertex_z = first.readUShort();
        int model_vertex_points = first.readUShort();
        int pos = 0;

        int vertex_flag_offset = pos;
        pos += numVertices;

        int model_face_compress_type_offset = pos;
        pos += trianglesCount;

        int model_face_pri_offset = pos;
        if (model_render_priority_opcode == 255)
            pos += trianglesCount;

        int model_muscle_offset = pos;
        if (model_muscle_opcode == 1)
            pos += trianglesCount;

        int model_render_type_offset = pos;
        if (model_render_type_opcode == 1)
            pos += trianglesCount;

        int model_bones_offset = pos;
        if (model_bones_opcode == 1)
            pos += numVertices;

        int model_alpha_offset = pos;
        if (model_alpha_opcode == 1)
            pos += trianglesCount;

        int model_points_offset = pos;
        pos += model_vertex_points;

        int model_color_offset = pos;
        pos += trianglesCount * 2;

        int model_simple_texture_offset = pos;
        pos += numberOfTexturesFaces * 6;

        int model_vertex_x_offset = pos;
        pos += model_vertex_x;

        int model_vertex_y_offset = pos;
        pos += model_vertex_y;

        int model_vertex_z_offset = pos;
        pos += model_vertex_z;

        vertexX = new int[numVertices];
        vertexY = new int[numVertices];
        vertexZ = new int[numVertices];
        facePointA = new int[trianglesCount];
        facePointB = new int[trianglesCount];
        facePointC = new int[trianglesCount];
        if (numberOfTexturesFaces > 0) {
            textureTypes = new byte[numberOfTexturesFaces];
            textures_face_a = new short[numberOfTexturesFaces];
            textures_face_b = new short[numberOfTexturesFaces];
            textures_face_c = new short[numberOfTexturesFaces];
        }

        if (model_bones_opcode == 1)
            vertexVSkin = new int[numVertices];

        if (model_render_type_opcode == 1) {
            faceDrawType = new int[trianglesCount];
            textures = new byte[trianglesCount];
            materials = new short[trianglesCount];
        }

        if (model_render_priority_opcode == 255)
            face_render_priorities = new byte[trianglesCount];
        else
            face_priority = (byte) model_render_priority_opcode;

        if (model_alpha_opcode == 1)
            face_alpha = new byte[trianglesCount];

        if (model_muscle_opcode == 1)
            triangleTSkin = new int[trianglesCount];

        triangleColours = new short[trianglesCount];
        first.currentPosition = vertex_flag_offset;
        second.currentPosition = model_vertex_x_offset;
        third.currentPosition = model_vertex_y_offset;
        fourth.currentPosition = model_vertex_z_offset;
        fifth.currentPosition = model_bones_offset;
        int start_x = 0;
        int start_y = 0;
        int start_z = 0;
        for (int point = 0; point < numVertices; point++) {
            int position_mask = first.readUnsignedByte();
            int x = 0;
            if ((position_mask & 0x1) != 0)
                x = second.readSmart();
            int y = 0;
            if ((position_mask & 0x2) != 0)
                y = third.readSmart();
            int z = 0;
            if ((position_mask & 0x4) != 0)
                z = fourth.readSmart();

            vertexX[point] = start_x + x;
            vertexY[point] = start_y + y;
            vertexZ[point] = start_z + z;
            start_x = vertexX[point];
            start_y = vertexY[point];
            start_z = vertexZ[point];
            if (model_bones_opcode == 1)
                vertexVSkin[point] = fifth.readUnsignedByte();

        }
        first.currentPosition = model_color_offset;
        second.currentPosition = model_render_type_offset;
        third.currentPosition = model_face_pri_offset;
        fourth.currentPosition = model_alpha_offset;
        fifth.currentPosition = model_muscle_offset;
        for (int face = 0; face < trianglesCount; face++) {
            triangleColours[face] = (short) first.readUShort();
            if (model_render_type_opcode == 1) {
                int render_mask = second.readUnsignedByte();
                if ((render_mask & 0x1) == 1) {
                    faceDrawType[face] = 1;
                    has_face_type = true;
                } else {
                    faceDrawType[face] = 0;
                }

                if ((render_mask & 0x2) != 0) {
                    textures[face] = (byte) (render_mask >> 2);
                    materials[face] = triangleColours[face];
                    triangleColours[face] = 127;
                    if (materials[face] != -1)
                        has_texture_type = true;


                } else {
                    textures[face] = -1;
                    materials[face] =  -1;
                }
            }
            if (model_render_priority_opcode == 255)
                face_render_priorities[face] = third.readSignedByte();

            if (model_alpha_opcode == 1) {
                face_alpha[face] = fourth.readSignedByte();


            }
            if (model_muscle_opcode == 1)
                triangleTSkin[face] = fifth.readUnsignedByte();

        }
        first.currentPosition = model_points_offset;
        second.currentPosition = model_face_compress_type_offset;
        int coordinate_a = 0;
        int coordinate_b = 0;
        int coordinate_c = 0;
        int offset = 0;
        int coordinate;
        for (int face = 0; face < trianglesCount; face++) {
            int opcode = second.readUnsignedByte();
            if (opcode == 1) {
                coordinate_a = (first.readSmart() + offset);
                offset = coordinate_a;
                coordinate_b = (first.readSmart() + offset);
                offset = coordinate_b;
                coordinate_c = (first.readSmart() + offset);
                offset = coordinate_c;
                facePointA[face] = coordinate_a;
                facePointB[face] = coordinate_b;
                facePointC[face] = coordinate_c;
            }
            if (opcode == 2) {
                coordinate_b = coordinate_c;
                coordinate_c = (first.readSmart() + offset);
                offset = coordinate_c;
                facePointA[face] = coordinate_a;
                facePointB[face] = coordinate_b;
                facePointC[face] = coordinate_c;
            }
            if (opcode == 3) {
                coordinate_a = coordinate_c;
                coordinate_c = (first.readSmart() + offset);
                offset = coordinate_c;
                facePointA[face] = coordinate_a;
                facePointB[face] = coordinate_b;
                facePointC[face] = coordinate_c;
            }
            if (opcode == 4) {
                coordinate = coordinate_a;
                coordinate_a = coordinate_b;
                coordinate_b = coordinate;
                coordinate_c = (first.readSmart() + offset);
                offset = coordinate_c;
                facePointA[face] = coordinate_a;
                facePointB[face] = coordinate_b;
                facePointC[face] = coordinate_c;
            }
        }
        first.currentPosition = model_simple_texture_offset;
        for (int face = 0; face < numberOfTexturesFaces; face++) {
            textureTypes[face] = 0;
            textures_face_a[face] = (short) first.readUShort();
            textures_face_b[face] = (short) first.readUShort();
            textures_face_c[face] = (short) first.readUShort();
        }
        if (textures != null) {
            boolean textured = false;
            for (int face = 0; face < trianglesCount; face++) {
                coordinate = textures[face] & 0xff;
                if (coordinate != 255) {
                    if (((textures_face_a[coordinate] & 0xffff) == facePointA[face]) && ((textures_face_b[coordinate] & 0xffff)  == facePointB[face]) && ((textures_face_c[coordinate] & 0xffff) == facePointC[face])) {
                        textures[face] = -1;
                    } else {
                        textured = true;
                    }
                }
            }
            if (!textured)
                textures = null;
        }
        if (!has_texture_type)
            materials = null;

        if (!has_face_type)
            faceDrawType = null;


		/*if(model_id == 9638) {
			this.aClass158Array3788 = new ModelParticleEmitter[1];
			for (int i_198_ = 0; i_198_ < 1; i_198_++) {
				final int particleId = 0;
				final int i_200_ = 1;
				this.aClass158Array3788[i_198_] = new ModelParticleEmitter(particleId, this.edge_a[i_200_], this.edge_b[i_200_], this.edge_c[i_200_]);
			}
		}*/

    }


    //dunno what these are
    public int animayaGroups[][];
    public int animayaScales[][];

    public void decode_new(byte data[], int model_id) {
        Buffer first = new Buffer(data);
        Buffer second = new Buffer(data);
        Buffer third = new Buffer(data);
        Buffer fourth = new Buffer(data);
        Buffer fifth = new Buffer(data);
        Buffer sixth = new Buffer(data);
        Buffer seventh = new Buffer(data);

        first.currentPosition = data.length - 23;
        numVertices = first.readUShort();
        trianglesCount = first.readUShort();
        numberOfTexturesFaces = first.readUnsignedByte();

        ModelHeader def = aClass21Array1661[model_id] = new ModelHeader();
        def.data = data;
        def.vertices = numVertices;
        def.faces = trianglesCount;
        def.texture_faces = numberOfTexturesFaces;

        int model_render_type_opcode = first.readUnsignedByte();//texture flag 00 false, 01+ true
        int model_priority_opcode = first.readUnsignedByte();
        int model_alpha_opcode = first.readUnsignedByte();
        int model_muscle_opcode = first.readUnsignedByte();
        int model_texture_opcode = first.readUnsignedByte();
        int model_bones_opcode = first.readUnsignedByte();
        int model_vertex_x = first.readUShort();
        int model_vertex_y = first.readUShort();
        int model_vertex_z = first.readUShort();
        int model_vertex_points = first.readUShort();
        int model_texture_indices = first.readUShort();
        int texture_id_simple = 0;
        int texture_id_complex = 0;
        int texture_id_cube = 0;
        int face;
        if (numberOfTexturesFaces > 0) {
            textureTypes = new byte[numberOfTexturesFaces];
            first.currentPosition = 0;
            for (face = 0; face < numberOfTexturesFaces; face++) {
                byte opcode = textureTypes[face] = first.readSignedByte();
                if (opcode == 0) {
                    texture_id_simple++;
                }
                if (opcode >= 1 && opcode <= 3) {
                    texture_id_complex++;
                }
                if (opcode == 2) {
                    texture_id_cube++;
                }

            }
        }
        int pos = numberOfTexturesFaces;

        int model_vertex_offset = pos;
        pos += numVertices;

        int model_render_type_offset = pos;
        if (model_render_type_opcode == 1)
            pos += trianglesCount;

        int model_face_offset = pos;
        pos += trianglesCount;

        int model_face_priorities_offset = pos;
        if (model_priority_opcode == 255)
            pos += trianglesCount;

        int model_muscle_offset = pos;
        if (model_muscle_opcode == 1)
            pos += trianglesCount;

        int model_bones_offset = pos;
        if (model_bones_opcode == 1)
            pos += numVertices;

        int model_alpha_offset = pos;
        if (model_alpha_opcode == 1)
            pos += trianglesCount;

        int model_points_offset = pos;
        pos += model_vertex_points;

        int model_texture_id = pos;
        if (model_texture_opcode == 1)
            pos += trianglesCount * 2;

        int model_texture_coordinate_offset = pos;
        pos += model_texture_indices;

        int model_color_offset = pos;
        pos += trianglesCount * 2;

        int model_vertex_x_offset = pos;
        pos += model_vertex_x;

        int model_vertex_y_offset = pos;
        pos += model_vertex_y;

        int model_vertex_z_offset = pos;
        pos += model_vertex_z;

        int model_simple_texture_offset = pos;
        pos += texture_id_simple * 6;

        int model_complex_texture_offset = pos;
        pos += texture_id_complex * 6;

        int model_texture_scale_offset = pos;
        pos += texture_id_complex * 6;

        int model_texture_rotation_offset = pos;
        pos += texture_id_complex * 2;

        int model_texture_direction_offset = pos;
        pos += texture_id_complex;

        int model_texture_translate_offset = pos;
        pos += texture_id_complex * 2 + texture_id_cube * 2;

        vertexX = new int[numVertices];
        vertexY = new int[numVertices];
        vertexZ = new int[numVertices];
        facePointA = new int[trianglesCount];
        facePointB = new int[trianglesCount];
        facePointC = new int[trianglesCount];
        if (model_bones_opcode == 1)
            vertexVSkin = new int[numVertices];

        if (model_render_type_opcode == 1)
            faceDrawType = new int[trianglesCount];

        if (model_priority_opcode == 255)
            face_render_priorities = new byte[trianglesCount];
        else
            face_priority = (byte) model_priority_opcode;

        if (model_alpha_opcode == 1)
            face_alpha = new byte[trianglesCount];

        if (model_muscle_opcode == 1)
            triangleTSkin = new int[trianglesCount];

        if (model_texture_opcode == 1)
            materials = new short[trianglesCount];

        if (model_texture_opcode == 1 && numberOfTexturesFaces > 0)
            textures = new byte[trianglesCount];

        triangleColours = new short[trianglesCount];
        if (numberOfTexturesFaces > 0) {
            textures_face_a = new short[numberOfTexturesFaces];
            textures_face_b = new short[numberOfTexturesFaces];
            textures_face_c = new short[numberOfTexturesFaces];
        }
        first.currentPosition = model_vertex_offset;
        second.currentPosition = model_vertex_x_offset;
        third.currentPosition = model_vertex_y_offset;
        fourth.currentPosition = model_vertex_z_offset;
        fifth.currentPosition = model_bones_offset;
        int start_x = 0;
        int start_y = 0;
        int start_z = 0;
        for (int point = 0; point < numVertices; point++) {
            int position_mask = first.readUnsignedByte();
            int x = 0;
            if ((position_mask & 1) != 0) {
                x = second.readSmart();
            }
            int y = 0;
            if ((position_mask & 2) != 0) {
                y = third.readSmart();
            }
            int z = 0;
            if ((position_mask & 4) != 0) {
                z = fourth.readSmart();
            }
            vertexX[point] = start_x + x;
            vertexY[point] = start_y + y;
            vertexZ[point] = start_z + z;
            start_x = vertexX[point];
            start_y = vertexY[point];
            start_z = vertexZ[point];
            if (vertexVSkin != null)
                vertexVSkin[point] = fifth.readUnsignedByte();

        }
        first.currentPosition = model_color_offset;
        second.currentPosition = model_render_type_offset;
        third.currentPosition = model_face_priorities_offset;
        fourth.currentPosition = model_alpha_offset;
        fifth.currentPosition = model_muscle_offset;
        sixth.currentPosition = model_texture_id;
        seventh.currentPosition = model_texture_coordinate_offset;
        for (face = 0; face < trianglesCount; face++) {
            triangleColours[face] = (short) (first.readUShort() & 0xFFFF);
            if (model_render_type_opcode == 1) {
                faceDrawType[face] = second.readSignedByte();
            }
            if (model_priority_opcode == 255) {
                face_render_priorities[face] = third.readSignedByte();
            }
            if (model_alpha_opcode == 1) {
                face_alpha[face] = fourth.readSignedByte();


            }
            if (model_muscle_opcode == 1)
                triangleTSkin[face] = fifth.readUnsignedByte();

            if (model_texture_opcode == 1) {
                materials[face] = (short) (sixth.readUShort() - 1);
                if(materials[face] >= 0) {
                    if(faceDrawType != null) {
                        if(faceDrawType[face] < 2
                                && triangleColours[face] != 127
                                && triangleColours[face] != -27075
                                && triangleColours[face] != 8128
                                && triangleColours[face] != 7510) {
                            materials[face] = -1;
                        }
                    }
                }
                if(materials[face] != -1)
                    triangleColours[face] = 127;

            }
            if (textures != null && materials[face] != -1) {
                textures[face] = (byte) (seventh.readUnsignedByte() - 1);
            }
        }
        first.currentPosition = model_points_offset;
        second.currentPosition = model_face_offset;
        int coordinate_a = 0;
        int coordinate_b = 0;
        int coordinate_c = 0;
        int last_coordinate = 0;
        for (face = 0; face < trianglesCount; face++) {
            int opcode = second.readUnsignedByte();
            if (opcode == 1) {
                coordinate_a = first.readSmart() + last_coordinate;
                last_coordinate = coordinate_a;
                coordinate_b = first.readSmart() + last_coordinate;
                last_coordinate = coordinate_b;
                coordinate_c = first.readSmart() + last_coordinate;
                last_coordinate = coordinate_c;
                facePointA[face] = coordinate_a;
                facePointB[face] = coordinate_b;
                facePointC[face] = coordinate_c;
            }
            if (opcode == 2) {
                coordinate_b = coordinate_c;
                coordinate_c = first.readSmart() + last_coordinate;
                last_coordinate = coordinate_c;
                facePointA[face] = coordinate_a;
                facePointB[face] = coordinate_b;
                facePointC[face] = coordinate_c;
            }
            if (opcode == 3) {
                coordinate_a = coordinate_c;
                coordinate_c = first.readSmart() + last_coordinate;
                last_coordinate = coordinate_c;
                facePointA[face] = coordinate_a;
                facePointB[face] = coordinate_b;
                facePointC[face] = coordinate_c;
            }
            if (opcode == 4) {
                int l14 = coordinate_a;
                coordinate_a = coordinate_b;
                coordinate_b = l14;
                coordinate_c = first.readSmart() + last_coordinate;
                last_coordinate = coordinate_c;
                facePointA[face] = coordinate_a;
                facePointB[face] = coordinate_b;
                facePointC[face] = coordinate_c;
            }
        }
        first.currentPosition = model_simple_texture_offset;
        second.currentPosition = model_complex_texture_offset;
        third.currentPosition = model_texture_scale_offset;
        fourth.currentPosition = model_texture_rotation_offset;
        fifth.currentPosition = model_texture_direction_offset;
        sixth.currentPosition = model_texture_translate_offset;
        for (face = 0; face < numberOfTexturesFaces; face++) {
            int opcode = textureTypes[face] & 0xff;
            if (opcode == 0) {
                textures_face_a[face] = (short) first.readUShort();
                textures_face_b[face] = (short) first.readUShort();
                textures_face_c[face] = (short) first.readUShort();
            }
            if (opcode == 1) {
                textures_face_a[face] = (short) second.readUShort();
                textures_face_b[face] = (short) second.readUShort();
                textures_face_c[face] = (short) second.readUShort();
            }
            if (opcode == 2) {
                textures_face_a[face] = (short) second.readUShort();
                textures_face_b[face] = (short) second.readUShort();
                textures_face_c[face] = (short) second.readUShort();
            }
            if (opcode == 3) {
                textures_face_a[face] = (short) second.readUShort();
                textures_face_b[face] = (short) second.readUShort();
                textures_face_c[face] = (short) second.readUShort();
            }
        }
        first.currentPosition = pos;
        face = first.readUnsignedByte();
        if(face != 0) {
            first.readUShort();
            first.readUShort();
            first.readUShort();
            first.readInt();
        }
    }
    private Model(int modelId) {
        byte[] is = aClass21Array1661[modelId].data;
        if (is[is.length - 1] == -3 && is[is.length - 2] == -1) {
            ModelLoader.decodeType3(this, is);
        } else if (is[is.length - 1] == -2 && is[is.length - 2] == -1) {
            ModelLoader.decodeType2(this, is);
        } else if (is[is.length - 1] == -1 && is[is.length - 2] == -1) {
            ModelLoader.decodeType1(this, is);
        } else {
            ModelLoader.decodeOldFormat(this, is);
        }

    }

    public static void method460(byte data[], int model_id) {
        try {
            if (data == null) {
                ModelHeader head = aClass21Array1661[model_id] = new ModelHeader();
                head.vertices = 0;
                head.faces = 0;
                head.texture_faces = 0;
                return;
            }
            Buffer buffer = new Buffer(data);
            buffer.currentPosition = data.length - 18;
            ModelHeader head = aClass21Array1661[model_id] = new ModelHeader();
            head.data = data;
            head.vertices = buffer.readUShort();
            head.faces = buffer.readUShort();
            head.texture_faces = buffer.readUnsignedByte();
            int model_render_type_opcode = buffer.readUnsignedByte();
            int model_priority_opcode = buffer.readUnsignedByte();
            int model_alpha_opcode = buffer.readUnsignedByte();
            int model_muscle_opcode = buffer.readUnsignedByte();
            int model_bones_opcode = buffer.readUnsignedByte();
            int model_vertex_x = buffer.readUShort();
            int model_vertex_y = buffer.readUShort();
            int model_vertex_z = buffer.readUShort();
            int model_vertex_points = buffer.readUShort();
            int pos = 0;
            head.vertex_offset = pos;
            pos += head.vertices;

            head.face_offset = pos;
            pos += head.faces;

            head.face_pri_offset = pos;
            if (model_priority_opcode == 255)
                pos += head.faces;
            else
                head.face_pri_offset = -model_priority_opcode - 1;

            head.muscle_offset = pos;
            if (model_muscle_opcode == 1)
                pos += head.faces;
            else
                head.muscle_offset = -1;

            head.render_type_offset = pos;
            if (model_render_type_opcode == 1)
                pos += head.faces;
            else
                head.render_type_offset = -1;

            head.bones_offset = pos;
            if (model_bones_opcode == 1)
                pos += head.vertices;
            else
                head.bones_offset = -1;

            head.alpha_offset = pos;
            if (model_alpha_opcode == 1)
                pos += head.faces;
            else
                head.alpha_offset = -1;

            head.points_offset = pos;
            pos += model_vertex_points;

            head.color_id = pos;
            pos += head.faces * 2;

            head.texture_id = pos;
            pos += head.texture_faces * 6;

            head.vertex_x_offset = pos;
            pos += model_vertex_x;

            head.vertex_y_offset = pos;
            pos += model_vertex_y;

            head.vertex_z_offset = pos;
            pos += model_vertex_z;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void init() {
        aClass21Array1661 = new ModelHeader[90000];

    }

    public static void method461(int file) {
        aClass21Array1661[file] = null;
    }

    public static Model getModel(int file) {
        if (aClass21Array1661 == null)
            return null;

        ModelHeader mdl = aClass21Array1661[file];
        if (mdl == null) {
            Client.instance.resourceProvider.provide(0,file);
            return null;
        } else {
            return new Model(file);
        }
    }

    public static boolean isCached(int file) {
        if (aClass21Array1661 == null)
            return false;

        ModelHeader mdl = aClass21Array1661[file];
        if (mdl == null) {
            Client.instance.resourceProvider.provide(0,file);
            return false;
        } else {
            return true;
        }
    }

    public Model(int length, Model model_segments[]) {
        this(length, model_segments, false);
    }

    public Model(int length, Model model_segments[], boolean preset) {
        try {
            fits_on_single_square = false;
            this.xMidOffset = -1;
            this.yMidOffset = -1;
            this.zMidOffset = -1;
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
            if(color_flag)
                triangleColours = new short[trianglesCount];

            if (render_type_flag)
                faceDrawType = new int[trianglesCount];

            if (priority_flag)
                face_render_priorities = new byte[trianglesCount];

            if (alpha_flag)
                face_alpha = new byte[trianglesCount];

            if (muscle_skin_flag)
                triangleTSkin = new int[trianglesCount];

            if(texture_flag)
                materials = new short[trianglesCount];

            if (coordinate_flag)
                textures = new byte[trianglesCount];

            if(numberOfTexturesFaces > 0) {
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
                        if(render_type_flag && build.faceDrawType != null)
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

                        if(texture_flag) {
                            if(build.materials != null)
                                materials[trianglesCount] = build.materials[face];
                            else
                                materials[trianglesCount] = -1;
                        }
                        if(coordinate_flag) {
                            if(build.textures != null && build.textures[face] != -1) {
                                textures[trianglesCount] = (byte) (build.textures[face] + numberOfTexturesFaces);
                            } else {
                                textures[trianglesCount] = -1;
                            }
                        }

                        if(color_flag && build.triangleColours != null)
                            triangleColours[trianglesCount] = build.triangleColours[face];

                        facePointA[trianglesCount] = get_shared_vertices(build, build.facePointA[face]);
                        facePointB[trianglesCount] = get_shared_vertices(build, build.facePointB[face]);
                        facePointC[trianglesCount] = get_shared_vertices(build, build.facePointC[face]);
                        trianglesCount++;
                    }
                    for (int texture_edge = 0; texture_edge < build.numberOfTexturesFaces; texture_edge++) {
                        byte opcode = textureTypes[numberOfTexturesFaces] = build.textureTypes[texture_edge];
                        if(opcode == 0) {
                            textures_face_a[numberOfTexturesFaces] = (short) get_shared_vertices(build, build.textures_face_a[texture_edge]);
                            textures_face_b[numberOfTexturesFaces] = (short) get_shared_vertices(build, build.textures_face_b[texture_edge]);
                            textures_face_c[numberOfTexturesFaces] = (short) get_shared_vertices(build, build.textures_face_c[texture_edge]);
                        }
                        if(opcode >= 1 && opcode <= 3) {
                            textures_face_a[numberOfTexturesFaces] = build.textures_face_a[texture_edge];
                            textures_face_b[numberOfTexturesFaces] = build.textures_face_b[texture_edge];
                            textures_face_c[numberOfTexturesFaces] = build.textures_face_c[texture_edge];
                        }
                        if(opcode == 2) {

                        }
                        numberOfTexturesFaces++;
                    }
                    if(!preset) //for models that don't have preset textured_faces
                        numberOfTexturesFaces++;

                }
            }
            if (getFaceTextures() != null) {
                int count = getFaceCount();
                float[] uv = new float[count * 6];
                int idx = 0;

                for (int segment_index = 0; segment_index < length; ++segment_index)
                {
                    build = model_segments[segment_index];
                    if (build != null)
                    {
                        float[] modelUV = build.getFaceTextureUVCoordinates();

                        if (modelUV != null)
                        {
                            System.arraycopy(modelUV, 0, uv, idx, build.getFaceCount() * 6);
                        }

                        idx += build.getFaceCount() * 6;
                    }
                }

                setFaceTextureUVCoordinates(uv);
            }
            vertexNormals();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    //Players - graphics in particular
    public Model(Model model_segments[]) {
        int length = 2;
        fits_on_single_square = false;
        anInt1620++;
        boolean render_type_flag = false;
        boolean priority_flag = false;
        boolean alpha_flag = false;
        boolean color_flag = false;
        boolean texture_flag = false;
        boolean coordinate_flag = false;
        numVertices = 0;
        trianglesCount = 0;
        numberOfTexturesFaces = 0;
        //int i_668_ = 0;
        //int i_669_ = 0;
        face_priority = -1;
        Model build;
        for (int segment_index = 0; segment_index < length; segment_index++) {
            build = model_segments[segment_index];
            if (build != null) {
                numVertices += build.numVertices;
                trianglesCount += build.trianglesCount;
                numberOfTexturesFaces += build.numberOfTexturesFaces;
                render_type_flag |= faceDrawType != null;
                if (build.face_render_priorities != null) {
                    priority_flag = true;
                } else {
                    if (face_priority == -1)
                        face_priority = build.face_priority;

                    if (face_priority != build.face_priority)
                        priority_flag = true;
                }
                alpha_flag |= build.face_alpha != null;
                color_flag |= build.triangleColours != null;
                texture_flag |= build.materials != null;
                coordinate_flag |= build.textures != null;
				/*if(build.aClass158Array3788 != null)
					i_668_ += build.aClass158Array3788.length;

				if(build.aClass169Array3776 != null)
					i_669_ += build.aClass169Array3776.length;*/
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

        if (render_type_flag)
            faceDrawType = new int[trianglesCount];

        if (priority_flag)
            face_render_priorities = new byte[trianglesCount];

        if (alpha_flag)
            face_alpha = new byte[trianglesCount];

        if (texture_flag)
            materials = new short[trianglesCount];

        if (coordinate_flag)
            textures = new byte[trianglesCount];

        if(numberOfTexturesFaces > 0) {
            textureTypes = new byte[numberOfTexturesFaces];
            textures_face_a = new short[numberOfTexturesFaces];
            textures_face_b = new short[numberOfTexturesFaces];
            textures_face_c = new short[numberOfTexturesFaces];
        }
        //if(i_668_ > 0) {
        //this.aClass158Array3788 = new ModelParticleEmitter[i_668_];
        //}
        //if(i_669_ > 0) {
        //this.aClass169Array3776 = new ModelParticleMagnet[i_669_];
        //}

        if (color_flag)
            triangleColours = new short[trianglesCount];

        numVertices = 0;
        trianglesCount = 0;
        numberOfTexturesFaces = 0;
        for (int segment_index = 0; segment_index < length; segment_index++) {
            build = model_segments[segment_index];
            if (build != null) {
                int vertex = numVertices;
                for (int point = 0; point < build.numVertices; point++) {
                    vertexX[numVertices] = build.vertexX[point];
                    vertexY[numVertices] = build.vertexY[point];
                    vertexZ[numVertices] = build.vertexZ[point];
                    numVertices++;
                }
                for (int face = 0; face < build.trianglesCount; face++) {
                    facePointA[trianglesCount] = build.facePointA[face] + vertex;
                    facePointB[trianglesCount] = build.facePointB[face] + vertex;
                    facePointC[trianglesCount] = build.facePointC[face] + vertex;
                    faceHslA[trianglesCount] = build.faceHslA[face];
                    faceHslB[trianglesCount] = build.faceHslB[face];
                    faceHslC[trianglesCount] = build.faceHslC[face];
					/*if (type_flag)
						if (build.render_type == null) {
							render_type[faces] = 0;
						} else {
							int type = build.render_type[face];
							if ((type & 2) == 2) //texture
								type += texture_faces << 2;

							render_type[faces] = type;
						}*/

                    if(render_type_flag && build.faceDrawType != null) {
                        faceDrawType[trianglesCount] = build.faceDrawType[face];
                    }

                    if (alpha_flag && build.face_alpha != null) {
                        face_alpha[trianglesCount] = build.face_alpha[face];
                    }

                    if (priority_flag)
                        if (build.face_render_priorities == null)
                            face_render_priorities[trianglesCount] = build.face_priority;
                        else
                            face_render_priorities[trianglesCount] = build.face_render_priorities[face];

                    if (color_flag && build.triangleColours != null)
                        triangleColours[trianglesCount] = build.triangleColours[face];

                    if(texture_flag) {
                        if(build.materials != null) {
                            materials[trianglesCount] = build.materials[face];
                        } else
                            materials[trianglesCount] = -1;
                    }
                    if(coordinate_flag) {
                        if(build.textures != null && build.textures[face] != -1) {
                            textures[trianglesCount] = (byte) (build.textures[face] + numberOfTexturesFaces);

                        } else
                            textures[trianglesCount] = -1;

                    }
					/*if (build.aClass158Array3788 != null) {
						for (int i_675_ = 0; i_675_ < build.aClass158Array3788.length; i_675_++) {
							this.aClass158Array3788[i_668_] = new ModelParticleEmitter(
								build.aClass158Array3788[i_675_].emitterType,
									build.aClass158Array3788[i_675_].anInt1485 + vertex,
										build.aClass158Array3788[i_675_].anInt1484 + vertex,
											build.aClass158Array3788[i_675_].anInt1476 + vertex);
							i_668_++;
						}
					}
					if (build.aClass169Array3776 != null) {
						for (int i_676_ = 0; i_676_ < build.aClass169Array3776.length; i_676_++) {
							this.aClass169Array3776[i_669_] = new ModelParticleMagnet(build.aClass169Array3776[i_676_].magnetType, build.aClass169Array3776[i_676_].vertexId + vertex);
							i_669_++;
						}
					}*/
                    trianglesCount++;
                }

                for (int texture_edge = 0; texture_edge < build.numberOfTexturesFaces; texture_edge++) {
                    textures_face_a[numberOfTexturesFaces] = (short) (build.textures_face_a[texture_edge] + vertex);
                    textures_face_b[numberOfTexturesFaces] = (short) (build.textures_face_b[texture_edge] + vertex);
                    textures_face_c[numberOfTexturesFaces] = (short) (build.textures_face_c[texture_edge] + vertex);
                    numberOfTexturesFaces++;
                }
                numberOfTexturesFaces += build.numberOfTexturesFaces;//texture_faces++;
            }
        }
        calc_diagonals();
    }

    public Model(boolean color_flag, boolean alpha_flag, boolean animated, Model model) {
        this(color_flag, alpha_flag, animated, false, model);
    }

    public Model(boolean color_flag, boolean alpha_flag, boolean animated, boolean texture_flag, Model model) {
        //aBoolean1618 = true;
        fits_on_single_square = false;
        anInt1620++;
        this.numVertices = 0;
        this.trianglesCount = 0;
        this.face_priority = 0;

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
            System.arraycopy(model.triangleColours, 0, triangleColours, 0, trianglesCount);

        }
        if(texture_flag) {
            materials = model.materials;
        } else {
            if(model.materials != null) {
                materials = new short[trianglesCount];
                System.arraycopy(model.materials, 0, materials, 0, trianglesCount);
            }
        }

        if (alpha_flag) {
            face_alpha = model.face_alpha;
        } else {
            face_alpha = new byte[trianglesCount];
            if (model.face_alpha == null) {
                for (int l = 0; l < trianglesCount; l++)
                    face_alpha[l] = 0;

            } else {
                System.arraycopy(model.face_alpha, 0, face_alpha, 0, trianglesCount);
            }
        }

        facePointA = model.facePointA;
        facePointB = model.facePointB;
        facePointC = model.facePointC;
        faceDrawType = model.faceDrawType;
        face_render_priorities = model.face_render_priorities;
        textures = model.textures;
        face_priority = model.face_priority;
        textureTypes = model.textureTypes;
        textures_face_a = model.textures_face_a;
        textures_face_b = model.textures_face_b;
        textures_face_c = model.textures_face_c;
        vertexVSkin = model.vertexVSkin;
        triangleTSkin = model.triangleTSkin;

        faceGroups = model.faceGroups;
        vertexGroups = model.vertexGroups;
        gouraud_vertex = model.gouraud_vertex;
    }

    public Model non_shaded(Model model) {
        model.numVertices = 0;
        model.trianglesCount = 0;
        model.face_priority = 0;
        model.numberOfTexturesFaces = 0;
        if (this.faceDrawType != null) {
            model.faceDrawType = new int[this.trianglesCount];
            for(int face = 0; face < this.trianglesCount; face++) {
                model.faceDrawType[face] = this.faceDrawType[face];
            }
        }
        model.faceHslA = this.faceHslA;
        model.faceHslB = this.faceHslB;
        model.faceHslC = this.faceHslC;
        model.vertexX = this.vertexX;
        model.vertexY = this.vertexY;
        model.vertexZ = this.vertexZ;
        model.facePointA = this.facePointA;
        model.facePointB = this.facePointB;
        model.facePointC = this.facePointC;
        model.face_render_priorities = this.face_render_priorities;
        model.face_alpha = this.face_alpha;
        model.textures = this.textures;
        model.triangleColours = this.triangleColours;
        model.materials = this.materials;
        model.face_priority = this.face_priority;
        model.textureTypes = this.textureTypes;
        model.textures_face_a = this.textures_face_a;
        model.textures_face_b = this.textures_face_b;
        model.textures_face_c = this.textures_face_c;
        super.modelBaseY = this.modelBaseY;
        model.maxVertexDistanceXZPlane = this.maxVertexDistanceXZPlane;
        model.diagonal_3D = this.diagonal_3D;
        model.scene_depth = this.scene_depth;
        model.min_x = this.min_x;
        model.max_z = this.max_z;
        model.min_z = this.min_z;
        model.max_x = this.max_x;
        return model;
    }

    public Model(boolean adjust_elevation, boolean gouraud_shading, Model model, int id) {
        fits_on_single_square = false;
        anInt1620++;

        numVertices = model.numVertices;
        trianglesCount = model.trianglesCount;
        numberOfTexturesFaces = model.numberOfTexturesFaces;
        if (adjust_elevation) {
            vertexY = new int[numVertices];
            System.arraycopy(model.vertexY, 0, vertexY, 0, numVertices);
            //for (int point = 0; point < vertices; point++)
            //	vertex_y[point] = model.vertex_y[point];

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
                for (int face = 0; face < trianglesCount; face++) {
                    faceDrawType[face] = 0;
                    ;//System.out.println("Caught! [Black object exists] " + id);
                }

            } else {
                //for (int face = 0; face < faces; face++)
                //	render_type[face] = model.render_type[face];
                System.arraycopy(model.faceDrawType, 0, faceDrawType, 0, trianglesCount);

            }
            super.normals = new VertexNormal[numVertices];
            for (int point = 0; point < numVertices; point++) {
                VertexNormal class33 = super.normals[point] = new VertexNormal();
                VertexNormal class33_1 = model.normals[point];
                class33.x = class33_1.x;
                class33.y = class33_1.y;
                class33.z = class33_1.z;
                class33.magnitude = class33_1.magnitude;
            }
            gouraud_vertex = model.gouraud_vertex;

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
        diagonal_3D = model.diagonal_3D;
        scene_depth = model.scene_depth;
        min_x = model.min_x;
        max_z = model.max_z;
        min_z = model.min_z;
        max_x = model.max_x;
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

        //this.aClass158Array3788 = model.aClass158Array3788;
        //this.aClass169Array3776 = model.aClass169Array3776;
    }

    private final int get_shared_vertices(Model model, int point) {
        int shared_vertex = -1;
        int x = model.vertexX[point];
        int y = model.vertexY[point];
        int z = model.vertexZ[point];
        for (int vertex = 0; vertex < numVertices; vertex++) {
            if(x == vertexX[vertex] && y == vertexY[vertex] && z == vertexZ[vertex]) {
                shared_vertex = vertex;
                break;
            }
        }
        if(shared_vertex == -1) {
            vertexX[numVertices] = x;
            vertexY[numVertices] = y;
            vertexZ[numVertices] = z;
            if (model.vertexVSkin != null)
                vertexVSkin[numVertices] = model.vertexVSkin[point];

            shared_vertex = numVertices++;
        }
        return shared_vertex;
    }

    public void calc_diagonals() {
        super.modelBaseY = 0;
        maxVertexDistanceXZPlane = 0;
        max_y = 0;
        for (int index = 0; index < numVertices; index++) {
            int x = vertexX[index];
            int y = vertexY[index];
            int z = vertexZ[index];
            if (-y > super.modelBaseY)
                super.modelBaseY = -y;

            if (y > max_y)
                max_y = y;

            int bounds = x * x + z * z;
            if (bounds > maxVertexDistanceXZPlane)
                maxVertexDistanceXZPlane = bounds;
        }
        maxVertexDistanceXZPlane = (int) (Math.sqrt(maxVertexDistanceXZPlane) + 0.98999999999999999D);
        diagonal_3D = (int) (Math.sqrt(maxVertexDistanceXZPlane * maxVertexDistanceXZPlane + super.modelBaseY * super.modelBaseY) + 0.98999999999999999D);//aShort3763
        scene_depth = diagonal_3D + (int) (Math.sqrt(maxVertexDistanceXZPlane * maxVertexDistanceXZPlane + max_y * max_y) + 0.98999999999999999D);//aShort3785
    }

    public void normalize() {
        super.modelBaseY = 0;
        max_y = 0;
        for (int index = 0; index < numVertices; index++) {
            int y = vertexY[index];
            if (-y > super.modelBaseY)
                super.modelBaseY = -y;

            if (y > max_y)
                max_y = y;

        }
        diagonal_3D = (int) (Math.sqrt(maxVertexDistanceXZPlane * maxVertexDistanceXZPlane + super.modelBaseY * super.modelBaseY) + 0.98999999999999999D);
        scene_depth = diagonal_3D + (int) (Math.sqrt(maxVertexDistanceXZPlane * maxVertexDistanceXZPlane + max_y * max_y) + 0.98999999999999999D);
    }

    public void calculateVertexData(int i) {
        super.modelBaseY = 0;
        maxVertexDistanceXZPlane = 0;
        max_y = 0;
        min_x = 0xf423f;
        max_x = 0xfff0bdc1;
        max_z = 0xfffe7961;
        min_z = 0x1869f;
        for (int j = 0; j < numVertices; j++) {
            int x = vertexX[j];
            int y = vertexY[j];
            int z = vertexZ[j];
            if (x < min_x)
                min_x = x;
            if (x > max_x)
                max_x = x;
            if (z < min_z)
                min_z = z;
            if (z > max_z)
                max_z = z;
            if (-y > super.modelBaseY)
                super.modelBaseY = -y;
            if (y > max_y)
                max_y = y;
            int j1 = x * x + z * z;
            if (j1 > maxVertexDistanceXZPlane)
                maxVertexDistanceXZPlane = j1;
        }
        maxVertexDistanceXZPlane = (int) Math.sqrt(maxVertexDistanceXZPlane);
        diagonal_3D = (int) Math.sqrt(maxVertexDistanceXZPlane * maxVertexDistanceXZPlane + super.modelBaseY * super.modelBaseY);
        if (i != 21073) {
            return;
        } else {
            scene_depth = diagonal_3D + (int) Math.sqrt(maxVertexDistanceXZPlane * maxVertexDistanceXZPlane + max_y * max_y);
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
            for (int uid = 0; uid <= k; uid++) {
                faceGroups[uid] = new int[ai1[uid]];
                ai1[uid] = 0;
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
                    int auid[] = vertexGroups[i3];
                    for (int j4 = 0; j4 < auid.length; j4++) {
                        int k5 = auid[j4];
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
            for (int uid = 0; uid < i1; uid++) {
                int j3 = skin[uid];
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

        Frame frame = Frame.method531(frameId);
        if (frame == null)
            return;

        FrameBase skin = frame.base;
        xAnimOffset = 0;
        yAnimOffset = 0;
        zAnimOffset = 0;

        for (int index = 0; index < frame.transformationCount; index++) {
            int pos = frame.transformationIndices[index];
            transformSkin(skin.transformationType[pos], skin.skinList[pos], frame.transformX[index], frame.transformY[index], frame.transformZ[index]);
        }

    }


    public void mix(int label[], int idle, int current) {
        if (current == -1)
            return;

        if (label == null || idle == -1) {
            applyTransform(current);
            return;
        }
        Frame anim = Frame.method531(current);
        if (anim == null)
            return;

        Frame skin = Frame.method531(idle);
        if (skin == null) {
            applyTransform(current);
            return;
        }
        FrameBase list = anim.base;
        xAnimOffset = 0;
        yAnimOffset = 0;
        zAnimOffset = 0;
        int id = 0;
        int table = label[id++];
        for (int index = 0; index < anim.transformationCount; index++) {
            int condition;
            for (condition = anim.transformationIndices[index]; condition > table; table = label[id++])
                ;//empty
            if (condition != table || list.transformationType[condition] == 0) {
                transformSkin(list.transformationType[condition], list.skinList[condition], skin.transformX[index], skin.transformY[index], skin.transformZ[index]);
            }
        }
        xAnimOffset = 0;
        yAnimOffset = 0;
        zAnimOffset = 0;
        id = 0;
        table = label[id++];
        for (int index = 0; index < skin.transformationCount; index++) {
            int condition;
            for (condition = skin.transformationIndices[index]; condition > table; table = label[id++])
                ;//empty
            if (condition == table || list.transformationType[condition] == 0) {
                transformSkin(list.transformationType[condition], list.skinList[condition], skin.transformX[index], skin.transformY[index], skin.transformZ[index]);
            }

        }
    }



    public void rotate90Degrees() {
        for (int point = 0; point < numVertices; point++) {
            int x = vertexX[point];
            vertexX[point] = vertexZ[point];
            vertexZ[point] = -x;
        }
    }

    public void leanOverX(int factor) {
        int sin = SINE[factor];
        int cos = COSINE[factor];
        for (int point = 0; point < numVertices; point++) {
            int y = vertexY[point] * cos - vertexZ[point] * sin >> 16;
            vertexZ[point] = vertexY[point] * sin + vertexZ[point] * cos >> 16;
            vertexY[point] = y;
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
        if(triangleColours != null) {
            for (int face = 0; face < trianglesCount; face++) {
                if (triangleColours[face] == (short) found) {
                    triangleColours[face] = (short) replace;
                }
            }
        }
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

    public void invert() {
        for (int index = 0; index < numVertices; index++)
            vertexZ[index] = -vertexZ[index];

        for (int face = 0; face < trianglesCount; face++) {
            int tri_a = facePointA[face];
            facePointA[face] = facePointC[face];
            facePointC[face] = tri_a;
        }
    }

    public void scale() {
        for (int index = 0; index < numVertices; index++) {
            vertexX[index] >>= 2;
            vertexY[index] >>= 2;
            vertexZ[index] >>= 2;
        }
    }

    public void scale(int x, int z, int y) {
        for (int index = 0; index < numVertices; index++) {
            vertexX[index] = (vertexX[index] * x) / 128;
            vertexY[index] = (vertexY[index] * y) / 128;
            vertexZ[index] = (vertexZ[index] * z) / 128;
        }

    }

	/*public void computeNormals() {//was condesnsed into the lighting method, this is the proper src
		if (super.aClass33Array1425 == null) {
			super.aClass33Array1425 = new Vertex[vertices];

			for (int index = 0; index < vertices; index++)
				super.aClass33Array1425[index] = new Vertex();

			for (int face = 0; face < faces; face++) {
				int j2 = triangle_edge_a[face];
				int l2 = triangle_edge_b[face];
				int i3 = triangle_edge_c[face];
				int j3 = vertex_x[l2] - vertex_x[j2];
				int k3 = vertex_y[l2] - vertex_y[j2];
				int l3 = vertex_z[l2] - vertex_z[j2];
				int i4 = vertex_x[i3] - vertex_x[j2];
				int j4 = vertex_y[i3] - vertex_y[j2];
				int k4 = vertex_z[i3] - vertex_z[j2];
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

				int type;
				if(render_type == null)
					type = 0;
				else
					type = render_type[face];


				if (render_type == null || (render_type[face] & 1) == 0) {
					Vertex class33_2 = super.aClass33Array1425[j2];
					class33_2.anInt602 += l4;
					class33_2.anInt603 += i5;
					class33_2.anInt604 += j5;
					class33_2.anInt605++;
					class33_2 = super.aClass33Array1425[l2];
					class33_2.anInt602 += l4;
					class33_2.anInt603 += i5;
					class33_2.anInt604 += j5;
					class33_2.anInt605++;
					class33_2 = super.aClass33Array1425[i3];
					class33_2.anInt602 += l4;
					class33_2.anInt603 += i5;
					class33_2.anInt604 += j5;
					class33_2.anInt605++;
				} else {
					if(super.aClass33Array1425 == null) {
						super.aClass33Array1425 = new Vertex[faces];
					}

					Vertex class33_2 = super.aClass33Array1425[face] = new Vertex();
					class33_2.anInt602 = l4;
					class33_2.anInt603 = i5;
					class33_2.anInt604 = j5;

				}
			}
		}
	}*/

    public void light(int intensity, int mag, int x, int y, int z, boolean flat_shading) {
        light(intensity, mag, x, y, z, flat_shading, false);
    }

    public void light(int dir_light_initial_intensity, int specular_distribution_factor, int dir_light_x, int dir_light_y, int dir_light_z, boolean flat_shading, boolean player) {
        int pre_dir_light_length = (int) Math.sqrt(dir_light_x * dir_light_x + dir_light_y * dir_light_y + dir_light_z * dir_light_z);
        int pre_specular_distribution_factor = specular_distribution_factor * pre_dir_light_length >> 8;
        faceHslA = new int[trianglesCount];
        faceHslB = new int[trianglesCount];
        faceHslC = new int[trianglesCount];

        if (super.normals == null) {
            super.normals = new VertexNormal[numVertices];
            for (int index = 0; index < numVertices; index++)
                super.normals[index] = new VertexNormal();

            for (int face = 0; face < trianglesCount; face++) {
                int point_a = facePointA[face];
                int point_b = facePointB[face];
                int point_c = facePointC[face];
                int b_a_pos_x = vertexX[point_b] - vertexX[point_a];
                int b_a_pos_y = vertexY[point_b] - vertexY[point_a];
                int b_a_pos_z = vertexZ[point_b] - vertexZ[point_a];
                int c_a_pos_x = vertexX[point_c] - vertexX[point_a];
                int c_a_pos_y = vertexY[point_c] - vertexY[point_a];
                int c_a_pos_z = vertexZ[point_c] - vertexZ[point_a];
                int normal_x = b_a_pos_y * c_a_pos_z - c_a_pos_y * b_a_pos_z;
                int normal_y = b_a_pos_z * c_a_pos_x - c_a_pos_z * b_a_pos_x;
                int normal_z;
                for (normal_z = b_a_pos_x * c_a_pos_y - c_a_pos_x * b_a_pos_y; normal_x > 8192 || normal_y > 8192 || normal_z > 8192 || normal_x < -8192 || normal_y < -8192 || normal_z < -8192; normal_z >>= 1) {
                    normal_x >>= 1;
                    normal_y >>= 1;
                }
                int dir_light_length = (int) Math.sqrt(normal_x * normal_x + normal_y * normal_y + normal_z * normal_z);
                if (dir_light_length <= 0)
                    dir_light_length = 1;

                normal_x = (normal_x * 256) / dir_light_length;
                normal_y = (normal_y * 256) / dir_light_length;
                normal_z = (normal_z * 256) / dir_light_length;

                int type;
                if(faceDrawType == null)
                    type = 0;
                else
                    type = faceDrawType[face];

                int transparent;
                if(face_alpha == null)
                    transparent = 0;
                else
                    transparent = face_alpha[face];

                short texture_id;
                if(materials == null)
                    texture_id = -1;
                else
                    texture_id = materials[face];

                if(transparent == -2) {
                    type = 3;
                }
                if(transparent == -1) {
                    type = 2;
                }
                if (faceDrawType == null || (faceDrawType[face] & 1) == 0) {
                    VertexNormal vertex = super.normals[point_a];
                    vertex.x += normal_x;
                    vertex.y += normal_y;
                    vertex.z += normal_z;
                    vertex.magnitude++;
                    vertex = super.normals[point_b];
                    vertex.x += normal_x;
                    vertex.y += normal_y;
                    vertex.z += normal_z;
                    vertex.magnitude++;
                    vertex = super.normals[point_c];
                    vertex.x += normal_x;
                    vertex.y += normal_y;
                    vertex.z += normal_z;
                    vertex.magnitude++;
                } else {
                    int light = dir_light_initial_intensity + (dir_light_x * normal_x + dir_light_y * normal_y + dir_light_z * normal_z) / (pre_specular_distribution_factor + pre_specular_distribution_factor / 2);
                    if(texture_id != -1) {
                        if(type == 1) {
                            faceHslA[face] = light(light);
                            faceHslC[face] = -1;
                        } else {
                            faceHslC[face] = -2;
                        }
                    } else {
                        if(type != 0) {
                            if(type == 1) {
                                faceHslA[face] = light(triangleColours[face] & 0xffff, light);
                                faceHslC[face] = -1;
                            } else if(type == 3) {
                                faceHslA[face] = 128;
                                faceHslC[face] = -1;
                            } else {
                                faceHslA[face] = light(triangleColours[face] & 0xffff, light);
                                faceHslC[face] = -1;
                            }
                        } else {
                            faceHslA[face] = light(triangleColours[face] & 0xffff, light);
                            faceHslC[face] = -1;
                        }
                    }
                }
            }
        }
        if (flat_shading) {
            flat_lighting(dir_light_initial_intensity, pre_specular_distribution_factor, dir_light_x, dir_light_y, dir_light_z, player);
        } else {
            gouraud_vertex = new VertexNormal[numVertices];
            for (int point = 0; point < numVertices; point++) {
                VertexNormal norm = super.normals[point];
                VertexNormal merge = gouraud_vertex[point] = new VertexNormal();
                merge.x = norm.x;
                merge.y = norm.y;
                merge.z = norm.z;
                merge.magnitude = norm.magnitude;
            }
        }
        if (flat_shading) {
            calc_diagonals();
        } else {
            calculateVertexData(21073);
        }

        if (textures == null) {
            vertexNormals();
        }

        if (faceTextureUVCoordinates == null) {
            computeTextureUvCoordinates();
        }

        VertexNormal[] vertexNormals = normals;
        VertexNormal[] vertexVertices = gouraud_vertex;

        if (vertexNormals != null && vertexNormalsX == null) {
            int verticesCount = getVerticesCount();

            vertexNormalsX = new int[verticesCount];
            vertexNormalsY = new int[verticesCount];
            vertexNormalsZ = new int[verticesCount];

            for (int i = 0; i < verticesCount; ++i) {
                VertexNormal vertexNormal;

                if (vertexVertices != null && (vertexNormal = vertexVertices[i]) != null) {
                    vertexNormalsX[i] = vertexNormal.x;
                    vertexNormalsY[i] = vertexNormal.y;
                    vertexNormalsZ[i] = vertexNormal.z;
                } else if ((vertexNormal = vertexNormals[i]) != null) {
                    vertexNormalsX[i] = vertexNormal.x;
                    vertexNormalsY[i] = vertexNormal.y;
                    vertexNormalsZ[i] = vertexNormal.z;
                }
            }
        }

    }

    public static final int OSRS_MODEL_DRAW_DISTANCE = 5550;

    public final void flat_lighting(int dir_light_initial_intensity, int specular_distribution_factor, int x, int y, int z) {
        flat_lighting(dir_light_initial_intensity, specular_distribution_factor, x, y, z, false);
    }

    public final void flat_lighting(int dir_light_initial_intensity, int specular_distribution_factor, int x, int y, int z, boolean player) {
        for (int face = 0; face < trianglesCount; face++) {
            int a = facePointA[face];
            int b = facePointB[face];
            int c = facePointC[face];
            if(materials != null) {
                if(player) {
                    //These checks are all important! - black triangle check
                    if(face_alpha != null && triangleColours != null) {
                        if(triangleColours[face] == 0 && face_render_priorities[face] == 0) {
                            if(faceDrawType[face] == 2 && materials[face] == -1) {
                                face_alpha[face] = (byte) 255;
                            }
                        }
                    } else if(face_alpha == null) {
                        if(triangleColours[face] == 0 && face_render_priorities[face] == 0) {
                            if(materials[face] == -1) {
                                face_alpha = new byte[trianglesCount];
                            }
                        }
                    }
                }
            }
            if (faceDrawType == null) {
                int hsl = triangleColours[face] & '\uffff';
                VertexNormal vertex = super.normals[a];
                int dir_light_intensity = dir_light_initial_intensity + (x * vertex.x + y * vertex.y + z * vertex.z) / (specular_distribution_factor * vertex.magnitude);
                faceHslA[face] = light(hsl, dir_light_intensity, 0);

                vertex = super.normals[b];
                dir_light_intensity = dir_light_initial_intensity + (x * vertex.x + y * vertex.y + z * vertex.z) / (specular_distribution_factor * vertex.magnitude);
                faceHslB[face] = light(hsl, dir_light_intensity, 0);

                vertex = super.normals[c];
                dir_light_intensity = dir_light_initial_intensity + (x * vertex.x + y * vertex.y + z * vertex.z) / (specular_distribution_factor * vertex.magnitude);
                faceHslC[face] = light(hsl, dir_light_intensity, 0);
            } else if ((faceDrawType[face] & 1) == 0) {
                int type = faceDrawType[face];
                int hsl = triangleColours[face] & '\uffff';
                VertexNormal vertex = super.normals[a];
                int dir_light_intensity = dir_light_initial_intensity + (x * vertex.x + y * vertex.y + z * vertex.z) / (specular_distribution_factor * vertex.magnitude);
                faceHslA[face] = light(hsl, dir_light_intensity, type);

                vertex = super.normals[b];
                dir_light_intensity = dir_light_initial_intensity + (x * vertex.x + y * vertex.y + z * vertex.z) / (specular_distribution_factor * vertex.magnitude);
                faceHslB[face] = light(hsl, dir_light_intensity, type);

                vertex = super.normals[c];
                dir_light_intensity = dir_light_initial_intensity + (x * vertex.x + y * vertex.y + z * vertex.z) / (specular_distribution_factor * vertex.magnitude);
                faceHslC[face] = light(hsl, dir_light_intensity, type);
            }
        }
        super.normals = null;
        gouraud_vertex = null;
        vertexVSkin = null;
        triangleTSkin = null;
        if (faceDrawType != null) {
            for (int face = 0; face < trianglesCount; face++)
                if ((faceDrawType[face] & 2) == 2)
                    return;
        }
        triangleColours = null;
    }

    public static final int light(int light) {
        if(light >= 2) {
            if(light > 126) {
                light = 126;
            }
        } else {
            light = 2;
        }
        return light;
    }

    public static final int light(int hsl, int light) {
        light = light * (hsl & 127) >> 7;
        if(light < 2) {
            light = 2;
        } else if(light > 126) {
            light = 126;
        }
        return (hsl & '\uff80') + light;
    }

    public static final int light(int hsl, int light, int type) {
        if ((type & 2) == 2)
            return light(light);

        return light(hsl, light);
    }

    //inventory / widget model rendering
    public final void render_2D(int roll, int yaw, int pitch, int start_x, int start_y, int zoom) {
        int depth = 0;
        int originViewX = Rasterizer3D.originViewX;
        int originViewY = Rasterizer3D.originViewY;
        int depth_sin = SINE[depth];
        int depth_cos = COSINE[depth];
        int roll_sin = SINE[roll];
        int roll_cos = COSINE[roll];
        int yaw_sin = SINE[yaw];
        int yaw_cos = COSINE[yaw];
        int pitch_sin = SINE[pitch];
        int pitch_cos = COSINE[pitch];
        int position = start_y * pitch_sin + zoom * pitch_cos >> 16;
        for (int index = 0; index < numVertices; index++) {
            int x = vertexX[index];
            int y = vertexY[index];
            int z = vertexZ[index];
            if (yaw != 0) {
                int rotated_x = y * yaw_sin + x * yaw_cos >> 16;
                y = y * yaw_cos - x * yaw_sin >> 16;
                x = rotated_x;
            }
            if (depth != 0) {
                int rotated_y = y * depth_cos - z * depth_sin >> 16;
                z = y * depth_sin + z * depth_cos >> 16;
                y = rotated_y;
            }
            if (roll != 0) {
                int rotated_z = z * roll_sin + x * roll_cos >> 16;
                z = z * roll_cos - x * roll_sin >> 16;
                x = rotated_z;
            }
            x += start_x;
            y += start_y;
            z += zoom;

            int y_offset = y * pitch_cos - z * pitch_sin >> 16;
            z = y * pitch_sin + z * pitch_cos >> 16;
            y = y_offset;

            anIntArray1668[index] = z - position;
            projected_vertex_z[index] = z;//0
            projected_vertex_x[index] = originViewX + (x << 9) / z;
            projected_vertex_y[index] = originViewY + (y << 9) / z;
            if (numberOfTexturesFaces > 0) {
                camera_vertex_y[index] = x;
                camera_vertex_x[index] = y;
                camera_vertex_z[index] = z;
            }
        }
        try {
            method483(false, false, 0);
        } catch (Exception _ex) {
            _ex.printStackTrace();
        }
    }

    public static final int VIEW_DISTANCE = 6500;

    //Scene models
    @Override
    public final void renderAtPoint(int orientation, int pitchSine, int pitchCos, int yawSin, int yawCos, int offsetX, int offsetY, int offsetZ, long uid) {
        calculateBoundingBox(orientation);
        int scene_x = offsetZ * yawCos - offsetX * yawSin >> 16;
        int scene_y = offsetY * pitchSine + scene_x * pitchCos >> 16;
        int dimension_sin_y = maxVertexDistanceXZPlane * pitchCos >> 16;
        int pos = scene_y + dimension_sin_y;
        final boolean gpu = Client.processGpuPlugin() && Rasterizer3D.world;
        if (pos <= 50 || (scene_y >= OSRS_MODEL_DRAW_DISTANCE && !gpu))
            return;
        int x_rot = offsetZ * yawSin + offsetX * yawCos >> 16;
        int obj_x = (x_rot - maxVertexDistanceXZPlane) * Rasterizer3D.fieldOfView;
        if (obj_x / pos >= Rasterizer2D.viewportCenterX)
            return;

        int obj_width = (x_rot + maxVertexDistanceXZPlane) * Rasterizer3D.fieldOfView;
        if (obj_width / pos <= -Rasterizer2D.viewportCenterX)
            return;

        int y_rot = offsetY * pitchCos - scene_x * pitchSine >> 16;
        int dimension_cos_y = maxVertexDistanceXZPlane * pitchSine >> 16;
        int obj_height = (y_rot + dimension_cos_y) * Rasterizer3D.fieldOfView;
        if (obj_height / pos <= -Rasterizer2D.viewportCenterY)
            return;

        int offset = dimension_cos_y + (super.modelBaseY * pitchCos >> 16);
        int obj_y = (y_rot - offset) * Rasterizer3D.fieldOfView;
        if (obj_y / pos >= Rasterizer2D.viewportCenterY)
            return;

        int size = dimension_sin_y + (super.modelBaseY * pitchSine >> 16);

        boolean near_sight = false;//wrong
        if (scene_y - size <= 50)
            near_sight = true;

        boolean highlighted = false;

        if (uid > 0 && obj_exists) {
            int obj_height_offset = scene_y - dimension_sin_y;
            if (obj_height_offset <= 50)
                obj_height_offset = 50;
            if (x_rot > 0) {
                obj_x /= pos;
                obj_width /= obj_height_offset;
            } else {
                obj_width /= pos;
                obj_x /= obj_height_offset;
            }
            if (y_rot > 0) {
                obj_y /= pos;
                obj_height /= obj_height_offset;
            } else {
                obj_height /= pos;
                obj_y /= obj_height_offset;
            }
            int mouse_x = anInt1685 - Rasterizer3D.originViewX;
            int mouse_y = anInt1686 - Rasterizer3D.originViewY;
            if (mouse_x > obj_x && mouse_x < obj_width && mouse_y > obj_y && mouse_y < obj_height) {
                if (fits_on_single_square) {
                    obj_key[obj_loaded++] = uid;
                    if (gpu) {
                        Client.instance.getDrawCallbacks().draw(this, orientation, pitchSine, pitchCos, yawSin, yawCos, offsetX, offsetY, offsetZ, uid);
                        return;
                    }
                } else {
                    highlighted = true;
                }
            }
        }
        int originViewX = Rasterizer3D.originViewX;
        int originViewY = Rasterizer3D.originViewY;
        int sine_x = 0;
        int cosine_x = 0;
        if (orientation != 0) {
            sine_x = SINE[orientation];
            cosine_x = COSINE[orientation];
        }

        for (int index = 0; index < numVertices; index++) {

            int raster_x = vertexX[index];
            int raster_y = vertexY[index];
            int raster_z = vertexZ[index];
            if (orientation != 0) {
                int rotated_x = raster_z * sine_x + raster_x * cosine_x >> 16;
                raster_z = raster_z * cosine_x - raster_x * sine_x >> 16;
                raster_x = rotated_x;
            }
            raster_x += offsetX;
            raster_y += offsetY;
            raster_z += offsetZ;

            int position = raster_z * yawSin + raster_x * yawCos >> 16;
            raster_z = raster_z * yawCos - raster_x * yawSin >> 16;
            raster_x = position;

            position = raster_y * pitchCos - raster_z * pitchSine >> 16;
            raster_z = raster_y * pitchSine + raster_z * pitchCos >> 16;
            raster_y = position;

            anIntArray1668[index] = raster_z - scene_y;
            projected_vertex_z[index] = raster_z;
            if (raster_z >= 50) {
                projected_vertex_x[index] = originViewX + raster_x * Rasterizer3D.fieldOfView / raster_z;
                projected_vertex_y[index] = originViewY + raster_y * Rasterizer3D.fieldOfView / raster_z;
            } else {
                projected_vertex_x[index] = -5000;
                near_sight = true;
            }
            if (near_sight || numberOfTexturesFaces > 0 && !gpu) {
                camera_vertex_y[index] = raster_x;
                camera_vertex_x[index] = raster_y;
                camera_vertex_z[index] = raster_z;
            }

        }

        try {
            if (!gpu || (highlighted && !(Math.sqrt(offsetX * offsetX + offsetZ * offsetZ) > 35 * Perspective.LOCAL_TILE_SIZE))) {
                method483(near_sight, highlighted, uid);
            }
            if (gpu) {
                Client.instance.getDrawCallbacks().draw(this, orientation, pitchSine, pitchCos, yawSin, yawCos, offsetX, offsetY, offsetZ, uid);
            }
        } catch (Exception _ex) {
            _ex.printStackTrace();
        }
    }

    private void method483(boolean flag, boolean flag1, long i) {
        final boolean gpu = Client.processGpuPlugin() && Rasterizer3D.world;

        for (int j = 0; j < scene_depth; j++)
            depthListIndices[j] = 0;

        for (int k = 0; k < trianglesCount; k++)
            if (faceDrawType == null || faceDrawType[k] != -1) {
                int l = facePointA[k];
                int k1 = facePointB[k];
                int j2 = facePointC[k];
                int i3 = projected_vertex_x[l];
                int l3 = projected_vertex_x[k1];
                int k4 = projected_vertex_x[j2];

                if (gpu) {
                    if (i3 == -5000 || l3 == -5000 || k4 == -5000) {
                        continue;
                    }
                    if (flag1 && entered_clickbox(anInt1685, anInt1686, projected_vertex_y[l], projected_vertex_y[k1], projected_vertex_y[j2], i3, l3, k4)) {
                        obj_key[obj_loaded++] = i;
                        flag1 = false;
                    }

                    if(Client.instance.getDrawCallbacks() != null) {
                        Client.instance.getDrawCallbacks().drawFace(this, k);
                    }
                }

                if (flag && (i3 == -5000 || l3 == -5000 || k4 == -5000)) {
                    outOfReach[k] = true;
                    int j5 = (anIntArray1668[l] + anIntArray1668[k1] + anIntArray1668[j2])
                            / 3 + diagonal_3D;
                    faceLists[j5][depthListIndices[j5]++] = k;
                } else {
                    if (flag1
                            && entered_clickbox(anInt1685, anInt1686,
                            projected_vertex_y[l], projected_vertex_y[k1],
                            projected_vertex_y[j2], i3, l3, k4)) {

                        obj_key[obj_loaded++] = i;
                        flag1 = false;
                    }
                    if ((i3 - l3) * (projected_vertex_y[j2] - projected_vertex_y[k1])
                            - (projected_vertex_y[l] - projected_vertex_y[k1])
                            * (k4 - l3) > 0) {
                        outOfReach[k] = false;
                        if (i3 < 0 || l3 < 0 || k4 < 0
                                || i3 > Rasterizer2D.lastX
                                || l3 > Rasterizer2D.lastX
                                || k4 > Rasterizer2D.lastX)
                            hasAnEdgeToRestrict[k] = true;
                        else
                            hasAnEdgeToRestrict[k] = false;
                        int k5 = (anIntArray1668[l] + anIntArray1668[k1] + anIntArray1668[j2])
                                / 3 + diagonal_3D;
                        faceLists[k5][depthListIndices[k5]++] = k;
                    }
                }
            }
        if (gpu) {
            return;
        }
        if (face_render_priorities == null) {
            for (int i1 = scene_depth - 1; i1 >= 0; i1--) {
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

        for (int i2 = scene_depth - 1; i2 >= 0; i2--) {
            int k2 = depthListIndices[i2];
            if (k2 > 0) {
                int ai1[] = faceLists[i2];
                for (int i4 = 0; i4 < k2; i4++) {
                    int l4 = ai1[i4];
                    int l5 = face_render_priorities[l4];
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
            l2 = (anIntArray1677[1] + anIntArray1677[2])
                    / (anIntArray1673[1] + anIntArray1673[2]);
        int k3 = 0;
        if (anIntArray1673[3] > 0 || anIntArray1673[4] > 0)
            k3 = (anIntArray1677[3] + anIntArray1677[4])
                    / (anIntArray1673[3] + anIntArray1673[4]);
        int j4 = 0;
        if (anIntArray1673[6] > 0 || anIntArray1673[8] > 0)
            j4 = (anIntArray1677[6] + anIntArray1677[8])
                    / (anIntArray1673[6] + anIntArray1673[8]);
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
            rasterize_rotation(face);
            return;
        }
        int tri_a = facePointA[face];
        int tri_b = facePointB[face];
        int tri_c = facePointC[face];
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

        if(materials != null && materials[face] != -1) {
            int texture_a;
            int texture_b;
            int texture_c;
            if(textures != null && textures[face] != -1) {
                int coordinate = textures[face] & 0xff;
                texture_a = textures_face_a[coordinate];
                texture_b = textures_face_b[coordinate];
                texture_c = textures_face_c[coordinate];
            } else {
                texture_a = tri_a;
                texture_b = tri_b;
                texture_c = tri_c;
            }

            if(faceHslC[face] == -1) {
                Rasterizer3D.drawTexturedTriangle(
                        projected_vertex_y[tri_a], projected_vertex_y[tri_b], projected_vertex_y[tri_c],
                        projected_vertex_x[tri_a], projected_vertex_x[tri_b], projected_vertex_x[tri_c],
                        faceHslA[face], faceHslA[face], faceHslA[face],
                        camera_vertex_y[texture_a], camera_vertex_y[texture_b], camera_vertex_y[texture_c],
                        camera_vertex_x[texture_a], camera_vertex_x[texture_b], camera_vertex_x[texture_c],
                        camera_vertex_z[texture_a], camera_vertex_z[texture_b], camera_vertex_z[texture_c],
                        materials[face]);
            } else {
                Rasterizer3D.drawTexturedTriangle(
                        projected_vertex_y[tri_a], projected_vertex_y[tri_b], projected_vertex_y[tri_c],
                        projected_vertex_x[tri_a], projected_vertex_x[tri_b], projected_vertex_x[tri_c],
                        faceHslA[face], faceHslB[face], faceHslC[face],
                        camera_vertex_y[texture_a], camera_vertex_y[texture_b], camera_vertex_y[texture_c],
                        camera_vertex_x[texture_a], camera_vertex_x[texture_b], camera_vertex_x[texture_c],
                        camera_vertex_z[texture_a], camera_vertex_z[texture_b], camera_vertex_z[texture_c],
                        materials[face]);

            }
        } else if(faceHslC[face] == -1) {
            Rasterizer3D.drawFlatTriangle(
                    projected_vertex_y[tri_a], projected_vertex_y[tri_b], projected_vertex_y[tri_c],
                    projected_vertex_x[tri_a], projected_vertex_x[tri_b], projected_vertex_x[tri_c],
                    modelIntArray3[faceHslA[face]]);

        } else {
            if (type == 0) {
                Rasterizer3D.drawShadedTriangle(
                        projected_vertex_y[tri_a], projected_vertex_y[tri_b], projected_vertex_y[tri_c],
                        projected_vertex_x[tri_a], projected_vertex_x[tri_b], projected_vertex_x[tri_c],
                        faceHslA[face], faceHslB[face], faceHslC[face]);

            }
        }
    }

    private final void rasterize_rotation(int face) {
        int originViewX = Rasterizer3D.originViewX;
        int originViewY = Rasterizer3D.originViewY;
        int factor = 0;
        int tri_a = facePointA[face];
        int tri_b = facePointB[face];
        int tri_c = facePointC[face];
        int depth_a = camera_vertex_z[tri_a];
        int depth_b = camera_vertex_z[tri_b];
        int depth_c = camera_vertex_z[tri_c];
        if (face_alpha == null)
            Rasterizer3D.alpha = 0;
        else
            Rasterizer3D.alpha = face_alpha[face] & 0xff;

        if (depth_a >= 50) {
            anIntArray1678[factor] = projected_vertex_x[tri_a];
            anIntArray1679[factor] = projected_vertex_y[tri_a];
            anIntArray1680[factor++] = faceHslA[face];
        } else {
            int x_a = camera_vertex_y[tri_a];
            int y_a = camera_vertex_x[tri_a];
            int z_a = faceHslA[face];
            if (depth_c >= 50) {
                int depth = (50 - depth_a) * modelIntArray4[depth_c - depth_a];
                anIntArray1678[factor] = originViewX + (x_a + ((camera_vertex_y[tri_c] - x_a) * depth >> 16)) * Rasterizer3D.fieldOfView / 50;
                anIntArray1679[factor] = originViewY + (y_a + ((camera_vertex_x[tri_c] - y_a) * depth >> 16)) * Rasterizer3D.fieldOfView / 50;
                anIntArray1680[factor++] = z_a + ((faceHslC[face] - z_a) * depth >> 16);
            }
            if (depth_b >= 50) {
                int depth = (50 - depth_a) * modelIntArray4[depth_b - depth_a];
                anIntArray1678[factor] = originViewX + (x_a + ((camera_vertex_y[tri_b] - x_a) * depth >> 16)) * Rasterizer3D.fieldOfView / 50;
                anIntArray1679[factor] = originViewY + (y_a + ((camera_vertex_x[tri_b] - y_a) * depth >> 16)) * Rasterizer3D.fieldOfView / 50;
                anIntArray1680[factor++] = z_a + ((faceHslB[face] - z_a) * depth >> 16);
            }
        }
        if (depth_b >= 50) {
            anIntArray1678[factor] = projected_vertex_x[tri_b];
            anIntArray1679[factor] = projected_vertex_y[tri_b];
            anIntArray1680[factor++] = faceHslB[face];
        } else {
            int x_b = camera_vertex_y[tri_b];
            int y_b = camera_vertex_x[tri_b];
            int z_c = faceHslB[face];
            if (depth_a >= 50) {
                int depth = (50 - depth_b) * modelIntArray4[depth_a - depth_b];
                anIntArray1678[factor] = originViewX + (x_b + ((camera_vertex_y[tri_a] - x_b) * depth >> 16)) * Rasterizer3D.fieldOfView / 50;
                anIntArray1679[factor] = originViewY + (y_b + ((camera_vertex_x[tri_a] - y_b) * depth >> 16)) * Rasterizer3D.fieldOfView / 50;
                anIntArray1680[factor++] = z_c + ((faceHslA[face] - z_c) * depth >> 16);
            }
            if (depth_c >= 50) {
                int depth = (50 - depth_b) * modelIntArray4[depth_c - depth_b];
                anIntArray1678[factor] = originViewX + (x_b + ((camera_vertex_y[tri_c] - x_b) * depth >> 16)) * Rasterizer3D.fieldOfView / 50;
                anIntArray1679[factor] = originViewY + (y_b + ((camera_vertex_x[tri_c] - y_b) * depth >> 16)) * Rasterizer3D.fieldOfView / 50;
                anIntArray1680[factor++] = z_c + ((faceHslC[face] - z_c) * depth >> 16);
            }
        }
        if (depth_c >= 50) {
            anIntArray1678[factor] = projected_vertex_x[tri_c];
            anIntArray1679[factor] = projected_vertex_y[tri_c];
            anIntArray1680[factor++] = faceHslC[face];
        } else {
            int x_c = camera_vertex_y[tri_c];
            int y_c = camera_vertex_x[tri_c];
            int z_c = faceHslC[face];
            if (depth_b >= 50) {
                int depth = (50 - depth_c) * modelIntArray4[depth_b - depth_c];
                anIntArray1678[factor] = originViewX + (x_c + ((camera_vertex_y[tri_b] - x_c) * depth >> 16)) * Rasterizer3D.fieldOfView / 50;
                anIntArray1679[factor] = originViewY + (y_c + ((camera_vertex_x[tri_b] - y_c) * depth >> 16)) * Rasterizer3D.fieldOfView / 50;
                anIntArray1680[factor++] = z_c + ((faceHslB[face] - z_c) * depth >> 16);
            }
            if (depth_a >= 50) {
                int depth = (50 - depth_c) * modelIntArray4[depth_a - depth_c];
                anIntArray1678[factor] = originViewX + (x_c + ((camera_vertex_y[tri_a] - x_c) * depth >> 16)) * Rasterizer3D.fieldOfView / 50;
                anIntArray1679[factor] = originViewY + (y_c + ((camera_vertex_x[tri_a] - y_c) * depth >> 16)) * Rasterizer3D.fieldOfView / 50;
                anIntArray1680[factor++] = z_c + ((faceHslA[face] - z_c) * depth >> 16);
            }
        }
        int x_a = anIntArray1678[0];
        int x_b = anIntArray1678[1];
        int x_c = anIntArray1678[2];
        int y_a = anIntArray1679[0];
        int y_b = anIntArray1679[1];
        int y_c = anIntArray1679[2];
        if ((x_a - x_b) * (y_c - y_b) - (y_a - y_b) * (x_c - x_b) > 0) {
            Rasterizer3D.textureOutOfDrawingBounds = false;
            int tex_a = tri_a;
            int tex_b = tri_b;
            int tex_c = tri_c;
            if (factor == 3) {
                if (x_a < 0 || x_b < 0 || x_c < 0 || x_a > Rasterizer2D.lastX || x_b > Rasterizer2D.lastX || x_c > Rasterizer2D.lastX)
                    Rasterizer3D.textureOutOfDrawingBounds = true;

                int type;
                if (faceDrawType == null)
                    type = 0;
                else
                    type = faceDrawType[face] & 3;

                if(materials != null && materials[face] != -1) {
                    if(textures != null && textures[face] != -1) {
                        int coordinate = textures[face] & 0xff;
                        tex_a = textures_face_a[coordinate];
                        tex_b = textures_face_b[coordinate];
                        tex_c = textures_face_c[coordinate];
                    }
                    if(faceHslC[face] == -1) {
                        Rasterizer3D.drawTexturedTriangle(
                                y_a, y_b, y_c,
                                x_a, x_b, x_c,
                                faceHslA[face], faceHslA[face], faceHslA[face],
                                camera_vertex_y[tex_a], camera_vertex_y[tex_b], camera_vertex_y[tex_c],
                                camera_vertex_x[tex_a], camera_vertex_x[tex_b], camera_vertex_x[tex_c],
                                camera_vertex_z[tex_a], camera_vertex_z[tex_b], camera_vertex_z[tex_c],
                                materials[face]);
                    } else {
                        Rasterizer3D.drawTexturedTriangle(
                                y_a, y_b, y_c,
                                x_a, x_b, x_c,
                                anIntArray1680[0], anIntArray1680[1], anIntArray1680[2],
                                camera_vertex_y[tex_a], camera_vertex_y[tex_b], camera_vertex_y[tex_c],
                                camera_vertex_x[tex_a], camera_vertex_x[tex_b], camera_vertex_x[tex_c],
                                camera_vertex_z[tex_a], camera_vertex_z[tex_b], camera_vertex_z[tex_c],
                                materials[face]);
                    }
                } else if(faceHslC[face] == -1) {
                    Rasterizer3D.drawFlatTriangle(y_a, y_b, y_c, x_a, x_b, x_c, modelIntArray3[faceHslA[face]]);
                } else {
                    if (type == 0) {
                        Rasterizer3D.drawShadedTriangle(y_a, y_b, y_c, x_a, x_b, x_c, anIntArray1680[0], anIntArray1680[1], anIntArray1680[2]);
                    }
                }


            }
            if (factor == 4) {
                if (x_a < 0 || x_b < 0 || x_c < 0 || x_a > Rasterizer2D.lastX || x_b > Rasterizer2D.lastX || x_c > Rasterizer2D.lastX || anIntArray1678[3] < 0 || anIntArray1678[3] > Rasterizer2D.lastX)
                    Rasterizer3D.textureOutOfDrawingBounds = true;

                int type;
                if (faceDrawType == null)
                    type = 0;
                else
                    type = faceDrawType[face] & 3;

                if(materials != null && materials[face] != -1) {
                    if(textures != null && textures[face] != -1) {
                        int coordinate = textures[face] & 0xff;
                        tex_a = textures_face_a[coordinate];
                        tex_b = textures_face_b[coordinate];
                        tex_c = textures_face_c[coordinate];
                    }
                    if(faceHslC[face] == -1) {
                        Rasterizer3D.drawTexturedTriangle(
                                y_a, y_b, y_c,
                                x_a, x_b, x_c,
                                faceHslA[face], faceHslA[face], faceHslA[face],
                                camera_vertex_y[tex_a], camera_vertex_y[tex_b], camera_vertex_y[tex_c],
                                camera_vertex_x[tex_a], camera_vertex_x[tex_b], camera_vertex_x[tex_c],
                                camera_vertex_z[tex_a], camera_vertex_z[tex_b], camera_vertex_z[tex_c],
                                materials[face]);
                        Rasterizer3D.drawTexturedTriangle(
                                y_a, y_c, anIntArray1679[3],
                                x_a, x_c, anIntArray1678[3],
                                faceHslA[face], faceHslA[face], faceHslA[face],
                                camera_vertex_y[tex_a], camera_vertex_y[tex_b], camera_vertex_y[tex_c],
                                camera_vertex_x[tex_a], camera_vertex_x[tex_b], camera_vertex_x[tex_c],
                                camera_vertex_z[tex_a], camera_vertex_z[tex_b], camera_vertex_z[tex_c],
                                materials[face]);
                    } else {
                        Rasterizer3D.drawTexturedTriangle(
                                y_a, y_b, y_c,
                                x_a, x_b, x_c,
                                anIntArray1680[0], anIntArray1680[1], anIntArray1680[2],
                                camera_vertex_y[tex_a], camera_vertex_y[tex_b], camera_vertex_y[tex_c],
                                camera_vertex_x[tex_a], camera_vertex_x[tex_b], camera_vertex_x[tex_c],
                                camera_vertex_z[tex_a], camera_vertex_z[tex_b], camera_vertex_z[tex_c],
                                materials[face]);
                        Rasterizer3D.drawTexturedTriangle(
                                y_a, y_c, anIntArray1679[3],
                                x_a, x_c, anIntArray1678[3],
                                anIntArray1680[0], anIntArray1680[2], anIntArray1680[3],
                                camera_vertex_y[tex_a], camera_vertex_y[tex_b], camera_vertex_y[tex_c],
                                camera_vertex_x[tex_a], camera_vertex_x[tex_b], camera_vertex_x[tex_c],
                                camera_vertex_z[tex_a], camera_vertex_z[tex_b], camera_vertex_z[tex_c],
                                materials[face]);
                        //return;
                    }
                } else if(faceHslC[face] == -1) {
                    int color = modelIntArray3[faceHslA[face]];
                    Rasterizer3D.drawFlatTriangle(y_a, y_b, y_c, x_a, x_b, x_c, color);
                    Rasterizer3D.drawFlatTriangle(y_a, y_c, anIntArray1679[3], x_a, x_c, anIntArray1678[3], color);
                    //return;
                } else {
                    if (type == 0) {
                        Rasterizer3D.drawShadedTriangle(y_a, y_b, y_c, x_a, x_b, x_c, anIntArray1680[0], anIntArray1680[1], anIntArray1680[2]);
                        Rasterizer3D.drawShadedTriangle(y_a, y_c, anIntArray1679[3], x_a, x_c, anIntArray1678[3], anIntArray1680[0], anIntArray1680[2], anIntArray1680[3]);
                        //return;
                    }
                }

            }
        }
    }

    private final boolean entered_clickbox(int mouse_x, int mouse_y, int y_a, int y_b, int y_c, int x_a, int x_b, int x_c) {
        if (mouse_y < y_a && mouse_y < y_b && mouse_y < y_c)
            return false;
        if (mouse_y > y_a && mouse_y > y_b && mouse_y > y_c)
            return false;
        if (mouse_x < x_a && mouse_x < x_b && mouse_x < x_c)
            return false;
        return mouse_x <= x_a || mouse_x <= x_b || mouse_x <= x_c;
    }

    //*Added*//
    public short[] materials;
    public byte[] textures;
    public byte[] textureTypes;

    public static int anInt1620;
    public static Model EMPTY_MODEL = new Model();
    private static int anIntArray1622[] = new int[2000];
    private static int anIntArray1623[] = new int[2000];
    private static int anIntArray1624[] = new int[2000];
    private static byte anIntArray1625[] = new byte[2000];
    public int numVertices;
    public int vertexX[];
    public int vertexY[];
    public int vertexZ[];
    public int trianglesCount;
    public int facePointA[];
    public int facePointB[];
    public int facePointC[];
    public int faceHslA[];
    public int faceHslB[];
    public int faceHslC[];
    public int faceDrawType[];
    public byte[] face_render_priorities;
    public byte face_alpha[];
    public short triangleColours[];
    public byte face_priority = 0;
    public int numberOfTexturesFaces;
    public short textures_face_a[];
    public short textures_face_b[];
    public short textures_face_c[];
    public int min_x;
    public int max_x;
    public int max_z;
    public int min_z;
    public int maxVertexDistanceXZPlane;
    public int max_y;
    public int scene_depth;
    public int diagonal_3D;
    public int itemDropHeight;
    public int vertexVSkin[];
    public int triangleTSkin[];
    public int vertexGroups[][];
    public int faceGroups[][];
    public boolean fits_on_single_square;
    public VertexNormal gouraud_vertex[];
    static ModelHeader aClass21Array1661[];
    static boolean hasAnEdgeToRestrict[] = new boolean[6500];
    static boolean outOfReach[] = new boolean[6500];
    static int projected_vertex_x[] = new int[6500];
    static int projected_vertex_y[] = new int[6500];
    static int projected_vertex_z[] = new int[6500];
    static int anIntArray1668[] = new int[6500];
    static int camera_vertex_y[] = new int[6500];
    static int camera_vertex_x[] = new int[6500];
    static int camera_vertex_z[] = new int[6500];
    static int depthListIndices[] = new int[6000];//1500
    static int faceLists[][] = new int[6000][512];//1500 / 512 //anIntArrayArray3809 //64
    static int anIntArray1673[] = new int[12];
    static int anIntArrayArray1674[][] = new int[12][2000];//1500
    static int anIntArray1676[] = new int[2000];//1500
    static int anIntArray1675[] = new int[2000];//1500
    static int anIntArray1677[] = new int[12];
    static int anIntArray1678[] = new int[10];
    static int anIntArray1679[] = new int[10];
    static int anIntArray1680[] = new int[10];
    static int xAnimOffset;
    static int yAnimOffset;
    static int zAnimOffset;
    public static boolean obj_exists;
    public static int anInt1685;
    public static int anInt1686;
    public static int obj_loaded;
    public static long obj_key[] = new long[1000];
    public static int SINE[];
    public static int COSINE[];
    static int modelIntArray3[];
    static int modelIntArray4[];


    Model() {
        numVertices = 0;
        trianglesCount = 0;
        numberOfTexturesFaces = 0;
        face_priority = 0;
        fits_on_single_square = true;
        this.xMidOffset = -1;
        this.yMidOffset = -1;
        this.zMidOffset = -1;
    }

    static {
        SINE = Rasterizer3D.anIntArray1470;
        COSINE = Rasterizer3D.COSINE;
        modelIntArray3 = Rasterizer3D.hslToRgb;
        modelIntArray4 = Rasterizer3D.anIntArray1469;
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
        return numVertices;
    }

    @Override
    public int[] getVerticesX() {
        return vertexX;
    }

    @Override
    public int[] getVerticesY() {
        return vertexY;
    }

    @Override
    public int[] getVerticesZ() {
        return vertexZ;
    }

    @Override
    public int getFaceCount() {
        return this.trianglesCount;
    }

    @Override
    public int[] getFaceIndices1() {
        return facePointA;
    }

    @Override
    public int[] getFaceIndices2() {
        return facePointB;
    }

    @Override
    public int[] getFaceIndices3() {
        return facePointC;
    }

    @Override
    public int[] getFaceColors1() {
        return this.faceHslA;
    }

    @Override
    public int[] getFaceColors2() {
        return faceHslB;
    }

    @Override
    public int[] getFaceColors3() {
        return faceHslC;
    }

    @Override
    public byte[] getFaceTransparencies() {
        return face_alpha;
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
        calc_diagonals();
    }

    @Override
    public byte[] getFaceRenderPriorities() {
        return this.face_render_priorities;
    }

    @Override
    public int[][] getVertexGroups() {
        return new int[0][];
    }

    @Override
    public int getRadius() {
        return diagonal_3D;
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
        for (int var1 = 0; var1 < this.getVerticesCount(); ++var1)
        {
            int var2 = this.getVerticesX()[var1];
            this.getVerticesX()[var1] = this.getVerticesZ()[var1];
            this.getVerticesZ()[var1] = -var2;
        }

    }

    @Override
    public void rotateY180Ccw() {
        for (int var1 = 0; var1 < this.getVerticesCount(); ++var1)
        {
            this.getVerticesX()[var1] = -this.getVerticesX()[var1];
            this.getVerticesZ()[var1] = -this.getVerticesZ()[var1];
        }

    }

    @Override
    public void rotateY270Ccw() {
        for (int var1 = 0; var1 < this.getVerticesCount(); ++var1)
        {
            int var2 = this.getVerticesZ()[var1];
            this.getVerticesZ()[var1] = this.getVerticesX()[var1];
            this.getVerticesX()[var1] = -var2;
        }
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
        return maxVertexDistanceXZPlane;
    }

    @Override
    public boolean isClickable() {
        return fits_on_single_square;
    }

    @Override
    public void drawFace(int face) {

    }

    @Override
    public void interpolateFrames(RSFrames frames, int frameId, RSFrames nextFrames, int nextFrameId, int interval, int intervalCount) {

    }

    private float[] faceTextureUVCoordinates;
    private int[] vertexNormalsX, vertexNormalsY, vertexNormalsZ;
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
        computeTextureUvCoordinates();
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

        final short[] texTriangleX = textures_face_a;
        final short[] texTriangleY = textures_face_b;
        final short[] texTriangleZ = textures_face_c;

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