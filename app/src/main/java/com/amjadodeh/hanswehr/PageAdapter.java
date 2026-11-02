package com.amjadodeh.hanswehr;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;

/** Shows one bundled dictionary page per pager position. */
public class PageAdapter extends PagerAdapter {

    /** Called when the user taps a page once (double taps zoom, as before). */
    public interface OnPageTapListener {
        void onPageTap();
    }

    private final PageIndex index;
    private final OnPageTapListener tapListener;

    public PageAdapter(PageIndex index, OnPageTapListener tapListener) {
        this.index = index;
        this.tapListener = tapListener;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View page = LayoutInflater.from(container.getContext()).inflate(R.layout.page, container, false);
        PhotoView photoView = page.findViewById(R.id.pageImage);
        photoView.setOnViewTapListener((view, x, y) -> tapListener.onPageTap());
        Uri uri = Uri.parse("file:///android_asset/" + PageIndex.assetPathForPosition(position));
        Glide.with(container.getContext())
                .load(uri)
                // Load at native resolution so pinch-zoom stays sharp; the scans are ~1000px wide.
                .override(Target.SIZE_ORIGINAL)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(photoView);
        container.addView(page);
        return page;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        View page = (View) object;
        PhotoView photoView = page.findViewById(R.id.pageImage);
        Glide.with(container.getContext()).clear(photoView);
        container.removeView(page);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public int getCount() {
        return index.getPageCount();
    }
}
