//code refferenced and inspired by https://www.journaldev.com/10096/android-viewpager-example-tutorial
/*
Kyle Lester
Jason Casebier
CS 4020
Assignment Final
*/
package com.kyle_jason.imagefinal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;

public class MyPagerAdapter extends PagerAdapter {

    private Context mContext;
    LayoutInflater mLayoutInflater;
    File[] allImages = new File[0];
    int length;

    public MyPagerAdapter(Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {

        File slideFile = new File(Environment.getExternalStorageDirectory() + "/SlideFile");

        if(slideFile.exists()) {
            File dir = new File("/sdcard/SlideFile/");
            allImages = dir.listFiles();
        }

        View itemView = mLayoutInflater.inflate(R.layout.view_image1, collection, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);

        Log.i("Kyle", "position " + position);

            if(position < allImages.length) {
                Log.i("Kyle", "allImages " + allImages[position]);
                Bitmap myBitmap = BitmapFactory.decodeFile(allImages[position].getAbsolutePath());
                imageView.setImageBitmap(myBitmap);
            }

        collection.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        Log.i("Kyle", "allImages length " + allImages.length);
        return 15; //couldnt get allImages.length to work
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}