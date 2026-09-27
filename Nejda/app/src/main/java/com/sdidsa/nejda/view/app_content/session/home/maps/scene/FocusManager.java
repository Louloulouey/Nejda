package com.sdidsa.nejda.view.app_content.session.home.maps.scene;

import android.content.Context;
import android.graphics.Color;

import com.google.android.filament.Box;
import com.google.android.filament.Colors;
import com.google.android.filament.Engine;
import com.google.android.filament.Material;
import com.google.android.filament.MaterialInstance;
import com.google.android.filament.RenderableManager;
import com.google.android.filament.Scene;
import com.google.android.filament.TransformManager;
import com.google.android.filament.filamat.MaterialBuilder;
import com.google.android.filament.filamat.MaterialPackage;
import com.google.android.filament.gltfio.FilamentAsset;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.style.StyleToColor;
import com.sdidsa.nejda.abs.utils.view.StyleUtils;
import com.sdidsa.nejda.view.app_content.session.home.maps.camera.CameraController;
import com.sdidsa.nejda.view.app_content.session.home.maps.camera.CameraPosition;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class FocusManager {
    private final Context owner;
    private final Scene scene;
    private final Engine engine;
    private final FilamentAsset asset;
    private final CameraController cameraController;
    private boolean autoFocus = true;
    private final HashMap<Integer, ObjectPosition> focusedPositions = new HashMap<>();

    private String dangerZone;
    private StyleToColor dangerZoneColor;

    public FocusManager(Context owner, Scene scene, Engine engine, FilamentAsset asset,
                        CameraController cameraController) {
        this.owner = owner;
        this.scene = scene;
        this.engine = engine;
        this.asset = asset;
        this.cameraController = cameraController;
        initMaterials();
    }

    private final float focusColor = 0.75f;

    public void initMaterials() {
        RenderableManager rm = engine.getRenderableManager();

        Material buildingOpaque = generateMaterial(MaterialBuilder.BlendingMode.OPAQUE, true);
        Material route = generateMaterial(MaterialBuilder.BlendingMode.FADE, false);

        for (int ent : asset.getEntities()) {
            String name = asset.getName(ent);
            if (name.startsWith("seg_") || name.startsWith("node_")) {
                MaterialInstance instance = route.createInstance();
                instance.setParameter("baseColor", Colors.RgbaType.SRGB, 0.4f, 0.8f, 0.4f,
                        0.4f);

                int renderableInstance = rm.getInstance(ent);
                rm.setMaterialInstanceAt(renderableInstance, 0, instance);
            }
        }
        applyMaterial(buildingOpaque);
    }

    public void setDangerZone(String dangerZone, StyleToColor dangerZoneColor) {
        this.dangerZoneColor = dangerZoneColor;
        this.dangerZone = dangerZone;
        clearFocus();
        if(dangerZone == null) return;
        String by = dangerZone.contains(" ") ? " " : dangerZone.contains("_") ? "_" : null;
        if(by != null) {
            String[] parts = dangerZone.split(by);
            this.dangerZone = parts[0].toLowerCase() + "_" +
                    (parts[1].length() == 1 ? "0":"") + parts[1];
        }

        clearFocus();
    }

    public void applyMaterial(Material mat) {
        RenderableManager rm = engine.getRenderableManager();
        for (int ent : asset.getEntities()) {
            String name = asset.getName(ent);
            if (skipMaterial(name)) continue;
            MaterialInstance instance = mat.createInstance();
            instance.setParameter("baseColor", Colors.RgbaType.SRGB,
                    focusColor,
                    focusColor,
                    focusColor,
                    1f);

            int renderableInstance = rm.getInstance(ent);
            rm.setMaterialInstanceAt(renderableInstance, 0, instance);
        }
    }

    private Material generateMaterial(MaterialBuilder.BlendingMode mode, boolean depthCulling) {
        MaterialPackage matPack = new MaterialBuilder()
                .platform(MaterialBuilder.Platform.MOBILE)
                .shading(MaterialBuilder.Shading.LIT)
                .blending(mode)
                .doubleSided(depthCulling)
                .transparencyMode(!depthCulling ?
                        MaterialBuilder.TransparencyMode.TWO_PASSES_ONE_SIDE :
                        MaterialBuilder.TransparencyMode.DEFAULT)
                .depthCulling(depthCulling)
                .uniformParameter(MaterialBuilder.UniformType.FLOAT4, "baseColor")
                .material("""
                        void material(inout MaterialInputs material) {
                            prepareMaterial(material);
                            material.baseColor.rgba = materialParams.baseColor;
                            material.roughness = 1.0;
                            material.metallic = 0.0;
                        }
                        """).build();
        ByteBuffer buffer = matPack.getBuffer();
        Material.Builder builder = new Material.Builder().payload(buffer, buffer.remaining());
        return builder.build(engine);
    }

    public void setAutoFocus(boolean autoFocus) {
        this.autoFocus = autoFocus;
    }

    public void focus(String name) {
        if (skipMaterial(name) && (name == null || !name.endsWith("_keep"))) {
            return;
        }
        name = name
                .replace("_roof", "")
                .replace("_floor", "")
                .replace("_keep", "");
        int entity = asset.getFirstEntityByName(name);
        if (entity != 0) {
            if (focusedPositions.containsKey(entity)) {
                focusedPositions.remove(entity);
            } else {
                focusedPositions.put(entity, getWorldPosition(entity));
            }
            applyFocus();
        }
    }

    public ObjectPosition getWorldPosition(int entity) {
        TransformManager tm = engine.getTransformManager();
        RenderableManager rm = engine.getRenderableManager();
        int renderableInstance = rm.getInstance(entity);

        Box localBox = rm.getAxisAlignedBoundingBox(renderableInstance, null);
        float[] center = localBox.getCenter();
        float[] extent = localBox.getHalfExtent();

        List<float[]> corners = new ArrayList<>();
        for (int x = -1; x <= 1; x += 2) {
            for (int y = -1; y <= 1; y += 2) {
                for (int z = -1; z <= 1; z += 2) {
                    corners.add(new float[]{
                            center[0] + x * extent[0],
                            center[1] + y * extent[1],
                            center[2] + z * extent[2],
                            1.0f
                    });
                }
            }
        }
        float[] matrix = new float[16];
        tm.getWorldTransform(tm.getInstance(entity), matrix);

        float[] min = new float[]{Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE};
        float[] max = new float[]{-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE};

        for (float[] corner : corners) {
            float[] world = multiplyMatrixVec4(matrix, corner); // Result is vec4
            for (int i = 0; i < 3; i++) {
                min[i] = Math.min(min[i], world[i]);
                max[i] = Math.max(max[i], world[i]);
            }
        }
        return new ObjectPosition(min, max);
    }

    public void clearFocus() {
        focusedPositions.clear();
        applyFocus();
    }

    public void applyFocus() {
        ArrayList<Integer> roofs = new ArrayList<>();
        ArrayList<Integer> floors = new ArrayList<>();

        for (int item : focusedPositions.keySet()) {
            String name = asset.getName(item);
            int roof = asset.getFirstEntityByName(name + "_roof");
            if (roof > 0) {
                roofs.add(roof);
            }
            int floor = asset.getFirstEntityByName(name + "_floor");
            if (floor > 0) {
                floors.add(floor);
            }
        }

        RenderableManager rm = engine.getRenderableManager();
        for (int ent : asset.getEntities()) {
            String name = asset.getName(ent);
            if (skipMaterial(name)) continue;
            if (roofs.contains(ent)) {
                scene.removeEntity(ent);
            } else {
                scene.addEntity(ent);
            }
            float a = (focusedPositions.containsKey(ent) || floors.contains(ent)
                    || focusedPositions.isEmpty()) ? 1f : 0.35f;
            int inst = rm.getInstance(ent);
            int count = rm.getPrimitiveCount(inst);

            Color color =  Color.valueOf((dangerZoneColor == null ?
                    Style.EMPTY :
                    dangerZoneColor).get(StyleUtils.getStyle(owner).get()));
            float r = dangerZone != null &&
                    name.toLowerCase().contains(dangerZone.toLowerCase()) ?
                    color.red() : focusColor;
            float g = dangerZone != null &&
                    name.toLowerCase().contains(dangerZone.toLowerCase()) ?
                    color.green() : focusColor;
            float b = dangerZone != null &&
                    name.toLowerCase().contains(dangerZone.toLowerCase()) ?
                    color.blue() : focusColor;

            for (int i = 0; i < count; i++) {
                MaterialInstance materialInstance = rm.getMaterialInstanceAt(inst, i);
                materialInstance.setParameter(
                        "baseColor",       // Parameter name
                        Colors.RgbaType.SRGB,    // Or LINEAR, match your workflow
                        r,                    // R = 1.0 (Neutral)
                        g,                    // G = 1.0 (Neutral)
                        b,                    // B = 1.0 (Neutral)
                        a              // A = Desired opacity
                );
            }
        }
        if (autoFocus) {
            cameraController.animateCameraTo(getFocusZone());
        }
    }

    public List<String> getFocused() {
        return focusedPositions.keySet().stream().map(asset::getName).collect(Collectors.toList());
    }
    public CameraPosition getFocusZone() {
        if (focusedPositions.isEmpty()) {
            return new CameraPosition(0, 0,
                    CameraController.RADIUS,
                    CameraController.ROTATE,
                    CameraController.TILT);
        }

        float minX = 1000;
        float maxX = -1000;
        float minY = 1000;
        float maxY = -1000;

        for (ObjectPosition a : focusedPositions.values()) {
            minX = Math.min(minX, a.getMinX());
            minY = Math.min(minY, a.getMinZ());
            maxX = Math.max(maxX, a.getMaxX());
            maxY = Math.max(maxY, a.getMaxZ());
        }

        float width = maxX - minX;
        float height = maxY - minY;

        float diam = (float) Math.sqrt(width * width + height * height);

        float centerX = (maxX + minX) / 2;
        float centerY = (maxY + minY) / 2;

        float nr = Math.min(4, Math.max(2, diam * 2f));
        return new CameraPosition(centerX, centerY, nr,
                cameraController.getRotate(),
                cameraController.getTilt());
    }

    float[] multiplyMatrixVec4(float[] m, float[] v) {
        return new float[]{
                m[0] * v[0] + m[4] * v[1] + m[8] * v[2] + m[12] * v[3],
                m[1] * v[0] + m[5] * v[1] + m[9] * v[2] + m[13] * v[3],
                m[2] * v[0] + m[6] * v[1] + m[10] * v[2] + m[14] * v[3],
                m[3] * v[0] + m[7] * v[1] + m[11] * v[2] + m[15] * v[3]
        };
    }

    private boolean skipMaterial(String name) {
        return name == null || name.endsWith("keep")
                || name.startsWith("seg_") || name.startsWith("node_");
    }

}
