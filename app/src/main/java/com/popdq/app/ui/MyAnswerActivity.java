package com.popdq.app.ui;

import android.os.Bundle;

import com.popdq.app.R;
import com.popdq.app.fragment.QuestionFragmentNewDesign;
import com.popdq.app.util.Utils;
import com.popdq.app.values.Values;

public class MyAnswerActivity extends BaseSearchActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_answer);
        QuestionFragmentNewDesign questionFragment = new QuestionFragmentNewDesign();
        questionFragment.setUrl(Values.URL_QUESTION_ANSWERED);
        questionFragment.setMyAnswer(true);
        getSupportFragmentManager().beginTransaction().add(R.id.content, questionFragment).commit();
        Utils.setActionBar(this, getString(R.string.my_answer), R.drawable.btn_back);
        Utils.checkStartActivityFromNotificationAndSendRead(this,null);
    }
}
