package com.meslize.fotomaton.detail.result;

import java.io.File;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.framework.library.view.ManagedImageView;
import com.meslize.fotomaton.MainActivity;
import com.meslize.fotomaton.R;
import com.meslize.fotomaton.model.Path;
import com.meslize.fotomaton.util.Constants;

public class DetailResultFragment extends Fragment{
	
	private DetailResultFragmentController controller;
	
	private ShareActionProvider mShareActionProvider;
	
	private ManagedImageView imageViewResult;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		controller = new DetailResultFragmentController(this);
		controller.setPath((Path)getActivity().getIntent().getSerializableExtra(Constants.EXTRA_PATH));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		
		final View rootView = inflater.inflate(R.layout.fragment_detail_result, container, false);
		imageViewResult = (ManagedImageView) rootView.findViewById(R.id.image_result);
		
		if(controller.getPath() != null && !TextUtils.isEmpty(controller.getPath().getPath())){
			imageViewResult.load(controller.getPath().getPath());
		}		
		
		return rootView;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.fragment_detail_result, menu);
		
	    MenuItem shareItem = menu.findItem(R.id.action_share);
	    mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
	    mShareActionProvider.setShareIntent(createShareImageIntent());
		
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.action_accept:
	        	controller.savePath();
	        	
	        	Intent intent = new Intent(getActivity(), MainActivity.class);
	        	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        	intent.putExtra(Constants.EXTRA_UPDATE, true);
	        	startActivity(intent);
	            return true;
	        case R.id.action_share:
	            return false;    
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private Intent createShareImageIntent() {
	    Intent shareIntent = new Intent(Intent.ACTION_SEND);
	    shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
	    shareIntent.setType("image/*");

	    if(controller.getPath() != null && !TextUtils.isEmpty(controller.getPath().getPath())){
		    Uri uri = Uri.fromFile(new File(controller.getPath().getPath()));
		    shareIntent.putExtra(Intent.EXTRA_STREAM, uri);	
	    }
	    
	    return shareIntent;
	} 
}
