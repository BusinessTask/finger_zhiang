package com.ZHIANG;

import com.ZHIANG.HostUsb;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.util.Log;

public class FPAPI {
    static final String TAG = "FPAPI";
    //****************************************************************************************************
	static 
	{
		try{
			System.loadLibrary("ZAZ_FpStdAPI");
		}
		catch(UnsatisfiedLinkError e) {
			Log.e("FPAPI","ZAZ_FpStdAPI",e);
		}
	}
	//****************************************************************************************************
	public static final int VID = 0x28E9;//0x0483;//
	public static final int PID = 0x2018;// 0x5710;//
	private static HostUsb m_usbHost = null;
	private static int m_hUSB = 0;
    public static final int MSG_OPEN_DEVICE = 0x10;
    public static final int MSG_CLOSE_DEVICE = 0x11;
    public static final int MSG_BULK_TRANS_IN = 0x12;
    public static final int MSG_BULK_TRANS_OUT = 0x13;
	//****************************************************************************************************
	public static final int WIDTH  = 256;
	public static final int HEIGHT  = 360;
	public static final int IMAGE_SIZE = WIDTH*HEIGHT;
    //****************************************************************************************************
	public static final int FPINFO_STD_MAX_SIZE = 1024;
    public static final int DEF_FINGER_SCORE = 50;
    public static final int DEF_QUALITY_SCORE = 50;
    public static final int DEF_MATCH_SCORE = 45;
    //****************************************************************************************************
    public static final int TRUE = 1;
    public static final int FALSE = 0;
    //****************************************************************************************************
    private static Activity m_content = null;
    //****************************************************************************************************
	private static int CallBack (int message, int notify, int param, Object data)
	{
		switch (message) {
		case MSG_OPEN_DEVICE:
			m_usbHost = new HostUsb ();
			if (!m_usbHost.AuthorizeDevice(m_content,VID, PID))
			{
				m_usbHost = null;
				return 0;
			}

			m_usbHost.WaitForInterfaces(); 
		    m_hUSB = m_usbHost.OpenDeviceInterfaces();
			if (m_hUSB<0) {
				m_usbHost = null;
				return 0;
			}
			return m_hUSB;
		case MSG_CLOSE_DEVICE:
			if (m_usbHost != null) {
				m_usbHost.CloseDeviceInterface();
				m_hUSB = -1;
				m_usbHost = null;
			}
			return 1;
		case MSG_BULK_TRANS_IN:
			if (m_usbHost.USBBulkReceive((byte[])data,notify,param)) return notify;
			return 0;
		case MSG_BULK_TRANS_OUT:
			if (m_usbHost.USBBulkSend((byte[])data,notify,param)) return notify;
			return 0;
		}
		return 0;
	}
    //****************************************************************************************************
	public FPAPI(Activity a) {
		m_content = a;
	}
	//****************************************************************************************************
	//------------------------------------------------------------------------------------------------//
	// Purpose   : This function initializes the Fingerprint Recognition SDK Library and 
	//				connects Fingerprint Collection Module.
 	// Function  : OpenDevice
	// Arguments : void
	// Return    : int  
	//			     If successful, return handle of device, else 0. 	
	//------------------------------------------------------------------------------------------------//
	public native int OpenDevice();
	//------------------------------------------------------------------------------------------------//
	// Purpose   : This function finalizes the Fingerprint Recognition SDK Library and 
	//				disconnects Fingerprint Collection Module.
 	// Function  : CloseDevice
	// Arguments : 
	//      (In) : int device : handle returned from function "OpenDevice()"
	// Return    : int
	//			      If successful, return 1, else 0 	
	//------------------------------------------------------------------------------------------------//
	public native int CloseDevice(int device);
	//------------------------------------------------------------------------------------------------//
	// Purpose   : This function does calibration of the Fingerprint Collection Module.
	// Function  : Calibration
	// Arguments : 
	//      (In) : int device : handle returned from function "OpenDevice()"
	//      (In) : int mode : for dry/default/wet
	// Return    :  
	//			   int :   If successful, return 1, else 0 	
	//------------------------------------------------------------------------------------------------//
	public native int Calibration(int device, int mode);
	//------------------------------------------------------------------------------------------------//
	// Purpose   : This function returns image captured from Fingerprint Collection Module.
	// Function  : GetImage
	// Arguments : 
	//      (In) : int device : handle returned from function "OpenDevice()"
	//  (In/Out) : byte[] image : image captured from this device
	// Return    : int
	//			      If successful, return 1, else 0 	
	//------------------------------------------------------------------------------------------------//
	public native int GetImage(int device, byte[] image);
	//------------------------------------------------------------------------------------------------//
	// Purpose   : This function checks whether finger is on sensor of this device or not.
	// Function  : IsFinger
	// Arguments : 
	//      (In) : int device : handle returned from function "OpenDevice()"
	//		(In) : byte[] image : image returned from function "GetImage()"
	// Return    : int 
	//				   return percent value indicating that finger is placed on sensor(0~100). 	
	//------------------------------------------------------------------------------------------------//
	public native int IsFinger(int device,byte[] image);
	//------------------------------------------------------------------------------------------------//
	// Purpose   : This function gets the quality value of fingerprint raw image. 
	// Function  : GetImageQuality
	// Arguments : 
	//      (In) : int device : handle returned from function "OpenDevice()"
	//		(In) : byte[] image : image returned from function "GetImage()"
	// Return    : int : 
	//				   return quality value(0~100) of fingerprint raw image. 	
	//------------------------------------------------------------------------------------------------//
	public native int GetImageQuality(int device,byte[] image);
	//------------------------------------------------------------------------------------------------//
	// Purpose   : This function gets the NFI quality value of fingerprint raw image. 
	// Function  : GetNFIQuality
	// Arguments : 
	//      (In) : int device : handle returned from function "OpenDevice()"
	//		(In) : byte[] image : image returned from function "GetImage()"
	// Return    : int : 
	//				   return NFI quality value(1~5) of fingerprint raw image. 	
	//------------------------------------------------------------------------------------------------//
	public native int GetNFIQuality(int device,byte[] image);
	//------------------------------------------------------------------------------------------------//
	// Purpose   : This function creates the ANSI standard template from the uncompressed raw image. 
	// Function  : CreateANSITemplate
	// Arguments : 
	//      (In) : int device : handle returned from function "OpenDevice()"
	//		(In) : byte[] image : image returned from function "GetImage()"
	//	(In/Out) : byte[] itemplate : ANSI standard template created from image.
	// Return    : int : 
	//				   If this function successes, return size of template, else 0. 	
	//------------------------------------------------------------------------------------------------//
	public native int CreateANSITemplate(int device,byte[] image, byte[] itemplate);
	//------------------------------------------------------------------------------------------------//
	// Purpose   : This function creates the ISO standard template from the uncompressed raw image. 
	// Function  : CreateISOTemplate
	// Arguments : void
	//      (In) : int device : handle returned from function "OpenDevice()"
	//		(In) : byte[] image : image returned from function "GetImage()"
	//  (In/Out) : byte[] itemplate : ISO standard template created from image.
	// Return    : int : 
	//				   If this function successes, return size of template, else 0. 	
	//------------------------------------------------------------------------------------------------//
	public native int CreateISOTemplate(int device,byte[] image,  byte[] itemplate);
	//------------------------------------------------------------------------------------------------//
	// Purpose   : This function matches two templates and returns similar match score.
	//             This function is for 1:1 Matching and only used in fingerprint verification. 
	// Function  : CompareTemplates
	// Arguments : 
	//      	(In) : int device : handle returned from function "OpenDevice()"
	//			(In) : byte[] itemplateToMatch : template to match : 
	//                 This template must be used as that is created by function "CreateANSITemplate()"  
	//                 or function "CreateISOTemplate()".
	//			(In) : byte[] itemplateToMatched : template to be matched
	//                 This template must be used as that is created by function "CreateANSITemplate()"  
	//                 or function "CreateISOTemplate()".
	// Return    : int 
	//					return similar match score(0~100) of two fingerprint templates.
	//------------------------------------------------------------------------------------------------//
	public native int CompareTemplates(int device,byte[] itemplateToMatch, byte[] itemplateToMatched);
	//------------------------------------------------------------------------------------------------//
	// Purpose   : This function matches the appointed ANSI template against to ANSI template array of DATABASE.
	//             This function is for 1:N Matching and only used in fingerprint identification. 
	// Function  : SearchingANSITemplates
	// Arguments : 
	//      	(In) : int device : handle returned from function "OpenDevice()"
	//			(In) : byte[] itemplateToSearch : template to search
	//                 This template must be used as that is created by function "CreateANSITemplate()".  
	//			(In) : int numberOfDbTemplates : number of templates to be searched.
	//			(In) : byte[] arrayOfDbTemplates : template array to be searched.
	//                 These templates must be used as that is created by function "CreateANSITemplate()".  
	//			(In) : int scoreThreshold : 
	//                 This argument is the threshold of similar match score for 1: N Matching.
	// Return    : int 
	//				   If successful, return index number of template searched inside template array, 
	//				   else -1. 	
	//------------------------------------------------------------------------------------------------//
	public native int SearchingANSITemplates(int device, byte[] itemplateToSearch, 
		   	int numberOfDbTemplates, byte[] arrayOfDbTemplates, int scoreThreshold);
	//------------------------------------------------------------------------------------------------//
	// Purpose   : This function matches the appointed ISO template against to ISO template array of DATABASE.
	//             This function is for 1:N Matching and only used in fingerprint identification. 
	// Function  : SearchingISOTemplates
	// Arguments : 
	//      	(In) : int device : handle returned from function "OpenDevice()"
	//			(In) : byte[] itemplateToSearch : template to search
	//                 This template must be used as that is created by function "CreateISOTemplate()".  
	//			(In) : int numberOfDbTemplates : number of templates to be searched.
	//			(In) : byte[] arrayOfDbTemplates : template array to be searched.
	//                 These templates must be used as that is created by function "CreateISOTemplate()".  
	//			(In) : int scoreThreshold : 
	//                 This argument is the threshold of similar match score for 1: N Matching.
	// Return    : int 
	//				   If successful, return index number of template searched inside template array, 
	//				   else -1. 	
	//------------------------------------------------------------------------------------------------//
	public native int SearchingISOTemplates(int device, byte[] itemplateToSearch, 
		   	int numberOfDbTemplates, byte[] arrayOfDbTemplates, int scoreThreshold);
	//------------------------------------------------------------------------------------------------//
	// Purpose   : This function gets the ANSI standard FIR from the uncompressed raw image.
	//			   FIR - Fingerprint Image Record
	// Function  : GetANSIImageRecord
	// Arguments : 
	//      (In) : int device : handle returned from function "OpenDevice()"
	//		(In) : byte[] image : raw image returned from function "GetImage()"
	//	(In/Out) : byte[] FIR : ANSI standard FIR created from raw image.
	// Return    : int : 
	//				   If this function successes, return size of FIR, else 0. 	
	//------------------------------------------------------------------------------------------------//
	public native int GetANSIImageRecord(int device,byte[] image, byte[] FIR);
	//------------------------------------------------------------------------------------------------//
	// Purpose   : This function gets the ISO standard FIR from the uncompressed raw image.
	//			   FIR - Fingerprint Image Record
	// Function  : GetISOImageRecord
	// Arguments : 
	//      (In) : int device : handle returned from function "OpenDevice()"
	//		(In) : byte[] image : raw image returned from function "GetImage()"
	//	(In/Out) : byte[] FIR : ISO standard FIR created from raw image.
	// Return    : int : 
	//				   If this function successes, return size of FIR, else 0. 	
	//------------------------------------------------------------------------------------------------//
	public native int GetISOImageRecord(int device,byte[] image, byte[] FIR);
	//------------------------------------------------------------------------------------------------//
	// Purpose   : This function compresses raw fingerprint image by WSQ algorithm
	// Function  : CompressToWSQImage
	// Arguments : 
	//      (In) : int device : handle returned from function "OpenDevice()"
	//		(In) : byte[] rawImage : fingerprint raw image
	//	(In/Out) : byte[] wsqImage : fingerprint image to be compressed by WSQ algorithm
	// Return    : long 
	//					return size of image compressed by WSQ
	//------------------------------------------------------------------------------------------------//
	public native long  CompressToWSQImage (int device,byte[] rawImage, byte[] wsqImage);
	//------------------------------------------------------------------------------------------------//
	// Purpose   : This function uncompresses wsq fingerprint image by WSQ algorithm
	// Function  : UnCompressFromWSQImage
	// Arguments : 
	//      (In) : int device : handle returned from function "OpenDevice()"
	//		(In) : byte[] wsqImage : compressed fingerprint image
	//		(In) : long wsqSize : compressed image size
	//	(In/Out) : byte[] rawImage : fingerprint image to be uncompressed
	// Return    : long 
	//				return size of uncompressed image
	//------------------------------------------------------------------------------------------------//
	public native long  UnCompressFromWSQImage (int device,byte[] wsqImage, long wsqSize, byte[] rawImage);
	//------------------------------------------------------------------------------------------------//
	


	  
    public static Bitmap getTransparentBitmap(Bitmap sourceImg, int number,int color){
		int[] argb = new int[sourceImg.getWidth() * sourceImg.getHeight()]; 
		sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0, sourceImg 
				.getWidth(), sourceImg.getHeight());// 获得图片的ARGB值 
		//number = number * 255 / 100;
		int alpha = 0; // 图片透明度
		for (int i = 0; i < argb.length; i++) {
			 if (colorInRange(argb[i])){
				 argb[i] = 0;
				 alpha = 0;
             }else{
                  // 设置为不透明 
            	 argb[i] = color;
                  alpha = 255;
              }
			 argb[i] = (alpha << 24) | (argb[i] & 0x00FFFFFF); 
		} 
		sourceImg = Bitmap.createBitmap(argb, sourceImg.getWidth(), sourceImg 
				.getHeight(), Config.ARGB_8888); 
		return sourceImg;
    }
 // 判断是背景还是内容
        public static boolean colorInRange(int color) {
             int red = (color & 0xff0000) >> 16;// 获取color(RGB)中R位
             int green = (color & 0x00ff00) >> 8;// 获取color(RGB)中G位
             int blue = (color & 0x0000ff);// 获取color(RGB)中B位
             // 通过RGB三分量来判断当前颜色是否在指定的颜色区间内
             if (red >= color_range && green >= color_range && blue >= color_range){
                 return true;
             };
             return false;
         }
         
        //色差范围0~255
        public static int color_range = 210;
	
	
	
	
}
