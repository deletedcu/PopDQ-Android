package com.popdq.app.ui;

import android.os.Bundle;

import com.popdq.app.R;
import com.popdq.app.fragment.QuestionFragmentNewDesign;
import com.popdq.app.util.Utils;
import com.popdq.app.values.Values;

public class MyQuestionActivity extends BaseSearchActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_question);
        QuestionFragmentNewDesign questionFragment = new QuestionFragmentNewDesign();
        questionFragment.setUrl(Values.URL_QUESTION_MY_LIST);
        questionFragment.setMyAnswer(false);

        getSupportFragmentManager().beginTransaction().add(R.id.content, questionFragment).commit();
        Utils.setActionBar(this, getString(R.string.my_question), R.drawable.btn_back);
    }
}
