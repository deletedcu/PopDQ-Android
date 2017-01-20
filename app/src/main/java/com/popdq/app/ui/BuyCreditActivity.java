//package com.azstack.quickanswer.ui;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.ListView;
//import android.widget.RadioButton;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.anjlab.android.iab.v3.BillingProcessor;
//import com.anjlab.android.iab.v3.SkuDetails;
//import com.anjlab.android.iab.v3.TransactionDetails;
//import com.azstack.quickanswer.R;
//import com.azstack.quickanswer.util.Utils;
//import com.azstack.quickanswer.utils_billing.IabHelper;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class BuyCreditActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {
//    BillingProcessor bp;
//    private static final String TAG = "BuyCreditActivity";
//    ListView list;
//    private List<Credit> credits;
//    private Button btnBottom;
//    int selectedPosition = 0;
//    String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkippt229LqtkZT0RZ5hZkTRxPdP2Oc2NcCI3gkgUbCs/vvXJqL8qM71DvQ7zH9qTXHYO55la600JSJiwfuvOfzTvyLJdm1Lr9Xkj88r0jvQ8p+VWYc0oLuI5VzZq2prTUtag2nFEBd4tms1YarXfh/XihRM8EUTav1dcouagrvBbBoojt+hFtzwmebJ8tvbeUygR0KqATvsPlJY2+tLmnvULY8YpeYF5fZPJLwdknntMoMmwtriEqPvoelVdyyb2O11yt5KLBzUJidq/ODPQ2YYi31HvZTaR39fljTWRZp9fXpjpZMo3xyqHTVb70/LWsdT6StvI68bznI4fWhzWbwIDAQAB";
//    // The helper object
//    IabHelper mHelper;
//    static final String ITEM_SKU = "android.test.purchased";
//
//    public String PRODUCTS_ID[] = new String[]{"com.azstack.item1", "com.azstack.item2", "com.azstack.item3", "com.azstack.item4", ITEM_SKU};
////    public String PRODUCTS_ID[] = new String[]{"android.test.purchased", "android.test.canceled", "android.test.refunded", "android.test.item_unavailable", ITEM_SKU};
//
//    int itemProduct = 0;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_buy_credit);
//        bp = new BillingProcessor(this, base64EncodedPublicKey, this);
////        mHelper = new IabHelper(this, base64EncodedPublicKey);
////        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
////            public void onIabSetupFinished(IabResult result) {
////                Toast.makeText(BuyCreditActivity.this, "Problem setting up In-app Billing: " + result, Toast.LENGTH_SHORT).show();
////                if (!result.isSuccess()) {
////                    // Oh noes, there was a problem.
////                    Log.d(TAG, "Problem setting up In-app Billing: " + result);
////
////                } else {
////                    Toast.makeText(BuyCreditActivity.this, "startSetup isSuccess", Toast.LENGTH_SHORT).show();
////
////                }
////                // Hooray, IAB is fully set up!
////            }
////        });
//
//        list = (ListView) findViewById(R.id.list);
//        Utils.setActionBar(this, getString(R.string.buy_credit), R.drawable.btn_back);
//        initListCredits();
//        btnBottom = ((Button) findViewById(R.id.btnBottom));
//        btnBottom.setText(getString(R.string.buy_credit));
//        btnBottom.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(BuyCreditActivity.this, credits.get(selectedPosition).cost + "", Toast.LENGTH_SHORT).show();
//                bp.purchase(BuyCreditActivity.this, PRODUCTS_ID[itemProduct]);
////                try {
////                    mHelper.launchPurchaseFlow(BuyCreditActivity.this, PRODUCTS_ID[itemProduct], 10001,
////                            new IabHelper.OnIabPurchaseFinishedListener() {
////                                @Override
////                                public void onIabPurchaseFinished(IabResult result, Purchase info) {
////                                    Toast.makeText(BuyCreditActivity.this, "onIabPurchaseFinished " + result.toString(), Toast.LENGTH_SHORT).show();
////                                }
////                            });
////                } catch (IabHelper.IabAsyncInProgressException e) {
////                    e.printStackTrace();
////                }
//
//            }
//        });
//
//        ((Button) findViewById(R.id.btnConsume)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                for (int i = 0; i < PRODUCTS_ID.length; i++) {
//                    try {
//                        bp.consumePurchase(PRODUCTS_ID[i]);
//                    } catch (Exception e) {
//
//                    }
//                }
//            }
//        });
//
//        ((Button) findViewById(R.id.btnGetListProduct)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                ArrayList aa = new ArrayList();
//                for (int i = 0; i < PRODUCTS_ID.length; i++) {
//                    aa.add(PRODUCTS_ID[i]);
//                }
//
//                List<SkuDetails> s = bp.getPurchaseListingDetails(aa);
//                SkuDetails skuDetails = null;
//                StringBuilder stringBuilder = new StringBuilder();
//                for (int i = 0; i < PRODUCTS_ID.length; i++) {
//                    skuDetails = s.get(i);
//                    stringBuilder.append(skuDetails.priceText + "--" + skuDetails.description + "--" + skuDetails.productId + "\n");
//                }
//
//                try {
//                    Toast.makeText(BuyCreditActivity.this, stringBuilder.toString(), Toast.LENGTH_SHORT).show();
//                } catch (Exception e) {
//
//                }
//            }
//        });
//
//    }
//
//    @Override
//    public void onDestroy() {
//        if (bp != null)
//            bp.release();
//        super.onDestroy();
//    }
//
//    public void initListCredits() {
//        credits = new ArrayList<>();
//        credits.add(new Credit(0.99f));
//        credits.add(new Credit(4.99f));
//        credits.add(new Credit(9.99f));
//        credits.add(new Credit(49.99f));
//        credits.add(new Credit(499.99f));
//
//
//        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this, R.layout.item_credit) {
//            @Override
//            public int getCount() {
//                return credits.size();
//            }
//
//
//            @Override
//            public View getView(final int position, View convertView, ViewGroup parent) {
//                View v = convertView;
//                if (v == null) {
//                    LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                    v = vi.inflate(R.layout.item_credit, null);
//                    RadioButton r = (RadioButton) v.findViewById(R.id.radioButton);
//                }
//                RelativeLayout root = (RelativeLayout) v.findViewById(R.id.root);
//                Credit credit = credits.get(position);
//                TextView tvCost = (TextView) v.findViewById(R.id.tvCost);
////                TextView tvCredit = (TextView) v.findViewById(R.id.tvCredit);
//                tvCost.setText("$ " + credit.cost);
////                tvCredit.setText(credit.credit + " Credits");
//                RadioButton r = (RadioButton) v.findViewById(R.id.radioButton);
//                r.setChecked(position == selectedPosition);
//                r.setTag(position);
//                root.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        itemProduct = position;
//                        selectedPosition = position;
//                        notifyDataSetChanged();
//                    }
//                });
//                return v;
//            }
//
//
//        };
//        list.setAdapter(adapter);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
//
//        Toast.makeText(BuyCreditActivity.this, "onActivityResult(" + requestCode + "," + resultCode + "," + data, Toast.LENGTH_SHORT).show();
//        // Pass on the activity result to the helper for handling
////        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
////            // not handled, so handle it ourselves (here's where you'd
////            // perform any handling of activity results not related to in-app
////            // billing...
////            super.onActivityResult(requestCode, resultCode, data);
////        }
////        else {
////            Log.d(TAG, "onActivityResult handled by IABUtil.");
////
////        }
//
//        if (!bp.handleActivityResult(requestCode, resultCode, data))
//            super.onActivityResult(requestCode, resultCode, data);
//    }
//
//    @Override
//    public void onProductPurchased(String productId, TransactionDetails details) {
//        Toast.makeText(BuyCreditActivity.this, "onProductPurchased "+productId, Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onPurchaseHistoryRestored() {
//        Toast.makeText(BuyCreditActivity.this, "onPurchaseHistoryRestored", Toast.LENGTH_SHORT).show();
//
//    }
//
//    @Override
//    public void onBillingError(int errorCode, Throwable error) {
//        Toast.makeText(BuyCreditActivity.this, "onBillingError "+errorCode+"\n", Toast.LENGTH_SHORT).show();
//        try {
//            Toast.makeText(BuyCreditActivity.this, "onBillingError " + errorCode + "\n" + error.toString(), Toast.LENGTH_SHORT).show();
//        }catch (Exception e){
//
//        }
//    }
//
//    @Override
//    public void onBillingInitialized() {
//        Toast.makeText(BuyCreditActivity.this, "onBillingInitialized", Toast.LENGTH_SHORT).show();
//
//    }
//
//
//    public class Credit {
//        public float cost;
//
//        public Credit(float cost) {
//            this.cost = cost;
//        }
//
//    }
//}
