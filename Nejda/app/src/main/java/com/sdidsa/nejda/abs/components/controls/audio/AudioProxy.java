package com.sdidsa.nejda.abs.components.controls.audio;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.sdidsa.nejda.abs.App;
import com.sdidsa.nejda.abs.utils.ErrorHandler;
import com.sdidsa.nejda.abs.utils.Platform;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class AudioProxy {
    public static final int FILE_EXISTS = 0;
    public static final int FILE_SAVED = 1;
    public static final int FILE_ERROR = -1;

    private static DiskAudioCache diskCache;

    public static void init(App owner) {
        diskCache = new DiskAudioCache(owner, "records");
    }

    private static String makeKey(String url) {
        if(url.contains("://")) {
            StringBuilder sb = new StringBuilder();

            int endIndex = url.indexOf("?");
            endIndex = endIndex == -1 ? url.length() : endIndex;
            for (int i = Math.max(0, endIndex - 40); i < endIndex; i++) {
                char c = url.charAt(i);
                if (Character.isDigit(c) || Character.isLetter(c) || c == '_') {
                    sb.append(Character.toLowerCase(c));
                }
            }

            return sb.toString();
        }else {
            return url;
        }
    }

    static final HashMap<String, List<Consumer<AudioFile>>> waiters = new HashMap<>();
    public synchronized static void getAudio(String url, Consumer<AudioFile> onResult) {
        String key = makeKey(url);

        if(waiters.containsKey(key)) {
            Objects.requireNonNull(waiters.get(key)).add(onResult);
            return;
        }

        List<Consumer<AudioFile>> forThis = new ArrayList<>();
        forThis.add(onResult);
        waiters.put(key, forThis);

        Platform.runBack(() -> {
            AudioFile cachedAudio = get(key);
            if (cachedAudio != null) {
                waiters.remove(key);
                Platform.runLater(() -> forThis.forEach(w -> w.accept(cachedAudio)));
                return;
            }

            Platform.runBack(() -> {
                Log.i("downloading", key);
                AudioFile downloadedAudio = null;
                try {
                    downloadedAudio = diskCache.download(url, key);
                } catch (Exception x) {
                    ErrorHandler.handle(x, "downloading audio at " + url);
                }

                AudioFile finalAudio = downloadedAudio;
                Platform.runLater(() -> {
                    waiters.remove(key);
                    forThis.forEach(w -> {
                        try {
                            w.accept(finalAudio);
                        }catch (Exception x) {
                            ErrorHandler.handle(x, "handling audio result");
                        }
                    });
                });
            });
        });
    }

    private static AudioFile get(String key) {
        return diskCache.get(key);
    }

    public static int saveAudioToGallery(Context context, File file, String fileName) {
        if (isFileInGallery(context, fileName)) {
            return FILE_EXISTS;
        }

        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/mpeg");
        values.put(MediaStore.Audio.Media.RELATIVE_PATH, Environment.DIRECTORY_MUSIC);
        values.put(MediaStore.Audio.Media.IS_PENDING, true);

        final var contentResolver = context.getContentResolver();
        final var uri = contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);

        try {
            if (uri == null) throw new IllegalStateException("failed to open input stream");
            OutputStream fos = contentResolver.openOutputStream(uri);
            if (fos == null) throw new IllegalStateException("failed to open input stream");

            Files.copy(file.toPath(), fos);
            fos.close();

            values.put(MediaStore.Audio.Media.IS_PENDING, false);
            contentResolver.update(uri, values, null, null);
            return FILE_SAVED;
        } catch (Exception x) {
            ErrorHandler.handle(x, "saving audio to storage");
            return FILE_ERROR;
        }
    }



    public static File saveTemp(File source, String name) {
        try {
            File f = diskCache.keyToFile(name);
            Files.copy(source.toPath(), f.toPath());
            return f;
        }catch(Exception x) {
            ErrorHandler.handle(x, "save audio file " + name);
            return null;
        }
    }

    public static boolean isFileInGallery(Context context, String fileName) {
        String[] projection = {MediaStore.Audio.Media.DISPLAY_NAME};
        String selection = MediaStore.Audio.Media.DISPLAY_NAME + " = ?";
        String[] selectionArgs = new String[]{fileName};

        Uri collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        try (Cursor cursor = context.getContentResolver().query(
                collection,
                projection,
                selection,
                selectionArgs,
                null
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                return true;
            }
        }
        return false;
    }
}
