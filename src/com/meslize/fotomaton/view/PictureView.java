package com.meslize.fotomaton.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;

import com.caterpillar.bitmap.util.Utils;
import com.framework.library.view.ManagedImageView;
import com.meslize.fotomaton.R;

/*
 * http://www.anddev.org/quick_and_easy_facedetector_demo-t3856.html
 */

public class PictureView extends ManagedImageView {
	
	// By default the identity card has 360x540
	public static final float DESIRED_WIDTH = 360;
	public static final float DESIRED_HEIGHT = 540;
	
	public static final int CORNER_NOTHING = 0;
	public static final int CORNER_LEFT_TOP = 1;
	public static final int CORNER_RIGHT_TOP = 2;
	public static final int CORNER_LEFT_BOTTOM = 3;
	public static final int CORNER_RIGHT_BOTTOM = 4;
	
	public static final String TEXT_DEFAULT = "%1$dpx x %2$spx";
	
	Bitmap bitmap;
	boolean bitmapAlready;
	
	float rectangleWidth = 0;
	float rectangleHeight = 0;
	float radiusCircle = 15;
	
	int textSize = 25;
	
	float xRectange = 0;
	float yRectange = 0;
	
	float selectedLeft;
	float selectedTop;
	float selectedRight;
	float selectedBottom;
	
	Paint paintSelected;
	Paint paintDesired;
	Paint paintCorner;
	Paint paintText;
	Paint paintShadow;
	Paint paintReset;
	
	int cornerTouched = 0;
	
	boolean drawDesired = false;
	
	private Area areaSelected;
	private Area areaDesired;
	
	public PictureView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public PictureView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PictureView(Context context) {
		super(context);
		init();
	}
	
	private void init(){
		setBitmapAlready(false);

		Paint paint = new Paint();
		paint.setColor(getResources().getColor(R.color.green_electric));
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(3);
		
		setPaintSelected(paint);
		
		paint = new Paint();
		paint.setColor(getResources().getColor(R.color.yellow_electric));
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(3);
		
		setPaintDesired(paint);
		
		paint = new Paint();
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		paint.setColor(getResources().getColor(R.color.blue_electric));
		
		setPaintCorner(paint);
		
		paint = new Paint();
		paint.setTextSize(getTextSize());
		paint.setColor(getResources().getColor(R.color.yellow_electric));
		
		setPaintText(paint);
		
		paint = new Paint();
		paint.setColor(getResources().getColor(R.color.black_shadow));
		
		setPaintShadow(paint);
		
		paint = new Paint();
		paint.setColor(getResources().getColor(R.color.blue_medium));
		
		setPaintReset(paint);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if(isBitmapAlready()){
			drawShadows(canvas);
			drawRectangleSelected(canvas);
			drawCornersCircle(canvas);
			drawText(canvas);
			
			drawRectangleDesired(canvas);
		}else{
			drawReset(canvas);
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			setCornerTouched(cornerTouched(event.getX(), event.getY()));

			break;
		case MotionEvent.ACTION_MOVE:
			if(event.getX() != getxRectange() || event.getY() != getyRectange()){
				if(getCornerTouched() == CORNER_NOTHING){
					touchMove(event.getX(), event.getY());
				}else{
					touchResize(event.getX(), event.getY());
				}
			}
			
			break;
		case MotionEvent.ACTION_UP:
			touchUp();
			
			break;
		}
		
		return true;
	}
	
