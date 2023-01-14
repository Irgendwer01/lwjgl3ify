package org.lwjglx.opengl;

public class EXTVertexAttrib64bit {
    public static void glGetVertexAttribLEXT(int index, int pname, java.nio.DoubleBuffer params) {
        org.lwjgl.opengl.EXTVertexAttrib64bit.glGetVertexAttribLdvEXT(index, pname, params);
    }

    public static void glVertexArrayVertexAttribLOffsetEXT(
            int vaobj, int buffer, int index, int size, int type, int stride, long offset) {
        org.lwjgl.opengl.EXTVertexAttrib64bit.glVertexArrayVertexAttribLOffsetEXT(
                vaobj, buffer, index, size, type, stride, offset);
    }

    public static void glVertexAttribL1EXT(int index, java.nio.DoubleBuffer v) {
        org.lwjgl.opengl.EXTVertexAttrib64bit.glVertexAttribL1dvEXT(index, v);
    }

    public static void glVertexAttribL1dEXT(int index, double x) {
        org.lwjgl.opengl.EXTVertexAttrib64bit.glVertexAttribL1dEXT(index, x);
    }

    public static void glVertexAttribL2EXT(int index, java.nio.DoubleBuffer v) {
        org.lwjgl.opengl.EXTVertexAttrib64bit.glVertexAttribL2dvEXT(index, v);
    }

    public static void glVertexAttribL2dEXT(int index, double x, double y) {
        org.lwjgl.opengl.EXTVertexAttrib64bit.glVertexAttribL2dEXT(index, x, y);
    }

    public static void glVertexAttribL3EXT(int index, java.nio.DoubleBuffer v) {
        org.lwjgl.opengl.EXTVertexAttrib64bit.glVertexAttribL3dvEXT(index, v);
    }

    public static void glVertexAttribL3dEXT(int index, double x, double y, double z) {
        org.lwjgl.opengl.EXTVertexAttrib64bit.glVertexAttribL3dEXT(index, x, y, z);
    }

    public static void glVertexAttribL4EXT(int index, java.nio.DoubleBuffer v) {
        org.lwjgl.opengl.EXTVertexAttrib64bit.glVertexAttribL4dvEXT(index, v);
    }

    public static void glVertexAttribL4dEXT(int index, double x, double y, double z, double w) {
        org.lwjgl.opengl.EXTVertexAttrib64bit.glVertexAttribL4dEXT(index, x, y, z, w);
    }

    public static void glVertexAttribLPointerEXT(int index, int size, int stride, java.nio.DoubleBuffer pointer) {
        org.lwjgl.opengl.EXTVertexAttrib64bit.glVertexAttribLPointerEXT(index, size, stride, pointer);
    }
}
