package com.github.rmheuer.azalea.voxel.render;

import com.github.rmheuer.azalea.render.Renderer;
import com.github.rmheuer.azalea.render.mesh.*;
import com.github.rmheuer.azalea.render.pipeline.ActivePipeline;
import com.github.rmheuer.azalea.render.utils.SharedIndexBuffer;
import com.github.rmheuer.azalea.utils.SafeCloseable;
import com.github.rmheuer.azalea.voxel.level.LevelListener;
import com.github.rmheuer.azalea.voxel.level.VoxelLevel;
import org.joml.FrustumIntersection;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;
import org.joml.Vector3i;

import java.util.*;

public abstract class VoxelLevelRenderer<B> implements SafeCloseable {
    private static final class SectionData implements SafeCloseable {
        private final VertexBuffer buffer;
        private int elementCount;
        private boolean dirty;

        public SectionData(Renderer renderer) {
            buffer = renderer.createVertexBuffer();
            elementCount = 0;
            dirty = true;
        }

        @Override
        public void close() {
            buffer.close();
        }
    }

    private final class LevelData implements LevelListener<B>, SafeCloseable {
        private final VoxelLevel<? extends B> level;
        private final Map<Vector3i, SectionData> sections;

        private final Vector3i changePos = new Vector3i();

        public LevelData(VoxelLevel<? extends B> level) {
            this.level = level;
            sections = new HashMap<>();

            level.addLevelListener(this);
        }

        private void markDirty(int sectionX, int sectionY, int sectionZ) {
            changePos.set(sectionX, sectionY, sectionZ);
            SectionData sectionData = sections.get(changePos);
            if (sectionData != null)
                sectionData.dirty = true;
        }

        @Override
        public void blockChanged(int x, int y, int z, B prevBlock, B newBlock) {
            int sectionX = Math.floorDiv(x, sectionSize);
            int sectionY = Math.floorDiv(y, sectionSize);
            int sectionZ = Math.floorDiv(z, sectionSize);
            int relX = Math.floorMod(x, sectionSize);
            int relY = Math.floorMod(y, sectionSize);
            int relZ = Math.floorMod(z, sectionSize);

            boolean faceNX = relX == 0;
            boolean facePX = relX == sectionSize - 1;
            boolean faceNY = relY == 0;
            boolean facePY = relY == sectionSize - 1;
            boolean faceNZ = relZ == 0;
            boolean facePZ = relZ == sectionSize - 1;

            markDirty(sectionX, sectionY, sectionZ);

            // These checks could probably be done much more efficiently

            // Check faces
            if (faceNX) markDirty(sectionX - 1, sectionY, sectionZ);
            if (facePX) markDirty(sectionX + 1, sectionY, sectionZ);
            if (faceNY) markDirty(sectionX, sectionY - 1, sectionZ);
            if (facePY) markDirty(sectionX, sectionY + 1, sectionZ);
            if (faceNZ) markDirty(sectionX, sectionY, sectionZ - 1);
            if (facePZ) markDirty(sectionX, sectionY, sectionZ + 1);

            // Check edges
            if (neighborUpdateRule.includesEdges()) {
                if (faceNX && faceNY) markDirty(sectionX - 1, sectionY - 1, sectionZ);
                if (faceNX && facePY) markDirty(sectionX - 1, sectionY + 1, sectionZ);
                if (facePX && faceNY) markDirty(sectionX + 1, sectionY - 1, sectionZ);
                if (facePX && facePY) markDirty(sectionX + 1, sectionY + 1, sectionZ);
                if (faceNX && faceNZ) markDirty(sectionX - 1, sectionY, sectionZ - 1);
                if (faceNX && facePZ) markDirty(sectionX - 1, sectionY, sectionZ + 1);
                if (facePX && faceNZ) markDirty(sectionX + 1, sectionY, sectionZ - 1);
                if (facePX && facePZ) markDirty(sectionX + 1, sectionY, sectionZ + 1);
                if (faceNY && faceNZ) markDirty(sectionX, sectionY - 1, sectionZ - 1);
                if (faceNY && facePZ) markDirty(sectionX, sectionY - 1, sectionZ + 1);
                if (facePY && faceNZ) markDirty(sectionX, sectionY + 1, sectionZ - 1);
                if (facePY && facePZ) markDirty(sectionX, sectionY + 1, sectionZ + 1);
            }

            // Check vertices
            if (neighborUpdateRule.includesVertices()) {
                if (faceNX && faceNY && faceNZ) markDirty(sectionX - 1, sectionY - 1, sectionZ - 1);
                if (faceNX && faceNY && facePZ) markDirty(sectionX - 1, sectionY - 1, sectionZ + 1);
                if (faceNX && facePY && faceNZ) markDirty(sectionX - 1, sectionY + 1, sectionZ - 1);
                if (faceNX && facePY && facePZ) markDirty(sectionX - 1, sectionY + 1, sectionZ + 1);
                if (facePX && faceNY && faceNZ) markDirty(sectionX + 1, sectionY - 1, sectionZ - 1);
                if (facePX && faceNY && facePZ) markDirty(sectionX + 1, sectionY - 1, sectionZ + 1);
                if (facePX && facePY && faceNZ) markDirty(sectionX + 1, sectionY + 1, sectionZ - 1);
                if (facePX && facePY && facePZ) markDirty(sectionX + 1, sectionY + 1, sectionZ + 1);
            }
        }

        public SectionData getSection(Vector3i pos) {
            return sections.get(pos);
        }

