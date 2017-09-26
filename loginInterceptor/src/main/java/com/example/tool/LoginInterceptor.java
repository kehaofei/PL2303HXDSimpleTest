package com.example.tool;

import com.example.login.LoginActivity;
import com.example.login.MainActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

/**
 * 登录判断类
 * 
 * @author bzl
 * 
 */
public class LoginInterceptor {
	public static final String mINVOKER = "INTERCEPTOR_INVOKER";

	/**
	 * 判断处理
	 * 
	 * @param ctx
	 *            当前activity的上下文
	 * @param target
	 *            目标activity的target
	 * @param params
	 *            目标activity所需要的参数
	 * @param intent
	 *            目标activity
	 * 
	 */
	public static void interceptor(Context ctx, String target, Bundle bundle, Intent intent) {
		if (target != null && target.length() > 0) {
			LoginCarrier invoker = new LoginCarrier(target, bundle);
			if (getLogin()) {
				invoker.invoke(ctx);
			} else {
				if (intent == null) {
					intent = new Intent(ctx, LoginActivity.class);
				}
				login(ctx, invoker, intent);
			}
		} else {
			Toast.makeText(ctx, "没有activity可以跳转", 300).show();
		}
	}

	/**
	 * 登录判断
	 * 
	 * @param ctx
	 *            当前activity的上下文
	 * @param target
	 *            目标activity的target
	 * @param params
	 *            目标activity所需要的参数
	 */
	public static void interceptor(Context ctx, String target, Bundle bundle) {
		interceptor(ctx, target, bundle, null);
	}

	// 这里获取登录状态，具体获取方法看项目具体的判断方法
	private static boolean getLogin() {
		return MainActivity.is_login;
	}

	private static void login(Context context, LoginCarrier invoker, Intent intent) {
		intent.putExtra(mINVOKER, invoker);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(intent);
	}
}
