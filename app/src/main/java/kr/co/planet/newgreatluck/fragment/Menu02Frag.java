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
import kr.co.planet.newgreatluck.activity.unse.Unse02Act;
import kr.co.planet.newgreatluck.data.InfoData;
import kr.co.planet.newgreatluck.databinding.FragmentMenu02Binding;
import kr.co.planet.newgreatluck.preference.UserPref;
import kr.co.planet.newgreatluck.server.Connect;
import kr.co.planet.newgreatluck.server.Cvalue;
import kr.co.planet.newgreatluck.util.Common;
import kr.co.planet.newgreatluck.util.CommonCode;

import static android.app.Activity.RESULT_OK;

public class Menu02Frag extends BasicFrag implements View.OnClickListener {
    FragmentMenu02Binding binding;
    Activity act;

    InfoData otherInfo;

    Calendar calendar;
    String year, month, day;

    ArrayList<String> result_list;

    private static final int TYPE_TODAY = 0;
    private static final int TYPE_COMPATIBILITY = 1;
    private static final int TYPE_TODAY_LOVE = 2;
    private static final int TYPE_DATE = 3;
    private static final int TYPE_HUMER = 4;
    private static final int TYPE_BLOOD = 5;

    private static final int TYPE_HUMER_OTHER = 6;

    private static final int TYPE_TODAY_COUNT = 3;
    private static final int TYPE_COMPATIBILITY_COUNT = 8;
    private static final int TYPE_TODAY_LOVE_COUNT = 2;
    private static final int TYPE_DATE_COUNT = 5;
    private static final int TYPE_HUMER_COUNT = 2;
    private static final int TYPE_BLOOD_COUNT = 3;

