package com.kyle_jason.imagefinal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class DrawingView extends View {

    private final float TOUCH_TOLERANCE = 4f;

    private int currentWidth;
    private int currentHeight;

    private ArrayList<PaintPath> paths;
    private ArrayList<PaintPath> redoPaths;
    private PaintPath paintPath;
    private Path path;
    private float pathX;
    private float pathY;
    private Bitmap image;
    private float scale;
    private float bufferY;
    private float bufferX;

    private Paint paint;
    private int strokeWidth;
    private int color;

    public DrawingView(Context context) {
        super(context);
        setup(null);
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(attrs);
    }

    public DrawingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(attrs);
    }

    public void setup(AttributeSet attrs) {
        paths = new ArrayList<>();
        redoPaths = new ArrayList<>();
        paint = new Paint();
        strokeWidth = 5;
        color = 0xff000000;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        currentWidth = w;
        currentHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setAntiAlias(true);

        if (image != null) {
            Matrix m = new Matrix();
            m.setTranslate(bufferX, bufferY);
            canvas.drawBitmap(image, m, paint);
        }

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);

        for (PaintPath paintPath : paths) {
            paint.setColor(paintPath.color);
            paint.setStrokeWidth(paintPath.strokeWidth);
            canvas.drawPath(paintPath.path, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startPath(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                movePath(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                endPath();
                invalidate();
                break;
        }

        return true;
    }

    private void startPath(float x, float y) {
        redoPaths.clear();
        path = new Path();
        paintPath = new PaintPath(color, strokeWidth, path);
        paths.add(paintPath);
        path.reset();
        path.moveTo(x, y);
        pathX = x;
        pathY = y;
    }

    private void movePath(float x, float y) {
        float moveX = Math.abs(x - pathX);
        float moveY = Math.abs(y - pathY);
        if (moveX >= TOUCH_TOLERANCE || moveY >= TOUCH_TOLERANCE) {
            path.quadTo(pathX, pathY, (x + pathX)/2, (y + pathY)/2);
            pathX = x;
            pathY = y;
        }
    }

    private void endPath() { path.lineTo(pathX, pathY); }

    public void undoLast() {
        if (paths.size() > 0) {
            redoPaths.add(paths.get(paths.size() - 1));
            paths.remove(paths.size() - 1);
            invalidate();
        }
    }

    public void redoLast() {
        if (redoPaths.size() > 0) {
            paths.add(redoPaths.get(redoPaths.size() - 1));
            redoPaths.remove(redoPaths.size() - 1);
            invalidate();
        }
    }

    public void setCurrentColor(int color) {
        this.color = color;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public void clearAll() {
        paths.clear();
        redoPaths.clear();
        invalidate();
    }

    public void loadImage(Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        Bitmap img;
        if (bitmap.getWidth() > bitmap.getHeight()) {
            Matrix m = new Matrix();
            m.postRotate(90);
            img = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m,
                    true);
        } else {
            img = bitmap;
        }
        float height = img.getHeight();
        float width = img.getWidth();
        if (height > currentHeight) {
            scale = currentHeight/height;
            if (width > currentWidth) {
                scale = currentWidth / width;
            }
        } else {
            scale = 1f;
        }
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        image = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix,
                true);
        if (image.getHeight() < currentHeight) {
            bufferY = (currentHeight - image.getHeight()) / 2f;
        } else {
            bufferY = 0f;
        }
        if (image.getWidth() < currentWidth) {
            bufferX = (currentWidth - image.getWidth()) / 2f;
        } else {
            bufferX = 0f;
        }
        invalidate();
    }

    public void removeImage() {
        image = null;
        invalidate();
    }

}
