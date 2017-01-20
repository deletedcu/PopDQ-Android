package com.popdq.app.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.InputType;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.popdq.app.MyApplication;
import com.popdq.app.R;

/**
 * Created by Dang Luu on 29/07/2016.
 */
public class InputForm extends LinearLayout {
    private Drawable icon;
    private String hint;
    private Context context;
    private EditText edtText;
    private ImageView imgIcon;

    public InputForm(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public InputForm(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initViews(context, attrs);
        init();


    }


    public InputForm(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initViews(context, attrs);
        init();


    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public InputForm(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        initViews(context, attrs);
        init();

    }

    public String getText() {
        if (edtText != null) {
            return edtText.getText().toString();
        } else return "";
    }

    public void setText(String text) {
        edtText.setText(text);
    }

    public void initViews(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.InputForm, 0, 0);

        try {
            // get the text and colors specified using the names in attrs.xml
            icon = a.getDrawable(R.styleable.InputForm_icon_form);
            hint = a.getString(R.styleable.InputForm_hint_form);


        } finally {
            a.recycle();
        }

    }

    public Drawable getIcon() {
        return icon;
    }

    public String getHint() {
        return hint;
    }


    public EditText getEdtText() {
        return edtText;
    }

    public void setInputType(int inputType) {
        edtText.setInputType(inputType);
    }

    public void setTypeface(Typeface typeface) {
        edtText.setTypeface(typeface);
    }

    public ImageView getImgIcon() {
        return imgIcon;
    }

    public void init() {
        inflate(context, R.layout.item_from_input, this);
        edtText = (EditText) findViewById(R.id.edtText);
        edtText.setTypeface(MyApplication.getInstanceTypeNormal(context));
        edtText.setHint(hint);
        edtText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        imgIcon = (ImageView) findViewById(R.id.imgIcon);
        imgIcon.setImageDrawable(icon);

    }
}
