package xyz.hydai.demo;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParsePush;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity {
	private EditText nicknameEditText;
	private Button loginButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		//Initialize parse.com API
		Parse.initialize(this, "iRoefRdyneKu2fNFQHRgssrJ6MAPXmVXaqISyxYR",
				"9W1h0Ibc23nCclCvDO72gd2pAfVa2TOtveSrHUly");
		ParseInstallation.getCurrentInstallation().saveInBackground();
		ParsePush.subscribeInBackground("all");
		
		//Get instance
		nicknameEditText = (EditText) findViewById(R.id.editTextLogin);
		loginButton = (Button) findViewById(R.id.button2);
		
		loginButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				login();
			}
		});
		
		nicknameEditText.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View view, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
					login();
					return true;
				}
				return false;
			}
		});
	}
	
	private void login() {
		String nickname = nicknameEditText.getText().toString();
		Intent intent = new Intent();
		intent.setClass(LoginActivity.this, MainActivity.class);
		intent.putExtra("nickname", nickname);
		startActivity(intent);
	}
}
