package com.popdq.app.view;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.popdq.app.R;
import com.popdq.app.view.textview.TextViewNormal;
import com.tokenautocomplete.TokenCompleteTextView;

/**
 * Created by Dang Luu on 11/07/2016.
 */
public class TagCompletionView extends TokenCompleteTextView<String> {
    Context context;

    public TagCompletionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        allowCollapse(false);
    }

    @Override
    public void addObject(String object) {
        super.addObject(object);
    }

    @Override
    protected View getViewForObject(final String object) {
        LayoutInflater l = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        LinearLayout view = (LinearLayout) l.inflate(R.layout.item_tag, (ViewGroup) TagCompletionView.this.getParent(), false);
        LinearLayout root =(LinearLayout)view.findViewById(R.id.root);
        root.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, object, Toast.LENGTH_SHORT).show();
            }
        });
        TextViewNormal textView = (TextViewNormal) view.findViewById(R.id.text);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, object, Toast.LENGTH_SHORT).show();
            }
        });
        textView.setText(object);

        return view;
    }




    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        int action = event.getActionMasked();
        Editable text = getText();
        boolean handled = false;

//        if (tokenClickStyle == TokenClickStyle.None) {
//            handled = super.onTouchEvent(event);
//        }
//
//        if (isFocused() && text != null && lastLayout != null && action == MotionEvent.ACTION_UP) {
//
            int offset = getOffsetForPosition(event.getX(), event.getY());
//
//            if (offset != -1) {
                TokenImageSpan[] links = text.getSpans(offset, offset, TokenImageSpan.class);

                if (links.length > 0) {
                    links[0].onClick();

                    Toast.makeText(context, "csacs", Toast.LENGTH_SHORT).show();
//                    handled = true;
//                } else {
                    //We didn't click on a token, so if any are selected, we should clear that
//                    clearSelections();
//                }
//            }
        }

//        if (!handled && tokenClickStyle != TokenClickStyle.None) {
//            handled = super.onTouchEvent(event);
//        }
        return handled;
    }

    @Override
    protected String defaultObject(String completionText) {

        return "test";
    }
}
