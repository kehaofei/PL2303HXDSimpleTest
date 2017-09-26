package com.example.login;

import com.example.loginin.R;
import com.example.tool.LoginInterceptor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * 这个activity主要是做了一个登录成功后，还是回到此页面做一些登录成功的页面更新
 * 
 * @author bzl
 *
 */
public class ThirdActivity extends Activity {
	private TextView text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_third);
		text = (TextView) findViewById(R.id.textView1);
	}

	/**
	 * 当你登录成功接收到你传递出去的标志后可以做一些当前页面更新操作
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		text.setText("终于登录成功  " + intent.getExtras().getString("intentMyself"));
	}

	/**
	 * 在这里点击登录后还是跳转到这个页面，当然传值的话会在onNewIntent方法中回传回来
	 * 
	 * @param v
	 */
	public void login(View v) {
		Bundle bun = new Bundle();
		bun.putString("intentMyself", "可以进行收藏的操作了");
		LoginInterceptor.interceptor(this, "com.example.logininterceptor.ThirdActivity", bun);
	}

}
