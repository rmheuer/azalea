package com.github.rmheuer.azalea.math;

public final class AABB {
    public static AABB fromBaseCenterSize(float centerX, float minY, float centerZ, float width, float height, float depth) {
        return new AABB(
                centerX - width/2,
                minY,
                centerZ - depth/2,
                centerX + width/2,
                minY + height,
                centerZ + depth/2
        );
    }

    public final float minX, minY, minZ;
    public final float maxX, maxY, maxZ;

    public AABB(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public AABB translate(float dx, float dy, float dz) {
        return new AABB(minX + dx, minY + dy, minZ + dz, maxX + dx, maxY + dy, maxZ + dz);
    }

    public AABB contract(float x, float y, float z) {
        return new AABB(minX + x, minY + y, minZ + z, maxX - x, maxY - y, maxZ - z);
    }

    public AABB expandTowards(float dx, float dy, float dz) {
        float minX = this.minX;
        float maxX = this.maxX;
        float minY = this.minY;
        float maxY = this.maxY;
        float minZ = this.minZ;
        float maxZ = this.maxZ;

        if (dx > 0)
            maxX += dx;
        else
            minX += dx;

        if (dy > 0)
            maxY += dy;
        else
            minY += dy;

        if (dz > 0)
            maxZ += dz;
        else
            minZ += dz;

        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public float getMin(Axis axis) {
        switch (axis) {
            case X: return minX;
            case Y: return minY;
            case Z: return minZ;
            default: throw new IndexOutOfBoundsException(String.valueOf(axis));
        }
    }

    public float getMax(Axis axis) {
        switch (axis) {
            case X: return maxX;
            case Y: return maxY;
            case Z: return maxZ;
            default: throw new IndexOutOfBoundsException(String.valueOf(axis));
        }
    }

    public boolean intersectsIgnoringAxis(AABB other, Axis ignoredAxis) {
        boolean x = (minX <= other.maxX && maxX >= other.minX);
        boolean y = (minY <= other.maxY && maxY >= other.minY);
        boolean z = (minZ <= other.maxZ && maxZ >= other.minZ);

        switch (ignoredAxis) {
            case X: return y && z;
            case Y: return x && z;
            case Z: return x && y;
            default:
                throw new IndexOutOfBoundsException(String.valueOf(ignoredAxis));
        }
    }
}
