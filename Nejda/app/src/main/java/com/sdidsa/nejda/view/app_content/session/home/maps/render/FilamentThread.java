package com.sdidsa.nejda.view.app_content.session.home.maps.render;

import android.os.Looper;

public class FilamentThread extends Thread{
    private Runnable postOnStart;

    public void run() {
        Looper.prepare();

        if(postOnStart != null)
            postOnStart.run();

        Looper.loop();
    }

    public void postOnStart(Runnable post) {
        postOnStart = post;
    }
}
