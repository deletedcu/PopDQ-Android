package com.popdq.app.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.popdq.app.R;
import com.popdq.app.connection.AnswerUtil;
import com.popdq.app.connection.FileUtil;
import com.popdq.app.connection.VolleyUtils;
import com.popdq.app.model.FileModel;
import com.popdq.app.model.Result;
import com.popdq.app.model.User;
import com.popdq.app.util.PreferenceUtil;
import com.popdq.app.util.Utils;
import com.popdq.app.values.Values;
import com.popdq.app.view.DialogBase;
import com.popdq.app.view.textview.TextViewNormal;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dang Luu on 26/07/2016.
 */
public class BaseProvideAnswerActivity extends BaseActivity {
    protected long question_id;
    protected String token;
    protected int method;
    protected String language_written,language_spoken;
    protected List<FileModel> fileModels;
    protected String title;
    protected String answer_content;
    protected Button btnBottom;
    protected long answer_id;
    ArrayList<String> myList = new ArrayList<>();

    protected boolean hasContent() {
        return true;
    }

    ;

    public void getDataFromIntent() {
        try {
            question_id = getIntent().getExtras().getLong(Values.question_id, 0);
            method = getIntent().getExtras().getInt(Values.method, 0);
            language_written = getIntent().getExtras().getString(Values.language_written, "en");
            language_spoken = getIntent().getExtras().getString(Values.language_written, "en");
            token = PreferenceUtil.getToken(this);
            fileModels = new ArrayList<>();
            title = getIntent().getExtras().getString(Values.title);
            answer_id = getIntent().getExtras().getLong(Values.answer_id,0);
            answer_content = getIntent().getExtras().getString(Values.answer_content);
            myList = (ArrayList<String>) getIntent().getSerializableExtra("mylist");
        }catch (Exception e){
        e.printStackTrace();
        }

    }


    public void setTitleBarText(String text) {
        ((RelativeLayout) findViewById(R.id.btnMenu)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasContent())
                    Utils.confirmExit(BaseProvideAnswerActivity.this);
                else finish();
            }
        });

        ((TextViewNormal) findViewById(R.id.title)).setText(text);
        ((ImageView) findViewById(R.id.iconNavigation)).setImageResource(R.drawable.btn_back);
    }

    @Override
    public void onBackPressed() {
        if (hasContent())
            Utils.confirmExit(BaseProvideAnswerActivity.this);
        else finish();
    }

    public void submitAnswer(final String content) {
        if (!hasContent()) {
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Upload files...");
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog.show();
            }

            @Override
            protected String doInBackground(Void... voids) {
                String response = null;
                try {
                    response = FileUtil.sendFile(Values.URL_FILE_UPLOAD, token, fileModels);
                    Log.e("LOG RESPONSE BG", response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return response;
            }

            //
            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);
                progressDialog.dismiss();
                Log.e("LOG RESPONSE CREATE", response);
                AnswerUtil.create(BaseProvideAnswerActivity.this, token, question_id, content, response, 
                        method, language_written,language_written, new VolleyUtils.OnRequestListenner() {
                    @Override
                    public void onSussces(String response, Result result) {
                        if (result.getCode() == 0) {
                            Toast.makeText(BaseProvideAnswerActivity.this, getString(R.string.reply_success), Toast.LENGTH_SHORT).show();
                            User newUserInfo = VolleyUtils.getUserInfo(response);
                            User.changeCreditAndPutPrefernce(BaseProvideAnswerActivity.this, newUserInfo.getCredit_earnings(), newUserInfo.getCredit());

                            Intent intent1 = new Intent("update_interest");
                            LocalBroadcastManager.getInstance(BaseProvideAnswerActivity.this).sendBroadcast(intent1);

                            Intent intent = new Intent("change_count_reply");
                            sendBroadcast(intent);
                            finish();
                        } else {
                            Toast.makeText(BaseProvideAnswerActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });

            }
        }.execute();

    }

    public void submitUpdateAnswer(final String content) {
        if (!hasContent()) {
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Upload files...");
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog.show();
            }

            @Override
            protected String doInBackground(Void... voids) {
                String response = null;
                try {
                    response = FileUtil.sendFile(Values.URL_FILE_UPLOAD, token, fileModels);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return response;
            }

            //
            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);
                progressDialog.dismiss();

                Log.e("Answer UPDATE ATT",response);

;                AnswerUtil.update(BaseProvideAnswerActivity.this, token, question_id, content, answer_id, response, method, language_written, language_spoken, new VolleyUtils.OnRequestListenner() {
                    @Override
                    public void onSussces(String response, Result result) {
                        if (result.getCode() == 0) {
                            Toast.makeText(BaseProvideAnswerActivity.this, getString(R.string.reply_success), Toast.LENGTH_SHORT).show();
//                            User newUserInfo = VolleyUtils.getUserInfo(response);
//                            User.changeCreditAndPutPrefernce(BaseProvideAnswerActivity.this, newUserInfo.getCredit_earnings(), newUserInfo.getCredit());
//                            showDialogShare();

//                            Intent intent1 = new Intent("update_interest");
//                            LocalBroadcastManager.getInstance(BaseProvideAnswerActivity.this).sendBroadcast(intent1);
//
//                            Intent intent = new Intent("change_count_reply");
//                            sendBroadcast(intent);
                            finish();
                        } else {
                            Toast.makeText(BaseProvideAnswerActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });

            }
        }.execute();

    }

    public void showDialogShare() {
        final DialogBase dialogBase = new DialogBase(this);
        dialogBase.setTitle(getString(R.string.notice));
        dialogBase.setMessage(getString(R.string.share_message));
        dialogBase.setTextOk("YES");
        dialogBase.setOnClickOkListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Utils.shareIntent(BaseProvideAnswerActivity.this,null);
                finish();
            }
        });
        dialogBase.setOnClickCancelListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBase.dismiss();
                finish();
            }
        });
        dialogBase.show();
    }
}
