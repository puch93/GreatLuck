package kr.co.planet.newgreatluck.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.billingclient.api.Purchase;
import com.asksira.loopingviewpager.LoopingViewPager;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.exceptions.RealmMigrationNeededException;
import kr.co.planet.newgreatluck.R;
import kr.co.planet.newgreatluck.adapter.MainBottomPagerAdapter;
import kr.co.planet.newgreatluck.adapter.MainTopPagerAdapter;
import kr.co.planet.newgreatluck.billing.BIllingManager;
import kr.co.planet.newgreatluck.data.InfoData;
import kr.co.planet.newgreatluck.databinding.ActivityMainBinding;
import kr.co.planet.newgreatluck.dialog.FinishDialog;
import kr.co.planet.newgreatluck.fragment.MainTopBannerFrag;
import kr.co.planet.newgreatluck.preference.UserPref;
import kr.co.planet.newgreatluck.receiver.AlarmReceiver;
import kr.co.planet.newgreatluck.server.Cvalue;
import kr.co.planet.newgreatluck.server.HttpController;
import kr.co.planet.newgreatluck.util.BackPressCloseHandler;
import kr.co.planet.newgreatluck.util.Common;
import kr.co.planet.newgreatluck.util.CommonCode;
import kr.co.planet.newgreatluck.util.MeasuredViewPager;
import kr.co.planet.newgreatluck.util.StringUtil;

public class MainAct extends BasicAct implements View.OnClickListener {
    ActivityMainBinding binding;
    public static Activity act;

    private static final int PROFILE = 1000;
    private static final int FINISH = 1001;

    MainBottomPagerAdapter adapter_bottom;
    MainTopPagerAdapter adapter_top;
    FragmentManager fragmentManager;

    ActionBar actionBar;

    private BackPressCloseHandler backPressCloseHandler;

    Realm realm;
    RealmConfiguration config;
    public static InfoData myInfo;

    LinearLayout alarm_state;

    /* animation */
    private Timer timer;
    public int fromAlarm;

    /* view pager */
    LoopingViewPager pager_top;
    MeasuredViewPager pager_bottom;
//    ViewPager pager_bottom;

    ArrayList<String> contents_list;
    ArrayList<String> title_list;

    /* alarm data */
    private static final int TYPE_ALARM_01 = 1001;
    private static final int TYPE_ALARM_02 = 1002;
    private static final int TYPE_ALARM_03 = 1003;

    private static final int TYPE_ALARM_CODE_01 = 0;
    private static final int TYPE_ALARM_CODE_02 = 1;
    private static final int TYPE_ALARM_CODE_03 = 2;


