package com.popdq.app.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.popdq.app.R;
import com.popdq.app.view.textview.TextViewNormal;

/**
 * Created by Dang Luu on 29/07/2016.
 */
public class ItemNavigationMenu extends LinearLayout {
    private Context context;
    private Drawable icon;
    private String text;
    private ImageView imgIcon;
    private TextViewNormal tvTitle;


    public ItemNavigationMenu(Context context) {
        super(context);
        this.context = context;
    }

    public ItemNavigationMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initViews(context, attrs);
        init();
    }

    public ItemNavigationMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initViews(context, attrs);
        init();

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ItemNavigationMenu(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        initViews(context, attrs);
        init();

    }

    public void initViews(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.ItemNavigationMenu, 0, 0);

        try {
            // get the text and colors specified using the names in attrs.xml
            icon = a.getDrawable(R.styleable.ItemNavigationMenu_icon_menu);
            text = a.getString(R.styleable.ItemNavigationMenu_text);


        } finally {
            a.recycle();
        }


    }

    private void init() {
        inflate(getContext(), R.layout.item_navigation_menu, this);
        this.tvTitle = (TextViewNormal) findViewById(R.id.tvTitle);
        this.imgIcon = (ImageView) findViewById(R.id.imgIcon);
        tvTitle.setText(text);
        imgIcon.setImageDrawable(icon);
    }

    public void setText(String text) {
        tvTitle.setText(text);
    }

}
