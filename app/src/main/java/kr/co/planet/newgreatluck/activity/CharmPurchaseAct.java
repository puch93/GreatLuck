package kr.co.planet.newgreatluck.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.android.billingclient.api.Purchase;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.ads.InterstitialAd;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import kr.co.planet.newgreatluck.BuildConfig;
import kr.co.planet.newgreatluck.R;
import kr.co.planet.newgreatluck.billing.BillingCharmManager;
import kr.co.planet.newgreatluck.data.CharmData;
import kr.co.planet.newgreatluck.databinding.ActivityCharmPurchaseBinding;
import kr.co.planet.newgreatluck.preference.UserPref;
import kr.co.planet.newgreatluck.server.Cvalue;
import kr.co.planet.newgreatluck.server.HttpController;
import kr.co.planet.newgreatluck.util.Common;

public class CharmPurchaseAct extends BasicAct {
    ActivityCharmPurchaseBinding binding;
    Activity act;
    ActionBar actionBar;

    CharmData selectedData;
    String name, year, month, day;

    int type;

    private static final int TYPE_SHARE = 0;
    private static final int TYPE_SAVE = 1;

    ProgressDialog dialog;
    ProgressDialog save_dialog;

    Bitmap charm_bitmap;

    /* billing */
    public static BillingCharmManager manager;
    public static String isPurchased = "error";

