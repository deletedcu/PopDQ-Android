package com.popdq.app.ui;

import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.popdq.app.util.Utils;

/**
 * Created by Dang Luu on 23/08/2016.
 */
public class BaseSearchActivity extends BaseActivity {


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        try {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                View v = getCurrentFocus();
                if (v instanceof EditText) {
                    Rect outRect = new Rect();
                    v.getGlobalVisibleRect(outRect);
                    if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                        Log.d("focus", "touchevent");
                        v.clearFocus();
                        Utils.hideKeyBoard(this);
                    }
                }
            }
        }catch (Exception e){

        }
        return super.dispatchTouchEvent(event);
    }

}
