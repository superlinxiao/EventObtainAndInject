package com.linxiao.motioneventobtain.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.linxiao.motioneventobtain.event.EventInput;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Description
 * Author lizheng
 * Create Data  2018\9\9 0009
 */
public class EventService extends Service {

  private static final String TAG = "server_ctrl";
  private EventInput input;
  private Handler renderThreadHandler;

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    initInjectLooper();
    initSocketThread();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    return START_STICKY;
  }

  private void test() {
    String dwon = "{\"o\":1,\"a\":0,\"i\":0,\"p\":[{\"x\":\"0.56985395\",\"y\":\"0.55734934\",\"p\":0}]}";
    String up = "{\"o\":1,\"a\":1,\"i\":0,\"p\":[{\"x\":\"0.56985395\",\"y\":\"0.55734934\",\"p\":0}]}";

    Message obtain = Message.obtain();
    obtain.obj = dwon;
    renderThreadHandler.sendMessage(obtain);
  }

  private void initSocketThread() {
    input = new EventInput();
    doNetwork stuff = new doNetwork();
    Thread myNet = new Thread(stuff);
    myNet.start();
  }

  private void initInjectLooper() {
    HandlerThread renderThread = new HandlerThread("event");
    renderThread.start();
    renderThreadHandler = new Handler(renderThread.getLooper()) {
      @Override
      public void handleMessage(Message msg) {
        String json = (String) msg.obj;
        input.sendJson(json);
      }
    };
  }

  class doNetwork implements Runnable {
    public BufferedReader in;

    public void run() {
      while(true){
        try {
          connect();
          Thread.sleep(2000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }

    private void connect() {
      String h = "127.0.0.1";
      try {
        Socket socket = new Socket(InetAddress.getByName(h), 7086);
        //InputStream mInStream = socket.getInputStream();
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        Log.i(TAG, "connected");

        String cmdstr;
        try {
          while (socket.isConnected()) {
            cmdstr = in.readLine();
            if (cmdstr != null) {

              cmdstr = cmdstr.replace("\n", "");
              Message obtain = Message.obtain();
              obtain.obj = cmdstr;
              renderThreadHandler.sendMessage(obtain);
            }
          }
        } catch (Exception e) {
          Log.e(TAG, "injectMotionEvent = " + e.toString());

        } finally {
          in.close();
          socket.close();
        }
      } catch (Exception e) {
        Log.i(TAG, "Unable to connect...\n");
      }
    }
  }
}
