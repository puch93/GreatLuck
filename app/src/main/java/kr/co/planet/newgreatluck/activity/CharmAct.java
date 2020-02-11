package kr.co.planet.newgreatluck.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.planet.newgreatluck.R;
import kr.co.planet.newgreatluck.adapter.CharmAdapter;
import kr.co.planet.newgreatluck.data.CharmData;
import kr.co.planet.newgreatluck.data.CharmManagerData;
import kr.co.planet.newgreatluck.databinding.ActivityCharmBinding;
import kr.co.planet.newgreatluck.server.Cvalue;
import kr.co.planet.newgreatluck.server.HttpController;
import kr.co.planet.newgreatluck.util.Common;
import kr.co.planet.newgreatluck.util.ItemOffsetDecoration;

public class CharmAct extends BasicAct implements View.OnClickListener {
    ActivityCharmBinding binding;
    Activity act;

    ActionBar actionBar;

    RecyclerView recyclerView;
    GridLayoutManager manager;
    CharmAdapter adapter;

    ArrayList<CharmData> array_data;


    String[][] charm_ids;
    String[][] charm_names;
    String[][] charm_prices;
    String[][] charm_conetents;
    TypedArray[] charm_images;
    String[] charm_themes = {"연애/결혼", "재물/진급", "판매/구설", "기타"};
    CharmManagerData managerData;

    ArrayList<CharmData> charm_list01 = new ArrayList<>();
    ArrayList<CharmData> charm_list02 = new ArrayList<>();
    ArrayList<CharmData> charm_list03 = new ArrayList<>();
    ArrayList<CharmData> charm_list04 = new ArrayList<>();

    private static final int TYPE_01 = 0;
    private static final int TYPE_02 = 1;
    private static final int TYPE_03 = 2;
    private static final int TYPE_04 = 3;

    private static final int TYPE_01_COUNT = 7;
    private static final int TYPE_02_COUNT = 12;
    private static final int TYPE_03_COUNT = 7;
    private static final int TYPE_04_COUNT = 18;

