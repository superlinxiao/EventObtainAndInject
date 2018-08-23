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

  private static final String TAG = "MainActivity";

  private InputManager inputManager;

  private int clickNum = 0;
  private int touchNum = 0;

  @SuppressLint("ClickableViewAccessibility")
  @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    final Button button1 = (Button) findViewById(R.id.button1);
    final Button button2 = (Button) findViewById(R.id.button2);

    inputManager = (InputManager) getSystemService(Context.INPUT_SERVICE);

    button1.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        clickNum = 0;
        touchNum = 0;
        int[] location = new int[2];
        button2.getLocationOnScreen(location);
        int clickX = location[0] + 20;
        int clickY = location[1] + 20;
        Log.d(TAG, "clickX " + clickX + "   clickY" + clickY);

        long downTime = SystemClock.uptimeMillis();
        MotionEvent down = MotionEvent.obtain(downTime, SystemClock.uptimeMillis()+1000, MotionEvent.ACTION_DOWN, clickX, clickY, 1);
        down.setSource(InputDeviceCompat.SOURCE_TOUCHSCREEN);
        injectEvent(down);
        Log.d(TAG, "down downTime " + downTime);


//        MotionEvent up = MotionEvent.obtain(downTime, SystemClock.uptimeMillis() , MotionEvent.ACTION_UP, clickX, clickY, 0);
//        up.setSource(InputDeviceCompat.SOURCE_TOUCHSCREEN);
//        injectEvent(up);

        for (int i = 0; i < 1; i++) {
//        button2.dispatchTouchEvent(down);
//          MotionEvent up = MotionEvent.obtain(downTime, SystemClock.uptimeMillis() + 100, MotionEvent.ACTION_UP, clickX, clickY, 0);
//          up.setSource(InputDeviceCompat.SOURCE_TOUCHSCREEN);

//          if (i == 50) {
//            MotionEvent up = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, clickX + i, clickY, 1);
//            up.setSource(InputDeviceCompat.SOURCE_TOUCHSCREEN);
//            injectEvent(up);
//            Log.d(TAG, "发出UP事件发出UP事件发出UP事件发出UP事件" + i);
//            continue;
//          }

          long downTime1 = SystemClock.uptimeMillis();
          Log.d(TAG, "move downTime " + downTime1);
          MotionEvent move = MotionEvent.obtain(downTime1, SystemClock.uptimeMillis() + 1000, MotionEvent.ACTION_MOVE, clickX + (i * 2), clickY, 1);
          move.setSource(InputDeviceCompat.SOURCE_TOUCHSCREEN);


//        button2.dispatchTouchEvent(up);

          injectEvent(move);
          Log.d(TAG, "发出move事件" + i);

        }
//        inputManager.injectInputEvent(down, 0);
//        inputManager.injectInputEvent(up, 0);
      }

    });


    button2.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Toast.makeText(MainActivity.this, "button2收到点击了........", Toast.LENGTH_LONG).show();
        clickNum++;
      }
    });

    button2.setOnLongClickListener(new View.OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        Toast.makeText(MainActivity.this, "long click", Toast.LENGTH_LONG).show();
        return true;
      }
    });

    button2.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        touchNum++;

        event.getDownTime();
        Log.d(TAG, "收到touch event.getActionMasked():" + event.getActionMasked() + "   " + "touchNum:" + touchNum + " event.getRawX() " + event.getRawX()+"  event.getDownTime();"+event.getDownTime());
        return false;
      }
    });
  }

  @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
  private void injectEvent(MotionEvent down) {
    try {
      Method injectInputEvent = InputManager.class.getMethod("injectInputEvent", InputEvent.class, int.class);
      injectInputEvent.invoke(inputManager, down, 0);
      down.recycle();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
  }


}
