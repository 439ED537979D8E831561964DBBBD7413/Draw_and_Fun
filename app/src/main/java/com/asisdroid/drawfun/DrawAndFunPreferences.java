package com.asisdroid.drawfun;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

/**
 * Created by ashishkumarpolai on 7/14/2017.
 */

public class DrawAndFunPreferences {
    private static DrawAndFunPreferences sCommonPref;
    public SharedPreferences mPreference;
    private Context mContext;
    private String CURRENT_BACKGROUND_COLOR = "cyrrentbgcol";
    private String CURRENT_PAINT_COLOR = "currentpaintbg";
    private String CURRENT_PAINT_BRUSH = "currentpaintbrush";
    private String CURRENT_PAINT_THICKNESS = "currentpaintthickness";

    private int mbgColor = Color.BLACK;
    private int mpaintColor = Color.WHITE;
    private String mbrushType = "normal";

    public static DrawAndFunPreferences getInstance(Context context) {
        if (sCommonPref == null) {
            sCommonPref = new DrawAndFunPreferences(context);
        }
        return sCommonPref;
    }

    public DrawAndFunPreferences(Context context) {
        mContext = context;
        mPreference = mContext.getSharedPreferences("DrawAndFun_Preferences",
                Context.MODE_PRIVATE);
    }

    public void clearPreference() {
        mPreference.edit().clear().commit();
    }

    public int getBgColor() {
        return mPreference.getInt(CURRENT_BACKGROUND_COLOR, mbgColor);
    }

    public void setBgColor(int bgColor) {
        mPreference.edit().putInt(CURRENT_BACKGROUND_COLOR, bgColor)
                .commit();
    }

    public int getPaintColor() {
        return mPreference.getInt(CURRENT_PAINT_COLOR, mpaintColor);
    }

    public void setPaintColor(int paintColor) {
        mPreference.edit().putInt(CURRENT_PAINT_COLOR, paintColor)
                .commit();
    }

    public String getBrushType() {
        return mPreference.getString(CURRENT_PAINT_BRUSH, mbrushType);
    }

    public void setBrushType(String brushType) {
        mPreference.edit().putString(CURRENT_PAINT_BRUSH, brushType)
                .commit();
    }
}
