package com.example.testing.goubiquitous;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.text.format.Time;
import android.view.SurfaceHolder;

/**
 * Created by matthiasko on 2/1/16.
 */
public class WeatherWatchFaceService extends CanvasWatchFaceService {

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

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

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
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {

            canvas.drawRect(0, 0, bounds.width(), bounds.height(), mBackgroundPaint);

            mTime.setToNow();

            String text = String.format("%d:%02d", mTime.hour, mTime.minute);
            canvas.drawText(text, bounds.centerX() - mTextXOffset, bounds.centerY() - mTextYOffset, mTextPaint);

            /*
            canvas.drawText("12:00",
                    bounds.centerX() - mTextXOffset,
                    bounds.centerY() - mTextYOffset,
                    mTextPaint);
                    */

        }
    }
}