    private static final String TYPE_01_TEXT = "연애/결혼";
    private static final String TYPE_02_TEXT = "재물/진급";
    private static final String TYPE_03_TEXT = "관재/구설";
    private static final String TYPE_04_TEXT = "기타";

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_charm, null);
        act = this;

        dialog = new ProgressDialog(act);
        dialog.setMessage("부적을 불러오는 중입니다.");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        setListener();

        charm_list01 = new ArrayList<>();
        charm_list02 = new ArrayList<>();
        charm_list03 = new ArrayList<>();
        charm_list04 = new ArrayList<>();

        setRecyclerView();

        setCharmListString();

        setActionBar();
    }


    private void setCharmListString() {
        /* 부적 내용 */
        charm_conetents = new String[4][];
        charm_conetents[TYPE_01] = getResources().getStringArray(R.array.charm_contents_01);
        charm_conetents[TYPE_02] = getResources().getStringArray(R.array.charm_contents_02);
        charm_conetents[TYPE_03] = getResources().getStringArray(R.array.charm_contents_03);
        charm_conetents[TYPE_04] = getResources().getStringArray(R.array.charm_contents_04);

        /* 부적 이름 */
        charm_names = new String[4][];
        charm_names[TYPE_01] = getResources().getStringArray(R.array.charm_names_01);
        charm_names[TYPE_02] = getResources().getStringArray(R.array.charm_names_02);
        charm_names[TYPE_03] = getResources().getStringArray(R.array.charm_names_03);
        charm_names[TYPE_04] = getResources().getStringArray(R.array.charm_names_04);

        /* 부적 가격 */
        charm_prices = new String[4][];
        charm_prices[TYPE_01] = getResources().getStringArray(R.array.charm_prices_01);
        charm_prices[TYPE_02] = getResources().getStringArray(R.array.charm_prices_02);
        charm_prices[TYPE_03] = getResources().getStringArray(R.array.charm_prices_03);
        charm_prices[TYPE_04] = getResources().getStringArray(R.array.charm_prices_04);


        charm_list01 = setCharmDataList(TYPE_01, TYPE_01_COUNT);
        charm_list02 = setCharmDataList(TYPE_02, TYPE_02_COUNT);
        charm_list03 = setCharmDataList(TYPE_03, TYPE_03_COUNT);
        charm_list04 = setCharmDataList(TYPE_04, TYPE_04_COUNT);

        binding.llMenu01.performClick();
    }

    private void setCharListImage(int type, ArrayList<CharmData> list) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        }, 750);

        getCharmImages(type, list);
    }

    private void getCharmImages(int type, ArrayList<CharmData> list) {
        final HttpController hc = new HttpController(Cvalue.Login);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "run");
                ArrayList<NameValuePair> p = new ArrayList<NameValuePair>();
                p.add(new BasicNameValuePair("siteUrl", Cvalue.Domain));
                p.add(new BasicNameValuePair("dbControl", "setImgList"));
                p.add(new BasicNameValuePair("i_class", "1"));
                p.add(new BasicNameValuePair("cate", charm_themes[type]));

                Log.i(TAG, "Charm Image Put Info : " + p);
                final String res = hc.getResultStreamPost(p);
                Log.e(TAG, "Charm Image Get Info : " + res);

                try {
                    JSONObject jo = new JSONObject(res);

                    final String result = jo.getString("result");
                    final String message = jo.getString("message");

                    if (result.equals("Y")) {
                        JSONArray ja = jo.getJSONArray("data");
                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject job = ja.getJSONObject(i);
                            list.get(i).setImageUrl(Cvalue.Domain2 + job.getString("i_img"));
                            list.get(i).setPurImageUrl(Cvalue.Domain2 + job.getString("i_img2"));
                            list.get(i).setIdx(job.getString("i_idx"));
                            list.get(i).setId(job.getString("i_name"));
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.setItems(list);
                                adapter.notifyDataSetChanged();
                                recyclerView.scrollToPosition(0);
                            }
                        });

                    } else {
                        Common.showToast(act, message);
                        Log.e(TAG, "message: " + message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private ArrayList<CharmData> setCharmDataList(int type, int count) {
        ArrayList<CharmData> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            CharmData data = new CharmData("", charm_names[type][i], charm_conetents[type][i], charm_prices[type][i], null, null, null);
            list.add(data);
        }
        return list;
    }

    private void setListener() {
        binding.llMenu01.setOnClickListener(this);
        binding.llMenu02.setOnClickListener(this);
        binding.llMenu03.setOnClickListener(this);
        binding.llMenu04.setOnClickListener(this);
    }

    private void setActionBar() {
        setSupportActionBar(binding.toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle(null);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.gf_arrow);
    }

    private void setRecyclerView() {
        array_data = new ArrayList<>();

        recyclerView = binding.rcvCharm;
        manager = new GridLayoutManager(act, 3);

        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);

        recyclerView.addItemDecoration(new ItemOffsetDecoration(getResources().getDimensionPixelSize(R.dimen.dimen_charm)));

        adapter = new CharmAdapter(act, array_data);
        recyclerView.setAdapter(adapter);
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


    private void init() {
        binding.llMenu01.setSelected(false);
        binding.llMenu02.setSelected(false);
        binding.llMenu03.setSelected(false);
        binding.llMenu04.setSelected(false);

        binding.tvMenu01.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.color_3b3a39));
        binding.tvMenu02.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.color_3b3a39));
        binding.tvMenu03.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.color_3b3a39));
        binding.tvMenu04.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.color_3b3a39));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_menu01:
                init();
                binding.llMenu01.setSelected(true);
                binding.tvMenu01.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));

                setCharListImage(TYPE_01, charm_list01);
                dialog.show();
                break;

            case R.id.ll_menu02:
                init();
                binding.llMenu02.setSelected(true);
                binding.tvMenu02.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));

                setCharListImage(TYPE_02, charm_list02);
                dialog.show();
                break;

            case R.id.ll_menu03:
                init();
                binding.llMenu03.setSelected(true);
                binding.tvMenu03.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));

                setCharListImage(TYPE_03, charm_list03);
                dialog.show();
                break;

            case R.id.ll_menu04:
                init();
                binding.llMenu04.setSelected(true);
                binding.tvMenu04.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));

                setCharListImage(TYPE_04, charm_list04);
                dialog.show();
                break;
        }
    }
}
