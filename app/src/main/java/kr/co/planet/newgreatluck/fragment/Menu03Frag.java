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
import kr.co.planet.newgreatluck.activity.ProfileOtherAct;
import kr.co.planet.newgreatluck.activity.unse.Unse03Act;
import kr.co.planet.newgreatluck.data.InfoData;
import kr.co.planet.newgreatluck.databinding.FragmentMenu03Binding;
import kr.co.planet.newgreatluck.preference.UserPref;
import kr.co.planet.newgreatluck.server.Connect;
import kr.co.planet.newgreatluck.server.Cvalue;
import kr.co.planet.newgreatluck.util.Common;
import kr.co.planet.newgreatluck.util.CommonCode;
import kr.co.planet.newgreatluck.util.StringUtil;

import static android.app.Activity.RESULT_OK;

public class Menu03Frag extends BasicFrag implements View.OnClickListener {
    FragmentMenu03Binding binding;
    Activity act;

    InfoData otherInfo;

    Calendar calendar;
    String year, month, week, day;

    String zodiac, zodiacText;
    String zodiac_other, zodiacText_other;

    ArrayList<String> result_list;

    private static final int TYPE_TODAY_ZODIAC = 0;
    private static final int TYPE_HEART = 1;
    private static final int TYPE_WEEK_ZODIAC = 2;
    private static final int TYPE_HEART_ZODIAC = 3;
    private static final int TYPE_SHARE = 4;
    private static final int TYPE_RECOMMEND = 5;

    private static final int TYPE_TODAY_ZODIAC_COUNT = 2;
    private static final int TYPE_HEART_COUNT = 1;
    private static final int TYPE_WEEK_ZODIAC_COUNT = 5;
    private static final int TYPE_HEART_ZODIAC_COUNT = 0;

    private static final int PROFILE = 1000;
    private static final int PROFILE_OTHER = 1001;

    int selected_count = 0;
    int selected_type = 0;
    String selected_server_type = null;
    ProgressDialog dialog;

    //애드몹
    public static InterstitialAd mInterstitialAd;

