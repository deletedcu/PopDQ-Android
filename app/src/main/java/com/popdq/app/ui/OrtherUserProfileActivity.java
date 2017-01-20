package com.popdq.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.popdq.app.R;
import com.popdq.app.values.Values;

/**
 * Created by Dang Luu on 9/5/2016.
 */
public class OrtherUserProfileActivity extends UserProfileActivity {
    private Button btnAsk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        btnAsk = (Button) findViewById(R.id.btnAsk);
        btnAsk.setVisibility(View.VISIBLE);
        imgFollow.setVisibility(View.VISIBLE);
        btnAsk.setOnClickListener(this);



    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == btnAsk.getId()) {
            Intent intent = new Intent(this, ProvideQuestionActivity.class);
            intent.putExtra(Values.user_id_answer, user.getId());
            intent.putExtra(Values.language_written, user.getLanguage_answer());
            intent.putExtra(Values.name, user.getDisplayName());
            intent.putExtra(Values.avatar, user.getAvatar());
            intent.putExtra(Values.professional_field, user.getCategoriesString());

            try {
                intent.putExtra(Values.text_credit, user.getConfig_charge()[0].price);
            } catch (Exception e) {
                intent.putExtra(Values.text_credit, 0);
            }
            try {
                intent.putExtra(Values.voice_credit, user.getConfig_charge()[1].price);
            } catch (Exception e) {
                intent.putExtra(Values.text_credit, 0);
            }
            try {
                intent.putExtra(Values.video_credit, user.getConfig_charge()[2].price);
            } catch (Exception e) {
                intent.putExtra(Values.text_credit, 0);
            }


            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }


}
