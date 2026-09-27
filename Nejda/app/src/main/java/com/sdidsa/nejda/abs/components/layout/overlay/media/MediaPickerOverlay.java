package com.sdidsa.nejda.abs.components.layout.overlay.media;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sdidsa.nejda.R;
import com.sdidsa.nejda.abs.animation.view.RotateAnimation;
import com.sdidsa.nejda.abs.components.controls.button.Button;
import com.sdidsa.nejda.abs.components.controls.button.ColoredButton;
import com.sdidsa.nejda.abs.components.controls.image.ColorIcon;
import com.sdidsa.nejda.abs.components.controls.image.ColoredIcon;
import com.sdidsa.nejda.abs.components.controls.text.ColoredLabel;
import com.sdidsa.nejda.abs.components.controls.text.font.Font;
import com.sdidsa.nejda.abs.components.controls.text.font.FontWeight;
import com.sdidsa.nejda.abs.components.layout.Alignment;
import com.sdidsa.nejda.abs.components.layout.linear.HBox;
import com.sdidsa.nejda.abs.components.layout.overlay.PartialSlideOverlay;
import com.sdidsa.nejda.abs.components.layout.overlay.media.medialist.MediaList;
import com.sdidsa.nejda.abs.data.property.ObservableArrayList;
import com.sdidsa.nejda.abs.data.property.Property;
import com.sdidsa.nejda.abs.style.Style;
import com.sdidsa.nejda.abs.utils.ErrorHandler;
import com.sdidsa.nejda.abs.utils.view.MarginUtils;
import com.sdidsa.nejda.abs.utils.view.PermissionUtils;
import com.sdidsa.nejda.abs.utils.Permissions;
import com.sdidsa.nejda.abs.utils.Platform;
import com.sdidsa.nejda.abs.utils.view.ContextUtils;
import com.sdidsa.nejda.abs.data.media.Bucket;
import com.sdidsa.nejda.abs.data.media.Media;
import com.sdidsa.nejda.abs.utils.view.SpacerUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MediaPickerOverlay extends PartialSlideOverlay {
    private final HBox top;
    private final Button select;
    private final Button done;
    private final Button bucket;
    private final BucketOverlay bucketOverlay;
    private MediaList mediaList;
    private List<Bucket> buckets;
    private Bucket selected;
    private Consumer<Media> onMedia;
    private Consumer<List<Media>> onMultiple;

    private final ObservableArrayList<Media> selectedMedia;
    private final Property<Boolean> single;

    public MediaPickerOverlay(Context owner) {
        super(owner, .6f);

        selectedMedia = new ObservableArrayList<>();
        single = new Property<>(false);

        done = new ColoredButton(owner, Style.ACCENT, s -> Color.WHITE,"Done");
        done.setFont(new Font(18, FontWeight.MEDIUM));
        selectedMedia.addListener((src, c) -> done.setDisabled(src.isEmpty()));

        MarginUtils.setMargin(done, owner, 10, 10, 10, 10);

        ColorIcon arrow = new ColoredIcon(owner, Style.TEXT_NORM, R.drawable.right_arrow);
        arrow.setRotation(-90);
        arrow.setSize(16);
        arrow.setFocusable(false);
        arrow.setClickable(false);

        top = new HBox(owner);
        top.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        top.setGravity(Gravity.TOP);
        MarginUtils.setMargin(top, owner, 10, 10, 10, 10);

        bucketOverlay = new BucketOverlay(owner);

        bucketOverlay.addToShow(new RotateAnimation(arrow, 90));
        bucketOverlay.addToHide(new RotateAnimation(arrow, -90));

        bucket = new ColoredButton(owner, Style.BACK_SEC,
                Style.TEXT_NORM, "Select Album");
        bucket.setTextAlignment(TEXT_ALIGNMENT_TEXT_START);
        bucket.extendLabel();
        bucket.setElevation(0);
        bucket.setOnClick(() -> {
            if (buckets == null) {
                Toast.makeText(owner, "Unresolved permissions", Toast.LENGTH_SHORT).show();
            } else {
                bucketOverlay.setHeightFactor(ovHeight());
                bucketOverlay.setData(buckets);
                bucketOverlay.show();
            }
        });

        bucketOverlay.setOnBucket(res -> {
            selected = res;
            bucket.setKey(res.getName());
            mediaList.setData(selected.getItems(), onMedia != null);
        });

        bucket.addPostLabel(arrow);

        SpacerUtils.spacerWidth(bucket, 1f);

        top.addView(bucket);

        select = new ColoredButton(owner, Style.BACK_SEC,
                Style.TEXT_NORM, "Select Images");
        select.setElevation(0);
        SpacerUtils.spacer(bucket);
        SpacerUtils.spacer(select);
        MarginUtils.setMarginLeft(select, owner, 10);

        select.setOnClick(() ->
                PermissionUtils.requestPermissionsOr(owner, this::readImages,
                    Permissions.imagePermissions()));

        addOnShowing(() -> {
            if(accessMode() == AccessMode.DENIED) {
                PermissionUtils.requirePermissionsOr(owner,
                        this::readImages, Permissions.imagePermissions());
            }else {
                if(accessMode() != AccessMode.DENIED)
                    readImages();
            }
        });

        mediaList = new MediaList(owner, selectedMedia, single);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.weight = 1;
        mediaList.setLayoutParams(params);

        MarginUtils.setMargin(mediaList, owner, 10, 0, 10, 0);

        ColoredLabel empty = new ColoredLabel(owner,
                Style.TEXT_SEC, "We couldn't find any images here");
        empty.setFont(new Font(18));
        empty.centerText();
        MarginUtils.setMarginTop(empty, owner, 20);

        mediaList.setOnData(data -> {
            list.removeView(empty);
            if(data.isEmpty()) {
                list.addView(empty, 1);
            }
        });

        list.setAlignment(Alignment.CENTER);
        list.addView(top);
        list.addView(mediaList);
        list.addView(done);

        done.setOnClick(() -> {
            hide();
            if(selectedMedia.isEmpty()) return;

            if(onMedia != null) {
                onMedia.accept(selectedMedia.get(0));
            }

            if(onMultiple != null) {
                onMultiple.accept(selectedMedia);
            }
        });

        addOnShowing(() -> {
            selectedMedia.clear();
            single.set(onMedia != null);
            done.setDisabled(true);
        });
    }

    private double ovHeight() {
        int[] l = new int[2];
        bucket.getLocationOnScreen(l);
        int y = l[1];
        int h = bucket.getHeight();

        int[] myL = new int[2];
        list.getLocationOnScreen(myL);
        int myH = list.getHeight();

        int mY = myL[1] + myH;

        int maxY = mY - (y + h);

        return (double) maxY / ContextUtils.getScreenHeight(owner);
    }

    public void setOnMedia(Consumer<Media> onMedia) {
        this.onMedia = onMedia;
    }

    public void setOnMultiple(Consumer<List<Media>> onMultiple) {
        this.onMultiple = onMultiple;
    }

    private void readImages() {
        top.removeAllViews();
        top.addView(bucket);
        if(accessMode() == AccessMode.SELECTIVE) {
            top.addView(select);
        }

        Platform.runBack(this::loadImages);
    }

    private AccessMode accessMode() {
        boolean hasAll;
        boolean hasSelective = false;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            hasSelective = PermissionUtils.isGranted(owner,
                    Permissions.selectiveImagePermission());
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            hasAll = PermissionUtils.isGranted(owner,
                    Permissions.allImagesPermission());
        }else {
            hasAll = PermissionUtils.isGranted(owner,
                    Permissions.externalStoragePermission());
        }

        if(!hasAll && !hasSelective) {
            return AccessMode.DENIED;
        }else if(hasSelective && !hasAll) {
            return AccessMode.SELECTIVE;
        }else {
            return AccessMode.FULL;
        }
    }

    private void loadImages() {
         listBuckets( l -> {
            buckets = l;
            Platform.runLater(() -> {
                bucket.setKey(selected.getName());
                mediaList.setData(selected.getItems(), onMedia != null);
            });
        });
    }

    private void listBuckets(Consumer<List<Bucket>> onResult) {
        Platform.runBack(() -> {
            ArrayList<Bucket> res = new ArrayList<>();

            String[] projection = new String[]{
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                    MediaStore.Images.Media.DATE_MODIFIED
            };

            Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            String BUCKET_ORDER_BY = MediaStore.Images.Media.DATE_MODIFIED + " DESC";
            Cursor cursor = owner.getContentResolver().query(images, projection, // Which columns to return
                    null,       // Which rows to return (all rows)
                    null,       // Selection arguments (none)
                    BUCKET_ORDER_BY        // Ordering
            );

            Bucket all = new Bucket("All Photos");
            res.add(all);

            selected = all;

            assert cursor != null;
            if (cursor.moveToFirst()) {

                int idCol = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                int nameCol = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
                int bucketCol = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                do {
                    long id = cursor.getLong(idCol);
                    String name = cursor.getString(nameCol);
                    String bucket = cursor.getString(bucketCol);

                    Bucket b = bucketFromName(res, bucket);
                    Uri contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                    Media media = new Media(id, contentUri, name);

                    all.getItems().add(media);
                    b.getItems().add(media);
                } while (cursor.moveToNext());
            }

            cursor.close();

            try {
                onResult.accept(res);
            } catch (Exception e) {
                ErrorHandler.handle(e, "loading images from device");
            }
        });
    }

    private Bucket bucketFromName(ArrayList<Bucket> list, String name) {
        Bucket res = null;

        for (Bucket b : list) {
            if (b.getName().equals(name)) {
                res = b;
                break;
            }
        }

        if (res == null) {
            res = new Bucket(name);
            list.add(res);
        }

        return res;
    }
}
