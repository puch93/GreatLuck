package kr.co.planet.newgreatluck.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

//git 테스트 중입니다 
//git 두번째 테스트 입니다

public class BasicAct extends AppCompatActivity {
    public static final String TAG = "TEST_HOME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
    }


    /* 공유하기 (운세글) */
    public void share(String contents) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.setPackage("com.kakao.talk");

        intent.putExtra(Intent.EXTRA_TEXT, contents + "자신의 운세 확인해보기\n" + "https://play.google.com/store/apps/details?id=kr.co.planet.newgreatluck");
        startActivity(Intent.createChooser(intent, "운세 공유하기"));
    }

    /* 공유하기 (앱링크) */
    public void shareAppLink() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.setPackage("com.kakao.talk");
        intent.putExtra(Intent.EXTRA_TEXT, "운세는 기본, 나만의 특별한 부적까지 만나 보세요~!\n오늘의운세 평생 무료 제공.\n" +"https://play.google.com/store/apps/details?id=kr.co.planet.newgreatluck");
        startActivity(Intent.createChooser(intent, "앱 공유하기"));
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
}
