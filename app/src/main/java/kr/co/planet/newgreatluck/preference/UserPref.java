package kr.co.planet.newgreatluck.preference;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import kr.co.planet.newgreatluck.data.CharmAdsManagerData;
import kr.co.planet.newgreatluck.data.CharmManagerData;
import kr.co.planet.newgreatluck.data.InfoData;

public class UserPref {
    //myinfo
    public static void saveMyInfo(Context ctx, InfoData value) {
        Gson gson = new GsonBuilder().create();
        String strGson = gson.toJson(value);

        SharedPreferences pref = ctx.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("myinfo", strGson);
        editor.commit();
    }

    public static InfoData getMyInfo(Context ctx) {
        Gson gson = new GsonBuilder().create();

        SharedPreferences pref = ctx.getSharedPreferences("user", Context.MODE_PRIVATE);
        String strGson = pref.getString("myinfo", null);
        InfoData value = gson.fromJson(strGson, InfoData.class);
        return value;
    }

    //otherinfo
    public static void saveOtherInfo(Context ctx, InfoData value) {
        Gson gson = new GsonBuilder().create();
        String strGson = gson.toJson(value);

        SharedPreferences pref = ctx.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("otherinfo", strGson);
        editor.commit();
    }

    public static InfoData getOtherInfo(Context ctx) {
        Gson gson = new GsonBuilder().create();

        SharedPreferences pref = ctx.getSharedPreferences("user", Context.MODE_PRIVATE);
        String strGson = pref.getString("otherinfo", null);
        InfoData value = gson.fromJson(strGson, InfoData.class);
        return value;
    }


    //nick
    public static void saveName(Context ctx, String value) {
        SharedPreferences pref = ctx.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("name", value);
        editor.commit();
    }

    public static String getName(Context ctx) {
        SharedPreferences pref = ctx.getSharedPreferences("user", Context.MODE_PRIVATE);
        return pref.getString("name", null);
    }


    //midx
    public static void saveMidx(Context ctx, String value) {
        SharedPreferences pref = ctx.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("midx", value);
        editor.commit();
    }

    public static String getMidx(Context ctx) {
        SharedPreferences pref = ctx.getSharedPreferences("user", Context.MODE_PRIVATE);
        return pref.getString("midx", null);
    }


    //fcm
    public static void saveFcmToken(Context ctx, String value) {
        SharedPreferences pref = ctx.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("fcm", value);
        editor.commit();
    }

    public static String getFcmToken(Context ctx) {
        SharedPreferences pref = ctx.getSharedPreferences("user", Context.MODE_PRIVATE);
        return pref.getString("fcm", null);
    }


    //uniq (device id)
    public static void saveDeviceId(Context ctx, String value) {
        SharedPreferences pref = ctx.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("uniq", value);
        editor.commit();
    }

    public static String getDeviceId(Context ctx) {
        SharedPreferences pref = ctx.getSharedPreferences("user", Context.MODE_PRIVATE);
        return pref.getString("uniq", null);

    }

    //cheer state
    public static void saveCheerState(Context ctx, boolean value) {
        SharedPreferences pref = ctx.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("cheer", value);
        editor.commit();
    }

    public static boolean getCheerState(Context ctx) {
        SharedPreferences pref = ctx.getSharedPreferences("user", Context.MODE_PRIVATE);
        return pref.getBoolean("cheer", false);
    }



    //phone number
    public static void savePhoneNum(Context ctx, String value) {
        SharedPreferences pref = ctx.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("phone", value);
        editor.commit();
    }

    public static String getPhoneNum(Context ctx) {
        SharedPreferences pref = ctx.getSharedPreferences("user", Context.MODE_PRIVATE);
        return pref.getString("phone", null);
    }


    //phons sim operator
    public static void saveSimOperator(Context ctx, String value) {
        SharedPreferences pref = ctx.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("sim", value);
        editor.commit();
    }

    public static String getSimOperator(Context ctx) {
        SharedPreferences pref = ctx.getSharedPreferences("user", Context.MODE_PRIVATE);
        return pref.getString("sim", null);
    }


