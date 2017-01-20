package com.popdq.app.view;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

/**
 * Created by Dang Luu on 08/08/2016.
 */
public class DialogChooseCredits extends AlertDialog.Builder {
    private String[] listCredit;
    private DialogInterface.OnClickListener onClickListener;
    private float credit;


    public DialogChooseCredits(@NonNull Context context) {
        super(context);

    }

    public DialogChooseCredits(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);

    }

    public String[] getListCredit() {
        return listCredit;
    }

    public void setListCredit(String[] listCredit) {
        this.listCredit = listCredit;
    }

    public DialogInterface.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(DialogInterface.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void init() {
        setItems(listCredit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    credit = Float.parseFloat(listCredit[i].toString());
                } catch (Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public float getCredit() {
        return credit;
    }


}
