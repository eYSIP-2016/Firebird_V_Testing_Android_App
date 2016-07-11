/*
 *
 * Project Name: Android App development for testing Firebird V Robot
 * Author List: Jatin Mittal
 * Filename: MyCustomTextView.java
 * Functions: MyCustomTextView(Context, AttributeSet)
 * Global Variables: None
 *
 */
package com.example.jatin.wi_bird;


import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/*
 * Class Name: MyCustomTextView
 * Logic: This activity customizes the font style for textview
 * Example Call: new MyCustomTextView()
 *
 */
public class MyCustomTextView extends TextView {

    public MyCustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/Classic Robot Condensed.ttf"));
    }
}
