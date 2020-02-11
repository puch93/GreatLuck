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
import kr.co.planet.newgreatluck.adapter.unse.UnseDreamAdapter;
import kr.co.planet.newgreatluck.data.CharmData;
import kr.co.planet.newgreatluck.data.InfoData;
import kr.co.planet.newgreatluck.databinding.ActivityUnseBinding;
import kr.co.planet.newgreatluck.preference.UserPref;
import kr.co.planet.newgreatluck.util.CommonCode;
import kr.co.planet.newgreatluck.util.StringUtil;

public class UnseDreamAct extends BasicAct {
    ActivityUnseBinding binding;
    Activity act;

    ActionBar actionBar;

    InfoData myInfo;

    ArrayList<String> contents_list, result_list, sub_list;
    ArrayList<Integer> star_list;
    ArrayList<String> title_list;

    /* charm ads image */
    ArrayList<CharmData> charmAdsList;

    Random random;

    RecyclerView recyclerView;
    LinearLayoutManager manager;
    UnseDreamAdapter adapter;

    public static final int TYPE_NEW = 0;
    public static final int TYPE_LOAD = 1;

    private static final int TYPE_TODAY = 0;
    private static final int TYPE_TOJUNG = 1;
    private static final int TYPE_WEEK = 2;
    private static final int TYPE_MONTH = 3;
    private static final int TYPE_SAJU = 4;

    private int contents_count;
    private String current_contents, current_stars;

    // token value
    String[] number = {"1)", "2)", "3)", "4)", "5)", "6)"};
    String[] flower = {"꽃이 피는 꿈", "꽃을 손에 잡는 꿈", "덩굴풀을 뽑는 꿈", "연꽃을 뽑는 꿈"};
    String[] fruit = {"포도 열매가 떨어지는 꿈", "복숭아 열매가 맺히는 꿈"};
    String[] other = {"1억 이상", "소나기 구름", "고여 있는", "금빛 나는", "사람을 죽이는", "남자 세", "낯선 사람", "산신령", "단 한 발의",
            "대밭을", "대통령에게", "돌아가신", "땅 밑이나", "돼지가", "무지개를", "백금이나", "맑은 하늘", "솟아오르는 둥근 달", "큰 돼지를", "아이가",
            "용을 타고", "유명 인사", "작은 시냇물", "잘 자란", "조상에 관한", "험한 길을"};

    int[] token_number = new int[6];
    int[] token_flower = new int[4];
    int[] token_fruit = new int[2];
    int[] token_other = new int[26];

    private ArrayList<Integer> token_contents;

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
                String contents = "대운 (꿈풀이)\n\n\n";
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
//        recyclerView.addItemDecoration(new ItemOffsetDecorationLinear(getResources().getDimensionPixelSize(R.dimen.dimen_unse), getResources().getDimensionPixelSize(R.dimen.dimen_10), contents_list.size() + 1));

        Log.e(TAG, "contents_list2: " + contents_list);

        adapter = new UnseDreamAdapter(act, contents_list, star_list, title_list, charmAdsList, R.drawable.gf_05_dream, "꿈해몽", sub_list);
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


    private void setData() {
        charmAdsList = CommonCode.getCharmList02(act);

        myInfo = UserPref.getMyInfo(act);

        Bundle bundle = getIntent().getExtras();
        result_list = bundle.getStringArrayList("list");

        resetContents();


        // set designated title
        title_list = new ArrayList<>();
        title_list.add("꿈의종류");
        title_list.add("꿈풀이");

        // set preference contents name
        current_contents = CommonCode.CONTENTS_TAEMONG;

        // set preference stars name
        current_stars = CommonCode.STAR_TAEMONG;

        // set contents count
        contents_count = result_list.size();


        // 글이 달라졌는지 확인
        // 달라지거나 글이 없을 경우 => Preference 에 Contents,Stars 새로저장
        // 글이 같을 경우 => Preference 에 저장된 기존 Contents, Stars 사용
        checkContents();
    }

    private void resetContents() {
        token_contents = new ArrayList<>();
        sub_list = new ArrayList<>();

        String contents = result_list.get(1);

        int value = 0;
        while (true) {
            if (value != -1) {
                value = contents.indexOf("니다", value + 1);
                if (value != -1) {
                    token_contents.add(value);
                }
            } else {
                value = contents.indexOf("니다");
                break;
            }
        }
        Log.e(TAG, "token_contents: " + token_contents);


        for (int i = 0; i < 26; i++) {
            if (i < 6) {
                token_number[i] = contents.indexOf(number[i]);
                if (token_number[i] == -1)
                    token_number[0] = -1;
            }

            if (i < 4) {
                token_flower[i] = contents.indexOf(flower[i]);
                if (token_flower[i] == -1)
                    token_flower[0] = -1;
            }

            if (i < 2) {
                token_fruit[i] = contents.indexOf(fruit[i]);
                if (token_fruit[i] == -1)
                    token_fruit[0] = -1;
            }

            token_other[i] = contents.indexOf(other[i]);
            if (token_other[i] == -1)
                token_other[0] = -1;
        }

        setSubList();
    }

    private void setSubList() {
        //첫번째 데이터
        String tmp = result_list.get(0).substring(2);
        if (tmp.contains(" ▒ ")) {
            tmp = tmp.replace(" ▒ ", " ▒ \n");
        } else if (tmp.contains(" ▒")) {
            tmp = tmp.replace(" ▒", " ▒\n");
        } else if (tmp.contains("꿈▒ ")) {
            tmp = tmp.replace("꿈▒ ", "꿈▒ \n");
        } else if (tmp.contains("꿈▒")) {
            tmp = tmp.replace("꿈▒", "꿈▒\n");
        }
        result_list.set(0, tmp);


        //두번째 데이터 세팅
        sub_list = new ArrayList<>();
        String str = result_list.get(1);

        if (token_contents.size() != 0) {
            sub_list.add(str.substring(2, token_contents.get(0) + 3));
            for (int i = 1; i < token_contents.size(); i++) {
                sub_list.add(str.substring(token_contents.get(i - 1) + 3, token_contents.get(i) + 3).trim());
            }
        } else {

            if (token_number[0] != -1) {
                for (int i = 0; i < token_number.length; i++) {
                    if (i == token_number.length - 1) {
                        sub_list.add(str.substring(token_number[i]).trim());
                    } else {
                        sub_list.add(str.substring(token_number[i], token_number[i + 1]).trim());
                    }
                }
            } else if (token_flower[0] != -1) {
                for (int i = 0; i < token_flower.length; i++) {
                    if (i == token_flower.length - 1) {
                        sub_list.add(str.substring(token_flower[i]).trim());
                    } else {
                        sub_list.add(str.substring(token_flower[i], token_flower[i + 1]).trim());
                    }
                }
            } else if (token_fruit[0] != -1) {
                for (int i = 0; i < token_fruit.length; i++) {
                    if (i == token_fruit.length - 1) {
                        sub_list.add(str.substring(token_fruit[i]).trim());
                    } else {
                        sub_list.add(str.substring(token_fruit[i], token_fruit[i + 1]).trim());
                    }
                }
            } else if (token_other[0] != -1) {
                for (int i = 0; i < token_other.length; i++) {
                    if (i == token_other.length - 1) {
                        sub_list.add(str.substring(token_other[i]).trim());
                    } else {
                        sub_list.add(str.substring(token_other[i], token_other[i + 1]).trim());
                    }
                }
            } else {
                sub_list.add(result_list.get(1).substring(2));
            }
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
