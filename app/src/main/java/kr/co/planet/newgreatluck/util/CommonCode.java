package kr.co.planet.newgreatluck.util;

import android.content.Context;

import java.util.ArrayList;

import kr.co.planet.newgreatluck.R;
import kr.co.planet.newgreatluck.data.CharmData;

public class CommonCode {
    public static final String RECOMMEND = "recommend";
    public static final String SHARE = "share";



    /* profile */
    public static final String GENDER_MALE = "1";
    public static final String GENDER_FEMALE = "2";
    public static final String CALENDER_01 = "01";
    public static final String CALENDER_02 = "02";
    public static final String MOON_01 = "01";
    public static final String MOON_02 = "02";



    /* unse contents, stars */
    //menu01
    public static final String STAR_TODAY = "today_star";
    public static final String STAR_TOJUNG = "tojung_star";
    public static final String STAR_WEEK = "week_star";
    public static final String STAR_MONTH = "month_star";
    public static final String STAR_SAJU = "saju_star";

    public static final String CONTENTS_TODAY = "today_contents";
    public static final String CONTENTS_TOJUNG = "tojung_contents";
    public static final String CONTENTS_WEEK = "week_contents";
    public static final String CONTENTS_MONTH = "month_contents";
    public static final String CONTENTS_SAJU = "saju_contents";

    /* dream data */
    //taemong
    public static final String STAR_TAEMONG = "taemong_star";

    public static final String CONTENTS_TAEMONG = "taemong_contents";

    //menu02
    public static final String STAR_TODAY_OTHER = "today_other_star";
    public static final String STAR_COMPATIBILITY = "compatibility_star";
    public static final String STAR_TODAY_LOVE = "today_love_star";
    public static final String STAR_DATE = "date_star";
    public static final String STAR_HUMER = "humer_star";
    public static final String STAR_BLOOD = "blood_star";
    public static final String STAR_HUMER_OTHER = "humer_other_star";

    public static final String CONTENTS_TODAY_OTHER = "today_other_contents";
    public static final String CONTENTS_COMPATIBILITY = "compatibility_contents";
    public static final String CONTENTS_TODAY_LOVE = "today_love_contents";
    public static final String CONTENTS_DATE = "date_contents";
    public static final String CONTENTS_HUMER = "humer_contents";
    public static final String CONTENTS_BLOOD = "blood_contents";
    public static final String CONTENTS_HUMER_OTHER = "humer_other_contents";


    //menu03
    public static final String STAR_TODAY_ZODIAC = "today_zodiac_star";
    public static final String STAR_HEART = "heart_star";
    public static final String STAR_WEEK_ZODIAC = "week_zodiac_star";
    public static final String STAR_HEART_ZODIAC = "heart_zodiac_star";

    public static final String CONTENTS_TODAY_ZODIAC = "today_zodiac_contents";
    public static final String CONTENTS_HEART = "heart_contents";
    public static final String CONTENTS_WEEK_ZODIAC = "week_zodiac_contents";
    public static final String CONTENTS_HEART_ZODIAC = "heart_zodiac_contents";



    //menu04
    public static final String STAR_TODAY_CONSTELLATION = "today_constellation_star";
    public static final String STAR_HEART2 = "heart2_star";
    public static final String STAR_WEEK_CONSTELLATION = "week_constellation_star";
    public static final String STAR_HEART_CONSTELLATION = "heart_constellation_star";

    public static final String CONTENTS_TODAY_CONSTELLATION = "today_constellation_contents";
    public static final String CONTENTS_HEART2 = "heart2_contents";
    public static final String CONTENTS_WEEK_CONSTELLATION = "week_constellation_contents";
    public static final String CONTENTS_HEART_CONSTELLATION = "heart_constellation_contents";


    /* server parameter */
    public static final String PARAM_SERVER = "siteUrl";
    public static final String PARAM_GROUP = "group";
    public static final String PARAM_UNIQ = "m_uniq";

    public static final String PARAM_USER_YEAR = "user1_year";
    public static final String PARAM_USER_MONTH = "user1_month";
    public static final String PARAM_USER_DAY = "user1_day";
    public static final String PARAM_USER_HOUR = "user1_hour";
    public static final String PARAM_USER_SOL = "user1_sol";
    public static final String PARAM_USER_GENDER = "user1_sex";
    public static final String PARAM_USER_BLOOD = "user1_blood";

    public static final String PARAM_USER_YEAR_OTHER = "user2_year";
    public static final String PARAM_USER_MONTH_OTHER = "user2_month";
    public static final String PARAM_USER_DAY_OTHER = "user2_day";
    public static final String PARAM_USER_HOUR_OTHER = "user2_hour";
    public static final String PARAM_USER_SOL_OTHER = "user2_sol";
    public static final String PARAM_USER_GENDER_OTHER = "user2_sex";
    public static final String PARAM_USER_BLOOD_OTHER = "user2_blood";




    public static final String PARAM_FORTUNE_YEAR = "target1_year";
    public static final String PARAM_FORTUNE_MONTH = "target1_month";
    public static final String PARAM_FORTUNE_DAY = "target1_day";

    public static final String PARAM_FORTUNE_YEAR_OTHER = "target2_year";
    public static final String PARAM_FORTUNE_MONTH_OTHER = "target2_month";
    public static final String PARAM_FORTUNE_DAY_OTHER = "target2_day";




