package com.linxiao.motioneventobtain.event;

import android.content.Context;
import android.hardware.input.InputManager;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v4.view.InputDeviceCompat;
import android.util.Log;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.linxiao.motioneventobtain.app.MyApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * 自动化事件(touch,按键)
 * Created by LiZhiYang on 2017/4/26 0026.
 */

public class EventInput {
  private static final String TAG = "server_ctrl";
  private static final String TAG_MSG = "ServerControlMsg";
  private InputManager im;

  private InputManager inputManager;

  /**
   * jni 调用入口
   *
   * @param json
   */
  public void sendJson(String json) {
    Log.i("ctrl_net", "recv json: " + json);
    try {
      JSONArray jsonArray = new JSONArray(json);
      for (int i = 0; i < jsonArray.length(); i++) {
        parseAction(jsonArray.getString(i));
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  public EventInput() {
    inputManager = (InputManager) MyApplication.getApplication().getSystemService(Context.INPUT_SERVICE);
  }

  private void injectMotionEvent(List<MyPoint> myPoints, int action, int index, String s) throws InvocationTargetException, IllegalAccessException {
    Log.d(TAG_MSG, "injectMotionEvent");
    MotionEvent.PointerProperties[] propertiesArray = new MotionEvent.PointerProperties[myPoints.size()];
    MotionEvent.PointerCoords[] pointerCoordsArray = new MotionEvent.PointerCoords[myPoints.size()];
    for (int i = 0; i < myPoints.size(); i++) {
      MyPoint myPoint = myPoints.get(i);
      MotionEvent.PointerProperties pointerProperties = new MotionEvent.PointerProperties();
      pointerProperties.id = myPoint.pointerId;
      pointerProperties.toolType = MotionEvent.TOOL_TYPE_FINGER;
      propertiesArray[i] = pointerProperties;
      MotionEvent.PointerCoords pointerCoords = new MotionEvent.PointerCoords();
      pointerCoords.x = myPoint.pointX;
      pointerCoords.y = myPoint.pointY;
      pointerCoords.pressure = 1.0f;
      pointerCoords.size = 1;
      pointerCoordsArray[i] = pointerCoords;
    }
    try {
      if (action == MotionEvent.ACTION_POINTER_DOWN || action == MotionEvent.ACTION_POINTER_UP) {
        MotionEvent event = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis() + 5,
          getPointerAction(action, index), pointerCoordsArray.length, propertiesArray, pointerCoordsArray,
          0, 0, 1.0f, 1.0f, 0, 0, InputDeviceCompat.SOURCE_TOUCHSCREEN, 0);
        event.setSource(InputDeviceCompat.SOURCE_TOUCHSCREEN);
//                injectInputEventMethod.invoke(im, event, 0);
        injectEvent(event);
//        inputManager.injectInputEvent(event, 0);
      } else {
        MotionEvent event = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis() + 5,
          action, pointerCoordsArray.length, propertiesArray, pointerCoordsArray,
          0, 0, 1.0f, 1.0f, 0, 0, InputDeviceCompat.SOURCE_TOUCHSCREEN, 0);
        event.setSource(InputDeviceCompat.SOURCE_TOUCHSCREEN);
//                injectInputEventMethod.invoke(im, event, 0);
//                new KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis() + 100, action, keyCode, 0, metaState,
//                        deviceId, 0, KeyEvent.FLAG_FROM_SYSTEM, InputDevice.SOURCE_KEYBOARD);
        injectEvent(event);
//        inputManager.injectInputEvent(event, 0);
      }
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      Log.e(TAG, "IllegalArgumentException " + action + " " + e.toString());
    } catch (Exception e) {
      e.printStackTrace();
      Log.e(TAG, "Exception " + action + " " + e.toString());
    }
  }

  private int getPointerAction(int motionEvent, int index) {
    return motionEvent + (index << MotionEvent.ACTION_POINTER_INDEX_SHIFT);
  }

  public void parseAction(String s) {
    try {
      JSONObject jsonRoot = new JSONObject(s);

      if (!jsonRoot.has("i")) {
        Log.e(TAG, "s is not control msg = " + s);
        return;
      }

      int action = jsonRoot.optInt("a");
      int index = jsonRoot.optInt("i");
      int orientation = jsonRoot.optInt("o");

      JSONArray jsonArray = jsonRoot.getJSONArray("p");
      List<MyPoint> myPointList = new ArrayList<>();
      for (int j = 0; j < jsonArray.length(); j++) {
        JSONObject pointJsonObj = jsonArray.optJSONObject(j);
        MyPoint myPoint = new MyPoint();
        float scaleX = Float.parseFloat(pointJsonObj.optString("x"));
        float scaleY = Float.parseFloat(pointJsonObj.optString("y"));
        int pointerId = pointJsonObj.optInt("p");

        if (orientation == 1) {//竖屏
          myPoint.pointX = scaleX * 1080;
          myPoint.pointY = scaleY * 1920;
        } else {//横屏或其他按横屏处理
          myPoint.pointX = scaleX * 1920;
          myPoint.pointY = scaleY * 1080;
        }

        myPoint.pointerId = pointerId;
        myPointList.add(myPoint);
      }

      if (myPointList != null || !myPointList.isEmpty()) {
        try {
          injectMotionEvent(myPointList, action, index, s);
        } catch (Exception e) {
          Log.e(TAG, "injectMotionEvent = " + e.toString());
        }
      } else {
        Log.e(TAG, "points is wrong");
      }
    } catch (Exception e) {
      Log.e(TAG, "parseAction = " + e.toString());
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
  private void injectEvent(MotionEvent down) {
    try {
      Log.i("ctrl_net", "recv x " + down.getX()+"  recv y"+down.getY());
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