	@Override
	public void setImageDownloaded(final Bitmap bitmap) {
		getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					@SuppressLint("NewApi")
					@Override
					public void onGlobalLayout() {
						if (getDrawable() != null) {
				    		setBitmapAlready(true);
							
				    		final Bitmap bitmapFinal = Bitmap.createScaledBitmap(bitmap, getWidth(), getHeight(), true);
				    						
							setBitmap(bitmapFinal);
							
							float rectangleWidth = (DESIRED_WIDTH / getScaleFactor()) / 2;
							float rectangleHeight = (DESIRED_HEIGHT / getScaleFactor()) / 2;
							
						    while(rectangleWidth > bitmap.getWidth() || rectangleHeight > bitmap.getHeight()){
						    	rectangleWidth = rectangleWidth / 2;
						    	rectangleHeight = rectangleHeight / 2;
						    }	
							
							setRectangleWidth(rectangleWidth);
							setRectangleHeight(rectangleHeight);
							
							fillCoordinates(getWidth() / 2, getHeight() / 2);
							  	  
							if (Utils.hasJellyBean()) {
								getViewTreeObserver().removeOnGlobalLayoutListener(this);
							} else {
								getViewTreeObserver().removeGlobalOnLayoutListener(this);
							}
							
							invalidate();
						}
					}
				});
		
		setBitmapAlready(true);		
		super.setImageDownloaded(bitmap);
	}

	public void touchUp(){
		setCornerTouched(CORNER_NOTHING);
		setDrawDesired(true);
		
		invalidate();
	}
	
	public void touchMove(float x, float y){
		Rect rect = new Rect((int)getSelectedLeft(), (int)getSelectedTop(), (int)getSelectedRight(), (int)getSelectedBottom());
		if(rect.contains((int)x, (int)y)){
			fillCoordinates(x, y);
			
			invalidate();		
		}	
	}
	
	public void touchResize(float x, float y){
		switch (getCornerTouched()) {
		case CORNER_LEFT_TOP:
			if(x < getSelectedRight()){
				setSelectedLeft(x);
			}
			
			if(y < getSelectedBottom()){
				setSelectedTop(y);
			}
			
			break;
		case CORNER_RIGHT_TOP:
			if(x > getSelectedLeft()){
				setSelectedRight(x);
			}
			
			if(y < getSelectedBottom()){
				setSelectedTop(y);	
			}
			
			break;
		case CORNER_LEFT_BOTTOM:
			if(x < getSelectedRight()){
				setSelectedLeft(x);	
			}
			
			if(y > getSelectedTop()){
				setSelectedBottom(y);
			}
			
			break;
		case CORNER_RIGHT_BOTTOM:
			if(x > getSelectedLeft()){
				setSelectedRight(x);
			}
			
			if(y > getSelectedTop()){
				setSelectedBottom(y);
			}
			
			break;
		}
		
		setRectangleWidth((getSelectedRight() - getSelectedLeft()) / 2);
		setRectangleHeight((getSelectedBottom() - getSelectedTop()) / 2);
		
		invalidate();
	}
	
	private int cornerTouched(float x, float y){		
		if(pointInsideCircle(getSelectedLeft(), getSelectedTop(), x, y, getRadiusCircleTouch())){
			return CORNER_LEFT_TOP;
		}else if(pointInsideCircle(getSelectedRight(), getSelectedTop(), x, y, getRadiusCircleTouch())){
			return CORNER_RIGHT_TOP;
		}else if(pointInsideCircle(getSelectedLeft(), getSelectedBottom(), x, y, getRadiusCircleTouch())){
			return CORNER_LEFT_BOTTOM;
		}else if(pointInsideCircle(getSelectedRight(), getSelectedBottom(), x, y, getRadiusCircleTouch())){
			return CORNER_RIGHT_BOTTOM;
		}else{
			return CORNER_NOTHING;
		}
	}
	
	private void fillCoordinates(float x, float y){
		setxRectange(x);
		setyRectange(y);
		
		setSelectedLeft(x - getRectangleWidth());
		setSelectedTop(y - getRectangleHeight());
		setSelectedRight(x + getRectangleWidth());
		setSelectedBottom(y + getRectangleHeight());
	}
	
	/**
	 * Draw the Rectangle that will be selected
	 * * @param canvas
	 */
	public void drawRectangleSelected(Canvas canvas){
		int left = (int)getSelectedLeft();
		int top = (int)getSelectedTop();
		int right = (int)getSelectedRight();
		int bottom = (int)getSelectedBottom();
		areaSelected = new Area(left, top, right - left, bottom - top);

		canvas.drawRect(left, top, right, bottom, getPaintSelected());
	}
	
	public void drawRectangleDesired(Canvas canvas){
		if(((getRectangleHeight() * 2) / (getRectangleWidth() * 2)) < (DESIRED_HEIGHT / DESIRED_WIDTH)){
			float extra = 0;
			
			do{
				extra--;
			}while(((getRectangleHeight() * 2) / ((getRectangleWidth() + extra) * 2)) < (DESIRED_HEIGHT / DESIRED_WIDTH));
			
			int left = (int)(getSelectedLeft() + Math.abs((extra)));
			int top = (int)getSelectedTop();
			int right = (int)(getSelectedRight() - Math.abs((extra)));
			int bottom = (int)getSelectedBottom();
			areaDesired = new Area(left, top, right - left, bottom - top);

			canvas.drawRect(left, top, right, bottom, getPaintDesired());
		}else if(((getRectangleHeight() * 2) / (getRectangleWidth() * 2)) > (DESIRED_HEIGHT / DESIRED_WIDTH)){
			float extra = 0;
			
			do{
				extra--;
			}while((((getRectangleHeight() + extra) * 2) / (getRectangleWidth() * 2)) > (DESIRED_HEIGHT / DESIRED_WIDTH));
			
			int left = (int)getSelectedLeft();
			int top = (int)(getSelectedTop() + Math.abs((extra)));
			int right = (int)getSelectedRight();
			int bottom = (int)(getSelectedBottom() - Math.abs((extra)));
			areaDesired = new Area(left, top, right - left, bottom - top);

			canvas.drawRect(left, top, right, bottom, getPaintDesired());
			
			
		}else{
			areaDesired = new Area((int)getSelectedLeft(), (int)getSelectedTop(), (int)getSelectedRight(), (int)getSelectedBottom());
		}
	}
	
	/**
	 * Draw the Bitmap on the canvas using the size of the bitmap
	 * @param canvas
	 */
	public void drawPicture(Canvas canvas){
		setBitmapAlready(true);
		canvas.drawBitmap(getBitmap(), 0, 0, null);			
	}
	
	/**
	 * Draw the circles for the corners
	 * @param canvas
	 */
	public void drawCornersCircle(Canvas canvas){
		canvas.drawCircle(getSelectedLeft(), getSelectedTop(), getRadiusCircle(), getPaintCorner());
		canvas.drawCircle(getSelectedRight(), getSelectedTop(), getRadiusCircle(), getPaintCorner());
		canvas.drawCircle(getSelectedLeft(), getSelectedBottom(), getRadiusCircle(), getPaintCorner());
		canvas.drawCircle(getSelectedRight(), getSelectedBottom(), getRadiusCircle(), getPaintCorner());
	}
	
	/**
	 * Draw the image dimensions
	 * @param canvas
	 */
	public void drawText(Canvas canvas){
		canvas.drawText(String.format(TEXT_DEFAULT, (int)(getSelectedRight() - getSelectedLeft()), (int)(getSelectedBottom() - getSelectedTop())), getSelectedLeft() + getTextSize(), getSelectedBottom() - getTextSize(), getPaintText());
	}
	
	/**
	 * Draw the shadows for all screen less the Rectangle selected
	 * @param canvas
	 */
	private void drawShadows(Canvas canvas){
		//Izquierda
		canvas.drawRect(0, 0, getSelectedLeft(), getHeight(), getPaintShadow());
		//Derecha
		canvas.drawRect(getSelectedRight(), 0, getWidth(), getHeight(), getPaintShadow());
		//Arriba
		canvas.drawRect(getSelectedLeft(), 0, getSelectedRight(), getSelectedTop(), getPaintShadow());
		//Abajo
		canvas.drawRect(getSelectedLeft(), getSelectedBottom(), getSelectedRight(), getHeight(), getPaintShadow());
	}
	
	public void drawReset(Canvas canvas){
		canvas.drawRect(0, 0, getWidth(), getHeight(), getPaintReset());
	}
	
	public boolean pointInsideCircle(double centerX, double centerY, double pointX, double pointY, double radio){
	    if (distanceBetweenPoints(centerX, centerY, pointX, pointY) <= radio){
	        return true;
	    }
	    return false;
	}
	
	public double distanceBetweenPoints(double centerX, double centerY, double pointX, double pointY){
		double distancia_x = pointX - centerX;
		double distancia_y = pointY - centerY;
	    return (double) Math.sqrt((distancia_x*distancia_x)+(distancia_y*distancia_y));
	}
	
	/**********************************
	 * SETTERS AND GETTERS
	 *********************************/

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public boolean isBitmapAlready() {
		return bitmapAlready;
	}

	public void setBitmapAlready(boolean bitmapAlready) {
		this.bitmapAlready = bitmapAlready;
	}

	public float getRectangleWidth() {
		return rectangleWidth;
	}

	public void setRectangleWidth(float rectangleWidth) {
		this.rectangleWidth = rectangleWidth;
	}

	public float getRectangleHeight() {
		return rectangleHeight;
	}

	public void setRectangleHeight(float rectangleHeight) {
		this.rectangleHeight = rectangleHeight;
	}

	public float getRadiusCircle() {
		return radiusCircle;
	}
	
	public float getRadiusCircleTouch() {
		return radiusCircle * 3;
	}

	public void setRadiusCircle(float radiusCircle) {
		this.radiusCircle = radiusCircle;
	}

	public int getTextSize() {
		return textSize;
	}

	public void setTextSize(int textSize) {
		this.textSize = textSize;
	}

	public float getxRectange() {
		return xRectange;
	}

	public void setxRectange(float xRectange) {
		this.xRectange = xRectange;
	}

	public float getyRectange() {
		return yRectange;
	}

	public void setyRectange(float yRectange) {
		this.yRectange = yRectange;
	}

	public float getSelectedLeft() {
		return selectedLeft;
	}

	public void setSelectedLeft(float selectedLeft) {
		this.selectedLeft = selectedLeft;
	}

	public float getSelectedTop() {
		return selectedTop;
	}

	public void setSelectedTop(float selectedTop) {
		this.selectedTop = selectedTop;
	}

	public float getSelectedRight() {
		return selectedRight;
	}

	public void setSelectedRight(float selectedRight) {
		this.selectedRight = selectedRight;
	}

	public float getSelectedBottom() {
		return selectedBottom;
	}

	public void setSelectedBottom(float selectedBottom) {
		this.selectedBottom = selectedBottom;
	}

	public Paint getPaintSelected() {
		return paintSelected;
	}

	public void setPaintSelected(Paint paintSelected) {
		this.paintSelected = paintSelected;
	}

	public Paint getPaintDesired() {
		return paintDesired;
	}

	public void setPaintDesired(Paint paintDesired) {
		this.paintDesired = paintDesired;
	}

	public Paint getPaintCorner() {
		return paintCorner;
	}

	public void setPaintCorner(Paint paintCorner) {
		this.paintCorner = paintCorner;
	}

	public Paint getPaintText() {
		return paintText;
	}

	public void setPaintText(Paint paintText) {
		this.paintText = paintText;
	}

	public Paint getPaintShadow() {
		return paintShadow;
	}

	public void setPaintShadow(Paint paintShadow) {
		this.paintShadow = paintShadow;
	}
	
	public Paint getPaintReset() {
		return paintReset;
	}
	
	public void setPaintReset(Paint paintReset) {
		this.paintReset = paintReset;
	}

	public int getCornerTouched() {
		return cornerTouched;
	}

	public void setCornerTouched(int cornerTouched) {
		this.cornerTouched = cornerTouched;
	}

	public boolean isDrawDesired() {
		return drawDesired;
	}

	public void setDrawDesired(boolean drawDesired) {
		this.drawDesired = drawDesired;
	}
	
	public Area getAreaSelected() {
		return areaSelected;
	}

	public void setAreaSelected(Area areaSelected) {
		this.areaSelected = areaSelected;
	}

	public Area getAreaDesired() {
		return areaDesired;
	}

	public void setAreaDesired(Area areaDesired) {
		this.areaDesired = areaDesired;
	}

	/**********************************
	 * END SETTERS AND GETTERS
	 *********************************/
	
	public static class Area{
		public int x;
		public int y;
		public int width;
		public int height;
		
		public Area(){}
		
		public Area(int x, int y, int width, int height) {
			super();
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}
	}
}
