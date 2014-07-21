package com.meslize.fotomaton.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.framework.library.util.AppIntent;
import com.meslize.fotomaton.R;
import com.meslize.fotomaton.detail.result.DetailResultActivity;
import com.meslize.fotomaton.model.Path;
import com.meslize.fotomaton.util.Constants;
import com.meslize.fotomaton.view.PictureView;

public class DetailFragment extends Fragment{
	
	private DetailFragmentController controller;
	
	private PictureView pictureView;
	
	private String path;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		path = getActivity().getIntent().getStringExtra(AppIntent.EXTRA_URI);

		controller = new DetailFragmentController(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		
		final View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
		pictureView = (PictureView) rootView.findViewById(R.id.image);

		if(!TextUtils.isEmpty(path)){
			pictureView.load(path);
		}
		
		return rootView;
	}
	
	public void saveCutPicture(){				
		Path path = controller.saveCutPicture(pictureView.getAreaDesired(), pictureView.getBitmap());
		
		if(path != null){
			Intent intent = new Intent(getActivity(), DetailResultActivity.class);
			intent.putExtra(Constants.EXTRA_PATH, path);
			
			startActivity(intent);
		}else{
			Toast.makeText(getActivity(), R.string.info_image_area_not_valid, Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.fragment_detail, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.action_save:
	        	saveCutPicture();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
}
