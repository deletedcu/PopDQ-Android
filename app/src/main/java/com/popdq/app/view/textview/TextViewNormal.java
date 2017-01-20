package com.popdq.app.view.textview;

import android.content.Context;
import android.util.AttributeSet;

import com.popdq.app.MyApplication;

import io.github.rockerhieu.emojicon.EmojiconTextView;


/**
 * Created by Admin on 23/08/2015.
 */
public class TextViewNormal extends EmojiconTextView {
    public TextViewNormal(Context context) {
        super(context);
        setTypeface(MyApplication.getInstanceTypeNormal(context));
    }

    public TextViewNormal(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(MyApplication.getInstanceTypeNormal(context));
    }

    public TextViewNormal(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface(MyApplication.getInstanceTypeNormal(context));

    }

//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    public TextViewNormal(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//        setTypeface(MyApplication.getInstanceTypeNormal(context));
//
//    }

}
