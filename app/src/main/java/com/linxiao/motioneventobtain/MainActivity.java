package com.linxiao.motioneventobtain;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.linxiao.motioneventobtain.event.EventInput;
import com.linxiao.motioneventobtain.service.EventService;

/**
 * 测试小结：
 * 1.bindserver不执行onStartCommand
 * 2.oncreate只执行一次,onbind也只执行一次，在一个activity中多次bind，返回的都是统一binder
 * 3.startservice > oncreate > onStartCommand>stopservice> onDestroy
 * 4.bindservice >onCreate >onBind >all unbind>onDestroy
 */
public class MainActivity extends AppCompatActivity {
  private static final String TAG = "event_input";
  private long start;
  private ServiceConnection conn;
  private Intent service;

  @SuppressLint("ClickableViewAccessibility")
  @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
//
//    findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View v) {
//        Log.e(TAG, "BTN SECOND ONCLICK");
//      }
//    });
    startTestService();
  }

  private void startTestService() {
    startService(new Intent(this, EventService.class));
  }

  /**
   * 测试代码
   *
   * @param view
   */
  public void clickFirst(View view) {
//    TimerTask task = new TimerTask() {
//      @Override
//      public void run() {
//        sendJson();
//      }
//    };
//    Timer timer = new Timer();
//    timer.schedule(task, 0, 5000);
//    sendJson();
    startTestService();
  }

  private void sendJson() {
//    EventInput input = new EventInput();
//    for (int i = 0; i < 100; i++) {
//      injectEvent(input, i / 100f);
//    }
//    injectEvent(input, 0.5f);
  }

  private void injectEvent(EventInput input, float s1) {
    start = System.currentTimeMillis();
    String s = "\"" + s1 + "\"";
//    input.sendJson("{\"o\":1,\"a\":2,\"i\":0,\"p\":[{\"x\":\"0.501557\",\"y\":" + s + ",\"p\":0}]}");
    input.sendJson("{\"o\":1,\"a\":0,\"i\":0,\"p\":[{\"x\":\"0.501557\",\"y\":" + s + ",\"p\":0}]}");
    input.sendJson("{\"o\":1,\"a\":1,\"i\":0,\"p\":[{\"x\":\"0.501557\",\"y\":" + s + ",\"p\":0}]}");
  }

  public void bindService(View view) {
    conn = new ServiceConnection() {
      @Override
      public void onServiceConnected(ComponentName name, IBinder service) {

        EventService.MyBinder myBinder = (EventService.MyBinder) service;
        myBinder.test();
        Log.i(TAG, "onServiceConnected");
      }

      @Override
      public void onServiceDisconnected(ComponentName name) {
        //只有在异常中断的时候，才会调用
        Log.i(TAG, "onServiceDisconnected");
      }
    };
    Intent service = new Intent(this, EventService.class);
    bindService(service, conn, BIND_AUTO_CREATE);

  }

  public void unbindservice(View view) {
    //如果已经解绑过一次，再次调用会报错，Service not registered
    unbindService(conn);
  }

  public void stopService(View view) {
    stopService(service);
//    stopService(new Intent(this, EventService.class));
  }

  public void startService(View view) {
    service = new Intent(this, EventService.class);
    startService(service);
  }
}
