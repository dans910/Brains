/*
 * Copyright (C) 2016
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package danielsandovalutrgv.brains;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.design.widget.FloatingActionButton;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;

public class MyTouchEventView extends View {

    public Paint paint = new Paint();
    public Path path = new Path();
    public Paint circlePaint = new Paint();
    public Path circlePath = new Path();

    public MyTouchEventView(Context context){
        super(context);
    }

    public MyTouchEventView(Context context, FloatingActionButton buttonReset/*, Button buttonRandomColor*/) {
        super(context);

        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(5f);

        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.BLACK);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeJoin(Paint.Join.MITER);
        circlePaint.setStrokeWidth(2f);

        // Reset Button
        buttonReset.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                // Resets the screen
                path.reset();

                // Calls the onDraw() method
                postInvalidate();
            }
        });
/*
        // Random Color Button
        buttonRandomColor.setOnClickListener(new View.OnClickListener() {

            Random rand = new Random();

            public void onClick(View v) {

                // Generate a random number
                int randomNumber = rand.nextInt(6) + 1;

                if(randomNumber == 1) {paint.setColor(Color.BLUE);}

                else if(randomNumber == 2) {paint.setColor(Color.GREEN);}

                else if(randomNumber == 3) {paint.setColor(Color.RED);}

                else if(randomNumber == 4) {paint.setColor(Color.YELLOW);}

                else if(randomNumber == 5) {paint.setColor(Color.GRAY);}

                else if(randomNumber == 6) {paint.setColor(Color.MAGENTA);}
            }
        });*/
    }

    //@Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(path, paint);
        canvas.drawPath(circlePath, circlePaint);
    }

    //@Override
    public boolean onTouchEvent(MotionEvent event) {

        // X and Y coordinates of the Event
        float x = event.getX();
        float y = event.getY();

        // Checks for the Event that occurs
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);

                return true;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(x, y);
                circlePath.reset();

                // Circle's center x-coordinate, y-coordinate, radius of the circle
                // direction to wind the shape
                circlePath.addCircle(x, y, 30, Path.Direction.CW);

                // circlePath.addRect(x - 25, y - 25
                break;

            case MotionEvent.ACTION_UP:
                circlePath.reset();

                break;

            default:
                return false;
        }

        // Schedules a repaint
        // Force a view to draw
        invalidate();
        return true;
    }

    @Override
    protected void onLayout(boolean change, int l, int t, int r, int b) {
        // TODO Auto-generated method stub

    }
}

