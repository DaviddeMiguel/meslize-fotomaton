package com.meslize.fotomaton.splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.meslize.fotomaton.MainActivity;
import com.meslize.fotomaton.R;
import com.meslize.fotomaton.util.Constants;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				if(!isFinishing()){
					startActivity(new Intent(getBaseContext(), MainActivity.class));
				}
			}
		}, Constants.TIME_SPLASH);
	}
}
