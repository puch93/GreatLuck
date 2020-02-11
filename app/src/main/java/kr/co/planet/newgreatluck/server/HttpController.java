package kr.co.planet.newgreatluck.server;

import android.content.Context;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class HttpController {
	private FileInputStream mFileInputStream;
	URL connectUrl;
	String lineEnd = "\r\n";
	String twoHyphens = "--";
	String boundary = "*****";
	String url;
	// Handler handler = new Handler();

	Context mCtx;
	HttpClient client = null;
	HttpParams params;

	public HttpController(Context ctx, String url) {
		this.url = url;
		this.mCtx = ctx;
	}

	public HttpController(String url) {
		this.url = url;
	}

	public HttpController(Context ctx) {
		this.mCtx = ctx;
	}

	public ArrayList<NameValuePair> getNameValuePairList(NameValuePair... nameValuePairs) {
		ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
		for(int i = 0; i < nameValuePairs.length; i++) {
			list.add(nameValuePairs[i]);
		}
		return list;
	}

	public NameValuePair getNameValuePair(String key, String content) {
		return new BasicNameValuePair(key, content);
	}

	public void setURL(String url) {
		this.url = url;
	}

	public void connHTTP() throws Exception {
		HttpParams pr = new BasicHttpParams();
		pr.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		client = new DefaultHttpClient(pr);
//		client = new DefaultHttpClient();
		params = client.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 20000);
		HttpConnectionParams.setSoTimeout(params, 20000);


	}

	public String outputStream(InputStream is) {
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		try {
			br = new BufferedReader(new InputStreamReader(is, "EUC-KR"));
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(br != null) {
				try {
					is.close();
				} catch(Exception e2) {
					
				}
				try {
					br.close();
				} catch(Exception e2) {
					
				}
			}
		}

		return sb.toString();
	}

	public String getResultStreamPost(ArrayList<NameValuePair> nameValuePairs) {
		try {
			connHTTP();
			HttpPost post = new HttpPost(url);
			if(nameValuePairs != null)
				post.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));// HTTP.UTF_8));
			HttpResponse response = client.execute(post);
			HttpEntity entity = response.getEntity();
			String s = EntityUtils.toString(entity, "EUC-KR");
			return s;
		} catch(IOException ie) {
			ie.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		} catch(OutOfMemoryError ooe) {
			ooe.printStackTrace();
		} finally {
			if(client != null) {
				
			}
		}

		return null;
	}

	public String getResultStreamGet(ArrayList<NameValuePair> p) {
		BufferedReader br = null;
		StringBuilder sb = null;
		String s = null;
		try {
			connHTTP();
			// HttpClient client = new DefaultHttpClient();
			String pa = "";
			if(p != null) {
				for (int i = 0; i < p.size(); i++) {
					if (i == 0)
						pa += "?";
					else
						pa += "&";
					pa += p.get(i).getName();
					pa += "=";
					pa += URLEncoder.encode(p.get(i).getValue(), "UTF-8");
				}
			}
			HttpGet get = new HttpGet(url + pa);
			HttpResponse resp = client.execute(get);
			HttpEntity entity = resp.getEntity();
			s = EntityUtils.toString(entity, "EUC-KR");
			//
			// while((str = br.readLine()) != null){
			// sb.append(str).append("\n");
			// }
			// br.close();

		} catch(Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public String getResultUploadFile(ArrayList<NameValuePair> nameValuePairs, HashMap<String, File> paramKeyFile) {
		// Boolean isSuccessed = false;
		Properties proper = new Properties();
		try {
			// client = HttpClientFactory.getThreadSafeClient();
			connHTTP();

			HttpPost post = new HttpPost(url);

			Charset chars = Charset.forName("UTF-8");
			MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

			// Set keySet = paramMap.keySet();
			// Iterator<String> iter = keySet.iterator();
			// while(iter.hasNext()){
			// String key = iter.next();
			// StringBody sb = new StringBody(paramMap.get(key),chars);
			// reqEntity.addPart(key, sb);
			// }
			for(int i = 0; i < nameValuePairs.size(); i++) {
				String key = nameValuePairs.get(i).getName();
				String value = nameValuePairs.get(i).getValue();
				if (value == null)
					value = "";
				StringBody sb = new StringBody(value, chars);
				reqEntity.addPart(key, sb);
			}

			if(paramKeyFile != null) {
				// Log.i("LOSA","paramKeyFile!=null");
				Set fileSet = paramKeyFile.keySet();
				Iterator<String> fileIter = fileSet.iterator();

				while (fileIter.hasNext()) {
					String fileKey = fileIter.next();
					File f = paramKeyFile.get(fileKey);
					// Log.i("LOSA","fileKey="+fileKey);
					if(f != null) {
						// Log.i("LOSA","fileBody add!");
						FileBody bin = new FileBody(f);
						reqEntity.addPart(fileKey, bin);
					}
				}
			}
			post.setEntity(reqEntity);
			HttpResponse response = client.execute(post);
			Log.i("TEST","code: "+response.getStatusLine().getStatusCode());
			HttpEntity entity = response.getEntity();
			String s = EntityUtils.toString(entity, "EUC-KR");

			return s;// entity.getContent();

		} catch(Exception e) {
			e.printStackTrace();
			// handler.post(new Runnable(){
			// public void run() {
			// Toast.makeText(mCtx,"��Ʈ��ũ ȯ���� ���� �ʽ��ϴ�.\n���߿� �ٽ� Ȯ�����ּ���",
			// Toast.LENGTH_SHORT).show();
			// }
			// });
		} finally {
			if(client != null) {
				// client.getConnectionManager().shutdown();
			}
		}

		return null;
	}

	public InputStream doPostUpload(Map<String, String> paramMap, String[] filePaths) {
		// Boolean isSuccessed = false;
		Properties proper = new Properties();
		try {
			// client = HttpClientFactory.getThreadSafeClient();
			connHTTP();

			HttpPost post = new HttpPost(url);

			Charset chars = Charset.forName("UTF-8");
			MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

			Set keySet = paramMap.keySet();
			Iterator<String> iter = keySet.iterator();
			while(iter.hasNext()) {
				String key = iter.next();
				StringBody sb = new StringBody(paramMap.get(key), chars);
				reqEntity.addPart(key, sb);
			}

			if(filePaths != null) {
				for(int i = 0; i < filePaths.length; i++) {
					String filePath = filePaths[i];
					if(filePath != null && !filePath.trim().equals("") && !filePath.equals("null")) {
						File f = new File(filePaths[i]);
						FileBody bin = new FileBody(f);
						reqEntity.addPart("m_file", bin);
					}
				}
			}
			post.setEntity(reqEntity);
			HttpResponse response = client.execute(post);
			HttpEntity entity = response.getEntity();
			return entity.getContent();

		} catch(Exception e) {
			// e.printStackTrace();
			// handler.post(new Runnable(){
			// public void run() {
			// Toast.makeText(mCtx,"��Ʈ��ũ ȯ���� ���� �ʽ��ϴ�.\n���߿� �ٽ� Ȯ�����ּ���",
			// Toast.LENGTH_SHORT).show();
			// }
			// });
		} finally {
			if(client != null) {
				// client.getConnectionManager().shutdown();
			}
		}

		return null;
	}

}

class HttpClientFactory {
	private static DefaultHttpClient client;

	public synchronized static DefaultHttpClient getThreadSafeClient() throws Exception {
		if(client != null)
			return client;

		client = new DefaultHttpClient();

		ClientConnectionManager mgr = client.getConnectionManager();

		HttpParams params = client.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 20000);
		HttpConnectionParams.setSoTimeout(params, 20000);
		client = new DefaultHttpClient(new ThreadSafeClientConnManager(params, mgr.getSchemeRegistry()), params);

		return client;
	}
}
