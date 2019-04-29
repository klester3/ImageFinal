package com.kyle_jason.imagefinal;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class EditorActivity extends AppCompatActivity {

    private ImageView imageView;
    private Bitmap imageBitmap;
    private int imageWidth;
    private int imageHeight;
    private String photoPath;
    private final int CROP_REQUEST_CODE = 1;

    private SharedPreferences.Editor editor;
    private SharedPreferences pref;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(Html.fromHtml("<small>Image Editor" +
                "</small>"));

        imageView = findViewById(R.id.editorImageView);

        Intent intent = getIntent();
        String imagePath = intent.getStringExtra("imagePath");

        imageBitmap = BitmapFactory.decodeFile(imagePath);
        setImageOrientation(imagePath);
        imageView.setImageBitmap(imageBitmap);

        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        editor = pref.edit();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, 0, 0, "Crop");
        menu.add(0, 1, 0, "Filter");
        menu.add(0, 2, 0, "Brightness");
        menu.add(0, 3, 0, "Contrast");
        menu.add(0, 4, 0, "Save");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                cropImage();
                return true;
            case 1:
                showFilterDialog();
                return true;
            case 2:
                showBrightnessDialog();
                return true;
            case 3:
                showContrastDialog();
                return true;
            case 4:
                Bitmap bitmap = imageBitmap;
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.US);
                Date date = new Date();


                String filename = "photo_" + dateFormat.format(date) + ".jpg";

                String filepath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                        File.separator + "DCIM" + File.separator + "ImageEditor";
                File dir = new File(filepath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File slideFile = new File(Environment.getExternalStorageDirectory() + "/SlideFile");

                if (!slideFile.exists()) {
                    File wallpaperDirectory = new File("/sdcard/SlideFile/");
                    wallpaperDirectory.mkdirs();
                }
                File file = new File(filepath + File.separator + filename);
                File slideShowFile = new File(new File("/sdcard/SlideFile/"), filename);
                FileOutputStream fileOutputStream;
                try {
                    file.createNewFile();
                    fileOutputStream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    Toast.makeText(getApplicationContext(), "Image Saved", Toast.LENGTH_LONG)
                            .show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Error Saving", Toast.LENGTH_LONG)
                            .show();
                    Log.i("IMG_ERROR", e.getMessage());
                }

                try {
                    fileOutputStream = new FileOutputStream(slideShowFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    Toast.makeText(getApplicationContext(), "Image Saved", Toast.LENGTH_LONG)
                            .show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Error Saving", Toast.LENGTH_LONG)
                            .show();
                    Log.i("IMG_ERROR", e.getMessage());
                }


                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
        String imageFileName = "CROP_" + timeStamp;
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/crop/");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = new File(storageDir,(imageFileName + ".jpg"));
        try {
            image.createNewFile();
        } catch (IOException e) {
            Log.i("IMG_ERROR", e.getMessage());
        }

        photoPath = image.getAbsolutePath();
        return image;
    }

    private void cropImage() {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setDataAndType(getImageUri(), "image/*");
        cropIntent.putExtra("crop", true);
        cropIntent.putExtra("scaleUpIfNeeded", true);
        cropIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG);
        if (cropIntent.resolveActivity(getPackageManager()) != null) {
            File imageFile = null;
            try {
                imageFile = createImageFile();
            } catch (IOException e) {
                Log.i("IMG_ERROR", e.getMessage());
            }
            if (imageFile != null) {
                Uri imageUri = Uri.fromFile(imageFile);
                cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(cropIntent, CROP_REQUEST_CODE);
            }
        }
    }

    private Uri getImageUri() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getApplicationContext()
                .getContentResolver(), imageBitmap, "CropImage", null);
        return Uri.parse(path);
    }

    private void showBrightnessDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_adjust_brightness, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setCancelable(true);
        final AlertDialog brightnessDialog = builder.create();
        brightnessDialog.show();
        brightnessDialog.getWindow().setBackgroundDrawable(new ColorDrawable(
                Color.TRANSPARENT));
        brightnessDialog.findViewById(R.id.increase_brightness).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        brightnessDialog.dismiss();
                        changeBrightness(20);
                    }
                });
        brightnessDialog.findViewById(R.id.decrease_brightness).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        brightnessDialog.dismiss();
                        changeBrightness(-20);
                    }
                });
    }

    private void showContrastDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_adjust_contrast, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setCancelable(true);
        final AlertDialog contrastDialog = builder.create();
        contrastDialog.show();
        contrastDialog.getWindow().setBackgroundDrawable(new ColorDrawable(
                Color.TRANSPARENT));
        contrastDialog.findViewById(R.id.increase_contrast).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        contrastDialog.dismiss();
                        changeContrast(20);
                    }
                });
        contrastDialog.findViewById(R.id.decrease_contrast).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        contrastDialog.dismiss();
                        changeContrast(-10);
                    }
                });
    }

    private void showFilterDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_filter_picker, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setCancelable(true);
        final AlertDialog filterPickerDialog = builder.create();
        filterPickerDialog.show();
        filterPickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(
                Color.TRANSPARENT));
        filterPickerDialog.findViewById(R.id.film_grain).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        filterPickerDialog.dismiss();
                        filmgrainEffect();
                    }
                });
        filterPickerDialog.findViewById(R.id.gray_scale).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        filterPickerDialog.dismiss();
                        grayscaleEffect();
                    }
                });
        filterPickerDialog.findViewById(R.id.invert_color).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        filterPickerDialog.dismiss();
                        invertEffect();
                    }
                });
        filterPickerDialog.findViewById(R.id.sepia_tone).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        filterPickerDialog.dismiss();
                        sepiaEffect();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CROP_REQUEST_CODE && resultCode == RESULT_OK) {
            imageBitmap = BitmapFactory.decodeFile(photoPath);
            imageView.setImageBitmap(imageBitmap);
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quit?");
        builder.setMessage("Changes will be lost");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditorActivity.super.onBackPressed();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void grayscaleEffect() {
        imageWidth = imageBitmap.getWidth();
        imageHeight = imageBitmap.getHeight();

        final double gsRed = 0.299;
        final double gsBlue = 0.587;
        final double gsGreen = 0.114;

        Bitmap gsBitmap = Bitmap.createBitmap(imageWidth, imageHeight, imageBitmap.getConfig());

        int a, r, g, b;
        int pixel;


        for (int x = 0; x < imageWidth; x++) {
            for (int y = 0; y < imageHeight; y++) {
                pixel = imageBitmap.getPixel(x, y);
                a = Color.alpha(pixel);
                r = Color.red(pixel);
                g = Color.green(pixel);
                b = Color.blue(pixel);

                r = g = b = (int) (gsRed * r + gsGreen * g + gsBlue * b);

                gsBitmap.setPixel(x, y, Color.argb(a, r, g, b));
            }
        }

        imageBitmap = gsBitmap;
        imageView.setImageBitmap(imageBitmap);
    }

    private void filmgrainEffect() {
        imageWidth = imageBitmap.getWidth();
        imageHeight = imageBitmap.getHeight();

        final int colorMax = 0xff;

        int[] pixels = new int[imageWidth * imageHeight];

        imageBitmap.getPixels(pixels, 0, imageWidth, 0, 0, imageWidth, imageHeight);

        Random random = new Random();

        int index;

        for (int y = 0; y < imageHeight; y++) {
            for (int x = 0; x < imageWidth; x++) {
                index = y * imageWidth + x;
                int randomColor = Color.rgb(random.nextInt(colorMax), random.nextInt(colorMax),
                        random.nextInt(colorMax));
                pixels[index] |= randomColor;
            }
        }

        Bitmap fgBitmap = Bitmap.createBitmap(imageWidth, imageHeight, imageBitmap.getConfig());
        fgBitmap.setPixels(pixels, 0, imageWidth, 0, 0, imageWidth, imageHeight);

        imageBitmap = fgBitmap;
        imageView.setImageBitmap(imageBitmap);
    }

    private void invertEffect() {
        imageWidth = imageBitmap.getWidth();
        imageHeight = imageBitmap.getHeight();

        Bitmap invBitmap = Bitmap.createBitmap(imageWidth, imageHeight, imageBitmap.getConfig());

        int a, r, g, b;
        int pixel;

        for (int x = 0; x < imageWidth; x++) {
            for (int y = 0; y < imageHeight; y++) {
                pixel = imageBitmap.getPixel(x, y);
                a = Color.alpha(pixel);
                r = 255 - Color.red(pixel);
                g = 255 - Color.green(pixel);
                b = 255 - Color.blue(pixel);

                invBitmap.setPixel(x, y, Color.argb(a, r, g, b));
            }
        }

        imageBitmap = invBitmap;
        imageView.setImageBitmap(imageBitmap);
    }

    private void sepiaEffect() {
        imageWidth = imageBitmap.getWidth();
        imageHeight = imageBitmap.getHeight();

        Bitmap sepiaBitmap = Bitmap.createBitmap(imageWidth, imageHeight, imageBitmap.getConfig());

        final double red = 0.3;
        final double green = 0.59;
        final double blue = 0.11;

        int a, r, g, b;
        int pixel;

        for (int x = 0; x < imageWidth; x++) {
            for (int y = 0; y < imageHeight; y++) {
                pixel = imageBitmap.getPixel(x, y);

                a = Color.alpha(pixel);
                r = Color.red(pixel);
                g = Color.green(pixel);
                b = Color.blue(pixel);

                b = g = r = (int) (red * r + green * g + blue * b);

                r += 200 * red;
                if (r > 255) {r = 255;}

                sepiaBitmap.setPixel(x, y, Color.argb(a, r, g, b));
            }
        }

        imageBitmap = sepiaBitmap;
        imageView.setImageBitmap(imageBitmap);
    }

    private void changeBrightness(double value) {
        imageWidth = imageBitmap.getWidth();
        imageHeight = imageBitmap.getHeight();

        Bitmap newBitmap = Bitmap.createBitmap(imageWidth, imageHeight, imageBitmap.getConfig());

        int a, r, g, b;
        int pixel;

        for (int x = 0; x < imageWidth; x++) {
            for (int y = 0; y < imageHeight; y++) {
                pixel = imageBitmap.getPixel(x, y);
                a = Color.alpha(pixel);
                r = Color.red(pixel);
                g = Color.green(pixel);
                b = Color.blue(pixel);

                r += value;
                if (r < 0) {
                    r = 0;
                } else if (r > 255) {
                    r = 255;
                }
                if (g < 0) {
                    g = 0;
                } else if (g > 255) {
                    g = 255;
                }
                if (b < 0) {
                    b = 0;
                } else if (b > 255) {
                    b = 255;
                }

                newBitmap.setPixel(x, y, Color.argb(a, r, g, b));
            }
        }

        imageBitmap = newBitmap;
        imageView.setImageBitmap(imageBitmap);
    }

    private void changeContrast(double value) {
        imageWidth = imageBitmap.getWidth();
        imageHeight = imageBitmap.getHeight();

        Bitmap newBitmap = Bitmap.createBitmap(imageWidth, imageHeight, imageBitmap.getConfig());

        int a, r, g, b;
        int pixel;

        double contrast = Math.pow((100 + value) / 100 , 2);

        for (int x = 0; x < imageWidth; x++) {
            for (int y = 0; y < imageHeight; y++) {
                pixel = imageBitmap.getPixel(x, y);
                a = Color.alpha(pixel);
                r = Color.red(pixel);
                r = (int) (((((r / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if (r < 0) {
                    r = 0;
                } else if (r > 255) {
                    r = 255;
                }
                g = Color.green(pixel);
                g = (int) (((((g / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if (g < 0) {
                    g = 0;
                } else if (g > 255) {
                    g = 255;
                }
                b = Color.blue(pixel);
                b = (int) (((((b / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if (b < 0) {
                    b = 0;
                } else if (b > 255) {
                    b = 255;
                }
                newBitmap.setPixel(x, y, Color.argb(a, r, g, b));
            }
        }

        imageBitmap = newBitmap;
        imageView.setImageBitmap(imageBitmap);
    }
}
