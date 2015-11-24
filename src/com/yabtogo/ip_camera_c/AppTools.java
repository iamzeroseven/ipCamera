package com.yabtogo.ip_camera_c;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.ActionBarActivity;

public class AppTools {
	public static final String URL_PROTOCOL = "rtsp://";
	public static final String URL_PORT = ":2025";
	public static final int PORT = 2025;
	
	/**
	 * 設定ActionBar上的圖示
	 */
	public static void setActionBarLogo(ActionBarActivity activity, int logo){
		activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
		activity.getSupportActionBar().setDisplayUseLogoEnabled(true);
		activity.getSupportActionBar().setLogo(logo);
	}
	
	/**
	 * 取IP address
	 */
	public static String getIpAddress(Activity activity){
		WifiManager wifiManager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int address = wifiInfo.getIpAddress();// 獲取IP地址，注意獲取的結果是整數
		String ip = ipIntToString(address);
		return ip;
	}
	
	/**
	 * 整數IP轉字串
	 */
	private static String ipIntToString(int ip) {
		return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF)
				+ "." + ((ip >> 24) & 0xFF);
	}
}
