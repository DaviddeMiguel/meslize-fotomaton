package com.meslize.fotomaton;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.framework.library.util.AppIntent;
import com.meslize.fotomaton.MainFragmentController.UI;
import com.meslize.fotomaton.detail.DetailActivity;
import com.meslize.fotomaton.detail.result.DetailResultActivity;
import com.meslize.fotomaton.util.Constants;

public class MainFragment extends Fragment implements UI{
	
	private MainFragmentController controller;
	
	private GridView listview;
	
	private PathsAdapter adapter;
	
	private ActionMode mActionMode;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		controller = new MainFragmentController(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		
		final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		rootView.findViewById(R.id.button_create_image).setOnClickListener(createImageClickListener);
		listview = (GridView) rootView.findViewById(R.id.listview);
		
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		
		boolean update = getActivity().getIntent().getBooleanExtra(Constants.EXTRA_UPDATE, false);
		
		if(adapter == null || update){
			controller.getPathsList();	
		}
	}

	private OnClickListener createImageClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			controller.launchPickImage();
		}
	};

	@Override
	public void historyLoaded() {
		if(getActivity() == null){
			return;
		}
		
		adapter = new PathsAdapter(getActivity().getLayoutInflater(), controller.getPaths());
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(itemClickListener);
		listview.setOnItemLongClickListener(itemLongClickListener);
	} 

	@Override
	public void imagePath(String path) {
		if(getActivity() == null){
			return;
		}
		
		Intent intent = new Intent(getActivity(), DetailActivity.class);
		intent.putExtra(AppIntent.EXTRA_URI, path);
		startActivity(intent);
	}

	@Override
	public void imageCancelled() {
		if(getActivity() == null){
			return;
		}
		
		Toast.makeText(getActivity(), R.string.info_image_cancel, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void imageFailed() {
		if(getActivity() == null){
			return;
		}
		
		Toast.makeText(getActivity(), R.string.info_image_failed, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		Intent intent = null;
		
		if(requestCode == AppIntent.RESULT_GALLERY){
			intent = new Intent(AppIntent.INTENT_GALLERY);
			intent.putExtra(AppIntent.EXTRA_URI, data != null ? data.getData() : null);
			intent.putExtra(AppIntent.REQUEST_CODE, requestCode);
			intent.putExtra(AppIntent.RESULT_CODE, resultCode);
		}else if(requestCode == AppIntent.RESULT_CAMERA){
			intent = new Intent(AppIntent.INTENT_CAMERA);
			intent.putExtra(AppIntent.EXTRA_URI, data != null ? data.getData() : null);
			intent.putExtra(AppIntent.REQUEST_CODE, requestCode);
			intent.putExtra(AppIntent.RESULT_CODE, resultCode);
		}else if(requestCode == AppIntent.RESULT_PICK_IMAGE){
			intent = new Intent(AppIntent.INTENT_PICK_IMAGE);
			intent.putExtra(AppIntent.EXTRA_URI, data != null ? data.getData() : null);
			intent.putExtra(AppIntent.REQUEST_CODE, requestCode);
			intent.putExtra(AppIntent.RESULT_CODE, resultCode);
		}
		
		if(intent != null){
			controller.onActivityResult(intent);
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.fragment_main, menu);
		
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.action_share:
	        	startActivity(createShareTextIntent());
	            return true;  
	            
            case R.id.action_good:
        	Intent intent = new Intent(Intent.ACTION_VIEW);
        	intent.setData(Uri.parse("market://details?id=" + getActivity().getPackageName()));
        	startActivity(intent);
            return true; 
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private Intent createShareTextIntent() {
		Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

		shareIntent.putExtra(Intent.EXTRA_SUBJECT, String.format(getString(R.string.share_title), getString(R.string.app_name)));
		shareIntent.putExtra(Intent.EXTRA_TEXT, String.format(getString(R.string.share_text), "http://play.google.com/store/apps/details?id=" + getActivity().getPackageName()));

	    return shareIntent;
	}
	
	private OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long position2) {
			if(mActionMode == null){
				Intent intent = new Intent(getActivity(), DetailResultActivity.class);
				intent.putExtra(Constants.EXTRA_PATH, controller.getPaths().get(position));
				
				startActivity(intent);
	        }else{
	            // add or remove selection for current list item
	            onListItemSelect(position);
	        }
		}
	};
	
	private OnItemLongClickListener itemLongClickListener = new OnItemLongClickListener() {
		 
        public boolean onItemLongClick(AdapterView<?> parent,
                View view, int position, long id) {
            onListItemSelect(position);
            return true;
        }
	};
	
    private void onListItemSelect(int position) {
    	adapter.toggleSelection(position);
        boolean hasCheckedItems = adapter.getSelectedCount() > 0;
 
        if (hasCheckedItems && mActionMode == null){
            // there are some selected items, start the actionMode
        	if(getActivity() instanceof ActionBarActivity){
        		mActionMode = ((ActionBarActivity)getActivity()).startSupportActionMode(new ActionModeCallback());
        	}
        }else if (!hasCheckedItems && mActionMode != null){
            // there no selected items, finish the actionMode
            mActionMode.finish();
        }
        
        if (mActionMode != null){
            mActionMode.setTitle(String.valueOf(adapter.getSelectedCount()) + " " + getString(R.string.delete_title));
        }
    }
    
    private class ActionModeCallback implements ActionMode.Callback {
    	 
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.fragment_main_context, menu);
            return true;
        }
 
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }
 
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
 
            switch (item.getItemId()) {
            case R.id.action_delete:
                // retrieve selected items and delete them out
                SparseBooleanArray selected = adapter.getSelectedIds();
                for (int i = (selected.size() - 1); i >= 0; i--) {
                    if (selected.valueAt(i)) {
                    	/*
                        Laptop selectedItem = adapter
                                .getItem(selected.keyAt(i));
                        adapter.remove(selectedItem);
                    	*/
                    }
                }
                
                mode.finish(); // Action picked, so close the CAB
                return true;
            default:
                return false;
            }
 
        }
 
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            // remove selection
        	adapter.removeSelection();
            mActionMode = null;
        }
    }
}
