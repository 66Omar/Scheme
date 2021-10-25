package com.scheme.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.scheme.R;


public class WeekDaySelector extends FrameLayout {
    private OnDaySelectedListener daySelectedListener = null;

    private GridLayout gridLayout;
    private TextView selectedView;
    private TextView[] allViews;



    public WeekDaySelector(Context context) {
        super(context);
    }

    public WeekDaySelector(Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
        draw();
    }

    public WeekDaySelector(Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        draw();
    }

    private void draw() {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.weekday_view, this, false);
        addView(view);

        gridLayout = view.findViewById(R.id.edit_grid);
        TextView saturday = view.findViewById(R.id.sat);
        TextView sunday = view.findViewById(R.id.sun);
        TextView monday = view.findViewById(R.id.mon);
        TextView tuesday = view.findViewById(R.id.tue);
        TextView wednesday = view.findViewById(R.id.wed);
        TextView thursday = view.findViewById(R.id.thr);
        TextView friday = view.findViewById(R.id.fri);
        allViews = new TextView[]{saturday, sunday, monday, tuesday, thursday, friday, wednesday};
        setOnClicks();

    }


    private void setOnClicks() {
        for (TextView textView : allViews) {
            textView.setOnClickListener(view -> {
                if (selectedView != null) {
                    selectedView.setBackgroundColor(Color.WHITE);
                }
                selectedView = textView;
                selectedView.setBackgroundColor(Color.parseColor("#D3D3D3"));
                if (daySelectedListener != null) {
                    daySelectedListener.daySelected(gridLayout.indexOfChild(selectedView));
                }
            });
        }
    }

    public void setSelectedDay(int day) {
        selectedView = (TextView) gridLayout.getChildAt(day);
        selectedView.setBackgroundColor(Color.parseColor("#D3D3D3"));
    }

    public void setOnDaySelectedListener(OnDaySelectedListener listener) {
        daySelectedListener = listener;
    }
    public interface OnDaySelectedListener {
        void daySelected(int day);
    }

}
