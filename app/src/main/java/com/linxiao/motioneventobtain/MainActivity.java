package com.linxiao.motioneventobtain;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.input.InputManager;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v4.view.InputDeviceCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.InputEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.linxiao.motioneventobtain.event.EventInput;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * 测试小结：
 * 1.eventTime对ACTION_MOVE事件有影响，这个表示ACTION_MOVE事件被触发的时间。但是对DOWN和UP事件没有影响。
 * 2.UP的优先级比MOVE的优先级打，如果在发送MOVE事件的时候（还没有执行MOVE），同时有UP事件被发送，那么
 * 会执行UP事件和最后一个move事件，其他的move事件会被忽略。
 *
 */
public class MainActivity extends AppCompatActivity {

  private static final String TAG = "event_input";
  @SuppressLint("ClickableViewAccessibility")
  @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Log.e(TAG,"BTN SECOND ONCLICK");
      }
    });
  }

  /**
   * 测试代码
   * @param view
   */
  public void clickFirst(View view) {
    TimerTask task = new TimerTask() {
      @Override
      public void run() {
        sendJson();
      }
    };
    Timer timer = new Timer();
    timer.schedule(task,0,5000);
  }

  private void sendJson() {
    EventInput input = new EventInput();
    for (int i = 0; i < 100; i++) {
      injectEvent(input, i/100f);
    }
  }

  private void injectEvent(EventInput input, float s1) {
    String s = "\"" + s1 + "\"";
    input.sendJson("{\"o\":1,\"a\":0,\"i\":0,\"p\":[{\"x\":\"0.503617\",\"y\":" + s + ",\"p\":0}]}");
    input.sendJson("{\"o\":1,\"a\":2,\"i\":0,\"p\":[{\"x\":\"0.501557\",\"y\":" + s + ",\"p\":0}]}");
    input.sendJson("{\"o\":1,\"a\":1,\"i\":0,\"p\":[{\"x\":\"0.501557\",\"y\":" + s + ",\"p\":0}]}");
  }
}
