package com.sdidsa.nejda.view.app_content.session.home.chats.display;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.ViewGroup;

import com.sdidsa.nejda.R;
import com.sdidsa.nejda.abs.components.controls.audio.AudioFile;
import com.sdidsa.nejda.abs.components.controls.audio.AudioProcessing;
import com.sdidsa.nejda.abs.components.controls.audio.AudioProxy;
import com.sdidsa.nejda.abs.components.controls.image.ColoredIcon;
import com.sdidsa.nejda.abs.components.controls.text.ColoredLabel;
import com.sdidsa.nejda.abs.components.controls.text.font.Font;
import com.sdidsa.nejda.abs.components.layout.Alignment;
import com.sdidsa.nejda.abs.components.layout.ColoredStackPane;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.utils.ErrorHandler;
import com.sdidsa.nejda.abs.utils.view.SizeUtils;
import com.sdidsa.nejda.abs.utils.view.StyleUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

/**
 * A square-shaped view for displaying audio attachments in chat messages.
 * Matches the dimensions and appearance of image attachments.
 */
public class AudioAttachmentView extends ColoredStackPane {
    private final ColoredIcon playIcon;
    private final ColoredLabel durationLabel;
    private final String audioUrl;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private final Handler handler = new Handler();
    private final WaveformView waveformView;
    private float progress = 0f;
    private final Random random = new Random();
    private final float[] waveformData = new float[40];