    public static final String PARAM_ZODIAC_DATA = "user_12gi1";
    public static final String PARAM_ZODIAC_DATA_OTHER = "user_12gi2";


    public static final String PARAM_CONSTELLATION_DATA = "user_zodiac1";
    public static final String PARAM_CONSTELLATION_DATA_OTHER = "user_zodiac2";


    public static final String PARAM_UNSE = "unse";

    public static final String PARAM_TODAY_UNSE = "today_unse";
    public static final String PARAM_TOJUNG_UNSE = "tojong";
    public static final String PARAM_WEEK_UNSE = "jugan_unse";
    public static final String PARAM_MONTH_UNSE = "month_unse";
    public static final String PARAM_SAJU_UNSE = "life_all";




    public static final String PARAM_TODAY_OTHER_UNSE = "today_date_unse";
    public static final String PARAM_COMPATIBILITY_OTHER_UNSE = "gunghap";
    public static final String PARAM_TODAYLOVE_OTHER_UNSE = "today_love_unse";
    public static final String PARAM_DATE_OTHER_UNSE = "firstdate";
    public static final String PARAM_HUMER_OTHER_UNSE = "yupgi_dosa";
    public static final String PARAM_HUMER_OTHER_WOMEN_UNSE = "yupgiunse";
    public static final String PARAM_BLOOD_OTHER_UNSE = "blood_blood";

    public static final String PARAM_TODAY_ZODIAC_UNSE = "12gi_today";
    public static final String PARAM_HEART_UNSE = "12gi_love";
    public static final String PARAM_WEEK_ZODIAC_UNSE = "12gi_week";
    public static final String PARAM_HEART_ZODIAC_UNSE = "12gi_gunghap";

    public static final String PARAM_LUCK_TYPE = "luck_type";



    public static final String PARAM_TODAY_CONSTELLATION_UNSE = "zodiac_today";
    public static final String PARAM_HEART2_UNSE = "zodiac_love";
    public static final String PARAM_WEEK_CONSTELLATION_UNSE = "zodiac_week";
    public static final String PARAM_HEART_CONSTELLATION_UNSE = "zodiac_gunghap";



    //dream server data
    public static final String PARAM_DREAM_KIND = "dumm";
    public static final String PARAM_DREAM_UNSE = "dream";

    public static ArrayList<CharmData> getCharmList01 (Context ctx){
        ArrayList<CharmData> list = new ArrayList<>();
        list.add(new CharmData("sub_charm02_01", "귀인 부적", ctx.getString(R.string.charm_ads01_01)));
        list.add(new CharmData("sub_charm02_07", "상충살 부적", ctx.getString(R.string.charm_ads01_02)));
        list.add(new CharmData("sub_charm02_12", "인덕 부적", ctx.getString(R.string.charm_ads01_03)));

        return list;
    }


    public static ArrayList<CharmData> getCharmList02 (Context ctx){
        ArrayList<CharmData> list = new ArrayList<>();
        list.add(new CharmData("sub_charm02_03", "만사대길 부적", ctx.getString(R.string.charm_ads02_01)));
        list.add(new CharmData("sub_charm03_01", "관재 부적", ctx.getString(R.string.charm_ads02_02)));
        list.add(new CharmData("sub_charm04_06", "백살 부적", ctx.getString(R.string.charm_ads02_03)));

        return list;
    }

    public static ArrayList<CharmData> getCharmList03 (Context ctx){
        ArrayList<CharmData> list = new ArrayList<>();
        list.add(new CharmData("sub_charm04_12", "인연 부적", ctx.getString(R.string.charm_ads03_01)));
        list.add(new CharmData("sub_charm01_03", "바람방지 부적", ctx.getString(R.string.charm_ads03_02)));
        list.add(new CharmData("sub_charm01_07", "원진살제거 부적", ctx.getString(R.string.charm_ads03_03)));

        return list;
    }

    public static ArrayList<CharmData> getCharmList04 (Context ctx){
        ArrayList<CharmData> list = new ArrayList<>();
        list.add(new CharmData("sub_charm01_05", "사랑 부적", ctx.getString(R.string.charm_ads04_01)));
        list.add(new CharmData("sub_charm01_02", "남자떼는 부적", ctx.getString(R.string.charm_ads04_02)));
        list.add(new CharmData("sub_charm01_06", "여자떼는 부적", ctx.getString(R.string.charm_ads04_03)));

        return list;
    }

    public static ArrayList<CharmData> getCharmList05 (Context ctx){
        ArrayList<CharmData> list = new ArrayList<>();
        list.add(new CharmData("sub_charm04_18", "재수 부적", ctx.getString(R.string.charm_ads05_01)));
        list.add(new CharmData("sub_charm04_13", "잡귀 부적", ctx.getString(R.string.charm_ads05_02)));
        list.add(new CharmData("sub_charm04_10", "우환소멸 부적", ctx.getString(R.string.charm_ads05_03)));

        return list;
    }

    public static ArrayList<CharmData> getCharmList06 (Context ctx){
        ArrayList<CharmData> list = new ArrayList<>();
        list.add(new CharmData("sub_charm04_08", "소원성취 부적", ctx.getString(R.string.charm_ads06_01)));
        list.add(new CharmData("sub_charm02_03", "만사대길 부적", ctx.getString(R.string.charm_ads06_02)));
        list.add(new CharmData("sub_charm03_06", "압살 부적", ctx.getString(R.string.charm_ads06_03)));

        return list;
    }
}
