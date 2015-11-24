package com.yabtogo.ip_camera_c;

import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.ActionBarActivity;

public class IPCameraMain extends ActionBarActivity {
	
	private Button btnCamera, btnMonitor;
	private TextView tv;
	private Timer timer;
	private boolean cameraModeAllow = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN ,WindowManager.LayoutParams. FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ipcamera_main);
		
		int apiLevel = Integer.valueOf(android.os.Build.VERSION.SDK_INT);
		Toast.makeText(getApplicationContext(), "API Level : " + Integer.toString(apiLevel), Toast.LENGTH_SHORT).show();
		if(apiLevel < 14){ //API 14 = Android 4.0
			cameraModeAllow = false;
		}
		
		initUi();
		initEvent();
	}
	
	private void initUi() {
		btnCamera = (Button) findViewById(R.id.btnCamera);
		btnMonitor = (Button) findViewById(R.id.btnMonitor);
		tv = (TextView)findViewById(R.id.tv);

		// 取螢幕解析度大小
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		// 螢幕解析度大小為 metrics.widthPixels * metrics.heightPixels

		btnCamera.setHeight(metrics.heightPixels / 3);
		btnCamera.setWidth(metrics.widthPixels);
		btnMonitor.setHeight(metrics.heightPixels / 3);
		btnMonitor.setWidth(metrics.widthPixels);
		
		if(!cameraModeAllow){
			btnCamera.setText(R.string.api_level_have_to_greater_than_14);
			btnCamera.setEnabled(false);
		}
		
	}
	
	private void initEvent(){
		btnCamera.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// into Camera Mode
				Intent intent=new Intent();
				intent.setClass(IPCameraMain.this,CameraMode.class);
				startActivity(intent);
			}
		});
		
		btnMonitor.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// into Monitor Mode
				Intent intent=new Intent();
				intent.setClass(IPCameraMain.this,MonitorMode.class);
				startActivity(intent);
			}
		});
		
		tv.setOnLongClickListener(new OnLongClickListener() {//隱藏之功能(不檢查wifi,直接設成online狀態)
			@Override
			public boolean onLongClick(View v) {
				//stop the wifi check work
				if(timer != null){
					timer.cancel();
					timer.purge();
					timer = null;
				}
				//設成online狀態
				getSupportActionBar().setTitle(R.string.wifi_online);
				AppTools.setActionBarLogo(IPCameraMain.this, R.drawable.online);
				btnMonitor.setEnabled(true);
				if(cameraModeAllow){
					btnCamera.setEnabled(true);							
				}
				return true;//return true means consume this event, onClickListener will not get this event
			}
		});
	}
	
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			super.handleMessage(msg);
			switch(msg.what){
				case 1:
					if(checkWiFi()){
						getSupportActionBar().setTitle(R.string.wifi_online);
						AppTools.setActionBarLogo(IPCameraMain.this, R.drawable.online);
						btnMonitor.setEnabled(true);
						if(cameraModeAllow){
							btnCamera.setEnabled(true);							
						}
					} else{
						Toast.makeText(getApplicationContext(), R.string.wifi_not_connected, Toast.LENGTH_SHORT).show();
						getSupportActionBar().setTitle(R.string.wifi_offline);
						AppTools.setActionBarLogo(IPCameraMain.this, R.drawable.offline);
						btnCamera.setEnabled(false);
						btnMonitor.setEnabled(false);
					}
					break;
			}
		}
	};
	
	private TimerTask wifiStatusCheckTask;
	
	private void generateWifiStatusCheckTask(){
		if(wifiStatusCheckTask != null){
			wifiStatusCheckTask.cancel();
			wifiStatusCheckTask = null;
		}
		wifiStatusCheckTask = new TimerTask(){
			@Override
			public void run() {
				Message message = new Message();
				message.what = 1;
				handler.sendMessage(message);
			}
		};
	}
	
	private boolean checkWiFi(){
		String ip = AppTools.getIpAddress(this);
		String[] ipArr = ip.split("\\.");
		String lastSection = ipArr[3];
		if(lastSection.equals("0")){
			return false;//WiFi沒連上
		} else{
			return true;//WiFi連上
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.ipcamera_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case R.id.menu_show_ip:
				Toast.makeText(getApplicationContext(), AppTools.getIpAddress(this), Toast.LENGTH_SHORT).show();
				break;
			case R.id.menu_troubleshoot:
				showTroubleshootDialog();
				break;
			case R.id.menu_about:
				showAboutDialog();
				break;
			case R.id.menu_license:
				showLicenseDialog();
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void showLicenseDialog(){
		new AlertDialog.Builder(IPCameraMain.this)
		.setTitle(R.string.license)
		.setIcon(R.drawable.ic_launcher)
		.setMessage(R.string.license_content)
		.setPositiveButton(R.string.license_detail_info, new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialoginterface,int i){
				Uri uri=Uri.parse("http://www.gnu.org/licenses/gpl-3.0.en.html");
				Intent intent=new Intent(Intent.ACTION_VIEW,uri);
				startActivity(intent); 
			}
		})
		.setNeutralButton(R.string.license_source, new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialoginterface,int i){
				Uri uri=Uri.parse("https://github.com/iamzeroseven/ipCamera");
				Intent intent=new Intent(Intent.ACTION_VIEW,uri);
				startActivity(intent); 
			}
		})
		.setNegativeButton(R.string.back, new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialoginterface,int i){
			}
		})
		.show();
	}
	
	private void showAboutDialog(){
		new AlertDialog.Builder(IPCameraMain.this)
		.setTitle(R.string.about_title)
		.setIcon(R.drawable.ic_launcher)
		.setMessage(R.string.about_content)
		.setPositiveButton(R.string.about_contact_author, new DialogInterface.OnClickListener(){  //聯絡作者
			public void onClick(DialogInterface dialoginterface,int i){
				Intent emailContact=new Intent();
				emailContact.setAction(Intent.ACTION_SENDTO);
				emailContact.setData(Uri.parse("mailto:iamzeroseven@gmail.com"));
				emailContact.putExtra(Intent.EXTRA_SUBJECT,"Contact Author_ipCamera_C");
				startActivity(Intent.createChooser(emailContact,"Send EMail"));
			}
		})
		.setNegativeButton(R.string.back, new DialogInterface.OnClickListener(){  //返回程式
			public void onClick(DialogInterface dialoginterface,int i){
			}
		})
		.show();
	}
	
	private void showTroubleshootDialog(){
		// into Troubleshoot Content
		Intent intent=new Intent();
		intent.setClass(IPCameraMain.this,TroubleshootContent.class);
		startActivity(intent);
	}
	
	private void showExitDialog(){
		new AlertDialog.Builder(IPCameraMain.this)
		.setTitle(R.string.exit_title)
		.setIcon(R.drawable.ic_launcher)
		.setMessage(R.string.exit_content)
		.setPositiveButton(R.string.exit_sure,new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialoginterface,int i){
				finish();
			}
		})
		.setNegativeButton(R.string.exit_cancel,new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialoginterface,int i){
			}
		})
		.show();
	}
	
	@Override
	public void onBackPressed(){
		showExitDialog();
	}
	
	
	
	@Override
	protected void onStart(){
		super.onStart();
		
		//start the wifi check work
		if(timer == null){
			timer = new Timer();
			generateWifiStatusCheckTask();
			timer.schedule(wifiStatusCheckTask, 0, 8000);
		}
		
	}
	
	@Override
	protected void onStop(){
		//stop the wifi check work
		if(timer != null){
			timer.cancel();
			timer.purge();
			timer = null;
		}
		super.onStop();
	}
	
	@Override
	protected void onDestroy(){  //結束主程式
		if(timer != null){
			timer.cancel();
			timer = null;
		}
		super.onDestroy();
//		android.os.Process.killProcess(android.os.Process.myPid());
	}
}
