package com.fxxc.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
	@SuppressLint("ShowToast")
	public static void toast(int id, Context context) {
		Toast.makeText(context, id, 1).show();
	}
}
