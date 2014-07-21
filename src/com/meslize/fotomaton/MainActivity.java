package com.meslize.fotomaton;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getSupportActionBar().setTitle("");
		
		if(Build.VERSION.SDK_INT <= 10){
			getSupportActionBar().setLogo(R.drawable.ic_logo);
		}
			
		if (getSupportFragmentManager().findFragmentByTag(MainFragment.class.getName()) == null) {
			final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.add(android.R.id.content, new MainFragment(), MainFragment.class.getName());

			ft.commit();
		}
	}
}
