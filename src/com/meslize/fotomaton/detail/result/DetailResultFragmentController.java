package com.meslize.fotomaton.detail.result;

import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.framework.library.controller.Controller;
import com.framework.library.util.Functions;
import com.meslize.fotomaton.R;
import com.meslize.fotomaton.db.FDao;
import com.meslize.fotomaton.model.Path;

public class DetailResultFragmentController extends Controller {
	
	public static final String METHOD_SAVE_PATH_BACKGROUND = "savePathBackground";
	public static final String METHOD_SAVE_PATH_BACKGROUND_RESULT = "savePathBackgroundResult";
	public static final String METHOD_SAVE_PATH_BACKGROUND_ERROR = "savePathBackgroundError";
	
	private Path path;
	
	public DetailResultFragmentController(Fragment fragment) {
		super(fragment);
	}

	public void savePath(){
		runInBackground(METHOD_SAVE_PATH_BACKGROUND, this, new Object[] {path}, new Class[]{Path.class}, METHOD_SAVE_PATH_BACKGROUND_RESULT, METHOD_SAVE_PATH_BACKGROUND_ERROR);
	}
	
	public Path savePathBackground(Path path){
		Path result = null;
		
		FDao dao = new FDao(getContext());
		
		try{
			result = dao.addPath(path);
		}catch (Exception e) {
			Functions.log(e);
		}finally{
			dao.close();
		}
		
		return result;
	}
	
	public void savePathBackgroundResult(Path path){
		Toast.makeText(getContext(), R.string.info_image_saved, Toast.LENGTH_SHORT).show();
	}
	
	public void savePathBackgroundError(Path path){
		Toast.makeText(getContext(), R.string.info_image_saved_error, Toast.LENGTH_SHORT).show();
	}

	public Path getPath() {
		return path;
	}

	public void setPath(Path path) {
		this.path = path;
	}
}
