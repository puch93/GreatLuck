package kr.co.planet.newgreatluck.dialog;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.bumptech.glide.Glide;

import kr.co.planet.newgreatluck.R;
import kr.co.planet.newgreatluck.databinding.DialogPopupAdBinding;
import kr.co.planet.newgreatluck.server.Cvalue;

public class PopUpAdDlg extends BasicDlg {
    DialogPopupAdBinding binding;
    Activity act;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 여백터치시 꺼지지 않게
        setFinishOnTouchOutside(false);

        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.dialog_popup_ad, null);
        act = this;

        // dialog theme 을 적용했을때, 패딩제거 목적
        WindowManager.LayoutParams wmlp = getWindow().getAttributes();
        wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;


        String imageUrl = getIntent().getStringExtra("imageUrl");
        Log.e(TAG, "imageUrl: " + imageUrl);
        String url = getIntent().getStringExtra("url");

        Glide.with(act).load(Cvalue.Domain2 + imageUrl).into(binding.ivContents);

        /* ok button listener */
        binding.ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(1);

                finish();
            }
        });

        binding.ivContents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(1);

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
