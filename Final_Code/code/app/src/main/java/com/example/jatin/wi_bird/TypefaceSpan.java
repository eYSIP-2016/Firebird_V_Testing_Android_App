/*
 *
 * Project Name: Android App development for testing Firebird V Robot
 * Author List: Jatin Mittal
 * Filename: TypeSpan.java
 * Functions: TypefaceSpan(Context, String), updateMeasureState(TextPaint), updateDrawState(TextPaint)
 * Global Variables: sTypefaceCache, mTypeface
 *
 */
package com.example.jatin.wi_bird;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.util.LruCache;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;
/*
 *
 * Class Name: TypefaceSpan
 * Logic: This class customizes the font
 * Example Call: new TypefaceSpan(this, "Classic Robot Condensed.ttf")
 *
 */
public class TypefaceSpan extends MetricAffectingSpan {
    // An <code>LruCache</code> for previously loaded typefaces.
    private static LruCache<String, Typeface> sTypefaceCache =
            new LruCache<String, Typeface>(12);
    // stores the typeface name passed in constructor calling
    private Typeface mTypeface;

    /**
     * Function Name: TypefaceSpan
     * Input: context, typefaceName
     * Output: customizes the font
     * Logic: calls parent class method and pass the font style
     * Example Call: Called automatically when the the object of this class is created
     */
    public TypefaceSpan(Context context, String typefaceName) {
        mTypeface = sTypefaceCache.get(typefaceName);
        if (mTypeface == null) {
            mTypeface = Typeface.createFromAsset(context.getApplicationContext()
                    .getAssets(), String.format("fonts/%s", typefaceName));
            // Cache the loaded Typeface
            sTypefaceCache.put(typefaceName, mTypeface);
        }
    }

    @Override
    public void updateMeasureState(TextPaint p) {
        p.setTypeface(mTypeface);
        //This flag is required for proper typeface rendering
        p.setFlags(p.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }

    @Override
    public void updateDrawState(TextPaint tp) {
        tp.setTypeface(mTypeface);
        // Note: This flag is required for proper typeface rendering
        tp.setFlags(tp.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }

}

