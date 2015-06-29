package com.utility;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Pref {

	private SharedPreferences spref;
	//private Activity _activity;
	private static final String PREF_FILE = "com.count";
	private Editor _editorSpref;

	public SharedPreferences getSharedPreferencesInstance() {
		return spref;
	}

	public Editor getSharedPreferencesEditorInstance() {
		return _editorSpref;
	}

	@SuppressLint("CommitPrefEdits")
	public Pref(Context _thisContext) {
		// TODO Auto-generated constructor stub
		//this._activity = (Activity)_thisContext;
		spref = _thisContext.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
		_editorSpref = spref.edit();
	}

	public String getSession(String key) {
		String value = spref.getString(key, "");
		return value;
	}

	public int getIntegerSession(String key) {
		int value = spref.getInt(key, Integer.MIN_VALUE);
		return value;
	}

	public void setSession(String key, String value) {
		if (key != null && value != null) {
			_editorSpref.putString(key, value);
			_editorSpref.commit();
		}
	}

	// @author : DTS
	public void setSession(String key, int value) {
		if (key != null) {
			_editorSpref.putInt(key, value);
			_editorSpref.commit();
		}
	}
	
	public void setSession(String key, boolean value) {
		if (key != null) {
			_editorSpref.putBoolean(key, value);
			_editorSpref.commit();
		}
	}
	
	public boolean getBooleanSession(String key){
		boolean value=spref.getBoolean(key, false);
		return value;
	}

    public void saveDeviecId(String device_id) {
        _editorSpref.putString("device_id", device_id);
        _editorSpref.commit();
    }

    public String getDeviceId() {
        return spref.getString("device_id", "");
    }

    public void saveAccessToken(String access_token) {
        _editorSpref.putString("access_token", access_token);
        _editorSpref.commit();
    }

    public String getAccessToken() {
        return spref.getString("access_token", "");
    }

    public void saveName(String name) {
        _editorSpref.putString("name", name);
        _editorSpref.commit();
    }

    public String getName() {
        return spref.getString("name", "");
    }

    public void saveMobileNo(String mobile) {
        _editorSpref.putString("mobile", mobile);
        _editorSpref.commit();
    }

    public String getMobileNo() {
        return spref.getString("mobile", "");
    }
}