    //alarm
    public static void saveAlarmState(Context ctx, boolean value) {
        SharedPreferences pref = ctx.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("alarm", value);
        editor.commit();
    }

    public static boolean getAlarmState(Context ctx) {
        SharedPreferences pref = ctx.getSharedPreferences("user", Context.MODE_PRIVATE);
        return pref.getBoolean("alarm", true);
    }

    //contents 광고 카운트
   public static void saveAdsCount(Context ctx, int value) {
        SharedPreferences pref = ctx.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("ads", value);
        editor.commit();
    }

    public static int getAdsCount(Context ctx) {
        SharedPreferences pref = ctx.getSharedPreferences("user", Context.MODE_PRIVATE);
        return pref.getInt("ads", 1);
    }




    /* contents */
    //star
    public static void saveStarData(Context ctx, String key, ArrayList<Integer> value) {
        SharedPreferences prefs = ctx.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray a = new JSONArray();
        for (int i = 0; i < value.size(); i++) {
            a.put(value.get(i));
        }
        if (!value.isEmpty()) {
            editor.putString(key, a.toString());
        } else {
            editor.putString(key, null);
        }
        editor.commit();
    }

    public static ArrayList<Integer> getStarData(Context ctx, String key) {
        SharedPreferences prefs = ctx.getSharedPreferences("user", Context.MODE_PRIVATE);
        String json = prefs.getString(key, null);
        ArrayList<Integer> stars = new ArrayList<Integer>();

        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++) {
                    Integer s = a.optInt(i);
                    stars.add(s);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return stars;
    }


    //star
    public static void saveContentsData(Context ctx, String key, ArrayList<String> value) {
        SharedPreferences prefs = ctx.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray a = new JSONArray();
        for (int i = 0; i < value.size(); i++) {
            a.put(value.get(i));
        }
        if (!value.isEmpty()) {
            editor.putString(key, a.toString());
        } else {
            editor.putString(key, null);
        }
        editor.commit();
    }

    public static ArrayList<String> getContentsData(Context ctx, String key) {
        SharedPreferences prefs = ctx.getSharedPreferences("user", Context.MODE_PRIVATE);
        String json = prefs.getString(key, null);
        ArrayList<String> stars = new ArrayList<String>();

        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++) {
                    String s = a.optString(i);
                    stars.add(s);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return stars;
        } else {
            return null;
        }
    }




    /* charm data */
    public static void saveCharmData(Context ctx, CharmManagerData value) {
        Gson gson = new GsonBuilder().create();
        String strGson = gson.toJson(value);

        SharedPreferences pref = ctx.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("charmlist", strGson);
        editor.commit();
    }

    public static CharmManagerData getCharmData(Context ctx) {
        Gson gson = new GsonBuilder().create();

        SharedPreferences pref = ctx.getSharedPreferences("user", Context.MODE_PRIVATE);
        String strGson = pref.getString("charmlist", null);
        CharmManagerData value = gson.fromJson(strGson, CharmManagerData.class);
        return value;
    }


    /* char ads data */
    public static void saveCharmAdsData(Context ctx, CharmAdsManagerData value) {
        Gson gson = new GsonBuilder().create();
        String strGson = gson.toJson(value);

        SharedPreferences pref = ctx.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("charmAdsList", strGson);
        editor.commit();
    }

    public static CharmAdsManagerData getCharmAdsData(Context ctx) {
        Gson gson = new GsonBuilder().create();

        SharedPreferences pref = ctx.getSharedPreferences("user", Context.MODE_PRIVATE);
        String strGson = pref.getString("charmAdsList", null);
        CharmAdsManagerData value = gson.fromJson(strGson, CharmAdsManagerData.class);
        return value;
    }

    /* MesauredViewPager */
    //midx
    public static void saveViewPagerHeight(Context ctx, int value) {
        SharedPreferences pref = ctx.getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("height", value);
        editor.commit();
    }

    public static int getViewPagerHeight(Context ctx) {
        SharedPreferences pref = ctx.getSharedPreferences("user", Context.MODE_PRIVATE);
        return pref.getInt("height", 0);
    }
}