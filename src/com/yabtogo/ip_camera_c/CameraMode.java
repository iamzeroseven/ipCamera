package com.yabtogo.ip_camera_c;

import net.majorkernelpanic.streaming.SessionBuilder;
import net.majorkernelpanic.streaming.gl.SurfaceView;
import net.majorkernelpanic.streaming.rtsp.RtspServer;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class CameraMode extends ActionBarActivity {
	private TextView tvCode;
	private SurfaceView mSurfaceView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN ,WindowManager.LayoutParams. FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.camera_mode);
		
		AppTools.setActionBarLogo(this, R.drawable.camera_with_back);
		
		tvCode = (TextView)findViewById(R.id.tvCode);
		mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);
		
		showCode();
		
		// Sets the port of the RTSP server to 2025
		Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
		editor.putString(RtspServer.KEY_PORT, String.valueOf(AppTools.PORT));
		editor.commit();
		
		// Configures the SessionBuilder
		SessionBuilder.getInstance()
		.setSurfaceView(mSurfaceView)
		.setPreviewOrientation(90)
		.setContext(getApplicationContext())
		//.setAudioEncoder(SessionBuilder.AUDIO_NONE)
		.setAudioEncoder(SessionBuilder.AUDIO_AAC)
		.setVideoEncoder(SessionBuilder.VIDEO_H264);
		
		// Starts the RTSP server 
		// example : context.startService(new Intent(this,RtspServer.class));
		this.startService(new Intent(this,RtspServer.class));
		
		// Stops the RTSP server
		// example : context.stopService(new Intent(this,RtspServer.class));
	}
	
	private void showCode(){
		String ip = AppTools.getIpAddress(this);
		String[] ipArr = ip.split("\\.");
		tvCode.setText(ipArr[3]);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.cam_mon, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case R.id.menu_show_ip:
				Toast.makeText(getApplicationContext(), AppTools.getIpAddress(this), Toast.LENGTH_SHORT).show();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onDestroy(){
		this.stopService(new Intent(this,RtspServer.class)); // Stops the RTSP server
		super.onDestroy();
	}
}
