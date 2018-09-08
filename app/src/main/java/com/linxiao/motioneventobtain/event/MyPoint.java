package com.linxiao.motioneventobtain.event;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * ---------------------------------------------------------------
 * Author: ZhaoYidong
 * Create: 2017/6/27 20:12
 * ---------------------------------------------------------------
 * Describe:
 * ---------------------------------------------------------------
 * Changes:
 * ---------------------------------------------------------------
 * 2017/6/27 20 : Create by ZhaoYidong
 * ---------------------------------------------------------------
 */
public class MyPoint {
  public float pointX;
  public float pointY;
  public int pointerId;

  public JSONObject getJsonObject() {
    try {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("pointX", pointX + "");
      jsonObject.put("pointY", pointY + "");
      jsonObject.put("pointerId", pointerId);
      return jsonObject;
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return null;
  }
}
