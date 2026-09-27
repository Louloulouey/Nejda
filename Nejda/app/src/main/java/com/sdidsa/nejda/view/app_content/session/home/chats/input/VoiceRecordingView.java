package com.sdidsa.nejda.view.app_content.session.home.chats.input;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.widget.LinearLayout;

import com.sdidsa.nejda.R;
import com.sdidsa.nejda.abs.components.controls.image.ColoredIcon;
import com.sdidsa.nejda.abs.components.controls.text.ColoredLabel;
import com.sdidsa.nejda.abs.components.controls.text.font.Font;
import com.sdidsa.nejda.abs.components.controls.text.font.FontWeight;
import com.sdidsa.nejda.abs.components.layout.Alignment;
import com.sdidsa.nejda.abs.components.layout.ColoredStackPane;
import com.sdidsa.nejda.abs.components.layout.linear.ColoredHBox;
import com.sdidsa.nejda.abs.components.layout.linear.ColoredVBox;
import com.sdidsa.nejda.abs.components.layout.linear.HBox;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.utils.view.MarginUtils;
import com.sdidsa.nejda.abs.utils.view.SizeUtils;
import com.sdidsa.nejda.abs.utils.view.StyleUtils;

import java.util.ArrayList;
import java.util.List;

public class VoiceRecordingView extends ColoredStackPane {

    private final ColoredVBox waveformContainer;
    private final WaveformView waveformView;
    private final ColoredLabel timerLabel;
    private final ColoredIcon stopButton;
    private final ColoredIcon sendRecordingButton;
    
    private OnRecordingActionListener listener;

    public VoiceRecordingView(Context context) {
        super(context, Style.BACK_PRI);
        
        setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        setCornerRadius(28);
        setPadding(16, 16, 16, 16);
        setAlpha(0);
        setTranslationY(100);
        
        HBox recordingControlsLayout = new ColoredHBox(context);
        recordingControlsLayout.setAlignment(Alignment.CENTER);
        recordingControlsLayout.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        
        waveformContainer = new ColoredVBox(context, Style.EMPTY);
        waveformContainer.setLayoutParams(new LinearLayout.LayoutParams(0, SizeUtils.dipToPx(40, context), 1));
        waveformContainer.setCornerRadius(8);
        
        // Create and add waveform view to container
        waveformView = new WaveformView(context);
        waveformView.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        waveformContainer.addView(waveformView);
        
        timerLabel = new ColoredLabel(context, Style.TEXT_NORM, "00:00");
        timerLabel.setFont(new Font(16, FontWeight.MEDIUM));
        
        stopButton = new ColoredIcon(context, Style.TEXT_ERR, R.drawable.close, 48);
        stopButton.setImagePadding(14);
        
        sendRecordingButton = new ColoredIcon(context, Style.ACCENT, R.drawable.check, 48);
        sendRecordingButton.setImagePadding(13);

        MarginUtils.setMarginLeft(waveformContainer, context, 8);
        MarginUtils.setMarginLeft(timerLabel, context, 16);
        MarginUtils.setMarginLeft(stopButton, context, 16);
        
        recordingControlsLayout.addViews(waveformContainer, timerLabel, stopButton, sendRecordingButton);
        addCentered(recordingControlsLayout);
        
        setupListeners();
    }
    
    private void setupListeners() {
        stopButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onStopRecording();
            }
        });
        
        sendRecordingButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSendRecording();
            }
        });
    }
    
    public void updateTimer(String time) {
        timerLabel.setKey(time);
    }
    
    public void updateWaveform(int amplitude) {
        if (waveformView != null) {
            waveformView.addAmplitude(amplitude);
        }
    }
    
    public void resetWaveform() {
        if (waveformView != null) {
            waveformView.reset();
        }
    }
    
    public void setOnRecordingActionListener(OnRecordingActionListener listener) {
        this.listener = listener;
    }
    
    public interface OnRecordingActionListener {
        void onStopRecording();
        void onSendRecording();
    }
    
    /**
     * Custom view that renders the audio waveform
     */
    private static class WaveformView extends View {
        private final Paint paint = new Paint();
        private final List<Float> amplitudes = new ArrayList<>();
        private final int maxAmplitudes = 50; // Maximum number of bars to show
        private final float barWidth = 3; // Width of each bar in dp
        private final float spacing = 1; // Spacing between bars in dp
        private final int primaryColor;
        
        public WaveformView(Context context) {
            super(context);
            paint.setAntiAlias(true);
            primaryColor = StyleUtils.getStyle(context).get().getAccent();
            paint.setColor(primaryColor);
        }
        
        public void addAmplitude(int amplitude) {
            float normalizedAmplitude = Math.min(1.0f, Math.max(0.1f, amplitude / 32767.0f));
            
            amplitudes.add(normalizedAmplitude);
            if (amplitudes.size() > maxAmplitudes) {
                amplitudes.remove(0);
            }
            invalidate();
        }
        
        public void reset() {
            amplitudes.clear();
            invalidate();
        }
        
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            
            if (amplitudes.isEmpty()) return;
            
            float centerY = getHeight() / 2f;
            float barWidthPx = SizeUtils.dipToPx(barWidth, getContext());
            float spacingPx = SizeUtils.dipToPx(spacing, getContext());
            float rightPadding = SizeUtils.dipToPx(4, getContext());
            
            // Start drawing from right to left (newest amplitudes on the right)
            float x = getWidth() - rightPadding;
            
            for (int i = amplitudes.size() - 1; i >= 0; i--) {
                float amplitude = amplitudes.get(i);
                float barHeight = getHeight() * amplitude * 0.8f; // 80% of height max
                
                // Draw bar centered vertically
                canvas.drawRect(
                        x - barWidthPx,
                        centerY - barHeight / 2,
                        x,
                        centerY + barHeight / 2,
                        paint
                );
                
                x -= (barWidthPx + spacingPx);
                if (x < 0) break; // Stop if we're off the left edge
            }
        }
    }
} 