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
import kr.co.planet.newgreatluck.adapter.unse.Unse01Adapter;
import kr.co.planet.newgreatluck.data.CharmData;
import kr.co.planet.newgreatluck.data.InfoData;
import kr.co.planet.newgreatluck.databinding.ActivityUnseBinding;
import kr.co.planet.newgreatluck.preference.UserPref;
import kr.co.planet.newgreatluck.util.CommonCode;
import kr.co.planet.newgreatluck.util.StringUtil;

public class Unse01Act extends BasicAct {
    ActivityUnseBinding binding;
    Activity act;

    ActionBar actionBar;

    InfoData myInfo;

    ArrayList<String> contents_list, result_list;
    ArrayList<Integer> star_list;
    ArrayList<String> title_list;

    /* charm ads image */
    ArrayList<CharmData> charmAdsList;

    Random random;

    RecyclerView recyclerView;
    LinearLayoutManager manager;
    Unse01Adapter adapter;

    public static final int TYPE_NEW = 0;
    public static final int TYPE_LOAD = 1;

    private static final int TYPE_TODAY = 0;
    private static final int TYPE_TOJUNG = 1;
    private static final int TYPE_WEEK = 2;
    private static final int TYPE_MONTH = 3;
    private static final int TYPE_SAJU = 4;

    private int type;
    private int contents_count;
    private String current_contents, current_stars;
    private String[] contents_type = {CommonCode.CONTENTS_TODAY, CommonCode.CONTENTS_TOJUNG, CommonCode.CONTENTS_WEEK, CommonCode.CONTENTS_MONTH, CommonCode.CONTENTS_SAJU};
    private String[] stars_type = {CommonCode.STAR_TODAY, CommonCode.STAR_TOJUNG, CommonCode.STAR_WEEK, CommonCode.STAR_MONTH, CommonCode.STAR_SAJU};

    private int[] title_drawable = {R.drawable.gf_01_star1, R.drawable.gf_02_book, R.drawable.gf_03_talk, R.drawable.gf_04_calendar, R.drawable.gf_06_bottle};
    private String[] title = {"오늘의 운세", "토정비결", "주간운세", "월간운세", "사주팔자"};

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
        /* 혹시몰라서 초기화 */
        if (charmAdsList == null)
            charmAdsList = new ArrayList<>();

        recyclerView = binding.rcvToday;
        manager = new LinearLayoutManager(act);
        recyclerView.setLayoutManager(manager);
        recyclerView.setItemViewCacheSize(20);
//        recyclerView.addItemDecoration(new ItemOffsetDecorationLinear(getResources().getDimensionPixelSize(R.dimen.dimen_unse), getResources().getDimensionPixelSize(R.dimen.dimen_10), contents_list.size() + 1));

        Log.e(TAG, "contents_list2: " + contents_list);

        adapter = new Unse01Adapter(act, contents_list, star_list, title_list, charmAdsList, title_drawable[type], title[type], type);
        recyclerView.setAdapter(adapter);
    }


    private void setActionBar() {
        binding.tvTitle.setText("정통운세");

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

    private void setCharmAds(String id, String name, String contents) {
        CharmData data = new CharmData();
        data.setId(id);
        data.setName(name);
        data.setContents(contents);

        charmAdsList.add(data);
    }

    private void setCharmAdsList(int type) {
        charmAdsList = new ArrayList<>();

        switch (type) {
            case TYPE_TODAY:
            case TYPE_TOJUNG:
            case TYPE_WEEK:
            case TYPE_MONTH:
                charmAdsList = CommonCode.getCharmList01(act);
                break;

            case TYPE_SAJU:
                charmAdsList = CommonCode.getCharmList02(act);
                break;
        }
    }

    private void setData() {
        myInfo = UserPref.getMyInfo(act);

        Bundle bundle = getIntent().getExtras();
        result_list = bundle.getStringArrayList("list");
        type = bundle.getInt("type");

        // set designated title and 부적광고데이터 불러오기
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

    private void setTitleList(int type) {
        title_list = new ArrayList<>();
        switch (type) {
            case TYPE_TODAY:
                title_list.add("오늘의총운세");
                title_list.add("금전운");
                title_list.add("사업운");
                title_list.add("연애운");
                title_list.add("소원풀이");
                title_list.add("희망사항");
                break;

            case TYPE_TOJUNG:
                title_list.add("올해의 총운");
                break;

            case TYPE_WEEK:
                title_list.add("주간운세 총운");
                title_list.add("여성운세");
                title_list.add("남성운세");
                title_list.add("사업운, 분야별 운세");
                title_list.add("희망사항");
                break;
            case TYPE_MONTH:
                title_list.add("이달의 총운");
                title_list.add("남성운,여성운");
                title_list.add("날짜별 운세");
                title_list.add("분야별 운세");
                break;
            case TYPE_SAJU:
                title_list.add("사주운");
                title_list.add("초년운");
                title_list.add("중년운");
                title_list.add("말년운");
                title_list.add("애정운과 재물운");
                break;
        }
    }

    private void checkContents() {
        contents_list = UserPref.getContentsData(act, current_contents);
        Log.e(TAG, "contents_list: " + contents_list);

        if (contents_list != null) {
            if (contents_list.get(0).equals(result_list.get(0))) {
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
