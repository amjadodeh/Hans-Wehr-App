package com.amjadodeh.hanswehr;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

/** The standard "zoom out" page-turn animation. */
public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
    private static final float MIN_SCALE = 0.85f;
    private static final float MIN_ALPHA = 0.5f;

    @Override
    public void transformPage(@NonNull View view, float position) {
        int pageWidth = view.getWidth();
        int pageHeight = view.getHeight();

        if (position < -1 || position > 1) {
            // Off-screen. Leave the view in its default state rather than hiding it: a page that is
            // jumped to without animation (search results) is never transformed again, and would
            // otherwise stay invisible until the user scrolls.
            view.setAlpha(1f);
            view.setScaleX(1f);
            view.setScaleY(1f);
            view.setTranslationX(0f);
            return;
        }
        float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
        float vertMargin = pageHeight * (1 - scaleFactor) / 2;
        float horzMargin = pageWidth * (1 - scaleFactor) / 2;
        view.setTranslationX(position < 0 ? horzMargin - vertMargin / 2 : -horzMargin + vertMargin / 2);
        view.setScaleX(scaleFactor);
        view.setScaleY(scaleFactor);
        view.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));
    }
}
