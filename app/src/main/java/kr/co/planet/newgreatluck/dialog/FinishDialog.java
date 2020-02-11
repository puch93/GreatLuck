package kr.co.planet.newgreatluck.dialog;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.planet.newgreatluck.R;
import kr.co.planet.newgreatluck.databinding.DialogBornTimeBinding;
import kr.co.planet.newgreatluck.databinding.DialogFinishBinding;
import kr.co.planet.newgreatluck.preference.UserPref;
import kr.co.planet.newgreatluck.server.Cvalue;
import kr.co.planet.newgreatluck.server.HttpController;

public class FinishDialog extends BasicDlg implements View.OnClickListener {
    DialogFinishBinding binding;
    Activity act;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.dialog_finish, null);
        act = this;

        firstSet();

        if(UserPref.getCheerState(act)) {
            //평점페이지
            binding.ivAds.setImageResource(R.drawable.gf_cheer_bn);
            binding.ivAds.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cheer();
                    setCheerState();
                    finish();
                }
            });
        } else {
            //공유하기
            binding.ivAds.setImageResource(R.drawable.gf_friend_bn);
            binding.ivAds.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareAppLink();
                    finish();
                }
            });
        }
    }

    private void setCheerState() {
        final HttpController hc = new HttpController(Cvalue.Login);
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<NameValuePair> p = new ArrayList<NameValuePair>();
                p.add(new BasicNameValuePair("siteUrl", Cvalue.Domain));
                p.add(new BasicNameValuePair("APPCONNECTCODE", Build.FINGERPRINT));
                p.add(new BasicNameValuePair("dbControl", "setCheckReviewState"));
                p.add(new BasicNameValuePair("_APP_MEM_IDX", UserPref.getMidx(act)));

                Log.i(TAG, "Set Cheer Put Info : " + p);
                final String res = hc.getResultStreamPost(p);
                Log.e(TAG, "Set Cheer Get Info : " + res);

                try {
                    JSONObject jo = new JSONObject(res);

                    final String result = jo.getString("result");
                    final String message = jo.getString("message");

                    if(result.equalsIgnoreCase("success")) {
                        UserPref.saveCheerState(act, false);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }).start();
    }

    private void firstSet() {
        binding.tvCancel.setOnClickListener(this);
        binding.tvOk.setOnClickListener(this);
        binding.ivAds.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_ok:
                setResult(RESULT_OK);
                finish();
                overridePendingTransition(0,0);
                break;

            case R.id.tv_cancel:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
    }
}
