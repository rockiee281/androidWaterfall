package com.liyun.waterfallView.activity;

import static com.liyun.waterfallView.tool.Constants.TAG;

import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.liyun.waterfallView.R;
import com.liyun.waterfallView.tool.VolleyTool;

public class BeautyDetailActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.beauty_detail);
		Intent intent = getIntent();

		String url = intent.getStringExtra("url");
		if (url == null || url.isEmpty()) {
			finish();
		}
		String requestUrl = null;
		try {
			int width = getWindowManager().getDefaultDisplay().getWidth();
			requestUrl = transImgUrl(url, width);
		} catch (Exception e) {
			Log.e(TAG, url, e);
		}
		ImageView imageView = (ImageView) findViewById(R.id.img);
		ImageListener listener = ImageLoader.getImageListener(imageView, android.R.drawable.ic_menu_rotate,
				android.R.drawable.ic_delete);
		VolleyTool.getInstance(this).getmImageLoader().get(requestUrl, listener);
	}

	/**
	 * 用于计算缩略图URL
	 * 
	 * @throws MalformedURLException
	 * */
	private String transImgUrl(String originUrl, int itemWidth) throws MalformedURLException {
		URL url = new URL(originUrl);
		return url.getProtocol() + "://" + url.getHost() + "/s/" + url.getPath() + "_" + itemWidth + "x0.jpg";
	}
}
