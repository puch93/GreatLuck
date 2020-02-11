package kr.co.planet.newgreatluck.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import kr.co.planet.newgreatluck.R;
import kr.co.planet.newgreatluck.activity.MainAct;
import kr.co.planet.newgreatluck.activity.ProfileAct;
import kr.co.planet.newgreatluck.activity.unse.Unse01Act;
import kr.co.planet.newgreatluck.databinding.FragmentMenu01Binding;
import kr.co.planet.newgreatluck.dialog.DreamDlg;
import kr.co.planet.newgreatluck.preference.UserPref;
import kr.co.planet.newgreatluck.server.Cvalue;
import kr.co.planet.newgreatluck.util.Common;
import kr.co.planet.newgreatluck.util.CommonCode;
import kr.co.planet.newgreatluck.server.Connect;

import static android.app.Activity.RESULT_OK;

public class Menu01Frag extends BasicFrag implements View.OnClickListener {
    FragmentMenu01Binding binding;
    Activity act;
    ProgressDialog dialog;

    Calendar calendar;
    String year, month, day;

    ArrayList<String> result_list;

    private static final int TYPE_TODAY = 0;
    private static final int TYPE_TOJUNG = 1;
    private static final int TYPE_WEEK = 2;
    private static final int TYPE_MONTH = 3;
    private static final int TYPE_SAJU = 4;

    private static final int TYPE_DREAM = -1;

    private static final int TYPE_TODAY_COUNT = 6;
    private static final int TYPE_TOJUNG_COUNT = 1;
    private static final int TYPE_WEEK_COUNT = 5;
    private static final int TYPE_MONTH_COUNT = 4;
    private static final int TYPE_SAJU_COUNT = 5;

    private static final int PROFILE = 1000;

    int selected_count = 0;
    int selected_type = 0;
    String selected_server_type = null;

    //애드몹
    public static InterstitialAd mInterstitialAd;

