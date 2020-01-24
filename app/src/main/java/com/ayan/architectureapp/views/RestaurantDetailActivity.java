package com.ayan.architectureapp.views;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

import com.ayan.architectureapp.R;
import com.ayan.architectureapp.utils.ImageDownloader;

public class RestaurantDetailActivity extends AppCompatActivity {

    public static final String TAG = "RestaurantDetail";
    private ImageView ivRestaurantImage;
    private ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor = 1.0f;
    private boolean isTapped = false;
    private boolean isZoomed = false;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        setTitle("Restaurant Image");
        ivRestaurantImage = findViewById(R.id.ivRestaurantImage);

        String url = getIntent().getStringExtra("URL");

        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        ivRestaurantImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                onDoubleTap();
                return false;
            }
        });

        new ImageDownloader(ivRestaurantImage).loadImage(url);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(event);
        return true;

    }

    private void onDoubleTap(){

        if(isTapped){
            if(isZoomed){
                isZoomed = false;
                ivRestaurantImage.setScaleX(1.0f);
                ivRestaurantImage.setScaleY(1.0f);
            }else {
                isZoomed = true;
                ivRestaurantImage.setScaleX(5.0f);
                ivRestaurantImage.setScaleY(5.0f);
            }

        }

        isTapped = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isTapped = false;
            }
        },250);


    }


    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(1.0f,
                    Math.min(mScaleFactor, 5.0f));
            ivRestaurantImage.setScaleX(mScaleFactor);
            ivRestaurantImage.setScaleY(mScaleFactor);
            return true;
        }
    }

}