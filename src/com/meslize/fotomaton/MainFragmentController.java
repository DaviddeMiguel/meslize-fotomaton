package com.meslize.fotomaton;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.framework.library.controller.Controller;
import com.framework.library.util.AppIntent;
import com.framework.library.util.Constants.AppType;
import com.framework.library.util.Functions;
import com.meslize.fotomaton.db.FDao;
import com.meslize.fotomaton.model.Path;

public class MainFragmentController extends Controller {

	public static final String METHOD_GET_PATHS_LIST_BACKGROUND = "getPathsListBackground";
	public static final String METHOD_GET_PATHS_LIST_BACKGROUND_RESULT = "getPathsListBackgroundResult";
	public static final String METHOD_GET_PATHS_LIST_BACKGROUND_ERROR = "getPathsListBackgroundError";
	
	private UI ui;
	
	private Intent intentPickImage;
	
	private List<Path> paths;
	
	public MainFragmentController(Fragment fragment) {
		super(fragment);
		ui = (UI) fragment;
	}

	public void launchPickImage(){
		intentPickImage = openApp(AppType.CHOOSER_IMAGE, null);
	}
	
	public void onActivityResult(Intent intent){
    	if(intent.getAction().equals(AppIntent.INTENT_GALLERY) ||
	       intent.getAction().equals(AppIntent.INTENT_CAMERA) || 
	       intent.getAction().equals(AppIntent.INTENT_PICK_IMAGE)){
		
    		if (intent.getIntExtra(AppIntent.RESULT_CODE, -2) == Activity.RESULT_OK) {
				Uri uri = null;
				
				if(intent.getParcelableExtra(AppIntent.EXTRA_URI) != null){
					uri = (Uri) intent.getParcelableExtra(AppIntent.EXTRA_URI);
				}else if(intentPickImage != null && intentPickImage.getParcelableExtra(AppIntent.EXTRA_URI) != null){
					uri = (Uri) intentPickImage.getParcelableExtra(AppIntent.EXTRA_URI);
				}
	        	
		        if(uri != null){
		        	String path = Functions.getRealPathFromUri(getActivity(), uri);
		        	ui.imagePath(path);
		        }else{
		        	ui.imageFailed();
		        }
	        } else if (intent.getIntExtra(AppIntent.RESULT_CODE, -2) == Activity.RESULT_CANCELED) {
	        	ui.imageCancelled();
	        } else {
	        	ui.imageFailed();
	        }
		}
	}
	
	public void galleryAddPicture(String path) {
	    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
	    File f = new File(path);
	    Uri contentUri = Uri.fromFile(f);
	    mediaScanIntent.setData(contentUri);
	    getContext().sendBroadcast(mediaScanIntent);
	}
	
	public void getPathsList(){
		runInBackground(METHOD_GET_PATHS_LIST_BACKGROUND, this, new Object[] {}, new Class[]{}, METHOD_GET_PATHS_LIST_BACKGROUND_RESULT, METHOD_GET_PATHS_LIST_BACKGROUND_ERROR);
	}
	
	public List<Path> getPathsListBackground(){
		List<Path> result = null;
		
		FDao dao = new FDao(getContext());
		
		try{
			result = dao.getPaths();
		}catch (Exception e) {
			Functions.log(e);
		}finally{
			dao.close();
		}
		
		return result;
	}
	
	public void getPathsListBackgroundResult(List<Path> paths){
		this.paths = paths;
		
		ui.historyLoaded();
	}
	
	public void getPathsListBackgroundError(){
		Toast.makeText(getContext(), R.string.info_image_history_error, Toast.LENGTH_SHORT).show();
	}

	public interface UI{
		public void historyLoaded();
		public void imagePath(String path);
		public void imageCancelled();
		public void imageFailed();
	}

	public Intent getIntentPickImage() {
		return intentPickImage;
	}

	public void setIntentPickImage(Intent intentPickImage) {
		this.intentPickImage = intentPickImage;
	}

	public List<Path> getPaths() {
		return paths;
	}

	public void setPaths(List<Path> paths) {
		this.paths = paths;
	}
}
