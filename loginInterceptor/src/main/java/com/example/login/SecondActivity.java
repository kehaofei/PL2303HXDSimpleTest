package com.example.login;

import com.example.loginin.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class SecondActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_second);
		// 接收从首页传递过来的消息
		Bundle b = getIntent().getExtras();
		Toast.makeText(this, b.getString("Type"), 300).show();
	}

	/**
	 * 模拟退出登录
	 * 
	 * @param v
	 */
	public void exitSign(View v) {
		MainActivity.is_login = false;
		finish();
	}
}
