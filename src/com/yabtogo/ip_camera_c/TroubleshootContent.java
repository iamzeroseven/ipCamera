package com.yabtogo.ip_camera_c;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;

public class TroubleshootContent extends ActionBarActivity {
	
	private Button btnContactAuthor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN ,WindowManager.LayoutParams. FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.troubleshoot_content);
		
		btnContactAuthor = (Button)findViewById(R.id.btnContactAuthor);
		btnContactAuthor.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent emailContact=new Intent();
				emailContact.setAction(Intent.ACTION_SENDTO);
				emailContact.setData(Uri.parse("mailto:iamzeroseven@gmail.com"));
				emailContact.putExtra(Intent.EXTRA_SUBJECT,"Contact Author_ipCamera_C");
				startActivity(Intent.createChooser(emailContact,"Send EMail"));
			}
		});
	}
}