    private static final int TYPE_HUMER_OTHER_COUNT = 6;


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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_menu02, container, false);
        act = getActivity();
        dialog = new ProgressDialog(act);
        dialog.setMessage("운세를 불러오는 중입니다..");
        dialog.setCanceledOnTouchOutside(false);

        setAdsFront();

        setListener();

        /* fromAlarm */
        if (((MainAct) MainAct.act).fromAlarm == 1) {
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


    /**
     * 데이트운세
     * 내 성별 기준으로 user1_sex, user2_sex 결정 // 내 성별 남 -> user1_sex = 1 (나), user2_sex = 2 (상대)
     * 기존앱에서 (상대 데이터) 성별 외에 안쓰임..
     * 기존앱에서 target2_year/month/day 만 쓰이고 target1_year/month/day 안쓰임..
     */
    private void menu01_process() {
        Connect c = new Connect();

        //나
        c.setValue(CommonCode.PARAM_USER_YEAR, MainAct.myInfo.getYear());
        c.setValue(CommonCode.PARAM_USER_MONTH, MainAct.myInfo.getMonth());
        c.setValue(CommonCode.PARAM_USER_DAY, MainAct.myInfo.getDay());
        c.setValue(CommonCode.PARAM_USER_HOUR, null);
        c.setValue(CommonCode.PARAM_USER_SOL, MainAct.myInfo.getCalendarKind());
        c.setValue(CommonCode.PARAM_USER_GENDER, MainAct.myInfo.getGender());

        c.setValue(CommonCode.PARAM_FORTUNE_YEAR, year);
        c.setValue(CommonCode.PARAM_FORTUNE_MONTH, month);
        c.setValue(CommonCode.PARAM_FORTUNE_DAY, day);


        //상대
        c.setValue(CommonCode.PARAM_USER_YEAR_OTHER, otherInfo.getYear());
        c.setValue(CommonCode.PARAM_USER_MONTH_OTHER, otherInfo.getMonth());
        c.setValue(CommonCode.PARAM_USER_DAY_OTHER, otherInfo.getDay());
        c.setValue(CommonCode.PARAM_USER_HOUR_OTHER, null);
        c.setValue(CommonCode.PARAM_USER_SOL_OTHER, otherInfo.getCalendarKind());

        if (MainAct.myInfo.getGender().equals(CommonCode.GENDER_MALE)) {
            c.setValue(CommonCode.PARAM_USER_GENDER_OTHER, CommonCode.GENDER_FEMALE);
        } else {
            c.setValue(CommonCode.PARAM_USER_GENDER_OTHER, CommonCode.GENDER_MALE);
        }

        c.setValue(CommonCode.PARAM_FORTUNE_YEAR_OTHER, year);
        c.setValue(CommonCode.PARAM_FORTUNE_MONTH_OTHER, month);
        c.setValue(CommonCode.PARAM_FORTUNE_DAY_OTHER, day);

        c.setValue(CommonCode.PARAM_UNSE, selected_server_type);

        last_process(c);
    }


    /**
     * 궁합
     * 기존앱에서 (상대 데이터) 모두 안쓰임..
     */
    private void menu02_process() {
        Connect c = new Connect();

        c.setValue(CommonCode.PARAM_USER_YEAR, MainAct.myInfo.getYear());
        c.setValue(CommonCode.PARAM_USER_MONTH, MainAct.myInfo.getMonth());
        c.setValue(CommonCode.PARAM_USER_DAY, MainAct.myInfo.getDay());
        c.setValue(CommonCode.PARAM_USER_HOUR, null);
        c.setValue(CommonCode.PARAM_USER_SOL, MainAct.myInfo.getCalendarKind());
        c.setValue(CommonCode.PARAM_USER_GENDER, MainAct.myInfo.getGender());

        c.setValue(CommonCode.PARAM_USER_YEAR_OTHER, otherInfo.getYear());
        c.setValue(CommonCode.PARAM_USER_MONTH_OTHER, otherInfo.getMonth());
        c.setValue(CommonCode.PARAM_USER_DAY_OTHER, otherInfo.getDay());
        c.setValue(CommonCode.PARAM_USER_HOUR_OTHER, null);
        c.setValue(CommonCode.PARAM_USER_SOL_OTHER, otherInfo.getCalendarKind());
        c.setValue(CommonCode.PARAM_USER_GENDER_OTHER, otherInfo.getGender());

        c.setValue(CommonCode.PARAM_UNSE, selected_server_type);

        last_process(c);
    }

    /**
     * 오늘사랑운세
     * 내 성별 기준으로 lucktype 결정 // 남 -> lucktype m1, 여 -> lucktype f1
     * 기존앱데이터 이상함 -> 한칸씩 밀려서 보냄..
     */
    private void menu03_process() {
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

        if (MainAct.myInfo.getGender().equals("1"))
            c.setValue(CommonCode.PARAM_LUCK_TYPE, "m1");
        else
            c.setValue(CommonCode.PARAM_LUCK_TYPE, "f1");

        c.setValue(CommonCode.PARAM_UNSE, selected_server_type);

        last_process(c);
    }


    /**
     * 첫 데이트 궁합
     * 기존앱에서 (상대 데이터) 모두 안쓰임..
     */
    private void menu04_process() {
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

        c.setValue(CommonCode.PARAM_USER_YEAR_OTHER, otherInfo.getYear());
        c.setValue(CommonCode.PARAM_USER_MONTH_OTHER, otherInfo.getMonth());
        c.setValue(CommonCode.PARAM_USER_DAY_OTHER, otherInfo.getDay());
        c.setValue(CommonCode.PARAM_USER_HOUR_OTHER, null);
        c.setValue(CommonCode.PARAM_USER_SOL_OTHER, otherInfo.getCalendarKind());
        c.setValue(CommonCode.PARAM_USER_GENDER_OTHER, otherInfo.getGender());


        c.setValue(CommonCode.PARAM_UNSE, selected_server_type);

        last_process(c);
    }

    /**
     * 오늘엽기운세
     * 내 성별 기준으로 unse 결정 // 남 -> yupgi_dosa, 여 -> yupgiunse
     */
    private void menu05_process() {
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
        last_process(c);
    }


    /**
     * 혈액형
     */
    private void menu06_process() {
        Connect c = new Connect();

        c.setValue(CommonCode.PARAM_USER_BLOOD, MainAct.myInfo.getBlood());
        c.setValue(CommonCode.PARAM_USER_BLOOD_OTHER, otherInfo.getBlood());

        c.setValue(CommonCode.PARAM_UNSE, selected_server_type);
        last_process(c);
    }

    private void last_process(Connect c) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String res = c.sendToServer(Cvalue.Today);
                Log.e(TAG, "Menu02 (" + selected_type + ") Get Info: " + res);

                if (res != null) {
                    try {
                        JSONObject jo = new JSONObject(res);

                        result_list = new ArrayList<>();
                        for (int i = 1; i < selected_count + 1; i++) {
                            Log.e(TAG, "fo_con" + i + ": " + jo.get("fo_con" + i).toString().trim());
//                            String data = jo.get("fo_con" + i).toString().trim();
                            String data = jo.get("fo_con" + i).toString();
                            data = checkContents(data);
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


    private void process() {
        switch (selected_type) {
            case TYPE_TODAY:
                menu01_process();
                break;

            case TYPE_COMPATIBILITY:
                menu02_process();
                break;

            case TYPE_TODAY_LOVE:
                menu03_process();
                break;

            case TYPE_DATE:
                menu04_process();
                break;

            case TYPE_HUMER:
            case TYPE_HUMER_OTHER:
                menu05_process();
                break;

            case TYPE_BLOOD:
                menu06_process();
                break;
        }

        dialog.show();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        }, 750);
    }

    private void process_intent() {
        Bundle bundle = new Bundle();
        bundle.putInt("type", selected_type);
        bundle.putStringArrayList("list", result_list);

        Intent intent = new Intent(act, Unse02Act.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private String checkContents(String s) {
//        String[] tag = {"1:", "2:", "3:", "4:", "5:", "6:", "7:", "8:", " "};
        String[] tag = {"1:", "2:", "3:", "4:", "5:", "6:", "7:", "8:"};

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

            set();
        }


        if (resultCode == RESULT_OK && requestCode == PROFILE_OTHER) {
            if (selected_type == TYPE_HUMER_OTHER) {
                if (MainAct.myInfo.getGender().equals(CommonCode.GENDER_FEMALE)) {
                    selected_type = TYPE_HUMER;
                    selected_server_type = CommonCode.PARAM_HUMER_OTHER_WOMEN_UNSE;
                    selected_count = TYPE_HUMER_COUNT;
                }
            }

            otherInfo = UserPref.getOtherInfo(act);
            Log.e(TAG, "onActivityResult otherinfo: " + otherInfo);
            process();
        }
    }


    private void set() {
        calendar = Calendar.getInstance();
        year = String.valueOf(calendar.get(Calendar.YEAR));
        month = String.valueOf(calendar.get(Calendar.MONTH));
        day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

        startActivityForResult(new Intent(act, ProfileOtherAct.class), PROFILE_OTHER);
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
                selected_type = TYPE_TODAY;
                selected_server_type = CommonCode.PARAM_TODAY_OTHER_UNSE;
                selected_count = TYPE_TODAY_COUNT;
                break;

            case R.id.btn_tab02:
                selected_type = TYPE_COMPATIBILITY;
                selected_server_type = CommonCode.PARAM_COMPATIBILITY_OTHER_UNSE;
                selected_count = TYPE_COMPATIBILITY_COUNT;
                break;

            case R.id.btn_tab03:
                selected_type = TYPE_TODAY_LOVE;
                selected_server_type = CommonCode.PARAM_TODAYLOVE_OTHER_UNSE;
                selected_count = TYPE_TODAY_LOVE_COUNT;
                break;

            case R.id.btn_tab04:
                selected_type = TYPE_DATE;
                selected_server_type = CommonCode.PARAM_DATE_OTHER_UNSE;
                selected_count = TYPE_DATE_COUNT;
                break;

            case R.id.btn_tab05:
                //엽기운세 -> 남녀에따라 값 달라짐
                selected_type = TYPE_HUMER_OTHER;
                selected_server_type = CommonCode.PARAM_HUMER_OTHER_UNSE;
                selected_count = TYPE_HUMER_OTHER_COUNT;
                break;

            case R.id.btn_tab06:
                selected_type = TYPE_BLOOD;
                selected_server_type = CommonCode.PARAM_BLOOD_OTHER_UNSE;
                selected_count = TYPE_BLOOD_COUNT;
                break;
        }

        checkSubscription();
    }
}

