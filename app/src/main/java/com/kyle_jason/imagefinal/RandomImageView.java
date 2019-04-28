package com.kyle_jason.imagefinal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

public class RandomImageView extends View {
    protected File imgFile;
    protected Bitmap myBitmap;
    protected Paint imagePaint;

    public RandomImageView(Context context) {
        super(context);
        setup(null);
    }

    public RandomImageView(Context context,AttributeSet attrs) {
        super(context, attrs);
        setup(attrs);
    }

    public RandomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(attrs);
    }

    private void setup(AttributeSet attrs) {

        imgFile = new  File("/sdcard/SlideShow/photo_1");

        if(imgFile.exists()){

            myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

        }

        imagePaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (myBitmap != null) {
            Matrix m = new Matrix();
            canvas.drawBitmap(myBitmap, m, imagePaint);
        }
    }
}
