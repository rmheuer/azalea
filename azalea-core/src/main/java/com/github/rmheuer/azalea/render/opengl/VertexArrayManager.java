package com.github.rmheuer.azalea.render.opengl;

import com.github.rmheuer.azalea.render.mesh.AttribType;
import com.github.rmheuer.azalea.render.mesh.VertexLayout;
import com.github.rmheuer.azalea.utils.SafeCloseable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import static org.lwjgl.opengl.GL33C.*;

public final class VertexArrayManager implements SafeCloseable {
    private static final class DrawBuffers {
        final int vbo, ibo;

        public DrawBuffers(int vbo, int ibo) {
            this.vbo = vbo;
            this.ibo = ibo;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DrawBuffers that = (DrawBuffers) o;
            return vbo == that.vbo && ibo == that.ibo;
        }

        @Override
        public int hashCode() {
            return Objects.hash(vbo, ibo);
        }
    }

    public static final class VertexArray {
        public final int id;
        private VertexLayout currentLayout;
        private int boundIbo;

        public VertexArray() {
            id = glGenVertexArrays();
            currentLayout = null;
            boundIbo = 0;
        }
    }

    private final GLStateManager state;
    private VertexArray boundVao;
    private VertexArray dummy;
    private final Map<DrawBuffers, VertexArray> cache;

    public VertexArrayManager(GLStateManager state) {
        this.state = state;
        boundVao = null;
        dummy = null;
        cache = new HashMap<>();
    }

    private void bindVao(VertexArray vao) {
        if (boundVao == null || boundVao.id != vao.id) {
            glBindVertexArray(vao.id);
            boundVao = vao;
        }
    }

    public void bindForIndexUpload(int ibo) {
        if (boundVao == null) {
            if (dummy == null)
                dummy = new VertexArray();

            bindVao(dummy);
        }

        if (boundVao.boundIbo != ibo && ibo != 0) {
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
            boundVao.boundIbo = ibo;
        }
    }

    public void bindForDrawing(int vbo, int ibo, VertexLayout layout) {
        VertexArray vao = cache.computeIfAbsent(new DrawBuffers(vbo, ibo), (b) -> {
            if (dummy != null) {
                VertexArray v = dummy;
                dummy = null;
                return v;
            } else {
                return new VertexArray();
            }
        });

        bindVao(vao);
        if (vao.currentLayout != layout) {
            applyLayout(vbo, layout, vao.currentLayout);
            vao.currentLayout = layout;
        }
        if (vao.boundIbo != ibo && ibo != 0) {
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
            vao.boundIbo = ibo;
        }
    }

    private void applyLayout(int vbo, VertexLayout layout, VertexLayout prevLayout) {
        AttribType[] attribs = layout.getTypes();
        int stride = layout.sizeOf();

        int offset = 0;
        state.bindArrayBuffer(vbo);
        for (int i = 0; i < attribs.length; i++) {
            AttribType type = attribs[i];
            glVertexAttribPointer(i,
                    type.getElemCount(),
                    type.getValueType() == AttribType.ValueType.FLOAT ? GL_FLOAT : GL_UNSIGNED_BYTE,
                    type.isNormalized(),
                    stride,
                    offset);
            glEnableVertexAttribArray(i);
            offset += type.sizeOf();
        }

        if (prevLayout != null) {
            // Disable any attribs that are no longer used
            for (int i = attribs.length; i < prevLayout.getTypes().length; i++) {
                glDisableVertexAttribArray(i);
            }
        }
    }

    private void deleteVao(VertexArray vao) {
        glDeleteVertexArrays(vao.id);
        if (boundVao != null && boundVao.id == vao.id) {
            boundVao = null;
        }
    }

    public void vertexBufferDeleted(int vbo) {
        for (Iterator<Map.Entry<DrawBuffers, VertexArray>> iter = cache.entrySet().iterator(); iter.hasNext();) {
            Map.Entry<DrawBuffers, VertexArray> entry = iter.next();
            if (entry.getKey().vbo == vbo) {
                deleteVao(entry.getValue());
                iter.remove();
            }
        }
    }

    public void indexBufferDeleted(int ibo) {
        if (dummy != null && dummy.boundIbo == ibo)
            dummy.boundIbo = 0;

        for (Iterator<Map.Entry<DrawBuffers, VertexArray>> iter = cache.entrySet().iterator(); iter.hasNext();) {
            Map.Entry<DrawBuffers, VertexArray> entry = iter.next();
            if (entry.getKey().ibo == ibo) {
                deleteVao(entry.getValue());
                iter.remove();
            }
        }
    }

    @Override
    public void close() {
        if (dummy != null)
            deleteVao(dummy);

        for (VertexArray vao : cache.values()) {
            deleteVao(vao);
        }
    }
}
