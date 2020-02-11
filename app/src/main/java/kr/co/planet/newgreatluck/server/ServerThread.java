package kr.co.planet.newgreatluck.server;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

public class ServerThread extends Thread {
    public interface AfterListener {
        void after(String res);
    }

    public static final String TAG = "TEST_HOME";
    private String address;
    private ArrayList<NameValuePair> p;
    private AfterListener afterListener;


    public ServerThread(AfterListener afterListener) {
        p = new ArrayList<NameValuePair>();
        this.afterListener = afterListener;
    }

    @Override
    public void run() {
        super.run();
        final HttpController hc = new HttpController(address);
        String res = hc.getResultStreamPost(p);
        afterListener.after(res);
    }

    public void setAddress(String url) {
        address = url;
    }


    public void setValue(String name, String value) {
        p.add(new BasicNameValuePair(name, value));
    }
}
