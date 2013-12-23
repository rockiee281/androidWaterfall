package com.liyun.waterfallView.activity;

import static com.liyun.waterfallView.tool.Constants.TAG;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.huewu.pla.lib.MultiColumnListView;
import com.huewu.pla.lib.internal.PLA_AbsListView;
import com.huewu.pla.lib.internal.PLA_AbsListView.OnScrollListener;
import com.liyun.waterfallView.R;
import com.liyun.waterfallView.adapter.BeautyImageLoaderAdapter;
import com.liyun.waterfallView.pojo.ImageInfo;
import com.liyun.waterfallView.tool.VolleyTool;

/**
 * 使用的开源的瀑布流 地址：https://github.com/youxiachai/pinterest-like-adapter-view
 * 有下拉刷新的功能的话 可以使用MultiColumnPullToRefreshListView
 * 
 * @author chen
 * 
 */
public class ImageLoaderActivity extends Activity implements OnScrollListener {
	private MultiColumnListView listView;
	private List<ImageInfo> urlList = new LinkedList<ImageInfo>();
	private long lastid = 0l; // 上次请求到的图片时间戳
	public final static String BASE_URL = "http://cloud.dakele.com/api/gamecenter/l1/beauty_v2?limit=10&offset=0";
	public final static String RAN_URL = "http://cloud.dakele.com/api/gamecenter/l1/beautyran?limit=10";
	private boolean loadRandom = false; // 是否适用随机图片
	private final LatestImageLoader lasetLoader = new LatestImageLoader();
	private final RandomImageLoader ranLoader = new RandomImageLoader();
	private BeautyImageLoaderAdapter adpater = null;
	private int itemWidth = 216;
	private boolean isLoading = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imageloader);
		// int width = getWindowManager().getDefaultDisplay().getWidth();
		// itemWidth = (width - 4) / 3; // 随便写了个20 限定大小，不限定也可
		adpater = new BeautyImageLoaderAdapter(this, urlList, itemWidth);

		listView = (MultiColumnListView) findViewById(R.id.listView);
		listView.setAdapter(adpater);
		listView.setOnScrollListener(this);

		lastid = getLastId();
		String url = BASE_URL + "&lasttime=" + (lastid + 1);
		// String url = BASE_URL + "&lasttime=" + (new Date().getTime() + 1);

		// A JSONObject to post with the request. Null is allowed and indicates
		// no parameters will be posted along with request.
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, lasetLoader,
				lasetLoader);
		jsonObjectRequest.setTag(ImageLoaderActivity.class.getSimpleName());// 设置tag
																			// cancelAll的时候使用
		VolleyTool.getInstance(this).getmRequestQueue().add(jsonObjectRequest);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		VolleyTool.getInstance(this).getmRequestQueue().cancelAll(ImageLoaderActivity.class.getSimpleName());
	}

	private long getLastId() {
		SharedPreferences p = this.getSharedPreferences("beauty_lastid", Context.MODE_PRIVATE);
		return p.getLong("lastid", 0);
	}

	private void setLastId(Long lastid) {
		this.getSharedPreferences("beauty_lastid", Context.MODE_PRIVATE).edit().putLong("lastid", lastid).commit();
	}

	@Override
	public void onScrollStateChanged(PLA_AbsListView view, int scrollState) {
		switch (scrollState) {
		// 当不滚动时
		case OnScrollListener.SCROLL_STATE_IDLE:
			// 判断滚动到底部
			if (view.getLastVisiblePosition() == (view.getCount() - 1) && !isLoading) {
				//
				if (loadRandom) {
					JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, RAN_URL, null,
							ranLoader, ranLoader);
					jsonObjectRequest.setTag(ImageLoaderActivity.class.getSimpleName());// 设置tag
																						// cancelAll的时候使用
					VolleyTool.getInstance(this).getmRequestQueue().add(jsonObjectRequest);
				} else {
					String url = BASE_URL + "&lasttime=" + (lastid + 1);
					JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
							lasetLoader, lasetLoader);
					jsonObjectRequest.setTag(ImageLoaderActivity.class.getSimpleName());// 设置tag
																						// cancelAll的时候使用
					VolleyTool.getInstance(this).getmRequestQueue().add(jsonObjectRequest);
				}
				isLoading = true;
			}
			break;
		}

	}

	@Override
	public void onScroll(PLA_AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

	}

	/**
	 * 用于加载最新图片
	 * */
	class LatestImageLoader implements Listener<JSONObject>, ErrorListener {

		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e(TAG, error.toString());
			isLoading = false;
		}

		@Override
		public void onResponse(JSONObject rsp) {
			JSONArray data = null;
			try {
				data = rsp.getJSONArray("data");
				lastid = rsp.getLong("ts");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			int len = 0;
			if (data == null || (len = data.length()) == 0) {
				// 没有最新数据，开始加载随机图片
				loadRandom = true;
				JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, RAN_URL, null,
						ranLoader, ranLoader);
				jsonObjectRequest.setTag(ImageLoaderActivity.class.getSimpleName());// 设置tag
																					// callAll的时候使用
				VolleyTool.getInstance(ImageLoaderActivity.this).getmRequestQueue().add(jsonObjectRequest);
				return;
			}

			// 更新listview
			for (int i = 0; i < len; i++) {
				try {
					JSONObject jsonObject = (JSONObject) data.get(i);
					ImageInfo imageInfo = new ImageInfo();
					imageInfo.setId((Integer) jsonObject.get("id"));
					imageInfo.setColor((String) jsonObject.get("backgroud"));
					imageInfo.setUrl((String) jsonObject.get("url"));
					imageInfo.setWidth((Integer) jsonObject.get("width"));
					imageInfo.setHeight((Integer) jsonObject.get("height"));
					urlList.add(imageInfo);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			adpater.notifyDataSetChanged();
			setLastId(lastid);
			isLoading = false;
		}

	}

	/**
	 * 用于加载随机图片
	 * */
	class RandomImageLoader implements Listener<JSONObject>, ErrorListener {

		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e(TAG, error.toString());
			isLoading = false;
		}

		@Override
		public void onResponse(JSONObject rsp) {
			JSONArray data = null;
			try {
				data = rsp.getJSONArray("data");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			int len = 0;
			if (data == null) {
				return;
			}
			len = data.length();
			if (len == 0) {
				return;
			}

			// 更新listview
			for (int i = 0; i < len; i++) {
				try {
					// TODO
					// urlList.add(data.getString(i));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			adpater.notifyDataSetChanged();
			isLoading = false;
		}
	}

}
