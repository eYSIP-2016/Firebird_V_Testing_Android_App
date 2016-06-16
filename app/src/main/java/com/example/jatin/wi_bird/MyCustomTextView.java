package com.example.jatin.wi_bird;


import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Trapti mittal on 15-Jun-16.
 */

public class MyCustomTextView extends TextView {

    public MyCustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/Classic Robot Condensed.ttf"));
    }
}
