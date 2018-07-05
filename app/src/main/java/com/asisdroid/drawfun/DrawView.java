package com.asisdroid.drawfun;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DrawView extends View implements OnTouchListener {
	private static final String TAG = "DrawView";
	private static final String NO_COLOR = "nocolor";
	private static final String SHAPE_BRUSH = "shape";
	private static final String NORMAL_BRUSH = "normal";
	private static final String STAR_BRUSH = "star";
	private static final String HEART_BRUSH = "heart";
	private static final String MULTISTAR_BRUSH = "multistar";
	private static final String LOVE_SMILEY_BRUSH = "lovesmiley";
	private static final String WINK_SMILEY_BRUSH = "winksmiley";
	private static final String COOL_SMILEY_BRUSH = "collsmiley";
	private static final String FLOWER_BRUSH = "flower";
	private static final String WINE_BRUSH = "wine";
	private static final String WALL_GRADIENT_BRUSH = "wall"+NO_COLOR;
	private static final String SKY_GRADIENT_BRUSH = "sky"+NO_COLOR;
	private static final String GRASS_GRADIENT_BRUSH = "grass"+NO_COLOR;
	private static final int XTRA_SMALL_WID = 30;
	private static final int SMALL_WID = 45;
	private static final int MED_WID = 90;
	private static final int LARGE_WID = 135;
	private static final int XTRA_LARGE_WID = 180;

	List<Point> points = new ArrayList<Point>();
	List<List<Point>> undoPointsInGroup = new ArrayList<List<Point>>(); //FOR KNOWING THE LAST POINTS
	List<List<Point>> redoPointsInGroup = new ArrayList<List<Point>>();
	List<Point> currentPoints = new ArrayList<Point>();
	Paint paint = new Paint();
	Random gen;
	int col_mode, color_integer, bgcolor_code;
	int wid_mode;
	private int bitmapSize;
	private final Point dummyBreakPOint; //ADDED TO PROVIDE BREAK FOR NEW PATHS
	boolean is_eraser_on = false, is_drawing = true;
    boolean isAddFlagFOrUndoCorrctn = false;

	public MainActivity mainActivityRef;


	private Bitmap mBitmapBrushStar, mBitmapBrushStarXS, mBitmapBrushStarS, mBitmapBrushStarL, mBitmapBrushStarXL
			, mBitmapBrushHeart, mBitmapBrushHeartXS, mBitmapBrushHeartS, mBitmapBrushHeartL, mBitmapBrushHeartXL,
			getmBitmapBrushMultiStar, getmBitmapBrushMultiStarXS, getmBitmapBrushMultiStarS, getmBitmapBrushMultiStarXL, getmBitmapBrushMultiStarL,
			mBitmapBrushLoveSMiley, mBitmapBrushLoveSMileyS, mBitmapBrushLoveSMileyXS, mBitmapBrushLoveSMileyL, mBitmapBrushLoveSMileyXL,
			mBitmapBrushCoolSMiley, mBitmapBrushCoolSMileyS, mBitmapBrushCoolSMileyXS, mBitmapBrushCoolSMileyL, mBitmapBrushCoolSMileyXL,
			mBitmapBrushWinkSMiley, mBitmapBrushWinkSMileyS, mBitmapBrushWinkSMileyXS, mBitmapBrushWinkSMileyL, mBitmapBrushWinkSMileyXL,
			mBitmapBrushWine, mBitmapBrushWineS, mBitmapBrushWineXS, mBitmapBrushWineL, mBitmapBrushWineXL,
			mBitmapBrushFlower, mBitmapBrushFlowerS, mBitmapBrushFlowerXS, mBitmapBrushFlowerL, mBitmapBrushFlowerXL,
			mBitmapBrushSkyGradient,mBitmapBrushSkyGradientS, mBitmapBrushSkyGradientXS, mBitmapBrushSkyGradientL, mBitmapBrushSkyGradientXL,
			mBitmapBrushWallGradient, mBitmapBrushWallGradientS, mBitmapBrushWallGradientXS, mBitmapBrushWallGradientL, mBitmapBrushWallGradientXL,
			mBitmapBrushGrassGradient, mBitmapBrushGrassGradientS, mBitmapBrushGrassGradientXS, mBitmapBrushGrassGradientL, mBitmapBrushGrassGradientXL;

	public DrawView(Context context) {
		super(context);
		mainActivityRef = (MainActivity) context;
		// set default colour to white
		col_mode = 0;

		// set default width to 7px
		wid_mode = 10;

		setFocusable(true);
		setFocusableInTouchMode(true);

		this.setOnTouchListener(this);

		paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(10);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);

        initBitmaps();

		dummyBreakPOint = new Point(-999,-999,-1, -1, Paint.Style.STROKE, NORMAL_BRUSH, bitmapSize);
	}

	private void initBitmaps(){
		bitmapSize = MED_WID;
		mBitmapBrushStar = BitmapFactory.decodeResource(getResources(), R.mipmap.star_draw);
		mBitmapBrushStar = Bitmap.createScaledBitmap(mBitmapBrushStar, MED_WID, MED_WID, true);
		mBitmapBrushStarS = Bitmap.createScaledBitmap(mBitmapBrushStar, SMALL_WID, SMALL_WID, true);
		mBitmapBrushStarXS = Bitmap.createScaledBitmap(mBitmapBrushStar, XTRA_SMALL_WID, XTRA_SMALL_WID, true);
		mBitmapBrushStarL = Bitmap.createScaledBitmap(mBitmapBrushStar, LARGE_WID, LARGE_WID, true);
		mBitmapBrushStarXL = Bitmap.createScaledBitmap(mBitmapBrushStar, XTRA_LARGE_WID, XTRA_LARGE_WID, true);

		mBitmapBrushHeart = BitmapFactory.decodeResource(getResources(), R.mipmap.heart_draw);
		mBitmapBrushHeart = Bitmap.createScaledBitmap(mBitmapBrushHeart,MED_WID, MED_WID,true);
		mBitmapBrushHeartS = Bitmap.createScaledBitmap(mBitmapBrushHeart,SMALL_WID, SMALL_WID,true);
		mBitmapBrushHeartXS = Bitmap.createScaledBitmap(mBitmapBrushHeart,XTRA_SMALL_WID, XTRA_SMALL_WID,true);
		mBitmapBrushHeartL = Bitmap.createScaledBitmap(mBitmapBrushHeart,LARGE_WID, LARGE_WID,true);
		mBitmapBrushHeartXL = Bitmap.createScaledBitmap(mBitmapBrushHeart,XTRA_LARGE_WID, XTRA_LARGE_WID,true);

		getmBitmapBrushMultiStar = BitmapFactory.decodeResource(getResources(), R.mipmap.multistar_draw);
		getmBitmapBrushMultiStar = Bitmap.createScaledBitmap(getmBitmapBrushMultiStar,MED_WID, MED_WID,true);
		getmBitmapBrushMultiStarS = Bitmap.createScaledBitmap(getmBitmapBrushMultiStar,SMALL_WID, SMALL_WID,true);
		getmBitmapBrushMultiStarXS= Bitmap.createScaledBitmap(getmBitmapBrushMultiStar,XTRA_SMALL_WID, XTRA_SMALL_WID,true);
		getmBitmapBrushMultiStarL = Bitmap.createScaledBitmap(getmBitmapBrushMultiStar,LARGE_WID, LARGE_WID,true);
		getmBitmapBrushMultiStarXL = Bitmap.createScaledBitmap(getmBitmapBrushMultiStar,XTRA_LARGE_WID, XTRA_LARGE_WID,true);

		mBitmapBrushFlower = BitmapFactory.decodeResource(getResources(), R.mipmap.flower_draw);
		mBitmapBrushFlower = Bitmap.createScaledBitmap(mBitmapBrushFlower,MED_WID, MED_WID,true);
		mBitmapBrushFlowerS = Bitmap.createScaledBitmap(mBitmapBrushFlower,SMALL_WID, SMALL_WID,true);
		mBitmapBrushFlowerXS= Bitmap.createScaledBitmap(mBitmapBrushFlower,XTRA_SMALL_WID, XTRA_SMALL_WID,true);
		mBitmapBrushFlowerL = Bitmap.createScaledBitmap(mBitmapBrushFlower,LARGE_WID, LARGE_WID,true);
		mBitmapBrushFlowerXL = Bitmap.createScaledBitmap(mBitmapBrushFlower,XTRA_LARGE_WID, XTRA_LARGE_WID,true);

		mBitmapBrushWine = BitmapFactory.decodeResource(getResources(), R.mipmap.wine_draw);
		mBitmapBrushWine = Bitmap.createScaledBitmap(mBitmapBrushWine,MED_WID, MED_WID,true);
		mBitmapBrushWineS = Bitmap.createScaledBitmap(mBitmapBrushWine,SMALL_WID, SMALL_WID,true);
		mBitmapBrushWineXS= Bitmap.createScaledBitmap(mBitmapBrushWine,XTRA_SMALL_WID, XTRA_SMALL_WID,true);
		mBitmapBrushWineL = Bitmap.createScaledBitmap(mBitmapBrushWine,LARGE_WID, LARGE_WID,true);
		mBitmapBrushWineXL = Bitmap.createScaledBitmap(mBitmapBrushWine,XTRA_LARGE_WID, XTRA_LARGE_WID,true);

		mBitmapBrushCoolSMiley = BitmapFactory.decodeResource(getResources(), R.mipmap.cool_smiley);
		mBitmapBrushCoolSMiley = Bitmap.createScaledBitmap(mBitmapBrushCoolSMiley,MED_WID, MED_WID,true);
		mBitmapBrushCoolSMileyS = Bitmap.createScaledBitmap(mBitmapBrushCoolSMiley,SMALL_WID, SMALL_WID,true);
		mBitmapBrushCoolSMileyXS= Bitmap.createScaledBitmap(mBitmapBrushCoolSMiley,XTRA_SMALL_WID, XTRA_SMALL_WID,true);
		mBitmapBrushCoolSMileyL = Bitmap.createScaledBitmap(mBitmapBrushCoolSMiley,LARGE_WID, LARGE_WID,true);
		mBitmapBrushCoolSMileyXL = Bitmap.createScaledBitmap(mBitmapBrushCoolSMiley,XTRA_LARGE_WID, XTRA_LARGE_WID,true);

		mBitmapBrushWinkSMiley = BitmapFactory.decodeResource(getResources(), R.mipmap.wink_smiley);
		mBitmapBrushWinkSMiley = Bitmap.createScaledBitmap(mBitmapBrushWinkSMiley,MED_WID, MED_WID,true);
		mBitmapBrushWinkSMileyS = Bitmap.createScaledBitmap(mBitmapBrushWinkSMiley,SMALL_WID, SMALL_WID,true);
		mBitmapBrushWinkSMileyXS= Bitmap.createScaledBitmap(mBitmapBrushWinkSMiley,XTRA_SMALL_WID, XTRA_SMALL_WID,true);
		mBitmapBrushWinkSMileyL = Bitmap.createScaledBitmap(mBitmapBrushWinkSMiley,LARGE_WID, LARGE_WID,true);
		mBitmapBrushWinkSMileyXL = Bitmap.createScaledBitmap(mBitmapBrushWinkSMiley,XTRA_LARGE_WID, XTRA_LARGE_WID,true);

		mBitmapBrushLoveSMiley = BitmapFactory.decodeResource(getResources(), R.mipmap.love_smiley);
		mBitmapBrushLoveSMiley = Bitmap.createScaledBitmap(mBitmapBrushLoveSMiley,MED_WID, MED_WID,true);
		mBitmapBrushLoveSMileyS = Bitmap.createScaledBitmap(mBitmapBrushLoveSMiley,SMALL_WID, SMALL_WID,true);
		mBitmapBrushLoveSMileyXS= Bitmap.createScaledBitmap(mBitmapBrushLoveSMiley,XTRA_SMALL_WID, XTRA_SMALL_WID,true);
		mBitmapBrushLoveSMileyL = Bitmap.createScaledBitmap(mBitmapBrushLoveSMiley,LARGE_WID, LARGE_WID,true);
		mBitmapBrushLoveSMileyXL = Bitmap.createScaledBitmap(mBitmapBrushLoveSMiley,XTRA_LARGE_WID, XTRA_LARGE_WID,true);

		mBitmapBrushSkyGradient = BitmapFactory.decodeResource(getResources(), R.mipmap.sky_pattern);
		mBitmapBrushSkyGradient = Bitmap.createScaledBitmap(mBitmapBrushSkyGradient,MED_WID, MED_WID,true);
		mBitmapBrushSkyGradientS = Bitmap.createScaledBitmap(mBitmapBrushSkyGradient,SMALL_WID, SMALL_WID,true);
		mBitmapBrushSkyGradientXS= Bitmap.createScaledBitmap(mBitmapBrushSkyGradient,XTRA_SMALL_WID, XTRA_SMALL_WID,true);
		mBitmapBrushSkyGradientL = Bitmap.createScaledBitmap(mBitmapBrushSkyGradient,LARGE_WID, LARGE_WID,true);
		mBitmapBrushSkyGradientXL = Bitmap.createScaledBitmap(mBitmapBrushSkyGradient,XTRA_LARGE_WID, XTRA_LARGE_WID,true);

		mBitmapBrushWallGradient = BitmapFactory.decodeResource(getResources(), R.mipmap.wall_pattern);
		mBitmapBrushWallGradient = Bitmap.createScaledBitmap(mBitmapBrushWallGradient,MED_WID, MED_WID,true);
		mBitmapBrushWallGradientS = Bitmap.createScaledBitmap(mBitmapBrushWallGradient,SMALL_WID, SMALL_WID,true);
		mBitmapBrushWallGradientXS= Bitmap.createScaledBitmap(mBitmapBrushWallGradient,XTRA_SMALL_WID, XTRA_SMALL_WID,true);
		mBitmapBrushWallGradientL = Bitmap.createScaledBitmap(mBitmapBrushWallGradient,LARGE_WID, LARGE_WID,true);
		mBitmapBrushWallGradientXL = Bitmap.createScaledBitmap(mBitmapBrushWallGradient,XTRA_LARGE_WID, XTRA_LARGE_WID,true);

		mBitmapBrushGrassGradient = BitmapFactory.decodeResource(getResources(), R.mipmap.grass_pattern);
		mBitmapBrushGrassGradient = Bitmap.createScaledBitmap(mBitmapBrushGrassGradient,MED_WID, MED_WID,true);
		mBitmapBrushGrassGradientS = Bitmap.createScaledBitmap(mBitmapBrushGrassGradient,SMALL_WID, SMALL_WID,true);
		mBitmapBrushGrassGradientXS= Bitmap.createScaledBitmap(mBitmapBrushGrassGradient,XTRA_SMALL_WID, XTRA_SMALL_WID,true);
		mBitmapBrushGrassGradientL = Bitmap.createScaledBitmap(mBitmapBrushGrassGradient,LARGE_WID, LARGE_WID,true);
		mBitmapBrushGrassGradientXL = Bitmap.createScaledBitmap(mBitmapBrushGrassGradient,XTRA_LARGE_WID, XTRA_LARGE_WID,true);

	}

	// used to clear the screen
	public void clearPoints () {
	    isAddFlagFOrUndoCorrctn = false;
		points.clear();
		undoPointsInGroup.clear();
		redoPointsInGroup.clear();
		forceRedraw();
	}

	public void allowDrawing(boolean isAllowedDraw){
		is_drawing = isAllowedDraw;
	}

	public void undoPoints(){
		int undoPointsGroupLength = undoPointsInGroup.size();
		if(undoPointsGroupLength==0){ //CHECKING FOR UNDO POINTS
			//Toast.makeText(getContext(), "Nothing to undo!", Toast.LENGTH_SHORT).show();
		}
		else {
			List<Point> currentOperatedPointsArray = undoPointsInGroup.get(undoPointsGroupLength - 1);
            //currentOperatedPointsArray.add(dummyBreakPOint);
			undoPointsInGroup.remove(undoPointsGroupLength - 1); //REMOVING LATEST POINTS FOR UNDO
			redoPointsInGroup.add(currentOperatedPointsArray); //ADDING LATEST POINTS FOR REDO
			//RE-ADDING ALL POINTS AFTER UNDO
			points.clear();
			for (int i = 0; i < undoPointsInGroup.size(); i++) {
				points.addAll(undoPointsInGroup.get(i));
			}
			points.add(dummyBreakPOint);
			forceRedraw();
		}
	}

	public int getUndoPointArraySize(){
		return undoPointsInGroup.size();
	}

	public int getRedoPointArraySize(){
		return redoPointsInGroup.size();
	}

	public void redoPoints(){
		int redoPointsGroupLength = redoPointsInGroup.size();
		if(redoPointsGroupLength==0){
			//Toast.makeText(getContext(), "Nothing to redo!", Toast.LENGTH_SHORT).show();
		}
		else{
			List<Point> currentOperatedPointsArray = redoPointsInGroup.get(redoPointsGroupLength-1);
			currentOperatedPointsArray.add(dummyBreakPOint);
			undoPointsInGroup.add(currentOperatedPointsArray); //ADDING LATEST REDO POINTS GROUP TO UNDO
			redoPointsInGroup.remove(redoPointsGroupLength-1);
			//RE-ADDING ALL POINTS AFTER UNDO
			points.clear();
			for (int i = 0; i < undoPointsInGroup.size(); i++) {
				points.addAll(undoPointsInGroup.get(i));
			}
			forceRedraw();
		}
	}

	/**
	 * Force view to redraw. Without this points aren't cleared until next action
	 */
	public void forceRedraw() {
		invalidate();
	}

	// used to set drawing colour
	public void changeColour (int col_in) {
		col_mode = col_in;
	}

	// used to set drawing colour by sending the color
	public void changeColour (int col_in, int sent_color) {
		col_mode = col_in;
		color_integer = sent_color;
	}

	//CHnage to eraser mode
	public void setEraserMode(boolean isEraserOnOrOff, int bgcolor){
		is_eraser_on = isEraserOnOrOff;
		bgcolor_code = bgcolor;
	}

	public boolean getEraserMode(){
		return is_eraser_on;
	}

	// used to set drawing width
	public void changeWidth (int wid_in) {
		wid_mode = wid_in;
		Log.d("asisu", ""+wid_mode * 9 +"--------"+wid_mode);
		if(wid_mode > 0 && wid_mode<=5) {
			bitmapSize = SMALL_WID;
		}
		else if(wid_mode > 5 && wid_mode<=10) {
			bitmapSize = MED_WID;
		}
		else if(wid_mode > 10 && wid_mode<=15) {
			bitmapSize = LARGE_WID;
		}
		else if(wid_mode > 15 && wid_mode<=20) {
			bitmapSize = XTRA_LARGE_WID;
		}
		else if(wid_mode == 0){
			bitmapSize = XTRA_SMALL_WID;
		}
		else{
			bitmapSize = XTRA_LARGE_WID;
		}
	}

	public void drawCircleInCanvas(float x_coor, float y_coor, int radius, int radiusHeight, int color, boolean isShapeFilled){
		//Log.d("asisi","Circle-"+x_coor+", "+y_coor);
		int x =0,y = 0;
		Paint.Style currentSTyle;

		if(isShapeFilled){
			currentSTyle = Paint.Style.FILL;
		}
		else{
			currentSTyle = Paint.Style.STROKE;
		}

		List<Point> circlePoints = new ArrayList<Point>();
		for (int i=0;i<360;i++) {
			x = (int) (x_coor + radius* Math.cos( (i*2*Math.PI)/350 ));
			y = (int) (y_coor + radiusHeight*-Math.sin( (i*2*Math.PI)/350 ));
			circlePoints.add(new Point(x, y, color, wid_mode, currentSTyle, SHAPE_BRUSH, bitmapSize));
		}

		points.add(dummyBreakPOint);
		points.addAll(circlePoints);
        points.add(dummyBreakPOint);
        circlePoints.add(dummyBreakPOint);
        redoPointsInGroup.clear();
        undoPointsInGroup.add(new ArrayList<Point>(circlePoints));
		forceRedraw();
		mainActivityRef.refreshUIOnDraw();
	}

	public void drawrectangleInCanvas(float x_coor, float y_coor, int width, int height,  int color, boolean isShapeFilled){
       // int x = (int) x_coor,y = (int) y_coor;
       // Log.d("asisi","Circle-"+x_coor+", "+y_coor+"----"+isShapeFilled);
		List<Point> rectPoints = new ArrayList<Point>();
		Paint.Style paintStyle;

		int x= (int)x_coor, y= (int)y_coor;

		if(isShapeFilled){
			paintStyle  = Paint.Style.FILL;
		}
		else{
			paintStyle  = Paint.Style.STROKE;
		}

        rectPoints.add(new Point(x,y,color,wid_mode, paintStyle, SHAPE_BRUSH, bitmapSize));
        x+=width;
        rectPoints.add(new Point(x,y,color,wid_mode, paintStyle, SHAPE_BRUSH, bitmapSize));
        y+=height;
        rectPoints.add(new Point(x,y,color,wid_mode, paintStyle, SHAPE_BRUSH, bitmapSize));
        x-=width;
        rectPoints.add(new Point(x,y,color,wid_mode, paintStyle, SHAPE_BRUSH, bitmapSize));
		y-=height;

        //LEFT Extra LINE
        for(int i=0;i<2;i++){
            rectPoints.add(new Point(x,y,color,wid_mode, paintStyle, SHAPE_BRUSH, bitmapSize));
            x++;
        }

		points.add(dummyBreakPOint);
		points.addAll(rectPoints);
		points.add(dummyBreakPOint);
		rectPoints.add(dummyBreakPOint);
		redoPointsInGroup.clear();
		undoPointsInGroup.add(new ArrayList<Point>(rectPoints));

		forceRedraw();
		mainActivityRef.refreshUIOnDraw();
    }

    public void drawTriangleInCanvas(float x_coor, float y_coor, int width, int height,  int color, boolean isShapeFilled){
        List<Point> trianglePoints = new ArrayList<Point>();
        Paint.Style paintStyle;

        int x= (int)x_coor, y= (int)y_coor;

        if(isShapeFilled){
            paintStyle  = Paint.Style.FILL;
        }
        else{
            paintStyle  = Paint.Style.STROKE;
        }

        trianglePoints.add(new Point(x, y, color, wid_mode, paintStyle, SHAPE_BRUSH, bitmapSize));
        x+=(width/2);
        y+=height;
        trianglePoints.add(new Point(x, y, color, wid_mode, paintStyle, SHAPE_BRUSH, bitmapSize));
        x-=width;
        trianglePoints.add(new Point(x, y, color, wid_mode, paintStyle, SHAPE_BRUSH, bitmapSize));
        x+=(width/2);
        y-=height;
        trianglePoints.add(new Point(x, y, color, wid_mode, paintStyle, SHAPE_BRUSH, bitmapSize));

        points.add(dummyBreakPOint);
        points.addAll(trianglePoints);
        points.add(dummyBreakPOint);
        trianglePoints.add(dummyBreakPOint);
        redoPointsInGroup.clear();
        undoPointsInGroup.add(new ArrayList<Point>(trianglePoints));

        forceRedraw();
        mainActivityRef.refreshUIOnDraw();
    }

    public void drawStarInCanvas(float x_coor, float y_coor, int width, int height,  int color, boolean isShapeFilled){
    	Log.d("asisi", width+" -- "+height);
        List<Point> starPoints = new ArrayList<Point>();
        Paint.Style paintStyle;

        int x= (int)x_coor, y= (int)y_coor;

        if(isShapeFilled){
            paintStyle  = Paint.Style.FILL;
        }
        else{
            paintStyle  = Paint.Style.STROKE;
        }

        //ADDD YOUR POINT LOGIC HERE
		starPoints.add(new Point(x+(width/2), y, color, wid_mode, paintStyle, SHAPE_BRUSH, bitmapSize)); //TOP MID POINT
        x+=2*(width/3);
        y+=(height/3);
		starPoints.add(new Point(x, y, color, wid_mode, paintStyle, SHAPE_BRUSH, bitmapSize));
		x+=(width/3);
		y=(int)y_coor;
		y+=(height/2.6);
		starPoints.add(new Point(x, y, color, wid_mode, paintStyle, SHAPE_BRUSH, bitmapSize));
		y=(int) y_coor;
		y+=2*(height/3);
		x = (int) x_coor;
		x+=2*(width/2.8);
		starPoints.add(new Point(x, y, color, wid_mode, paintStyle, SHAPE_BRUSH, bitmapSize));
		y+=(height/3);
		x = (int) x_coor;
		x += 2*(width/2.5);
		starPoints.add(new Point(x, y, color, wid_mode, paintStyle, SHAPE_BRUSH, bitmapSize));
		x = (int) x_coor;
		x+=(width/2);
		y-=(height/4.4);
		starPoints.add(new Point(x, y, color, wid_mode, paintStyle, SHAPE_BRUSH, bitmapSize));
		y=(int) y_coor;
		y+=height;
		x = (int) x_coor;
		x+=(width/4.8);
		starPoints.add(new Point(x, y, color, wid_mode, paintStyle, SHAPE_BRUSH, bitmapSize));
		y=(int) y_coor;
		y+=2*(height/3);
		x = (int) x_coor;
		x+=(width/3.5);
		starPoints.add(new Point(x, y, color, wid_mode, paintStyle, SHAPE_BRUSH, bitmapSize));
		x = (int) x_coor;
		y=(int) y_coor;
		y+=(height/2.6);
		starPoints.add(new Point(x, y, color, wid_mode, paintStyle, SHAPE_BRUSH, bitmapSize));
		x+=(width/2.8);
		y=(int) y_coor;
		y+=(height/3.1);
		starPoints.add(new Point(x, y, color, wid_mode, paintStyle, SHAPE_BRUSH, bitmapSize));
		x=(int) x_coor;
		y = (int) y_coor;
		starPoints.add(new Point(x+(width/2), y, color, wid_mode, paintStyle, SHAPE_BRUSH, bitmapSize)); //TOP MID POINT

        points.add(dummyBreakPOint);
        points.addAll(starPoints);
        points.add(dummyBreakPOint);
		starPoints.add(dummyBreakPOint);
        redoPointsInGroup.clear();
        undoPointsInGroup.add(new ArrayList<Point>(starPoints));

        forceRedraw();
        mainActivityRef.refreshUIOnDraw();
    }

	@Override
	public void onDraw(Canvas canvas) {
		// for each point, draw on canvas
		//Log.d("onDraw","START");
		/*for (Point point : points) {
			point.draw(canvas, paint);
		}*/

		Bitmap currentBitmap = null;
        Path path = new Path();
        boolean first = true, startNew = false;
        for (Point point : points) {
            if (first) {
                first = false;
                paint.setColorFilter(null);
                paint.setColor(point.col);
                paint.setStrokeWidth(point.width);
                paint.setStyle(point.style);
				//Log.d("onDraw","START - "+point.x+", "+point.y);
				if(point.x==-999){
					startNew = true;
				}
				else {
					path.moveTo(point.x, point.y);
				}
				if(!(point.brushStyle.equalsIgnoreCase(NORMAL_BRUSH) && point.brushStyle.equalsIgnoreCase(SHAPE_BRUSH))) {
					currentBitmap = getCurrentBitmap(point); //gets the current drawable bitmap
				}
            } else
            if(point.x==-999){
                startNew = true;
            }
            else if(startNew){
                canvas.drawPath(path, paint);
                path.reset();
                startNew = false;
				paint.setColorFilter(null);
                paint.setColor(point.col);
                paint.setStrokeWidth(point.width);
				paint.setStyle(point.style);
                path.moveTo(point.x, point.y);
				if(point.brushStyle.equalsIgnoreCase(NORMAL_BRUSH)) {
					paint.setStyle(Paint.Style.FILL);
					float circleRadius;
					if(point.width!=0){
						circleRadius = point.width/2;
					}
					else{
						circleRadius = 1.5f;
					}
					canvas.drawCircle(point.x, point.y, circleRadius, paint);
					paint.setStyle(point.style);
				}
				else if(!point.brushStyle.equalsIgnoreCase(SHAPE_BRUSH)){ //ONLY FOR BITMAPS
					currentBitmap = getCurrentBitmap(point); //gets the current drawable bitmap
					if(!point.brushStyle.contains(NO_COLOR)) {
						changeBitmapColor(point.col);
					}
					else{
						paint.setColorFilter(null);
					}
					//Log.d("check",point.bitmapSize+"\n"+getCurrentBitmap(point));
					canvas.drawBitmap(currentBitmap, point.x, point.y, paint);
				}
            }
            else
            {
            	//Log.d("asisu", ""+point.brushStyle);
            	if(point.brushStyle.equalsIgnoreCase(NORMAL_BRUSH) || point.brushStyle.equalsIgnoreCase(SHAPE_BRUSH)) {
					//FOR NORMAL BRUSH
					path.lineTo(point.x, point.y);
				}
				else{
					//FOR STAR BRUSHESss
					if(!point.brushStyle.contains(NO_COLOR)) {
						changeBitmapColor(point.col);
					}
					else{
						paint.setColorFilter(null);
					}
					//Log.d("check",point.bitmapSize+"\n"+getCurrentBitmap(point));
					canvas.drawBitmap(currentBitmap, point.x, point.y, paint);
				}
            }
        }
        canvas.drawPath(path, paint);
        paint.setStyle(Paint.Style.STROKE); //TO RESET TO NORMAL
		paint.setAntiAlias(true);
		//Log.d("onDraw","END");
	}

	private Bitmap getCurrentBitmap(Point point){
		if(point.brushStyle.equalsIgnoreCase(STAR_BRUSH)){
			if(point.bitmapSize == 45) { //SMALL
				return mBitmapBrushStarS;
			}
			else if(point.bitmapSize == 90) { //MEDIUM
				return mBitmapBrushStar;
			}
			else if(point.bitmapSize == 135) { //LARGE
				return mBitmapBrushStarL;
			}
			else if(point.bitmapSize == 180) { //XTRA LARGE
				return mBitmapBrushStarXL;
			}
			else if(point.bitmapSize == 30){ //XTRA SMALL
				return mBitmapBrushStarXS;
			}
		}
		else if(point.brushStyle.equalsIgnoreCase(HEART_BRUSH)){
			if(point.bitmapSize == 45) { //SMALL
				return mBitmapBrushHeartS;
			}
			else if(point.bitmapSize == 90) { //MEDIUM
				return mBitmapBrushHeart;
			}
			else if(point.bitmapSize == 135) { //LARGE
				return mBitmapBrushHeartL;
			}
			else if(point.bitmapSize == 180) { //XTRA LARGE
				return mBitmapBrushHeartXL;
			}
			else if(point.bitmapSize == 30){ //XTRA SMALL
				return mBitmapBrushHeartXS;
			}
		}
		else if(point.brushStyle.equalsIgnoreCase(MULTISTAR_BRUSH)){
			if(point.bitmapSize == 45) { //SMALL
				return getmBitmapBrushMultiStarS;
			}
			else if(point.bitmapSize == 90) { //MEDIUM
				return getmBitmapBrushMultiStar;
			}
			else if(point.bitmapSize == 135) { //LARGE
				return getmBitmapBrushMultiStarL;
			}
			else if(point.bitmapSize == 180) { //XTRA LARGE
				return getmBitmapBrushMultiStarXL;
			}
			else if(point.bitmapSize == 30){ //XTRA SMALL
				return getmBitmapBrushMultiStarXS;
			}
		}
		else if(point.brushStyle.equalsIgnoreCase(COOL_SMILEY_BRUSH)){
			if(point.bitmapSize == 45) { //SMALL
				return mBitmapBrushCoolSMileyS;
			}
			else if(point.bitmapSize == 90) { //MEDIUM
				return mBitmapBrushCoolSMiley;
			}
			else if(point.bitmapSize == 135) { //LARGE
				return mBitmapBrushCoolSMileyL;
			}
			else if(point.bitmapSize == 180) { //XTRA LARGE
				return mBitmapBrushCoolSMileyXL;
			}
			else if(point.bitmapSize == 30){ //XTRA SMALL
				return mBitmapBrushCoolSMileyXS;
			}
		}
		else if(point.brushStyle.equalsIgnoreCase(WINK_SMILEY_BRUSH)){
			if(point.bitmapSize == 45) { //SMALL
				return mBitmapBrushWinkSMileyS;
			}
			else if(point.bitmapSize == 90) { //MEDIUM
				return mBitmapBrushWinkSMiley;
			}
			else if(point.bitmapSize == 135) { //LARGE
				return mBitmapBrushWinkSMileyL;
			}
			else if(point.bitmapSize == 180) { //XTRA LARGE
				return mBitmapBrushWinkSMileyXL;
			}
			else if(point.bitmapSize == 30){ //XTRA SMALL
				return mBitmapBrushWinkSMileyXS;
			}
		}
		else if(point.brushStyle.equalsIgnoreCase(LOVE_SMILEY_BRUSH)){
			if(point.bitmapSize == 45) { //SMALL
				return mBitmapBrushLoveSMileyS;
			}
			else if(point.bitmapSize == 90) { //MEDIUM
				return mBitmapBrushLoveSMiley;
			}
			else if(point.bitmapSize == 135) { //LARGE
				return mBitmapBrushLoveSMileyL;
			}
			else if(point.bitmapSize == 180) { //XTRA LARGE
				return mBitmapBrushLoveSMileyXL;
			}
			else if(point.bitmapSize == 30){ //XTRA SMALL
				return mBitmapBrushLoveSMileyXS;
			}
		}
		else if(point.brushStyle.equalsIgnoreCase(WALL_GRADIENT_BRUSH)){
			if(point.bitmapSize == 45) { //SMALL
				return mBitmapBrushWallGradientS;
			}
			else if(point.bitmapSize == 90) { //MEDIUM
				return mBitmapBrushWallGradient;
			}
			else if(point.bitmapSize == 135) { //LARGE
				return mBitmapBrushWallGradientL;
			}
			else if(point.bitmapSize == 180) { //XTRA LARGE
				return mBitmapBrushWallGradientXL;
			}
			else if(point.bitmapSize == 30){ //XTRA SMALL
				return mBitmapBrushWallGradientXS;
			}
		}
		else if(point.brushStyle.equalsIgnoreCase(SKY_GRADIENT_BRUSH)){
			if(point.bitmapSize == 45) { //SMALL
				return mBitmapBrushSkyGradientS;
			}
			else if(point.bitmapSize == 90) { //MEDIUM
				return mBitmapBrushSkyGradient;
			}
			else if(point.bitmapSize == 135) { //LARGE
				return mBitmapBrushSkyGradientL;
			}
			else if(point.bitmapSize == 180) { //XTRA LARGE
				return mBitmapBrushSkyGradientXL;
			}
			else if(point.bitmapSize == 30){ //XTRA SMALL
				return mBitmapBrushSkyGradientXS;
			}
		}
		else if(point.brushStyle.equalsIgnoreCase(GRASS_GRADIENT_BRUSH)){
			if(point.bitmapSize == 45) { //SMALL
				return mBitmapBrushGrassGradientS;
			}
			else if(point.bitmapSize == 90) { //MEDIUM
				return mBitmapBrushGrassGradient;
			}
			else if(point.bitmapSize == 135) { //LARGE
				return mBitmapBrushGrassGradientL;
			}
			else if(point.bitmapSize == 180) { //XTRA LARGE
				return mBitmapBrushGrassGradientXL;
			}
			else if(point.bitmapSize == 30){ //XTRA SMALL
				return mBitmapBrushGrassGradientXS;
			}
		}
		else if(point.brushStyle.equalsIgnoreCase(WINE_BRUSH)){
			if(point.bitmapSize == 45) { //SMALL
				return mBitmapBrushWineS;
			}
			else if(point.bitmapSize == 90) { //MEDIUM
				return mBitmapBrushWine;
			}
			else if(point.bitmapSize == 135) { //LARGE
				return mBitmapBrushWineL;
			}
			else if(point.bitmapSize == 180) { //XTRA LARGE
				return mBitmapBrushWineXL;
			}
			else if(point.bitmapSize == 30){ //XTRA SMALL
				return mBitmapBrushWineXS;
			}
		}
		else if(point.brushStyle.equalsIgnoreCase(FLOWER_BRUSH)){
			if(point.bitmapSize == 45) { //SMALL
				return mBitmapBrushFlowerS;
			}
			else if(point.bitmapSize == 90) { //MEDIUM
				return mBitmapBrushFlower;
			}
			else if(point.bitmapSize == 135) { //LARGE
				return mBitmapBrushFlowerL;
			}
			else if(point.bitmapSize == 180) { //XTRA LARGE
				return mBitmapBrushFlowerXL;
			}
			else if(point.bitmapSize == 30){ //XTRA SMALL
				return mBitmapBrushFlowerXS;
			}
		}
		return null;
	}

	private void changeBitmapColor(int color) {
		ColorFilter filter = new LightingColorFilter(color, 1);
		paint.setColorFilter(filter);
	}

	public boolean onTouch(View view, MotionEvent event) {
		if(is_drawing==false){
			return false;
		}
		int new_col = 0;
		if (col_mode < 0) {
			gen = new Random();
			col_mode = gen.nextInt( 8 );
		}
		// This if statement may be redundant now
		if (col_mode >= 0) {
			switch (col_mode) {
				case 0 : {
					new_col =  Color.WHITE;
					break;
				}
				case 1 : {
					new_col =  Color.BLUE;
					break;
				}
				case 2 : {
					new_col =  Color.CYAN;
					break;
				}
				case 3 : {
					new_col =  Color.GREEN;
					break;
				}
				case 4 : {
					new_col =  Color.MAGENTA;
					break;
				}
				case 5 : {
					new_col =  Color.RED;
					break;
				}
				case 6 : {
					new_col =  Color.YELLOW;
					break;
				}
				case 7 : {
					new_col =  Color.BLACK;
					break;
				}
				case 99:{
					new_col = color_integer;
				}
			}
		}
		/* else {
			gen = new Random();
			new_col = gen.nextInt( 8 );
		} */

		Point point;
		if(event.getAction() == MotionEvent.ACTION_MOVE) {
			if(!is_eraser_on) {
				point = new FriendlyPoint(event.getX(), event.getY(), new_col, points.get(points.size() - 1), wid_mode, Paint.Style.STROKE, mainActivityRef.currentBrushType, bitmapSize);
			}
			else{
				point = new FriendlyPoint(event.getX(), event.getY(), bgcolor_code, points.get(points.size() - 1), wid_mode, Paint.Style.STROKE, NORMAL_BRUSH, bitmapSize);
			}
			//Log.d("asisu move",""+mainActivityRef.currentBrushType);
		} else if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if(!is_eraser_on) {
				point = new Point(event.getX(), event.getY(), new_col, wid_mode, Paint.Style.STROKE, mainActivityRef.currentBrushType, bitmapSize);
			}
			else{
				point = new Point(event.getX(), event.getY(), bgcolor_code , wid_mode, Paint.Style.STROKE, NORMAL_BRUSH, bitmapSize);
			}
			//mainActivityRef.animateCloseBottomLayout();
			//mainActivityRef.animateCloseTopLayout();
			mainActivityRef.animateExtraOptionCLose();
			if(undoPointsInGroup.size()==0 && isAddFlagFOrUndoCorrctn){
			    points.add(dummyBreakPOint);
            }
            else{
			    isAddFlagFOrUndoCorrctn = true;
            }
		} else {
            points.add(dummyBreakPOint);
            currentPoints.add(dummyBreakPOint);
			undoPointsInGroup.add(new ArrayList<Point>(currentPoints));
			//Log.d("asisi","before==="+currentPoints.size()+"--undopiints="+undoPointsInGroup.get(undoPointsInGroup.size()-1)+"--"+undoPointsInGroup.size());
			currentPoints.clear();
			redoPointsInGroup.clear(); //CLEAR REDO POINTS ON DRAWING
			//Log.d("asisi","after==="+currentPoints.size()+"--undopiints="+undoPointsInGroup.get(undoPointsInGroup.size()-1)+"--"+undoPointsInGroup.size());
			mainActivityRef.refreshUIOnDraw();
			//mainActivityRef.animateOpenBottomLayout();
			//mainActivityRef.animateOpenTopLayout();
			return false;
		}
		if((!mainActivityRef.currentBrushType.equalsIgnoreCase(NORMAL_BRUSH)) && currentPoints.size()>0){
			Point prevPoint = points.get(points.size()-1);
			double dist = Math.sqrt( Math.pow((prevPoint.x-point.x), 2) + Math.pow((prevPoint.y-point.y), 2) );
			if(dist>(bitmapSize)){
				points.add(point);
				currentPoints.add(point);
			}
		}
		else {
			points.add(point);
			currentPoints.add(point);
		}

		forceRedraw();
		Log.d(TAG, "point: " + point);
		return true;
	}

}