    //애드몹
    private InterstitialAd mInterstitialAd;
    private static final int READY = 100;
    private static final int OK = 101;
    private static final int FAIL = 102;
    private int admob_state = READY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_charm_purchase, null);
        act = this;

        save_dialog = new ProgressDialog(act);
        save_dialog.setMessage("부적을 저장하는 중입니다..");
        save_dialog.setCancelable(false);


        dialog = new ProgressDialog(act);
        dialog.setMessage("부적을 생성하는 중입니다..");
        dialog.setCancelable(false);
        dialog.show();

        setActionBar();

        setData();

        setBilling();

        setListener();
    }

    private void setBilling() {
        manager = new BillingCharmManager(act, selectedData, new BillingCharmManager.AfterBilling() {
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
                Common.showToast(act, selectedData.getName() + "이 갤러리에 저장되었습니다.");
                saveImage(type);
            }

            @Override
            public void getSubsriptionState(String subscription, Purchase purchase) {
                Log.e(TAG, "isPurchased: " + isPurchased);
                isPurchased = subscription;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!dialog.isShowing())
            super.onBackPressed();
    }

    private void setListener() {
        binding.flPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = TYPE_SAVE;
                checkPurchased();
            }
        });

        binding.flGift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = TYPE_SHARE;
                checkPurchased();
            }
        });

        binding.flCharm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(act, CharmAct.class));
            }
        });
    }

    private void exit() {
        if (CharmDetailAct.act != null) {
            CharmDetailAct.act.finish();
        }
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
                p.add(new BasicNameValuePair("p_purchasePrice", "7700"));
                p.add(new BasicNameValuePair("p_purchage_item_name", selectedData.getId()));
                p.add(new BasicNameValuePair("ITEMIDX", selectedData.getIdx()));

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
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    // 부적 인앱일때 사용 => 현재 구독이므로 사용안함
    private void checkPurchased() {
        final HttpController hc = new HttpController(Cvalue.Login);
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<NameValuePair> p = new ArrayList<NameValuePair>();
                p.add(new BasicNameValuePair("siteUrl", Cvalue.Domain));
                p.add(new BasicNameValuePair("dbControl", "setUserPaymentCk"));
                p.add(new BasicNameValuePair("USERIDX", UserPref.getMidx(act)));
                p.add(new BasicNameValuePair("ITEMIDX", selectedData.getIdx()));


                Log.i(TAG, "Purchase Check Put Info : " + p);
                final String res = hc.getResultStreamPost(p);
                Log.e(TAG, "Purchase Check Get Info : " + res);

                try {
                    JSONObject jo = new JSONObject(res);
                    final String result = jo.getString("result");
                    if (result.equals("Y")) {
                        manager.purchase(selectedData.getId());
                    } else {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Common.showToast(act, "이미 구매한 부적입니다.\n" + selectedData.getName() + "이 갤러리에 저장되었습니다.");
                                saveImage(type);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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

    private void setActionBar() {
        setSupportActionBar(binding.toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle(null);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.gf_arrow);
    }

    private String checkDigits(String num) {
        if (num.length() == 1) {
            return "0" + num;
        } else {
            return num;
        }
    }

    private void setData() {
        selectedData = (CharmData) getIntent().getSerializableExtra("selectedData");
        selectedData.setId(selectedData.getId().replace("sub_", ""));
        Log.e(TAG, "selectedData: " + selectedData);
        name = getIntent().getStringExtra("name");
        year = getIntent().getStringExtra("year");

        month = checkDigits(getIntent().getStringExtra("month"));
        day = checkDigits(getIntent().getStringExtra("day"));

        Log.e(TAG, "year: /" + year + "/");
        Log.e(TAG, "month: /" + month + "/");
        Log.e(TAG, "day: /" + day + "/");


        Glide.with(act)
                .load(selectedData.getPurImageUrl())
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE)// 디스크 캐시 저장 off
                .skipMemoryCache(true)// 메모리 캐시 저장 off
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        Log.e(TAG, "onResourceReady");
                        Canvas c = new Canvas();
                        c.setBitmap(resource);

                        Paint p = new Paint();
                        p.setColor(getResources().getColor(R.color.color_c01003));
                        p.setTextSize(57);
                        p.setTypeface(ResourcesCompat.getFont(act, R.font.custom_charm_font));
                        p.setTextAlign(Paint.Align.CENTER);

                        //생년
                        int y = 150;
                        int x = 115;

                        for (int i = 0; i < year.length(); i++) {
                            c.drawText("" + year.charAt(i), x, y, p);
                            y += 55;
//                            if (i == 0)
//                                minus_x = -2;
//                            minus_x -= 1;
                        }
                        c.drawText("년", x, y, p);

                        //월
                        y += 100;
                        for (int i = 0; i < month.length(); i++) {
                            c.drawText("" + month.charAt(i), x, y, p);
                            y += 55;
                        }
                        c.drawText("월", x, y, p);

                        //일
                        y += 100;
                        for (int i = 0; i < day.length(); i++) {
                            c.drawText("" + day.charAt(i), x, y, p);
                            y += 55;
                        }
                        c.drawText("일", x, y, p);

                        //이름
                        if (name.length() < 3) {
                            y += 175;
                        } else {
                            y += 115;
                        }
                        for (int i = 0; i < name.length(); i++) {
                            c.drawText("" + name.charAt(i), x, y, p);
                            y += 60;
                        }

                        charm_bitmap = resource;
                        binding.ivCharm.setImageBitmap(resource);


                        if (dialog.isShowing())
                            dialog.dismiss();
                    }
                });
    }


    private void saveImage(int type) {
        if (!save_dialog.isShowing())
            save_dialog.show();

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String getTime = sdf.format(date);

        String s = selectedData.getName();
        if (s.contains(" ")) {
            s = s.replaceAll(" ", "");
        }

        String birth = year + "_" + month + "_" + day;
        String filename = getTime + "_" + birth + "_" + name + "_" + s + ".png";
        String path = Environment.getExternalStorageDirectory().toString() + "/LUCK";

        saveBitmapToPng(charm_bitmap, path, filename, type);
    }


    public void saveBitmapToPng(Bitmap bitmap, String strFilePath, String filename, int type) {
        Log.e(TAG, "saveBitmapToPng");
        File file = new File(strFilePath);

        if (!file.exists()) {
            file.mkdirs();
        }

        File fileItem = new File(strFilePath + "/" + filename);
        OutputStream outStream = null;

        try {
            fileItem.createNewFile();
            outStream = new FileOutputStream(fileItem);

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + strFilePath + "/" + filename)));

            outStream.close();


            if (save_dialog.isShowing())
                save_dialog.dismiss();

            if (type == TYPE_SHARE) {
                shareCharm(fileItem);
            } else {
                exit();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /* 공유하기 */
    public void shareCharm(File file) {
        Log.e(TAG, "file.getAbsolutePath(): " + file.getAbsolutePath());

        Intent intent = new Intent(Intent.ACTION_SEND);

        Uri imageUri = null;
        imageUri = Uri.fromFile(file);
        if (file.exists()) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                imageUri = FileProvider.getUriForFile(act, BuildConfig.APPLICATION_ID + ".provider", file);
            } else {
                imageUri = Uri.fromFile(file);
            }
        }

        Log.e(TAG, "imageUri: " + imageUri);

        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, imageUri);
        startActivity(Intent.createChooser(intent, "부적 보내기"));

        exit();
    }
}

