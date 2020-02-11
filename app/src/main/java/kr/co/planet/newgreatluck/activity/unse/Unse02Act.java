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
import kr.co.planet.newgreatluck.adapter.unse.Unse02Adapter;
import kr.co.planet.newgreatluck.data.CharmData;
import kr.co.planet.newgreatluck.data.InfoData;
import kr.co.planet.newgreatluck.databinding.ActivityUnseBinding;
import kr.co.planet.newgreatluck.preference.UserPref;
import kr.co.planet.newgreatluck.util.CommonCode;
import kr.co.planet.newgreatluck.util.StringUtil;

public class Unse02Act extends BasicAct {
    ActivityUnseBinding binding;
    Activity act;

    ActionBar actionBar;

    InfoData myInfo;

    ArrayList<String> result_list, contents_list;
    ArrayList<Integer> star_list;
    ArrayList<String> title_list;

    /* charm ads image */
    ArrayList<CharmData> charmAdsList;

    Random random;


    RecyclerView recyclerView;
    LinearLayoutManager manager;
    Unse02Adapter adapter;

    public static final int TYPE_NEW = 0;
    public static final int TYPE_LOAD = 1;

    private static final int TYPE_TODAY = 0;
    private static final int TYPE_COMPATIBILITY = 1;
    private static final int TYPE_TODAY_LOVE = 2;
    private static final int TYPE_DATE = 3;
    private static final int TYPE_HUMER = 4;
    private static final int TYPE_BLOOD = 5;
    private static final int TYPE_HUMER_OTHER = 6;

    private int type;
    private int contents_count;
    private String current_contents, current_stars;
    private String[] contents_type = {CommonCode.CONTENTS_TODAY_OTHER, CommonCode.CONTENTS_COMPATIBILITY, CommonCode.CONTENTS_TODAY_LOVE, CommonCode.CONTENTS_DATE, CommonCode.CONTENTS_HUMER, CommonCode.CONTENTS_BLOOD, CommonCode.CONTENTS_HUMER_OTHER};
    private String[] stars_type = {CommonCode.STAR_TODAY_OTHER, CommonCode.STAR_COMPATIBILITY, CommonCode.STAR_TODAY_LOVE, CommonCode.STAR_DATE, CommonCode.STAR_HUMER, CommonCode.STAR_BLOOD, CommonCode.STAR_HUMER_OTHER};

    private int[] title_drawable = {R.drawable.gf_07_sun, R.drawable.gf_08_love, R.drawable.gf_09_signal, R.drawable.gf_10_leaf, R.drawable.gf_11_star2, R.drawable.gf_12_blood, R.drawable.gf_10_leaf};
    private String[] title = {"데이트 운세", "궁합", "오늘사랑운세", "월간운세", "오늘엽기운세", "혈액형운세", "오늘엽기운세"};

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
                    if(StringUtil.isNull(result_list.get(i)))
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
        /* 혹시몰라서 초기화 */
        if (charmAdsList == null)
            charmAdsList = new ArrayList<>();

        recyclerView = binding.rcvToday;
        manager = new LinearLayoutManager(act);
        recyclerView.setLayoutManager(manager);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setHasFixedSize(true);
//        recyclerView.addItemDecoration(new ItemOffsetDecorationLinear(getResources().getDimensionPixelSize(R.dimen.dimen_unse), getResources().getDimensionPixelSize(R.dimen.dimen_10), contents_list.size() + 1));

        adapter = new Unse02Adapter(act, contents_list, star_list, title_list, charmAdsList, title_drawable[type], title[type], type);
        recyclerView.setAdapter(adapter);
    }

    private void setActionBar() {
        binding.tvTitle.setText("남녀운세");

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
        myInfo = UserPref.getMyInfo(act);
        Bundle bundle = getIntent().getExtras();
        result_list = bundle.getStringArrayList("list");
        type = bundle.getInt("type");

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

    private void setCharmAdsList(int type) {
        switch (type) {
            case TYPE_DATE:
            case TYPE_TODAY:
                charmAdsList = CommonCode.getCharmList03(act);
                break;
            case TYPE_TODAY_LOVE:
            case TYPE_COMPATIBILITY:
                charmAdsList = CommonCode.getCharmList04(act);
                break;

            case TYPE_BLOOD:
                charmAdsList = CommonCode.getCharmList06(act);
                break;

            case TYPE_HUMER_OTHER:
                charmAdsList = CommonCode.getCharmList05(act);
                break;
        }
    }
    private void setTitleList(int type) {
        title_list = new ArrayList<>();
        switch (type) {
            case TYPE_TODAY:
                title_list.add("오늘의 데이트 운세");
                title_list.add("데이트 장소는?");
                title_list.add("희망은?");
                break;
            case TYPE_COMPATIBILITY:
                title_list.add("남자 분의 성격운");
                title_list.add("여자 분의 성격운");
                title_list.add("남자 분의 애정운");
                title_list.add("여자 분의 애정운");
                title_list.add("남자 분의 결혼운");
                title_list.add("여자 분의 결혼운");
                title_list.add("운명적 궁합");
                title_list.add("결혼 후 궁합");
                break;

            case TYPE_TODAY_LOVE:
                title_list.add("사랑지수");
                title_list.add("오늘의 사랑운세");
                break;

            case TYPE_DATE:
                title_list.add("두 분의 데이트 궁합");
                title_list.add("첫 만남의 마음");
                title_list.add("첫 만남의 행동");
                title_list.add("첫 데이트 만족도");
                title_list.add("첫 데이트");
                break;

            case TYPE_HUMER:
                title_list.add("엽기운세");
                title_list.add("희망은?");

                break;

            case TYPE_BLOOD:
                title_list.add("남성분의 성격");
                title_list.add("여성분의 성격");
                title_list.add("두 분의 궁합");
                break;

            case TYPE_HUMER_OTHER:
                title_list.add("엽기운세");
                title_list.add("오늘 운세 해설");
                title_list.add("금전운");
                title_list.add("소원운과 건강운");
                title_list.add("시험과 직장운");
                title_list.add("애정운");
                break;
        }
    }

    private void checkContents() {
        contents_list = UserPref.getContentsData(act, current_contents);

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
            Log.e(TAG, "type new");
            star_list = new ArrayList<>();
            random = new Random();

            for (int i = 0; i < contents_count; i++) {
                star_list.add(3 + random.nextInt(3));
            }

            UserPref.saveStarData(act, current_stars, star_list);
        } else if (type == TYPE_LOAD) {
            Log.e(TAG, "type load");
            star_list = UserPref.getStarData(act, current_stars);
        }
    }
}
