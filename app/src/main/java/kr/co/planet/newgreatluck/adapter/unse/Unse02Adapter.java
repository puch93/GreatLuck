package kr.co.planet.newgreatluck.adapter.unse;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.planet.newgreatluck.R;
import kr.co.planet.newgreatluck.activity.CharmAct;
import kr.co.planet.newgreatluck.activity.CharmDetailAct;
import kr.co.planet.newgreatluck.activity.SubscriptionAct;
import kr.co.planet.newgreatluck.data.CharmData;
import kr.co.planet.newgreatluck.data.InfoData;
import kr.co.planet.newgreatluck.preference.UserPref;
import kr.co.planet.newgreatluck.server.Cvalue;
import kr.co.planet.newgreatluck.server.HttpController;
import kr.co.planet.newgreatluck.util.Common;


public class Unse02Adapter extends RecyclerView.Adapter<Unse02Adapter.ViewHolder> implements View.OnClickListener {
    private ArrayList<String> contents_list;
    private ArrayList<Integer> star_list;
    private ArrayList<String> title_list;
    private ArrayList<CharmData> charmAdsList;
    private int title_drawable;
    private String title;


    private InfoData myInfo;
    private TypedArray images;
    private int type;

    private Activity act;

    private static final String TAG = "TEST_HOME";

    public static final int SUBSCRIPTION = 2000;

    public Unse02Adapter(Activity act, ArrayList<String> contents_list, ArrayList<Integer> star_list, ArrayList<String> title_list, ArrayList<CharmData> charmAdsList, int title_drawable, String title, int type) {
        this.act = act;

        this.contents_list = contents_list;
        this.star_list = star_list;
        this.title_list = title_list;
        this.charmAdsList = charmAdsList;

        this.title_drawable = title_drawable;
        this.title = title;
        this.type = type;

        images = act.getResources().obtainTypedArray(R.array.zodiac_drawable);
        myInfo = UserPref.getMyInfo(act);
    }


