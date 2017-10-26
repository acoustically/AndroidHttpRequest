package AndroidHttpRequest;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by acoustically on 17. 10. 25.
 */

public class HttpRequestorBuilder {
  private String mUrl;
  private Map<String, String> mHeaders;

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
      HttpURLConnection connection = (HttpURLConnection)url.openConnection();
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
