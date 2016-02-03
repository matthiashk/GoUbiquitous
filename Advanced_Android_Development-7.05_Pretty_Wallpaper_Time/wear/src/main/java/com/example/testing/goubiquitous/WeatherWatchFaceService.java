package com.example.testing.goubiquitous;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.text.format.Time;
import android.view.SurfaceHolder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;

/**
 * Created by matthiasko on 2/1/16.
 */
public class WeatherWatchFaceService extends CanvasWatchFaceService {

    protected Resources mResources;
    protected Bitmap mWeatherConditionDrawable;

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private class Engine extends CanvasWatchFaceService.Engine
            implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

        private GoogleApiClient mGoogleApiClient;

        Paint mTextPaint;
        Float mTextXOffset;
        Float mTextYOffset;

        Time mTime;
        Paint mBackgroundPaint;

        Paint mDatePaint;

        Paint mHighLow;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            System.out.println("onCreate **********************");

            // Create a GoogleApiClient instance
            mGoogleApiClient = new GoogleApiClient.Builder(WeatherWatchFaceService.this)
                    // Request access only to the Wearable API
                    .addApi(Wearable.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            mResources = WeatherWatchFaceService.this.getResources();

            // Create the Paint for later use
            mTextPaint = new Paint();
            mTextPaint.setTextSize(40);
            mTextPaint.setColor(Color.WHITE);
            mTextPaint.setAntiAlias(true);

            // In order to make text in the center, we need adjust its position
            mTextXOffset = mTextPaint.measureText("12:00") / 2;
            mTextYOffset = (mTextPaint.ascent() + mTextPaint.descent()) / 2;

            Resources resources = WeatherWatchFaceService.this.getResources();
            mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor(resources.getColor(R.color.background));

            mTime = new Time();

            mDatePaint = new Paint();
            mDatePaint.setTextSize(20);
            mDatePaint.setColor(Color.WHITE);
            mDatePaint.setAntiAlias(true);

            mHighLow = new Paint();
            mHighLow.setTextSize(30);
            mHighLow.setColor(Color.WHITE);
            mHighLow.setAntiAlias(true);

        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {

            System.out.println("onDraw **********************");


            int width = bounds.width();
            int height = bounds.height();
            float radius = width / 2;
            float yOffset;

            yOffset = 0;

            canvas.drawRect(0, 0, bounds.width(), bounds.height(), mBackgroundPaint);

            mTime.setToNow();

            String text = String.format("%d:%02d", mTime.hour, mTime.minute);
            canvas.drawText(text, bounds.centerX() - mTextXOffset, bounds.centerY() - mTextYOffset - 80, mTextPaint);

            String myDate = "MON, FEB / 01 / 2016";
            canvas.drawText(myDate, bounds.centerX() - mTextXOffset - 40, bounds.centerY() - mTextYOffset - 40, mDatePaint);


            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append("art_clear");

            String name = stringBuilder.toString();

            //int id = mResources.getIdentifier(name, "drawable", "com.example.android.sunshine.app"); // check if this is right

            Drawable b = mResources.getDrawable(R.drawable.art_clear);
            mWeatherConditionDrawable = ((BitmapDrawable) b).getBitmap();
            float sizeScale = (width * 0.25f) / mWeatherConditionDrawable.getWidth(); // change size of image here
            mWeatherConditionDrawable = Bitmap.createScaledBitmap(mWeatherConditionDrawable, (int) (mWeatherConditionDrawable.getWidth() * sizeScale), (int) (mWeatherConditionDrawable.getHeight() * sizeScale), true);

            canvas.drawBitmap(mWeatherConditionDrawable, radius - mWeatherConditionDrawable.getWidth() / 2 - 50 , 0 - yOffset + 150, null);

            String highLowString = "15 / 32";

            canvas.drawText(highLowString, radius - mWeatherConditionDrawable.getWidth() / 2 + 50 , 0 - yOffset + 200, mHighLow);

        }

        @Override
        public void onConnected(Bundle connectionHint) {
            // Connected to Google Play services!
            // The good stuff goes here.
        }

        @Override
        public void onConnectionSuspended(int cause) {
            // The connection has been interrupted.
            // Disable any UI components that depend on Google APIs
            // until onConnected() is called.
        }

        @Override
        public void onConnectionFailed(ConnectionResult result) {
            // This callback is important for handling errors that
            // may occur while attempting to connect with Google.
            //
            // More about this in the 'Handle Connection Failures' section.
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            if (visible) {

                System.out.println("onVisibilityChanged");
            }


        }
    }
}
