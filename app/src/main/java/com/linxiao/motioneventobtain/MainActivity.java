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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 * 测试小结：
 * 1.eventTime对ACTION_MOVE事件有影响，这个表示ACTION_MOVE事件被触发的时间。但是对DOWN和UP事件没有影响。
 * 2.UP的优先级比MOVE的优先级打，如果在发送MOVE事件的时候（还没有执行MOVE），同时有UP事件被发送，那么
 * 会执行UP事件和最后一个move事件，其他的move事件会被忽略。
 *
 */
public class MainActivity extends AppCompatActivity {

  @SuppressLint("ClickableViewAccessibility")
  @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

}
