package com.UARTLoopback;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.UARTLoopback.R.drawable;

public class UARTLoopbackActivity extends Activity {

	// menu item
	Menu myMenu;
    final int MENU_FORMAT = Menu.FIRST;
    final int MENU_CLEAN = Menu.FIRST+1;
    final String[] formatSettingItems = {"ASCII","Hexadecimal", "Decimal"};
    
	final int FORMAT_ASCII = 0;
	final int FORMAT_HEX = 1;
	final int FORMAT_DEC = 2;
	
	int inputFormat = FORMAT_ASCII;
	StringBuffer readSB = new StringBuffer();
    
	/* thread to read the data */
	public handler_thread handlerThread;

	/* declare a FT311 UART interface variable */
	public FT311UARTInterface uartInterface;
	/* graphical objects */
	EditText readText;
	EditText writeText;
	Spinner baudSpinner;;
	Spinner stopSpinner;
	Spinner dataSpinner;
	Spinner paritySpinner;
	Spinner flowSpinner;

	Button writeButton, configButton;

	/* local variables */
	byte[] writeBuffer;
	byte[] readBuffer;
	char[] readBufferToChar;
	int[] actualNumBytes;

	int numBytes;
	byte count;
	byte status;
	byte writeIndex = 0;
	byte readIndex = 0;

	int baudRate; /* baud rate */
	byte stopBit; /* 1:1stop bits, 2:2 stop bits */
	byte dataBit; /* 8:8bit, 7: 7bit */
	byte parity; /* 0: none, 1: odd, 2: even, 3: mark, 4: space */
	byte flowControl; /* 0:none, 1: flow control(CTS,RTS) */
	public Context global_context;
	public boolean bConfiged = false;
	public SharedPreferences sharePrefSettings;
	Drawable originalDrawable;
	public String act_string; 

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		sharePrefSettings = getSharedPreferences("UARTLBPref", 0);
		//cleanPreference();
		/* create editable text objects */
		readText = (EditText) findViewById(R.id.ReadValues);
		writeText = (EditText) findViewById(R.id.WriteValues);

		global_context = this;

		configButton = (Button) findViewById(R.id.configButton);
		writeButton = (Button) findViewById(R.id.WriteButton);
		
		originalDrawable = configButton.getBackground();

		/* allocate buffer */
		writeBuffer = new byte[64];
		readBuffer = new byte[4096];
		readBufferToChar = new char[4096]; 
		actualNumBytes = new int[1];

		/* setup the baud rate list */
		baudSpinner = (Spinner) findViewById(R.id.baudRateValue);
		ArrayAdapter<CharSequence> baudAdapter = ArrayAdapter.createFromResource(this, R.array.baud_rate,
				R.layout.my_spinner_textview);
		baudAdapter.setDropDownViewResource(R.layout.my_spinner_textview);		
		baudSpinner.setAdapter(baudAdapter);
		baudSpinner.setGravity(0x10);
		baudSpinner.setSelection(4);
		/* by default it is 9600 */
		baudRate = 9600;

		/* stop bits */
		stopSpinner = (Spinner) findViewById(R.id.stopBitValue);
		ArrayAdapter<CharSequence> stopAdapter = ArrayAdapter.createFromResource(this, R.array.stop_bits,
						R.layout.my_spinner_textview);
		stopAdapter.setDropDownViewResource(R.layout.my_spinner_textview);
		stopSpinner.setAdapter(stopAdapter);
		stopSpinner.setGravity(0x01);
		/* default is stop bit 1 */
		stopBit = 1;

		/* daat bits */
		dataSpinner = (Spinner) findViewById(R.id.dataBitValue);
		ArrayAdapter<CharSequence> dataAdapter = ArrayAdapter.createFromResource(this, R.array.data_bits,
						R.layout.my_spinner_textview);
		dataAdapter.setDropDownViewResource(R.layout.my_spinner_textview);
		dataSpinner.setAdapter(dataAdapter);
		dataSpinner.setGravity(0x11);
		dataSpinner.setSelection(1);
		/* default data bit is 8 bit */
		dataBit = 8;

		/* parity */
		paritySpinner = (Spinner) findViewById(R.id.parityValue);
		ArrayAdapter<CharSequence> parityAdapter = ArrayAdapter.createFromResource(this, R.array.parity,
						R.layout.my_spinner_textview);
		parityAdapter.setDropDownViewResource(R.layout.my_spinner_textview);
		paritySpinner.setAdapter(parityAdapter);
		paritySpinner.setGravity(0x11);
		/* default is none */
		parity = 0;

