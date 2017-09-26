package com.zmb.updemo;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity {
	private Button mBtnUpload;
	private ProgressBar mPgBar;
	private TextView mTvProgress;
	private MyTask mTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mBtnUpload = (Button)findViewById(R.id.btn_upload);
		mBtnUpload.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//���ﻻ����Ҫ�ϴ����ļ�·��
				String filePath = "/mnt/sdcard/DCIM/fairytail_falls.jpg";
				//�����IPҪ�������������IP,����ʹ��localhost,���򽫱�ģ������Ϊ������
				String url = "http://10.203.6.5:8080/UploadServlet/servlet/UploadServlet";
				//�����view���ϴ����ȵĵ���
				View upView = getLayoutInflater().inflate(R.layout.filebrowser_uploading, null);
				mPgBar = (ProgressBar)upView.findViewById(R.id.pb_filebrowser_uploading);
				mTvProgress = (TextView)upView.findViewById(R.id.tv_filebrowser_uploading);
				new AlertDialog.Builder(MainActivity.this).setTitle("�ϴ�����").setView(upView).create().show();
				//AsyncTask��ʵ��
				mTask = new MyTask();
				mTask.execute(filePath, url);
			}
		});
	}

	private class MyTask extends AsyncTask<String, Integer, String>{

		@Override
		protected void onPostExecute(String result) {
			mTvProgress.setText(result);	
		}

		@Override
		protected void onPreExecute() {
			mTvProgress.setText("loading...");
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			mPgBar.setProgress(values[0]);
			mTvProgress.setText("loading..." + values[0] + "%");
		}

		@Override
		protected String doInBackground(String... params) {
			String filePath = params[0];
			String uploadUrl = params[1];
			String end = "\r\n";
			String twoHyphens = "--";
			String boundary = "******";
			try {
				URL url = new URL(uploadUrl);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url
						.openConnection();
				httpURLConnection.setDoInput(true);
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setUseCaches(false);
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setConnectTimeout(6*1000);
				httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
				httpURLConnection.setRequestProperty("Charset", "UTF-8");
				httpURLConnection.setRequestProperty("Content-Type",
						"multipart/form-data;boundary=" + boundary);

				DataOutputStream dos = new DataOutputStream(httpURLConnection
						.getOutputStream());
				dos.writeBytes(twoHyphens + boundary + end);
				dos.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\""
								+ filePath.substring(filePath.lastIndexOf("/") + 1)
								+ "\"" + end);
				dos.writeBytes(end);

				FileInputStream fis = new FileInputStream(filePath);
				long total = fis.available();
				String totalstr = String.valueOf(total);
				Log.d("�ļ���С", totalstr);
				byte[] buffer = new byte[8192]; // 8k
				int count = 0;
				int length = 0;
				while ((count = fis.read(buffer)) != -1) {
					dos.write(buffer, 0, count);
					length += count;
					publishProgress((int) ((length / (float) total) * 100));
					//Ϊ����ʾ����,����500����
					//Thread.sleep(500);
				}			
				fis.close();
				dos.writeBytes(end);
				dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
				dos.flush();

				InputStream is = httpURLConnection.getInputStream();
				InputStreamReader isr = new InputStreamReader(is, "utf-8");
				BufferedReader br = new BufferedReader(isr);
				@SuppressWarnings("unused")
				String result = br.readLine();
				dos.close();
				is.close();
				return "�ϴ��ɹ�";
		}catch (Exception e) {
			e.printStackTrace();
			return "�ϴ�ʧ��";
		}	
	}
}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
