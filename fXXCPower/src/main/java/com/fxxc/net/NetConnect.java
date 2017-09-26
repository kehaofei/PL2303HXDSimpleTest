package com.fxxc.net;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;

import org.kobjects.base64.Base64;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import com.fxxc.config.Info;

public class NetConnect {
	public String getAlarmBox(String ConcentratorID) {
		SoapObject soapObject = new SoapObject(Info.TARGETNAMESPACE,
				Info.GETALARMBOX);
		soapObject.addProperty("ConcentratorID", ConcentratorID);
		soapObject.addProperty("ErrorCode", 0);
		SoapSerializationEnvelope envelop = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelop.bodyOut = soapObject;
		envelop.dotNet = true;
		envelop.setOutputSoapObject(soapObject);
		envelop.encodingStyle = "UTF-8";
		HttpTransportSE httpSE = new HttpTransportSE(Info.WSDL);
		httpSE.debug = true;
		try {
			httpSE.call(Info.TARGETNAMESPACE + Info.GETALARMBOX, envelop);
			System.out.println(envelop.bodyIn + "---" + envelop.bodyOut);
			Object object = envelop.getResponse();
			// System.out.println(object.toString());
			return object.toString();
		} catch (IOException e) {
			return e.toString();
		} catch (XmlPullParserException e) {
			return e.toString();
		}
	}

	public String GetTaskStatus(String taskCode) {
		SoapObject soapObject = new SoapObject(Info.TARGETNAMESPACE,
				Info.GETTASKSTATUS);
		soapObject.addProperty("_taskCode", taskCode);
		SoapSerializationEnvelope envelop = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelop.bodyOut = soapObject;
		envelop.dotNet = true;
		envelop.setOutputSoapObject(soapObject);
		envelop.encodingStyle = "UTF-8";
		HttpTransportSE httpSE = new HttpTransportSE(Info.WSDL);
		httpSE.debug = true;
		try {
			httpSE.call(Info.TARGETNAMESPACE + Info.GETTASKSTATUS, envelop);
			System.out.println(envelop.bodyIn + "---" + envelop.bodyOut);
			Object object = envelop.getResponse();
			System.out.println(object.toString());
			return object.toString();
		} catch (IOException e) {
			return e.toString();
		} catch (XmlPullParserException e) {
			return e.toString();
		}
	}

	public String SetMeterParams(String equipid, String meterid, String state,
			String starttime, String endtime, String opt) {
		SoapObject soapObject = new SoapObject(Info.TARGETNAMESPACE,
				Info.SETMETERPARAMS);
		soapObject.addProperty("ConcentratorID", equipid);
		soapObject.addProperty("MeterCode", meterid);
//		soapObject.addProperty("_command", "C035"); //ErrorCode
//		soapObject.addProperty("_jobname", "sss");
		soapObject.addProperty("state", opt);
	
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		soapObject.addProperty("startTime", format.format(date) + " "
				+ starttime);
		soapObject.addProperty("restoreTime", format.format(date) + " "
				+ endtime);
		soapObject.addProperty("ErrorCode", 0);
		SoapSerializationEnvelope envelop = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelop.bodyOut = soapObject;
		envelop.dotNet = true;
		envelop.setOutputSoapObject(soapObject);
		envelop.encodingStyle = "UTF-8";
		HttpTransportSE httpSE = new HttpTransportSE(Info.WSDL);
		httpSE.debug = true;
		try {
			httpSE.call(Info.TARGETNAMESPACE + Info.SETMETERPARAMS, envelop);
			System.out.println(envelop.bodyIn + "---" + envelop.bodyOut);
			Object object = envelop.getResponse();
			System.out.println(object.toString());
			return object.toString();
		} catch (IOException e) {
			return e.toString();
		} catch (XmlPullParserException e) {
			return e.toString();
		}
	}

	public String PointMeterReadMeterId() {
		SoapObject soapObject = new SoapObject(Info.TARGETNAMESPACE,
				Info.POINTMETERREADMETERID);
		soapObject.addProperty("_rtuid", 200481);
		soapObject.addProperty("_meterid", 163);
		soapObject.addProperty("_protocol", 468);
		soapObject.addProperty("_jobname", "adf");
		SoapSerializationEnvelope envelop = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelop.bodyOut = soapObject;
		envelop.dotNet = true;
		envelop.setOutputSoapObject(soapObject);
		envelop.encodingStyle = "UTF-8";
		HttpTransportSE httpSE = new HttpTransportSE(Info.WSDL);
		httpSE.debug = true;
		try {
			httpSE.call(Info.TARGETNAMESPACE + Info.POINTMETERREADMETERID,
					envelop);
			System.out.println(envelop.bodyIn + "---" + envelop.bodyOut);
			Object object = envelop.getResponse();
			System.out.println(object.toString());
			return object.toString();
		} catch (IOException e) {
			return e.toString();
		} catch (XmlPullParserException e) {
			return e.toString();
		}
	}

	public String ReadMeterVersionByCode(Integer concentratorid,String meterid) {
		SoapObject soapObject = new SoapObject(Info.TARGETNAMESPACE,
				Info.READMETERVERSIONBYCODE);
		soapObject.addProperty("ConcentratorID", concentratorid);
		soapObject.addProperty("MeterCode", meterid);
		soapObject.addProperty("ErrorCode", 0);
		SoapSerializationEnvelope envelop = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelop.bodyOut = soapObject;
		envelop.dotNet = true;
		envelop.setOutputSoapObject(soapObject);
		envelop.encodingStyle = "UTF-8";
		HttpTransportSE httpSE = new HttpTransportSE(Info.WSDL);
		httpSE.debug = true;
		try {
			httpSE.call(Info.TARGETNAMESPACE + Info.READMETERVERSIONBYCODE,
					envelop);
			System.out.println(envelop.bodyIn + "---" + envelop.bodyOut);
			Object object = envelop.getResponse();
			System.out.println(object.toString());
			return object.toString();
		} catch (IOException e) {
			return e.toString();
		} catch (XmlPullParserException e) {
			return e.toString();
		}
	}
	
}
