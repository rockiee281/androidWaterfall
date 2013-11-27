package com.liyun.waterfallView.activity;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.liyun.waterfallView.R;
import com.liyun.waterfallView.Application;
import com.liyun.waterfallView.adapter.MainAdapter;
import com.liyun.waterfallView.bean.ListItemBean;

public class MainActivity extends Activity implements OnItemClickListener{
	private ListView mainList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainList = (ListView) findViewById(R.id.mainList);
        Application app = (Application) getApplication();
        List<ListItemBean> items = app.getItems();
        mainList.setAdapter(new MainAdapter(this,items));
        mainList.setOnItemClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		ListItemBean item = (ListItemBean) parent.getItemAtPosition(position);
		if(null != item.getIntent()) {
			startActivity(item.getIntent());
		}
	}

}
