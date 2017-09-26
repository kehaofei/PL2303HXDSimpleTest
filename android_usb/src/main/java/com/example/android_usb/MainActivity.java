package com.example.android_usb;


import java.io.IOException;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private LinearLayout usbList ;
    private ProlificSerialDriver driver;

    private static Activity activity;
    private static Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			String string = (String)msg.obj;
			Toast.makeText(activity, string, Toast.LENGTH_SHORT).show();
		}
    	
    };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);
		activity = MainActivity.this;
		try {
			usbList = (LinearLayout)this.findViewById(R.id.usbList);
			//showDevice();
			UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
			UsbDevice usbDevice = null;
			for (UsbDevice item : usbManager.getDeviceList().values()) {
				usbDevice = item;
			}
			if(usbDevice == null){
				Toast.makeText(MainActivity.this, "设备是空的", Toast.LENGTH_SHORT).show();
				return ;
			}else{
				Toast.makeText(MainActivity.this, "设备不是空的", Toast.LENGTH_SHORT).show();
				
			}
			driver = new ProlificSerialDriver(this, usbDevice);
			driver.setup(38400);
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(MainActivity.this, "出现异常了:"+e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}
	/**
	 * 项usb写数据
	 * @param view
	 */
	public void writeToUSb(View view){
		String str = "68 01 00 00 00 00 00 68 01 02 34 23 2B 16";
		byte[] data = StringToByteArr(str);
		driver.write(data, data.length);
	}
	/**
	 * 字符串转Byte数组按序转换（空格分隔）
	 * @author XCCD
	 * @param str
	 * @return
	 */
	public static byte[] StringToByteArr(String str){
		if(str.contains(" ")){
			str = str.replace(" ", "");
		}

		if(str.toCharArray().length%2 == 1){
			str = "0"+str;
		}
		int len = str.toCharArray().length/2;
		byte[] bytes = new byte[len];
		int a = 0;
		for(int i=0;i<str.toCharArray().length;i=i+2){
			String sts = str.substring(i,i+2);
			int bs  = Integer.parseInt(sts,16);

			bytes[a] = int2Onebyte(bs)[0];
			a++;
		}
		return bytes;
	}
	/**
	 *
	 * TODO  整数转byte（最好两位以内？？）低位在前，高位在后
	 * <br>2016-5-11  下午6:01:35
	 * @param num
	 * @return
	 */
	public static byte[] int2Onebyte(int num) {
		byte[] buf = new byte[1];
		for (int i = 0; i < buf.length; i++) {
			buf[i] = (byte) (num & 0xFF);
		}
		return buf;
	}

	/**
	 * 读取
	 */
	public void readFromUsb(View view){	
		//Toast.makeText(MainActivity.this, "read05", Toast.LENGTH_SHORT).show();
			new Thread(){
				@Override
				public void run() {				
					super.run();
					try {
						//showMessage("read06");
						while(1 == 1){
							//showMessage("read07");
							read();
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
			}.start();
	}
	
    public void read() throws IOException {
    	try {
    		//showMessage("read03");
			byte[] data = driver.read();
			//showMessage("read04");
//			if(data.length == 0){
//				//showMessage("没读到数据");
//				return ;
//			}
			
			String message = "Read " + data.length + " bytes: \n"
			        + HexDump.dumpHexString(data) + "\n\n";
			showMessage(message);
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(MainActivity.this, "出现异常了:"+e.getMessage(), Toast.LENGTH_SHORT).show();
		}
    }
    /**
     * 
     */
    public static void showMessage(String message){
    	Message msg = new Message();
    	msg.obj = message;
    	handler.sendMessage(msg);
    }
	/**
	 * 搜索USB设备
	 */
	public void searchUsb(View view){
		showDevice();
	}
	@Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(this, "执行了onResume", Toast.LENGTH_SHORT).show();
        showDevice();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(this, "执行了onPause", Toast.LENGTH_SHORT).show();
        showDevice();
    }
    
    /**
     * 显示USB设备
     */
    private void showDevice(){
    	usbList.removeAllViews();
		UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
		for (UsbDevice usbDevice : usbManager.getDeviceList().values()) {
			int vendorId = usbDevice.getVendorId();
	        int productId = usbDevice.getProductId();
	        TextView tv = new TextView(this);
	        tv.setText("vendorId:" + vendorId + "  productId:" + productId);
	        usbList.addView(tv);
        }
    }
}
