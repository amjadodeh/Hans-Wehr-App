package com.amjadodeh.hanswehr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class DrawerRowAdapter extends ArrayAdapter<String> {

    private final String[] titles;
    private final int[] icons;

    public DrawerRowAdapter(Context context, String[] titles, int[] icons) {
        super(context, R.layout.drawer_item_row, titles);
        this.titles = titles;
        this.icons = icons;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View row = convertView != null ? convertView
                : LayoutInflater.from(getContext()).inflate(R.layout.drawer_item_row, parent, false);
        ((TextView) row.findViewById(R.id.rowText)).setText(titles[position]);
        ((ImageView) row.findViewById(R.id.rowIcon)).setImageResource(icons[position]);
        return row;
    }
}
