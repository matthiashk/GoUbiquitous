package com.example.android.sunshine.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.text.format.Time;
import android.util.Log;
import android.view.SurfaceHolder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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
            implements DataApi.DataListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

        private GoogleApiClient mGoogleApiClient;

        Paint mTextPaint;
        Float mTextXOffset;
        Float mTextYOffset;

        Time mTime;
        Paint mBackgroundPaint;

        Paint mDatePaint;

        Paint mHighLow;

        String mHighLowString;

        private static final long TIMEOUT_MS = 100; // is this value OK?



        public class MessageReceiver extends BroadcastReceiver {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = intent.getStringExtra("message");
                // Display message in UI
                //mTextView.setText(message);

                mHighLowString = message;




                invalidate(); // call onDraw to refresh display

                System.out.println("message = " + message);
            }
        }


        @Override
        public void onDataChanged(DataEventBuffer dataEvents) {

            System.out.println("WeatherWatchFaceService - onDataChanged"); // this is not called


            for (DataEvent event : dataEvents) {
                if (event.getType() == DataEvent.TYPE_CHANGED &&
                        event.getDataItem().getUri().getPath().equals("/image")) {



                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                    Asset profileAsset = dataMapItem.getDataMap().getAsset("weatherImage");
                    mWeatherConditionDrawable = loadBitmapFromAsset(profileAsset);


                    invalidate();


                }
            }
        }

        public Bitmap loadBitmapFromAsset(Asset asset) {
            if (asset == null) {
                throw new IllegalArgumentException("Asset must be non-null");
            }
            ConnectionResult result =
                    mGoogleApiClient.blockingConnect(TIMEOUT_MS, TimeUnit.MILLISECONDS);
            if (!result.isSuccess()) {
                return null;
            }
            // convert asset into a file descriptor and block until it's ready
            InputStream assetInputStream = Wearable.DataApi.getFdForAsset(
                    mGoogleApiClient, asset).await().getInputStream();
            mGoogleApiClient.disconnect();

            if (assetInputStream == null) {
                Log.w("WeatherWatchFaceService", "Requested an unknown Asset.");
                return null;
            }
            // decode the stream into a bitmap
            return BitmapFactory.decodeStream(assetInputStream);
        }

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            //System.out.println("onCreate **********************");

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

            mHighLowString = "15 / 32";



            // register for messages from the mobile app
            IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
            MessageReceiver messageReceiver = new MessageReceiver();

            LocalBroadcastManager.getInstance(WeatherWatchFaceService.this).registerReceiver(messageReceiver, messageFilter);


            // create the placeholder image
            Drawable b = mResources.getDrawable(R.drawable.art_clear);
            mWeatherConditionDrawable = ((BitmapDrawable) b).getBitmap();
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {

            //System.out.println("onDraw **********************");


            int width = bounds.width();
            int height = bounds.height();
            float radius = width / 2;
            float yOffset;

            yOffset = 0;

            canvas.drawRect(0, 0, bounds.width(), bounds.height(), mBackgroundPaint);

            mTime.setToNow();

            String text = String.format("%d:%02d", mTime.hour, mTime.minute);
            canvas.drawText(text, bounds.centerX() - mTextXOffset, bounds.centerY() - mTextYOffset - 80, mTextPaint);


            // get the current date and format properly
            SimpleDateFormat sdf = new SimpleDateFormat("E, MMM dd, yyyy");
            String dateString = sdf.format(new Date());




            canvas.drawText(dateString, bounds.centerX() - mTextXOffset - 40, bounds.centerY() - mTextYOffset - 40, mDatePaint);


            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append("art_clear");

            //String name = stringBuilder.toString();

            //int id = mResources.getIdentifier(name, "drawable", "com.example.android.sunshine.app"); // check if this is right




            float sizeScale = (width * 0.25f) / mWeatherConditionDrawable.getWidth(); // change size of image here
            mWeatherConditionDrawable = Bitmap.createScaledBitmap(
                    mWeatherConditionDrawable,
                    (int) (mWeatherConditionDrawable.getWidth() * sizeScale),
                    (int) (mWeatherConditionDrawable.getHeight() * sizeScale),
                    true);

            canvas.drawBitmap(mWeatherConditionDrawable, radius - mWeatherConditionDrawable.getWidth() / 2 - 50 , 0 - yOffset + 150, null);

            canvas.drawText(mHighLowString, radius - mWeatherConditionDrawable.getWidth() / 2 + 50 , 0 - yOffset + 200, mHighLow);

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
