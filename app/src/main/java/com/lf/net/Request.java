package com.lf.net;

import org.json.JSONObject;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.lf.utils.LogUtils;
import com.lf.utils.StringUtils;

/**
 * 项目名称：LogStatisticLib
 * 类名称：Request
 * 类描述： 请求对象
 * 创建人：libai
 * 创建时间：2014-12-18 下午9:17:54
 * 修改人：libai
 * 修改时间：2014-12-18 下午9:17:54
 * 修改备注：
 * 
 * @version
 * 
 */
public class Request {

	private static final String TAG = "Request";
	
	private static final String URL = "Request";
	
	private static RequestQueue mQueue = null;

	/**
	 * @description:用于发起网络请求
	 */
	public static boolean post(final Context context, final String data, final NetWorkCallback callback) {
		// 判断数据
		if (context == null) {
			return false;
		}
		try {
			// 创建request对象
			JSONObject obj = new JSONObject(data);
			LogUtils.i(TAG, "param content:" + obj.toString());
			JsonObjectRequest req = new JsonObjectRequest(URL, obj , new Response.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject jsonobj) {
					LogUtils.d(TAG,jsonobj.toString());
					try {
						if (callback != null) {
							// 获取数据为空
							if (jsonobj == null || StringUtils.isEmpty(jsonobj.toString())) {
								callback.onError();
								return;
							}

							// 检查标志
							if (jsonobj.getInt("r") == 1 && !StringUtils.isEmpty(jsonobj.getString("d"))) {
							    LogUtils.i(TAG,"onResponse success：" + jsonobj.toString());
								callback.onSuccess(jsonobj.getString("d"));
							}
							else {
								callback.onError();
								if(!StringUtils.isEmpty(jsonobj.getString("d")))
								LogUtils.w("Request", "Error data:" + jsonobj.getString("d"));
							}
						}
					}
					catch (Exception e) {
						e.printStackTrace();
						if (callback != null) {
							callback.onError();
						}
					}
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					LogUtils.e("Request", "onErrorResponse:" + error.getMessage());
					LogUtils.w("Request", "data:" + data);
					if (callback != null) {
						callback.onError();
					}
				}
			});
			
			// 判断队列是否为空
			if (mQueue == null) {
				mQueue = Volley.newRequestQueue(context);
			}
			// 发起执行
			mQueue.add(req);
		}
		catch (Exception e) {
			e.printStackTrace();
			if (callback != null) {
				callback.onError();
			}
			return false;
		}
		return true;
	}

}