    /* billing */
    public static BIllingManager manager;
    public static String playStoreLoginState = "none";
    public static final String SUBS_CONTENTS = "sub_contents";
    public static final int SUBSCRIPTION = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main, null);
        backPressCloseHandler = new BackPressCloseHandler(this);
        act = this;

        getCheerState();

        setBilling();

        setDrawerData();

        setListener();

        setViewPager();

        checkMyInfo();

        setAlarm();
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
            }

            @Override
            public void getSubsriptionState(String subscription, Purchase purchase) {
                playStoreLoginState = subscription;
            }
        });
    }


    @Override
    protected void onPause() {
//        if (pager_top != null)
//            pager_top.pauseAutoScroll();
        /* 부적배너 shimmer off */
        binding.llShimmer.stopShimmer();
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        cancelAnimationTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();

        /* 부적배너 shimmer on */
        binding.llShimmer.startShimmer();

        if (timer == null)
            setAnimationTimer();

//        if (pager_top != null)
//            pager_top.resumeAutoScroll();


        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "device id: " + UserPref.getDeviceId(act), null);
                Log.e(TAG, "phone num: " + UserPref.getPhoneNum(act), null);
                Log.e(TAG, "phone sim operator: " + UserPref.getSimOperator(act), null);

                //기기 고유번호 체크
                if (StringUtil.isNull(UserPref.getDeviceId(act)))
                    UserPref.saveDeviceId(act, Common.getDeviceId(act));

                //폰번호 체크
                if (StringUtil.isNull(UserPref.getPhoneNum(act)))
                    UserPref.savePhoneNum(act, Common.getPhoneNumber(act));

                //통신사 체크
                if (StringUtil.isNull(UserPref.getSimOperator(act)))
                    UserPref.saveSimOperator(act, Common.getSimOperator(act));
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
//        backPressCloseHandler.onBackPressed();
        startActivityForResult(new Intent(act, FinishDialog.class), FINISH);
    }

    private void checkMyInfo() {
        // initialize Realm and create Realm object
        Realm.init(this);

        config = new RealmConfiguration.Builder()
                .name("luck.realm")
                .build();

        realm = null;
        try {
            realm = Realm.getInstance(config);
        } catch (RealmMigrationNeededException e) {
            // When the class structure is changed, create new Realm object
            Realm.deleteRealm(config);
            realm = Realm.getInstance(config);
        }


        // check my info
        myInfo = realm.where(InfoData.class).equalTo("idx", "0").findFirst();
        if (myInfo != null) {
            setMyInfo();
        } else {
            // 회원정보 없을 때
            Intent intent = new Intent(act, ProfileAct.class);
            intent.putExtra("type", "new");
            startActivity(intent);
            finish();
        }



        // process from alarm
        if (myInfo != null) {
            fromAlarm = getIntent().getIntExtra("fromAlarm", -1);
            if (fromAlarm != -1) {
                pager_bottom.setCurrentItem(fromAlarm, true);
            }
        }
    }

    /**
     * state = N -> 응원하기 하지 않은 상태
     * state = Y -> 이미 응원하기한 상태
     * */
    private void getCheerState() {
        final HttpController hc = new HttpController(Cvalue.Login);
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<NameValuePair> p = new ArrayList<NameValuePair>();
                p.add(new BasicNameValuePair("siteUrl", Cvalue.Domain));
                p.add(new BasicNameValuePair("APPCONNECTCODE", Build.FINGERPRINT));
                p.add(new BasicNameValuePair("dbControl", "getCheckReviewState"));
                p.add(new BasicNameValuePair("_APP_MEM_IDX", UserPref.getMidx(act)));

                Log.i(TAG, "Check Cheer Put Info : " + p);
                final String res = hc.getResultStreamPost(p);
                Log.e(TAG, "Check Cheer Get Info : " + res);

                try {
                    JSONObject jo = new JSONObject(res);

                    final String value = jo.getString("result");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(value.equalsIgnoreCase("N")) {
                                UserPref.saveCheerState(act, true);
                            } else {
                                UserPref.saveCheerState(act, false);
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public void doMainUpdate() {
        // check my info
        myInfo = realm.where(InfoData.class).equalTo("idx", "0").findFirst();

        if (myInfo != null) {
            setMyInfo();
        }
    }

    private void setAlarm() {
        Log.e(TAG, "setAlarm");

        /* initialize alarm service */
        Calendar mCalendar = Calendar.getInstance();


        /* set compare data */
        int LAST_DAY_OF_YEAR = mCalendar.getMaximum(Calendar.DAY_OF_YEAR);
        int NOW_DAY_OF_YEAR = mCalendar.get(Calendar.DAY_OF_YEAR);


        /* set calendar */
        Calendar calendar01 = setCalendar(NOW_DAY_OF_YEAR, LAST_DAY_OF_YEAR);


        /* set alarm manager */
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);


        /* set pending intent */
        PendingIntent pendingIntent01 = PendingIntent.getBroadcast(act, TYPE_ALARM_CODE_01, new Intent(act, AlarmReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);


        /* register alarm (버전별로 따로) */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.e(TAG, "first");
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar01.getTimeInMillis(), pendingIntent01);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Log.e("TEST_HOME", "second");
            manager.setExact(AlarmManager.RTC_WAKEUP, calendar01.getTimeInMillis(), pendingIntent01);
        } else {
            Log.e("TEST_HOME", "third");
            manager.set(AlarmManager.RTC_WAKEUP, calendar01.getTimeInMillis(), pendingIntent01);
        }
    }

    private Calendar setCalendar(int NOW_DAY_OF_YEAR, int LAST_DAY_OF_YEAR) {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        // manager.set 할때 현재시간보다 이전 시간대면, 리시버가 바로 실행됨, 다음날로 지정해줘야 함 => ex) 세팅시간 8:00, 현재시간 9:20 이면, 다음날 8:00로 지정
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            Log.e(TAG, "현재시간보다 작음");

            // 올해의 마지막 날일 경우
            if (NOW_DAY_OF_YEAR == LAST_DAY_OF_YEAR) {
                NOW_DAY_OF_YEAR = 1;
            } else {
                NOW_DAY_OF_YEAR++;
            }
            calendar.set(Calendar.DAY_OF_YEAR, NOW_DAY_OF_YEAR);
        }

        return calendar;
    }

    /* set drawer layout and default layout */
    private void setMyInfo() {
        /* drawer data */
        TextView tv_name, tv_zodiac, tv_birth, tv_solar_lunar;
        ImageView iv_zodiac, iv_gender;

        tv_name = (TextView) findViewById(R.id.tv_drawer_name);
        tv_zodiac = (TextView) findViewById(R.id.tv_drawer_zodiac);
        tv_birth = (TextView) findViewById(R.id.tv_drawer_birth);
        tv_solar_lunar = (TextView) findViewById(R.id.tv_drawer_solar_lunar);

        iv_zodiac = (ImageView) findViewById(R.id.iv_drawer_zodiac);
        iv_gender = (ImageView) findViewById(R.id.iv_drawer_gender);


        // set zodiac image
        TypedArray imags = getResources().obtainTypedArray(R.array.zodiac_drawable);
        binding.ivZodiac.setImageResource(imags.getResourceId(Integer.parseInt(myInfo.getZodiac()), -1));
        iv_zodiac.setImageResource(imags.getResourceId(Integer.parseInt(myInfo.getZodiac()), -1));
        imags.recycle();

        // set zodiac text
        binding.tvZodiac.setText(myInfo.getZodiacText());
        tv_zodiac.setText(myInfo.getZodiacText());

        // set name
        binding.tvName.setText(myInfo.getName());
        tv_name.setText(myInfo.getName());


        // set birth
        String month = myInfo.getMonth();
        if(month.length() == 1) {
            month  = "0" + month;
        }

        String day = myInfo.getDay();
        if(day.length() == 1) {
            day  = "0" + day;
        }

        binding.tvBirth.setText(myInfo.getYear() + "년 " + month + "월 " + day + "일");
        tv_birth.setText(myInfo.getYear() + "년 " + month + "월 " + day + "일");


        // set gender
        if (myInfo.getGender().equals("1")) {
            binding.ivGender.setImageResource(R.drawable.frtn_icon_men_190716);
            iv_gender.setImageResource(R.drawable.frtn_icon_men_190716);
        } else {
            binding.ivGender.setImageResource(R.drawable.frtn_icon_women_190716);
            iv_gender.setImageResource(R.drawable.frtn_icon_women_190716);
        }


        // set calendar/moon kind
        if (myInfo.getCalendarKind().equals(CommonCode.CALENDER_01)) {
            binding.tvSolarLunar.setText("양력");
            tv_solar_lunar.setText("양력");
        } else {
            if (myInfo.getMoonKind().equals(CommonCode.MOON_01)) {
                binding.tvSolarLunar.setText("음력평달");
                tv_solar_lunar.setText("음력평달");
            } else {
                binding.tvSolarLunar.setText("음력윤달");
                tv_solar_lunar.setText("음력윤달");
            }
        }
    }

    private void setAnimationTimer() {
        Log.e(TAG, "setAnimationTimer");
        timer = new Timer();

        /* animation case 1 */
        TimerTask adTask = new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        YoYo.with(Techniques.Swing)
                                .duration(650)
                                .repeat(2)
                                .pivot(50.0f, 50.0f)
                                .interpolate(new AccelerateDecelerateInterpolator())
                                .playOn(binding.ivCharmTop);
                    }
                });
            }
        };
        timer.schedule(adTask, 0, 3200);
    }

    private void cancelAnimationTimer() {
        Log.e(TAG, "cancelAnimationTimer");

        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    private void setListener() {
        // set click listener
        binding.ivCharm.setOnClickListener(this);

        binding.flCharm.setOnClickListener(this);
        binding.flMenu.setOnClickListener(this);


        // set drawer click listener
        (findViewById(R.id.ll_drawer_profile)).setOnClickListener(this);
        (findViewById(R.id.ll_drawer_charm)).setOnClickListener(this);
        (findViewById(R.id.ll_drawer_notice)).setOnClickListener(this);
        (findViewById(R.id.ll_drawer_alarm)).setOnClickListener(this);
        (findViewById(R.id.ll_drawer_private)).setOnClickListener(this);
        (findViewById(R.id.ll_drawer_share)).setOnClickListener(this);
        (findViewById(R.id.ll_drawer_review)).setOnClickListener(this);
    }


    private void setDrawerData() {
        // set alarm state
        alarm_state = (LinearLayout) findViewById(R.id.ll_drawer_alarm);
        alarm_state.setSelected(UserPref.getAlarmState(act));


        // set version name
        PackageInfo pi = null;
        try {
            pi = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (pi != null) {
            ((TextView) findViewById(R.id.tv_version)).setText(pi.versionName);
        }
    }


    private void fragmentReplace(int pos) {
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        MainTopBannerFrag currentFragment = new MainTopBannerFrag();
        currentFragment.setData(pos, title_list.get(pos), contents_list.get(pos));

        transaction.replace(R.id.top_pager, currentFragment);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

    private void setViewPager() {
        /* 상단 문구 */
        contents_list = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.main_top_contents)));
        title_list = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.main_top_title)));
        fragmentReplace(0);

        /* 메뉴 viewpager */
        pager_bottom = binding.vpBottom;
        fragmentManager = getSupportFragmentManager();
        adapter_bottom = new MainBottomPagerAdapter(fragmentManager);
        pager_bottom.setAdapter(adapter_bottom);

        pager_bottom.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.layoutTab));
        binding.layoutTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager_bottom.setCurrentItem(tab.getPosition(), true);
                fragmentReplace(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PROFILE) {
                // check my info
                doMainUpdate();
            } else if (requestCode == SUBSCRIPTION) {
                if (data != null) {
                    playStoreLoginState = data.getStringExtra("playStoreLoginState");
                }
            } else if(requestCode == FINISH) {
                    Log.e(TAG, "onActivityResult: FINISH");
                    finish();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_menu:
                binding.dlMainDrawer.openDrawer(Gravity.LEFT);
                break;

            case R.id.ll_drawer_charm:
            case R.id.iv_charm:
            case R.id.fl_charm:
                startActivity(new Intent(act, CharmAct.class));
                break;


            /* drawer */
            case R.id.ll_drawer_profile:
                startActivityForResult(new Intent(act, ProfileAct.class), PROFILE);
                break;

            case R.id.ll_drawer_share:
                shareAppLink();
                break;

            case R.id.ll_drawer_review:
                cheer();
                break;

            case R.id.ll_drawer_notice:
                startActivity(new Intent(act, NoticeAct.class));
                break;

            case R.id.ll_drawer_alarm:
                if (alarm_state.isSelected()) {
                    alarm_state.setSelected(false);
                    UserPref.saveAlarmState(act, false);
                } else {
                    alarm_state.setSelected(true);
                    UserPref.saveAlarmState(act, true);
                }
                break;

            case R.id.ll_drawer_private:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://greatluck.alrigo.co.kr/term.siso"));
                startActivity(intent);
                break;
        }
    }
}
