package kr.co.lylstudio.myapplication.AndroidHttpRequest;

import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by acoustically on 17. 10. 25.
 */

public class HttpRequestorBuilder {
  private String mUrl;
  private Map<String, String> mHeaders;
  public static TrustManager TRUST_MANAGER = new X509TrustManager() {

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
      return null;
    }
  };


  public HttpRequestorBuilder(String mUrl) {
    this.mUrl = mUrl;
    mHeaders = new HashMap<>();
  }

  public HttpRequestorBuilder addParams(String key, String value) {
    if (mUrl.indexOf('?') == -1) {
      mUrl += '?';
    } else {
      mUrl += "&";
    }
    mUrl += key + "=" + value;
    return this;
  }

  public HttpRequestorBuilder addHeaders(String key, String value) {
    mHeaders.put(key, value);
    return this;
  }

  public HttpRequestor build() {
    try {
      URL url = new URL(mUrl);
      SSLContext context = SSLContext.getInstance("TLS");
      context.init(null, new TrustManager[] {TRUST_MANAGER}, null);
      HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
      HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(){
        public boolean verify(String hostname, SSLSession session) {
          return true;
        }});
      HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
      for(String key : mHeaders.keySet()) {
        connection.setRequestProperty(key, mHeaders.get(key));
      }
      HttpRequestor requestor = new HttpRequestor(connection, null);
      return requestor;
    } catch (Exception e) {
      HttpRequestor requestor = new HttpRequestor(null, e);
      return requestor;
    }
  }

  public String getUrl() {
    return mUrl;
  }
}
