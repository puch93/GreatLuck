package kr.co.planet.newgreatluck.activity.unse;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.Random;

import kr.co.planet.newgreatluck.R;
import kr.co.planet.newgreatluck.activity.BasicAct;
import kr.co.planet.newgreatluck.activity.CharmAct;
import kr.co.planet.newgreatluck.activity.MainAct;
import kr.co.planet.newgreatluck.adapter.unse.Unse03Adapter;
import kr.co.planet.newgreatluck.data.CharmData;
import kr.co.planet.newgreatluck.data.InfoData;
import kr.co.planet.newgreatluck.databinding.ActivityUnseBinding;
import kr.co.planet.newgreatluck.preference.UserPref;
import kr.co.planet.newgreatluck.util.CommonCode;
import kr.co.planet.newgreatluck.util.StringUtil;

public class Unse03Act extends BasicAct {
    ActivityUnseBinding binding;
    Activity act;

    ActionBar actionBar;

    InfoData myInfo, otherInfo;
    String zodiacText, zodiacText_other;

    ArrayList<String> result_list, contents_list;
    ArrayList<Integer> star_list;
    ArrayList<String> title_list;

    /* charm ads image */
    ArrayList<CharmData> charmAdsList;

    Random random;

    RecyclerView recyclerView;
    LinearLayoutManager manager;
    Unse03Adapter adapter;

    public static final int TYPE_NEW = 0;
    public static final int TYPE_LOAD = 1;

    private static final int TYPE_TODAY_ZODIAC = 0;
    private static final int TYPE_HEART = 1;
    private static final int TYPE_WEEK_ZODIAC = 2;
    private static final int TYPE_HEART_ZODIAC = 3;

    private int type;
    private int contents_count;
    private String current_contents, current_stars;
    private String[] contents_type = {CommonCode.CONTENTS_TODAY_ZODIAC, CommonCode.CONTENTS_HEART, CommonCode.CONTENTS_WEEK_ZODIAC, CommonCode.CONTENTS_HEART_ZODIAC};
    private String[] stars_type = {CommonCode.STAR_TODAY_ZODIAC, CommonCode.STAR_HEART, CommonCode.STAR_WEEK_ZODIAC, CommonCode.STAR_HEART_ZODIAC};

    private int[] title_drawable = {R.drawable.gf_13_chicken, R.drawable.gf_08_love, R.drawable.gf_14_clover, R.drawable.gf_15_flower};
    private String[] title = {"오늘띠별운세", "오늘애정궁합", "주간띠별운세", "띠별궁합"};