    @NonNull
    @Override
    public Unse02Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_unse_contents, viewGroup, false);
        Unse02Adapter.ViewHolder viewHolder = new Unse02Adapter.ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull Unse02Adapter.ViewHolder holder, int i) {
        if (i == 0) {
            holder.ll_top_contents.setVisibility(View.VISIBLE);
            holder.ll_contents.setVisibility(View.GONE);
            holder.ll_bottom_charm.setVisibility(View.GONE);
            images.recycle();

            holder.iv_title_image.setImageResource(title_drawable);
            holder.tv_unse_type_top.setText(title);
            holder.tv_name.setText(myInfo.getName());
            holder.tv_unse_type.setText(title);

        } else if (i == contents_list.size() + 1) {
            holder.ll_top_contents.setVisibility(View.GONE);
            holder.ll_contents.setVisibility(View.GONE);
            holder.ll_bottom_charm.setVisibility(View.VISIBLE);

            /* charm ads */
                holder.fl_more_charm.setOnClickListener(this);
                holder.fl_subscription.setOnClickListener(this);

            for (int j = 0; j < 3; j++) {
                setCharmAds(j, holder, charmAdsList.get(j));
            }
        } else {
            holder.ll_top_contents.setVisibility(View.GONE);
            holder.ll_contents.setVisibility(View.VISIBLE);
            holder.ll_bottom_charm.setVisibility(View.GONE);

            final String title = title_list.get(i - 1);
            holder.today_title.setText(title);

            final String contents = contents_list.get(i - 1);
            final int star_count = star_list.get(i - 1);

            holder.tv_star_count.setText(String.valueOf(star_count));

            for (int j = 0; j < star_count; j++) {
                switch (j) {
                    case 0:
                        holder.view_list.get(j).setImageResource(R.drawable.gf_scorestar1);
                        break;
                    case 1:
                        holder.view_list.get(j).setImageResource(R.drawable.gf_scorestar2);
                        break;
                    case 2:
                        holder.view_list.get(j).setImageResource(R.drawable.gf_scorestar3);
                        break;
                    case 3:
                        holder.view_list.get(j).setImageResource(R.drawable.gf_scorestar4);
                        break;
                    case 4:
                        holder.view_list.get(j).setImageResource(R.drawable.gf_scorestar5);
                        break;
                }
            }

            holder.today_total_fortune.setText(contents);
        }
    }

    private void setCharmAds(int idx, Unse02Adapter.ViewHolder holder, CharmData data) {
        holder.tv_charms.get(idx).setText(data.getName());
        holder.ll_charms.get(idx).setOnClickListener(this);
        setCharmAdsImage(idx, holder, data.getId());
    }

    private void setCharmAdsImage(int idx, Unse02Adapter.ViewHolder holder, String id) {
        final HttpController hc = new HttpController(Cvalue.Login);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "run");
                ArrayList<NameValuePair> p = new ArrayList<NameValuePair>();
                p.add(new BasicNameValuePair("siteUrl", Cvalue.Domain));
                p.add(new BasicNameValuePair("dbControl", "setImgListDetail"));
                p.add(new BasicNameValuePair("i_name", id));

                Log.i(TAG, "Charm Ads Image Put Info : " + p);
                final String res = hc.getResultStreamPost(p);
                Log.e(TAG, "Charm Ads Image Get Info : " + res);

                try {
                    JSONObject jo = new JSONObject(res);

                    final String result = jo.getString("result");
                    final String message = jo.getString("message");
                    final String img = jo.getString("img");
                    final String img2 = jo.getString("i_img2");
                    final String i_idx = jo.getString("i_idx");

                    act.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (result.equals("Y")) {
                                charmAdsList.get(idx).setImageUrl(Cvalue.Domain2 + img);
                                charmAdsList.get(idx).setPurImageUrl(Cvalue.Domain2 + img2);
                                charmAdsList.get(idx).setIdx(i_idx);

                                Glide.with(act)
                                        .load(Cvalue.Domain2 + img)
                                        .placeholder(R.drawable.gf_tp_m_off)
                                        .into(holder.iv_charms.get(idx));
                            } else {
                                Common.showToast(act, message);
                                Log.e(TAG, "message: " + message);
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @Override
    public int getItemCount() {
        return contents_list.size() + 2;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_more_charm:
                act.startActivity(new Intent(act, CharmAct.class));
                break;

            case R.id.fl_subscription:
                act.startActivityForResult(new Intent(act, SubscriptionAct.class), SUBSCRIPTION);
                break;

            case R.id.ll_charm1:
                doIntent(charmAdsList.get(0));
                break;
            case R.id.ll_charm2:
                doIntent(charmAdsList.get(1));
                break;
            case R.id.ll_charm3:
                doIntent(charmAdsList.get(2));
                break;

        }
    }

    private void doIntent(CharmData data) {
        Intent intent = new Intent(act, CharmDetailAct.class);
        intent.putExtra("selectedData", data);
        act.startActivity(intent);
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        /* top data */
        ImageView iv_title_image;
        TextView tv_name, tv_unse_type, tv_unse_type_top;
        LinearLayout ll_top_contents;

        /* contents data */
        LinearLayout ll_contents;
        TextView today_title, today_total_fortune, tv_star_count;
        ArrayList<ImageView> view_list = new ArrayList<>();

        /* bottom charm ads */
        ArrayList<TextView> tv_charms;
        ArrayList<ImageView> iv_charms;
        ArrayList<LinearLayout> ll_charms;


        FrameLayout fl_more_charm, fl_subscription;
        LinearLayout ll_bottom_charm;

        ViewHolder(View v) {
            super(v);
            /* top data */
            iv_title_image = (ImageView) v.findViewById(R.id.iv_title_image);
            tv_unse_type_top = (TextView) v.findViewById(R.id.tv_unse_type_top);

            tv_name = (TextView) v.findViewById(R.id.tv_name);
            tv_unse_type = (TextView) v.findViewById(R.id.tv_unse_type);
            ll_top_contents = (LinearLayout) v.findViewById(R.id.ll_top_contents);

            /* top data */
            ll_contents = (LinearLayout) v.findViewById(R.id.ll_contents);
            today_title = (TextView) v.findViewById(R.id.today_title);
            today_total_fortune = (TextView) v.findViewById(R.id.today_total_fortune);
            tv_star_count = (TextView) v.findViewById(R.id.tv_star_count);

            view_list.add((ImageView) v.findViewById(R.id.today_total_staricon1));
            view_list.add((ImageView) v.findViewById(R.id.today_total_staricon2));
            view_list.add((ImageView) v.findViewById(R.id.today_total_staricon3));
            view_list.add((ImageView) v.findViewById(R.id.today_total_staricon4));
            view_list.add((ImageView) v.findViewById(R.id.today_total_staricon5));

            /* bottom charm ads */
            tv_charms = new ArrayList<>();
            tv_charms.add((TextView) v.findViewById(R.id.tv_charm_name1));
            tv_charms.add((TextView) v.findViewById(R.id.tv_charm_name2));
            tv_charms.add((TextView) v.findViewById(R.id.tv_charm_name3));

            iv_charms = new ArrayList<>();
            iv_charms.add((ImageView) v.findViewById(R.id.iv_charm1));
            iv_charms.add((ImageView) v.findViewById(R.id.iv_charm2));
            iv_charms.add((ImageView) v.findViewById(R.id.iv_charm3));

            ll_charms = new ArrayList<>();
            ll_charms.add((LinearLayout) v.findViewById(R.id.ll_charm1));
            ll_charms.add((LinearLayout) v.findViewById(R.id.ll_charm2));
            ll_charms.add((LinearLayout) v.findViewById(R.id.ll_charm3));

            fl_more_charm = (FrameLayout) v.findViewById(R.id.fl_more_charm);
            fl_subscription = (FrameLayout) v.findViewById(R.id.fl_subscription);
            ll_bottom_charm = (LinearLayout) v.findViewById(R.id.ll_bottom_charm);
        }
    }
}
