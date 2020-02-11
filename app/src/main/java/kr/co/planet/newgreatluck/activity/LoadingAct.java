package kr.co.planet.newgreatluck.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.WindowManager;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import kr.co.planet.newgreatluck.R;
import kr.co.planet.newgreatluck.databinding.ActivityLoadingBinding;
import kr.co.planet.newgreatluck.preference.UserPref;
import kr.co.planet.newgreatluck.server.Cvalue;
import kr.co.planet.newgreatluck.server.HttpController;
import kr.co.planet.newgreatluck.util.Common;
import kr.co.planet.newgreatluck.util.StringUtil;

public class LoadingAct extends BasicAct {
    ActivityLoadingBinding binding;
    Activity act;

    String fcm_token,device_version;

    private Timer timer = new Timer();
    boolean isReady = true;

    private static final int PERMISSION = 1000;
    private static final int NETWORK = 1001;

    private static final int READY = 100;
    private static final int OK = 101;
    private static final int FAIL = 102;
    private int admob_state = READY;
    private Timer admobTimer = new Timer();
    private String resu;

    int fromAlarm;

    //애드몹
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_loading, null);
        act = this;

        Log.e(TAG, "업로드 날짜: 9/11");

        UserPref.saveViewPagerHeight(act, 0);

        fromAlarm = getIntent().getIntExtra("fromAlarm", -1);

        try {
            device_version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        setAdsFront();

        checkVersion();
    }


    public void startProgram(){
        if (!checkPermission()) {
            startActivityForResult(new Intent(LoadingAct.this, PermissionAct.class), PERMISSION);
        } else {
            getFcmToken();
            checkSetting();
        }
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (
                    checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                            checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ) {
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "resultCode: " + resultCode);

        if (resultCode != RESULT_OK && resultCode != RESULT_CANCELED)
            return;

        switch (requestCode) {
            case PERMISSION:
                startProgram();
                break;

            case NETWORK:
                checkSetting();
                break;
        }
    }

    private void setAdsFront() {
        MobileAds.initialize(act, getString(R.string.add_mob_user_id));
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.add_mob_front_ads));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Log.e(TAG, "onAdFailedToLoad: " + i);
                admob_state = FAIL;
            }

            @Override
            public void onAdLoaded() {
                if(mInterstitialAd.isLoaded()) {
                    admob_state = OK;
                } else {
                    Log.e(TAG, "setAdsFront: not ready");
                }
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Log.e(TAG, "onAdClosed");
                lastProcess();
            }
        });
    }

    private void lastProcess() {
        Intent intent = new Intent(act, ProfileAct.class);
        intent.putExtra("type", "new");

        if (fromAlarm != -1)
            intent.putExtra("fromAlarm", fromAlarm);

        startActivity(intent);
        finish();
    }

    //로딩중 텍스트 애니메이션
    public void checkTimer() {
        TimerTask adTask = new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (fcm_token != null && isReady) {
                            isReady = false;
                            loginInfo();
//                          testLogin();
                            timer.cancel();
                        }
                    }
                }, 0);
            }
        };
        timer.schedule(adTask, 0, 1000);
    }


    private void checkSetting() {
        checkNetwork(new Runnable() {
            @Override
            public void run() {
                checkTimer();
            }
        });
    }


    //데이터 또는 WIFI 켜져 있는지 확인 / 안켜져있으면 데이터 설정창으로
    private void checkNetwork(final Runnable afterCheckAction) {
        ConnectivityManager manager = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (manager != null) {
            networkInfo = manager.getActiveNetworkInfo();
        }
        if (networkInfo != null && networkInfo.isConnected()) {
            if (afterCheckAction != null) {
                afterCheckAction.run();
            }
        } else {
            showNetworkAlert();
        }
    }


    //네트워크 연결 다이얼로그
    public void showNetworkAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(act);

        alertDialog.setCancelable(false);
        alertDialog.setTitle("네트워크 사용유무");
        alertDialog.setMessage("인터넷이 연결되어 있지 않습니다. \n설정창으로 가시겠습니까?");

        // OK 를 누르게 되면 설정창으로 이동합니다.
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Intent in = new Intent(Settings.ACTION_DATA_USAGE_SETTINGS);
                        in.addCategory(Intent.CATEGORY_DEFAULT);
                        startActivityForResult(in, NETWORK);
                    }
                });
        // Cancle 하면 종료 합니다.
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        finish();
                    }
                });
        alertDialog.show();
    }


    private void getFcmToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.i("TEST", "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        Log.i("TEST", "myFcmToken: " + token);
                        UserPref.saveFcmToken(act, token);
                        fcm_token = token.replace("%3", ":");
                    }
                });
    }


    private void checkVersion() {
        final HttpController hc = new HttpController(Cvalue.Login);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "run");
                ArrayList<NameValuePair> p = new ArrayList<NameValuePair>();
                p.add(new BasicNameValuePair("siteUrl", Cvalue.Domain));
                p.add(new BasicNameValuePair("dbControl", "setPlaystorUpdateCheck"));
                p.add(new BasicNameValuePair("thisVer", device_version));

                Log.i(TAG, "Check Version Put Info : " + p);
                final String res = hc.getResultStreamPost(p);
                Log.e(TAG, "Check Version Get Info : " + res);
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (!StringUtil.isNull(res)) {
                                JSONObject jo = new JSONObject(res);
                                if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                                    android.app.AlertDialog.Builder alertDialogBuilder =
                                            new android.app.AlertDialog.Builder(new ContextThemeWrapper(act, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert));
                                    alertDialogBuilder.setTitle("업데이트");
                                    alertDialogBuilder.setMessage("새로운 버전이 있습니다.")
                                            .setPositiveButton("업데이트 바로가기", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                                    intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=kr.co.planet.newgreatluck"));
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }).setNegativeButton("기존버전으로 계속하기", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            startProgram();
                                        }
                                    });
                                    android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.setCanceledOnTouchOutside(false);
                                    alertDialog.show();
                                } else {
                                    startProgram();
                                }
                            } else {
                                startProgram();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }


    private void testLogin() {
        final HttpController hc = new HttpController(Cvalue.Login);
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<NameValuePair> p = new ArrayList<NameValuePair>();
                p.add(new BasicNameValuePair("siteUrl", Cvalue.Domain));
                p.add(new BasicNameValuePair("APPCONNECTCODE", Build.FINGERPRINT));
                p.add(new BasicNameValuePair("dbControl", "setUserMemberRegi"));
                p.add(new BasicNameValuePair("fcm", fcm_token));
                p.add(new BasicNameValuePair("hp", "01012341234"));
                p.add(new BasicNameValuePair("uniq", Common.getDeviceId(act)));



                Log.i(TAG, "Login Put Info : " + p);
                final String res = hc.getResultStreamPost(p);
                Log.e(TAG, "Login Get Info : " + res);

                try {
                    JSONObject jo = new JSONObject(res);

                    final String result = jo.getString("result");
                    final String message = jo.getString("message");
                    final String midx = jo.getString("midx");

                    act.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            UserPref.saveMidx(act, midx);

                            if (result.equals("Y")) {
                                Intent intent = new Intent(act, ProfileAct.class);
                                intent.putExtra("type", "new");

                                if (fromAlarm != -1)
                                    intent.putExtra("fromAlarm", fromAlarm);

                                startActivity(intent);
                                finish();
                            } else {
                                Log.e(TAG, "message: " + message);

                                if(UserPref.getMyInfo(act) == null) {
                                    Intent intent = new Intent(act, ProfileAct.class);
                                    intent.putExtra("type", "new");

                                    if (fromAlarm != -1)
                                        intent.putExtra("fromAlarm", fromAlarm);

                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(LoadingAct.this, MainAct.class);
                                    if (fromAlarm != -1)
                                        intent.putExtra("fromAlarm", fromAlarm);
                                    startActivity(intent);
                                }
                                finish();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void loginInfo() {
        if (StringUtil.isNull(Common.getPhoneNumber(act))) {
            Common.showToast(act, "전화번호 없이 회원가입 하실 수 없습니다.");
            finish();
        } else {
            final HttpController hc = new HttpController(Cvalue.Login);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ArrayList<NameValuePair> p = new ArrayList<NameValuePair>();
                    p.add(new BasicNameValuePair("siteUrl", Cvalue.Domain));
                    p.add(new BasicNameValuePair("APPCONNECTCODE", Build.FINGERPRINT));
                    p.add(new BasicNameValuePair("dbControl", "setUserMemberRegi"));
                    p.add(new BasicNameValuePair("fcm", fcm_token));
                    p.add(new BasicNameValuePair("hp", Common.getPhoneNumber(act)));
                    p.add(new BasicNameValuePair("uniq", Common.getDeviceId(act)));


                    Log.i(TAG, "Login Put Info : " + p);
                    final String res = hc.getResultStreamPost(p);
                    Log.e(TAG, "Login Get Info : " + res);

                    try {
                        JSONObject jo = new JSONObject(res);

                        final String result = jo.getString("result");
                        final String message = jo.getString("message");
                        final String midx = jo.getString("midx");

                        act.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                UserPref.saveMidx(act, midx);
                                resu = result;


                                if (result.equals("Y")) {
                                    checkAdmobTimer();
                                } else {
                                    if(UserPref.getMyInfo(act) == null) {
                                        checkAdmobTimer();
                                    } else {
                                        Intent intent = new Intent(LoadingAct.this, MainAct.class);
                                        if (fromAlarm != -1)
                                            intent.putExtra("fromAlarm", fromAlarm);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    // 애드몹 전면광고 불러왔는지 체크
    public void checkAdmobTimer() {
        TimerTask adTask = new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mInterstitialAd != null && admob_state == OK) {
                            admob_state = READY;
                            mInterstitialAd.show();
                            admobTimer.cancel();
                        } else if(admob_state == FAIL) {
                            lastProcess();
                            admobTimer.cancel();
                        }
                    }
                }, 0);
            }
        };
        admobTimer.schedule(adTask, 0, 500);
    }
}