    public static final int SUBSCRIPTION = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_unse, null);
        act = this;

       checkSubscription();

        binding.flShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contents = "대운 (" + title[type] + ")\n\n\n";
                for (int i = 0; i < result_list.size(); i++) {
                    if (StringUtil.isNull(result_list.get(i)))
                        continue;
                    contents += title_list.get(i) + "\n\n" + result_list.get(i) + "\n\n\n";
                }
                share(contents);
            }
        });

        binding.flCharm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(act, CharmAct.class));
            }
        });

        setActionBar();

        setData();

        setRecyclerView();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if(requestCode == SUBSCRIPTION) {
                binding.adView.setVisibility(View.GONE);
            }
        }
    }

    private void setAdView() {
        /* admob */
        AdView adView = (AdView) findViewById(R.id.adView);
        if (!adView.isLoading()) {
            MobileAds.initialize(act, getString(R.string.add_mob_user_id));
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Log.e(TAG, "onAdFailedToLoad (activity): " + i);
            }
        });
    }

    private void checkSubscription() {
        switch (MainAct.playStoreLoginState) {
            case "noAccount":
                setAdView();
                Log.e(TAG, "구글 플레이스토어 계정 정보가 없습니다");
                break;
            case "error":
                setAdView();
                Log.e(TAG, "에러가 발생했습니다. 다시 시도해 주세요.");
                break;
            case "Y":
                binding.adView.setVisibility(View.GONE);
                Log.e(TAG, "구독중");
                break;
            default:
                setAdView();
                break;
        }
    }

    private void setRecyclerView() {
        recyclerView = binding.rcvToday;
        manager = new LinearLayoutManager(act);
        recyclerView.setLayoutManager(manager);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setHasFixedSize(true);
//        recyclerView.addItemDecoration(new ItemOffsetDecorationLinear(getResources().getDimensionPixelSize(R.dimen.dimen_unse), getResources().getDimensionPixelSize(R.dimen.dimen_10), contents_list.size() + 1));

        adapter = new Unse03Adapter(act, contents_list, star_list, title_list, charmAdsList, title_drawable[type], title[type], type);
        recyclerView.setAdapter(adapter);
    }

    private void setActionBar() {
        binding.tvTitle.setText("띠별운세");

        setSupportActionBar(binding.toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle(null);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.frtn_btn_back_190716);
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

    private void setData() {
        charmAdsList = new ArrayList<>();

        myInfo = UserPref.getMyInfo(act);
        otherInfo = UserPref.getOtherInfo(act);

        Bundle bundle = getIntent().getExtras();
        result_list = bundle.getStringArrayList("list");
        type = bundle.getInt("type");

        zodiacText = myInfo.getZodiacText();

//        if (type == TYPE_HEART || type == TYPE_HEART_ZODIAC || type == TYPE_TODAY_ZODIAC) {
//            zodiacText_other = otherInfo.getZodiacText();
//        }

        resetContents();

        // set designated title
        setTitleList(type);
        setCharmAdsList(type);

        // set preference contents name
        current_contents = contents_type[type];

        // set preference stars name
        current_stars = stars_type[type];

        // set contents count
        contents_count = result_list.size();


        // 글이 달라졌는지 확인
        // 달라지거나 글이 없을 경우 => Preference 에 Contents,Stars 새로저장
        // 글이 같을 경우 => Preference 에 저장된 기존 Contents, Stars 사용
        checkContents();
    }

    private void resetContents() {
        if (type == TYPE_HEART || type == TYPE_HEART_ZODIAC) {
            zodiacText_other = otherInfo.getZodiacText();

            String women, man;
            if (myInfo.getGender().equals("1")) {
                man = zodiacText;
                women = zodiacText_other;
            } else {
                man = zodiacText_other;
                women = zodiacText;
            }

            String subject, last_str;
            if (type == TYPE_HEART) {
                subject = "애정";
                last_str = "/100";
            } else {
                subject = "사랑관";
                last_str = "%이상";
            }


            String[] tagName = {women + " 여성의 " + subject, man + " 남성의 " + subject, "두 분의", last_str};
            String tmp = result_list.get(0);
            Log.e(TAG, "tmp: " + tmp);


            int[] len = new int[4];
            for (int i = 0; i < 4; i++) {
                len[i] = tmp.indexOf(tagName[i]);
            }


            result_list = new ArrayList<>();
            if (type == TYPE_HEART) {
                result_list.add(tmp.substring(len[0] + 10, len[1] - 4).trim());
                result_list.add(tmp.substring(len[1] + 9, len[2]).trim());
                result_list.add(tmp.substring(len[3] + 6).trim());
            } else {
                result_list.add(tmp.substring(len[0] + 11, len[1] - 4).trim());
                result_list.add(tmp.substring(len[1] + 11, len[2]).trim());
                result_list.add(tmp.substring(len[3] + 3).trim());
            }
        } else if (type == TYPE_TODAY_ZODIAC) {
            String tmp = result_list.get(1);
            if(tmp.contains("100점")) {
                Log.e(TAG, "resetContents");
                tmp = tmp.replace("100점", "100점\n");
            }

            int idx = tmp.indexOf("종합운");
            result_list.set(1, tmp.substring(idx + 4));
            Log.e(TAG, "result_list.get(1): " + result_list.get(1));

        }
    }

    private void setCharmAdsList(int type) {
        switch (type) {
            case TYPE_WEEK_ZODIAC:
            case TYPE_TODAY_ZODIAC:
                charmAdsList = CommonCode.getCharmList01(act);
                break;
            case TYPE_HEART_ZODIAC:
            case TYPE_HEART:
                charmAdsList = CommonCode.getCharmList04(act);
                break;
        }
    }

    private void setTitleList(int type) {
        title_list = new ArrayList<>();
        switch (type) {
            case TYPE_TODAY_ZODIAC:
                title_list.add("띠별 총운");
                title_list.add("종합운");
                break;
            case TYPE_HEART:
                title_list.add("여성의 애정");
                title_list.add("남성의 애정");
                title_list.add("두분의 오늘 연애궁합은?");
                break;

            case TYPE_WEEK_ZODIAC:
                title_list.add("띠별 총운");
                title_list.add("연애운");
                title_list.add("건강운");
                title_list.add("금전운");
                title_list.add("비지니스 운");
                break;

            case TYPE_HEART_ZODIAC:
                title_list.add("여성의 사랑관");
                title_list.add("남성의 사랑관");
                title_list.add("두분의 오늘 연애궁합은?");
                break;
        }
    }

    private void checkContents() {
        contents_list = UserPref.getContentsData(act, current_contents);
        Log.e(TAG, "contents_list: " + contents_list);

        if (contents_list != null) {
            if (contents_list.get(0).equals(result_list.get(0)) &&
                    contents_list.get(1).equals(result_list.get(1))) {
                checkStar(TYPE_LOAD);
            } else {
                contents_list = result_list;
                UserPref.saveContentsData(act, current_contents, contents_list);
                checkStar(TYPE_NEW);
            }
        } else {
            contents_list = result_list;
            UserPref.saveContentsData(act, current_contents, contents_list);
            checkStar(TYPE_NEW);
        }
    }

    private void checkStar(int type) {
        if (type == TYPE_NEW) {
            star_list = new ArrayList<>();
            random = new Random();

            for (int i = 0; i < contents_count; i++) {
                star_list.add(3 + random.nextInt(3));
            }

            UserPref.saveStarData(act, current_stars, star_list);
        } else if (type == TYPE_LOAD) {
            star_list = UserPref.getStarData(act, current_stars);
        }
    }
}
