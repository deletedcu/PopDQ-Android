package com.popdq.app.view.textview;

import android.content.Context;
import android.util.AttributeSet;

import com.popdq.app.MyApplication;

import io.github.rockerhieu.emojicon.EmojiconTextView;


/**
 * Created by Admin on 23/08/2015.
 */
public class TextViewBold extends EmojiconTextView {
    public TextViewBold(Context context) {
        super(context);
        setTypeface(MyApplication.getInstanceTypeBold(context));
    }

    public TextViewBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(MyApplication.getInstanceTypeBold(context));

    }

    public TextViewBold(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface(MyApplication.getInstanceTypeBold(context));

    }

//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    public TextViewBold(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//        setTypeface(MyApplication.getInstanceTypeBold(context));
//
//    }

}
