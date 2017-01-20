package com.popdq.app.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.popdq.app.R;
import com.popdq.app.connection.CreditUtils;
import com.popdq.app.connection.VolleyUtils;
import com.popdq.app.model.Credit;
import com.popdq.app.model.Result;
import com.popdq.app.util.PreferenceUtil;
import com.popdq.app.util.Utils;
import com.popdq.app.utils_billing.IabHelper;
import com.popdq.app.utils_billing.IabResult;
import com.popdq.app.utils_billing.Purchase;
import com.popdq.app.view.InputForm;
import com.popdq.app.view.TranferCreditPopup;
import com.popdq.app.view.textview.TextViewNormal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.branch.referral.Branch;
import io.branch.referral.util.BranchEvent;

public class BuyCreditActivityBackup extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "BuyCreditActivity";
    private ListView list;
    private InputForm inputAmount;
    private Button btnCancel, btnConvert;
    private Button btnBottom;
    private List<Credit> credits;
    int selectedPosition = 0;
    private String token;


    String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAoBpC1VxFs0cfVcq9Knmr8JLkycAxfoefhaVc06Z20bUU5vNfNL6L/2b/dnK9q4SJXYXHteRqZM8/5F6mMTytxUFw0efiGIYfFiEpZqFrnH/m/VtEJJz3JrtVhtY3TAixMf5PlYVPksUX0dWA8zpztW8L7PwBuP9zM1AEgBn4Qv0oUAgTwSGvo/0bJ9J48ZhpKsbdQhonaskg3oDVEk6OrYRmEXtO1S0bpWh0cCe+1ryZhjHXrwizSRSwO7BC8h0hUCfo4irZodfU5yg8Yo72MCtER1raVejrngHlZhBeYk0O2IPy6k+orCC/0PuDsxqyelGodti3elzL0voepQL5EwIDAQAB";
    IabHelper mHelper;
    static final String ITEM_SKU = "android.test.purchased";

//    public String PRODUCTS_ID[] = new String[]{"com.azstack.item1", "com.azstack.item2", "com.azstack.item3", "com.azstack.item4", "com.azstack.item6", "com.azstack.item5"};
//    public String PRODUCTS_ID[] = new String[]{"android.test.purchased", "android.test.canceled", "android.test.refunded", "android.test.item_unavailable", ITEM_SKU};

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result,
                                          Purchase purchase) {
            Log.e("Purchased result:", result.toString() + " " + result.getResponse());
            if (result.isFailure()) {
                if (result.getResponse() == 7) {
                    sendResultPurchaseToServer(token, getInfoPurchased(credits.get(selectedPosition).getName()));
                }
                // Handle error
                Toast.makeText(BuyCreditActivityBackup.this, "Purchase error!! " + result.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            } else {
                sendResultPurchaseToServer(PreferenceUtil.getToken(BuyCreditActivityBackup.this), purchase);
            }


        }
    };
//    IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener
//            = new IabHelper.QueryInventoryFinishedListener() {
//        public void onQueryInventoryFinished(IabResult result,
//                                             Inventory inventory) {
//            Toast.makeText(BuyCreditActivityBackup.this, "onQueryInventoryFinished " + result.toString(), Toast.LENGTH_SHORT).show();
//
//            if (result.isFailure()) {
//                // Handle failure
//            } else {
//                try {
//                    for (int i = 0; i < PRODUCTS_ID.length; i++) {
//                        if (inventory.hasPurchase(PRODUCTS_ID[i])) {
//                            mHelper.consumeAsync(inventory.getPurchase(PRODUCTS_ID[i]),
//                                    mConsumeFinishedListener);
//                            Log.e(TAG, "Consuming: " + PRODUCTS_ID[i]);
//                            Toast.makeText(BuyCreditActivityBackup.this, "Consuming: " + PRODUCTS_ID[i], Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//
//
//                } catch (IabHelper.IabAsyncInProgressException e) {
//                    e.printStackTrace();
//                }
////                Toast.makeText(BuyCreditActivity.this, "onQueryInventoryFinished " + result.toString(), Toast.LENGTH_SHORT).show();
//
////                mHelper.consumeAsync(inventory.getPurchase(ITEM_SKU),
////                        mConsumeFinishedListener);
//            }
//        }
//    };

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener =
            new IabHelper.OnConsumeFinishedListener() {
                public void onConsumeFinished(Purchase purchase,
                                              IabResult result) {

                    if (result.isSuccess()) {
//                        Toast.makeText(BuyCreditActivityBackup.this, "onConsumed " + purchase.toString(), Toast.LENGTH_SHORT).show();
                    } else {
                        // handle error
                    }
                }
            };
