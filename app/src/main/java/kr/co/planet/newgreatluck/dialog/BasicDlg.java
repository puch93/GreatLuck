package kr.co.planet.newgreatluck.dialog;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import java.util.HashMap;

public class BasicDlg extends AppCompatActivity {
    public static final String TAG = "TEST_HOME";
    public HashMap<String, String> result_data = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
    }

    public void dialogData(String data){
        Intent dataIntent = new Intent();

        if(null != data) {
            dataIntent.putExtra("dialog_result", data);
            setResult(Activity.RESULT_OK, dataIntent);
        } else {
            setResult(Activity.RESULT_CANCELED);
        }
        finish();
    }

    public void dialogResult() {
        Intent dataIntent = new Intent();

        for (String key : result_data.keySet()) {
            String value = result_data.get(key);
            dataIntent.putExtra(key, value);
        }

//        Iterator<String> iter = result_data.keySet().iterator();
//
//        while(iter.hasNext()) {
//            String key = iter.next();
//            String value = result_data.get(key);
//            dataIntent.putExtra(key, value);
//        }

        setResult(Activity.RESULT_OK, dataIntent);
        finish();
    }

    public void setResultData(String key, String value) {
        result_data.put(key, value);
        Log.e(TAG, "key, value: (" + key + ", " + value + ")");
    }

    /* 평점 (플레이스토어 이동) */
    public void cheer() {
//        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        final String appPackageName = "kr.co.planet.newgreatluck"; // getPackageName() from Context or Activity object
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
            intent.setPackage("com.android.vending");
            startActivity(intent);

//            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName));
            intent.setPackage("com.android.vending");
            startActivity(intent);

//            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    /* 공유하기 (앱링크) */
    public void shareAppLink() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.setPackage("com.kakao.talk");
        intent.putExtra(Intent.EXTRA_TEXT, "운세는 기본, 나만의 특별한 부적까지 만나 보세요~!\n오늘의운세 평생 무료 제공.\n" +"https://play.google.com/store/apps/details?id=kr.co.planet.newgreatluck");
        startActivity(Intent.createChooser(intent, "앱 공유하기"));
    }
}
