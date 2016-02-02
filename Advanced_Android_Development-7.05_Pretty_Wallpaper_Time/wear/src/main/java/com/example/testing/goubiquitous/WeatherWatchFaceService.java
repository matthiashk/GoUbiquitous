package com.example.testing.goubiquitous;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.text.format.Time;
import android.view.SurfaceHolder;

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

    private class Engine extends CanvasWatchFaceService.Engine {
        Paint mTextPaint;
        Float mTextXOffset;
        Float mTextYOffset;

        Time mTime;
        Paint mBackgroundPaint;

        Paint mDatePaint;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

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




        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {

            int width = bounds.width();
            int height = bounds.height();
            float radius = width / 2;
            float yOffset;

            yOffset = 0;

            canvas.drawRect(0, 0, bounds.width(), bounds.height(), mBackgroundPaint);

            mTime.setToNow();

            String text = String.format("%d:%02d", mTime.hour, mTime.minute);
            canvas.drawText(text, bounds.centerX() - mTextXOffset, bounds.centerY() - mTextYOffset, mTextPaint);

            String myDate = "MON, FEB / 01 / 2016";

            // TODO: fix position
            canvas.drawText(myDate, bounds.centerX() - mTextXOffset - 40, bounds.centerY() - mTextYOffset + 30, mDatePaint);


            // check if name is right

            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append("art_clear");

            String name = stringBuilder.toString();

            //int id = mResources.getIdentifier(name, "drawable", "com.example.android.sunshine.app"); // check if this is right

            Drawable b = mResources.getDrawable(R.drawable.art_clear);
            mWeatherConditionDrawable = ((BitmapDrawable) b).getBitmap();
            float sizeScale = (width * 0.5f) / mWeatherConditionDrawable.getWidth();
            mWeatherConditionDrawable = Bitmap.createScaledBitmap(mWeatherConditionDrawable, (int) (mWeatherConditionDrawable.getWidth() * sizeScale), (int) (mWeatherConditionDrawable.getHeight() * sizeScale), true);

            canvas.drawBitmap(mWeatherConditionDrawable, radius - mWeatherConditionDrawable.getWidth() / 2, 0 - yOffset, null);


    }
    }
}
