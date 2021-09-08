package com.ZHIANG;
 
import android.app.Activity;
import android.util.Log;

public class ZAandroid {
    static final String TAG = "Log";
    private static Activity m_content = null;
    //****************************************************************************************************
	static 
	{
		try{
			System.loadLibrary("ZAandroid");
		}
		catch(UnsatisfiedLinkError e) {
			Log.e("FPAPI","ZAandroid",e);
		}
	}
	public ZAandroid(Activity a) {
		m_content = a;
	}
	//------------------------------------------------------------------------------------------------//
	public native int  GetQualityScore (byte[] rawImage,int weith,int height);
	//------------------------------------------------------------------------------------------------//
}
