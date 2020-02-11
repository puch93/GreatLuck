package kr.co.planet.newgreatluck.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.android.billingclient.api.Purchase;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.planet.newgreatluck.R;
import kr.co.planet.newgreatluck.billing.BIllingManager;
import kr.co.planet.newgreatluck.databinding.ActivitySubscriptionBinding;
import kr.co.planet.newgreatluck.preference.UserPref;
import kr.co.planet.newgreatluck.server.Cvalue;
import kr.co.planet.newgreatluck.server.HttpController;
import kr.co.planet.newgreatluck.util.Common;

public class SubscriptionAct extends BasicAct {
    ActivitySubscriptionBinding binding;
    Activity act;

    ActionBar actionBar;

    /* billing */
    public static BIllingManager manager;
    public static String playStoreLoginState = "error";
    public static final String SUBS_CONTENTS = "sub_contents";
    private String itemId;
    private String price;
    private static final String SUBS_ONE = "subs_one_month";
    private static final String SUBS_THREE = "subs_three_month";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_subscription, null);
        act = this;


        setBilling();
        setListener();
        setActionBar();

        binding.llSubs01.performClick();
    }

    private void setActionBar() {
        setSupportActionBar(binding.toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle(null);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.gf_arrow);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setListener() {
        binding.llSubs01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemId = SUBS_ONE;
                price = "2500";
                binding.llSubs02.setSelected(false);
                binding.llSubs01.setSelected(true);
            }
        });

        binding.llSubs02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemId = SUBS_THREE;
                price = "6000";
                binding.llSubs02.setSelected(true);
                binding.llSubs01.setSelected(false);
            }
        });

        binding.ivSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSubscription();
            }
        });

        binding.flCharm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(act, CharmAct.class));
            }
        });
    }

    private void checkSubscription() {
        switch (MainAct.playStoreLoginState) {
            case "noAccount":
                Common.showToast(act, "구글 플레이스토어 계정 정보가 없습니다");
                break;
            case "error":
                Common.showToast(act, "에러가 발생했습니다. 다시 시도해 주세요.");
                break;
            case "Y":
                Common.showToast(act, "이미 구독중입니다.");
                finish();
                break;
            default:
                manager.purchase(itemId);
                break;
        }
    }

    private void setBilling() {
        manager = new BIllingManager(act, new BIllingManager.AfterBilling() {
            @Override
            public void sendResult(com.android.billingclient.api.Purchase purchase) {
                Log.e(TAG, "result purchase: " + purchase);
                Log.e(TAG, "purchase.getSku(): " + purchase.getSku());
                Log.e(TAG, "purchase.getPurchaseTime(): " + purchase.getPurchaseTime());
                Log.e(TAG, "purchase.getPurchaseToken(): " + purchase.getPurchaseToken());
                Log.e(TAG, "purchase.getPackageName(): " + purchase.getPackageName());
                Log.e(TAG, "purchase.getOrderId(): " + purchase.getOrderId());
                Log.e(TAG, "purchase.getDeveloperPayload(): " + purchase.getDeveloperPayload());
                Log.e(TAG, "purchase.getPurchaseState(): " + purchase.getPurchaseState());
                Log.e(TAG, "purchase.getOriginalJson(): " + purchase.getOriginalJson());
                Log.e(TAG, "purchase.getSignature(): " + purchase.getSignature());

                sendPurchaseResult(purchase);
                playStoreLoginState = "Y";
                Common.showToast(act, "결제가 완료되었습니다");
            }

            @Override
            public void getSubsriptionState(String subscription, Purchase purchase) {
                playStoreLoginState = subscription;
            }
        });
    }

    private void exit() {
//        Intent intent = new Intent();
//        intent.putExtra("playStoreLoginState", "Y");
//        setResult(RESULT_OK, intent);

        MainAct.playStoreLoginState = "Y";
        setResult(RESULT_OK);
        finish();
    }

    private void sendPurchaseResult(Purchase purchase) {
        final HttpController hc = new HttpController(Cvalue.Login);
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<NameValuePair> p = new ArrayList<NameValuePair>();
                p.add(new BasicNameValuePair("siteUrl", Cvalue.Domain));
                p.add(new BasicNameValuePair("APPCONNECTCODE", Build.FINGERPRINT));

                p.add(new BasicNameValuePair("dbControl", "setUserPayment"));

                p.add(new BasicNameValuePair("USERIDX", UserPref.getMidx(act)));
                p.add(new BasicNameValuePair("p_store_type", "GOOGLE"));
                p.add(new BasicNameValuePair("p_purchasePrice", price));
//                p.add(new BasicNameValuePair("item_name", selectedData.getId()));
                p.add(new BasicNameValuePair("p_purchage_item_name", itemId));
                p.add(new BasicNameValuePair("ITEMIDX", "1000000"));

                p.add(new BasicNameValuePair("p_purchasetime", String.valueOf(purchase.getPurchaseTime())));
                p.add(new BasicNameValuePair("p_itemcount", "1"));
                p.add(new BasicNameValuePair("p_orderid", purchase.getOrderId()));
                p.add(new BasicNameValuePair("p_info", purchase.getOriginalJson()));
                p.add(new BasicNameValuePair("p_signature", purchase.getPurchaseToken()));


                Log.i(TAG, "Purchase Put Info : " + p);
                final String res = hc.getResultStreamPost(p);
                Log.e(TAG, "Purchase Get Info : " + res);

                try {
                    JSONObject jo = new JSONObject(res);

                    act.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                           exit();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
