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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Description
 * Author lizheng
 * Create Data  2018\9\9 0009
 */
public class EventService extends Service {

  private static final String TAG = "event_input";
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
    public void run() {
      while (true) {
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
      Socket socket = null;
      BufferedReader in = null;
      try {
        socket = new Socket(InetAddress.getByName(h), 7086);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String json = in.readLine();
        while (json != null) {
          json = json.replace("\n", "");
          Message obtain = Message.obtain();
          obtain.obj = json;
          renderThreadHandler.sendMessage(obtain);
          json = in.readLine();
        }
      } catch (Exception e) {
        Log.e(TAG, "Unable to connect...\n");
      } finally {
        try {
          if (in != null) {
            in.close();
          }
          if (socket != null) {
            socket.close();
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
