package com.example.android_usb;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Arrays;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.util.Log;

/**
 * PL2303串口驱动
 * @author pc
 *
 */
public class ProlificSerialDriver {

	private static final int USB_READ_TIMEOUT_MILLIS = 10;
    private static final int USB_WRITE_TIMEOUT_MILLIS = 10;
    private final Object mReadBufferLock = new Object();
    private final Object mWriteBufferLock = new Object();
    private byte[] buffer = new byte[4096];
    private ByteArrayOutputStream bufferOS = new ByteArrayOutputStream(); 
	private Context context;
	private UsbEndpoint mReadEndpoint;
    private UsbEndpoint mWriteEndpoint;
    private UsbDeviceConnection connection;
    private UsbDevice usbDevice;
    
    public ProlificSerialDriver(Context context, UsbDevice usbDevice){
    	this.context = context;
    	this.usbDevice = usbDevice;
    }
    /**
     * 安装
     * @param baudRate
     * @throws IOException 
     */
    public void setup(int baudRate) throws IOException{
    	open();
    	initPL2303Chip();    	
    	ctrlOut(baudRate);

    }
    private void open() throws IOException{
    	UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
    	//申请权限
    	PendingIntent mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent("com.prolific.pl2303hxdsimpletest.USB_PERMISSION"), 0);
    	usbManager.requestPermission(usbDevice, mPermissionIntent);
    	UsbInterface usbInterface = usbDevice.getInterface(0);
		for (int i = 0; i < usbInterface.getEndpointCount(); ++i) {
            UsbEndpoint currentEndpoint = usbInterface.getEndpoint(i);

            switch (currentEndpoint.getAddress()) {
            case 0x83:
                mReadEndpoint = currentEndpoint;
                break;

            case 0x02:
                mWriteEndpoint = currentEndpoint;
                break;
            }
        }
		connection = usbManager.openDevice(usbDevice); 
		connection.claimInterface(usbInterface, true);
		
    }
    private final void ctrlOut(int baudRate) throws IOException {
    	//设置传输控制
    	byte[] lineRequestData = new byte[7];

    	lineRequestData[0] = (byte) (baudRate & 0xff);
    	lineRequestData[1] = (byte) ((baudRate >> 8) & 0xff);
    	lineRequestData[2] = (byte) ((baudRate >> 16) & 0xff);
    	lineRequestData[3] = (byte) ((baudRate >> 24) & 0xff);
    	        
    	lineRequestData[4] = 0;
    	lineRequestData[5] = 0;
    	lineRequestData[6] = (byte) 8;
    	 outControlTransfer(UsbConstants.USB_DIR_OUT | UsbConstants.USB_TYPE_CLASS | 0x01, 0x20, 0, 0, lineRequestData);
    }
    /**
     * 初始化芯片
     * @throws IOException 
     */
    private void initPL2303Chip() throws IOException{
    	int mDeviceType = getDeviceType();
    	vendorIn(0x8484, 0, 1);
        vendorOut(0x0404, 0, null);
        
        vendorIn(0x8484, 0, 1);
        vendorIn(0x8383, 0, 1);
        vendorIn(0x8484, 0, 1);
        
        vendorOut(0x0404, 1, null);
        vendorIn(0x8484, 0, 1);
        vendorIn(0x8383, 0, 1);
        
        vendorOut(0, 1, null);
        vendorOut(1, 0, null);
        vendorOut(2, (mDeviceType == 0) ? 0x44 : 0x24, null);
    }
    /**
     * 获得设备类型
     */
    private int getDeviceType(){
    	int mDeviceType = 0;
    	try {
			if (usbDevice.getDeviceClass() == 0x02) {
			    mDeviceType = 1;
			} else{
				Method getRawDescriptorsMethod = connection.getClass().getMethod("getRawDescriptors");
			    byte[] rawDescriptors = (byte[]) getRawDescriptorsMethod.invoke(connection);
			    byte maxPacketSize0 = rawDescriptors[7];
			    if (maxPacketSize0 == 64) {
			          mDeviceType = 0;
			    } else if ((usbDevice.getDeviceClass() == 0x00) || (usbDevice.getDeviceClass() == 0xff)) {
			          mDeviceType = 2;
			    } else {
			          mDeviceType = 0;
			    }
			}
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return mDeviceType;
    }
    /**
     * 写
     * @param buf
     * @param wlength
     * @return
     */
    public int write(byte[] buf, int wlength) {
        int offset = 0;
        byte[] write_buf = new byte[4096];
        synchronized (mWriteBufferLock) {
        	while (offset < wlength) {
                int write_size = 4096;

                if (offset + write_size > wlength) {
                    write_size = wlength - offset;
                }

                System.arraycopy(buf, offset, write_buf, 0, write_size);

                Log.i("发送数据：", Arrays.toString(write_buf));
                int actual_length = this.connection.bulkTransfer(this.mWriteEndpoint, write_buf, write_size, USB_WRITE_TIMEOUT_MILLIS);
                if (actual_length < 0) {
                    return -1;
                }

                offset += actual_length;
            }
        }        
        return offset;
    }
    /**
     * 读取
     * @param
     * @throws IOException
     */
    public byte[] read() throws IOException {
    	synchronized (mReadBufferLock) {
    		try { 
        		bufferOS.reset();
    			int number = 0;
    			while(number != -1){
    				number = connection.bulkTransfer(mReadEndpoint, buffer, buffer.length, USB_READ_TIMEOUT_MILLIS);
//                    number = connection.controlTransfer(UsbConstants.USB_DIR_IN | UsbConstants.USB_TYPE_VENDOR, 0x0A, 0, 0, buffer, buffer.length, USB_READ_TIMEOUT_MILLIS);

                    if(number>0){
                        Log.i("ceshishuju", Arrays.toString(bufferOS.toByteArray()));
                        MainActivity.showMessage("number:"+number);
                        bufferOS.write(buffer, 0, number);
                    }
    			}
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	}

        /*synchronized (mReadBufferLock) {
            int inMax = mReadEndpoint.getMaxPacketSize();
            ByteBuffer buf = ByteBuffer.allocate(256);
            UsbRequest usbRequest = new UsbRequest();
            usbRequest.initialize(connection, mReadEndpoint);
            boolean ret = usbRequest.queue(buf,buf.array().length);
            if(ret){
                if(connection.requestWait() == usbRequest){
                    byte[] retData = buf.array();
                    *//*for(Byte byte1 : retData){
                        System.out.println(byte1);
                    }*//*
                    Log.i("接收数据", Arrays.toString(buf.array()));
                    return buf.array();
                }
            }

        }*/
    	return bufferOS.toByteArray();
    }
    /**
     * 关闭资源
     */
    public void close(){
        try {
			if (connection == null) {
			    return ;
			}
			connection.releaseInterface(this.usbDevice.getInterface(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    /**
     * 输出的传输
     * @throws IOException
     */
    private final void outControlTransfer(int requestType, int request, int value, int index, byte[] data) throws IOException {
        int length = (data == null) ? 0 : data.length;
        int result = connection.controlTransfer(requestType, request, value, index, data, length, USB_WRITE_TIMEOUT_MILLIS);
        if (result != length) {
            throw new IOException(String.format("ControlTransfer with value 0x%x failed: %d", value, result));
        }
    }
    /**
     * 输入的传输
     * @throws IOException
     */
	 private final byte[] inControlTransfer(int requestType, int request, int value, int index, int length) throws IOException {
        byte[] buffer = new byte[length];
        int result = connection.controlTransfer(requestType, request, value, index, buffer, length, USB_READ_TIMEOUT_MILLIS);
        if (result != length) {
            throw new IOException(String.format("ControlTransfer with value 0x%x failed: %d", value, result));
        }
        return buffer;
    }
	 /**
	  * 写设备控制
	  */
	 private final byte[] vendorIn(int value, int index, int length)throws IOException {
        return inControlTransfer(UsbConstants.USB_DIR_IN | UsbConstants.USB_TYPE_VENDOR, 0x01, value, index, length);
    }
	 /**
	  * 读设备控制
	  */
	 private final void vendorOut(int value, int index, byte[] data)throws IOException {
        outControlTransfer(UsbConstants.USB_DIR_OUT | UsbConstants.USB_TYPE_VENDOR, 0x01, value, index, data);
    }
}