    public static final int READY = 100;
    public static final int OK = 101;
    public static final int FAIL = 102;
    private int admob_state = READY;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_menu03, container, false);
        act = getActivity();

        dialog = new ProgressDialog(act);
        dialog.setMessage("운세를 불러오는 중입니다..");
        dialog.setCanceledOnTouchOutside(false);

        setAdsFront();

        setListener();

        /* fromAlarm */
        if (((MainAct) MainAct.act).fromAlarm == 2) {
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
//        binding.btnTab05.setOnClickListener(this);
    }

    private void set() {
        zodiac = MainAct.myInfo.getZodiac();
        zodiacText = MainAct.myInfo.getZodiacText();


        calendar = Calendar.getInstance();
        year = String.valueOf(calendar.get(Calendar.YEAR));
        month = String.valueOf(calendar.get(Calendar.MONTH));
        week = String.valueOf(calendar.get(Calendar.WEEK_OF_MONTH));
        day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));


        if (selected_type < 4) {
            if (selected_type == TYPE_HEART || selected_type == TYPE_HEART_ZODIAC) {
                startActivityForResult(new Intent(act, ProfileOtherAct.class), PROFILE_OTHER);
            } else {
                process();
            }
        }
    }


    /**
     * 오늘띠별운세
     */
    private void menu01_process() {
        Connect c = new Connect();
        c.setValue(CommonCode.PARAM_ZODIAC_DATA, zodiac);

        c.setValue(CommonCode.PARAM_FORTUNE_YEAR, year);
        c.setValue(CommonCode.PARAM_FORTUNE_MONTH, month);
        c.setValue(CommonCode.PARAM_FORTUNE_DAY, day);

        c.setValue(CommonCode.PARAM_UNSE, selected_server_type);

        last_process(c);
    }


    /**
     * 오늘띠별궁합
     * 내성별 기준으로 user_12gi1, user_12gi2 구별
     */
    private void menu02_process() {
        Connect c = new Connect();

        if (MainAct.myInfo.getGender().equals(CommonCode.GENDER_MALE)) {
            c.setValue(CommonCode.PARAM_ZODIAC_DATA_OTHER, zodiac);
            c.setValue(CommonCode.PARAM_ZODIAC_DATA, zodiac_other);
        } else {
            c.setValue(CommonCode.PARAM_ZODIAC_DATA_OTHER, zodiac_other);
            c.setValue(CommonCode.PARAM_ZODIAC_DATA, zodiac);
        }

        c.setValue(CommonCode.PARAM_FORTUNE_YEAR, year);
        c.setValue(CommonCode.PARAM_FORTUNE_MONTH, month);
        c.setValue(CommonCode.PARAM_FORTUNE_DAY, day);

        c.setValue(CommonCode.PARAM_UNSE, selected_server_type);

        last_process(c);
    }

    /**
     * 주간띠별운세
     */
    private void menu03_process() {
        Connect c = new Connect();

        c.setValue(CommonCode.PARAM_ZODIAC_DATA, zodiac);

        c.setValue(CommonCode.PARAM_FORTUNE_YEAR, year);
        c.setValue(CommonCode.PARAM_FORTUNE_MONTH, month);
        c.setValue(CommonCode.PARAM_FORTUNE_DAY, day);

        c.setValue(CommonCode.PARAM_UNSE, selected_server_type);

        last_process(c);
    }

    /**
     * 띠별궁합
     * 내성별 기준으로 user_12gi1, user_12gi2 구별
     */
    private void menu04_process() {
        Connect c = new Connect();

        if (MainAct.myInfo.getGender().equals(CommonCode.GENDER_MALE)) {
            c.setValue(CommonCode.PARAM_ZODIAC_DATA_OTHER, zodiac);
            c.setValue(CommonCode.PARAM_ZODIAC_DATA, zodiac_other);
        } else {
            c.setValue(CommonCode.PARAM_ZODIAC_DATA_OTHER, zodiac_other);
            c.setValue(CommonCode.PARAM_ZODIAC_DATA, zodiac);
        }

        c.setValue(CommonCode.PARAM_UNSE, selected_server_type);

        last_process(c);
    }


    private void process() {
        dialog.show();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        }, 750);

        if (zodiac.equals("00")) {
            zodiac = "12";
        }

        if (!StringUtil.isNull(zodiac_other)) {
            if (zodiac_other.equals("00")) {
                zodiac_other = "12";
            }
        }


        switch (selected_type) {
            case TYPE_TODAY_ZODIAC:
                menu01_process();
                break;

            case TYPE_HEART:
                menu02_process();
                break;

            case TYPE_WEEK_ZODIAC:
                menu03_process();
                break;

            case TYPE_HEART_ZODIAC:
                menu04_process();
                break;
        }
    }

    private void last_process(Connect c) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String res = c.sendToServer(Cvalue.Today);
                Log.e(TAG, "Menu03 (" + selected_type + ") Get Info: " + res);

                if (res != null) {
                    try {
                        JSONObject jo = new JSONObject(res);

                        result_list = new ArrayList<>();

                        int start = 1;
                        if (selected_type == TYPE_HEART_ZODIAC)
                            start = 0;

                        for (int i = start; i < selected_count + 1; i++) {
                            Log.e(TAG, "fo_con" + i + ": " + jo.get("fo_con" + i).toString().trim());
                            String data = jo.get("fo_con" + i).toString().trim();

                            if (selected_type == TYPE_HEART || selected_type == TYPE_HEART_ZODIAC) {
                                data = data.replace("   ", "");
                                result_list.add(data);
                            } else {
                                Log.e(TAG, "this");
                                data = checkContents(data);
                                Log.e(TAG, "result: " + data);
                                result_list.add(data);
                            }
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

    private String checkContents(String s) {
        String[] tag = {"1:", "2:", "3:", "4:", "5:", "6:", "7:", "8:", "  ", "연애운", "건강운", "금전운", "비지니스운"};

        for (int i = 0; i < tag.length; i++) {
            if (s.contains(tag[i]))
                s = s.replaceAll(tag[i], "");
        }
        return s;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK && requestCode == PROFILE) {
            // update my info
            ((MainAct) MainAct.act).doMainUpdate();

            if (selected_type < 4)
                set();
        }


        if (resultCode == RESULT_OK && requestCode == PROFILE_OTHER) {
            otherInfo = UserPref.getOtherInfo(act);
            zodiac_other = otherInfo.getZodiac();
            zodiacText_other = otherInfo.getZodiacText();

            process();
        }
    }


    private void process_intent() {
        Bundle bundle = new Bundle();
        bundle.putInt("type", selected_type);
        bundle.putStringArrayList("list", result_list);

        Intent intent = new Intent(act, Unse03Act.class);
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

    private void lastProcess() {
        // 광고 2번에 1번실행하기위한 카운트
        int count = UserPref.getAdsCount(act);
        UserPref.saveAdsCount(act, ++count);

        if (MainAct.myInfo == null) {
            startActivityForResult(new Intent(act, ProfileAct.class), PROFILE);
        } else {
            set();
        }
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
                if (mInterstitialAd.isLoaded()) {
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
                selected_type = TYPE_TODAY_ZODIAC;
                selected_server_type = CommonCode.PARAM_TODAY_ZODIAC_UNSE;
                selected_count = TYPE_TODAY_ZODIAC_COUNT;
                break;


            case R.id.btn_tab02:
                selected_type = TYPE_HEART;
                selected_server_type = CommonCode.PARAM_HEART_UNSE;
                selected_count = TYPE_HEART_COUNT;
                break;

            case R.id.btn_tab03:
                selected_type = TYPE_WEEK_ZODIAC;
                selected_server_type = CommonCode.PARAM_WEEK_ZODIAC_UNSE;
                selected_count = TYPE_WEEK_ZODIAC_COUNT;
                break;


            case R.id.btn_tab04:
                selected_type = TYPE_HEART_ZODIAC;
                selected_server_type = CommonCode.PARAM_HEART_ZODIAC_UNSE;
                selected_count = TYPE_HEART_ZODIAC_COUNT;
                break;

        }

        checkSubscription();
    }
}

