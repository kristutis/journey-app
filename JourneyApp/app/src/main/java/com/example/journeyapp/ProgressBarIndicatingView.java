package com.example.journeyapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;

public class ProgressBarIndicatingView extends View {
    public static final int NOTEXECUTED = 0;
    public static final int LOADING = 1;
    public static final int BAR_BELOW = 10;

    int state = NOTEXECUTED;
    int transWidth=-1;

    public ProgressBarIndicatingView(Context context) {
        super(context);
    }

    public ProgressBarIndicatingView (Context context, AttributeSet attr) {
        super(context,attr);
    }

    public ProgressBarIndicatingView (Context context, AttributeSet attr,int defStyleAttr) {
        super(context,attr, defStyleAttr);
    }

    public int getState() {
        return  state;
    }

    public void setState(int state) {
        this.state=state;
    }

    public void setTransWidth(int w) {
        this.transWidth=w;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        Paint paint;
        Rect r;
        switch (state) {
            case LOADING:
                ViewGroup.LayoutParams params = getLayoutParams();
                params.width=transWidth;
                setLayoutParams(params);
                break;
            case BAR_BELOW:
                paint = new Paint();
                int w = width/5;
                r = new Rect(0,0,w,height);
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(getResources().getColor(R.color.green1));
                canvas.drawRect(r, paint);

                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(getResources().getColor(R.color.green1));
                canvas.drawRect(r, paint);
                //----
                paint = new Paint();
                r = new Rect(w,0,w+width/5,height);
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(getResources().getColor(R.color.green2));
                canvas.drawRect(r, paint);

                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(getResources().getColor(R.color.green2));
                canvas.drawRect(r, paint);
                w += width/5;
                //---
                paint = new Paint();
                r = new Rect(w,0,w+width/5,height);
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(getResources().getColor(R.color.green3));
                canvas.drawRect(r, paint);

                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(getResources().getColor(R.color.green3));
                canvas.drawRect(r, paint);
                w += width/5;
                //---
                paint = new Paint();
                r = new Rect(w,0,w+width/5,height);
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(getResources().getColor(R.color.green4));
                canvas.drawRect(r, paint);

                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(getResources().getColor(R.color.green4));
                canvas.drawRect(r, paint);
                w += width/5;
                //----
                paint = new Paint();
                r = new Rect(w,0,w+width/5,height);
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(getResources().getColor(R.color.green5));
                canvas.drawRect(r, paint);

                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(getResources().getColor(R.color.green5));
                canvas.drawRect(r, paint);
                break;
            default:
                break;
        }
    }
}