		/* flow control */
		flowSpinner = (Spinner) findViewById(R.id.flowControlValue);
		ArrayAdapter<CharSequence> flowAdapter = ArrayAdapter.createFromResource(this, R.array.flow_control,
						R.layout.my_spinner_textview);
		flowAdapter.setDropDownViewResource(R.layout.my_spinner_textview);
		flowSpinner.setAdapter(flowAdapter);
		flowSpinner.setGravity(0x11);
		/* default flow control is is none */
		flowControl = 0;


		/* set the adapter listeners for baud */
		baudSpinner.setOnItemSelectedListener(new MyOnBaudSelectedListener());
		/* set the adapter listeners for stop bits */
		stopSpinner.setOnItemSelectedListener(new MyOnStopSelectedListener());
		/* set the adapter listeners for data bits */
		dataSpinner.setOnItemSelectedListener(new MyOnDataSelectedListener());
		/* set the adapter listeners for parity */
		paritySpinner.setOnItemSelectedListener(new MyOnParitySelectedListener());
		/* set the adapter listeners for flow control */
		flowSpinner.setOnItemSelectedListener(new MyOnFlowSelectedListener());		
		
		act_string = getIntent().getAction();
		if( -1 != act_string.indexOf("android.intent.action.MAIN")){
			restorePreference();
		}			
		else if( -1 != act_string.indexOf("android.hardware.usb.action.USB_ACCESSORY_ATTACHED")){
			cleanPreference();
		}		
		
		configButton.setOnClickListener(new View.OnClickListener() {

			// @Override
			public void onClick(View v) {
				
				if(false == bConfiged){
					bConfiged = true;
					uartInterface.SetConfig(baudRate, dataBit, stopBit, parity, flowControl);
					savePreference();
				}

				if(true == bConfiged){
					configButton.setBackgroundColor(0xff888888); // color GRAY:0xff888888
					configButton.setText("Configured");
				}
			}

		});

		/* handle write click */
		writeButton.setOnClickListener(new View.OnClickListener() {

			// @Override
			public void onClick(View v) {
 
				if (writeText.length() != 0x00)
				{
					writeData();
				}
			}
		});

