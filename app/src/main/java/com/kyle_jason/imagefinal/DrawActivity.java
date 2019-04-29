package com.kyle_jason.imagefinal;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DrawActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private DrawingView dv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(Html.fromHtml("<small>Draw" +
                "</small>"));

        dv = findViewById(R.id.drawingView);
        dv.setBackground(new ColorDrawable(0xfffdfdfd));

        Button undoButton = findViewById(R.id.undoButton);
        undoButton.setText(Html.fromHtml("&#8630;"));
        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dv.undoLast();
            }
        });

        Button redoButton = findViewById(R.id.redoButton);
        redoButton.setText(Html.fromHtml("&#8631;"));
        redoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dv.redoLast();
            }
        });

        SeekBar seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quit?");
        builder.setMessage("Drawing will be lost");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DrawActivity.super.onBackPressed();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, 0, 0, "Color");
        menu.add(0, 1, 0, "Clear");
        menu.add(0, 2, 0, "Save");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                showColorPickerDialog();
                return true;
            case 1:
                dv.clearAll();
                dv.removeImage();
                return true;
            case 2:
                dv.setDrawingCacheEnabled(true);
                dv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                Bitmap bitmap = dv.getDrawingCache();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.US);
                Date date = new Date();
                String filename = "drawing_" + dateFormat.format(date) + ".jpg";
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
                    Toast.makeText(getApplicationContext(), "Drawing Saved", Toast.LENGTH_LONG)
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
                dv.setDrawingCacheEnabled(false);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showColorPickerDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_color_picker, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setCancelable(true);
        final AlertDialog colorPickerDialog = builder.create();
        colorPickerDialog.show();
        colorPickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(
                Color.TRANSPARENT));
        colorPickerDialog.findViewById(R.id.orangeColor).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dv.setCurrentColor(0xffff8800);
                        colorPickerDialog.dismiss();
                    }
                });
        colorPickerDialog.findViewById(R.id.yellowColor).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dv.setCurrentColor(0xffffee33);
                        colorPickerDialog.dismiss();
                    }
                });
        colorPickerDialog.findViewById(R.id.greenColor).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dv.setCurrentColor(0xff008800);
                        colorPickerDialog.dismiss();
                    }
                });
        colorPickerDialog.findViewById(R.id.blueColor).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dv.setCurrentColor(0xff0000cc);
                        colorPickerDialog.dismiss();
                    }
                });
        colorPickerDialog.findViewById(R.id.redColor).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dv.setCurrentColor(0xffff0000);
                        colorPickerDialog.dismiss();
                    }
                });
        colorPickerDialog.findViewById(R.id.purpleColor).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dv.setCurrentColor(0xff883399);
                        colorPickerDialog.dismiss();
                    }
                });
        colorPickerDialog.findViewById(R.id.blackColor).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dv.setCurrentColor(0xff000000);
                        colorPickerDialog.dismiss();
                    }
                });
        colorPickerDialog.findViewById(R.id.whiteColor).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dv.setCurrentColor(0xffffffff);
                        colorPickerDialog.dismiss();
                    }
                });
        colorPickerDialog.findViewById(R.id.grayColor).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dv.setCurrentColor(0xffaaaaaa);
                        colorPickerDialog.dismiss();
                    }
                });
        colorPickerDialog.findViewById(R.id.limegreenColor).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dv.setCurrentColor(0xff22ff00);
                        colorPickerDialog.dismiss();
                    }
                });
        colorPickerDialog.findViewById(R.id.ltpurpleColor).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dv.setCurrentColor(0xffcc99ff);
                        colorPickerDialog.dismiss();
                    }
                });
        colorPickerDialog.findViewById(R.id.redorangeColor).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dv.setCurrentColor(0xffff5500);
                        colorPickerDialog.dismiss();
                    }
                });
        colorPickerDialog.findViewById(R.id.tealColor).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dv.setCurrentColor(0xff66ffff);
                        colorPickerDialog.dismiss();
                    }
                });
        colorPickerDialog.findViewById(R.id.pinkColor).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dv.setCurrentColor(0xffff00ff);
                        colorPickerDialog.dismiss();
                    }
                });
        colorPickerDialog.findViewById(R.id.ltbrownColor).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dv.setCurrentColor(0xff996633);
                        colorPickerDialog.dismiss();
                    }
                });
        colorPickerDialog.findViewById(R.id.brownColor).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dv.setCurrentColor(0xff663300);
                        colorPickerDialog.dismiss();
                    }
                });
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        dv.setStrokeWidth(i + 5);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // onStartTrackingTouch
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // onStopTrackingTouch
    }
}
