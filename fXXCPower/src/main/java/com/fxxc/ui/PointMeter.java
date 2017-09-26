package com.fxxc.ui;

import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.fxxcpower.R;
import com.fxxc.net.NetConnect;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class PointMeter extends Activity implements OnClickListener {
	private TimePickerDialog timePickerDialog;
	private DatePickerDialog datePickerDialog;

	private EditText concentratorid;
	private Button concentrator_id;
	private EditText meterid;
	private Button meter_id;
	private EditText starttime;
	private Button start_time;
	private EditText endtime;
	private Button end_time;
	private Button getalarm;
	private Button setpointmeter;
	private Button getVersion;
	private RadioButton open;
	private RadioButton close;
	private int id;
	private String opt;
	private String state;
	private TextView version;

	private String taskCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pointmeter);
		initview();
		Intent tinIntent=new Intent();
	}

	public void initview() {
		version = (TextView) findViewById(R.id.activity_pointmeter_version_text);
		concentratorid = (EditText) findViewById(R.id.activity_pointmeter_concentrator_id_edittext);
		concentrator_id = (Button) findViewById(R.id.activity_pointmeter_concentrator_id_button);
		meterid = (EditText) findViewById(R.id.activity_pointmeter_meter_id_edittext);
		meter_id = (Button) findViewById(R.id.activity_pointmeter_meter_id_button);
		starttime = (EditText) findViewById(R.id.activity_pointmeter_starttime_edittext);
		start_time = (Button) findViewById(R.id.activity_pointmeter_starttime_button);
		endtime = (EditText) findViewById(R.id.activity_pointmeter_endtime_edittext);
		end_time = (Button) findViewById(R.id.activity_pointmeter_endtime_button);
		getalarm = (Button) findViewById(R.id.activity_pointmeter_getalarm);
		setpointmeter = (Button) findViewById(R.id.activity_pointmeter_setpointmeter);
		open = (RadioButton) findViewById(R.id.activity_pointmeter_on);
		close = (RadioButton) findViewById(R.id.activity_pointmeter_off);
		getVersion = (Button) findViewById(R.id.activity_pointmeter_getversion);

		concentrator_id.setOnClickListener(this);
		meter_id.setOnClickListener(this);
		start_time.setOnClickListener(this);
		end_time.setOnClickListener(this);
		getalarm.setOnClickListener(this);
		setpointmeter.setOnClickListener(this);
		getVersion.setOnClickListener(this);

	}

