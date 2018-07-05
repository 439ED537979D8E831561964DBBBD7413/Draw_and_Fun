package com.asisdroid.drawfun;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Image_Slider_Activity extends AppCompatActivity {
    private static ViewPager mPager;
    private static int currentPage = 0;
    private static String currentImageName;
    private static int NUM_PAGES = 0;
    private static Integer[] IMAGES;
    File[] listFile;
    private ArrayList<File> ImagesArray = new ArrayList<File>();
    ArrayList<String> fileNames = new ArrayList<String>();
    ArrayList<String> favDrawingNames = new ArrayList<String>();
    ActionBar myActionBar;
    private ImageButton back;
    private TextView txt_drawingIndex, txtDrawingDate;
    private FloatingActionButton btnFav;
    GalleryDBAdapter myGalleryDB;
    private CardView btnDelete, btnShare;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image__slider_);
        getSupportActionBar().hide();

        currentPage = getIntent().getExtras().getInt("position");
        currentImageName = getIntent().getExtras().getString("filename");
        favDrawingNames = getIntent().getStringArrayListExtra("favArray");

        back = (ImageButton) findViewById(R.id.backarrow);
        txt_drawingIndex = (TextView) findViewById(R.id.txtIndex);
        txtDrawingDate = (TextView) findViewById(R.id.txtTime);

        btnFav = (FloatingActionButton) findViewById(R.id.fav_btn);
        btnDelete = (CardView) findViewById(R.id.deleteCardView);
        btnShare = (CardView) findViewById(R.id.shareCardView);

        myGalleryDB = new GalleryDBAdapter(this);
        myGalleryDB.open();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSupportNavigateUp();
            }
        });

        init();
    }

    private void init(){

        File file= new File(Environment.getExternalStorageDirectory(), "Draw & Fun");

        if (file.isDirectory())
        {
            listFile = file.listFiles();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Arrays.sort(listFile, new Comparator<File>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    public int compare(File f1, File f2) {
                        return Long.compare(f2.lastModified(), f1.lastModified());
                    }
                });
            }
            else{
                Arrays.sort(listFile, Collections.<File>reverseOrder()); //SORTING FILE IN DESCENDING ORDER NAME
            }
        }

        fileNames.clear();
        for(int x=0;x<listFile.length;x++){
            fileNames.add(listFile[x].getName());
            ImagesArray.add(listFile[x]);
        }

        if(currentPage==-1) //FROM FAVOURITE PAGE
        {
            currentPage = fileNames.indexOf(currentImageName);
        }

        mPager = (ViewPager) findViewById(R.id.pager);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mPager.setTransitionName("animate");
        }
        mPager.setAdapter(new SlidingImage_Adapter(Image_Slider_Activity.this,ImagesArray));

        mPager.setCurrentItem(currentPage);

        NUM_PAGES =listFile.length;
        final String txtPlural ;
        if(NUM_PAGES>1){
            txtPlural = "drawings";
        }
        else{
            txtPlural = "drawing";
        }

        setTheSliderImage(currentPage, txtPlural);

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                currentPage = position;
                setTheSliderImage(currentPage, txtPlural);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(favDrawingNames.contains(fileNames.get(currentPage))){ //ALREADY FAV
                    myGalleryDB.removeFromFav(fileNames.get(currentPage));
                    favDrawingNames.remove(fileNames.get(currentPage));
                    btnFav.setImageResource(R.mipmap.fav_off);
                }
                else{ //NOT FAV
                    myGalleryDB.insertIntoFav(fileNames.get(currentPage));
                    favDrawingNames.add(fileNames.get(currentPage));
                    btnFav.setImageResource(R.mipmap.fav_on);
                }
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri imageURIAbove24 = FileProvider.getUriForFile(Image_Slider_Activity.this, BuildConfig.APPLICATION_ID + ".GenericFileProvider", ImagesArray.get(currentPage));;
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("image/jpeg");
                String shareBody = "How is my drawing ? ;-)\nTry Draw & Fun App and have fun :- https://play.google.com/store/apps/details?id=com.asisdroid.drawfun&hl=en.";
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                sharingIntent.putExtra(Intent.EXTRA_STREAM, imageURIAbove24);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmDialogForDelete();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void setTheSliderImage(int img_position, String txt_plural){
        Date lastModDate = new Date(listFile[img_position].lastModified());
        DateFormat formatter = new SimpleDateFormat("EEE, MMM d, ''yy");
        DateFormat formatter1 = new SimpleDateFormat("h:mm a");
        String picTime = formatter1.format(lastModDate);
        String picDate = formatter.format(lastModDate);
        txt_drawingIndex.setText((img_position+1)+"/"+NUM_PAGES+" "+txt_plural);
        txtDrawingDate.setText(picDate+", "+picTime);
        if(favDrawingNames.contains(fileNames.get(img_position))){
            btnFav.setImageDrawable(getResources().getDrawable(R.mipmap.fav_on));
        }
        else{
            btnFav.setImageDrawable(getResources().getDrawable(R.mipmap.fav_off));
        }
    }

    public void showConfirmDialogForDelete(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage("Your current drawing will be deleted permanently! Are your sure?");
        dialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if(favDrawingNames.contains(fileNames.get(currentPage))) {
                    myGalleryDB.removeFromFav(fileNames.get(currentPage));
                    favDrawingNames.remove(fileNames.get(currentPage));
                }
                if((ImagesArray.get(currentPage)).delete()){
                    ImagesArray.remove(currentPage);
                    NUM_PAGES = ImagesArray.size();
                    mPager.setAdapter(new SlidingImage_Adapter(Image_Slider_Activity.this,ImagesArray));
                    if(NUM_PAGES!=0) {
                        if (currentPage == NUM_PAGES) {
                          currentPage-=1;
                        }
                        mPager.setCurrentItem(currentPage);
                    }
                    else{
                        onSupportNavigateUp(); //NO IMAGES REMAINING
                    }
                    Toast.makeText(Image_Slider_Activity.this, "Deleted!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(Image_Slider_Activity.this, "Some problem in deleting!", Toast.LENGTH_SHORT).show();
                }

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
}
