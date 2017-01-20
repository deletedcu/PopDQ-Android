package com.popdq.app.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.popdq.app.R;

import java.util.ArrayList;

/**
 * Created by Dang Luu on 18/07/2016.
 */
public class CreditSpiner extends Spinner {
    private float credit = 0;
    private String hint = "";
    private Context context;
    private float[] listCredit;

    public void setListCredit(float[] listCredit) {
        this.listCredit = listCredit;
        init(context);
    }

    public void init(Context context) {

//        final String[] listCredit = context.getResources().getStringArray(R.array.list_credit_string);

        int length = listCredit.length;
        final ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            list.add("$"+listCredit[i]);
        }
        list.add(hint);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (context, R.layout.item_spiner_text_credit, list) {
            @Override
            public int getCount() {
                return super.getCount() - 1;
            }
        };
        adapter.setDropDownViewResource
                (android.R.layout.simple_list_item_single_choice);

        setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (listCredit.length > i)
                    credit = listCredit[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        setAdapter(adapter);
        setSelection(list.size() - 1);
    }

    public void setHint(String hintText) {
        //hint must set before init
        this.hint = hintText;
//        init(context);

    }

    public void setSelection(float credit) {
        int lenght = listCredit.length;
        for (int i = 0; i < lenght; i++) {
            if (listCredit[i] == credit) {
                setSelection(i);
                return;
            }
        }
    }

    public float getCredit() {
        return credit;
    }

  /*  public CreditSpiner(Context context) {
        super(context);
        this.context = context;
        init(context);
    }

    public CreditSpiner(Context context, int mode) {
        super(context, mode);
        this.context = context;
        init(context);

    }*/

    public CreditSpiner(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initViews(context, attrs);

    }

    public CreditSpiner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initViews(context, attrs);


    }

    public CreditSpiner(Context context, AttributeSet attrs, int defStyleAttr, int mode) {
        super(context, attrs, defStyleAttr, mode);
        this.context = context;
        initViews(context, attrs);


    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CreditSpiner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, int mode) {
        super(context, attrs, defStyleAttr, defStyleRes, mode);
        this.context = context;
        initViews(context, attrs);


    }

    @TargetApi(Build.VERSION_CODES.M)
    public CreditSpiner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, int mode, Resources.Theme popupTheme) {
        super(context, attrs, defStyleAttr, defStyleRes, mode, popupTheme);
        this.context = context;
        initViews(context, attrs);


    }

    public void initViews(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.CreditSpiner, 0, 0);
        this.context =context;

        try {
            // get the text and colors specified using the names in attrs.xml
            hint = a.getString(R.styleable.CreditSpiner_hint);
//            init(context);
        } finally {
            a.recycle();
        }


    }


}
