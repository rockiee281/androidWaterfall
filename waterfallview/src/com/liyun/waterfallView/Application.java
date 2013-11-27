package com.liyun.waterfallView;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;

import com.liyun.waterfallView.activity.ImageLoaderActivity;
import com.liyun.waterfallView.activity.ImageRequestActivity;
import com.liyun.waterfallView.activity.JsonRequestAcitivity;
import com.liyun.waterfallView.activity.NetWorkIVActivity;
import com.liyun.waterfallView.activity.WaterfallActivity;
import com.liyun.waterfallView.bean.ListItemBean;
import com.liyun.waterfallView.tool.VolleyTool;


/**
 * 目前没什么可做
 * @author chen
 *
 */
public class Application extends android.app.Application {
	private List<ListItemBean> items = new ArrayList<ListItemBean>();
	@Override
	public void onCreate() {
		super.onCreate();
		initMainItems();
	}

	private void initMainItems() {
		items.add(new ListItemBean("WaterFall",new Intent(this,WaterfallActivity.class)));
		items.add(new ListItemBean("ImageRequest",new Intent(this,ImageRequestActivity.class)));
		items.add(new ListItemBean("ImageLoader",new Intent(this,ImageLoaderActivity.class)));
		items.add(new ListItemBean("JsonRequest",new Intent(this,JsonRequestAcitivity.class)));
		items.add(new ListItemBean("NetworkImageView",new Intent(this,NetWorkIVActivity.class)));
	}
	
	public List<ListItemBean> getItems() {
		return items;
	}

	@Override
	public void onTerminate() {
		VolleyTool.getInstance(this).release();
		super.onTerminate();
	}
	
	
}
