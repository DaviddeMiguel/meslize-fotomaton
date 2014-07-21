package com.meslize.fotomaton.detail.result;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.meslize.fotomaton.R;

public class DetailResultActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getSupportActionBar().setHomeButtonEnabled(true);
//		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		if(Build.VERSION.SDK_INT <= 10){
			getSupportActionBar().setLogo(R.drawable.ic_action_cancel);
		}
		
		if (getSupportFragmentManager().findFragmentByTag(DetailResultFragment.class.getName()) == null) {
			final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.add(android.R.id.content, new DetailResultFragment(), DetailResultFragment.class.getName());

			ft.commit();
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
		case android.R.id.home:			
			finish();
			
			break;
		}
    	
    	return super.onOptionsItemSelected(item);
	}
}