//	private String CheckMeterNo(){
//		String meterNo = meterid.getText().toString().trim();
//		if(meterNo.length() <= 12){
//			meterNo = "000000000000".substring(0, 12-meterNo.length()) + meterNo;
//		}
//		return meterNo;
//		
//	}
	
	public void setTime() {
		Date date = new Date();
		timePickerDialog = new TimePickerDialog(PointMeter.this,
				new OnTimeSetListener() {

					@Override
					public void onTimeSet(TimePicker view, int hourOfDay,
							int minute) {
						if (id == 2) {
							starttime.setText(hourOfDay + ":" + minute + ":00");
							id = 0;
						} else if (id == 1) {
							endtime.setText(hourOfDay + ":" + minute + ":00");
						}

					}
				}, date.getHours(), date.getMinutes(), true);
		timePickerDialog.show();
		// datePickerDialog = new DatePickerDialog(PointMeter.this,
		// new OnDateSetListener() {
		//
		// @Override
		// public void onDateSet(DatePicker view, int year,
		// int monthOfYear, int dayOfMonth) {
		// // TODO Auto-generated method stub
		//
		// }
		// }, 2015, 12, 30);
		// datePickerDialog.show();
	}

	public void getVersion() {
		ExecRunable.execRun(new Runnable() {

			@Override
			public void run() {
				String result = new NetConnect().ReadMeterVersionByCode(
						Integer.valueOf(concentratorid.getText().toString()), 
						meterid.getText().toString());

				if (result.equals("10")) {
					handler.sendEmptyMessage(10);
				} else if (result.equals("-1")) {
					handler.sendEmptyMessage(-1);
				} else if (result.equals("9")) {
					handler.sendEmptyMessage(9);
				} else if (result.equals("29")) {
					handler.sendEmptyMessage(29);
				} else {
					JSONObject object = null;
					try {
						object = new JSONObject(result);
					} catch (JSONException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					if (object != null) {
						JSONArray array = null;
						try {
							array = object.getJSONArray("results");
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						if (array.length() > 0) {
							try {
								taskCode = array.getJSONObject(0).getString(
										"Jobid");
								Message message = new Message();
								message.what = 12;
								handler.sendMessageDelayed(message, 8000);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					}

				}
			}
		});
	}

	public void GetTaskStatus() {
		ExecRunable.execRun(new Runnable() {
			@Override
			public void run() {
				String result = new NetConnect().GetTaskStatus(taskCode);
				if (result != null) {
					JSONObject object = null;
					try {
						object = new JSONObject(result);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					JSONObject object2 = null;
					try {
						object2 = object.getJSONObject("results");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					JSONArray array = null;
					try {
						array = object2.getJSONArray("ShowResult");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (array.length() > 0) {
						String object3 = null;
						try {
							object3 = array.getString(1);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						handler.obtainMessage(11, object3).sendToTarget();
					} else if (result != null) {
						handler.obtainMessage(11, result).sendToTarget();
					}
				}
			}
		});

	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
//			case -1:
//				Toast.makeText(PointMeter.this,
//						getResources().getString(R.string.warn_1),
//						Toast.LENGTH_LONG).show();
//				break;
			case 0:
				Toast.makeText(PointMeter.this, "OPEN", Toast.LENGTH_LONG)
						.show();
				break;
			case 1:
				Toast.makeText(PointMeter.this, "CLOSE", Toast.LENGTH_LONG)
						.show();
				break;
			case 2:
				Toast.makeText(PointMeter.this, "SUCCESS", Toast.LENGTH_LONG)
						.show();

				break;
			case 3:
				Toast.makeText(PointMeter.this, "FAIL", Toast.LENGTH_LONG)
						.show();
				break;
			case 4:
				Toast.makeText(PointMeter.this, msg.obj.toString(),
						Toast.LENGTH_LONG).show();
				break;
//
			case 9:
				Toast.makeText(PointMeter.this,
						"错误代码-9",
						Toast.LENGTH_LONG).show();
				break;
//			case 29:
//				Toast.makeText(PointMeter.this,
//						getResources().getString(R.string.warn29),
//						Toast.LENGTH_LONG).show();
//				break;
//			case 10:
//				Toast.makeText(PointMeter.this,
//						getResources().getString(R.string.warn10),
//						Toast.LENGTH_LONG).show();
//				break;
			case 11:
				version.setText(msg.obj.toString());
				break;
			case 12:
				GetTaskStatus();
				break;

			}
		}
	};

	public void getAlarm() {
		ExecRunable.execRun(new Runnable() {
			@Override
			public void run() {
				String result = new NetConnect().getAlarmBox(concentratorid
						.getText().toString());
				if (result != null) {
					JSONObject object = null;
					try {
						object = new JSONObject(result);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						System.out.println(result);
					}
					if (object != null) {
						JSONArray array = null;
						try {
							array = object.getJSONArray("data");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						JSONObject object2 = null;
						try {
							if(array.length()>0)
							object2 = array.getJSONObject(0);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						String state = null;
						try {
							if(object2!=null)
							state = object2.getString("STATE");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if(state==null){
							handler.sendEmptyMessage(0);
						}else if (state.equals("1")) {
							handler.sendEmptyMessage(1);
						} else {
							handler.sendEmptyMessage(0);
						}
					}
				}

			}
		});
	}

	public void setPointMeter() {
		ExecRunable.execRun(new Runnable() {
			@Override
			public void run() {
				String result = new NetConnect().SetMeterParams(concentratorid
						.getText().toString(), meterid.getText().toString(),
						state, starttime.getText().toString(), endtime
								.getText().toString(), opt);
				if (result != null) {
					JSONObject object = null;
					try {
						object = new JSONObject(result);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (object != null) {
						boolean flag = false;
						try {
							flag = object.getBoolean("success");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (flag) {
							handler.sendEmptyMessage(2);
						} else {
							handler.sendEmptyMessage(3);
						}
					}
				}

			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			switch (requestCode) {
			case 0:
				concentratorid.setText(data.getStringExtra("result"));
				break;
			case 1:
				meterid.setText("0000"
						+ data.getStringExtra("result").substring(5));
				break;
			}
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.activity_pointmeter_endtime_button:
			id = 1;
			setTime();
			break;
		case R.id.activity_pointmeter_starttime_button:
			id = 2;
			setTime();
			break;
		case R.id.activity_pointmeter_concentrator_id_button:
			intent.setClass(PointMeter.this, CameraActivityCapture.class);
			this.startActivityForResult(intent, 0);
			break;
		case R.id.activity_pointmeter_meter_id_button:
			intent.setClass(PointMeter.this, CameraActivityCapture.class);
			this.startActivityForResult(intent, 1);
			break;

		case R.id.activity_pointmeter_getalarm:
			if (concentratorid.getText().equals("")) {
				Toast.makeText(PointMeter.this,
						"You must fill a concentratorId first!",
						Toast.LENGTH_LONG).show();
			} else {
				getAlarm();
			}
			break;
		case R.id.activity_pointmeter_setpointmeter:
			if (meterid.getText().toString().equals("")) {
				Toast.makeText(PointMeter.this, "Please fill the MeterId!",
						Toast.LENGTH_LONG).show();
				break;
			}

			if (concentratorid.getText().toString().equals("")) {
				Toast.makeText(PointMeter.this,
						"Please fill the ConcentratorId!", Toast.LENGTH_LONG)
						.show();
				break;
			}

			if (starttime.getText().toString().equals("")) {
				Toast.makeText(PointMeter.this, "Please fill the StartTime!",
						Toast.LENGTH_LONG).show();
				break;
			}
			if (endtime.getText().toString().equals("")) {
				Toast.makeText(PointMeter.this, "Please fill the EndTime!",
						Toast.LENGTH_LONG).show();
				break;
			}
			if (close.isChecked()) {
				opt = "80";
			} else if (open.isChecked()) {
				opt = "95";
			}
			setPointMeter();
			break;

		case R.id.activity_pointmeter_getversion:
			if (!concentratorid.getText().toString().equals("")
					&& !meterid.getText().toString().equals(""))
				getVersion();

			break;

		}
	}
}
