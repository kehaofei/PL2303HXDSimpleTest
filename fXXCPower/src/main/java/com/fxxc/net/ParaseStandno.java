package com.fxxc.net;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.JsonReader;

public class ParaseStandno {
	private List<String> standno = new ArrayList<String>();

	public List<String> getStandno(String string) {
		JSONObject object = null;
		try {
			object = new JSONObject(string);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONArray array = null;
		try {
			array = object.getJSONArray("data");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (array.length() <= 0) {
			return standno;
		}
		for (int i = 0; i < array.length(); i++) {
			JSONObject object2 = null;
			try {
				object2 = array.getJSONObject(i);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				standno.add(object2.getString("STREET_NUMBER"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return standno;
	}
}
