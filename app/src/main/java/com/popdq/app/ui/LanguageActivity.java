package com.popdq.app.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.popdq.app.R;
import com.popdq.app.util.Utils;
import com.popdq.app.values.Values;
import com.popdq.app.view.textview.TextViewNormal;

import java.util.ArrayList;
import java.util.List;

public class LanguageActivity extends BaseActivity {
    private List<LanguageModel> languageModels;
    private String current_language;
    private ListView list;

    private class LanguageModel {
        public int id;
        public String language;
        public boolean isCheck;

        public LanguageModel(int id, String language, boolean isCheck) {
            this.id = id;
            this.language = language;
            this.isCheck = isCheck;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        Utils.setActionBar(this, getString(R.string.language).toUpperCase(), R.drawable.btn_back);
        list = (ListView) findViewById(R.id.listInterest);
        if (getIntent().hasExtra(Values.language_written)) {
            current_language = getIntent().getStringExtra(Values.language_written);
        }

        getListLanguages();
        setAdapter();

        Utils.setBottomButton(this, "OK", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringBuilder stringBuilder = new StringBuilder();
                int length = languageModels.size();
                for (int i = 0; i < length; i++) {
                    if (languageModels.get(i).isCheck)
                        stringBuilder.append(languageModels.get(i).language + " | ");
                }
                if (stringBuilder.length() > 2)
                    stringBuilder.deleteCharAt(stringBuilder.length() - 2);
                Intent intent = new Intent();
                intent.putExtra(Values.language_written, stringBuilder.toString());
                setResult(Activity.RESULT_OK, intent);
                finish();

            }
        });

    }

    public void getListLanguages() {
        int size = Values.languages.length;
        languageModels = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            boolean isCheck = current_language.contains(Values.languages[i]);
            languageModels.add(new LanguageModel(i, Values.languages[i], isCheck));
        }
    }


    public void setAdapter() {
        ArrayAdapter<LanguageModel> adapter = new ArrayAdapter<LanguageModel>(LanguageActivity.this, R.layout.item_my_interest) {
            @Override
            public int getCount() {
                return languageModels.size();
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.item_my_interest, null);
                }
                RelativeLayout root = (RelativeLayout) v.findViewById(R.id.root);
                final LanguageModel language = languageModels.get(position);
                TextViewNormal tvName = (TextViewNormal) v.findViewById(R.id.tvNameAnswer);
                tvName.setText(language.language);
                final CheckBox r = (CheckBox) v.findViewById(R.id.checbox);
                r.setChecked(language.isCheck);
                root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (!language.isCheck) {
                            int count = 0;
                            for (int i = 0; i < languageModels.size(); i++) {
                                if (languageModels.get(i).isCheck) {
                                    count++;
                                }
                            }
                            if (count >= 3) {
                                Toast.makeText(LanguageActivity.this, getString(R.string.limited_language), Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                        language.isCheck = !language.isCheck;
                        notifyDataSetChanged();
                    }
                });
                return v;
            }
        };
        list.setAdapter(adapter);
    }
}
