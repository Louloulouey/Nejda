package com.sdidsa.nejda.view.app_content.session.home.maps.render;

import android.view.Surface;
import android.view.TextureView;

import com.google.android.filament.Camera;
import com.google.android.filament.Colors;
import com.google.android.filament.Engine;
import com.google.android.filament.Entity;
import com.google.android.filament.EntityManager;
import com.google.android.filament.Fence;
import com.google.android.filament.LightManager;
import com.google.android.filament.RenderableManager;
import com.google.android.filament.Renderer;
import com.google.android.filament.Scene;
import com.google.android.filament.SwapChain;
import com.google.android.filament.TransformManager;
import com.google.android.filament.View;
import com.google.android.filament.Viewport;
import com.google.android.filament.android.DisplayHelper;
import com.google.android.filament.android.UiHelper;
import com.google.android.filament.gltfio.AssetLoader;
import com.google.android.filament.gltfio.FilamentAsset;
import com.google.android.filament.gltfio.MaterialProvider;
import com.google.android.filament.gltfio.ResourceLoader;
import com.google.android.filament.gltfio.UbershaderProvider;
import com.google.android.filament.utils.Float3;
import com.google.android.filament.utils.Mat4;
import com.google.android.filament.utils.MatrixKt;
import com.google.android.filament.utils.VectorKt;
import com.sdidsa.nejda.abs.utils.ErrorHandler;

import java.nio.Buffer;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;


class ModelViewer {
    private static final float kNearPlane = 0.05f;
    private static final float kFarPlane = 1000.0f;
    private static final float kAperture = 16f;
    private static final float kShutterSpeed = 1f / 125f;
    private static final float kSensitivity = 100f;

    private final Engine engine;
    private final UiHelper uiHelper;
    private FilamentAsset asset;

    public Engine getEngine() {
        return engine;
    }

    public FilamentAsset getAsset() {
        return asset;
    }

    public Scene getScene() {
        return scene;
    }

    public View getView() {
        return view;
    }

    public Renderer getRenderer() {
        return renderer;
    }

    private Scene scene;
    private View view;
    private Camera camera;
    private Renderer renderer;

    @Entity
    private int light;

    private final DisplayHelper displayHelper;
    private final TextureView textureView;

    private SwapChain swapChain;
    private AssetLoader assetLoader;
    private MaterialProvider materialProvider;
    private ResourceLoader resourceLoader;
    private final int[] readyRenderables = new int[128];

    private void init() {
        renderer = engine.createRenderer();
        scene = engine.createScene();
        camera = javaLet(engine.createCamera(engine.getEntityManager().create()), cam -> {
            cam.setExposure(kAperture, kShutterSpeed, kSensitivity);
        });
        view = engine.createView();
        view.setScene(scene);
        view.setCamera(camera);

        materialProvider = new UbershaderProvider(engine);
        assetLoader = new AssetLoader(engine, materialProvider, EntityManager.get());
        boolean normalizeSkinningWeights = true;
        resourceLoader = new ResourceLoader(engine, normalizeSkinningWeights);

        light = EntityManager.get().create();

        float[] rgb = Colors.cct(6_500.0f);
        new LightManager.Builder(LightManager.Type.DIRECTIONAL)
                .color(rgb[0], rgb[1], rgb[2])
                .intensity(40000)
                .direction(-0.5f, -1.0f, -0.5f)
                .castShadows(true)
                .build(engine, light);

        scene.addEntity(light);
    }

    public ModelViewer(TextureView textureView, Engine engine, UiHelper uiHelper) {
        this.engine = engine != null ? engine : Engine.create();
        this.uiHelper = uiHelper != null ? uiHelper : new UiHelper(UiHelper.ContextErrorPolicy.DONT_CHECK);

        init();

        this.textureView = textureView;
        displayHelper = new DisplayHelper(textureView.getContext());
        this.uiHelper.setRenderCallback(new SurfaceCallback());
        this.uiHelper.attachTo(textureView);
        //addDetachListener
    }

    public void loadModelGlb(Buffer buffer) {
        destroyModel();
        asset = assetLoader.createAsset(buffer);
        javaLet(asset, ass -> {
            resourceLoader.asyncBeginLoad(asset);
            asset.releaseSourceData();
        });
    }

