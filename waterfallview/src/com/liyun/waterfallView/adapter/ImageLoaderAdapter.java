package com.liyun.waterfallView.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.liyun.waterfallView.R;
import com.liyun.waterfallView.tool.VolleyTool;

public class ImageLoaderAdapter extends BaseAdapter {
	private Context context;
	private List<String> list;
	public ImageLoaderAdapter(Context ctx, List<String> list) {
		this.context = ctx;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public String getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		NetWorkAdapterHolder holder;
		if(convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.imageloader_item,null);
			holder = new NetWorkAdapterHolder();
			holder.itemImage = (ImageView) convertView.findViewById(R.id.itemImage);
			holder.itemText = (TextView) convertView.findViewById(R.id.itemText);
			convertView.setTag(holder);
		} else {
			holder = (NetWorkAdapterHolder) convertView.getTag();
		}
		
		String url = getItem(position);
		ImageLoader imageLoader = VolleyTool.getInstance(context).getmImageLoader();
		if (!imageLoader.isCached(url, 0, 0)){	// 如果未缓存情况imageview，避免闪烁
			holder.itemImage.setImageDrawable(null);
		}
		
		//android.R.drawable.ic_menu_rotate代表默认图 
		//android.R.drawable.ic_delete代表加载失败的图
		ImageListener listener = ImageLoader.getImageListener(holder.itemImage, 
				android.R.drawable.ic_menu_rotate, android.R.drawable.ic_delete);
		imageLoader.get(url, listener);
		holder.itemText.setText(url.substring(url.length() - 8));
		return convertView;
	}
	
	class NetWorkAdapterHolder {
		ImageView itemImage;
		TextView itemText;
	}

}
