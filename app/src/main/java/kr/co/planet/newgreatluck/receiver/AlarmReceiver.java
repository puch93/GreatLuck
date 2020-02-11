package kr.co.planet.newgreatluck.receiver;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

import kr.co.planet.newgreatluck.R;
import kr.co.planet.newgreatluck.dialog.PopUpDlg;
import kr.co.planet.newgreatluck.preference.UserPref;
import kr.co.planet.newgreatluck.util.PushWakeLock;

/**
 * Intent 플래그
 * FLAG_ONE_SHOT : 한번만 사용하고 다음에 이 PendingIntent가 불려지면 Fail을 함
 * FLAG_NO_CREATE : PendingIntent를 생성하지 않음. PendingIntent가 실행중인것을 체크를 함
 * FLAG_CANCEL_CURRENT : 실행중인 PendingIntent가 있다면 기존 인텐트를 취소하고 새로만듬
 * FLAG_UPDATE_CURRENT : 실행중인 PendingIntent가 있다면  Extra Data만 교체함
 * <p>
 * AlarmType
 * RTC_WAKEUP : 대기모드에서도 알람이 작동함을 의미함
 * RTC : 대기모드에선 알람을 작동안함
 */

public class AlarmReceiver extends BroadcastReceiver {
    private Context ctx;
    public static final String TAG = "TEST_HOME";

    private static final int TYPE_ALARM_01 = 1001;
    private static final int TYPE_ALARM_02 = 1002;
    private static final int TYPE_ALARM_03 = 1003;

    private static final int TYPE_ALARM_CODE_01 = 0;
    private static final int TYPE_ALARM_CODE_02 = 1;
    private static final int TYPE_ALARM_CODE_03 = 2;

    Intent intent;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("TEST_HOME", "onReceive");

        ctx = context;
        this.intent = intent;

        process();
    }

    private void process() {
        if (UserPref.getAlarmState(ctx)) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date(System.currentTimeMillis()));
            int dayNum = cal.get(Calendar.DAY_OF_WEEK);
            String day = "";
            switch (dayNum) {

                case 1:
                    day = "일";
                    break;
                case 2:
                    day = "월";
                    break;
                case 3:
                    day = "화";
                    break;
                case 4:
                    day = "수";
                    break;
                case 5:
                    day = "목";
                    break;
                case 6:
                    day = "금";
                    break;
                case 7:
                    day = "토";
                    break;
            }

            String title = "오늘의 운세";
            String contents = day + ctx.getString(R.string.alarm_contents_01);

            sendNotiAndPopup(title, contents);
        }

        setAlarm();
    }

    private void sendNotiAndPopup(String title, String contents) {
        PowerManager pm = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
        if (!pm.isScreenOn()) {
            //화면 깨우기
            PushWakeLock.acquireCpuWakeLock(ctx);
            PushWakeLock.releaseCpuLock();
        }

        /* send noti and popup */
        sendNotification(title, contents);
    }

    /* 푸시보내기 기본*/
    public void sendNotification(String title, String message) {
        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        //If on Oreo then notification required a notification channel.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default", "Default", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, "default")
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.app_icon);


        Intent intent = new Intent(ctx, PopUpDlg.class);
        intent.putExtra("title", title);
        intent.putExtra("contents", message);
        intent.putExtra("type", TYPE_ALARM_CODE_01);
        intent.putExtra("sendType", "front");

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Notification notification = builder.build();

        notification.flags = notification.flags | notification.FLAG_AUTO_CANCEL;

        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pendingIntent);
        notification = builder.build();
        notificationManager.notify(1, notification);
    }


    private void setAlarm() {
        Log.e(TAG, "setAlarm");

        /* initialize alarm service */
        Calendar mCalendar = Calendar.getInstance();


        /* set compare data */
        int LAST_DAY_OF_YEAR = mCalendar.getMaximum(Calendar.DAY_OF_YEAR);
        int NOW_DAY_OF_YEAR = mCalendar.get(Calendar.DAY_OF_YEAR);


        /* set calendar */
        Calendar calendar01 = setCalendar(TYPE_ALARM_01, NOW_DAY_OF_YEAR, LAST_DAY_OF_YEAR);


        /* set alarm manager */
        AlarmManager manager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);


        /* set pending intent */
        PendingIntent pendingIntent01 = PendingIntent.getBroadcast(ctx, TYPE_ALARM_CODE_01, new Intent(ctx, AlarmReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);


        /* register alarm (버전별로 따로) */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.e(TAG, "first");
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar01.getTimeInMillis(), pendingIntent01); //10초뒤 알람
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Log.e("TEST_HOME", "second");
            manager.setExact(AlarmManager.RTC_WAKEUP, calendar01.getTimeInMillis(), pendingIntent01);
        } else {
            Log.e("TEST_HOME", "third");
            manager.set(AlarmManager.RTC_WAKEUP, calendar01.getTimeInMillis(), pendingIntent01);
        }
    }

    private Calendar setCalendar(int type, int NOW_DAY_OF_YEAR, int LAST_DAY_OF_YEAR) {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        // manager.set 할때 현재시간보다 이전 시간대면, 리시버가 바로 실행됨, 다음날로 지정해줘야 함 => ex) 세팅시간 8:00, 현재시간 9:20
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            Log.e(TAG, "현재시간보다 작음");

            // 올해의 마지막 날일 경우
            if (NOW_DAY_OF_YEAR == LAST_DAY_OF_YEAR) {
                NOW_DAY_OF_YEAR = 1;
            } else {
                NOW_DAY_OF_YEAR++;
            }
            calendar.set(Calendar.DAY_OF_YEAR, NOW_DAY_OF_YEAR);
        }

        return calendar;
    }

}
