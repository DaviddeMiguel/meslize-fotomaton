package com.meslize.fotomaton.detail;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.Fragment;

import com.framework.library.controller.Controller;
import com.framework.library.memory.FileCache;
import com.framework.library.util.DateTime;
import com.meslize.fotomaton.model.Path;
import com.meslize.fotomaton.view.PictureView.Area;

public class DetailFragmentController extends Controller {
	
	private final static String FILE_PATH = "Android/data/";
	private final static String FILE_NAME_DNI = "fotomaton_dni";
	private final static String FILE_NAME = "fotomaton";
	private final static String FILE_EXTENSION = ".jpg";
	
	private FileCache fileCache;
	
	private String resultPath;
	
	public DetailFragmentController(Fragment fragment) {
		super(fragment);
		
		createPath();
	}
	
	public void createPath(){
		resultPath =  FILE_PATH + getActivity().getPackageName() + "/" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.GERMANY).format(new Date()) + "/";
	}

	public void galleryAddPicture(String path) {
	    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
	    File f = new File(path);
	    Uri contentUri = Uri.fromFile(f);
	    mediaScanIntent.setData(contentUri);
	    getContext().sendBroadcast(mediaScanIntent);
	}
	
	public Path saveCutPicture(Area area, Bitmap bitmap){				
		if(area.x > 0 && area.y > 0 && area.width < bitmap.getWidth() && area.height < bitmap.getHeight()){
			Bitmap bitmapDni = Bitmap.createBitmap(bitmap, area.x, area.y, area.width, area.height);
			
			fileCache = new FileCache(getActivity(), resultPath);
			File file = fileCache.saveBitmap(bitmapDni, FILE_NAME_DNI + FILE_EXTENSION, Bitmap.CompressFormat.JPEG, 90);
			String thumbPath = file.getAbsolutePath();
			
			galleryAddPicture(thumbPath);

			String combinedPath = combineImages(4, 2, bitmapDni);
			galleryAddPicture(combinedPath);
			
			Path path = new Path(new DateTime(new Date()).toStringRfc3339(), combinedPath, thumbPath);
			
			return path;
		}else{
			return null;
		}
	}
	
	public String combineImages(int copyNumberPerRow, int copyNumberRows, Bitmap bitmap) { 
	    Bitmap result = Bitmap.createBitmap((bitmap.getWidth() * copyNumberPerRow) + (copyNumberPerRow * 2), (bitmap.getHeight() * copyNumberRows) + (copyNumberRows * 2), Bitmap.Config.ARGB_8888); 
	    Canvas comboImage = new Canvas(result); 
	    comboImage.drawColor(Color.WHITE);
	    
	    for(int i=0; i<copyNumberRows; i++){
		    for(int j=0; j<copyNumberPerRow; j++){
			    comboImage.drawBitmap(bitmap, (j * bitmap.getWidth()) + (j * 2) , i *  bitmap.getHeight() + (i * 2), null); 
		    } 	
	    }

	    fileCache = new FileCache(getActivity(), resultPath);
		File file = fileCache.saveBitmap(result, FILE_NAME + Calendar.getInstance().getTimeInMillis() + FILE_EXTENSION, Bitmap.CompressFormat.JPEG, 90);
	
		return file.getAbsolutePath();
	}
}