    public static final int READY = 100;
    public static final int OK = 101;
    public static final int FAIL = 102;
    private int admob_state = READY;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_menu01, container, false);
        act = getActivity();

        dialog = new ProgressDialog(act);
        dialog.setMessage("운세를 불러오는 중입니다..");
        dialog.setCanceledOnTouchOutside(false);

        setAdsFront();

        setListener();

        /* fromAlarm */
        if (((MainAct) MainAct.act).fromAlarm == 0) {
            binding.btnTab01.performClick();
            ((MainAct) MainAct.act).fromAlarm = -1;
        }

        return binding.getRoot();
    }


    private void setListener() {
        binding.btnTab01.setOnClickListener(this);
        binding.btnTab02.setOnClickListener(this);
        binding.btnTab03.setOnClickListener(this);
        binding.btnTab04.setOnClickListener(this);
        binding.btnTab05.setOnClickListener(this);
        binding.btnTab06.setOnClickListener(this);
    }


    private void set() {
        calendar = Calendar.getInstance();
        year = String.valueOf(calendar.get(Calendar.YEAR));
        month = String.valueOf(calendar.get(Calendar.MONTH));
        day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));


        if (selected_type == TYPE_DREAM) {
            startActivity(new Intent(act, DreamDlg.class));
        } else {
            process();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "requestCode: " + requestCode);
        Log.e(TAG, "resultCode: " + resultCode);

        if (resultCode == RESULT_OK && requestCode == PROFILE) {
            // update my info
            ((MainAct) MainAct.act).doMainUpdate();

            set();
        }
    }

    private void process() {
        Connect c = new Connect();
        c.setValue(CommonCode.PARAM_USER_YEAR, MainAct.myInfo.getYear());
        c.setValue(CommonCode.PARAM_USER_MONTH, MainAct.myInfo.getMonth());
        c.setValue(CommonCode.PARAM_USER_DAY, MainAct.myInfo.getDay());
        c.setValue(CommonCode.PARAM_USER_HOUR, null);
        c.setValue(CommonCode.PARAM_USER_SOL, MainAct.myInfo.getCalendarKind());
        c.setValue(CommonCode.PARAM_USER_GENDER, MainAct.myInfo.getGender());
        c.setValue(CommonCode.PARAM_FORTUNE_YEAR, year);
        c.setValue(CommonCode.PARAM_FORTUNE_MONTH, month);
        c.setValue(CommonCode.PARAM_FORTUNE_DAY, day);
        c.setValue(CommonCode.PARAM_UNSE, selected_server_type);


        new Thread(new Runnable() {
            @Override
            public void run() {
                final String res = c.sendToServer(Cvalue.Today);
                Log.e(TAG, "Menu01 (" + selected_type + ") Get Info: " + res);

                if (res != null) {
                    try {
                        JSONObject jo = new JSONObject(res);

                        result_list = new ArrayList<>();
                        for (int i = 1; i < selected_count + 1; i++) {
                            Log.e(TAG, "fo_con" + i + ": " + jo.get("fo_con" + i).toString().trim());
                            String data = jo.get("fo_con" + i).toString();
//                            data = data.substring(3);
//                            String data = jo.get("fo_con" + i).toString().trim();
//                            data = data.replace(" ", "");
                            result_list.add(data);
                        }
                        process_intent();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Common.showToastNetwork(act);
                }
            }
        }).start();
    }

    private void process_intent() {
        Bundle bundle = new Bundle();
        bundle.putInt("type", selected_type);
        bundle.putStringArrayList("list", result_list);

        Intent intent = new Intent(act, Unse01Act.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void checkSubscription() {
        switch (MainAct.playStoreLoginState) {
            case "noAccount":
                if(UserPref.getAdsCount(act) % 2 == 1) {
                    checkAdmobTimer();
                } else {
                    lastProcess();
                }
                Log.e(TAG, "구글 플레이스토어 계정 정보가 없습니다");
                break;
            case "error":
                if(UserPref.getAdsCount(act) % 2 == 1) {
                    checkAdmobTimer();
                } else {
                    lastProcess();
                }
                Log.e(TAG, "에러가 발생했습니다. 다시 시도해 주세요.");
                break;
            case "Y":
                lastProcess();
                break;
            default:
                if(UserPref.getAdsCount(act) % 2 == 1) {
                    checkAdmobTimer();
                } else {
                    lastProcess();
                }
                break;
        }
    }

    private void lastProcess() {
        dialog.show();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        }, 750);

        // 광고 2번에 1번실행하기위한 카운트
        int count = UserPref.getAdsCount(act);
        UserPref.saveAdsCount(act, ++count);

        if (MainAct.myInfo == null) {
            startActivityForResult(new Intent(act, ProfileAct.class), PROFILE);
        } else {
            set();
        }
    }

    // 애드몹 전면광고 불러왔는지 체크
    public void checkAdmobTimer() {
        Timer admobTimer = new Timer();
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

    private void setAdsFront() {
        MobileAds.initialize(act, getString(R.string.add_mob_user_id));
        mInterstitialAd = new InterstitialAd(act);
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
                    Log.e(TAG, "setAdsFront: ok ready");
                } else {
                    Log.e(TAG, "setAdsFront: not ready");
                }
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Log.e(TAG, "onAdClosed");
                lastProcess();
                setAdsFront();
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_tab01:

                selected_type = TYPE_TODAY;
                selected_server_type = CommonCode.PARAM_TODAY_UNSE;
                selected_count = TYPE_TODAY_COUNT;
                break;

            case R.id.btn_tab02:

                selected_type = TYPE_TOJUNG;
                selected_server_type = CommonCode.PARAM_TOJUNG_UNSE;
                selected_count = TYPE_TOJUNG_COUNT;
                break;

            case R.id.btn_tab03:

                selected_type = TYPE_WEEK;
                selected_server_type = CommonCode.PARAM_WEEK_UNSE;
                selected_count = TYPE_WEEK_COUNT;
                break;

            case R.id.btn_tab04:

                selected_type = TYPE_MONTH;
                selected_server_type = CommonCode.PARAM_MONTH_UNSE;
                selected_count = TYPE_MONTH_COUNT;
                break;

            case R.id.btn_tab05:
                selected_type = TYPE_DREAM;
                break;

            case R.id.btn_tab06:

                selected_type = TYPE_SAJU;
                selected_server_type = CommonCode.PARAM_SAJU_UNSE;
                selected_count = TYPE_SAJU_COUNT;
                break;
        }


        checkSubscription();
    }
}

