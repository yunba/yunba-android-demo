
package io.yunba.thirdpart.util;

import android.content.Context;
import android.content.SharedPreferences;


public class SharePrefsHelper {
	private static SharedPreferences _pushPref = null;
	private static final String TAG = "SharePrefsHelper";
	private static final String SERVER_CONFIG = "com.example.yunba.SharePrefs";

	private static SharedPreferences getDefaultSharedPreferences(Context context) {
		if(_pushPref==null)
			_pushPref = context.getSharedPreferences(SERVER_CONFIG,Context.MODE_PRIVATE);
		return _pushPref;
	}

	public static String getString(Context context,String key, String defValue) {
		getDefaultSharedPreferences(context);
		String retStr= _pushPref.getString(key, defValue);
		return retStr;
	}

	public static void setString(Context context,String key,String value) {
		SharedPreferences.Editor editor = getDefaultSharedPreferences(context).edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static int getInt(Context context,String key, int value) {
		getDefaultSharedPreferences(context);
		int retInt = _pushPref.getInt(key, value);
		return retInt;

	}
	public static void setInt(Context context,String key,int value) {
		SharedPreferences.Editor editor = getDefaultSharedPreferences(context).edit();
		editor.putInt(key, value);
		editor.commit();
	}



	public static long getLong(Context context,String key, long value) {
		getDefaultSharedPreferences(context);
		long retIong = _pushPref.getLong(key, value);
		return retIong;

	}
	public static void setLong(Context context,String key, long value) {
		SharedPreferences.Editor editor = getDefaultSharedPreferences(context).edit();
		editor.putLong(key, value);
		editor.commit();
	}

}
