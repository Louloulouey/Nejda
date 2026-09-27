package com.sdidsa.nejda.abs.components.layout.scroll;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ScrollView;

import com.sdidsa.nejda.abs.animation.base.Animation;
import com.sdidsa.nejda.abs.animation.base.ValueAnimation;
import com.sdidsa.nejda.abs.animation.combine.ParallelAnimation;
import com.sdidsa.nejda.abs.animation.easing.Interpolator;
import com.sdidsa.nejda.abs.animation.view.AlphaAnimation;
import com.sdidsa.nejda.abs.animation.view.position.TranslateYAnimation;
import com.sdidsa.nejda.abs.components.controls.scratches.Orientation;
import com.sdidsa.nejda.abs.components.controls.text.ColoredLabel;
import com.sdidsa.nejda.abs.components.controls.text.Label;
import com.sdidsa.nejda.abs.components.controls.text.font.Font;
import com.sdidsa.nejda.abs.components.layout.Alignment;
import com.sdidsa.nejda.abs.components.layout.ColoredStackPane;
import com.sdidsa.nejda.abs.data.property.Property;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.utils.view.AlignUtils;
import com.sdidsa.nejda.abs.utils.view.MarginUtils;
import com.sdidsa.nejda.abs.utils.view.SizeUtils;
import com.sdidsa.nejda.abs.utils.view.SpacerUtils;

import java.util.function.Consumer;

import me.everything.android.ui.overscroll.IOverScrollDecor;
import me.everything.android.ui.overscroll.IOverScrollState;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class Scroller extends ColoredStackPane {
    private static final float max_dist = 150;
    private final android.widget.ScrollView sv;
    private final float DOWN_PX;
    private Consumer<Float> onMaybeRefresh;
    private Runnable onRefresh;
    private final Label refreshLabel;

    private final Property<Float> refreshGestureProgress;

    public Scroller(Context context) {
        super(context);
        sv = new android.widget.ScrollView(context) {
            float initX, initY;
            @Override
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                return switch (ev.getAction()) {
                    case MotionEvent.ACTION_DOWN ->
                    {
                        initX = ev.getRawX();
                        initY = ev.getRawY();
                        yield super.onInterceptTouchEvent(ev);
                    }
                    case MotionEvent.ACTION_MOVE -> {
                        float x = ev.getRawX();
                        float y = ev.getRawY();
                        float dx = x - initX;
                        float dy = y - initY;
                        float distance = (float) Math.sqrt(dx * dx + dy * dy);
                        yield distance > 20;
                    }
                    default -> false;
                };
            }

            @Override
            public boolean onTouchEvent(MotionEvent event) {
                performClick();
                return super.onTouchEvent(event);
            }

            @Override
            public boolean performClick() {
                return super.performClick();
            }
        };
        refreshGestureProgress = new Property<>(0f);

        refreshLabel = new ColoredLabel(context, Style.TEXT_NORM, "refresh_release")
                .setFont(new Font(20));

        setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        setFill(Style.EMPTY);


        super.addView(sv, 0);

        MarginUtils.setMarginTop(refreshLabel, context, 30);

        sv.setFillViewport(true);

        DOWN_PX = SizeUtils.dipToPx(50, context);

        SpacerUtils.spacer(this, Orientation.VERTICAL);
        sv.setVerticalScrollBarEnabled(false);
        sv.setClipChildren(false);

        setScrollContainer(true);
        setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);

        IOverScrollDecor d = OverScrollDecoratorHelper.setUpOverScroll(sv);
        d.setOverScrollUpdateListener((decor, state, offset) -> {
            if(offset < 0 || state != IOverScrollState.STATE_DRAG_START_SIDE) return;
            offset = Math.min(offset, max_dist);
            if(running != null && running.isRunning()) running.stop();
            stockMaybeRefresh(offset / max_dist);
        });
        d.setOverScrollStateListener((decor, oldState, newState) -> {
            if(oldState == IOverScrollState.STATE_DRAG_START_SIDE &&
                    newState == IOverScrollState.STATE_BOUNCE_BACK) {
                if(refreshGestureProgress.get() >= 1) {
                    stockRefresh();
                } else {
                    running = new ValueAnimation(300, refreshGestureProgress.get(), 0) {
                        @Override
                        public void updateValue(float v) {
                            stockMaybeRefresh(v);
                        }
                    }.setInterpolator(Interpolator.EASE_OUT)
                            .start();
                }
            }
        });
    }

    private Animation running;

    private void stockMaybeRefresh(float dist) {
        refreshGestureProgress.set(dist);
        if (onRefresh != null) {
            if (onMaybeRefresh != null) onMaybeRefresh.accept(dist);

            removeView(refreshLabel);
            addAligned(refreshLabel, Alignment.TOP_CENTER);
            refreshLabel.setAlpha(dist < 1 ? dist / 2 : 1);
            refreshLabel.setTranslationY(DOWN_PX * (dist * 0.5f) - DOWN_PX);
            sv.setAlpha(1 - (dist * .5f));
            refreshLabel.setKey(dist < 1 ? "refresh_drag" : "refresh_release");
            if (dist <= 0) {
                removeView(refreshLabel);
            }
        }
    }

    private void stockRefresh() {
        if(running != null && running.isRunning()) running.stop();
        refreshGestureProgress.set(0f);
        if (onRefresh != null) {
            onRefresh.run();
            running = new ParallelAnimation(300)
                    .addAnimation(new AlphaAnimation(sv, 1))
                    .addAnimation(new TranslateYAnimation(refreshLabel, -DOWN_PX))
                    .addAnimation(new AlphaAnimation(refreshLabel, 0))
                    .setInterpolator(Interpolator.EASE_OUT)
                    .setOnFinished(() -> super.removeView(refreshLabel))
                    .start();
        }
    }

    @Override
    public void setOnScrollChangeListener(OnScrollChangeListener l) {
        sv.setOnScrollChangeListener(l);
    }

    public void setContent(View content) {
        sv.removeAllViews();
        sv.addView(content);
    }

    public void setContent(View content, Alignment alignment) {
        sv.removeAllViews();
        sv.addView(content);
        AlignUtils.alignInFrame(content, alignment);
    }

    public ScrollView getSv() {
        return sv;
    }

    public void setOnRefreshGesture(Consumer<Float> onMaybeRefresh) {
        this.onMaybeRefresh = onMaybeRefresh;
    }

    public void smoothScrollTo(int x, int y) {
        sv.smoothScrollTo(x, y);
    }

    public void setOnRefresh(Runnable onRefresh) {
        this.onRefresh = onRefresh;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