    public void transformToUnitCube(Float3 centerPoint) {
        javaLet(asset, ass -> {
            TransformManager tm = engine.getTransformManager();
            Float3 center = javaLet(ass.getBoundingBox().getCenter(), this::arrToFloat3);
            Float3 halfExtent = javaLet(ass.getBoundingBox().getHalfExtent(), this::arrToFloat3);
            float maxExtent = 2.0f * VectorKt.max(halfExtent);
            float scaleFactor = 2.0f / maxExtent;
            center = center.minus(centerPoint.div(scaleFactor));

            Mat4 transform = MatrixKt.scale(new Float3(scaleFactor))
                    .times(MatrixKt.translation(center.unaryMinus()));

            tm.setTransform(tm.getInstance(asset.getRoot()), MatrixKt.transpose(transform).toFloatArray());
        });
    }

    public void clearRootTransform() {
        javaLet(asset, ass -> {
            TransformManager tm = engine.getTransformManager();
            tm.setTransform(tm.getInstance(ass.getRoot()), new Mat4().toFloatArray());
        });
    }

    public void destroyModel() {
        resourceLoader.asyncCancelLoad();
        resourceLoader.evictResourceData();
        javaLet(asset, ass -> {
            scene.removeEntities(ass.getEntities());
            assetLoader.destroyAsset(asset);
            this.asset = null;
        });
    }

    public void render(long frameTimeNanos) {
        if (!uiHelper.isReadyToRender() || !engine.isValidSwapChain(swapChain)) {
            return;
        }

        try {
            resourceLoader.asyncUpdateLoad();

            javaLet(asset, this::populateScene);

            if (swapChain != null && renderer.beginFrame(swapChain, frameTimeNanos)) {
                renderer.render(view);
                renderer.endFrame();
            }
        } catch (Exception x) {
            ErrorHandler.handle(x, "rendering");
        }
    }

    public Camera getCamera() {
        return camera;
    }

    private void populateScene(FilamentAsset asset) {
        RenderableManager rcm = engine.getRenderableManager();
        var count = 0;
        while ((count = asset.popRenderables(readyRenderables)) != 0) {
            for (int item : readyRenderables) {
                int ri = rcm.getInstance(item);
                rcm.setScreenSpaceContactShadows(ri, true);
            }
            scene.addEntities(Arrays.copyOf(readyRenderables, count));
        }
        scene.addEntities(asset.getLightEntities());
    }

    private void updateCameraProjection() {
        int width = view.getViewport().width;
        int height = view.getViewport().height;
        double aspect = (double) width / height;
        camera.setLensProjection(28f, aspect, kNearPlane, kFarPlane);
    }

    class SurfaceCallback implements UiHelper.RendererCallback {
        @Override
        public void onNativeWindowChanged(Surface surface) {
            engine.flushAndWait();
            javaLet(swapChain, engine::destroySwapChain);
            swapChain = engine.createSwapChain(surface);
            javaLet(textureView, sv -> {
                displayHelper.attach(renderer, sv.getDisplay());
            });
        }

        @Override
        public void onDetachedFromSurface() {
            engine.flushAndWait();
            displayHelper.detach();
            javaLet(swapChain, swc -> {
                engine.destroySwapChain(swc);
                engine.flushAndWait();
                swapChain = null;
            });
        }

        @Override
        public void onResized(int width, int height) {
            view.setViewport(new Viewport(0, 0, width, height));
            updateCameraProjection();
            synchronizePendingFrames(engine);
        }
    }

    private void synchronizePendingFrames(Engine engine) {
        Fence fence = engine.createFence();
        fence.wait(Fence.Mode.FLUSH, Fence.WAIT_FOR_EVER);
        engine.destroyFence(fence);
    }

    public void destroy() {
        uiHelper.detach();

        destroyModel();
        assetLoader.destroy();
        materialProvider.destroyMaterials();
        materialProvider.destroy();
        resourceLoader.destroy();

        engine.destroyEntity(light);
        engine.destroyRenderer(renderer);
        engine.destroyView(view);
        engine.destroyScene(scene);
        engine.destroyCameraComponent(camera.getEntity());
        EntityManager.get().destroy(camera.getEntity());

        EntityManager.get().destroy(light);

        engine.destroy();
    }

    private Float3 arrToFloat3(float[] arr) {
        return new Float3(arr[0], arr[1], arr[2]);
    }

    private <U> U javaLet(U data, Consumer<U> func) {
        if (data == null) return null;
        func.accept(data);
        return data;
    }

    private <U, V> V javaLet(U data, Function<U, V> func) {
        if (data == null) return null;
        return func.apply(data);
    }
}