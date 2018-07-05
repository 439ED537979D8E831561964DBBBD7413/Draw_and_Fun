package com.asisdroid.drawfun;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;


public class OneFragment extends Fragment {

    public OneFragment() {
        // Required empty public constructor
    }

    public static OneFragment getInstance() {
        return new OneFragment();
    }

    private ImageAdapter imageAdapter;
    File[] listFile;
    ArrayList<String> fileNames = new ArrayList<String>();
    ArrayList<String> favDrawingNames = new ArrayList<String>();
    boolean[] favOrNotList;
    GridView imagegrid;
    int width, imgSize;
    GalleryDBAdapter myGalleryDB;
    boolean isOnlyFav = false;
    TextView noRecordMsg;

    private View fragView;
    private Activity activity;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragView = inflater.inflate(R.layout.fragment_one, container, false);

        // Inflate the layout for this fragment
        return fragView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        imgSize = Math.round(width/3);

        noRecordMsg = (TextView) fragView.findViewById(R.id.no_images_all);

        myGalleryDB = new GalleryDBAdapter(getContext());
        myGalleryDB.open();

        imagegrid = (GridView) fragView.findViewById(R.id.PhoneImageGrid_all);
        getFromSdcard();
        imageAdapter = new ImageAdapter();
        imagegrid.setAdapter(imageAdapter);
    }

    public void getFromSdcard()
    {
        File file= new File(Environment.getExternalStorageDirectory(), "Draw & Fun");

        if (file.isDirectory())
        {
            listFile = null;
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

            fileNames.clear();
            for(int x=0;x<listFile.length;x++){
                fileNames.add(listFile[x].getName());
               Log.d("asisi start", listFile[x].getName());
            }

            favDrawingNames.clear();
            for(int c=0; c<listFile.length;c++) { //GETTING THE FAV OR NOT DETAILS TO LOCAL ARRAY
                if(myGalleryDB.isMyFavourite(fileNames.get(c))){
                    favDrawingNames.add(fileNames.get(c));
                }
            }

            /*if(isOnlyFav){
                ArrayList<File> tempListFile = new ArrayList<File>();
                ArrayList<String> tempFileNames = new ArrayList<String>();
                for(int c=0; c<listFile.length;c++) { //STORING ONLY FAVOURITE IMAGES
                    if(favDrawingNames.contains(fileNames.get(c))){
                        tempFileNames.add(fileNames.get(c));
                        tempListFile.add(listFile[c]);
                    }
                }
                listFile = tempListFile.toArray(new File[tempListFile.size()]);
                fileNames = tempFileNames;
            }*/
        }

        if(listFile==null || listFile.length==0){ //NO DRAWINGS FOUND
            noRecordMsg.setVisibility(View.VISIBLE);
            imagegrid.setVisibility(View.GONE);
        }
        else{
            noRecordMsg.setVisibility(View.GONE);
            imagegrid.setVisibility(View.VISIBLE);
        }
    }

    public class ImageAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public ImageAdapter() {
            mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            if(listFile!=null) {
                return listFile.length;
            }
            else{
                return 0;
            }
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(
                        R.layout.galleryitem_layout, null);
                holder.imageview = (ImageView) convertView.findViewById(R.id.thumbImage);
                holder.fav_Imageview = (ImageView) convertView.findViewById(R.id.myfav);

                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            Glide.with(getContext()).load(listFile[position]).into(holder.imageview);


            holder.imageview.setTag(R.id.thumbImage,position);
            holder.fav_Imageview.setTag(R.id.myfav,position);

            holder.imageview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int clkdposition = 0;

                    try {
                        clkdposition = (Integer) view.getTag(R.id.thumbImage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Intent imageSliderIntent = new Intent(getActivity(), Image_Slider_Activity.class);
                    imageSliderIntent.putExtra("position", clkdposition);
                    imageSliderIntent.putExtra("filename", fileNames.get(clkdposition));
                    imageSliderIntent.putStringArrayListExtra("favArray", favDrawingNames);

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation(activity, view, "animate");
                        startActivity(imageSliderIntent, options.toBundle());
                    }
                    else {
                        startActivity(imageSliderIntent);
                    }
                }
            });

            holder.fav_Imageview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int clkdposition = 0;
                    try {
                        clkdposition = (Integer) view.getTag(R.id.myfav);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if(favDrawingNames.contains(fileNames.get(position))){ //ALREADY FAV
                        myGalleryDB.removeFromFav(fileNames.get(clkdposition));
                        favDrawingNames.remove(fileNames.get(clkdposition));
                        holder.fav_Imageview.setImageResource(R.mipmap.fav_off);
                    }
                    else{ //NOT FAV
                        myGalleryDB.insertIntoFav(fileNames.get(clkdposition));
                        favDrawingNames.add(fileNames.get(clkdposition));
                        holder.fav_Imageview.setImageResource(R.mipmap.fav_on);
                    }

                    refershGalleryAll();
                    //Log.d("Asisi sdfdsfsdfds", clkdposition+"--"+fileNames.get(clkdposition)+"---"+isfavDrawingNames.get(clkdposition));
                }
            });

            //holder.imageview.setImageBitmap(myBitmap);
            holder.imageview.setLayoutParams(new RelativeLayout.LayoutParams(imgSize,imgSize));
            int favImgSize = (Integer) Math.round(imgSize/4);
            RelativeLayout.LayoutParams favParams = new RelativeLayout.LayoutParams(favImgSize, favImgSize);
            favParams.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.thumbImage);
            favParams.addRule(RelativeLayout.ALIGN_RIGHT, R.id.thumbImage);
            holder.fav_Imageview.setLayoutParams(favParams);

            //SETTING ALL FAVOURITES
            Log.d("Asisi", position+"--"+fileNames.get(position)+"---"+favDrawingNames.contains(fileNames.get(position)));
            if(favDrawingNames.contains(fileNames.get(position))){ //ALREADY FAV
                holder.fav_Imageview.setImageResource(R.mipmap.fav_on);
            }
            else{ //NOT FAV
                holder.fav_Imageview.setImageResource(R.mipmap.fav_off);
            }

            return convertView;
        }
    }
    class ViewHolder {
        ImageView imageview, fav_Imageview;
    }

    //TO SHOW OR UNSHOW ONLY FAVOURITES
    private void refershGalleryAll(){
        getFromSdcard();
        imageAdapter.notifyDataSetChanged();
    }

    //CALLS WHEN FRAGMENT GETS VISISBLE
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        // Make sure that we are currently visible
        if (this.isVisible()) {
            // If we are becoming invisible, then...
            if (isVisibleToUser) {
                refershGalleryAll();
                // TODO stop audio playback
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refershGalleryAll();
    }
}
