package com.asisdroid.drawfun;

/**
 * Created by ashishkumarpolai on 1/11/2018.
 */


import android.graphics.Bitmap;
import android.view.View;

public class ScreenshotUtils {

    /*  Method which will return Bitmap after taking screenshot. We have to pass the view which we want to take screenshot.  */
    public static Bitmap getScreenShot(View view) {
        View screenView = view;
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        return bitmap;
    }
}