//    Purchase purchase;

    public Purchase getInfoPurchased(String sku) {
        Purchase purchase = null;
        Bundle ownedItems = null;
        try {
            ownedItems = mHelper.getListPurchaed();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        int response = ownedItems.getInt("RESPONSE_CODE");
        if (response == 0) {
            ArrayList<String> ownedSkus =
                    ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
            ArrayList<String> purchaseDataList =
                    ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
            ArrayList<String> signatureList =
                    ownedItems.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");
            String continuationToken =
                    ownedItems.getString("INAPP_CONTINUATION_TOKEN");

            for (int i = 0; i < ownedSkus.size(); ++i) {
                if (ownedSkus.get(i).equals(sku)) {
                    String purchasedData = purchaseDataList.get(i);
                    String signatureData = signatureList.get(i);
                    try {
                        purchase = new Purchase(IabHelper.ITEM_TYPE_INAPP, purchasedData, signatureData);
                        return purchase;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return purchase;
    }

    public void sendResultPurchaseToServer(String token, final Purchase purchase) {
        if (purchase == null) {
            Toast.makeText(BuyCreditActivityBackup.this, "Buy error! purchase = null", Toast.LENGTH_SHORT).show();
            return;
        }
        String signatureData = purchase.getSignature();
        JSONObject purchasedData = null;
        try {
            purchasedData = new JSONObject(purchase.getOriginalJson());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Receipt_dataModel receipt_dataModel = new Receipt_dataModel();
        receipt_dataModel.purchaseData = purchasedData;
        receipt_dataModel.dataSignature = signatureData;
        String r = receipt_dataModel.toString();
        Log.e("Buy credits:", "requesting to my server");

        CreditUtils.verify(this, token, r, new VolleyUtils.OnRequestListenner() {
            @Override
            public void onSussces(String response, Result result) {
                Log.e("Buy credits:", "respone: " + response);
//                NotificationUtil.sendNotification(BuyCreditActivityBackup.this, getString(R.string.app_name),
//                        getString(R.string.noti_credited), MyTransactionActivity.class);
                if (VolleyUtils.requestSusscess(response)) {
                    Branch.getInstance(getApplicationContext()).userCompletedAction(BranchEvent.SHARE_STARTED);
                    VolleyUtils.getUserAndPushPreference(BuyCreditActivityBackup.this, response);
                    LocalBroadcastManager.getInstance(BuyCreditActivityBackup.this).sendBroadcast(new Intent("change_user_more_tab"));
                    Intent intent = new Intent(BuyCreditActivityBackup.this, TransactionViewActivity.class);
                    startActivity(intent);
                    consume(purchase);
                    finish();
                }

            }

            @Override
            public void onError(String error) {

            }
        });
    }

    public void consume(Purchase purchase) {
        if (purchase == null) {
            Toast.makeText(BuyCreditActivityBackup.this, "consume error! purchase = null: ", Toast.LENGTH_SHORT).show();
            return;

        }
        try {
            mHelper.consumeAsync(purchase,
                    mConsumeFinishedListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "Consuming: " + purchase);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_credit);
        btnBottom = ((Button) findViewById(R.id.btnBottom));
        btnBottom.setText(getString(R.string.buy_credit));
        token = PreferenceUtil.getToken(this);
        mHelper = new IabHelper(this, base64EncodedPublicKey);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
//                Toast.makeText(BuyCreditActivityBackup.this, "Problem setting up In-app Billing: " + result, Toast.LENGTH_SHORT).show();
                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    Log.d(TAG, "Problem setting up In-app Billing: " + result);

                }
            }
        });
        list = (ListView) findViewById(R.id.list);
        Utils.setActionBar(this, getString(R.string.buy_credit), R.drawable.btn_back);
        initListCredits();

        ((TextViewNormal) findViewById(R.id.btnFromEarning)).setOnClickListener(this);
        btnBottom.setOnClickListener(this);
//        if (User.getInstance(this).getCredit_earnings() <= 0) {
//            ((TextViewNormal) findViewById(R.id.btnFromEarning)).setVisibility(View.GONE);
//        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");
            String dataPurchase = data.getStringExtra("INAPP_PURCHASE_DATA");

            Log.e("onResult Signature: ", dataSignature);
            Log.e("onResult Purchase: ", dataPurchase);
        }
        if (!mHelper.handleActivityResult(requestCode,
                resultCode, data)) {
            Log.e(TAG, data.toString());
            Toast.makeText(BuyCreditActivityBackup.this, "Error", Toast.LENGTH_SHORT).show();
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) try {
            mHelper.dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mHelper = null;
    }

    public void initListCredits() {
        credits = CreditUtils.getLocalListCredit(this);
        if (credits == null || credits.size() < 1) {
            CreditUtils.getListCanBuy(this, token, new VolleyUtils.OnRequestListenner() {
                @Override
                public void onSussces(String response, Result result) {
                    if (result.getCode() == 0) {
                        credits = CreditUtils.getListCreditFromRespone(response);
                        setDataListCredits();
                    }
                }

                @Override
                public void onError(String error) {

                }
            });
        } else {
            setDataListCredits();
        }

//        credits = new ArrayList<>();
//        credits.add(new Credit(0.99f));
//        credits.add(new Credit(4.99f));
//        credits.add(new Credit(9.99f));
//        credits.add(new Credit(49.99f));
//        credits.add(new Credit(99.99f));
//        credits.add(new Credit(299.99f));


    }

    public void setDataListCredits() {
        ArrayAdapter<Credit> adapter = new ArrayAdapter<Credit>(this, R.layout.item_credit) {
            @Override
            public int getCount() {
                return credits.size();
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.item_credit, null);
                    RadioButton r = (RadioButton) v.findViewById(R.id.radioButton);
                }
                RelativeLayout root = (RelativeLayout) v.findViewById(R.id.root);
                Credit credit = credits.get(position);
                TextViewNormal tvCost = (TextViewNormal) v.findViewById(R.id.tvCost);
//                TextView tvCredit = (TextView) v.findViewById(R.id.tvCredit);
                tvCost.setText("$ " + credit.getCredit());
//                tvCredit.setText(credit.credit + " Credits");
                RadioButton r = (RadioButton) v.findViewById(R.id.radioButton);
                r.setChecked(position == selectedPosition);
                r.setTag(position);
                root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        selectedPosition = position;
                        notifyDataSetChanged();
                    }
                });
                return v;
            }


        };
        list.setAdapter(adapter);
    }


    private Dialog dialogConvert;

