package com.popdq.app.view.textview;

import android.content.Context;
import android.util.AttributeSet;

import com.popdq.app.MyApplication;

import io.github.rockerhieu.emojicon.EmojiconTextView;


/**
 * Created by Admin on 23/08/2015.
 */
public class TextViewThin extends EmojiconTextView {
    public TextViewThin(Context context) {
        super(context);
        setTypeface(MyApplication.getInstanceTypeThin(context));
    }

    public TextViewThin(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(MyApplication.getInstanceTypeThin(context));

    }

    public TextViewThin(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface(MyApplication.getInstanceTypeThin(context));

    }

//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    public TextViewThin(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//        setTypeface(MyApplication.getInstanceTypeThin(context));
//
//    }

}
