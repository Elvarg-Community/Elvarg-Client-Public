package com.runescape.entity.model;

public final class ModelHeader
{

    public ModelHeader()
    {
    }

    public byte data[];
    public int vertices;
    public int faces;
    public int texture_faces;
    public int vertex_offset;
    public int vertex_x_offset;
    public int vertex_y_offset;
    public int vertex_z_offset;
    public int bones_offset;
    public int points_offset;
    public int face_offset;
    public int color_id;
    public int render_type_offset;
    public int face_pri_offset;
    public int alpha_offset;
    public int muscle_offset;
    public int texture_id;
}