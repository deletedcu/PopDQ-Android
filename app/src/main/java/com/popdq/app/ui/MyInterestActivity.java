package com.popdq.app.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.popdq.app.MainActivity;
import com.popdq.app.R;
import com.popdq.app.connection.VolleyUtils;
import com.popdq.app.fragment.FeedFragment;
import com.popdq.app.model.Interest;
import com.popdq.app.model.Result;
import com.popdq.app.util.NotificationUtil;
import com.popdq.app.util.PreferenceUtil;
import com.popdq.app.util.Utils;
import com.popdq.app.values.Values;
import com.popdq.app.view.textview.TextViewNormal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyInterestActivity extends BaseActivity {
    private static final String TAG = "MyInterestActivity";
    private ListView listInterest;
    private List<Interest> myInterests;
    private SharedPreferences sharedPreferences;
    private String token;
    private boolean isFromEditProfile;
    private String idCategories;
    private boolean isNewUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_interest);
        if (getIntent().hasExtra("isNew")) {
            isNewUser = true;
        } else isNewUser = false;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        listInterest = (ListView) findViewById(R.id.listInterest);
        token = sharedPreferences.getString(Values.TOKEN, "");
        isFromEditProfile = getIntent().getBooleanExtra(Values.GET_CATEGORY, false);