		uartInterface = new FT311UARTInterface(this, sharePrefSettings);

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		handlerThread = new handler_thread(handler);
		handlerThread.start();

	}
	
	protected void cleanPreference(){
		SharedPreferences.Editor editor = sharePrefSettings.edit();
		editor.remove("configed");
		editor.remove("baudRate");
		editor.remove("stopBit");
		editor.remove("dataBit");
		editor.remove("parity");
		editor.remove("flowControl");
		editor.commit();
	}

	protected void savePreference() {
		if(true == bConfiged){
			sharePrefSettings.edit().putString("configed", "TRUE").commit();
			sharePrefSettings.edit().putInt("baudRate", baudRate).commit();
			sharePrefSettings.edit().putInt("stopBit", stopBit).commit();
			sharePrefSettings.edit().putInt("dataBit", dataBit).commit();
			sharePrefSettings.edit().putInt("parity", parity).commit();			
			sharePrefSettings.edit().putInt("flowControl", flowControl).commit();			
		}
		else{
			sharePrefSettings.edit().putString("configed", "FALSE").commit();
		}
	}
	
	protected void restorePreference() {
		String key_name = sharePrefSettings.getString("configed", "");
		if(true == key_name.contains("TRUE")){
			bConfiged = true;
		}
		else{
			bConfiged = false;
        }
		
		baudRate = sharePrefSettings.getInt("baudRate", 9600);
		stopBit = (byte)sharePrefSettings.getInt("stopBit", 1);
		dataBit = (byte)sharePrefSettings.getInt("dataBit", 8);
		parity = (byte)sharePrefSettings.getInt("parity", 0);
		flowControl = (byte)sharePrefSettings.getInt("flowControl", 0);

		if(true == bConfiged){			
			configButton.setText("Configured");
			configButton.setBackgroundColor(0xff888888); // color GRAY:0xff888888
			switch(baudRate)
			{
			case 300:baudSpinner.setSelection(0);break;
			case 600:baudSpinner.setSelection(1);break;
			case 1200:baudSpinner.setSelection(2);break;
			case 4800:baudSpinner.setSelection(3);break;
			case 9600:baudSpinner.setSelection(4);break;
			case 19200:baudSpinner.setSelection(5);break;
			case 38400:baudSpinner.setSelection(6);break;
			case 57600:baudSpinner.setSelection(7);break;
			case 115200:baudSpinner.setSelection(8);break;
			case 230400:baudSpinner.setSelection(9);break;
			case 460800:baudSpinner.setSelection(10);break;
			case 921600:baudSpinner.setSelection(11);break;
			default:baudSpinner.setSelection(4);break;
			}
			
			switch(stopBit)
			{
			case 1:stopSpinner.setSelection(0);break;
			case 2:stopSpinner.setSelection(1);break;
			default:stopSpinner.setSelection(0);break;
			}

			switch(dataBit)
			{
			case 7:dataSpinner.setSelection(0);break;
			case 8:dataSpinner.setSelection(1);break;
			default:dataSpinner.setSelection(1);break;
			}

			switch(parity)
			{
			case 0:paritySpinner.setSelection(0);break;
			case 1:paritySpinner.setSelection(1);break;
			case 2:paritySpinner.setSelection(2);break;
			case 3:paritySpinner.setSelection(3);break;
			case 4:paritySpinner.setSelection(4);break;
			default:paritySpinner.setSelection(0);break;
			}
			
			switch(flowControl)
			{
			case 0:flowSpinner.setSelection(0);break;
			case 1:flowSpinner.setSelection(1);break;
			default:flowSpinner.setSelection(0);break;
			}
		}
		else{
			baudSpinner.setSelection(4);
			stopSpinner.setSelection(0);
			dataSpinner.setSelection(1);
			paritySpinner.setSelection(0);
			flowSpinner.setSelection(0);
			configButton.setBackgroundDrawable(originalDrawable);			
		}
	}
	
	
	public class MyOnBaudSelectedListener implements OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {

			baudRate = Integer.parseInt(parent.getItemAtPosition(pos).toString());
		}

		public void onNothingSelected(AdapterView<?> parent) { // Do nothing. }}
		}
	}

	public class MyOnStopSelectedListener implements OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {

			stopBit = (byte) Integer.parseInt(parent.getItemAtPosition(pos).toString());
		}

		public void onNothingSelected(AdapterView<?> parent) { // Do nothing. }}
		}
	}

	public class MyOnDataSelectedListener implements OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {

			dataBit = (byte) Integer.parseInt(parent.getItemAtPosition(pos).toString());
		}

		public void onNothingSelected(AdapterView<?> parent) { // Do nothing. }}
		}
	}

	public class MyOnParitySelectedListener implements OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {

			String parityString = new String(parent.getItemAtPosition(pos).toString());
			if (parityString.compareTo("None") == 0) {
				parity = 0;
			}

			if (parityString.compareTo("Odd") == 0) {
				parity = 1;
			}

			if (parityString.compareTo("Even") == 0) {
				parity = 2;
			}

			if (parityString.compareTo("Mark") == 0) {
				parity = 3;
			}

			if (parityString.compareTo("Space") == 0) {
				parity = 4;
			}
		}

		public void onNothingSelected(AdapterView<?> parent) { // Do nothing. }}
		}
	}

	public class MyOnFlowSelectedListener implements OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {

			String flowString = new String(parent.getItemAtPosition(pos).toString());
			if (flowString.compareTo("None") == 0) {
				flowControl = 0;
			}

			if (flowString.compareTo("CTS/RTS") == 0) {
				flowControl = 1;
			}
		}

		public void onNothingSelected(AdapterView<?> parent) { // Do nothing. }}
		}
	}

	//@Override
	public void onHomePressed() {
		onBackPressed();
	}	

	public void onBackPressed() {
	    super.onBackPressed();
	}	
	
	@Override
	protected void onResume() {
		// Ideally should implement onResume() and onPause()
		// to take appropriate action when the activity looses focus
		super.onResume();		
		if( 2 == uartInterface.ResumeAccessory() )
		{
			cleanPreference();
			restorePreference();
		}
	}

	@Override
	protected void onPause() {
		// Ideally should implement onResume() and onPause()
		// to take appropriate action when the activity looses focus
		super.onPause();
	}

	@Override
	protected void onStop() {
		// Ideally should implement onResume() and onPause()
		// to take appropriate action when the activity looses focus
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		uartInterface.DestroyAccessory(bConfiged);
		super.onDestroy();
	}


	final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			
			for(int i=0; i<actualNumBytes[0]; i++)
			{
				readBufferToChar[i] = (char)readBuffer[i];
			}
			appendData(readBufferToChar, actualNumBytes[0]);
		}
	};

	/* usb input data handler */
	private class handler_thread extends Thread {
		Handler mHandler;

		/* constructor */
		handler_thread(Handler h) {
			mHandler = h;
		}

		public void run() {
			Message msg;

			while (true) {
				
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
				}

				status = uartInterface.ReadData(4096, readBuffer,actualNumBytes);

				if (status == 0x00 && actualNumBytes[0] > 0) {
					msg = mHandler.obtainMessage();
					mHandler.sendMessage(msg);
				}

			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		myMenu = menu;
		myMenu.add(0, MENU_FORMAT, 0, "Format - ASCII");
		myMenu.add(0, MENU_CLEAN, 0, "Clean Read Bytes Field");		
		return super.onCreateOptionsMenu(myMenu);
	}	

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
        case MENU_FORMAT:
        	new AlertDialog.Builder(global_context).setTitle("Data Format")
			.setItems(formatSettingItems, new DialogInterface.OnClickListener() 
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{	
					MenuItem item = myMenu.findItem(MENU_FORMAT);
					if(0 == which)
					{						
						inputFormat = FORMAT_ASCII;
					    item.setTitle("Format - "+ formatSettingItems[0]);
					}
					else if(1 == which)
					{
						inputFormat = FORMAT_HEX;
						item.setTitle("Format - "+ formatSettingItems[1]);
					}
					else
					{
						inputFormat = FORMAT_DEC;
						item.setTitle("Format - "+ formatSettingItems[2]);						
					}
				    char[] ch = new char[1];
				    appendData(ch, 0);
				}
			}).show();           	
        	
        	break;
        	
        case MENU_CLEAN:
        default:        	
        	readSB.delete(0, readSB.length());
        	readText.setText(readSB);
        	break;
        }
 
        return super.onOptionsItemSelected(item);
    }
    
    public void writeData()
    {
    	String srcStr = writeText.getText().toString();    	
    	String destStr = "";
    	switch(inputFormat)
    	{
    	case FORMAT_HEX:
    		{	
				String[] tmpStr = srcStr.split(" ");
				
				for(int i = 0; i < tmpStr.length; i++)
				{
					if(tmpStr[i].length() == 0)
					{
						msgToast("Incorrect input for HEX format."
								+"\nThere should be only 1 space between 2 HEX words.",Toast.LENGTH_SHORT);
						return;
					}					
					else if(tmpStr[i].length() != 2)
					{
						msgToast("Incorrect input for HEX format."
								+"\nIt should be 2 bytes for each HEX word.",Toast.LENGTH_SHORT);
						return;
					}						
				}
				
				try	
				{
					destStr = hexToAscii(srcStr.replaceAll(" ", ""));
				}
				catch(IllegalArgumentException e)
				{
					msgToast("Incorrect input for HEX format."
						    +"\nAllowed charater: 0~9, a~f and A~F",Toast.LENGTH_SHORT);
					return;
				}
    		}
    		break;
    		
    	case FORMAT_DEC:
    		{				
				String[] tmpStr = srcStr.split(" ");
				
				for(int i = 0; i < tmpStr.length; i++)
				{
					if(tmpStr[i].length() == 0)
					{
						msgToast("Incorrect input for DEC format."
								+"\nThere should be only 1 space between 2 DEC words.",Toast.LENGTH_SHORT);
						return;
					}					
					else if(tmpStr[i].length() != 3)
					{
						msgToast("Incorrect input for DEC format."
								+"\nIt should be 3 bytes for each DEC word.",Toast.LENGTH_SHORT);
						return;
					}						
				}
				
				try
				{
					destStr = decToAscii(srcStr.replaceAll(" ", ""));
				}
				catch(IllegalArgumentException e)
				{	
					if(e.getMessage().equals("ex_a"))
					{
						msgToast("Incorrect input for DEC format."
								+"\nAllowed charater: 0~9",Toast.LENGTH_SHORT);					
					}
					else
					{
						msgToast("Incorrect input for DEC format."
							    +"\nAllowed range: 0~255",Toast.LENGTH_SHORT);
					}					
					return;
				}
    		}
    		break;
    		
    	case FORMAT_ASCII:    		
    	default:
    		destStr = srcStr;
    		break;
    	}

		numBytes = destStr.length();
		for (int i = 0; i < numBytes; i++) {
			writeBuffer[i] = (byte)destStr.charAt(i);
		}
		uartInterface.SendData(numBytes, writeBuffer);
		
    }
    
	String hexToAscii(String s) throws IllegalArgumentException
	{
		  int n = s.length();
		  StringBuilder sb = new StringBuilder(n / 2);
		  for (int i = 0; i < n; i += 2)
		  {
		    char a = s.charAt(i);
		    char b = s.charAt(i + 1);
		    sb.append((char) ((hexToInt(a) << 4) | hexToInt(b)));
		  }
		  return sb.toString();
	}
	
	static int hexToInt(char ch)
	{
		  if ('a' <= ch && ch <= 'f') { return ch - 'a' + 10; }
		  if ('A' <= ch && ch <= 'F') { return ch - 'A' + 10; }
		  if ('0' <= ch && ch <= '9') { return ch - '0'; }
		  throw new IllegalArgumentException(String.valueOf(ch));
	}

	String decToAscii(String s) throws IllegalArgumentException
	{	
		int n = s.length();
		boolean pause = false;
		StringBuilder sb = new StringBuilder(n / 2);
		for (int i = 0; i < n; i += 3)
		{
			char a = s.charAt(i);
			char b = s.charAt(i + 1);
			char c = s.charAt(i + 2);
			int val = decToInt(a)*100 + decToInt(b)*10 + decToInt(c);
			if(0 <= val && val <= 255)
			{
				sb.append((char) val);
			}
			else
			{
				pause = true;
				break;
			}
		}
		
		if(false == pause)
			return sb.toString();
		throw new IllegalArgumentException("ex_b");
	}
	
	static int decToInt(char ch)
	{
		  if ('0' <= ch && ch <= '9') { return ch - '0'; }
		  throw new IllegalArgumentException("ex_a");
	}
	
    void msgToast(String str, int showTime)
    {
    	Toast.makeText(global_context, str, showTime).show();
    }
    
    public void appendData(String s)
    {
    	switch(inputFormat)
    	{
    	case FORMAT_HEX:
    		{
    			readText.append("Hex");    		
    		}
    		break;
    		
    	case FORMAT_DEC:
    		{
    			readText.append("Dec");
    		}
    		break;
    		
    	case FORMAT_ASCII:    		
    	default:
    		readText.append(s);
    		break;
    	}
    }
    
    public void appendData(char[] data, int len)
    {
    	if(len >= 1)    		
    		readSB.append(String.copyValueOf(data, 0, len));
    	
    	switch(inputFormat)
    	{
    	case FORMAT_HEX:
    		{
    			char[] ch = readSB.toString().toCharArray();
    			String temp;
    			StringBuilder tmpSB = new StringBuilder();
    			for(int i = 0; i < ch.length; i++)
    			{
    				temp = String.format("%02x", (int) ch[i]);

    				if(temp.length() == 4)
    				{
    					tmpSB.append(temp.substring(2, 4));
    				}
    				else
    				{
    					tmpSB.append(temp);
    				}

   					if(i+1 < ch.length)
   					{
   						tmpSB.append(" ");	
   					}
    			}    			
    			readText.setText(tmpSB);
    			tmpSB.delete(0, tmpSB.length());
    		}
    		break;
    		
    	case FORMAT_DEC:
    		{
    			char[] ch = readSB.toString().toCharArray();
    			String temp;
    			StringBuilder tmpSB = new StringBuilder();
    			for(int i = 0; i < ch.length; i++)
    			{   				
    				temp = Integer.toString((int)(ch[i] & 0xff));

    				for(int j = 0; j < (3 - temp.length()); j++)
    				{
    					tmpSB.append("0");
    				}
   					tmpSB.append(temp);
   					
   					if(i+1 < ch.length)
   					{
   						tmpSB.append(" ");	
   					}
    			}    			
    			readText.setText(tmpSB);
    			tmpSB.delete(0, tmpSB.length());    			
    		}
    		break;
    		
    	case FORMAT_ASCII:    		
    	default:
    		readText.setText(readSB);
    		break;
    	}
    }
}
