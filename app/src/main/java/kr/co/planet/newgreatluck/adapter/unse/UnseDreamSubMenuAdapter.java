package kr.co.planet.newgreatluck.adapter.unse;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.planet.newgreatluck.R;
import kr.co.planet.newgreatluck.activity.unse.UnseDreamAct;
import kr.co.planet.newgreatluck.server.Connect;
import kr.co.planet.newgreatluck.server.Cvalue;
import kr.co.planet.newgreatluck.util.Common;
import kr.co.planet.newgreatluck.util.CommonCode;


public class UnseDreamSubMenuAdapter extends RecyclerView.Adapter<UnseDreamSubMenuAdapter.ViewHolder> {
    private String[] code_array, title_array;
    private Activity act;
    private ArrayList<String> result_list;
    ProgressDialog dialog;

    private static final String TAG = "TEST_HOME";


    public UnseDreamSubMenuAdapter(Activity act, String[] code_array, String[] title_array) {
        this.act = act;
        this.code_array = code_array;
        this.title_array = title_array;

        dialog = new ProgressDialog(act);
        dialog.setMessage("운세를 불러오는 중입니다..");
        dialog.setCanceledOnTouchOutside(false);
    }


    @NonNull
    @Override
    public UnseDreamSubMenuAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_unse_dream_menu, viewGroup, false);
        UnseDreamSubMenuAdapter.ViewHolder viewHolder = new UnseDreamSubMenuAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UnseDreamSubMenuAdapter.ViewHolder holder, int i) {
        final String selectedCode = code_array[i];
        final String selectedTitle = title_array[i];
        holder.tv_menu.setText(selectedTitle);

        holder.ll_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                process(selectedCode);
            }
        });
    }

    private void process(String selectedCode) {
        Connect c = new Connect();
        c.setValue(CommonCode.PARAM_DREAM_KIND, selectedCode);
        c.setValue(CommonCode.PARAM_UNSE, CommonCode.PARAM_DREAM_UNSE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                final String res = c.sendToServer(Cvalue.Today);
                Log.e(TAG, "Menu01 (" + selectedCode + ") Get Info: " + res);

                if (res != null) {
                    try {
                        JSONObject jo = new JSONObject(res);

                        result_list = new ArrayList<>();
                        for (int i = 1;; i++) {
                            Log.e(TAG, "fo_con" + i + ": " + jo.get("fo_con" + i).toString().trim());

                            String data = jo.get("fo_con" + i).toString().trim();
                            data = data.replace("   ", "");
                            result_list.add(data);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    process_intent();
                } else {
                    Common.showToastNetwork(act);
                }
            }
        }).start();
    }


    private void process_intent() {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                }, 750);
            }
        });
        
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("list", result_list);

        Intent intent = new Intent(act, UnseDreamAct.class);
        intent.putExtras(bundle);
        act.startActivity(intent);
//        act.finish();
    }


    @Override
    public int getItemCount() {
        return code_array.length;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_menu;
        LinearLayout ll_menu;

        ViewHolder(View v) {
            super(v);
            tv_menu = (TextView) v.findViewById(R.id.tv_menu);
            ll_menu = (LinearLayout) v.findViewById(R.id.ll_menu);
        }
    }
}
