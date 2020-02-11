package kr.co.planet.newgreatluck.util;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

public class Common {
    // Toast
    public static void showToast(final Activity act, final String msg) {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(act, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void showToastLong(final Activity act, final String msg) {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(act, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void showToastDevelop(final Activity act) {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(act, "개발중인 기능입니다", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void showToastNetwork(final Activity act) {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(act, "네트워크 상태를 확인해주세요", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Snackbar
    public static void showSnackbar(final Activity act, final String msg) {

        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Snackbar.make(act.getWindow().getDecorView().getRootView(), msg, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    public static void showSnackbarLong(final Activity act, final String msg) {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Snackbar.make(act.getWindow().getDecorView().getRootView(), msg, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    // get phone num
    public static String getPhoneNumber(Activity act) {
        TelephonyManager tm = (TelephonyManager) act.getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNum = tm.getLine1Number();
        if (StringUtil.isNull(phoneNum)) {
            return null;
        } else {
            if (phoneNum.startsWith("+82")) {
                phoneNum = phoneNum.replace("+82", "0");
            }
            return phoneNum;
        }
    }

    // get sim operator
    public static String getSimOperator(Activity act) {
        TelephonyManager tm = (TelephonyManager) act.getSystemService(Context.TELEPHONY_SERVICE);
        String so = tm.getSimOperator();
        String agencyNumber = "";

        if (so != null && !so.trim().equals("")) {

            switch (Integer.parseInt(so)) {
                case 45005:
                case 45011:
                    agencyNumber = "0"; // SKT
                    break;

                case 45002:
                case 45004:
                case 45008:
                    agencyNumber = "1"; // KTF
                    break;

                case 45003:
                case 45006:
                    agencyNumber = "2"; // LG U+
                    break;

                default:
                    agencyNumber = "99";
                    break;
            }
        }

        return agencyNumber;
    }

    // get device density
    public static String getDensityName(Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        if (density >= 4.0) {
            return "xxxhdpi";
        }
        if (density >= 3.0) {
            return "xxhdpi";
        }
        if (density >= 2.0) {
            return "xhdpi";
        }
        if (density >= 1.5) {
            return "hdpi";
        }
        if (density >= 1.0) {
            return "mdpi";
        }
        return "ldpi";
    }


    /**
     * 사용법
     * boolean isAppToRun = Common.isAppTopRun(context, LoadingAct.class.getName().toString());
     */
    public static boolean isAppTopRun(Context ctx, String baseClassName) {
        ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> info;
        info = activityManager.getRunningTasks(1);
        if (info == null || info.size() == 0) {
            return false;
        }

        return info.get(0).baseActivity.getClassName().equals(baseClassName);
    }

    // get device id
    public static String getDeviceId(Context ctx) {
        String deviceID = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ctx.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                deviceID = ((TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
            }
        } else {
            deviceID = ((TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        }

        if (StringUtil.isNull(deviceID)) {
            deviceID = "35" +
                    Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                    Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +
                    Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
                    Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
                    Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
                    Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
                    Build.USER.length() % 10;
            if (TextUtils.isEmpty(deviceID)) {
                deviceID = UUID.randomUUID().toString();
            }
        }

        return deviceID;
    }

    // 나중에 참고할 것
    private void other() {
        /* ring alarm ton (팝업알림시 소리내기위함) */
//        Uri alarmTone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        Ringtone ringtoneAlarm = RingtoneManager.getRingtone(context, alarmTone);
//        Log.e(TAG, "ringtoneAlarm: " + ringtoneAlarm);
//        ringtoneAlarm.play();
//
//        ComponentName comp = new ComponentName(ctx.getPackageName(),
//                AlarmReceiver.class.getName());
//
//        Intent serviceIntent = new Intent(ctx, AlarmReceiver.class);
//        startWakefulService(ctx, (serviceIntent.setComponent(comp)));
    }

    public static String checkZodiac(String year) {
        String z = String.valueOf((Integer.parseInt(year) - 3) % 12);
        if (Integer.parseInt(z) < 10)
            z = "0" + z;
        else if (Integer.parseInt(z) == 00) {
            z = "12;";
        }
        return z;
    }



    public static String checkZodiacText(String s) {
        String result = null;
        Log.e("TEST_HOME", "checkZodiacText -> s: " + s);

        switch (Integer.parseInt(s)) {
            case 00:
                result = "돼지띠";
                break;
            case 1:
                result = "쥐띠";
                break;
            case 2:
                result = "소띠";
                break;
            case 3:
                result = "호랑이띠";
                break;
            case 4:
                result = "토끼띠";
                break;
            case 5:
                result = "용띠";
                break;
            case 6:
                result = "뱀띠";
                break;
            case 7:
                result = "말띠";
                break;
            case 8:
                result = "양띠";
                break;
            case 9:
                result = "원숭이띠";
                break;
            case 10:
                result = "닭띠";
                break;
            case 11:
                result = "개띠";
                break;
        }

        return result;
    }

    public static String checkConstellation(String _month, String _day) {
        int month = Integer.parseInt(_month);
        int day = Integer.parseInt(_day);
        String result = null;

        switch (month) {
            case 1:
                if (day >= 1 && day <= 19) {
                    result = "12";
                } else {
                    result = "01";
                }
                break;

            case 2:
                if (day >= 1 && day <= 18) {
                    result = "01";
                } else {
                    result = "02";
                }
                break;
            case 3:
                if (day >= 1 && day <= 20) {
                    result = "02";
                } else {
                    result = "03";
                }
                break;
            case 4:
                if (day >= 1 && day <= 19) {
                    result = "03";
                } else {
                    result = "04";
                }
                break;
            case 5:
                if (day >= 1 && day <= 20) {
                    result = "04";
                } else {
                    result = "05";
                }
                break;
            case 6:
                if (day >= 1 && day <= 21) {
                    result = "05";
                } else {
                    result = "06";
                }
                break;
            case 7:
                if (day >= 1 && day <= 22) {
                    result = "06";
                } else {
                    result = "07";
                }
                break;
            case 8:
                if (day >= 1 && day <= 22) {
                    result = "07";
                } else {
                    result = "08";
                }
                break;
            case 9:
                if (day >= 1 && day <= 22) {
                    result = "08";
                } else {
                    result = "09";
                }
                break;
            case 10:
                if (day >= 1 && day <= 22) {
                    result = "09";
                } else {
                    result = "10";
                }
                break;
            case 11:
                if (day >= 1 && day <= 21) {
                    result = "10";
                } else {
                    result = "11";
                }
                break;
            case 12:
                if (day >= 1 && day <= 21) {
                    result = "11";
                } else {
                    result = "12";
                }
                break;

        }
        return result;
    }
}
