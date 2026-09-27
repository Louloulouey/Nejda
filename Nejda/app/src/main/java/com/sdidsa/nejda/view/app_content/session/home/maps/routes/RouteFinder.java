package com.sdidsa.nejda.view.app_content.session.home.maps.routes;

import android.util.Log;

import com.google.android.filament.Box;
import com.google.android.filament.Engine;
import com.google.android.filament.RenderableManager;
import com.google.android.filament.Scene;
import com.google.android.filament.TransformManager;
import com.google.android.filament.gltfio.FilamentAsset;
import com.sdidsa.nejda.view.app_content.session.home.maps.scene.ObjectPosition;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;

public class RouteFinder {
    private final Scene scene;
    private final Engine engine;
    private final FilamentAsset asset;
    private final Map<String, ObjectPosition> nodePositions = new HashMap<>();
    private final Map<String, List<String>> graph = new HashMap<>();
    private final Map<String, List<String>> graph_danger = new HashMap<>();
    private final Map<String, String> location_nodes = new HashMap<>();

    private boolean danger = false;

    public RouteFinder(Scene scene, Engine engine, FilamentAsset asset) {
        this.scene = scene;
        this.engine = engine;
        this.asset = asset;

        loadNodes();
        loadEdges();
        loadBuildings();
    }

    public void setDanger(boolean danger) {
        this.danger = danger;
    }

    public void clearPath() {
        for (int entity : asset.getEntities()) {
            String name = asset.getName(entity);
            if(name.startsWith("node_") || name.startsWith("seg_")) {
                scene.removeEntity(entity);
            }
        }
    }

    public void visualizePathByBuilding(String start, String end) {
        visualizePath(location_nodes.get(start), location_nodes.get(end));
    }

    public void visualizePath(String start, String end) {
        for (int entity : asset.getEntities()) {
            String name = asset.getName(entity);
            if(name.startsWith("node_") || name.startsWith("seg_")) {
                scene.removeEntity(entity);
            }
        }

        List<String> path = findPath(start, end, danger);
        for (String nodeName : path) {
            ObjectPosition pos = nodePositions.get(nodeName);
            if (pos != null) {
                scene.addEntity(asset.getFirstEntityByName(nodeName));
            }
        }

        for (int i = 0; i < path.size() - 1; i++) {
            String from = path.get(i);
            String to = path.get(i + 1);
            String edge = "seg_" + from.split("_")[1] + "_" + to.split("_")[1];
            int entity = asset.getFirstEntityByName(edge);
            if(entity == 0) {
                edge = "seg_" + to.split("_")[1] + "_" + from.split("_")[1];
                entity = asset.getFirstEntityByName(edge);
            }
            if (entity != 0) {
                scene.addEntity(entity);
            }
        }
    }

    public List<String> findPathByBuilding(String start, String end, boolean danger) {
        if (!location_nodes.containsKey(start) || !location_nodes.containsKey(end)) {
            Log.e("RouteFinder", "Start or end node does not exist.");
            return new ArrayList<>();
        }
        return findPath(location_nodes.get(start), location_nodes.get(end), danger);
    }

    public List<String> findPath(String start, String end, boolean danger) {
        if (!graph.containsKey(start) || !graph.containsKey(end)) {
            Log.e("RouteFinder", "Start or end node does not exist.");
            return new ArrayList<>();
        }

        Map<String, Float> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();

        Map<String, List<String>> graphToUse = danger ? graph_danger : graph;
        for (String node : graphToUse.keySet()) {
            distances.put(node, Float.MAX_VALUE);
        }
        distances.put(start, 0f);

        PriorityQueue<String> queue = new PriorityQueue<>(Comparator.comparing(distances::get));
        queue.add(start);

        Set<String> visited = new HashSet<>();

        while (!queue.isEmpty()) {
            String current = queue.poll();

            if (visited.contains(current)) continue;
            visited.add(current);

            if (current.equals(end)) break;

            for (String neighbor : graphToUse.get(current)) {
                float alt = distances.get(current) + distance(nodePositions.get(current), nodePositions.get(neighbor));
                if (alt < distances.get(neighbor)) {
                    distances.put(neighbor, alt);
                    previous.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        List<String> path = new LinkedList<>();
        for (String at = end; at != null; at = previous.get(at)) {
            path.add(0, at);
        }

        if (!path.isEmpty() && path.get(0).equals(start)) {
            return path;
        } else {
            Log.e("RouteFinder", "No path found from " + start + " to " + end);
            return new ArrayList<>();
        }
    }

    private float distance(ObjectPosition a, ObjectPosition b) {
        float dx = a.getCenterX() - b.getCenterX();
        float dy = a.getCenterY() - b.getCenterY();
        float dz = a.getCenterZ() - b.getCenterZ();
        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    private void loadBuildings() {
        for (int entity : asset.getEntities()) {
            String name = asset.getName(entity);
            if(name.endsWith("_roof")) {
                name = name.replace("_roof", "");
                ObjectPosition pos = getWorldPosition(entity);

                float minDist = 1000;
                String minNode = null;
                for(Map.Entry<String, ObjectPosition> nodePosition : nodePositions.entrySet()) {
                    float dist = distance(nodePosition.getValue(), pos);
                    if(dist < minDist) {
                        minDist = dist;
                        minNode = nodePosition.getKey();
                    }
                }
                if(minNode != null) {
                    location_nodes.put(name, minNode);
                }
            }
        }
    }

    private void loadNodes() {
        for (int entity : asset.getEntities()) {
            String name = asset.getName(entity);
            if (name != null && name.startsWith("node_")) {
                nodePositions.put(name, getWorldPosition(entity));
                graph.put(name, new ArrayList<>());
                graph_danger.put(name, new ArrayList<>());
            }
        }
    }

    private void loadEdges() {
        for (int entity : asset.getEntities()) {
            String name = asset.getName(entity);
            if (name != null && name.startsWith("seg_")) {
                String[] parts = name.split("_");
                if (parts.length == 3) {
                    String nodeA = "node_" + parts[1];
                    String nodeB = "node_" + parts[2];

                    if (nodePositions.containsKey(nodeA) && nodePositions.containsKey(nodeB)) {
                        Objects.requireNonNull(graph_danger.get(nodeA)).add(nodeB);
                        Objects.requireNonNull(graph_danger.get(nodeB)).add(nodeA);
                        if(!name.contains("danger")) {
                            Objects.requireNonNull(graph.get(nodeA)).add(nodeB);
                            Objects.requireNonNull(graph.get(nodeB)).add(nodeA);
                        }
                    }
                }
            }
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

    float[] multiplyMatrixVec4(float[] m, float[] v) {
        return new float[]{
                m[0] * v[0] + m[4] * v[1] + m[8] * v[2] + m[12] * v[3],
                m[1] * v[0] + m[5] * v[1] + m[9] * v[2] + m[13] * v[3],
                m[2] * v[0] + m[6] * v[1] + m[10] * v[2] + m[14] * v[3],
                m[3] * v[0] + m[7] * v[1] + m[11] * v[2] + m[15] * v[3]
        };
    }
}
