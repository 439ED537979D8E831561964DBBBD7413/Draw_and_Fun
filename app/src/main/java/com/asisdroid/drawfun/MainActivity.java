package com.asisdroid.drawfun;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;

import org.jsoup.Jsoup;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.res.Resources.getSystem;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "DrawAndFun";
    private static final String NO_COLOR = "nocolor";
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
    public static final String VALIDATION_MESSAGE = "Paint & background color is same!";
    private static final int SHARE_CODE = 999;

    public static  MainActivity mainActivityInstance;
    private SharedPreferences permissionStatus;
    private static final int WRITE_EXTERNAL_STORAGE = 2380;
    private static final int REQUEST_PERMISSION_SETTING = 893;
    private Menu mainMenu;
    private boolean secondTimeBackClicked = false;
    String imgpath;

    private ImageView btnEraser, btnChangeDrawColor, btnChangeDrawWidth, btnOption, btnUndo, btnRedo, btnDummyOption, btnShareDummy
            , btnCircleDrag, btnCircleClose, btnCircleStretch, btnCircleStretchVertical, btnCircleColor, btnCircleFillUnfill,
            btnRectDrag, btnRectClose, btnRectStretch, btnRectColor, btnRectFillUnfill, btnRectStretchVertical,
            btnTriDrag, btnTriClose, btnTriStretch, btnTriColor, btnTriFillUnfill, btnTriStretchVertical,
            btnStarDrag, btnStarClose, btnStarStretch, btnStarColor, btnStarFillUnfill, btnStarStretchVertical, btnHideTopBottomMenu;
    Button btnCircleOk, btnRectOk, btnTriOk, btnStarOk;
    private TextView btnNew, btnSave, btnGallery, btnClearCanvas, btnRateApp, btnDrawCircle, btnDrawRect, btnDrawTriangle, btnDrawStar,
    btnNormalBrush, btnHeartBrush, btnStarBrush, btnMultiStarBrush, btnLoveSmileyBrush, btnWinkSmileyBrush, btnCoolSmileyBrush, btnWineBrush,
    btnFlowerBrush,
    btnSkyGardientBrush, btnWallGardientBrush, btnGrassGardientBrush;
    private FloatingActionButton btnShare;
    private RelativeLayout drawingRelativeLayout;
    private TextView txt_Watermark;
    LinearLayout linContainer;
    private static Toast startDrawingToast;

    private static final int colors[] = {Color.WHITE, Color.BLUE, Color.CYAN, Color.GREEN, Color.MAGENTA, Color.RED, Color.YELLOW, Color.BLACK};
    private static final int definedWidths[] = {0, 5, 10, 15, 20};
    private int paintThickness = 10;
    private int currentBGColor = Color.BLACK;
    private int currentPaintColor = Color.WHITE;
    public String currentBrushType = NORMAL_BRUSH;

    private DrawAndFunPreferences mySavePreferences;

    DrawView drawView;
    int afterPermissionIndex;

    private static String currentVer = null;

    private RelativeLayout fragLayout, bottomLayout, extraOptionsLayout, drawCircleLayout, onlyCircleLayout, mainActivityLayout
            , drawRectLayout, onlyRectLayout, drawTriLayout, onlyTriLayout, drawStarLayout, onlyStarLayout;

    private File lastSharedFile;
    private boolean isBottomLayoutOpen = true;
    /*
          1 = Saving drawing
          2 = Sharing drawing
          3 = Opening Gallery in App
    */


    private ContextMenuDialogFragment mMenuDialogFragment, mMenuDialogFragForPaintColor, mMenuDialogFragForPaintWidth,
            mMenuDialogFragForExtraOptions;
    private FragmentManager fragmentManager;

    private float shape_x_coord = 100, shape_y_coord = 100;
    int shape_size =100, shape_color = -111, shape_width, shape_height, shape_size_ellipsis = 100;
    boolean isShapeFill = false;
    public int device_Width, device_Height, ad_Height;
    int actionBarHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        mySavePreferences = DrawAndFunPreferences.getInstance(this);
        initUI();
        initEvents();
        txt_Watermark.setVisibility(View.INVISIBLE);

        if(isInternetOn()){
            updateApp();
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        device_Height = displayMetrics.heightPixels;
        device_Width = displayMetrics.widthPixels;

        TypedValue tv = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);
        actionBarHeight = getResources().getDimensionPixelSize(tv.resourceId);
    }

    private void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            // Update UI to reflect image being shared
            Bitmap myBitmap = BitmapFactory.decodeFile(imageUri.getPath());
            Drawable d = new BitmapDrawable(getResources(), myBitmap);
            drawView.setBackground(d);
            startDrawingToast.show();

            currentBGColor = -111; //FOR BACKGROUND PICTURES
            btnEraser.setAlpha(0.4f);//Setting deactivated eraser image
        }
    }

    public void initUI(){
        drawingRelativeLayout = (RelativeLayout) findViewById(R.id.myDrawLayout) ;
        fragLayout = (RelativeLayout) findViewById(R.id.fragLayout) ;
        bottomLayout = (RelativeLayout) findViewById(R.id.bottomLayout) ;
        extraOptionsLayout = (RelativeLayout) findViewById(R.id.extraOptionsLayout) ;
        drawCircleLayout = (RelativeLayout) findViewById(R.id.draw_circle_layout) ;
        drawRectLayout = (RelativeLayout) findViewById(R.id.draw_rect_layout) ;
        drawTriLayout = (RelativeLayout) findViewById(R.id.draw_tri_layout) ;
        drawStarLayout = (RelativeLayout) findViewById(R.id.draw_star_layout) ;
        onlyCircleLayout  = (RelativeLayout) findViewById(R.id.circleLayout) ;
        onlyRectLayout = (RelativeLayout) findViewById(R.id.rectLayout);
        onlyTriLayout = (RelativeLayout) findViewById(R.id.triLayout);
        onlyStarLayout = (RelativeLayout) findViewById(R.id.starLayout);
        mainActivityLayout  = (RelativeLayout) findViewById(R.id.mainActivityLayout) ;
        fragLayout.bringToFront();
        bottomLayout.bringToFront();
        extraOptionsLayout.bringToFront();
        linContainer = (LinearLayout) findViewById(R.id.linDrawViewContainer);

        //SHOW AND HIDE THE AD FRAGMENT
        if(!isInternetOn()){
            fragLayout.setVisibility(View.GONE);
        }
        else{
            fragLayout.setVisibility(View.VISIBLE);
        }

        // Set full screen view

        // lock screen orientation (stops screen clearing when rotating phone)
        setRequestedOrientation(getResources().getConfiguration().orientation);

        mainActivityInstance = this;
        getSupportActionBar().hide();
        //optional

        MobileAds.initialize(this, getResources().getString(R.string.admobappID));

        permissionStatus = getSharedPreferences("whatstracepreferneces",MODE_PRIVATE);

        drawView = new DrawView(this);

        linContainer.addView(drawView);

        startDrawingToast = Toast.makeText(mainActivityInstance, "Start Drawing..", Toast.LENGTH_LONG);
        startDrawingToast.setGravity(Gravity.CENTER,0,0);

        btnEraser = (ImageView) findViewById(R.id.btn_changeBG);
        btnChangeDrawColor = (ImageView) findViewById(R.id.btn_changeDrawColor);
        btnChangeDrawWidth = (ImageView) findViewById(R.id.btn_changeDrawWidth);
        btnRedo = (ImageView) findViewById(R.id.btn_redo);
        btnUndo = (ImageView) findViewById(R.id.btn_undo);
        btnOption = (ImageView) findViewById(R.id.btn_option);
        btnDummyOption = (ImageView) findViewById(R.id.btn_option_dummy);
        btnShareDummy = (ImageView) findViewById(R.id.dummyTxt);
        btnCircleClose = (ImageView) findViewById(R.id.btn_circle_close);
        btnCircleColor = (ImageView) findViewById(R.id.btn_circle_fillcolor);
        btnCircleFillUnfill = (ImageView) findViewById(R.id.btn_circle_fill);
        btnCircleDrag = (ImageView) findViewById(R.id.btn_circle_drag);
        btnCircleOk = (Button) findViewById(R.id.btnOK);
        btnCircleStretch = (ImageView) findViewById(R.id.btn_circle_size);
        btnCircleStretchVertical = (ImageView) findViewById(R.id.btn_circle_stretch_vertical);

        btnRectClose = (ImageView) findViewById(R.id.btn_rect_close);
        btnRectColor = (ImageView) findViewById(R.id.btn_rect_fillcolor);
        btnRectDrag = (ImageView) findViewById(R.id.btn_rect_drag);
        btnRectStretch = (ImageView) findViewById(R.id.btn_rect_size);
        btnRectStretchVertical = (ImageView) findViewById(R.id.btn_rect_stretch_height);
        btnRectFillUnfill = (ImageView) findViewById(R.id.btn_rect_fill);
        btnRectOk = (Button) findViewById(R.id.btn_rect_OK);

        btnTriClose = (ImageView) findViewById(R.id.btn_tri_close);
        btnTriColor = (ImageView) findViewById(R.id.btn_tri_fillcolor);
        btnTriDrag = (ImageView) findViewById(R.id.btn_tri_drag);
        btnTriStretch = (ImageView) findViewById(R.id.btn_tri_size);
        btnTriStretchVertical = (ImageView) findViewById(R.id.btn_tri_stretch_height);
        btnTriFillUnfill = (ImageView) findViewById(R.id.btn_tri_fill);
        btnTriOk = (Button) findViewById(R.id.btn_tri_OK);

        btnStarClose = (ImageView) findViewById(R.id.btn_star_close);
        btnStarColor = (ImageView) findViewById(R.id.btn_star_fillcolor);
        btnStarDrag = (ImageView) findViewById(R.id.btn_star_drag);
        btnStarStretch = (ImageView) findViewById(R.id.btn_star_size);
        btnStarStretchVertical = (ImageView) findViewById(R.id.btn_star_stretch_height);
        btnStarFillUnfill = (ImageView) findViewById(R.id.btn_star_fill);
        btnStarOk = (Button) findViewById(R.id.btn_star_OK);

        btnHideTopBottomMenu = (ImageView) findViewById(R.id.btn_hide_bottom_top_menu);
        btnDummyOption.bringToFront();

        btnNew = (TextView) findViewById(R.id.btn_new_canvas);
        btnSave = (TextView) findViewById(R.id.btn_save);
        btnGallery = (TextView) findViewById(R.id.btn_gallery);
        btnClearCanvas = (TextView) findViewById(R.id.btn_clear);
        btnRateApp = (TextView) findViewById(R.id.btn_rateapp);
        btnDrawCircle = (TextView) findViewById(R.id.btn_draw_circle);
        btnDrawRect = (TextView) findViewById(R.id.btn_draw_rect);
        btnDrawTriangle = (TextView) findViewById(R.id.btn_draw_triangle);
        btnDrawStar = (TextView) findViewById(R.id.btn_draw_star);
        btnNormalBrush = (TextView) findViewById(R.id.btn_normal_brush);
        btnHeartBrush = (TextView) findViewById(R.id.btn_brush_heart);
        btnStarBrush = (TextView) findViewById(R.id.btn_brush_star);
        btnMultiStarBrush = (TextView) findViewById(R.id.btn_multistar_brush);
        btnLoveSmileyBrush = (TextView) findViewById(R.id.btn_lovesmiley_brush);
        btnCoolSmileyBrush = (TextView) findViewById(R.id.btn_coolsmiley_brush);
        btnWinkSmileyBrush = (TextView) findViewById(R.id.btn_winksmiley_brush);
        btnSkyGardientBrush = (TextView) findViewById(R.id.btn_skygradient_brush);
        btnWallGardientBrush = (TextView) findViewById(R.id.btn_wallgradient_brush);
        btnGrassGardientBrush = (TextView) findViewById(R.id.btn_grassgradient_brush);
        btnFlowerBrush = (TextView) findViewById(R.id.btn_flower_brush);
        btnWineBrush = (TextView) findViewById(R.id.btn_wine_brush);

        txt_Watermark = (TextView) findViewById(R.id.appWatermark);

        btnShare = (FloatingActionButton) findViewById(R.id.fab);


        //CHECK PREV VALUES
        currentBGColor = mySavePreferences.getBgColor();
        currentPaintColor = mySavePreferences.getPaintColor();

        drawView.setBackgroundColor(currentBGColor);
        drawView.changeColour(99, currentPaintColor);
        drawView.requestFocus();

        setBrushesActive(); //CHECK AND SET THE BRUSHES ACTIVE

        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.tool_bar_height));
        menuParams.setMenuObjects(getMenuObjectsForBGColor());
        menuParams.setAnimationDuration(15);
        menuParams.setClosableOutside(true);
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
        mMenuDialogFragment.setItemClickListener(new OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(View clickedView, int position) {
                if(position>0 && position<9) {//PREDEFINED BACKGPRUND COLORS
                        currentBGColor = colors[position-1];
                        mySavePreferences.setBgColor(currentBGColor);
                        btnEraser.setAlpha(1.0f);//Setting activated eraser image
                        drawView.setBackgroundColor(currentBGColor);
                        drawView.changeWidth(10);
                        drawView.clearPoints();
                        startDrawingToast.show();
                        if(currentBGColor == Color.BLACK){
                            if(currentPaintColor == Color.BLACK) {
                                currentPaintColor = Color.WHITE;
                                drawView.changeColour(0); //CHANGING TO WHITE
                            }
                        }
                        else{
                            if(currentPaintColor == Color.WHITE) {
                                currentPaintColor = Color.BLACK;
                                drawView.changeColour(7); //CHANGING TO BLACK
                            }
                        }
                        mySavePreferences.setPaintColor(currentPaintColor);
                }
                else if(position==10){ //CUSTOM BACKGROUND IMAGES
                    setCustomBackground(drawView);
                }
                else if(position==9){ //CUSTOM BACKGROUND COLORS
                    changeCustomBgColor();
                }
            }
        });

        MenuParams menuParams1 = new MenuParams();
        menuParams1.setActionBarSize((int) getResources().getDimension(R.dimen.tool_bar_height));
        menuParams1.setMenuObjects(getMenuObjectsForPaintColor());
        menuParams1.setAnimationDuration(15);
        menuParams1.setClosableOutside(true);
        mMenuDialogFragForPaintColor = ContextMenuDialogFragment.newInstance(menuParams1);
        mMenuDialogFragForPaintColor.setItemClickListener(new OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(View clickedView, int position) {
                if(position>0 && position<9) {//PREDEFINED PAINT COLORS
                    int tempColor = colors[position-1];
                        currentPaintColor = colors[position-1];
                        drawView.changeColour(position - 1);
                        mySavePreferences.setPaintColor(currentPaintColor);
                    if(tempColor==currentBGColor){
                        Toast.makeText(MainActivity.this, VALIDATION_MESSAGE, Toast.LENGTH_LONG).show();
                    }
                }
                else if(position==9){//CUSTOM PAINT COLORS
                    changeCustomPaintColor();
                }
            }
        });

        MenuParams menuParams2 = new MenuParams();
        menuParams2.setActionBarSize((int) getResources().getDimension(R.dimen.tool_bar_height));
        menuParams2.setMenuObjects(getMenuObjectsForPaintWidth());
        menuParams2.setAnimationDuration(15);
        menuParams2.setClosableOutside(true);
        mMenuDialogFragForPaintWidth = ContextMenuDialogFragment.newInstance(menuParams2);
        mMenuDialogFragForPaintWidth.setItemClickListener(new OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(View clickedView, int position) {
                if(position>0 && position<6) { //PREDEFINED WIDTHS
                    paintThickness = definedWidths[position - 1];
                    drawView.changeWidth(paintThickness);
                }
                else if(position==6){//CUSTOM WIDTHS
                    changeCustomPaintWidthDialog();
                }
            }
        });

        //Get intent, action and MIME type iF COMES FROM GALLERY
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendImage(intent); // Handle single image being sent
            }
        }
        else{
            mMenuDialogFragment.show(fragmentManager, ContextMenuDialogFragment.TAG);
        }

        checkUndoIcon();
        checkRedoIcon();

        //Set the ad layout background color
        fragLayout.setBackgroundColor(currentBGColor);
    }

    //CHECK AND SET THE BRUSHES ACTIVE
    public void setBrushesActive(){
        currentBrushType = mySavePreferences.getBrushType();
        if(currentBrushType.equalsIgnoreCase(NORMAL_BRUSH)){
            btnNormalBrush.setBackground(getResources().getDrawable(R.drawable.options_heading_bg_without_border));
        }
        else{
            btnNormalBrush.setBackground(null);
        }

        if(currentBrushType.equalsIgnoreCase(HEART_BRUSH)){
            btnHeartBrush.setBackground(getResources().getDrawable(R.drawable.options_heading_bg_without_border));
        }
        else{
            btnHeartBrush.setBackground(null);
        }

        if(currentBrushType.equalsIgnoreCase(STAR_BRUSH)){
            btnStarBrush.setBackground(getResources().getDrawable(R.drawable.options_heading_bg_without_border));
        }
        else{
            btnStarBrush.setBackground(null);
        }

        if(currentBrushType.equalsIgnoreCase(MULTISTAR_BRUSH)){
            btnMultiStarBrush.setBackground(getResources().getDrawable(R.drawable.options_heading_bg_without_border));
        }
        else{
            btnMultiStarBrush.setBackground(null);
        }

        if(currentBrushType.equalsIgnoreCase(LOVE_SMILEY_BRUSH)){
            btnLoveSmileyBrush.setBackground(getResources().getDrawable(R.drawable.options_heading_bg_without_border));
        }
        else{
            btnLoveSmileyBrush.setBackground(null);
        }

        if(currentBrushType.equalsIgnoreCase(WINK_SMILEY_BRUSH)){
            btnWinkSmileyBrush.setBackground(getResources().getDrawable(R.drawable.options_heading_bg_without_border));
        }
        else{
            btnWinkSmileyBrush.setBackground(null);
        }

        if(currentBrushType.equalsIgnoreCase(COOL_SMILEY_BRUSH)){
            btnCoolSmileyBrush.setBackground(getResources().getDrawable(R.drawable.options_heading_bg_without_border));
        }
        else{
            btnCoolSmileyBrush.setBackground(null);
        }

        if(currentBrushType.equalsIgnoreCase(WINE_BRUSH)){
            btnWineBrush.setBackground(getResources().getDrawable(R.drawable.options_heading_bg_without_border));
        }
        else{
            btnWineBrush.setBackground(null);
        }

        if(currentBrushType.equalsIgnoreCase(FLOWER_BRUSH)){
            btnFlowerBrush.setBackground(getResources().getDrawable(R.drawable.options_heading_bg_without_border));
        }
        else{
            btnFlowerBrush.setBackground(null);
        }

        if(currentBrushType.equalsIgnoreCase(SKY_GRADIENT_BRUSH)){
            btnSkyGardientBrush.setBackground(getResources().getDrawable(R.drawable.options_heading_bg_without_border));
        }
        else{
            btnSkyGardientBrush.setBackground(null);
        }

        if(currentBrushType.equalsIgnoreCase(GRASS_GRADIENT_BRUSH)){
            btnGrassGardientBrush.setBackground(getResources().getDrawable(R.drawable.options_heading_bg_without_border));
        }
        else{
            btnGrassGardientBrush.setBackground(null);
        }

        if(currentBrushType.equalsIgnoreCase(WALL_GRADIENT_BRUSH)){
            btnWallGardientBrush.setBackground(getResources().getDrawable(R.drawable.options_heading_bg_without_border));
        }
        else{
            btnWallGardientBrush.setBackground(null);
        }
    }

    public void hideExtraOptionsWithAnimation(){
        if(extraOptionsLayout.getAlpha()!=0f) {
            extraOptionsLayout.animate().alpha(0f).setDuration(10);
            btnDummyOption.animate().alpha(0f).setDuration(10);
            btnShare.animate().translationY(0f).setDuration(100);
            btnShareDummy.animate().alpha(0f).setDuration(100);
            extraOptionsLayout.setVisibility(View.GONE);
            btnDummyOption.setVisibility(View.GONE);
        }
    }

    public void showExtraOptionsWithAnimation(){
        if(extraOptionsLayout.getAlpha()!=1f) {
            extraOptionsLayout.setVisibility(View.VISIBLE);
            btnDummyOption.setVisibility(View.VISIBLE);
            extraOptionsLayout.animate().alpha(1f).setDuration(300);
            btnDummyOption.animate().alpha(1f).setDuration(300);
            btnShare.animate().translationY(400f).setDuration(300);
            btnShareDummy.animate().alpha(1f).setDuration(100);
        }
    }

    public void initEvents(){
        btnChangeDrawWidth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideExtraOptionsWithAnimation();
                mMenuDialogFragForPaintWidth.show(fragmentManager, ContextMenuDialogFragment.TAG);
            }
        });

        btnChangeDrawColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideExtraOptionsWithAnimation();
                drawView.setEraserMode(false, currentBGColor);
                drawView.changeWidth(paintThickness);
                btnEraser.setImageDrawable(getResources().getDrawable(R.mipmap.bgcolor));
                mMenuDialogFragForPaintColor.show(fragmentManager, ContextMenuDialogFragment.TAG);
            }
        });

        btnEraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideExtraOptionsWithAnimation();
                if(currentBGColor != -111) {
                    if(drawView.getEraserMode()) {
                        drawView.setEraserMode(false, currentBGColor);
                        drawView.changeWidth(paintThickness);
                        btnEraser.setImageDrawable(getResources().getDrawable(R.mipmap.bgcolor));
                        Toast.makeText(MainActivity.this, "Eraser Mode off", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        drawView.changeWidth(30);
                        btnEraser.setImageDrawable(getResources().getDrawable(R.mipmap.eraser_mode_on));
                        drawView.setEraserMode(true, currentBGColor);
                        Toast.makeText(MainActivity.this, "Eraser Mode on", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(MainActivity.this, "Eraser not available for the background!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideExtraOptionsWithAnimation();
                afterPermissionIndex = 2;
                txt_Watermark.setVisibility(View.VISIBLE);
                askPStorageStatePermissions();
            }
        });

        btnShareDummy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideExtraOptionsWithAnimation();
                afterPermissionIndex = 2;
                txt_Watermark.setVisibility(View.VISIBLE);
                askPStorageStatePermissions();
            }
        });

        btnRedo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        hideExtraOptionsWithAnimation();
                        drawView.redoPoints();
                        checkRedoIcon();
                        checkUndoIcon();
            }
        });

        btnUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideExtraOptionsWithAnimation();
                drawView.undoPoints();
                checkUndoIcon();
                checkRedoIcon();
            }
        });

        btnOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(extraOptionsLayout.getAlpha()==0f) {
                    showExtraOptionsWithAnimation();
                }
                else{
                    hideExtraOptionsWithAnimation();
                }
            }
        });

        btnDummyOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(extraOptionsLayout.getAlpha()==0f) {
                    showExtraOptionsWithAnimation();
                }
                else{
                    hideExtraOptionsWithAnimation();
                }
            }
        });

        btnHideTopBottomMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isBottomLayoutOpen){
                    animateCloseBottomLayout();
                    //animateCloseTopLayout();
                    btnHideTopBottomMenu.animate().translationY(actionBarHeight).setDuration(500).start();
                    btnHideTopBottomMenu.animate().rotationBy(180).setInterpolator(new LinearInterpolator()).setDuration(500).start();
                    isBottomLayoutOpen = false;
                }
                else{
                    animateOpenBottomLayout();
                    //animateOpenTopLayout();
                    btnHideTopBottomMenu.animate().translationY(0).setDuration(200).start();
                    btnHideTopBottomMenu.animate().rotation(0).setInterpolator(new LinearInterpolator()).setDuration(200).start();
                    isBottomLayoutOpen = true;
                }
            }
        });

        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideExtraOptionsWithAnimation();
                if(drawView.getUndoPointArraySize()!=0) {
                    showConfirmDialogForClear();
                }
                else {
                    drawView.clearPoints();
                    drawView.allowDrawing(true);
                    drawCircleLayout.setVisibility(View.GONE);
                    mMenuDialogFragment.show(fragmentManager, ContextMenuDialogFragment.TAG);
                    checkUndoIcon();
                    checkRedoIcon();
                    drawView.setEraserMode(false, currentBGColor);
                    drawView.changeWidth(paintThickness);
                    mySavePreferences.setBrushType(NORMAL_BRUSH);
                    setBrushesActive();
                    btnEraser.setImageDrawable(getResources().getDrawable(R.mipmap.bgcolor));
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideExtraOptionsWithAnimation();
                afterPermissionIndex = 1;
                txt_Watermark.setVisibility(View.VISIBLE);
                askPStorageStatePermissions();
            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideExtraOptionsWithAnimation();
                afterPermissionIndex = 3;
                askPStorageStatePermissions();
            }
        });

        btnClearCanvas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideExtraOptionsWithAnimation();
                if(drawView!=null){
                    drawView.clearPoints();
                    checkUndoIcon();
                    checkRedoIcon();
                }
            }
        });

        btnRateApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideExtraOptionsWithAnimation();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=com.asisdroid.drawfun"));
                startActivity(intent);
            }
        });

        btnNormalBrush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mySavePreferences.setBrushType(NORMAL_BRUSH);
                setBrushesActive();
                hideExtraOptionsWithAnimation();
            }
        });

        btnMultiStarBrush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mySavePreferences.setBrushType(MULTISTAR_BRUSH);
                setBrushesActive();
                hideExtraOptionsWithAnimation();
            }
        });

        btnLoveSmileyBrush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mySavePreferences.setBrushType(LOVE_SMILEY_BRUSH);
                setBrushesActive();
                hideExtraOptionsWithAnimation();
            }
        });

        btnCoolSmileyBrush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mySavePreferences.setBrushType(COOL_SMILEY_BRUSH);
                setBrushesActive();
                hideExtraOptionsWithAnimation();
            }
        });

        btnWineBrush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mySavePreferences.setBrushType(WINE_BRUSH);
                setBrushesActive();
                hideExtraOptionsWithAnimation();
            }
        });

        btnFlowerBrush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mySavePreferences.setBrushType(FLOWER_BRUSH);
                setBrushesActive();
                hideExtraOptionsWithAnimation();
            }
        });

        btnWinkSmileyBrush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mySavePreferences.setBrushType(WINK_SMILEY_BRUSH);
                setBrushesActive();
                hideExtraOptionsWithAnimation();
            }
        });

        btnSkyGardientBrush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mySavePreferences.setBrushType(SKY_GRADIENT_BRUSH);
                setBrushesActive();
                hideExtraOptionsWithAnimation();
            }
        });

        btnWallGardientBrush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mySavePreferences.setBrushType(WALL_GRADIENT_BRUSH);
                setBrushesActive();
                hideExtraOptionsWithAnimation();
            }
        });

        btnGrassGardientBrush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mySavePreferences.setBrushType(GRASS_GRADIENT_BRUSH);
                setBrushesActive();
                hideExtraOptionsWithAnimation();
            }
        });

        btnStarBrush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mySavePreferences.setBrushType(STAR_BRUSH);
                setBrushesActive();
                hideExtraOptionsWithAnimation();
            }
        });

        btnHeartBrush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mySavePreferences.setBrushType(HEART_BRUSH);
                setBrushesActive();
                hideExtraOptionsWithAnimation();
            }
        });

        btnDrawCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(drawCircleLayout.getVisibility() == View.GONE) {
                    hideExtraOptionsWithAnimation();
                    drawCircleLayout.bringToFront();
                    drawCircleLayout.setVisibility(View.VISIBLE);
                    drawView.setEraserMode(false, currentBGColor);
                    drawView.changeWidth(paintThickness);
                    btnEraser.setImageDrawable(getResources().getDrawable(R.mipmap.bgcolor));
                    drawView.allowDrawing(false); //preventing user to draw
                }
                else{
                    hideExtraOptionsWithAnimation();
                    drawCircleLayout.setVisibility(View.GONE);
                    drawView.allowDrawing(true); //preventing user to draw
                }
                btnHideTopBottomMenu.setVisibility(View.GONE);
                btnHideTopBottomMenu.animate().translationY(actionBarHeight).setDuration(500).start();
                btnHideTopBottomMenu.animate().rotationBy(180).setInterpolator(new LinearInterpolator()).setDuration(500).start();
                animateCloseBottomLayout();
            }
        });

        btnCircleClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(drawCircleLayout.getVisibility() == View.VISIBLE) {
                    drawCircleLayout.setVisibility(View.GONE);
                    shape_color = -111;
                    isShapeFill = false;
                    btnCircleFillUnfill.setImageResource(R.mipmap.circle_unfill);
                    drawView.allowDrawing(true); //Allowing user to draw
                }
                animateOpenBottomLayout();
                btnHideTopBottomMenu.setVisibility(View.VISIBLE);
                btnHideTopBottomMenu.animate().translationY(0).setDuration(200).start();
                btnHideTopBottomMenu.animate().rotation(0).setInterpolator(new LinearInterpolator()).setDuration(200).start();
            }
        });


        btnCircleDrag.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float dX =0, dY=0;
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_MOVE:
                    {
                        /*drawCircleLayout.animate()
                                .x((event.getRawX() + dX) - (drawCircleLayout.getWidth()/2))
                                .y((event.getRawY() + dY) - (drawCircleLayout.getHeight()))
                                .setDuration(0)
                                .start();*/
                        drawCircleLayout.setX((event.getRawX() + dX) - (drawCircleLayout.getWidth()/2));
                        drawCircleLayout.setY((event.getRawY() + dY) - (drawCircleLayout.getHeight()));
                        //Log.d("asha","move"+shape_x_coord+", "+shape_y_coord);
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    {
                        //Log.d("asha","up"+shape_x_coord+", "+shape_y_coord);
                        break;
                    }
                    case MotionEvent.ACTION_DOWN:
                    {
                        //	dragParam.height = 100;
                        //	dragParam.width = 100;
                        Log.d("asha","down");
                        dX = drawCircleLayout.getX();
                        dY = drawCircleLayout.getY();
                        break;
                    }
                }
                return true;
            }
        });

        onlyCircleLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                float dX =0, dY=0;
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_MOVE:
                    {
                        /*drawCircleLayout.animate()
                                .x((event.getRawX() + dX) - (drawCircleLayout.getWidth()/2))
                                .y((event.getRawY() + dY) - (drawCircleLayout.getHeight()))
                                .setDuration(0)
                                .start();*/
                        drawCircleLayout.setX((event.getRawX() + dX) - (drawCircleLayout.getWidth()/2));
                        drawCircleLayout.setY((event.getRawY() + dY) - (drawCircleLayout.getHeight()));
                        //Log.d("asha","move"+shape_x_coord+", "+shape_y_coord);
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    {
                        //Log.d("asha","up"+shape_x_coord+", "+shape_y_coord);
                        break;
                    }
                    case MotionEvent.ACTION_DOWN:
                    {
                        //	dragParam.height = 100;
                        //	dragParam.width = 100;
                        Log.d("asha","down");
                        dX = drawCircleLayout.getX();
                        dY = drawCircleLayout.getY();
                        break;
                    }
                }
                return true;
            }
        });

        btnCircleStretch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                RelativeLayout.LayoutParams dragParam = (RelativeLayout.LayoutParams) onlyCircleLayout.getLayoutParams();
                int startX=(int) v.getX();
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_MOVE:
                    {
                        //int newSize = dragParam.height = onlyCircleLayout.getWidth() + (((int) event.getRawX()) - startX);;
                        int newSize =  onlyCircleLayout.getWidth() + (((int) event.getRawX()) - startX);;
                       // Log.d("asha","move="+(startX)+"---"+ (int) event.getRawX()+"=="+newSize);
                        if(newSize>dpToPx(32) && newSize<(device_Width-dpToPx(110))) {
                            dragParam.width = newSize;
                            onlyCircleLayout.setLayoutParams(dragParam);
                            shape_size_ellipsis = shape_size = dragParam.width;
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    {
                        break;
                    }
                    case MotionEvent.ACTION_DOWN:
                    {
                        break;
                    }
                }
                return true;
            }
        });

        btnCircleStretchVertical.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                RelativeLayout.LayoutParams dragParam = (RelativeLayout.LayoutParams) onlyCircleLayout.getLayoutParams();
                int[] tempPose = new int[2];
                v.getLocationInWindow(tempPose);
                int startY=(int) tempPose[1];
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_MOVE:
                    {
                        int newSize = onlyCircleLayout.getHeight() + (((int) event.getRawY()) - startY);;
                         Log.d("asha","move vert="+(startY)+"---"+ (int) event.getRawY()+"=="+newSize);
                        if(newSize>dpToPx(32) && newSize<(device_Height-dpToPx(110))) {
                            dragParam.height = newSize;
                            onlyCircleLayout.setLayoutParams(dragParam);
                            shape_size_ellipsis = dragParam.height;
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    {
                        break;
                    }
                    case MotionEvent.ACTION_DOWN:
                    {
                        break;
                    }
                }
                return true;
            }
        });

        btnCircleColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPickerDialogBuilder
                        .with(mainActivityInstance)
                        .setTitle("Choose Circle Draw Color")
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
                            }
                        })
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                shape_color = selectedColor;
                                //Log.d("asha","select color="+shape_color);
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .build()
                        .show();
            }
        });

        btnCircleFillUnfill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isShapeFill){
                    btnCircleFillUnfill.setImageResource(R.mipmap.circle_unfill);
                    isShapeFill = false;
                }
                else{
                    btnCircleFillUnfill.setImageResource(R.mipmap.circle);
                    isShapeFill = true;
                }
            }
        });

        btnCircleOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(shape_color==-111) {
                    shape_color = currentPaintColor;
                }
                shape_size = onlyCircleLayout.getWidth();
                shape_size_ellipsis = onlyCircleLayout.getHeight();
                int[] oldFolderCellPosition = new int[2];
                onlyCircleLayout.getLocationInWindow(oldFolderCellPosition);
                shape_x_coord = oldFolderCellPosition[0]+(shape_size/2);
                shape_y_coord = oldFolderCellPosition[1]+(shape_size_ellipsis/2);
                ad_Height = fragLayout.getHeight();
                drawView.drawCircleInCanvas(shape_x_coord, shape_y_coord - ad_Height, shape_size/2, shape_size_ellipsis/2,  shape_color, isShapeFill);
                //AD HEIGHT IS SUBTRACTED FORM Y-COORDINATE ON ABOVE LINE IS DONE TO BALANCE THE SCREEN AS THE SCREEN STARTS BELOW THE AD.
            }
        });

        btnDrawStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(drawStarLayout.getVisibility() == View.GONE) {
                    hideExtraOptionsWithAnimation();
                    drawStarLayout.bringToFront();
                    drawStarLayout.setVisibility(View.VISIBLE);
                    drawView.setEraserMode(false, currentBGColor);
                    drawView.changeWidth(paintThickness);
                    btnEraser.setImageDrawable(getResources().getDrawable(R.mipmap.bgcolor));
                    drawView.allowDrawing(false); //preventing user to draw
                }
                else{
                    hideExtraOptionsWithAnimation();
                    drawStarLayout.setVisibility(View.GONE);
                    drawView.allowDrawing(true); //preventing user to draw
                }
                animateCloseBottomLayout();
                btnHideTopBottomMenu.animate().translationY(actionBarHeight).setDuration(500).start();
                btnHideTopBottomMenu.animate().rotationBy(180).setInterpolator(new LinearInterpolator()).setDuration(500).start();
                btnHideTopBottomMenu.setVisibility(View.GONE);
            }
        });

        btnStarClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(drawStarLayout.getVisibility() == View.VISIBLE) {
                    drawStarLayout.setVisibility(View.GONE);
                    shape_color = -111;
                    isShapeFill = false;
                    btnStarFillUnfill.setImageResource(R.mipmap.star_unfill);
                    drawView.allowDrawing(true); //Allowing user to draw
                }
                animateOpenBottomLayout();
                btnHideTopBottomMenu.setVisibility(View.VISIBLE);
                btnHideTopBottomMenu.animate().translationY(0).setDuration(200).start();
                btnHideTopBottomMenu.animate().rotation(0).setInterpolator(new LinearInterpolator()).setDuration(200).start();
            }
        });


        btnStarDrag.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float dX =0, dY=0;
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_MOVE:
                    {
                        /*drawCircleLayout.animate()
                                .x((event.getRawX() + dX) - (drawCircleLayout.getWidth()/2))
                                .y((event.getRawY() + dY) - (drawCircleLayout.getHeight()))
                                .setDuration(0)
                                .start();*/
                        drawStarLayout.setX((event.getRawX() + dX) - (drawStarLayout.getWidth()/2));
                        drawStarLayout.setY((event.getRawY() + dY) - (drawStarLayout.getHeight()));
                        //Log.d("asha","move"+shape_x_coord+", "+shape_y_coord);
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    {
                        //Log.d("asha","up"+shape_x_coord+", "+shape_y_coord);
                        break;
                    }
                    case MotionEvent.ACTION_DOWN:
                    {
                        //	dragParam.height = 100;
                        //	dragParam.width = 100;
                        Log.d("asha","down");
                        dX = drawStarLayout.getX();
                        dY = drawStarLayout.getY();
                        break;
                    }
                }
                return true;
            }
        });

        onlyStarLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                float dX =0, dY=0;
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_MOVE:
                    {
                        /*drawCircleLayout.animate()
                                .x((event.getRawX() + dX) - (drawCircleLayout.getWidth()/2))
                                .y((event.getRawY() + dY) - (drawCircleLayout.getHeight()))
                                .setDuration(0)
                                .start();*/
                        drawStarLayout.setX((event.getRawX() + dX) - (drawStarLayout.getWidth()/2));
                        drawStarLayout.setY((event.getRawY() + dY) - (drawStarLayout.getHeight()));
                        //Log.d("asha","move"+shape_x_coord+", "+shape_y_coord);
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    {
                        //Log.d("asha","up"+shape_x_coord+", "+shape_y_coord);
                        break;
                    }
                    case MotionEvent.ACTION_DOWN:
                    {
                        //	dragParam.height = 100;
                        //	dragParam.width = 100;
                        Log.d("asha","down");
                        dX = drawStarLayout.getX();
                        dY = drawStarLayout.getY();
                        break;
                    }
                }
                return true;
            }
        });

        btnStarStretch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                RelativeLayout.LayoutParams dragParam = (RelativeLayout.LayoutParams) onlyStarLayout.getLayoutParams();
                int startX=(int) v.getX();
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_MOVE:
                    {
                        //int newSize = dragParam.height = onlyCircleLayout.getWidth() + (((int) event.getRawX()) - startX);;
                        int newSize =  onlyStarLayout.getWidth() + (((int) event.getRawX()) - startX);;
                        // Log.d("asha","move="+(startX)+"---"+ (int) event.getRawX()+"=="+newSize);
                        if(newSize>dpToPx(32) && newSize<(device_Width-dpToPx(110))) {
                            dragParam.width = newSize;
                            onlyStarLayout.setLayoutParams(dragParam);
                            shape_size_ellipsis = shape_size = dragParam.width;
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    {
                        break;
                    }
                    case MotionEvent.ACTION_DOWN:
                    {
                        break;
                    }
                }
                return true;
            }
        });

        btnStarStretchVertical.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                RelativeLayout.LayoutParams dragParam = (RelativeLayout.LayoutParams) onlyStarLayout.getLayoutParams();
                int[] tempPose = new int[2];
                v.getLocationInWindow(tempPose);
                int startY=(int) tempPose[1];
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_MOVE:
                    {
                        int newSize = onlyStarLayout.getHeight() + (((int) event.getRawY()) - startY);;
                        Log.d("asha","move vert="+(startY)+"---"+ (int) event.getRawY()+"=="+newSize);
                        if(newSize>dpToPx(32) && newSize<(device_Height-dpToPx(110))) {
                            dragParam.height = newSize;
                            onlyStarLayout.setLayoutParams(dragParam);
                            shape_size_ellipsis = dragParam.height;
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    {
                        break;
                    }
                    case MotionEvent.ACTION_DOWN:
                    {
                        break;
                    }
                }
                return true;
            }
        });

        btnStarColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPickerDialogBuilder
                        .with(mainActivityInstance)
                        .setTitle("Choose Circle Draw Color")
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
                            }
                        })
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                shape_color = selectedColor;
                                //Log.d("asha","select color="+shape_color);
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .build()
                        .show();
            }
        });

        btnStarFillUnfill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isShapeFill){
                    btnStarFillUnfill.setImageResource(R.mipmap.star_unfill);
                    isShapeFill = false;
                }
                else{
                    btnStarFillUnfill.setImageResource(R.mipmap.star_fill);
                    isShapeFill = true;
                }
            }
        });

        btnStarOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(shape_color==-111) {
                    shape_color = currentPaintColor;
                }
                shape_size = onlyStarLayout.getWidth();
                shape_size_ellipsis = onlyStarLayout.getHeight();
                int[] oldFolderCellPosition = new int[2];
                onlyStarLayout.getLocationInWindow(oldFolderCellPosition);
                shape_x_coord = oldFolderCellPosition[0];
                shape_y_coord = oldFolderCellPosition[1];
                ad_Height = fragLayout.getHeight();
                drawView.drawStarInCanvas(shape_x_coord, shape_y_coord - ad_Height, shape_size, shape_size_ellipsis,  shape_color, isShapeFill);
                //AD HEIGHT IS SUBTRACTED FORM Y-COORDINATE ON ABOVE LINE IS DONE TO BALANCE THE SCREEN AS THE SCREEN STARTS BELOW THE AD.
            }
        });

        btnDrawTriangle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(drawTriLayout.getVisibility() == View.GONE) {
                    hideExtraOptionsWithAnimation();
                    drawTriLayout.bringToFront();
                    drawTriLayout.setVisibility(View.VISIBLE);
                    drawView.setEraserMode(false, currentBGColor);
                    drawView.changeWidth(paintThickness);
                    btnEraser.setImageDrawable(getResources().getDrawable(R.mipmap.bgcolor));
                    drawView.allowDrawing(false); //preventing user to draw
                }
                else{
                    hideExtraOptionsWithAnimation();
                    drawTriLayout.setVisibility(View.GONE);
                    drawView.allowDrawing(true); //preventing user to draw
                }
                animateCloseBottomLayout();
                btnHideTopBottomMenu.animate().translationY(actionBarHeight).setDuration(500).start();
                btnHideTopBottomMenu.animate().rotationBy(180).setInterpolator(new LinearInterpolator()).setDuration(500).start();
                btnHideTopBottomMenu.setVisibility(View.GONE);
            }
        });

        btnTriClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(drawTriLayout.getVisibility() == View.VISIBLE) {
                    drawTriLayout.setVisibility(View.GONE);
                    shape_color = -111;
                    isShapeFill = false;
                    btnTriFillUnfill.setImageResource(R.mipmap.triangle_unfill);
                    drawView.allowDrawing(true); //Allowing user to draw
                }
                animateOpenBottomLayout();
                btnHideTopBottomMenu.setVisibility(View.VISIBLE);
                btnHideTopBottomMenu.animate().translationY(0).setDuration(200).start();
                btnHideTopBottomMenu.animate().rotation(0).setInterpolator(new LinearInterpolator()).setDuration(200).start();
            }
        });

        btnTriStretch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                RelativeLayout.LayoutParams dragParam = (RelativeLayout.LayoutParams) onlyTriLayout.getLayoutParams();
                int startX=(int) v.getX();
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_MOVE:
                    {
                        Log.d("asha","move="+(startX)+"---"+ (int) event.getRawX());
                        int newWidth = onlyTriLayout.getWidth() + (((int) event.getRawX()) - startX);
                        if(newWidth>dpToPx(32) && newWidth<(device_Width-dpToPx(110))) {
                            dragParam.width = newWidth;
                            onlyTriLayout.setLayoutParams(dragParam);
                            shape_width = dragParam.width;
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    {
                        break;
                    }
                    case MotionEvent.ACTION_DOWN:
                    {
                        break;
                    }
                }
                return true;
            }
        });

        btnTriStretchVertical.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                RelativeLayout.LayoutParams dragParam = (RelativeLayout.LayoutParams) onlyTriLayout.getLayoutParams();

                int[] tempPos = new int[2];
                v.getLocationInWindow(tempPos);
                int startY=(int) tempPos[1];
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_MOVE:
                    {
                        Log.d("asha","move="+(startY)+"---"+ (int) event.getRawY());
                        int newHeight = onlyTriLayout.getHeight() + (((int) event.getRawY()) - startY);
                        if(newHeight>dpToPx(32)  && newHeight<(device_Height-dpToPx(110))) {
                            dragParam.height = newHeight;
                            onlyTriLayout.setLayoutParams(dragParam);
                            shape_height = dragParam.height;
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    {
                        break;
                    }
                    case MotionEvent.ACTION_DOWN:
                    {
                        break;
                    }
                }
                return true;
            }
        });

        btnTriDrag.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float dX =0, dY=0;
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_MOVE:
                    {
                        /*drawCircleLayout.animate()
                                .x((event.getRawX() + dX) - (drawCircleLayout.getWidth()/2))
                                .y((event.getRawY() + dY) - (drawCircleLayout.getHeight()))
                                .setDuration(0)
                                .start();*/
                        drawTriLayout.setX((event.getRawX() + dX) - (drawTriLayout.getWidth()/2));
                        drawTriLayout.setY((event.getRawY() + dY) - (drawTriLayout.getHeight()));
                        //Log.d("asha","move"+shape_x_coord+", "+shape_y_coord);
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    {
                        //Log.d("asha","up"+shape_x_coord+", "+shape_y_coord);
                        break;
                    }
                    case MotionEvent.ACTION_DOWN:
                    {
                        //	dragParam.height = 100;
                        //	dragParam.width = 100;
                        Log.d("asha","down");
                        dX = drawTriLayout.getX();
                        dY = drawTriLayout.getY();
                        break;
                    }
                }
                return true;
            }
        });

        onlyTriLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float dX =0, dY=0;
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_MOVE:
                    {
                        /*drawCircleLayout.animate()
                                .x((event.getRawX() + dX) - (drawCircleLayout.getWidth()/2))
                                .y((event.getRawY() + dY) - (drawCircleLayout.getHeight()))
                                .setDuration(0)
                                .start();*/
                        drawTriLayout.setX((event.getRawX() + dX) - (drawTriLayout.getWidth()/2));
                        drawTriLayout.setY((event.getRawY() + dY) - (drawTriLayout.getHeight()));
                        //Log.d("asha","move"+shape_x_coord+", "+shape_y_coord);
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    {
                        //Log.d("asha","up"+shape_x_coord+", "+shape_y_coord);
                        break;
                    }
                    case MotionEvent.ACTION_DOWN:
                    {
                        //	dragParam.height = 100;
                        //	dragParam.width = 100;
                        Log.d("asha","down");
                        dX = drawTriLayout.getX();
                        dY = drawTriLayout.getY();
                        break;
                    }
                }
                return true;
            }
        });

        btnTriColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPickerDialogBuilder
                        .with(mainActivityInstance)
                        .setTitle("Choose Rectangle Draw Color")
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
                            }
                        })
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                shape_color = selectedColor;
                                //Log.d("asha","select color="+shape_color);
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .build()
                        .show();
            }
        });

        btnTriFillUnfill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isShapeFill){
                    btnTriFillUnfill.setImageResource(R.mipmap.triangle_unfill);
                    isShapeFill = false;
                }
                else{
                    btnTriFillUnfill.setImageResource(R.mipmap.triangle_fill);
                    isShapeFill = true;
                }
            }
        });

        btnTriOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(shape_color==-111) {
                    shape_color = currentPaintColor;
                }
                shape_width = onlyTriLayout.getWidth();
                shape_height = onlyTriLayout.getHeight();
                int[] oldFolderCellPositionRect = new int[2];
                onlyTriLayout.getLocationInWindow(oldFolderCellPositionRect);
                shape_x_coord = oldFolderCellPositionRect[0];
                shape_y_coord = oldFolderCellPositionRect[1];
                ad_Height = fragLayout.getHeight();
                drawView.drawTriangleInCanvas(shape_x_coord + (shape_width/2), shape_y_coord - ad_Height, shape_width, shape_height, shape_color, isShapeFill);
                //X-Coordinate is added to get the first coordinate for the triangle
                //AD HEIGHT IS SUBTRACTED FORM Y-COORDINATE ON ABOVE LINE IS DONE TO BALANCE THE SCREEN AS THE SCREEN STARTS BELOW THE AD.
            }
        });

        btnDrawRect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(drawRectLayout.getVisibility() == View.GONE) {
                    hideExtraOptionsWithAnimation();
                    drawRectLayout.bringToFront();
                    drawRectLayout.setVisibility(View.VISIBLE);
                    drawView.setEraserMode(false, currentBGColor);
                    drawView.changeWidth(paintThickness);
                    btnEraser.setImageDrawable(getResources().getDrawable(R.mipmap.bgcolor));
                    drawView.allowDrawing(false); //preventing user to draw
                }
                else{
                    hideExtraOptionsWithAnimation();
                    drawRectLayout.setVisibility(View.GONE);
                    drawView.allowDrawing(true); //preventing user to draw
                }
                animateCloseBottomLayout();
                btnHideTopBottomMenu.animate().translationY(actionBarHeight).setDuration(500).start();
                btnHideTopBottomMenu.animate().rotationBy(180).setInterpolator(new LinearInterpolator()).setDuration(500).start();
                btnHideTopBottomMenu.setVisibility(View.GONE);
            }
        });

        btnRectClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(drawRectLayout.getVisibility() == View.VISIBLE) {
                    drawRectLayout.setVisibility(View.GONE);
                    shape_color = -111;
                    isShapeFill = false;
                    btnRectFillUnfill.setImageResource(R.mipmap.rect_unfill);
                    drawView.allowDrawing(true); //Allowing user to draw
                }
                animateOpenBottomLayout();
                btnHideTopBottomMenu.setVisibility(View.VISIBLE);
                btnHideTopBottomMenu.animate().translationY(0).setDuration(200).start();
                btnHideTopBottomMenu.animate().rotation(0).setInterpolator(new LinearInterpolator()).setDuration(200).start();
            }
        });

        btnRectStretch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                RelativeLayout.LayoutParams dragParam = (RelativeLayout.LayoutParams) onlyRectLayout.getLayoutParams();
                int startX=(int) v.getX();
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_MOVE:
                    {
                        Log.d("asha","move="+(startX)+"---"+ (int) event.getRawX());
                        int newWidth = onlyRectLayout.getWidth() + (((int) event.getRawX()) - startX);
                        if(newWidth>dpToPx(32) && newWidth<(device_Width-dpToPx(110))) {
                            dragParam.width = newWidth;
                            onlyRectLayout.setLayoutParams(dragParam);
                            shape_width = dragParam.width;
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    {
                        break;
                    }
                    case MotionEvent.ACTION_DOWN:
                    {
                        break;
                    }
                }
                return true;
            }
        });

        btnRectStretchVertical.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                RelativeLayout.LayoutParams dragParam = (RelativeLayout.LayoutParams) onlyRectLayout.getLayoutParams();

                int[] tempPos = new int[2];
                v.getLocationInWindow(tempPos);
                int startY=(int) tempPos[1];
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_MOVE:
                    {
                        Log.d("asha","move="+(startY)+"---"+ (int) event.getRawY());
                        int newHeight = onlyRectLayout.getHeight() + (((int) event.getRawY()) - startY);
                        if(newHeight>dpToPx(32)  && newHeight<(device_Height-dpToPx(110))) {
                            dragParam.height = newHeight;
                            onlyRectLayout.setLayoutParams(dragParam);
                            shape_height = dragParam.height;
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    {
                        break;
                    }
                    case MotionEvent.ACTION_DOWN:
                    {
                        break;
                    }
                }
                return true;
            }
        });

        btnRectDrag.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float dX =0, dY=0;
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_MOVE:
                    {
                        /*drawCircleLayout.animate()
                                .x((event.getRawX() + dX) - (drawCircleLayout.getWidth()/2))
                                .y((event.getRawY() + dY) - (drawCircleLayout.getHeight()))
                                .setDuration(0)
                                .start();*/
                        drawRectLayout.setX((event.getRawX() + dX) - (drawRectLayout.getWidth()/2));
                        drawRectLayout.setY((event.getRawY() + dY) - (drawRectLayout.getHeight()));
                        //Log.d("asha","move"+shape_x_coord+", "+shape_y_coord);
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    {
                        //Log.d("asha","up"+shape_x_coord+", "+shape_y_coord);
                        break;
                    }
                    case MotionEvent.ACTION_DOWN:
                    {
                        //	dragParam.height = 100;
                        //	dragParam.width = 100;
                        Log.d("asha","down");
                        dX = drawRectLayout.getX();
                        dY = drawRectLayout.getY();
                        break;
                    }
                }
                return true;
            }
        });

        onlyRectLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float dX =0, dY=0;
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_MOVE:
                    {
                        /*drawCircleLayout.animate()
                                .x((event.getRawX() + dX) - (drawCircleLayout.getWidth()/2))
                                .y((event.getRawY() + dY) - (drawCircleLayout.getHeight()))
                                .setDuration(0)
                                .start();*/
                        drawRectLayout.setX((event.getRawX() + dX) - (drawRectLayout.getWidth()/2));
                        drawRectLayout.setY((event.getRawY() + dY) - (drawRectLayout.getHeight()));
                        //Log.d("asha","move"+shape_x_coord+", "+shape_y_coord);
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    {
                        //Log.d("asha","up"+shape_x_coord+", "+shape_y_coord);
                        break;
                    }
                    case MotionEvent.ACTION_DOWN:
                    {
                        //	dragParam.height = 100;
                        //	dragParam.width = 100;
                        Log.d("asha","down");
                        dX = drawRectLayout.getX();
                        dY = drawRectLayout.getY();
                        break;
                    }
                }
                return true;
            }
        });

        btnRectColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPickerDialogBuilder
                        .with(mainActivityInstance)
                        .setTitle("Choose Rectangle Draw Color")
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
                            }
                        })
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                shape_color = selectedColor;
                                //Log.d("asha","select color="+shape_color);
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .build()
                        .show();
            }
        });

        btnRectFillUnfill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isShapeFill){
                    btnRectFillUnfill.setImageResource(R.mipmap.rect_unfill);
                    isShapeFill = false;
                }
                else{
                    btnRectFillUnfill.setImageResource(R.mipmap.rectangle);
                    isShapeFill = true;
                }
            }
        });

        btnRectOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(shape_color==-111) {
                    shape_color = currentPaintColor;
                }
                shape_width = onlyRectLayout.getWidth();
                shape_height = onlyRectLayout.getHeight();
                int[] oldFolderCellPositionRect = new int[2];
                onlyRectLayout.getLocationInWindow(oldFolderCellPositionRect);
                shape_x_coord = oldFolderCellPositionRect[0];
                shape_y_coord = oldFolderCellPositionRect[1];
                ad_Height = fragLayout.getHeight();
                drawView.drawrectangleInCanvas(shape_x_coord, shape_y_coord - ad_Height, shape_width, shape_height, shape_color, isShapeFill);
                //AD HEIGHT IS SUBTRACTED FORM Y-COORDINATE ON ABOVE LINE IS DONE TO BALANCE THE SCREEN AS THE SCREEN STARTS BELOW THE AD.
            }
        });
    }

    public static int dpToPx(int dp)
    {
        return (int) (dp * getSystem().getDisplayMetrics().density);
    }

    private void openInAppGallery(){
        Intent MyDrawingIntent = new Intent(MainActivity.this, MyGalleryActivity.class);
        startActivity(MyDrawingIntent);
    }

    private void checkUndoIcon(){ //CHECK UNDO ACTIVATED OR NOT
        if(drawView.getUndoPointArraySize()==0){
            btnUndo.setAlpha(0.4f);
        }
        else{
            btnUndo.setAlpha(1.0f);
        }
    }

    private void checkRedoIcon(){   //CHECK REDO ACTIVATED OR NOT
        if(drawView.getRedoPointArraySize()==0){
            btnRedo.setAlpha(0.4f);
        }
        else{
            btnRedo.setAlpha(1.0f);
        }
    }

    @Override
    protected void onResume() {
        txt_Watermark.setVisibility(View.INVISIBLE);
        //SHOW AND HIDE THE AD FRAGMENT
        if(!isInternetOn()){
            fragLayout.setVisibility(View.GONE);
        }
        else{
            fragLayout.setVisibility(View.VISIBLE);
        }
        super.onResume();
    }

    private List<MenuObject> getMenuObjectsForBGColor() {
        // You can use any [resource, bitmap, drawable, color] as image:
        // item.setResource(...)
        // item.setBitmap(...)
        // item.setDrawable(...)
        // item.setColor(...)
        // You can set image ScaleType:
        // item.setScaleType(ScaleType.FIT_XY)
        // You can use any [resource, drawable, color] as background:
        // item.setBgResource(...)
        // item.setBgDrawable(...)
        // item.setBgColor(...)
        // You can use any [color] as text color:
        // item.setTextColor(...)
        // You can set any [color] as divider color:
        // item.setDividerColor(...)

        List<MenuObject> menuObjects = new ArrayList<>();

        MenuObject close = new MenuObject("SELECT DRAWING BACKGROUND          ");
        close.setBgColor(Color.RED);
        close.setResource(R.mipmap.close);

        MenuObject white = new MenuObject("WHITE");
        white.setBgColor(Color.WHITE);
        white.setResource(R.mipmap.bgmenucolor);

        MenuObject blue = new MenuObject("BLUE");
        blue.setBgColor(Color.BLUE);
        blue.setResource(R.mipmap.bgmenucolor);

        MenuObject cyan = new MenuObject("CYAN");
        cyan.setBgColor(Color.CYAN);
        cyan.setResource(R.mipmap.bgmenucolor);

        MenuObject green = new MenuObject("GREEN");
        green.setBgColor(Color.GREEN);
        green.setResource(R.mipmap.bgmenucolor);

        MenuObject magenta = new MenuObject("MAGENTA");
        magenta.setBgColor(Color.MAGENTA);
        magenta.setResource(R.mipmap.bgmenucolor);

        MenuObject red = new MenuObject("RED");
        red.setBgColor(Color.RED);
        red.setResource(R.mipmap.bgmenucolor);

        MenuObject yellow = new MenuObject("YELLOW");
        yellow.setBgColor(Color.YELLOW);
        yellow.setResource(R.mipmap.bgmenucolor);

        MenuObject black = new MenuObject("BLACK");
        black.setBgColor(Color.BLACK);
        black.setResource(R.mipmap.bgmenucolor);

        MenuObject customColor = new MenuObject("CHOOSE BACKGROUND COLOR");
        customColor.setBgColor(Color.WHITE);
        customColor.setResource(R.mipmap.customcolorpicker);

        MenuObject customPic = new MenuObject("CUSTOM PICTURE");
        customPic.setBgColor(Color.WHITE);
        customPic.setResource(R.mipmap.bgmenuimage);

        menuObjects.add(close);
        menuObjects.add(white);
        menuObjects.add(blue);
        menuObjects.add(cyan);
        menuObjects.add(green);
        menuObjects.add(magenta);
        menuObjects.add(red);
        menuObjects.add(yellow);
        menuObjects.add(black);
        menuObjects.add(customColor);
        menuObjects.add(customPic);
        return menuObjects;
    }

    private List<MenuObject> getMenuObjectsForPaintWidth() {

        List<MenuObject> menuObjects = new ArrayList<>();

        MenuObject close = new MenuObject();
        close.setBgColor(Color.RED);
        close.setResource(R.mipmap.close);
        close.setDividerColor(R.color.colorPrimaryDark);

        MenuObject xs = new MenuObject("EXTRA SMALL");
        xs.setBgColor(getResources().getColor(R.color.menuBg));
        xs.setResource(R.mipmap.xs);
        xs.setDividerColor(R.color.black);

        MenuObject small = new MenuObject("SMALL");
        small.setBgColor(getResources().getColor(R.color.menuBg));
        small.setResource(R.mipmap.small);
        small.setDividerColor(R.color.black);

        MenuObject med = new MenuObject("MEDIUM");
        med.setBgColor(getResources().getColor(R.color.menuBg));
        med.setResource(R.mipmap.med);
        med.setDividerColor(R.color.black);

        MenuObject large = new MenuObject("LARGE");
        large.setBgColor(getResources().getColor(R.color.menuBg));
        large.setResource(R.mipmap.large);
        large.setDividerColor(R.color.black);

        MenuObject xl = new MenuObject("EXTRA LARGE");
        xl.setBgColor(getResources().getColor(R.color.menuBg));
        xl.setResource(R.mipmap.xl);
        xl.setDividerColor(R.color.black);

        MenuObject customThck = new MenuObject("CUSTOM WIDTH");
        customThck.setBgColor(getResources().getColor(R.color.menuBg));
        customThck.setResource(R.mipmap.linewidth);
        customThck.setDividerColor(R.color.black);

        menuObjects.add(close);
        menuObjects.add(xs);
        menuObjects.add(small);
        menuObjects.add(med);
        menuObjects.add(large);
        menuObjects.add(xl);
        menuObjects.add(customThck);
        return menuObjects;
    }

    private List<MenuObject> getMenuObjectsForPaintColor() {

        List<MenuObject> menuObjects = new ArrayList<>();

        MenuObject close = new MenuObject();
        close.setBgColor(Color.RED);
        close.setResource(R.mipmap.close);

        MenuObject white = new MenuObject("WHITE");
        white.setBgColor(Color.WHITE);
        white.setResource(R.mipmap.drawcolormenu);

        MenuObject blue = new MenuObject("BLUE");
        blue.setBgColor(Color.BLUE);
        blue.setResource(R.mipmap.drawcolormenu);

        MenuObject cyan = new MenuObject("LIGHT BLUE");
        cyan.setBgColor(Color.CYAN);
        cyan.setResource(R.mipmap.drawcolormenu);

        MenuObject green = new MenuObject("GREEN");
        green.setBgColor(Color.GREEN);
        green.setResource(R.mipmap.drawcolormenu);

        MenuObject magenta = new MenuObject("PINK");
        magenta.setBgColor(Color.MAGENTA);
        magenta.setResource(R.mipmap.drawcolormenu);

        MenuObject red = new MenuObject("RED");
        red.setBgColor(Color.RED);
        red.setResource(R.mipmap.drawcolormenu);

        MenuObject yellow = new MenuObject("YELLOW");
        yellow.setBgColor(Color.YELLOW);
        yellow.setResource(R.mipmap.drawcolormenu);

        MenuObject black = new MenuObject("BLACK");
        black.setBgColor(Color.BLACK);
        black.setResource(R.mipmap.drawcolormenu);

        MenuObject customPic = new MenuObject("CHOOSE PAINT COLOR");
        customPic.setBgColor(Color.WHITE);
        customPic.setResource(R.mipmap.customcolorpicker);

        menuObjects.add(close);
        menuObjects.add(white);
        menuObjects.add(blue);
        menuObjects.add(cyan);
        menuObjects.add(green);
        menuObjects.add(magenta);
        menuObjects.add(red);
        menuObjects.add(yellow);
        menuObjects.add(black);
        menuObjects.add(customPic);
        return menuObjects;
    }

    void setCustomBackground(DrawView v) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(Intent.createChooser(galleryIntent, "Choose Background Picture"),151);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if statement prevents force close error when picture isn't selected

        //DELETING IMAGE IN CASE OF SHARING
        if(requestCode == SHARE_CODE){
            if(lastSharedFile.exists()){
                lastSharedFile.delete();
            }
        }
        else {
            try {
                // When an Image is picked
                if (requestCode == 151 && resultCode == RESULT_OK
                        && null != data) {
                    // Get the Image from data

                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.MediaColumns.DATA};

                    // Get the cursor
                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imgpath = cursor.getString(columnIndex);
                    Log.d("path", imgpath);
                    cursor.close();


                    Bitmap myBitmap = BitmapFactory.decodeFile(imgpath);
                    Drawable d = new BitmapDrawable(getResources(), myBitmap);
                    drawView.setBackground(d);
                    startDrawingToast.show();

                    currentBGColor = -111; //FOR BACKGROUND PICTURES
                    btnEraser.setAlpha(0.4f);//Setting deactivated eraser image
                } else {
                    Toast.makeText(this, "You haven't picked Image",
                            Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(drawStarLayout.getVisibility() == View.VISIBLE) {
            drawStarLayout.setVisibility(View.GONE);
            shape_color = -111;
            isShapeFill = false;
            btnStarFillUnfill.setImageResource(R.mipmap.circle_unfill);
            drawView.allowDrawing(true); //Allowing user to draw
            animateOpenBottomLayout();
        }
        else if(drawTriLayout.getVisibility() == View.VISIBLE){
            drawTriLayout.setVisibility(View.GONE);
            shape_color = -111;
            isShapeFill = false;
            btnTriFillUnfill.setImageResource(R.mipmap.rect_unfill);
            drawView.allowDrawing(true); //Allowing user to draw
            animateOpenBottomLayout();
        }
        else if(drawCircleLayout.getVisibility() == View.VISIBLE) {
            drawCircleLayout.setVisibility(View.GONE);
            shape_color = -111;
            isShapeFill = false;
            btnCircleFillUnfill.setImageResource(R.mipmap.circle_unfill);
            drawView.allowDrawing(true); //Allowing user to draw
            animateOpenBottomLayout();
        }
        else if(drawRectLayout.getVisibility() == View.VISIBLE){
            drawRectLayout.setVisibility(View.GONE);
            shape_color = -111;
            isShapeFill = false;
            btnRectFillUnfill.setImageResource(R.mipmap.rect_unfill);
            drawView.allowDrawing(true); //Allowing user to draw
            animateOpenBottomLayout();
        }
        else if(extraOptionsLayout.getAlpha()!=0f) {
            hideExtraOptionsWithAnimation();
        }
        else {
            if (secondTimeBackClicked) {
                super.onBackPressed();
            }
            if (!secondTimeBackClicked) {
                secondTimeBackClicked = true;
                Toast.makeText(this, "Tap again to exit", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void askPStorageStatePermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(mainActivityInstance, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(mainActivityInstance, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //Show Information about why you need the permission
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mainActivityInstance);
                    builder.setTitle("Draw & Fun");
                    builder.setMessage("Need permissions for Saving or Sharing.");
                    builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            ActivityCompat.requestPermissions(mainActivityInstance, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else if (permissionStatus.getBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE, false)) {
                    //Previously Permission Request was cancelled with 'Dont Ask Again',
                    // Redirect to Settings after showing Information about why you need the permission
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mainActivityInstance);
                    builder.setTitle("Draw & Fun");
                    builder.setMessage("Need permissions for Saving or Sharing.");
                    builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                            Toast.makeText(MainActivity.this, "Go to Permissions to Grant Location", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else {
                    //just request the permission
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE);
                }

                SharedPreferences.Editor editor = permissionStatus.edit();
                editor.putBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE, true);
                editor.commit();


            } else {
                if(afterPermissionIndex==1 || afterPermissionIndex == 2) {
                    shareScreenshot();
                }
                else if(afterPermissionIndex == 3){ //For opening in app gallery
                    openInAppGallery();
                }
                //You already have the permission, just go ahead.
                //getCountryIsoAndSetCode();
            }
        }
        else{
            //Not needed for asking permissions below MarshMallow
            if(afterPermissionIndex==1 || afterPermissionIndex == 2) {
                shareScreenshot();
            }
            else if(afterPermissionIndex == 3){ //For opening in app gallery
                openInAppGallery();
            }
        }
    }

    public void shareScreenshot(){
        File mainDir = new File(Environment.getExternalStorageDirectory(), "Draw & Fun");

        int fileCount = 0;
        if(mainDir.isDirectory()){
            fileCount = mainDir.listFiles().length;
        }
        else {
            mainDir.mkdirs();
        }

        File image = new File(mainDir, fileCount+"_drawandfun_image_"+System.currentTimeMillis()+".jpg");

        Bitmap bitmap = (Bitmap)ScreenshotUtils.getScreenShot(drawingRelativeLayout);
        try {
            FileOutputStream out = new FileOutputStream(image);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

       // Uri imageURIBelow24 =  Uri.fromFile(image); //FOR BELOW ANDROID 24
        Uri imageURIAbove24 = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".GenericFileProvider", image);;

        //Toast.makeText(this, ""+ScreenshotUtils.getScreenShot(mContent), Toast.LENGTH_SHORT).show();
        //ScreenshotUtils.store(ScreenshotUtils.getScreenShot(mContent),"WhatsTrace_"+ Calendar.getInstance().getTime(), ScreenshotUtils.getMainDirectoryName(this));
        if(afterPermissionIndex==2) { //FOR SHARING
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("image/jpeg");
            String shareBody = "How is my drawing ? ;-)\nTry Draw & Fun App and have fun :- https://play.google.com/store/apps/details?id=com.asisdroid.drawfun&hl=en.";
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            sharingIntent.putExtra(Intent.EXTRA_STREAM, imageURIAbove24);
            lastSharedFile = image;
            startActivityForResult(Intent.createChooser(sharingIntent, "Share via"), SHARE_CODE);
        }
        else if(afterPermissionIndex==1){ //FOR JUST SAVING
            Toast.makeText(this, "Saved! Go to My Drawings to view.", Toast.LENGTH_SHORT).show();
            /*Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(imageURIAbove24, "image*//*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);*/
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==WRITE_EXTERNAL_STORAGE) {  //CALLING FRAGMENT ONREUESTPERMISSIONRESULT ACCORDING TO REQUEST CODE
            if(grantResults[0] == 0){
                //PERMISSION GRANTED
                // Toast.makeText(mainActivityInstance, "granted", Toast.LENGTH_SHORT).show();
                if(afterPermissionIndex==1 || afterPermissionIndex==2) {
                    shareScreenshot();
                }
                else if(afterPermissionIndex == 3){ //For opening in app gallery
                    openInAppGallery();
                }
            }
            else{
                //PERMISSION DENIED
                Toast.makeText(mainActivityInstance, "Permissions required to proceed!", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void changeCustomPaintWidthDialog() {
        final AlertDialog.Builder d = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.number_picker_dialog, null);
        d.setView(dialogView);
        final SeekBar seekBar = (SeekBar) dialogView.findViewById(R.id.seekbarForThickness);
        final TextView txtShowProgress = (TextView) dialogView.findViewById(R.id.txtProgress);
        final TextView txtShowProgressValuew = (TextView) dialogView.findViewById(R.id.txtProgressShow);
        final RelativeLayout txtShowProgressLayout = (RelativeLayout) dialogView.findViewById(R.id.layoutDemo);
        seekBar.setProgress(paintThickness);
        txtShowProgressLayout.setBackgroundColor(currentBGColor);
        txtShowProgress.setBackgroundColor(currentPaintColor);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) txtShowProgress.getLayoutParams();
        params.height = paintThickness;
        txtShowProgress.setLayoutParams(params);
        txtShowProgressValuew.setText("Current thickness - "+paintThickness);
        //txtShowProgress.setText(""+paintThickness);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressVal;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
               progressVal = progress;
                txtShowProgressValuew.setText("Current thickness - "+progressVal);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) txtShowProgress.getLayoutParams();
                params.height = progressVal;
                txtShowProgress.setLayoutParams(params);
            }
        });
        d.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                paintThickness = seekBar.getProgress();
                drawView.changeWidth(paintThickness);
                //Log.d(TAG, "onClick: " + seekBar.getProgress());
            }
        });
        d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        AlertDialog alertDialog = d.create();
        alertDialog.show();
    }

    public void showConfirmDialogForClear(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage("Your current drawing will be completely erased! Are your sure?");
        dialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                drawView.setBackgroundColor(Color.BLACK);
                drawView.clearPoints();
                drawView.allowDrawing(true);
                drawCircleLayout.setVisibility(View.GONE);
                mMenuDialogFragment.show(fragmentManager, ContextMenuDialogFragment.TAG);
                checkUndoIcon();
                checkRedoIcon();
                drawView.setEraserMode(false, currentBGColor);
                drawView.changeWidth(paintThickness);
                mySavePreferences.setBrushType(NORMAL_BRUSH);
                setBrushesActive();
                btnEraser.setImageDrawable(getResources().getDrawable(R.mipmap.bgcolor));
            }
        });
        dialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public void changeCustomPaintColor(){
        ColorPickerDialogBuilder
                .with(mainActivityInstance)
                .setTitle("Choose paint color")
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
                    }
                })
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        if(selectedColor!=currentBGColor) {
                            currentPaintColor = selectedColor;
                            drawView.changeColour(99, selectedColor);
                            mySavePreferences.setPaintColor(currentPaintColor);
                        }
                        else{
                            Toast.makeText(MainActivity.this, VALIDATION_MESSAGE, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }

    public void changeCustomBgColor(){
        ColorPickerDialogBuilder
                .with(mainActivityInstance)
                .setTitle("Choose background color")
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
                    }
                })
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                            currentBGColor = selectedColor;
                            Log.d("asisi","Colro"+selectedColor);
                            mySavePreferences.setBgColor(currentBGColor);
                            btnEraser.setImageDrawable(getResources().getDrawable(R.mipmap.bgcolor));//Setting activated eraser image
                            drawView.setBackgroundColor(selectedColor);
                            startDrawingToast.show();
                            if(currentBGColor == Color.BLACK){
                                if(currentPaintColor == Color.BLACK) {
                                    currentPaintColor = Color.WHITE;
                                    drawView.changeColour(0); //CHANGING TO WHITE
                                }
                            }
                            else{
                                if(currentPaintColor == Color.WHITE) {
                                    currentPaintColor = Color.BLACK;
                                    drawView.changeColour(7); //CHANGING TO BLACK
                                }
                            }
                            mySavePreferences.setPaintColor(currentPaintColor);
                        if(selectedColor==currentPaintColor){
                            Toast.makeText(MainActivity.this, VALIDATION_MESSAGE, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }

    public  void refreshUIOnDraw(){
        checkRedoIcon();
        checkUndoIcon();
    }

    public void animateCloseBottomLayout(){
        //Log.d("asisi","clsoe bottom");
            bottomLayout.animate().translationY(400f).setDuration(300);
            btnShare.animate().translationY(400f).setDuration(300);
            extraOptionsLayout.animate().alpha(0f).setDuration(10);
            btnDummyOption.animate().alpha(0f).setDuration(10);
            btnShareDummy.animate().alpha(0f).setDuration(100);
            txt_Watermark.setVisibility(View.GONE);

            if(extraOptionsLayout.getVisibility() == View.VISIBLE) {
                extraOptionsLayout.setVisibility(View.GONE);
                btnDummyOption.setVisibility(View.GONE);
            }
    }

    public void animateExtraOptionCLose(){
        if(extraOptionsLayout.getVisibility() == View.VISIBLE) {
            hideExtraOptionsWithAnimation();
        }
    }

    public void animateCloseTopLayout(){
        //Log.d("asisi","clsoe top");
            fragLayout.animate().translationY(-400f).setDuration(300);
    }

    public void animateOpenBottomLayout(){
        //Log.d("asisi","open bototm");
            bottomLayout.animate().translationY(0f).setDuration(200);
            btnShare.animate().translationY(0f).setDuration(200);
    }

    public void animateOpenTopLayout(){
        //Log.d("asisi","open top");
        fragLayout.animate().translationY(0f).setDuration(200);
    }

    private void updateApp(){
        GetVersionCode tempObj = new GetVersionCode();
        tempObj.execute();
    }

    private class GetVersionCode extends AsyncTask<Void, String, String> {
        @Override
        protected String doInBackground(Void... voids) {
            String newVersion = null;
            try {
                currentVer = DrawFunApplication.getInstance().getPackageManager().getPackageInfo(DrawFunApplication.getInstance().getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            try {
                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + DrawFunApplication.getInstance().getPackageName() + "&hl=it")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div[itemprop=softwareVersion]")
                        .first()
                        .ownText();
                return newVersion;
            } catch (Exception e) {
                return newVersion;
            }
        }

        @Override
        protected void onPostExecute(String onlineVersion) {
            super.onPostExecute(onlineVersion);
            Log.d("asisi update", "Current version " + currentVer + "playstore version " + onlineVersion);
            if (onlineVersion != null && !onlineVersion.isEmpty()) {
                if (Float.valueOf(currentVer) < Float.valueOf(onlineVersion)) {
                    //show dialog
                    showForceUpdateDialog(onlineVersion);
                }
            }
        }
    }

    public void showForceUpdateDialog(String latestVersion){
        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(new ContextThemeWrapper(mainActivityInstance,
                R.style.AlertDialogCustomDark));

        alertDialogBuilder.setTitle(mainActivityInstance.getString(R.string.youAreNotUpdatedTitle));
        alertDialogBuilder.setMessage(mainActivityInstance.getString(R.string.youAreNotUpdatedMessage) + " " + latestVersion +" "+ mainActivityInstance.getString(R.string.youAreNotUpdatedMessage1)+".");
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mainActivityInstance.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + mainActivityInstance.getPackageName())));
                dialog.cancel();
            }
        });
        alertDialogBuilder.show();
    }

    public static boolean isInternetOn(){

        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager) DrawFunApplication.getInstance().getSystemService(DrawFunApplication.getInstance().getBaseContext().CONNECTIVITY_SERVICE);

        if(connec.getNetworkInfo(0)!=null && connec.getNetworkInfo(1)!=null) {

            // Check for network connections
            if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED ||
                    connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTING ||
                    connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING ||
                    connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) {


                return true;

            } else if (
                    connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED ||
                            connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED) {


                return false;
            }
        }
        return false;
    }

}
