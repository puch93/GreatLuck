package kr.co.planet.newgreatluck.fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;

public class BasicFrag extends Fragment {
    public static final String TAG = "TEST_HOME";

    /* 공유하기 */
    public void share() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
//        intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=kr.co.chat.hitalk");
        intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=kr.co.planet.newgreatluck");
        startActivity(Intent.createChooser(intent, "앱 추천하기"));
    }
}
