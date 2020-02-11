package kr.co.planet.newgreatluck.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kr.co.planet.newgreatluck.R;
import kr.co.planet.newgreatluck.adapter.notice.NoticeAdapter;
import kr.co.planet.newgreatluck.data.NoticeChildData;
import kr.co.planet.newgreatluck.data.NoticeParentData;
import kr.co.planet.newgreatluck.databinding.ActivityNoticeBinding;
import kr.co.planet.newgreatluck.server.Cvalue;
import kr.co.planet.newgreatluck.server.HttpController;
import kr.co.planet.newgreatluck.util.Common;

public class NoticeAct extends BasicAct {
    ActivityNoticeBinding binding;
    Activity act;

    ActionBar actionBar;

    RecyclerView recyclerView;
    LinearLayoutManager manager;
    NoticeAdapter adapter;

    List<NoticeParentData> array_data;

    List<NoticeParentData> noticeList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notice, null);
        act = this;

        binding.flCharm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(act, CharmAct.class));
            }
        });


        getNoticeData();

        setActionBar();
    }

    private void getNoticeData() {
        final HttpController hc = new HttpController(Cvalue.Login);
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<NameValuePair> p = new ArrayList<NameValuePair>();
                p.add(new BasicNameValuePair("siteUrl", Cvalue.Domain));
                p.add(new BasicNameValuePair("APPCONNECTCODE", Build.FINGERPRINT));
                p.add(new BasicNameValuePair("dbControl", "getNotice"));


                Log.i(TAG, "Notice Idx Put Info : " + p);
                final String res = hc.getResultStreamPost(p);
                Log.e(TAG, "Notice Idx Get Info : " + res);

                try {
                    JSONObject jo = new JSONObject(res);

                    final String result = jo.getString("result");
                    final String message = jo.getString("message");

                    if (result.equals("Y")) {
                        JSONArray ja = jo.getJSONArray("data");
                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject job = ja.getJSONObject(i);
                            String title = job.getString("b_title");
                            String regDate = job.getString("b_regdate");

                            SimpleDateFormat orgin = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            try {
                                Date old = orgin.parse(regDate);
                                regDate = sdf.format(old);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            String idx = job.getString("b_idx");
                            String contents = job.getString("b_contents");

                            //자식 데이터 (내용)
                            List<NoticeChildData> childDataList = new ArrayList<>();
                            NoticeChildData sub = new NoticeChildData(contents);
                            childDataList.add(sub);

                            //부모 데이터 (타이틀, 날짜, isNew)
                            NoticeParentData data = new NoticeParentData(title, regDate, false, childDataList);
                            data.setIdx(idx);
                            if( i < 2) {
                                data.setNew(true);
                            }
                            noticeList.add(data);
                        }

                        /* last item => 넣어줘야 마지막 애니메이션 적용됨 */
                        List<NoticeChildData> lastChildList = new ArrayList<>();
                        NoticeChildData lastItem_sub = new NoticeChildData("");
                        lastChildList.add(lastItem_sub);

                        NoticeParentData lastItem = new NoticeParentData("", "", false, lastChildList);
                        noticeList.add(lastItem);

                        for (int i = 0; i < noticeList.size() - 1; i++) {
                            getNoticeDetail(noticeList.get(i), i);
                        }
                    } else {
                        Common.showToast(act, message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void getNoticeDetail(NoticeParentData data, int pos) {
        final HttpController hc = new HttpController(Cvalue.Login);
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<NameValuePair> p = new ArrayList<NameValuePair>();
                p.add(new BasicNameValuePair("siteUrl", Cvalue.Domain));
                p.add(new BasicNameValuePair("APPCONNECTCODE", Build.FINGERPRINT));
                p.add(new BasicNameValuePair("dbControl", "getNoticeDetail"));
                p.add(new BasicNameValuePair("CODE", data.getIdx()));


                Log.i(TAG, "Notice Detail Put Info : " + p);
                final String res = hc.getResultStreamPost(p);
                Log.e(TAG, "Notice Detail Get Info : " + res);

                try {
                    Log.e(TAG, "detail");
                    JSONObject jo = new JSONObject(res);
                    if (jo.getString("b_new_is").equals("Y")) {
                        data.setNew(true);
                    } else {
                        data.setNew(false);
                    }
                    noticeList.set(pos, data);
                    if (pos == noticeList.size() - 2) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setRecyclerView();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void setRecyclerView() {
        recyclerView = binding.recyclerView;
        manager = new LinearLayoutManager(this);

        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        if (animator instanceof DefaultItemAnimator) {
            ((DefaultItemAnimator) animator).setSupportsChangeAnimations(false);
        }

        adapter = new NoticeAdapter(noticeList);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setHasFixedSize(true);
    }


    private void setActionBar() {
        setSupportActionBar(binding.toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle(null);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.gf_arrow);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        adapter.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        adapter.onRestoreInstanceState(savedInstanceState);
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


    private void setSampleData() {
        array_data = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            List<NoticeChildData> childDataList = new ArrayList<>();

            NoticeChildData sample1_sub = new NoticeChildData(getString(R.string.sample1_sub));
            childDataList.add(sample1_sub);

            //부모 데이터 (타이틀, 날짜, isNew)
            NoticeParentData sample1 = new NoticeParentData(getString(R.string.sample1) + " " + i, "2019-07-26", true, childDataList);
            array_data.add(sample1);
        }

        List<NoticeChildData> childDataList2 = new ArrayList<>();
        NoticeChildData sample1_sub2 = new NoticeChildData(getString(R.string.sample2_sub));
        childDataList2.add(sample1_sub2);

        NoticeParentData sample2 = new NoticeParentData(getString(R.string.sample2), "2019-07-24", false, childDataList2);
        array_data.add(sample2);


        /* last item => 넣어줘야 마지막 애니메이션 적용됨 */
        List<NoticeChildData> lastChildList = new ArrayList<>();
        NoticeChildData lastItem_sub = new NoticeChildData("");
        lastChildList.add(lastItem_sub);

        NoticeParentData lastItem = new NoticeParentData("", "", false, lastChildList);
        array_data.add(lastItem);
    }
}
