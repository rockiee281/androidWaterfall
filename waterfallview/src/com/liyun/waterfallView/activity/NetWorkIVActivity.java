package com.liyun.waterfallView.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.liyun.waterfallView.R;
import com.liyun.waterfallView.bean.UrlBean;
/**
 * 使用NetWorkImageView
 * @author chen
 *
 */
public class NetWorkIVActivity extends Activity {
	private ListView listView;
	//需要使用瀑布流或者下拉刷新瀑布流的使用MultiColumnListView或者MultiColumnPullToRefreshListView
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_network_imageview);
		listView = (ListView) findViewById(R.id.listView);
		listView.setAdapter(new NetWorkImageAdapter(this,UrlBean.urls));
	}
}
