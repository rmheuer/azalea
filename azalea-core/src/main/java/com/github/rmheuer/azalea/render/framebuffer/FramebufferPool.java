package com.github.rmheuer.azalea.render.framebuffer;

import com.github.rmheuer.azalea.render.Renderer;
import com.github.rmheuer.azalea.render.texture.Texture;
import com.github.rmheuer.azalea.render.texture.Texture2D;
import com.github.rmheuer.azalea.utils.SafeCloseable;
import org.joml.Vector2i;

import java.util.*;

public final class FramebufferPool implements SafeCloseable {
    public static final class Params {
        private final Vector2i size;
        private final int colorAttachmentCount;
        private final boolean depthStencilAttachment;

        public Params(Vector2i size, int colorAttachmentCount, boolean depthStencilAttachment) {
            this.size = size;
            this.colorAttachmentCount = colorAttachmentCount;
            this.depthStencilAttachment = depthStencilAttachment;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Params params = (Params) o;
            return size.equals(params.size) &&
                    colorAttachmentCount == params.colorAttachmentCount &&
                    depthStencilAttachment == params.depthStencilAttachment;
        }

        @Override
        public int hashCode() {
            return Objects.hash(size, colorAttachmentCount, depthStencilAttachment);
        }
    }

    private final Renderer renderer;
    private Map<Params, List<Framebuffer>> prevFrame, currFrame;

    public FramebufferPool(Renderer renderer) {
        this.renderer = renderer;
        prevFrame = new HashMap<>();
        currFrame = new HashMap<>();
    }

    public void newFrame() {
        // Close unused framebuffers from last frame
        for (List<Framebuffer> buffers : prevFrame.values()) {
            for (Framebuffer fb : buffers) {
                fb.close();
            }
        }
        prevFrame.clear();

        Map<Params, List<Framebuffer>> temp = prevFrame;
        prevFrame = currFrame;
        currFrame = temp;
    }

    public Framebuffer get(Params params) {
        // TODO: Check if performance is different when reusing framebuffers
        //  within a frame vs. creating more and using each once per frame
        // Currently this does not reuse framebuffers during a frame but will
        // reuse them between frames

        Framebuffer fb;
        List<Framebuffer> prev = prevFrame.get(params);
        if (prev != null && !prev.isEmpty()) {
            // Reuse previous one
            fb = prev.remove(prev.size() - 1);
        } else {
            // Create new one
            FramebufferBuilder builder = renderer.createFramebufferBuilder(params.size.x, params.size.y);
            for (int i = 0; i < params.colorAttachmentCount; i++) {
                Texture2D tex = builder.addColorTexture(i);
                tex.setFilters(Texture.Filter.LINEAR);
            }
            if (params.depthStencilAttachment)
                builder.addDepthStencilAttachment();
            fb = builder.build();
        }

        List<Framebuffer> curr = currFrame.computeIfAbsent(params, (p) -> new ArrayList<>());
        curr.add(fb);
        return fb;
    }

    @Override
    public void close() {
        for (List<Framebuffer> buffers : prevFrame.values()) {
            for (Framebuffer fb : buffers) {
                fb.close();
            }
        }
        for (List<Framebuffer> buffers : currFrame.values()) {
            for (Framebuffer fb : buffers) {
                fb.close();
            }
        }
    }
}
