package com.tanishisherewith.dynamichud.internal;

import org.joml.Vector4f;

public interface IRenderLayer {
    /**
     * Set uniform u to the value described by v4f, formatted as a vec4 (4 floats)
     * @param u Name
     * @param v4f Value
     */
    default void dynamichud$setUniform(String u, Vector4f v4f) {
        float[] v = new float[]{v4f.x, v4f.y, v4f.z, v4f.w};
        dynamichud$setUniform(u, v);
    }

    /**
     * Set uniform u to the value of v, should be an int[], float[] or Matrix4f
     * @param u Name
     * @param v Value
     */
    void dynamichud$setUniform(String u, Object v);
}