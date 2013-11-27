package com.liyun.waterfallView;

import com.liyun.waterfallView.tool.VolleyTool;

/**
 * 目前没什么可做
 * 
 * @author chen
 * 
 */
public class Application extends android.app.Application {

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onTerminate() {
		VolleyTool.getInstance(this).release();
		super.onTerminate();
	}

}
