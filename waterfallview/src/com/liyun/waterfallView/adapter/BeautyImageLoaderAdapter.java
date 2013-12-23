package com.liyun.waterfallView.adapter;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.NetworkImageView;
import com.liyun.waterfallView.R;
import com.liyun.waterfallView.activity.BeautyDetailActivity;
import com.liyun.waterfallView.pojo.ImageInfo;
import com.liyun.waterfallView.tool.VolleyTool;

public class BeautyImageLoaderAdapter extends BaseAdapter {
	private Context context;
	private List<ImageInfo> list;
	private int itemWidth = 0;

	public BeautyImageLoaderAdapter(Context ctx, List<ImageInfo> list, int itemWidth) {
		this.context = ctx;
		this.list = list;
		this.itemWidth = itemWidth;

	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public ImageInfo getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final NetWorkAdapterHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.beautyimageloader_item, null);
			holder = new NetWorkAdapterHolder();
			holder.itemImage = (NetworkImageView) convertView.findViewById(R.id.itemImage);
			// holder.itemText = (TextView)
			// convertView.findViewById(R.id.itemText);
			convertView.setTag(holder);
		} else {
			holder = (NetWorkAdapterHolder) convertView.getTag();
		}
		ImageInfo imageInfo = getItem(position);
		final String originUrl = imageInfo.getUrl();
		String requestUlr = "";
		try {
			requestUlr = transImgUrl(originUrl, itemWidth);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		// android.R.drawable.ic_menu_rotate代表默认图
		// android.R.drawable.ic_delete代表加载失败的图
		holder.itemImage.setTag(requestUlr); // 用于标识加载的
		holder.itemImage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(context, BeautyDetailActivity.class);
				i.putExtra("url", originUrl);
				context.startActivity(i);
			}
		});
		ImageLoader imageLoader = VolleyTool.getInstance(context).getmImageLoader();
		// if (!imageLoader.isCached(requestUlr, 0, 0)) { //
		// 如果未缓存情况imageview，避免闪烁
		// holder.itemImage.setImageDrawable(null);
		// } else {
		// holder.itemImage.setDefaultImageResId(android.R.drawable.ic_menu_rotate);
		holder.itemImage.setBackgroundColor(Color.parseColor("#" + imageInfo.getColor()));
		holder.itemImage.setErrorImageResId(android.R.drawable.ic_delete);
		holder.itemImage.setImageUrl(requestUlr, imageLoader);

		// 设置宽高
		LayoutParams params = holder.itemImage.getLayoutParams();
		params.height = imageInfo.getHeight() * itemWidth / imageInfo.getWidth();
		params.width = itemWidth;
		holder.itemImage.setLayoutParams(params);

		// }

		// ImageListener listener = new BeautyImageListener(holder.itemImage,
		// android.R.drawable.ic_menu_rotate,
		// android.R.drawable.ic_delete, requestUlr);
		// imageLoader.get(requestUlr, listener);
		return convertView;
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

	class NetWorkAdapterHolder {
		NetworkImageView itemImage;
		// TextView itemText;
	}

	class BeautyImageListener implements ImageListener {
		private ImageView view;
		private int defaultImageResId;
		private int errorImageResId;
		private String url;

		public BeautyImageListener(ImageView view, int defaultImageResId, int errorImageResId, String url) {
			this.view = view;
			this.defaultImageResId = defaultImageResId;
			this.errorImageResId = errorImageResId;
			this.url = url;
		}

		@Override
		public void onErrorResponse(VolleyError error) {
			if (errorImageResId != 0) {
				view.setImageResource(errorImageResId);
			}
		}

		@Override
		public void onResponse(ImageContainer response, boolean isImmediate) {
			if (view.getTag() == url) { // 判断view是否已被复用，避免当前加载的图片覆盖新图片导致闪烁
				if (response.getBitmap() != null) {
					view.setImageBitmap(response.getBitmap());
				} else if (defaultImageResId != 0) {
					view.setImageResource(defaultImageResId);
				}
			}
		}
	}
}
