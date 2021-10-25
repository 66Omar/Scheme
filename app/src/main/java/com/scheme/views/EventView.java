package com.scheme.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.RelativeLayout;

import java.io.Serializable;

public class EventView extends androidx.appcompat.widget.AppCompatTextView implements Serializable {
    private int width;
    private int height;
    private int x;
    private int y;
    private int color;
    private String task;

    public EventView(Context context) {
        super(context);
    }

    public EventView(Context context, int width, int height, int x, int y, int color, String task) {
        super(context);
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.color = color;
        this.task = task;
        draw();
    }
    public void draw() {
        setText(task);
        setBackgroundColor(color);
        setTextColor(Color.WHITE);
        setTextSize(9.0f);
        setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
        params.setMargins(x, y, 0, 0);
        setLayoutParams(params);
    }
}
