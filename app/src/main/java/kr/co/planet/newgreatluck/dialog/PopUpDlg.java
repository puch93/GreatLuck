package kr.co.planet.newgreatluck.dialog;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import kr.co.planet.newgreatluck.R;
import kr.co.planet.newgreatluck.activity.LoadingAct;
import kr.co.planet.newgreatluck.activity.MainAct;
import kr.co.planet.newgreatluck.databinding.DialogPopupBinding;
import kr.co.planet.newgreatluck.util.StringUtil;

import static android.view.animation.Animation.INFINITE;
import static android.view.animation.Animation.REVERSE;

public class PopUpDlg extends BasicDlg {
    DialogPopupBinding binding;
    Activity act;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "PopUpDlg in");
        setFinishOnTouchOutside(false);
//        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

        super.onCreate(savedInstanceState);
        act = this;

        /* confirm front or popup */

        String sendType = getIntent().getStringExtra("sendType");

        /* set title, contents */
        String title = getIntent().getStringExtra("title");
        String contents = getIntent().getStringExtra("contents");
        int type = getIntent().getIntExtra("type", 0);

        if (!StringUtil.isNull(sendType)) {
            Log.e(TAG, "PopUpDlg type: push");
            if (MainAct.act != null) {
                MainAct.act.finish();
            }

            Intent intent = new Intent(act, LoadingAct.class);
            intent.putExtra("fromAlarm", type);
            startActivity(intent);
            finish();
        } else {
            Log.e(TAG, "PopUpDlg type: popup");
            binding = DataBindingUtil.setContentView(this, R.layout.dialog_popup, null);

            binding.tvTitle.setText(title);
//            binding.tvMessage.setText(contents);


            /* set blink animation */
            final Animation animation = new AlphaAnimation((float) 1.0, 0);
            animation.setDuration(600);
            animation.setInterpolator(new LinearInterpolator());
            animation.setRepeatCount(INFINITE);
            animation.setRepeatMode(REVERSE);
            binding.ivLetter.startAnimation(animation);

            /* ok button listener */
            binding.flOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (MainAct.act != null) {
                        MainAct.act.finish();
                    }

                    Intent intent = new Intent(act, LoadingAct.class);
                    intent.putExtra("fromAlarm", type);
                    startActivity(intent);
                    finish();

                    NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(1);
                }
            });

            binding.flCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(1);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
