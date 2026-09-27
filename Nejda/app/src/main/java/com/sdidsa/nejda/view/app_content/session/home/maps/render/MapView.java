package com.sdidsa.nejda.view.app_content.session.home.maps.render;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.Choreographer;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.TextureView;

import androidx.annotation.NonNull;

import com.google.android.filament.ColorGrading;
import com.google.android.filament.Engine;
import com.google.android.filament.Filament;
import com.google.android.filament.IndirectLight;
import com.google.android.filament.Renderer;
import com.google.android.filament.Scene;
import com.google.android.filament.ToneMapper;
import com.google.android.filament.View;
import com.google.android.filament.android.UiHelper;
import com.google.android.filament.filamat.MaterialBuilder;
import com.google.android.filament.gltfio.FilamentAsset;
import com.google.android.filament.gltfio.Gltfio;
import com.google.android.filament.utils.AutomationEngine;
import com.google.android.filament.utils.Float3;
import com.google.android.filament.utils.KTX1Loader;
import com.google.android.filament.utils.Utils;
import com.sdidsa.nejda.abs.components.layout.StackPane;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.style.StyleToColor;
import com.sdidsa.nejda.abs.style.Styleable;
import com.sdidsa.nejda.abs.utils.ErrorHandler;
import com.sdidsa.nejda.abs.utils.view.ContextUtils;
import com.sdidsa.nejda.abs.utils.view.StyleUtils;
import com.sdidsa.nejda.view.app_content.session.home.maps.camera.CameraController;
import com.sdidsa.nejda.view.app_content.session.home.maps.routes.RouteFinder;
import com.sdidsa.nejda.view.app_content.session.home.maps.scene.FocusManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class MapView extends StackPane implements Styleable {

    static {
        Filament.init();
        Gltfio.init();
        Utils.INSTANCE.init();
        MaterialBuilder.init();
    }

    private final TextureView surfaceView;
    private final Choreographer choreographer;

    private final ModelViewer modelViewer;
    private final FrameCallback frameScheduler = new FrameCallback();
    private final GestureDetector doubleTapDetector;
    private final GestureDetector singleTapDetector;
    private final AutomationEngine automation = new AutomationEngine();

    private final Handler mHandler;

    private final CameraController camController;
    private final FocusManager focusManager;
    private final RouteFinder routeFinder;

    @SuppressWarnings("ClickableViewAccessibility")
    public MapView(Context owner) {
        super(owner);

        mHandler = new Handler(Objects.requireNonNull(Looper.myLooper()));

        surfaceView = new TextureView(owner);

        choreographer = Choreographer.getInstance();

        DoubleTapListener doubleTapListener = new DoubleTapListener();
        doubleTapDetector = new GestureDetector(owner, doubleTapListener);
        SingleTapListener singleTapListener = new SingleTapListener();
        singleTapDetector = new GestureDetector(owner, singleTapListener);

        UiHelper uiHelper = new UiHelper(UiHelper.ContextErrorPolicy.DONT_CHECK);

        modelViewer = new ModelViewer(
                surfaceView,
                Engine.create(),
                uiHelper);

        createDefaultRenderables();
        createIndirectLight();

        camController = new CameraController(modelViewer.getView().getCamera());

        focusManager = new FocusManager(
                owner,
                modelViewer.getScene(),
                modelViewer.getEngine(),
                modelViewer.getAsset(),
                camController);

        routeFinder = new RouteFinder(
                modelViewer.getScene(),
                modelViewer.getEngine(),
                modelViewer.getAsset()
        );

        addCentered(surfaceView);

        surfaceView.setOnTouchListener((v, event) -> {
            doubleTapDetector.onTouchEvent(event);
            singleTapDetector.onTouchEvent(event);

            camController.onTouch(event);

            return true;
        });

        View view = modelViewer.getView();
        view.setBlendMode(View.BlendMode.OPAQUE);

        view.setColorGrading(
                new ColorGrading.Builder()
                        .whiteBalance(0, 0)
                        .saturation(1)
                        .vibrance(1)
                        .exposure(0)
                        .gamutMapping(false)
                        .luminanceScaling(false)
                        .format(ColorGrading.LutFormat.FLOAT)
                        .toneMapper(new ToneMapper.Linear())
                        .quality(ColorGrading.QualityLevel.MEDIUM)
                        .build(modelViewer.getEngine()));

        view.setPostProcessingEnabled(false);

        View.RenderQuality quality = view.getRenderQuality();
        quality.hdrColorBuffer = View.QualityLevel.MEDIUM;
        view.setRenderQuality(quality);

        View.DynamicResolutionOptions resolutionOptions = view.getDynamicResolutionOptions();
        resolutionOptions.enabled = true;
        resolutionOptions.sharpness = 1.0f;
        resolutionOptions.quality = View.QualityLevel.MEDIUM;
        view.setDynamicResolutionOptions(resolutionOptions);

        View.MultiSampleAntiAliasingOptions msaa = view.getMultiSampleAntiAliasingOptions();
        msaa.enabled = true;
        view.setMultiSampleAntiAliasingOptions(msaa);

        view.setAntiAliasing(View.AntiAliasing.FXAA);

        View.AmbientOcclusionOptions aoo = view.getAmbientOcclusionOptions();
        aoo.enabled = true;
        aoo.quality = View.QualityLevel.MEDIUM;
        aoo.radius = 0.05f;
        aoo.resolution = 1.0f;
        aoo.intensity = 0.5f;
        view.setAmbientOcclusionOptions(aoo);

        applyStyle(StyleUtils.getStyle(owner));
    }

    public void clearPath() {
        routeFinder.clearPath();
    }

    public void findPath() {
        List<String> focused = focusManager.getFocused();
        if(focused.size() != 2) {
            ContextUtils.toast(owner, "2 locations must be selected");
            ContextUtils.toast(owner, "you have " + focused.size() + " selected");
            routeFinder.clearPath();
            return;
        }
        routeFinder.visualizePathByBuilding(focused.get(0), focused.get(1));
    }

    public void setAutoFocus(boolean autoFocus) {
        focusManager.setAutoFocus(autoFocus);
        if(autoFocus) {
            focusManager.applyFocus();
        }
    }

    private void createDefaultRenderables() {
        javaLet(readCompressedAsset("models/ntic_mesh.glb"), d -> {
            modelViewer.loadModelGlb(d);
            updateRootTransform();
        });
    }

    private void createIndirectLight() {
        Engine engine = modelViewer.getEngine();
        Scene scene = modelViewer.getScene();
        String env_name = "default_env";

        String assetName = "envs/" + env_name;
        javaLet(readCompressedAsset(assetName + "_ibl.ktx"), d -> {
            IndirectLight indirect = KTX1Loader.INSTANCE
                    .createIndirectLight(engine, d, new KTX1Loader.Options());
            indirect.setIntensity(40000);
            scene.setIndirectLight(indirect);
        });
    }

    private <T> void javaLet(T data, Consumer<T> func) {
        if (data != null)
            func.accept(data);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private ByteBuffer readCompressedAsset(String assetName) {
        try (InputStream input = getResources().getAssets()
                .open(assetName)) {
            byte[] bytes = new byte[input.available()];
            input.read(bytes);
            return ByteBuffer.wrap(bytes);
        } catch (IOException x) {
            ErrorHandler.handle(x, "load " + assetName);
            return null;
        }
    }

    boolean isRendering = false;
    public void startRendering() {
        if(isRendering) return;
        isRendering = true;
        choreographer.postFrameCallback(frameScheduler);
    }

    public void stopRendering() {
        if(!isRendering) return;
        isRendering = false;
        choreographer.removeFrameCallback(frameScheduler);
    }

    public void onDestroy() {
        choreographer.removeFrameCallback(frameScheduler);
        modelViewer.destroy();
    }

    private void updateRootTransform() {
        if (automation.getViewerOptions().autoScaleEnabled) {
            modelViewer.transformToUnitCube(new Float3(0.0f, 0.0f, 0.0f));

        } else {
            modelViewer.clearRootTransform();
        }
    }

    @Override
    public void applyStyle(Style style) {
        Renderer.ClearOptions cop = new Renderer.ClearOptions();
        Color c = Color.valueOf(style.getBackgroundPrimary());
        cop.clearColor = new float[]{c.red(), c.green(), c.blue(), c.alpha()};
        cop.clear = true;
        modelViewer.getRenderer().setClearOptions(cop);
    }

    public void setDanger(String location, StyleToColor color) {
        focusManager.setDangerZone(location, color);
        routeFinder.setDanger(true);
    }

    public void clearDanger() {
        focusManager.setDangerZone(null, null);
        routeFinder.setDanger(false);
    }

    class FrameCallback implements Choreographer.FrameCallback {

        @Override
        public void doFrame(long frameTimeNanos) {
            choreographer.postFrameCallback(this);
            camController.lookAt();
            modelViewer.render(frameTimeNanos);
        }
    }

    class DoubleTapListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(@NonNull MotionEvent event) {
            focusManager.clearFocus();
            routeFinder.clearPath();
            return super.onDoubleTap(event);
        }
    }

    class SingleTapListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            modelViewer.getView().pick(
                    (int) event.getX(),
                    surfaceView.getHeight() - (int) event.getY(),
                    mHandler,
                    r -> {
                        FilamentAsset asset = modelViewer.getAsset();
                        if (asset != null) {
                            String name = asset.getName(r.renderable);
                            focusManager.focus(name);
                            routeFinder.clearPath();
                        }
                    }
            );
            return super.onSingleTapUp(event);
        }
    }
}
