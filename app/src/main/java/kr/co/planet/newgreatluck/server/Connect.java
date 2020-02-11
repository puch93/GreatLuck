package kr.co.planet.newgreatluck.server;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

public class Connect {
    public static final String TAG = "TEST_HOME";
    private ArrayList<NameValuePair> p;



    public Connect() {
        p = new ArrayList<NameValuePair>();
    }



    public String sendToServer(String address) {
        final HttpController hc = new HttpController(address);
        Log.e(TAG, "Put Info: " + p);
        return hc.getResultStreamPost(p);
    }



    public void setValue(String name, String value) {
        p.add(new BasicNameValuePair(name, value));
    }
}
