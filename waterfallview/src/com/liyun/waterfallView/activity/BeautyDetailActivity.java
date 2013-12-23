package com.liyun.waterfallView.activity;

import static com.liyun.waterfallView.tool.Constants.TAG;

import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response.Listener;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.liyun.waterfallView.R;
import com.liyun.waterfallView.tool.Constants;
import com.liyun.waterfallView.tool.VolleyTool;

public class BeautyDetailActivity extends Activity implements OnClickListener {
	private String originUrl;
	private boolean imgLiked = false;
	private String token = "7D2F87DD6A2032EC226F1CAF9C16BA2B823B6E580810BC6F59D21962266D7265";
	private ImageLoader imageLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.beauty_detail);
		Intent intent = getIntent();

		originUrl = intent.getStringExtra("url");
		if (originUrl == null || originUrl.isEmpty()) {
			finish();
		}
		String fitImgUrl = originUrl;
		try {
			int width = getWindowManager().getDefaultDisplay().getWidth();
			fitImgUrl = transImgUrl(originUrl, width);
		} catch (Exception e) {
			Log.e(TAG, originUrl, e);
		}
		NetworkImageView imageView = (NetworkImageView) findViewById(R.id.img);
		imageView.setDefaultImageResId(android.R.drawable.ic_menu_rotate);
		imageView.setErrorImageResId(android.R.drawable.ic_delete);
		imageLoader = VolleyTool.getInstance(this).getmImageLoader();
		imageView.setImageUrl(fitImgUrl, imageLoader);
		// ImageListener listener = ImageLoader.getImageListener(imageView,
		// android.R.drawable.ic_menu_rotate,
		// android.R.drawable.ic_delete);
		// VolleyTool.getInstance(this).getmImageLoader().get(fitImgUrl,
		// listener);

		ImageView likeView = (ImageView) findViewById(R.id.like);
		likeView.setOnClickListener(BeautyDetailActivity.this);
		isImageLiked(likeView);
		getImageLikeCount((TextView) findViewById(R.id.likeCounter));

	}

	/**
	 * 判断图片是否已经被赞过
	 * */
	private void isImageLiked(final ImageView likeView) {
		String requestUrl = Constants.IMAGE_LIKE_URL + "?img=" + this.originUrl + "&token=" + token + "&action=check";
		StringRequest request = new StringRequest(requestUrl, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				if ("true".equals(response.trim())) {
					likeView.setImageResource(R.drawable.like_128x128);
					imgLiked = true;
				} else {
					likeView.setImageResource(R.drawable.unlike_128x128);
					imgLiked = false;
				}
			}
		}, null);
		VolleyTool.getInstance(this).getmRequestQueue().add(request);
	}

	/**
	 * 获取赞的个数
	 * */
	private void getImageLikeCount(final TextView findViewById) {
		String requestUrl = Constants.IMAGE_COUNTER_URL + "?img=" + originUrl;
		StringRequest request = new StringRequest(requestUrl, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				findViewById.setText(response.trim());
			}
		}, null);

		VolleyTool.getInstance(this).getmRequestQueue().add(request);
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.like:
			final String action = imgLiked ? "remove" : "add";

			String requestUrl = Constants.IMAGE_LIKE_URL + "?img=" + this.originUrl + "&token=" + token + "&action="
					+ action;
			final ImageView imageView = (ImageView) v;
			final TextView counter = (TextView) findViewById(R.id.likeCounter);
			StringRequest request = new StringRequest(requestUrl, new Listener<String>() {

				@Override
				public void onResponse(String response) {
					if ("1".equals(response.trim())) {
						int counterVal = 0;
						try {
							counterVal = Integer.valueOf(counter.getText().toString());
						} catch (Exception e) {
							e.printStackTrace();
						}
						if ("remove".equals(action)) {
							imgLiked = false;
							imageView.setImageResource(R.drawable.unlike_128x128);
							counter.setText(String.valueOf(counterVal - 1));
						} else {
							imageView.setImageResource(R.drawable.like_128x128);
							counter.setText(String.valueOf(counterVal + 1));
							imgLiked = true;
						}
					}
				}
			}, null);
			VolleyTool.getInstance(this).getmRequestQueue().add(request);
			break;

		default:
			break;
		}

	}
}
