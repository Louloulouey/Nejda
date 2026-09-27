package com.sdidsa.nejda.view.app_content.session.attachments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.media.audiofx.LoudnessEnhancer;
import android.os.Handler;
import android.os.Looper;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.sdidsa.nejda.R;
import com.sdidsa.nejda.abs.UiCache;
import com.sdidsa.nejda.abs.components.controls.audio.AudioProcessing;
import com.sdidsa.nejda.abs.components.controls.audio.DiskAudioCache;
import com.sdidsa.nejda.abs.components.controls.button.ColoredButton;
import com.sdidsa.nejda.abs.components.controls.image.ColoredIcon;
import com.sdidsa.nejda.abs.components.controls.scratches.Orientation;
import com.sdidsa.nejda.abs.components.controls.scratches.loading.ColoredSpinLoading;
import com.sdidsa.nejda.abs.components.controls.text.ColoredLabel;
import com.sdidsa.nejda.abs.components.controls.text.font.Font;
import com.sdidsa.nejda.abs.components.controls.text.font.FontWeight;
import com.sdidsa.nejda.abs.components.layout.Alignment;
import com.sdidsa.nejda.abs.components.layout.StackPane;
import com.sdidsa.nejda.abs.components.layout.abs.CornerUtils;
import com.sdidsa.nejda.abs.components.layout.linear.HBox;
import com.sdidsa.nejda.abs.components.layout.linear.VBox;
import com.sdidsa.nejda.abs.components.layout.overlay.PartialSlideOverlay;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.utils.ErrorHandler;
import com.sdidsa.nejda.abs.utils.Platform;
import com.sdidsa.nejda.abs.utils.view.MarginUtils;
import com.sdidsa.nejda.abs.utils.view.PaddingUtils;
import com.sdidsa.nejda.abs.utils.view.SizeUtils;
import com.sdidsa.nejda.abs.utils.view.SpacerUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AudioPlayer extends PartialSlideOverlay {
    private static final ArrayList<AudioPlayer> cache = new ArrayList<>();

    public static AudioPlayer getInstance(Context owner) {
        cache.removeIf(inst -> inst.getOwner() != owner);

        AudioPlayer found = null;
        for (AudioPlayer inst : cache) {
            if (!inst.isAttachedToWindow()) {
                found = inst;
                break;
            }
        }

        if (found == null) {
            found = new AudioPlayer(owner);
            cache.add(found);
        }

        return found;
    }

    public static void clearCache() {
        cache.clear();
    }

    static {
        UiCache.register(AudioPlayer::clearCache);
    }
    private final ColoredIcon play;
    private MediaPlayer mediaPlayer;
    private boolean playing;
    private final PlayerProgress progress;
    private final Handler handler;
    private Runnable updateProgressAction;
    private boolean wasPlaying = false;

    private final VBox root;
    private final ColoredSpinLoading loader;

    //labels
    private final ColoredLabel current;
    private final ColoredLabel total;
    private final ColoredLabel speed;

    private Runnable onRemove;

    private AudioPlayer(Context owner) {
        super(owner, .4f);

        list.setAlignment(Alignment.TOP_CENTER);
        list.setPadding(20);
        list.setSpacing(30);
        list.setClipChildren(false);

        root = new VBox(owner);
        root.setAlignment(Alignment.BOTTOM_CENTER);
        root.setSpacing(50);

        loader = new ColoredSpinLoading(owner, Style.TEXT_SEC, 48);

        StackPane controls = new StackPane(owner);

        play = new ColoredIcon(owner, Style.BACK_PRI, Style.TEXT_NORM,
                R.drawable.play, 64);
        play.setPadding(4);
        play.setRadiusNoClip(64);
        play.setImagePadding(15);
        play.setAutoMirror(true);
        play.setElevation(SizeUtils.dipToPx(7, owner));

        speed = new ColoredLabel(owner, Style.BACK_PRI, Style.TEXT_SEC, "x1.0");
        speed.setLayoutParams(new LinearLayout.LayoutParams(
                SizeUtils.dipToPx(57, owner),
                FrameLayout.LayoutParams.WRAP_CONTENT
        ));
        speed.setTranslationX(SizeUtils.dipToPx(54, owner));
        speed.setFont(new Font(16, FontWeight.BOLD));
        speed.centerText();
        PaddingUtils.setPaddingHorizontalVertical(speed, 0, 7, owner);
        speed.setCornerRadius(CornerUtils.cornerRightRadius(owner, 30));

        speed.setOnClickListener((e) -> {
            double ov = Double.parseDouble(speed.getText().toString().substring(1));
            double nv = ov + 0.5;
            nv = nv > 2 ? 0.5 : nv;
            speed.setText("x".concat(Double.toString(nv)));
            if (mediaPlayer != null) {
                wasPlaying = mediaPlayer.isPlaying();
                PlaybackParams playbackParams = new PlaybackParams();
                playbackParams.setSpeed((float) nv);
                mediaPlayer.setPlaybackParams(playbackParams);
                if (wasPlaying) play();
                else pause();
            }
        });

        current = new ColoredLabel(owner, Style.TEXT_SEC, "00:00")
                .setFont(new Font(16, FontWeight.MEDIUM));
        total = new ColoredLabel(owner, Style.TEXT_SEC, "00:00")
                .setFont(new Font(16, FontWeight.MEDIUM));

        StackPane times = new StackPane(owner);
        times.addAligned(current, Alignment.BOTTOM_LEFT);
        times.addAligned(total, Alignment.BOTTOM_RIGHT);

        times.setTranslationY(-SizeUtils.dipToPx(40, owner));

        controls.addCentered(play);
        controls.addCentered(speed);

        progress = new PlayerProgress(owner);
        progress.addCentered(times);
        MarginUtils.setMarginBottom(progress, owner, 30);

        progress.setOnStartSeeking(() -> {
            wasPlaying = false;
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                wasPlaying = true;
                pause();
            }
        });

        progress.setOnSeek(p -> {
            int duration = mediaPlayer.getDuration();
            int currentPosition = (int) (duration * p);
            seekTo(currentPosition);

            if (wasPlaying) {
                play();
            }
        });

        progress.setOnSeeking(p -> {
            int duration = mediaPlayer.getDuration();
            int currentPosition = (int) (duration * p);

            updateTime(current, currentPosition);
        });

        handler = new Handler(Looper.getMainLooper());
        updateProgressAction = () -> {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    louder.setEnabled(true);
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    int duration = mediaPlayer.getDuration();

                    float p = (float) currentPosition / duration;
                    progress.setProgress(p);

                    updateTime(current, currentPosition);
                }
                handler.postDelayed(updateProgressAction, 10);
            }
        };

        root.addView(controls);
        root.addView(progress);

        SpacerUtils.spacer(root, Orientation.VERTICAL);
        MarginUtils.setMarginTop(loader, owner, 64);

        HBox top = new HBox(owner);
        top.setAlignment(Alignment.CENTER);

        ColoredButton remove = new ColoredButton(owner, Style.BACK_PRI, Style.TEXT_ERR,
                "Remove");
        remove.setPadding(0);
        remove.getContent().setLayoutParams(new LayoutParams(-1,-1));
        remove.getContent().setPadding(10);
        remove.getContent().setAlignment(Alignment.CENTER_LEFT);
        remove.setElevation(0);
        remove.setFont(new Font(18, FontWeight.MEDIUM));
        remove.setLayoutParams(new LinearLayout.LayoutParams(SizeUtils.dipToPx(100, owner), -2));
        remove.setOnClick(() -> {
            hide();
            if(onRemove != null) onRemove.run();
        });

        ColoredIcon close = new ColoredIcon(owner, Style.TEXT_NORM, R.drawable.close, 48);
        close.setPadding(14);
        close.setOnClick(this::hide);

        top.addViews(remove, SpacerUtils.spacer(owner, Orientation.HORIZONTAL), close);

        list.addViews(top, root);
    }

    public void setOnRemove(Runnable onRemove) {
        this.onRemove = onRemove;
    }

    private LoudnessEnhancer louder;

    @SuppressLint("SetTextI18n")
    private void load(File record) {
        playing = false;
        progress.setProgress(0);
        play.setImageResource(R.drawable.play);
        speed.setText("x1.0");

        long duration = DiskAudioCache.getAudioDuration(record);
        Platform.runBack(() -> {
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(record.getAbsolutePath());
                mediaPlayer.prepare();
                mediaPlayer.setVolume(1, 1);
                updateTime(total, mediaPlayer.getDuration());
                updateTime(current, 0);
                handler.post(updateProgressAction);
                mediaPlayer.setOnCompletionListener((player) -> {
                    pause();
                    seekTo(0);
                    progress.setProgress(0);
                    playing = false;
                });

                play.setOnClick(() -> {
                    if (playing) {
                        pause();
                    } else {
                        play();
                    }
                });

                float tooLow = -40;
                float tooHigh = -8f;

                float vol = AudioProcessing.getMaxLoudness(record);
                if (Float.isNaN(vol) || !Float.isFinite(vol)) {
                    vol = -20;
                }
                float gainDb = tooHigh - vol;

                int gainMb = (int) (gainDb * 100);
                louder =
                        new LoudnessEnhancer(mediaPlayer.getAudioSessionId());
                louder.setTargetGain(vol < tooLow || vol > tooHigh ? 0 : gainMb);
                louder.setEnabled(true);

                Platform.runLater(() -> {
                    loader.stopLoading();
                    list.removeView(loader);
                    list.addView(root);
                });
            } catch (IOException e) {
                ErrorHandler.handle(e, "opening audio file");
            }
        });
    }

    private void seekTo(int seekTo) {
        updateTime(current, seekTo);
        mediaPlayer.seekTo(seekTo);
    }

    @SuppressLint("SetTextI18n")
    private void updateTime(ColoredLabel lab, int time) {
        int durSecTotal = (time + (lab == total ? 500 : 0)) / 1000;
        int durSec = durSecTotal % 60;
        int durMin = durSecTotal / 60;
        lab.setText(format(durMin) + ":" + format(durSec));
    }

    private String format(int v) {
        return (v < 10 ? "0" : "") + v;
    }

    public void pause() {
        mediaPlayer.pause();
        play.setImageResource(R.drawable.play);

        playing = false;
    }

    public void play() {
        mediaPlayer.start();
        play.setImageResource(R.drawable.pause);

        playing = true;
    }

    public void show(File item) {
        list.removeView(root);
        loader.startLoading();
        list.addView(loader);
        load(item);
        super.show();
    }

    @Override
    public void show() {
        ErrorHandler.handle(new IllegalAccessError(
                        "can't show without loading a record object, " +
                                "use show(RecordResponse) instead"),
                "showing RecordOverlay");
    }

    @Override
    public void hide() {
        super.hide();

        handler.removeCallbacks(updateProgressAction);

        Platform.runBack(() -> {
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
        });
    }
}
