package com.github.rmheuer.azalea.math;

import org.joml.Vector3f;

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

    public static AABB fromCenterSize(float centerX, float centerY, float centerZ, float width, float height, float depth) {
        float halfW = width / 2;
        float halfH = height / 2;
        float halfD = depth / 2;
        return new AABB(
                centerX - halfW,
                centerY - halfH,
                centerZ - halfD,
                centerX + halfW,
                centerY + halfH,
                centerZ + halfD
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

    public boolean intersects(AABB other) {
        boolean x = (minX < other.maxX && maxX > other.minX);
        boolean y = (minY < other.maxY && maxY > other.minY);
        boolean z = (minZ < other.maxZ && maxZ > other.minZ);

        return x && y && z;
    }

    public boolean intersectsIgnoringAxis(AABB other, Axis ignoredAxis) {
        boolean x = (minX < other.maxX && maxX > other.minX);
        boolean y = (minY < other.maxY && maxY > other.minY);
        boolean z = (minZ < other.maxZ && maxZ > other.minZ);

        switch (ignoredAxis) {
            case X: return y && z;
            case Y: return x && z;
            case Z: return x && y;
            default:
                throw new IndexOutOfBoundsException(String.valueOf(ignoredAxis));
        }
    }

    public float collideAlongAxis(AABB other, Axis axis, float movement) {
        if (!intersectsIgnoringAxis(other, axis))
            return movement;

        float min = getMin(axis);
        float max = getMax(axis);
        float otherMin = other.getMin(axis);
        float otherMax = other.getMax(axis);
        if (movement > 0 && otherMin + Math.ulp(otherMin) >= max) {
            movement = Math.min(movement, otherMin - max);
        }
        if (movement < 0 && otherMax - Math.ulp(otherMax) <= min) {
            movement = Math.max(movement, otherMax - min);
        }

        return movement;
    }

    public static final class RayIntersection {
        public final float hitDist;
        public final Vector3f hitPos;
        public final CubeFace hitFace;

        public RayIntersection(float hitDist, Vector3f hitPos, CubeFace hitFace) {
            this.hitDist = hitDist;
            this.hitPos = hitPos;
            this.hitFace = hitFace;
        }
    }

    public RayIntersection intersectRay(Vector3f origin, Vector3f dir) {
        float tx1 = (minX - origin.x) / dir.x;
        float tx2 = (maxX - origin.x) / dir.x;
        float txMin = Math.min(tx1, tx2);
        float txMax = Math.max(tx1, tx2);

        float ty1 = (minY - origin.y) / dir.y;
        float ty2 = (maxY - origin.y) / dir.y;
        float tyMin = Math.min(ty1, ty2);
        float tyMax = Math.max(ty1, ty2);

        float tz1 = (minZ - origin.z) / dir.z;
        float tz2 = (maxZ - origin.z) / dir.z;
        float tzMin = Math.min(tz1, tz2);
        float tzMax = Math.max(tz1, tz2);

        CubeFace face;
        float tMin;
        if (txMin > tyMin && txMin > tzMin) {
            tMin = txMin;
            face = dir.x < 0 ? CubeFace.POS_X : CubeFace.NEG_X;
        } else if (tyMin > txMin && tyMin > tzMin) {
            tMin = tyMin;
            face = dir.y < 0 ? CubeFace.POS_Y : CubeFace.NEG_Y;
        } else {
            tMin = tzMin;
            face = dir.z < 0 ? CubeFace.POS_Z : CubeFace.NEG_Z;
        }

        float tMax = Math.min(Math.min(txMax, tyMax), tzMax);

        if (tMax > Math.max(tMin, 0)) {
            return new RayIntersection(tMin, new Vector3f(origin).fma(tMin, dir), face);
        } else {
            return null;
        }
    }
}
