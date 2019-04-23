package com.kyle_jason.imagefinal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.cameraButton).setOnClickListener(this);
        findViewById(R.id.drawButton).setOnClickListener(this);
        findViewById(R.id.openButton).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.cameraButton) {
            //open camera to take picture
        } else if (viewId == R.id.drawButton) {
            //open blank canvas for drawing
        } else if (viewId == R.id.openButton) {
            //open file system to select image
        } else {
            throw new RuntimeException("Not Implemented");
        }
    }
}
