package com.example.acoustically.http.Http;

import android.os.Message;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

/**
 * Created by acoustically on 17. 10. 25.
 */

public class HttpRequestor {
  private HttpURLConnection mConnection;
  private Exception mBuildException;

  public HttpRequestor(HttpURLConnection mConnection, Exception e) {
    this.mConnection = mConnection;
    this.mBuildException = e;
  }

  public String getUrl() {
    return mConnection.getURL().toString();
  }

  public void get(HttpResponseListener listener) {
    try {
      mConnection.setRequestMethod("GET");
    } catch (Exception e) {
      sendMessage(2, e, listener);
    }
    sendWithoutBody(listener);
  }

  public void post(JSONObject json, HttpResponseListener listener) {
    try {
      mConnection.setRequestMethod("POST");
    } catch (Exception e) {
      sendMessage(2, e, listener);
    }
    sendWithBody(json, listener);
  }

  public void delete(HttpResponseListener listener) {
    try {
      mConnection.setRequestMethod("DELETE");
    } catch (Exception e) {
      sendMessage(2, e, listener);
    }
    sendWithoutBody(listener);
  }

  public void put(JSONObject json, HttpResponseListener listener) {
    try {
      mConnection.setRequestMethod("PUT");
    } catch (Exception e) {
      sendMessage(2, e, listener);
    }
    sendWithBody(json, listener);
  }

  private void sendWithoutBody(final HttpResponseListener listener) {
    if(processBuildException(listener)) {
      sendMessage(2, mBuildException, listener);
      return;
    }
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        try {
          InputStream inputStream = new BufferedInputStream(mConnection.getInputStream());
          try {
            String data = readData(inputStream);
            sendMessage(1, data, listener);
          } catch (Exception e) {
            sendMessage(2, e, listener);
          } finally {
            inputStream.close();
          }
        } catch (Exception e) {
          sendMessage(2, e, listener);
        } finally {
          mConnection.disconnect();
        }
      }
    };
    Thread thread = new Thread(runnable);
    thread.start();
  }

  private void sendWithBody(final JSONObject json, final HttpResponseListener listener) {
    if(processBuildException(listener)) {
      sendMessage(2, mBuildException, listener);
      return;
    }
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        try {
          mConnection.setRequestProperty("Content-type", "application/json");
          mConnection.setDoOutput(true);
          mConnection.setDoInput(true);
          OutputStream outputStream = mConnection.getOutputStream();
          try {
            outputStream.write(json.toString().getBytes("UTF-8"));
          } catch (Exception e) {
            sendMessage(2, e, listener);
          } finally {
            outputStream.close();
          }
          InputStream inputStream = new BufferedInputStream(mConnection.getInputStream());
          try {
            String data = readData(inputStream);
            sendMessage(1, data, listener);
          } catch (Exception e) {
            sendMessage(2, e, listener);
          } finally {
           inputStream.close();
          }
        } catch (Exception e) {
          sendMessage(2, e, listener);
        } finally {
          mConnection.disconnect();
        }
      }
    };
    Thread thread = new Thread(runnable);
    thread.start();
  }
  private void sendMessage(int what, Object object, HttpResponseListener listener) {
    Message message = new Message();
    message.what = what;
    message.obj = object;
    listener.sendMessage(message);
  }

  private String readData(InputStream inputStream) throws Exception {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    while (true) {
      byte[] buffer = new byte[4096];
      int length = inputStream.read(buffer);
      if (length < 1) {
        break;
      }
      outputStream.write(buffer, 0, length);
    }
    String data = new String(outputStream.toByteArray(), "EUC-KR");
    return data;
  }
  private boolean processBuildException(HttpResponseListener listener) {
    if (mBuildException != null) {
      sendMessage(2, mBuildException, listener);
      return true;
    } else {
      return false;
    }
  }
}
