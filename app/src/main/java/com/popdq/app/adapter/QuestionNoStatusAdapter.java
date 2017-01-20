package com.popdq.app.adapter;

import android.content.Context;
import android.view.View;

import com.popdq.app.model.Question;

import java.util.List;

/**
 * Created by Dang Luu on 11/08/2016.
 */
public class QuestionNoStatusAdapter extends QuestionAdapter {
    public QuestionNoStatusAdapter(Context context, List<Question> questions) {
        super(context, questions);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.status.setVisibility(View.GONE);
    }
}