        public SectionData getOrCreateSection(Vector3i pos) {
            return sections.computeIfAbsent(pos, (p) -> new SectionData(renderer));
        }

        public void clearMeshData() {
            for (SectionData section : sections.values()) {
                section.close();
            }
            sections.clear();
        }

        @Override
        public void close() {
            level.removeLevelListener(this);
            for (SectionData section : sections.values()) {
                section.close();
            }
        }
    }

    private final Renderer renderer;
    private final VertexLayout vertexLayout;
    private final SharedIndexBuffer sharedIndexBuffer;

    private final int sectionSize;
    private LevelData levelData;

    private NeighborUpdateRule neighborUpdateRule;
    private long maxRemeshMillis;

    private final FrustumIntersection frustum;
    private final List<Vector3i> toRender;
    private final Set<Vector3i> toRemove;

    public VoxelLevelRenderer(Renderer renderer, VertexLayout vertexLayout, int sectionSize) {
        this.renderer = renderer;
        this.vertexLayout = vertexLayout;

        // Worst case for simple cube voxels, with entire section filled with
        // checkerboard pattern.
        int initialCapacity = sectionSize * sectionSize * sectionSize * 3;
        sharedIndexBuffer = new SharedIndexBuffer(
                renderer,
                PrimitiveType.TRIANGLES,
                4,
                0, 1, 2, 0, 2, 3
        );

        this.sectionSize = sectionSize;
        levelData = null;

        neighborUpdateRule = NeighborUpdateRule.FACES;
        maxRemeshMillis = 5;

        frustum = new FrustumIntersection();
        toRender = new ArrayList<>();
        toRemove = new HashSet<>();
    }

    protected abstract void meshBlock(
            B block,
            int x, int y, int z,
            VoxelLevel<? extends B> level,
            VertexData dataOut
    );

    protected void renderSections(
            Vector3fc cameraPos,
            Matrix4fc viewProjectionMatrix,
            Collection<Vector3i> sectionPositions,
            ActivePipeline renderPipeline
    ) {
        if (levelData == null)
            return;

        // Remove non-visible sections
        toRemove.clear();
        toRemove.addAll(levelData.sections.keySet());
        toRemove.removeAll(sectionPositions);
        for (Vector3i invisible : toRemove) {
            SectionData section = levelData.sections.remove(invisible);
            if (section != null)
                section.close();
        }

        // Only render if visible by camera
        toRender.clear();
        frustum.set(viewProjectionMatrix, false);
        for (Vector3i position : sectionPositions) {
            int blockX = position.x * sectionSize;
            int blockY = position.y * sectionSize;
            int blockZ = position.z * sectionSize;
            if (frustum.testAab(blockX, blockY, blockZ, blockX + sectionSize, blockY + sectionSize, blockZ + sectionSize)) {
                toRender.add(position);
            }
        }

        // Minimize overdraw
        toRender.sort(Comparator.comparingDouble((pos) -> {
            int halfSz = sectionSize / 2;
            return cameraPos.distanceSquared(
                    pos.x * sectionSize + halfSz,
                    pos.y * sectionSize + halfSz,
                    pos.z * sectionSize + halfSz
            );
        }));

        // Re-mesh sections that aren't up to date
        long startTime = System.currentTimeMillis();
        for (Vector3i pos : toRender) {
            SectionData section = levelData.getOrCreateSection(pos);
            if (!section.dirty)
                continue;

            remeshSection(pos.x, pos.y, pos.z, section);

            if (System.currentTimeMillis() - startTime > maxRemeshMillis)
                break;
        }

        // Render sections
        for (Vector3i pos : toRender) {
            SectionData section = levelData.getSection(pos);
            if (section != null && section.elementCount > 0) {
                renderPipeline.draw(section.buffer, sharedIndexBuffer.getIndexBuffer(), 0, section.elementCount);
            }
        }
    }

    private VertexData createSectionMesh(int originX, int originY, int originZ) {
        VertexData data = new VertexData(vertexLayout);

        for (int y = originY; y < originY + sectionSize; y++) {
            for (int z = originZ; z < originZ + sectionSize; z++) {
                for (int x = originX; x < originX + sectionSize; x++) {
                    B block = levelData.level.getBlock(x, y, z);

                    meshBlock(block, x, y, z, levelData.level, data);
                }
            }
        }

        return data;
    }

    private void remeshSection(int x, int y, int z, SectionData section) {
        try (VertexData data = createSectionMesh(x * sectionSize, y * sectionSize, z * sectionSize)) {
            section.buffer.setData(data, DataUsage.DYNAMIC);

            int faceCount = data.getVertexCount() / 4;
            section.elementCount = faceCount * 6;
            sharedIndexBuffer.ensureCapacity(faceCount);
        }
        section.dirty = false;
    }

    public void setLevel(VoxelLevel<? extends B> level) {
        if (levelData != null) {
            if (levelData.level == level)
                return;

            levelData.close();
        }

        if (level == null)
            levelData = null;
        else
            levelData = new LevelData(level);
    }

    public void clearMeshData() {
        levelData.clearMeshData();
    }

    public void setNeighborUpdateRule(NeighborUpdateRule neighborUpdateRule) {
        this.neighborUpdateRule = neighborUpdateRule;
    }

    public void setMaxRemeshMillis(long maxRemeshMillis) {
        this.maxRemeshMillis = maxRemeshMillis;
    }

    @Override
    public void close() {
        if (levelData != null)
            levelData.close();
    }
}
