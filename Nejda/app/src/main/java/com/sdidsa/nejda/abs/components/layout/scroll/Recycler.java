package com.sdidsa.nejda.abs.components.layout.scroll;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.sdidsa.nejda.abs.utils.ErrorHandler;
import com.sdidsa.nejda.abs.utils.view.MarginUtils;
import com.sdidsa.nejda.abs.utils.view.SizeUtils;
import com.sdidsa.nejda.abs.utils.view.SpacerUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import me.everything.android.ui.overscroll.IOverScrollDecor;
import me.everything.android.ui.overscroll.IOverScrollState;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class Recycler<U, V extends RecyclerItemView<U>> extends ColoredStackPane {
    private static final float max_dist = 100;
    private RecyclerView sv;
    private float DOWN_PX;
    private Consumer<Float> onMaybeRefresh;
    private Runnable onRefresh;
    private Label refreshLabel;

    private Class<V> viewType;

    private Property<Float> refreshGestureProgress;

    private ArrayList<U> itemList;
    private RecyclerView.Adapter<RecyclerViewHolder<U, V>> adapter;

    public Recycler(Context context) {
        super(context);
    }

    public Recycler(Context context, Class<V> viewType) {
        super(context);
        this.viewType = viewType;
        sv = new RecyclerView(context) {
            float initX, initY;

            @Override
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                return switch (ev.getAction()) {
                    case MotionEvent.ACTION_DOWN -> {
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

        refreshLabel = new ColoredLabel(context, Style.TEXT_NORM, "Release to refresh")
                .setFont(new Font(20));

        setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        setFill(Style.EMPTY);

        super.addView(sv, 0);

        MarginUtils.setMarginTop(refreshLabel, context, 30);

        DOWN_PX = SizeUtils.dipToPx(50, context);

        SpacerUtils.spacer(this, Orientation.VERTICAL);
        sv.setVerticalScrollBarEnabled(false);
        sv.setClipChildren(false);

        setScrollContainer(true);
        setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);

        sv.setLayoutManager(new LinearLayoutManager(getOwner(), LinearLayoutManager.VERTICAL, false));

        itemList = new ArrayList<>();

        adapter = new RecyclerView.Adapter<>() {
            @NonNull
            @Override
            public RecyclerViewHolder<U, V> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                try {
                    V view = Recycler.this.viewType.getConstructor(Context.class).newInstance(owner);
                    return new RecyclerViewHolder<>(view);
                } catch (NoSuchMethodException | IllegalAccessException | InstantiationException |
                         InvocationTargetException e) {
                    ErrorHandler.handle(e, "create recycler item view");
                    return null;
                }
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerViewHolder<U, V> holder, int position) {
                holder.load(itemList.get(position));
            }

            @Override
            public int getItemCount() {
                return itemList.size();
            }
        };
        sv.setAdapter(adapter);

        IOverScrollDecor d = OverScrollDecoratorHelper.setUpOverScroll(sv,
                OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        d.setOverScrollUpdateListener((decor, state, offset) -> {
            if (offset < 0 || state != IOverScrollState.STATE_DRAG_START_SIDE) return;
            offset = Math.min(offset, max_dist);
            if (running != null && running.isRunning()) running.stop();
            stockMaybeRefresh(offset / max_dist);
        });
        d.setOverScrollStateListener((decor, oldState, newState) -> {
            if (oldState == IOverScrollState.STATE_DRAG_START_SIDE &&
                    newState == IOverScrollState.STATE_BOUNCE_BACK) {
                if (refreshGestureProgress.get() >= 1) {
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

    public ArrayList<U> getItemList() {
        return itemList;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addItems(List<U> items) {
        itemList.addAll(items);
        adapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addItem(U item) {
        itemList.add(item);
        adapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<U> items) {
        itemList.clear();
        addItems(items);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void removeItem(U pending) {
        itemList.remove(pending);
        adapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clearItems() {
        itemList.clear();
        adapter.notifyDataSetChanged();
    }

    @SuppressWarnings("unchecked")
    public V getViewFor(U item) {
        RecyclerView.ViewHolder holder = sv.findViewHolderForAdapterPosition(itemList.indexOf(item));
        assert holder != null;
        return (V) holder.itemView;
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
            refreshLabel.setText(dist < 1 ? "drag to refresh" : "release to refresh");
            if (dist <= 0) {
                removeView(refreshLabel);
            }
        }
    }

    private void stockRefresh() {
        if (running != null && running.isRunning()) running.stop();
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

    public void setOnRefreshGesture(Consumer<Float> onMaybeRefresh) {
        this.onMaybeRefresh = onMaybeRefresh;
    }

    public void smoothScrollTo(int p) {
        sv.smoothScrollToPosition(p);
    }

    public void smoothScrollToBottom() {
        if(itemList.isEmpty()) return;
        sv.smoothScrollToPosition(itemList.size() - 1);
    }

    public void scrollToBottom() {
        if(itemList.isEmpty()) return;
        sv.scrollToPosition(itemList.size() - 1);
    }

    public void scrollTo(int x, int y) {
        sv.scrollTo(x, y);
    }

    public void setOnRefresh(Runnable onRefresh) {
        this.onRefresh = onRefresh;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