//    public void showDialogConvert() {
//        dialogConvert = new Dialog(this);
//        dialogConvert.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogConvert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        dialogConvert.setContentView(R.layout.layout_dialog_convert_credit);
//        btnCancel = (Button) dialogConvert.findViewById(R.id.btnCancel);
//        btnConvert = (Button) dialogConvert.findViewById(R.id.btnConvert);
//
//        btnCancel.setTypeface(MyApplication.getInstanceTypeNormal(this));
//        btnConvert.setTypeface(MyApplication.getInstanceTypeNormal(this));
//        btnConvert.setOnClickListener(this);
//        btnCancel.setOnClickListener(this);
//        inputAmount = (InputForm) dialogConvert.findViewById(R.id.inputAmount);
//        inputAmount.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
//
//        dialogConvert.show();
//    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnBottom:
//                Toast.makeText(BuyCreditActivityBackup.this, credits.get(selectedPosition).cost + "", Toast.LENGTH_SHORT).show();
                try {
                    mHelper.launchPurchaseFlow(BuyCreditActivityBackup.this, credits.get(selectedPosition).getName(), 1001,
                            mPurchaseFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btnCancel:
                if (dialogConvert != null && dialogConvert.isShowing()) {
                    dialogConvert.dismiss();
                }
                break;
            case R.id.btnConvert:
//                float credit = 0;
//                try {
//                    credit = Float.parseFloat(inputAmount.getText().toString());
//                } catch (Exception e) {
//                    Toast.makeText(BuyCreditActivityBackup.this, "Credit format error!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                CreditUtils.convert(this, PreferenceUtil.getToken(this), credit, new VolleyUtils.OnRequestListenner() {
//                    @Override
//                    public void onSussces(String response, Result result) {
//                        if (VolleyUtils.requestSusscess(response)) {
//                            Toast.makeText(BuyCreditActivityBackup.this, "Convert success!", Toast.LENGTH_SHORT).show();
//                            VolleyUtils.getUserAndPushPreference(BuyCreditActivityBackup.this, response);
//                            sendBroadcast(new Intent("change_user"));
//                            if (dialogConvert != null && dialogConvert.isShowing())
//                                dialogConvert.dismiss();
//                        } else {
//                            Toast.makeText(BuyCreditActivityBackup.this, "Convert error!", Toast.LENGTH_SHORT).show();
//
//                        }
//                    }
//
//                    @Override
//                    public void onError(String error) {
//
//                    }
//                });
                break;

            case R.id.btnFromEarning:
                TranferCreditPopup.showDialogConvert(this, new TranferCreditPopup.TranferListener() {
                    @Override
                    public void onSuccess(float credit) {
//                        if (User.getInstance(BuyCreditActivityBackup.this).getCredit_earnings() <= 0) {
//                            ((TextViewNormal) findViewById(R.id.btnFromEarning)).setVisibility(View.GONE);
//                        }

                    }
                });
//                showDialogConvert();
                break;
        }
    }

//    public class Credit {
//        public float cost;
////        public int credit;
//
//        public Credit(float cost) {
//            this.cost = cost;
//        }
//
////        public Credit(float cost, int credit) {
////            this.cost = cost;
////            this.credit = credit;
////        }
//    }

    public static class Receipt_dataModel {
        public JSONObject purchaseData;
        public String dataSignature;

        @Override
        public String toString() {
            return "{\"dataSignature\": \"" + dataSignature + "\", \"purchaseData\": " + purchaseData.toString() + "}";
        }
    }
}
