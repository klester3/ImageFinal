package com.kyle_jason.imagefinal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;

public class CustomPagerAdapter extends PagerAdapter {

    private Context mContext;
    LayoutInflater mLayoutInflater;

    public CustomPagerAdapter(Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        File dir = new File("/sdcard/SlideFile/");
        File[] allImages = dir.listFiles();

        View itemView = mLayoutInflater.inflate(R.layout.view_image1, collection, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);

        Log.i("Kyle", "allImages length " + allImages.length);

        for(int i = 0; i < allImages.length; i++){
                Bitmap myBitmap = BitmapFactory.decodeFile(allImages[i].getAbsolutePath());
                imageView.setImageBitmap(myBitmap);

            Log.i("Kyle", "allImages " + allImages[i]);

        }

        return itemView;

        /*View itemView = mLayoutInflater.inflate(R.layout.pager_item, collection, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
        imageView.setImageResource(mResources[position]);

        collection.addView(itemView);

        return itemView;

        /*ModelObject modelObject = ModelObject.values()[position];
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(modelObject.getLayoutResId(), collection, false);
        collection.addView(layout);
        return layout;*/
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return ModelObject.values().length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        ModelObject customPagerEnum = ModelObject.values()[position];
        return mContext.getString(customPagerEnum.getTitleResId());
    }

}