package com.kyle_jason.imagefinal;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final int TAKE_PICTURE_CODE = 1;
    private final int OPEN_IMAGE_CODE = 2;
    private ImageView imageView;
    private Bitmap imageBitmap;
    private String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        verifyStoragePermissions(this);

        findViewById(R.id.cameraButton).setOnClickListener(this);
        findViewById(R.id.drawButton).setOnClickListener(this);
        findViewById(R.id.openButton).setOnClickListener(this);

        imageView = findViewById(R.id.imageView);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.cameraButton) {
            takePicture();
        } else if (view.getId() == R.id.drawButton) {
            openPaint();
        } else if (view.getId() == R.id.openButton) {
            openImage();
        } else {
            throw new RuntimeException("Not Implemented");
        }
    }

    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(activity, permissions, 1);
        }
    }

    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File imageFile = null;
            try {
                imageFile = createImageFile();
            } catch (IOException e) {
                Log.i("IMG_ERROR", e.getMessage());
            }
            if (imageFile != null) {
                Uri imageUri = FileProvider.getUriForFile(this,
                        "com.kyle_jason.imagefinal", imageFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(takePictureIntent, TAKE_PICTURE_CODE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        photoPath = image.getAbsolutePath();
        return image;
    }

    private void openPaint() {
        //open a blank canvas to draw on
    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), OPEN_IMAGE_CODE);
    }

    private void setImageOrientation(String fileName) {
        try {
            ExifInterface ei = new ExifInterface(fileName);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    imageBitmap = rotateImage(imageBitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    imageBitmap = rotateImage(imageBitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    imageBitmap = rotateImage(imageBitmap, 270);
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            Log.i("IMG_ERROR", e.getMessage());
        }
    }

    private Bitmap rotateImage(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                matrix, true);
    }

    private String getFilePath(Uri uri) {
        final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
        String selection = null;
        String[] selectionArgs = null;
        if (needToCheckUri && DocumentsContract.isDocumentUri(getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                selection = "_id=?";
                selectionArgs = new String[]{split[1]};
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor;
            try {
                cursor = getApplicationContext().getContentResolver().query(uri, projection,
                        selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                Log.i("IMG_ERROR", e.getMessage());
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    private boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_PICTURE_CODE && resultCode == RESULT_OK) {
            imageBitmap = BitmapFactory.decodeFile(photoPath);
            setImageOrientation(photoPath);
            imageView.setImageBitmap(imageBitmap);
        } else if (requestCode == OPEN_IMAGE_CODE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                imageBitmap = BitmapFactory.decodeStream(inputStream);
                setImageOrientation(getFilePath(imageUri));
                imageView.setImageBitmap(imageBitmap);
            } catch (FileNotFoundException e) {
                Log.i("IMG_ERROR", e.getMessage());
            }
        }
    }
}
