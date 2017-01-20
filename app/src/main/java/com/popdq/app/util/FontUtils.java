package com.popdq.app.util;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by Dang Luu on 11/08/2016.
 */
public class FontUtils {
    public static Typeface getTypefaceNormal(Context Context) {
        Typeface tf;
        try {
            tf = Typeface.createFromAsset(Context.getAssets(),
                    "fonts/Montserrat-Regular.otf");
            if (tf != null) {
                return tf;
            } else {
                return Typeface.DEFAULT;
            }
        } catch (Exception e) {
            return Typeface.DEFAULT;
        }
    }

    public static Typeface getTypefaceBold(Context Context) {
        Typeface tf;
        try {
            tf = Typeface.createFromAsset(Context.getAssets(),
                    "fonts/Montserrat-SemiBold.otf");
            if (tf != null) {
                return tf;
            } else {
                return Typeface.DEFAULT;
            }
        } catch (Exception e) {
            return Typeface.DEFAULT;
        }
    }

    public static Typeface getTypefaceThin(Context Context) {
        Typeface tf;
        try {
            tf = Typeface.createFromAsset(Context.getAssets(),
                    "fonts/Montserrat-Light.otf");
            if (tf != null) {
                return tf;
            } else {
                return Typeface.DEFAULT;
            }
        } catch (Exception e) {
            return Typeface.DEFAULT;
        }
    }
}
