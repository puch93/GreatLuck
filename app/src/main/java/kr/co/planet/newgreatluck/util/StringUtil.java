package kr.co.planet.newgreatluck.util;

import org.json.JSONObject;

import java.text.DecimalFormat;

public class StringUtil {
    public static boolean isNull(String str){
        if(str == null || str.length() == 0 || str.equals("null")){
            return true;
        }else{
            return false;
        }
    }

    public static String setNumComma(int price){
        DecimalFormat format = new DecimalFormat("###,###");
        return format.format(price);
    }

    public static String getStr(JSONObject jo, String key) {
        String s = null;
        try {
            s = jo.getString(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }
}
