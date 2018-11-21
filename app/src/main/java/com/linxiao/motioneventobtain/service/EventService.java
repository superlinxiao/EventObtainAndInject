package com.linxiao.motioneventobtain.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
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

  public static final String TAG = "event_input";
  private EventInput input;
  private Handler renderThreadHandler;

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    Log.i(TAG, "onBind");
    return new MyBinder();
  }

  public class MyBinder extends Binder {

    public void test() {
      Log.i(TAG, "test data");
    }
  }

  @Override
  public void onCreate() {
    super.onCreate();
    Log.i(TAG, "onCreate");
//    initInjectLooper();
//    initSocketThread();
  }


  /**
   * START_STICKY：表示Service运行的进程被Android系统强制杀掉之后，Android系统会将该Service依然设置为started状态（即运行状态），但是不再保存onStartCommand方法传入的intent对象，然后Android系统会尝试再次重新创建该Service，并执行onStartCommand回调方法，这时onStartCommand回调方法的Intent参数为null，也就是onStartCommand方法虽然会执行但是获取不到intent信息。
   * <p>
   * 使用场景：如果你的Service可以在任意时刻运行或结束都没什么问题，而且不需要intent信息，那么就可以在onStartCommand方法中返回START_STICKY，比如一个用来播放背景音乐功能的Service就适合返回该值。
   * <p>
   * START_REDELIVER_INTENT：表示Service运行的进程被Android系统强制杀掉之后，与返回START_STICKY的情况类似，Android系统会将再次重新创建该Service，并执行onStartCommand回调方法，但是不同的是，Android系统会再次将Service在被杀掉之前最后一次传入onStartCommand方法中的Intent再次保留下来并再次传入到重新创建后的Service的onStartCommand方法中，这样我们就能读取到intent参数。
   * <p>
   * 使用场景：如果我们的Service需要依赖具体的Intent才能运行（需要从Intent中读取相关数据信息等），并且在强制销毁后有必要重新创建运行，那么这样的Service就适合返回START_REDELIVER_INTENT。
   *
   * @return START_STICKY  START_REDELIVER_INTENT
   */
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.i(TAG, "onStartCommand");
    return START_STICKY;
  }


  @Override
  public boolean onUnbind(Intent intent) {
    Log.i(TAG, "onUnbind");
    return super.onUnbind(intent);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    Log.i(TAG, "onDestroy");
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
          Log.e(TAG, "connect retry");
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
        if (socket.isConnected()) {
          Log.e(TAG, "connect success");
        }
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String json = in.readLine();
        while (json != null) {
          json = json.replace("\n", "");
          Message obtain = Message.obtain();
          obtain.obj = json;
          renderThreadHandler.sendMessage(obtain);
          json = in.readLine();
        }
        Log.e(TAG, "read null and disconnect");
      } catch (Exception e) {
        e.printStackTrace();
        Log.e(TAG, "error:" + e.getMessage());
      } finally {
        try {
          if (in != null) {
            in.close();
          }
          if (socket != null) {
            socket.close();
          }
        } catch (IOException e) {
          Log.e(TAG, "error:" + e.getMessage());
          e.printStackTrace();
        }
      }
    }
  }
}
