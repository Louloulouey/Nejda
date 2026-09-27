package com.sdidsa.nejda.view.app_content.session.home.maps.scene;

import com.google.android.filament.utils.Float3;

public class ObjectPosition {
    private final float minX;
    private final float minY;
    private final float minZ;
    private final float maxX;
    private final float maxY;
    private final float maxZ;
    private final float centerX;
    private final float centerY;
    private final float centerZ;

    public ObjectPosition(float minX, float minY, float minZ,
                          float maxX, float maxY, float maxZ,
                          float centerX, float centerY, float centerZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        this.centerX = centerX;
        this.centerY = centerY;
        this.centerZ = centerZ;
    }

    public ObjectPosition(Float3 min, Float3 max, Float3 center) {
        this.minX = min.getX();
        this.minY = min.getY();
        this.minZ = min.getZ();

        this.maxX = max.getX();
        this.maxY = max.getY();
        this.maxZ = max.getZ();

        this.centerX = center.getX();
        this.centerY = center.getY();
        this.centerZ = center.getZ();
    }

    public ObjectPosition(float[] min, float[] max, float[] center) {
        this.minX = min[0];
        this.minY = min[1];
        this.minZ = min[2];

        this.maxX = max[0];
        this.maxY = max[1];
        this.maxZ = max[2];

        this.centerX = center[0];
        this.centerY = center[1];
        this.centerZ = center[2];
    }

    public ObjectPosition(Float3 min, Float3 max) {
        this.minX = min.getX();
        this.minY = min.getY();
        this.minZ = min.getZ();

        this.maxX = max.getX();
        this.maxY = max.getY();
        this.maxZ = max.getZ();

        this.centerX = (maxX + minX) / 2;
        this.centerY = (maxY + minY) / 2;
        this.centerZ = (maxZ + minZ) / 2;
    }

    public ObjectPosition(float[] min, float[] max) {
        this.minX = min[0];
        this.minY = min[1];
        this.minZ = min[2];

        this.maxX = max[0];
        this.maxY = max[1];
        this.maxZ = max[2];

        this.centerX = (maxX + minX) / 2;
        this.centerY = (maxY + minY) / 2;
        this.centerZ = (maxZ + minZ) / 2;
    }

    public float getMinX() {
        return minX;
    }

    public float getMinY() {
        return minY;
    }

    public float getMinZ() {
        return minZ;
    }

    public float getMaxX() {
        return maxX;
    }

    public float getMaxY() {
        return maxY;
    }

    public float getMaxZ() {
        return maxZ;
    }

    public float getCenterX() {
        return centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public float getCenterZ() {
        return centerZ;
    }

    public float[] getCenter() {
        return new float[]{centerX, centerY, centerZ};
    }
}
