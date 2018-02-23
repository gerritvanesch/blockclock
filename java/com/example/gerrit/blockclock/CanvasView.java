package com.example.gerrit.blockclock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class CanvasView extends View {

    Paint paint = new Paint();
    Timer timer = new Timer();
    Date now = null;

    public CanvasView(Context context) {
        super(context);
    }

    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CanvasView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width  = getWidth();
        int height = getHeight();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        canvas.drawPaint(paint);

        now = Calendar.getInstance().getTime();
        int h = now.getHours();
        int m = now.getMinutes();
        int s = now.getSeconds();

        // subtract 200 for the titles etc. and make the hours bigger than the minutes bigger than the seconds
        float hHeight = Math.max(50, (float)((height - 200) / 2.6));
        float mHeight = Math.max(40, (float)((height - 200) / 2.95));
        float sHeight = Math.max(30, (float)((height - 200) / 3.3));

        float widthUnit = 0;
        float heightUnit = 0;

        // draw the titles
        paint.setColor(Color.WHITE);
        paint.setTextSize(40);
        paint.setTextAlign(Paint.Align.CENTER);

        int center = width / 2;

        canvas.drawText("HOURS", center, 40, paint);
        canvas.drawText("MINUTES", center, 90 + hHeight, paint);
        canvas.drawText("SECONDS", center, 140 + hHeight + mHeight, paint);

        paint.setStrokeWidth(1);

        // draw the hours boxes
        paint.setColor(Color.parseColor("#FF4D4D"));
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        int hourbreak = 0;

        for(int i = 0; i < 24; i++) {
            // once we have filled a box for every hour, switch to a light color
            if (i == h)
                paint.setColor(Color.parseColor("#FFCCCC"));
            // if 12+ hours have passed, need to combine the first 3 blocks (5+5+2)
            if (h >= 12) {
                // put spaces between the rest
                if (i == 12 || i == 17 || i == 22)
                    hourbreak += 10;
                // distribute the extra space across the first 20 blocks
                // subtract 70 for the spaces, 20 for the outside borders, div into 24 for 24 hour blocks
                widthUnit = ((width - 20 - 70) / 24) + ((i < 10) ? 2 : 0);
                // after the extra space has been distributed, we need to make sure the alignment won't be off
                if (i == 10)
                    hourbreak += 20;
            } else {
                // put divisions in to group the boxes
                if (i == 5 || i == 10 || i == 12 || i == 17 || i == 22)
                    hourbreak += 10;
                widthUnit = ((width - 20 - 70) / 24);
            }
            canvas.drawRect(10 + (i * widthUnit) + hourbreak, 50, 10 + ((i + 1) * widthUnit) + hourbreak, 50 + hHeight, paint);
        }

        // draw borders around hour blocks in darker color. Other code the same
        paint.setColor(Color.parseColor("#800000"));
        paint.setStyle(Paint.Style.STROKE);
        hourbreak = 0;

        for(int i = 0; i < 24; i++) {
            if (h >= 12) {
                if (i == 12 || i == 17 || i == 22)
                    hourbreak += 10;
                widthUnit = ((width - 20 - 70) / 24) + ((i < 10) ? 2 : 0);
                if (i == 10)
                    hourbreak += 20;
            } else {
                if (i == 5 || i == 10 || i == 12 || i == 17 || i == 22)
                    hourbreak += 10;
                widthUnit = ((width - 20 - 70) / 24);
            }
            canvas.drawRect(10 + (i * widthUnit) + hourbreak, 50, 10 + ((i + 1) * widthUnit) + hourbreak, 50 + hHeight, paint);
        }

        // draw the minute blocks
        paint.setColor(Color.parseColor("#FFFF33"));
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        // we'll pull them apart into 6 groups of 10, with 2 columns and 5 rows for each group, and spaces in between
        // groups will go beside each other horizontally
        // divide subtract spaces as before, divide into 12 b/c we'll have 2 columns for every group of 10 minutes--6*2
        widthUnit = (width - 20 - 70) / 12;
        // have 5 rows
        heightUnit = mHeight / 5;

        for (int i = 0; i < 60; i++) {
            // if we've reached the number of minutes passed, switch to light color
            if (i == m)
                paint.setColor(Color.parseColor("#FFFFCC"));
            // KEEPING IN MIND THAT THE INDEX STARTS AT 0 NOT 1 -- SO MINUTE #1 IS BEING REPRESENTED AS 0
            // We want to fill in the boxes from the bottom left.
            // Finding the left offset. Divide by 10 and round down, then multiply by two. If it is an odd number add 1
            // That gets us the number of the column that we need to add this box to (starting at 0). Then multiply this by a width unit to get a distance
            // To this add the result of rounding down i divided by 10 (which gets us the number of spaces between groups that we need to account for) and multiply this by 10 to get a distance
            float leftOffset = (((int)Math.floor(i / 10) * 2 + ((i % 2 == 0) ? 0 : 1)) * widthUnit) + ((int)Math.floor(i / 10) * 10);
            // for the top offset we need to find the number of rows that we need to go down
            // Find the remainder of i divided by 10 (which gets us the ones place in the minutes) and divide by 2, then round it down.
            // Will be clear with examples--i=15 (minute #16) - 2.5 rounded to 2. It's in the third row from top. i=9 (minute 10) - 4.5 rounded to 4. It's in the first row from top.
            // i=16 (minute #17) - 3. It's in the 2nd row from top. To get the number from top with indexes starting at 0, subtract this result from 4.
            // Then multiply by the heightUnit to get a distance from top.
            float topOffset  = (4 - (int)Math.floor(i % 10 / 2)) * heightUnit;
            canvas.drawRect(10 + leftOffset, 100 + hHeight + topOffset, 10 + leftOffset + widthUnit, 100 + hHeight + topOffset + heightUnit, paint);
        }

        // draw the borders in a darker color using the same math.
        paint.setColor(Color.parseColor("#999900"));
        paint.setStyle(Paint.Style.STROKE);

        for (int i = 0; i < 60; i++) {
            float leftOffset = (((int)Math.floor(i / 10) * 2 + ((i % 2 == 0) ? 0 : 1)) * widthUnit) + ((int)Math.floor(i / 10) * 10);
            float topOffset  = (4 - (int)Math.floor(i % 10 / 2)) * heightUnit;
            canvas.drawRect(10 + leftOffset, 100 + hHeight + topOffset, 10 + leftOffset + widthUnit, 100 + hHeight + topOffset + heightUnit, paint);
        }

        // draw the seconds blocks
        paint.setColor(Color.parseColor("#33E283"));
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        // same idea as with minutes, but use the seconds height. The width unit is the same as minutes.
        heightUnit = sHeight / 5;

        for (int i = 0; i < 60; i++) {
            // if we've reached the number of seconds passed, switch to light color
            if (i == s)
                paint.setColor(Color.parseColor("#CCFFE6"));
            // this math is the same as with minutes but using the seconds index and values
            float leftOffset = (((int)Math.floor(i / 10) * 2 + ((i % 2 == 0) ? 0 : 1)) * widthUnit) + ((int)Math.floor(i / 10) * 10);
            float topOffset  = (4 - (int)Math.floor(i % 10 / 2)) * heightUnit;
            canvas.drawRect(10 + leftOffset, 150 + hHeight + mHeight + topOffset, 10 + leftOffset + widthUnit, 150 + hHeight + mHeight + topOffset + heightUnit, paint);
        }

        // draw the borders in a darker color
        paint.setColor(Color.parseColor("#007C1D"));
        paint.setStyle(Paint.Style.STROKE);

        for (int i = 0; i < 60; i++) {
            float leftOffset = (((int)Math.floor(i / 10) * 2 + ((i % 2 == 0) ? 0 : 1)) * widthUnit) + ((int)Math.floor(i / 10) * 10);
            float topOffset  = (4 - (int)Math.floor(i % 10 / 2)) * heightUnit;
            canvas.drawRect(10 + leftOffset, 150 + hHeight + mHeight + topOffset, 10 + leftOffset + widthUnit, 150 + hHeight + mHeight + topOffset + heightUnit, paint);
        }

        // schedule a redraw to update the time in 250 milliseconds.
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                postInvalidate();
            }
        }, 250);
    }

}
