package com.sdidsa.nejda.abs.components.controls.audio;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import com.sdidsa.nejda.abs.utils.ErrorHandler;
import com.sdidsa.nejda.abs.utils.view.PermissionUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class AudioRecorder {

    private MediaRecorder recorder = null;
    private File file;
    private Handler handler;
    private Runnable amplitudeMonitor;
    private boolean isRecording = false;
    private List<Consumer<Integer>> amplitudeListeners = new ArrayList<>();
    private Consumer<String> timeUpdateListener;
    private long startTime;

    public void startRecording(Context context, Runnable onStart) {
        this.onStart = onStart;
        PermissionUtils.requirePermissions(context, () -> start(context),
                android.Manifest.permission.RECORD_AUDIO);
    }

    private Runnable onStart;

    private void start(Context context) {
        onStart.run();
        try {
            file = File.createTempFile("record", ".mp4");
            file.deleteOnExit();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                recorder = new MediaRecorder(context);
            } else {
                recorder = new MediaRecorder();
            }
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setOutputFile(file);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.setAudioEncodingBitRate(16*44100);
            recorder.setAudioSamplingRate(44100);
            recorder.prepare();
        } catch (IOException e) {
            ErrorHandler.handle(e, "recording audio from mic");
            return;
        }

        recorder.start();
        isRecording = true;
        startTime = System.currentTimeMillis();
        startAmplitudeMonitoring();
    }

    private void startAmplitudeMonitoring() {
        handler = new Handler(Looper.getMainLooper());
        amplitudeMonitor = new Runnable() {
            @Override
            public void run() {
                if (recorder != null && isRecording) {
                    int amplitude = recorder.getMaxAmplitude();
                    notifyAmplitudeListeners(amplitude);
                    updateRecordingTime();
                    handler.postDelayed(this, 100); // Update every 100ms
                }
            }
        };
        handler.post(amplitudeMonitor);
    }

    private void updateRecordingTime() {
        if (timeUpdateListener != null) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            int seconds = (int) (elapsedTime / 1000) % 60;
            int minutes = (int) (elapsedTime / 1000) / 60;
            String timeString = String.format(Locale.getDefault(),
                    "%02d:%02d", minutes, seconds);
            timeUpdateListener.accept(timeString);
        }
    }

    private void notifyAmplitudeListeners(int amplitude) {
        for (Consumer<Integer> listener : amplitudeListeners) {
            listener.accept(amplitude);
        }
    }

    public void addAmplitudeListener(Consumer<Integer> listener) {
        amplitudeListeners.add(listener);
    }

    public void setTimeUpdateListener(Consumer<String> listener) {
        this.timeUpdateListener = listener;
    }

    public void stopRecording(Consumer<File> onSuccess) {
        if (recorder != null) {
            isRecording = false;
            if (handler != null) {
                handler.removeCallbacks(amplitudeMonitor);
            }
            try {
                recorder.stop();
                recorder.release();
                onSuccess.accept(file);
            } catch (Exception e) {
                ErrorHandler.handle(e, "stopping audio recording");
                onSuccess.accept(null);
            } finally {
                recorder = null;
            }
        }
    }
}