//        idCategories = PreferenceUtil.getCategoryArr(this);
        if (getIntent().hasExtra(Values.category)) {
            idCategories = getIntent().getStringExtra(Values.category);

            Log.e("MY INTEREST EDIT", idCategories);
        }
        Utils.setActionBar(this, getString(R.string.my_interest), R.drawable.btn_back);
        if (isFromEditProfile) {
            ((TextViewNormal) findViewById(R.id.title)).setText(getString(R.string.my_area_expertise).toUpperCase());
            ((TextViewNormal) findViewById(R.id.des)).setVisibility(View.GONE);
            Utils.setBottomButton(this, "OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    StringBuilder stringBuilderId = new StringBuilder("[");
                    int length = myInterests.size();
                    for (int i = 0; i < length; i++) {
                        if (myInterests.get(i).isMyInterest())
                            stringBuilderId.append((myInterests.get(i).get_id()) + ",");
                    }
                    stringBuilderId.deleteCharAt(stringBuilderId.length() - 1);
                    stringBuilderId.append("]");

                    StringBuilder stringBuilderName = new StringBuilder();
                    for (int i = 0; i < length; i++) {
                        if (myInterests.get(i).isMyInterest())
                            stringBuilderName.append(myInterests.get(i).getName() + ", ");
                    }
                    if (stringBuilderName.length() < 2) {
                        finish();
                    } else {
                        stringBuilderName.deleteCharAt(stringBuilderName.length() - 2);

                        Intent intent = new Intent();
                        intent.putExtra(Values.category_id, stringBuilderId.toString());
                        intent.putExtra(Values.category_name, stringBuilderName.toString());
                        setResult(Activity.RESULT_OK, intent);

                        Log.e("LOG UPDATE INTEREST", stringBuilderId.toString());
//                        PreferenceUtil.getInstanceEditor(getApplicationContext()).putString(Values.category_id, stringBuilderId.toString());
//                        PreferenceUtil.getInstanceEditor(getApplicationContext()).commit();

                        finish();

                    }
                }
            });

        } else
            Utils.setBottomButton(this, getString(R.string.save), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    StringBuilder stringBuilder = new StringBuilder("[");
                    int length = myInterests.size();
                    for (int i = 0; i < length; i++) {
                        if (myInterests.get(i).isMyInterest())
                            stringBuilder.append((myInterests.get(i).getId()) + ",");
                    }
                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                    stringBuilder.append("]");
                    if (stringBuilder.toString().length() >= 3) {
                        update(stringBuilder.toString());
                        if(isNewUser){
                            regsiterNotif();
                        }
                    } else {
                        Toast.makeText(MyInterestActivity.this, getString(R.string.my_interest_select), Toast.LENGTH_SHORT).show();
                    }
                    Log.e(TAG, stringBuilder.toString());
//                Toast.makeText(MyInterestActivity.this, stringBuilder, Toast.LENGTH_SHORT).show();

                }
            });
        initData();

    }


    public void initData() {
        myInterests = new ArrayList<>();
//        myInterests.add(new Interest(1, "Technology"));
//        myInterests.add(new Interest(2, "LifeStyle"));
//        myInterests.add(new Interest(3, "Photography"));
//        myInterests.add(new Interest(4, "Cooking"));
//        myInterests.add(new Interest(5, "Art"));
//        myInterests.add(new Interest(1, "Technology"));
//        myInterests.add(new Interest(2, "LifeStyle"));
//        myInterests.add(new Interest(3, "Photography"));
//        myInterests.add(new Interest(4, "Cooking"));
//        myInterests.add(new Interest(5, "Art"));
//
//        myInterests.add(new Interest(1, "Technology"));
//        myInterests.add(new Interest(2, "LifeStyle"));
//        myInterests.add(new Interest(3, "Photography"));
//        myInterests.add(new Interest(4, "Cooking"));
//        myInterests.add(new Interest(5, "Art"));
        getData();
    }

    public void getData() {
        VolleyUtils volleyUtils = new VolleyUtils(this, Values.URL_USER_GET_LIST_INTEREST);
        volleyUtils.addParam(Values.TOKEN, token);
        volleyUtils.setOnRequestComplete(new VolleyUtils.OnRequestListenner() {
            @Override
            public void onSussces(String reponse, Result result) {
                myInterests = VolleyUtils.getListModelFromRespone(reponse, Interest.class);
                if (isFromEditProfile) {

                } else {
                    Interest interestSelectAll = new Interest(0, getString(R.string.select_all));
                    interestSelectAll.setMyInterest(true);
                    myInterests.add(0, interestSelectAll);
                    if (isNewUser) {
                        for (int i = 1; i < myInterests.size(); i++) {
                            myInterests.get(i).setMyInterest(true);
                        }
                    } else {
                        for (int i = 1; i < myInterests.size(); i++) {
                            if (!myInterests.get(i).isMyInterest()) {
                                myInterests.get(0).setMyInterest(false);
                                break;
                            }
                        }
                    }

                }








                if (isFromEditProfile) {


                    try {

                        if(idCategories.equals("[]")){
                            idCategories = "[0]";
                        }
                        String newId = idCategories;

                        Log.e("numberscategros", idCategories);
                        newId = newId.replace("[", "");
                        newId = newId.replace("]", "");

                        String[] numbers = newId.split(",");//if spaces are uneven, use \\s+ instead of " "

                        List<Integer> listSelectedInterest = new ArrayList<Integer>();
                        List<Integer> listMyInterest = new ArrayList<Integer>();
//                List<Integer> selectedInterest = new ArrayList<Integer>();
                        for (String number : numbers) {
                            Log.e("numbers", Arrays.toString(numbers));
                            listSelectedInterest.add(Integer.valueOf(number));
                        }
                        for (int i = 0; i < myInterests.size(); i++) {
                            Log.e("MY INTEREST SERVER", String.valueOf(myInterests.get(i).get_id()));
                            listMyInterest.add(myInterests.get(i).get_id());

                        }
                        for (int w = 0; w < listSelectedInterest.size(); w++) {
                            Log.e("MY INTEREST CLIENT", String.valueOf(listSelectedInterest.get(w)));

                        }


                        for (int x = 0; x < listMyInterest.size(); x++) {
                            Log.e("LOOP SERVER INTEREST", String.valueOf(listMyInterest.get(x)));


                            if (listSelectedInterest.contains(listMyInterest.get(x))) {

                                Log.e("MY GET INTEREST", String.valueOf(listMyInterest.get(x)));
                                myInterests.get(x).setMyInterest(true);

                            } else {

                                myInterests.get(x).setMyInterest(false);

                            }


                        }

                    } catch (NumberFormatException e){
                        Log.e("Exception", String.valueOf(e));
                    }




                }
                ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(MyInterestActivity.this, R.layout.item_my_interest) {
                    @Override
                    public int getCount() {
                        return myInterests.size();
                    }

                    @Override
                    public View getView(final int position, View convertView, ViewGroup parent) {
                        View v = convertView;
                        if (v == null) {
                            LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            v = vi.inflate(R.layout.item_my_interest, null);
                        }
                        RelativeLayout root = (RelativeLayout) v.findViewById(R.id.root);
                        final Interest myInterest = myInterests.get(position);
                        TextViewNormal tvName = (TextViewNormal) v.findViewById(R.id.tvNameAnswer);
                        tvName.setText(myInterest.getName());
                        final CheckBox r = (CheckBox) v.findViewById(R.id.checbox);

                        r.setChecked(myInterest.isMyInterest());
                        root.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (isFromEditProfile) {
                                    if (!myInterest.isMyInterest()) {
                                        int count = 0;
                                        for (Interest interest : myInterests) {
                                            if (interest.isMyInterest()) {
                                                count++;
                                            }
                                        }
                                        if (count >= 3) {
                                            Toast.makeText(MyInterestActivity.this, getString(R.string.description_interest_limit), Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    }

                                    myInterests.get(position).setMyInterest(!myInterests.get(position).isMyInterest());
                                } else {
                                    myInterests.get(position).setMyInterest(!myInterests.get(position).isMyInterest());
                                    if (position == 0) {
                                        for (Interest interest : myInterests) {
                                            interest.setMyInterest(myInterests.get(0).isMyInterest());
                                        }
                                    } else {
                                        int count = 0;
                                        for (int i = 1; i < myInterests.size(); i++) {
                                            if (myInterests.get(i).isMyInterest()) {
                                                count++;
                                            }
                                        }
                                        if (count >= myInterests.size() - 1) {
                                            myInterests.get(0).setMyInterest(true);
                                        } else {
                                            myInterests.get(0).setMyInterest(false);

                                        }
                                    }
                                }


//                                if (!isFromEditProfile && position == 0) {
//                                    if (!isFromEditProfile)
//                                        for (Interest interest : myInterests) {
//                                            interest.setMyInterest(!r.isChecked());
//                                        }
//                                    else {
//                                        Toast.makeText(MyInterestActivity.this, getString(R.string.description_interest_limit), Toast.LENGTH_SHORT).show();
//                                        return;
//                                    }
//                                } else {
//                                    if (!myInterest.isMyInterest()) {
//                                        int count = 0;
//                                        for (int i = 0; i < myInterests.size(); i++) {
//                                            if (myInterests.get(i).isMyInterest()) {
//                                                count++;
//
//                                            }
//                                        }
//                                        if (isFromEditProfile)
//                                            if (count >= 3) {
//                                                Toast.makeText(MyInterestActivity.this, getString(R.string.description_interest_limit), Toast.LENGTH_SHORT).show();
//                                                return;
//                                            }
//                                    }
//                                    myInterest.setMyInterest(!myInterest.isMyInterest());
//                                }

                                notifyDataSetChanged();
                            }
                        });
                        return v;
                    }


                };
                listInterest.setAdapter(adapter);

            }

            @Override
            public void onError(String error) {

            }
        });
        volleyUtils.query();
    }

    private void regsiterNotif() {

        VolleyUtils volleyUtils = new VolleyUtils(this, Values.URL_PUSH_NOTIFICATION);
        volleyUtils.addParam(Values.TOKEN, token);
//        volleyUtils.addParam(Values.newInterest, newList);
        volleyUtils.setOnRequestComplete(new VolleyUtils.OnRequestListenner() {
            @Override
            public void onSussces(String response, Result result) {

//                Toast.makeText(MyInterestActivity.this, response, Toast.LENGTH_SHORT).show();
                if (VolleyUtils.requestSusscess(response)) {
//                    if (getIntent().hasExtra("isNew")) {
//                        NotificationUtil.sendNotification(MyInterestActivity.this, getString(R.string.noti_wellcome_title),
//                                getString(R.string.noti_wellcome_des), MainActivity.class);
//                        Intent intent = new Intent(MyInterestActivity.this, MainActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(intent);
//                        finish();
//                    } else {
//                        Intent intent = new Intent("update_interest");
//                        LocalBroadcastManager.getInstance(MyInterestActivity.this).sendBroadcast(intent);
//                        finish();
//
//                    }


                }

            }

            @Override
            public void onError(String error) {

            }
        });
        volleyUtils.query();

    }


    public void update(String newList) {
        VolleyUtils volleyUtils = new VolleyUtils(this, Values.URL_USER_UPDATE_LIST_INTEREST);
        volleyUtils.addParam(Values.TOKEN, token);
        volleyUtils.addParam(Values.newInterest, newList);
        volleyUtils.setOnRequestComplete(new VolleyUtils.OnRequestListenner() {
            @Override
            public void onSussces(String response, Result result) {

//                Toast.makeText(MyInterestActivity.this, response, Toast.LENGTH_SHORT).show();
                if (VolleyUtils.requestSusscess(response)) {

//                    Log.e("NEW USER", String.valueOf(isNewUser));

                    if (getIntent().hasExtra("isNew")) {


                        Log.e("IS NEW USER", "TRUE");
                        NotificationUtil.sendNotification(MyInterestActivity.this, getString(R.string.noti_wellcome_title),
                                getString(R.string.noti_wellcome_des), MainActivity.class);
                        Intent intent = new Intent(MyInterestActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.e("IS NEW USER", "FALSE");
                        Intent intent = new Intent("update_interest");
                        LocalBroadcastManager.getInstance(MyInterestActivity.this).sendBroadcast(intent);
                        finish();
//
                    }


                }

            }

            @Override
            public void onError(String error) {

            }
        });
        volleyUtils.query();

    }
}
