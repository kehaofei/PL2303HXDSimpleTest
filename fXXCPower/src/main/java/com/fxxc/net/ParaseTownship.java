package com.fxxc.net;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ParaseTownship {
	List<String> township = new ArrayList<String>();

	public List<String> getTownship(String result) {
		JSONObject object = null;
		try {
			object = new JSONObject(result);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		JSONArray array = null;
		try {
			array = object.getJSONArray("data");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < array.length(); i++) {
			JSONObject object2 = null;
			try {
				object2 = array.getJSONObject(i);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				township.add(object2.getString("NAME"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return township;
	}
}
