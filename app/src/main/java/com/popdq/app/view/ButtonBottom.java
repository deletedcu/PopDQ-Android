package com.popdq.app.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Button;

import com.popdq.app.MyApplication;

/**
 * Created by Dang Luu on 11/08/2016.
 */
public class ButtonBottom extends Button {
    public ButtonBottom(Context context) {
        super(context);
        init(context);
    }

    public ButtonBottom(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    public ButtonBottom(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ButtonBottom(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);

    }

    public void init(Context context) {
        setTypeface(MyApplication.getInstanceTypeNormal(context));
    }
}