    private final Runnable updateProgress = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration();
                progress = (float) currentPosition / duration;
                updateDurationText(currentPosition, duration);
                waveformView.invalidate();
                handler.postDelayed(this, 50);
            }
        }
    };

    public AudioAttachmentView(Context context, String audioUrl) {
        super(context);
        this.audioUrl = audioUrl;

        setFill(Style.BACK_TER);
        setCornerRadius(10);
        int size = SizeUtils.dipToPx(MessageItem.ATTACHMENT_SIZE, context);
        setLayoutParams(new ViewGroup.LayoutParams(size, size));

        playIcon = new ColoredIcon(context, Style.TEXT_NORM, R.drawable.play, 48);
        playIcon.setPadding(10);

        durationLabel = new ColoredLabel(context, Style.TEXT_SEC, "");
        durationLabel.setFont(new Font(10));
        durationLabel.setPadding(10);

        waveformView = new WaveformView(context);
        waveformView.setLayoutParams(new ViewGroup.LayoutParams(
                -1, -1
        ));

        // Initialize with default values until analysis is complete
        Arrays.fill(waveformData, 0f);

        // Add components to the view
        addAligned(waveformView, Alignment.BOTTOM_CENTER);
        addAligned(playIcon, Alignment.CENTER);
        addAligned(durationLabel, Alignment.TOP_CENTER);

        // Set up click listener
        setOnClickListener(v -> togglePlayback());

        setClipToOutline(true);
    }

    private AudioFile audioFile;

    private void analyzeAudioFile() {
        if (audioFile == null) {
            return;
        }

        try {
            // Decode audio to PCM data
            byte[] pcmData = AudioProcessing.decodeAACToPCM(audioFile.file().getAbsolutePath());

            // Calculate number of samples we want to analyze
            int numSamples = waveformData.length;
            int samplesPerSegment = pcmData.length / (2 * numSamples); // Divide by 2 because each sample is 2 bytes

            // Analyze each segment
            for (int i = 0; i < numSamples; i++) {
                float maxAmplitude = 0;
                int startIndex = i * samplesPerSegment * 2;

                // Analyze this segment of the audio
                for (int j = 0; j < samplesPerSegment && startIndex + 1 < pcmData.length; j++) {
                    short sample = (short) ((pcmData[startIndex + 1] << 8) | (pcmData[startIndex] & 0xFF));
                    float amplitude = Math.abs(sample) / 32768.0f;
                    maxAmplitude = Math.max(maxAmplitude, amplitude);
                    startIndex += 2;
                }

                // Store the normalized amplitude
                waveformData[i] = maxAmplitude;
            }

            // Redraw the waveform
            invalidate();

        } catch (Exception e) {
            // If analysis fails, use random data as fallback
            for (int i = 0; i < waveformData.length; i++) {
                waveformData[i] = 0.2f + (random.nextFloat() * 0.3f);
            }
            invalidate();
        }
    }

    private void togglePlayback() {
        if (isPlaying) {
            pauseAudio();
        } else {
            playAudio();
        }
    }

    private void playAudio() {
        AudioProxy.getAudio(audioUrl, audio -> {
            this.audioFile = audio;
            analyzeAudioFile();
            try {
                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(audio.file().getAbsolutePath());
                    mediaPlayer.prepare();
                    mediaPlayer.setOnCompletionListener(mp -> {
                        isPlaying = false;
                        playIcon.setImageResource(R.drawable.play);
                        mediaPlayer.seekTo(0);
                        updateDurationText(0, mediaPlayer.getDuration());
                        handler.removeCallbacks(updateProgress);
                    });
                    mediaPlayer.setOnPreparedListener(mp -> {
                        updateDurationText(0, mediaPlayer.getDuration());
                    });
                }

                mediaPlayer.start();
                isPlaying = true;
                playIcon.setImageResource(R.drawable.pause);
                handler.post(updateProgress);

            } catch (IOException e) {
                ErrorHandler.handle(e, "playing audio attachment");
            }
        });

    }

    private void pauseAudio() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
            playIcon.setImageResource(R.drawable.play);
            handler.removeCallbacks(updateProgress);
        }
    }

    private void updateDurationText(int currentMs, int totalMs) {
        int currentSec = currentMs / 1000;
        int totalSec = totalMs / 1000;

        String currentFormatted = String.format(Locale.getDefault(),
                "%d:%02d", currentSec / 60, currentSec % 60);
        String totalFormatted = String.format(Locale.getDefault(),
                "%d:%02d", totalSec / 60, totalSec % 60);

        durationLabel.setKey(currentFormatted + " / " + totalFormatted);
    }

    public void release() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
            handler.removeCallbacks(updateProgress);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        release();
    }

    private class WaveformView extends android.view.View {
        private final Paint waveformPaint;
        private final Paint progressPaint;
        private final float barWidth = 2.5f; // Width of each bar in dp
        private final float spacing = 1f; // Spacing between bars in dp
        private final int maxBars = 40; // Maximum number of bars to show

        public WaveformView(Context context) {
            super(context);
            waveformPaint = new Paint();
            waveformPaint.setColor(StyleUtils.getStyle(context).get().getTextSecondary());
            waveformPaint.setStyle(Paint.Style.FILL);
            waveformPaint.setAlpha(40);
            waveformPaint.setAntiAlias(true);

            progressPaint = new Paint(waveformPaint);
            progressPaint.setColor(StyleUtils.getStyle(context).get().getAccent());
            progressPaint.setAlpha(180);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            int width = getWidth();
            int height = getHeight();
            float centerY = height;
            float barWidthPx = SizeUtils.dipToPx(barWidth, getContext());
            float spacingPx = SizeUtils.dipToPx(spacing, getContext());

            // Calculate how many bars we can fit
            float totalBarWidth = barWidthPx + spacingPx;
            int numBars = Math.min(maxBars, (int) ((width) / totalBarWidth));

            float x = 0;

            for (int i = 0; i < numBars; i++) {
                float amplitude = i < waveformData.length ? waveformData[i] : 0.2f;
                float barHeight = height * amplitude * 0.4f; // 80% of height max

                canvas.drawRect(
                        x,
                        centerY - barHeight / 2,
                        x + barWidthPx,
                        centerY,
                        waveformPaint
                );

                if (i < numBars * progress) {
                    canvas.drawRect(
                            x,
                            centerY - barHeight / 2,
                            x + barWidthPx,
                            centerY,
                            progressPaint
                    );
                }

                x += totalBarWidth;
            }
        }
    }
} 