package com.liyun.waterfallView.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.Response.Listener;
import com.android.volley.toolbox.ImageRequest;
import com.liyun.waterfallView.R;
import com.liyun.waterfallView.bean.UrlBean;
import com.liyun.waterfallView.tool.VolleyTool;
import com.liyun.waterfallView.widget.MyScrollView;

/**
 * 瀑布流  
 * @author chen
 *
 */
public class WaterfallActivity extends Activity {
	private static final int column = 3;//3列
	private static final int pageCount = 12; //每页加载个数
	private int currentPage = 0; //当前页
	private int columnWidth = 0;//列宽
	private LinearLayout mianContainer;//主容器
	private List<LinearLayout> columnLayouts = new ArrayList<LinearLayout>();
	private long lastid = 0l;	// 上次请求到的图片时间戳
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	lastid = getLastId();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waterfall);
        mianContainer = (LinearLayout) findViewById(R.id.mianContainer);
        int width = getWindowManager().getDefaultDisplay().getWidth();
        columnWidth = (width - 4)/ 3 ;//4为中间2条空隙 
        ((MyScrollView)findViewById(R.id.scrollView)).setScrollCallBack(new MyScrollCallBack());
        addColumn();
    }
    
    private long getLastId() {
    	SharedPreferences p =  this.getSharedPreferences("beauty_lastid", Context.MODE_PRIVATE);
    	return p.getLong("lastid", 0);
	}
    
    private void setLastId(Long lastid){
    	this.getSharedPreferences("beauty_lastid", Context.MODE_PRIVATE).edit().putLong("lastid", lastid).commit();
    }

	/**
     * 滚动回调
     */
    class MyScrollCallBack implements MyScrollView.ScrollCallBack {

		@Override
		public void onTop() {
			
		}

		@Override
		public void onBottom() {
			currentPage++;
			addImageView2Column();
		}

		@Override
		public void onScroll() {
			
		}
    	
    }

    /**
     * 构造列
     */
    private void addColumn() {
    	for(int i = 0;i < column;i++) {//构造列
    		LinearLayout columnLayout = new LinearLayout(this);
    		columnLayout.setLayoutParams(new LayoutParams(columnWidth, LayoutParams.MATCH_PARENT));
    		columnLayout.setOrientation(LinearLayout.VERTICAL);
    		columnLayouts.add(columnLayout);
    		mianContainer.addView(columnLayout);
    	}
    	
    	addImageView2Column();
	}
    
    /**
     * 构造完后开始加入imageView到列中
     */
	private void addImageView2Column() {
		//网上抄的方法,目前没发现什么BUG
		int columnIndex = 0;
		int imageCount = UrlBean.urls.length;
		for(int i = currentPage * pageCount;i< (currentPage +1)*pageCount && i < imageCount;i++) {
			columnIndex = columnIndex >= column ? columnIndex = 0 : columnIndex;
			ImageView itemImage = new ImageView(this);
			itemImage.setLayoutParams(new LayoutParams(columnWidth,LayoutParams.WRAP_CONTENT));
			itemImage.setPadding(2, 2, 2, 2);
			columnLayouts.get(columnIndex).addView(itemImage);
			downloadImage(itemImage,i);
			columnIndex++;
		}
	}

	
	/**
	 * 下载图片，自带缓存
	 * @param itemImage
	 * @param index
	 */
	private void downloadImage(final ImageView itemImage, int index) {
		//columnWidth这个是设置下载图片的maxWidth,0代表不限定
		ImageRequest request = new ImageRequest(UrlBean.urls[index], new Listener<Bitmap>() {
			@Override
			public void onResponse(Bitmap response) {
				itemImage.setImageBitmap(response);
			}
		}, columnWidth, 0, Config.RGB_565, null);
		request.setTag(WaterfallActivity.class.getSimpleName());
		request.setShouldCache(true);//设置缓存 缓存路径看我以前的帖子
		//下面注释掉的代码是缓存到sdcard中的，需要打开mainfest中的权限文件写权限
//		File sdDir = Environment.getExternalStorageDirectory();//获取跟目录 
//		File file = new File(sdDir,"chen");
//		DiskBasedCache cache = new DiskBasedCache(file, 20*1024*1024);
//		Network network = new BasicNetwork(new HurlStack());
//		queue = new RequestQueue(cache, network);
//		queue.add(request);
//		queue.start();
		VolleyTool.getInstance(this).getmRequestQueue().add(request);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		VolleyTool.getInstance(this).getmRequestQueue().cancelAll(WaterfallActivity.class.getSimpleName());
	}
}
