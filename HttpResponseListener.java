package com.example.acoustically.http.Http;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * Created by acoustically on 17. 10. 25.
 */
public abstract class HttpResponseListener extends Handler {
  protected abstract void httpResponse(String data);
  protected abstract void httpExcepted(Exception e);

  public HttpResponseListener() {
    super(Looper.getMainLooper());
  }

  @Override
  public void handleMessage(Message msg) {
    if(msg.what==1) {
      httpResponse((String)msg.obj);
    } else {
      httpExcepted((Exception)msg.obj);
    }
    super.handleMessage(msg);
  }
}
