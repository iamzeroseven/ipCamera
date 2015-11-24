package com.yabtogo.ip_camera_c;

import java.io.IOException;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MonitorMode extends ActionBarActivity {
	
	private EditText etIp;
	private ImageButton btnPlay;
	
	private MediaPlayer mediaPlayer;
	private ProgressBar pbProgressBar;
	private SurfaceView sv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN ,WindowManager.LayoutParams. FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.monitor_mode);
		
		AppTools.setActionBarLogo(this, R.drawable.monitor_with_back);
		
		etIp = (EditText)findViewById(R.id.etIp);
		btnPlay = (ImageButton)findViewById(R.id.btnPlay);
		pbProgressBar = (ProgressBar)findViewById(R.id.pbProgressBar);
		sv = (SurfaceView)findViewById(R.id.surfaceView);
		
		btnPlay.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(!checkInput()){
					Toast.makeText(getApplicationContext(), R.string.invalid_input, Toast.LENGTH_SHORT).show();
					return;
				}
				try {
					String url = getURL();
					if(mediaPlayer == null){
						mediaPlayer = new MediaPlayer();
					}
					mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
					mediaPlayer.setDataSource(url);
					mediaPlayer.setDisplay(sv.getHolder());
					mediaPlayer.prepareAsync();
					mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
						@Override
						public void onPrepared(MediaPlayer mp) {
							mp.start();
						}
					});
					mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
						@Override
						public void onCompletion(MediaPlayer mp) {
							mp.reset();
							pbProgressBar.setVisibility(View.INVISIBLE);
							btnPlay.setVisibility(View.VISIBLE);
						}
					});
					mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
						@Override
						public boolean onError(MediaPlayer mp, int what, int extra) {
							mp.reset();
							pbProgressBar.setVisibility(View.INVISIBLE);
							btnPlay.setVisibility(View.VISIBLE);
							return false;
						}
					});
					hideSoftKeyboard();
					pbProgressBar.setVisibility(View.VISIBLE);
					btnPlay.setVisibility(View.INVISIBLE);
					Toast.makeText(getApplicationContext(), url, Toast.LENGTH_SHORT).show();
				} catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
					Toast.makeText(getApplicationContext(), R.string.execution_error, Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
	private String getURL(){
		String url = AppTools.URL_PROTOCOL + getIpSection123() + etIp.getText().toString() + AppTools.URL_PORT;
		return url;
	}
	
	private boolean checkInput(){
		String input = etIp.getText().toString();
		if(input == null || input.equals("")){
			return false;
		}
		int ipPart4 = Integer.parseInt(input);
		if(ipPart4 < 1 || ipPart4 >255){
			return false;
		}
		return true;
	}
	
	/**
	 * 取IP前三段
	 */
	private String getIpSection123() {
		String ip = AppTools.getIpAddress(this);
		String[] ipParts = ip.split("\\.");
		return ipParts[0] + "." + ipParts[1] + "." + ipParts[2] + ".";
	}
	
	private void hideSoftKeyboard() {
		InputMethodManager inputMethodManager = (InputMethodManager)  this.getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);	   
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
	protected void onStop(){
		if(mediaPlayer != null){
			if(mediaPlayer.isPlaying()){
				mediaPlayer.stop();
			}
			mediaPlayer.reset();
			mediaPlayer.release();
			mediaPlayer = null;
			
			pbProgressBar.setVisibility(View.INVISIBLE);
			btnPlay.setVisibility(View.VISIBLE);
		}
		super.onStop();
	}
	
	@Override
	protected void onDestroy(){ // 再檢查一次，因為有時候不知為何onStop裡面不會去釋放mediaPlayer(ASUS手機測試)
		if(mediaPlayer != null){
			if(mediaPlayer.isPlaying()){
				mediaPlayer.stop();
			}
			mediaPlayer.reset();
			mediaPlayer.release();
			mediaPlayer = null;
		}
		super.onDestroy();
	}

}
