package com.fxxc.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.security.PublicKey;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.TunnelRefusedException;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.kobjects.base64.Base64;

import com.example.fxxcpower.R;
import com.fxxc.net.NetConnect;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.transition.Visibility;
import android.util.Base64DataException;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class WelcomeActivity extends Activity {
	private final static int CWJ_HEAP_SIZE = 6 * 1024 * 1024;
	private String version;
	String versionsString;
	private TextView tv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		tv = (TextView) findViewById(R.id.activity_welcome_loading);
		// Class<?> cls = Class.forName("dalvik.system.VMRuntime");
		// Method getRuntime = cls.getMethod("getRuntime");
		// Object obj = getRuntime.invoke(null);
		//
		// obj.getClass().getRuntime().setMinimumHeapSize(CWJ_HEAP_SIZE);

		// if (getversion()) {
		//
		// } else {
		//
		// }
		// Request(null);
		// test();
		// getversion();
		Handler handler = new Handler();
		handler.postDelayed(gotoMainAct, 3000);

	}

	Runnable gotoMainAct = new Runnable() {
		@Override
		public void run() {
			startActivity(new Intent(WelcomeActivity.this, PointMeter.class));
			finish();
		}
	};

}
