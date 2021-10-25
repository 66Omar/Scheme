package com.scheme.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.scheme.R;
import com.scheme.models.DayEvent;
import com.scheme.utilities.EventTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class TimetableView extends RelativeLayout {
    private final Context context;
    int HEIGHT_PER_MINUTE = 3;
    int lineSize;
    int hourHeight;
    int headerHeight;
    ArrayList<Integer> times;
    private OnEventSelectedListener eventSelectedListener = null;

    private GridLayout gridLayout;
    private RelativeLayout relativeLayout;
    private TextView saturday;
    private TextView sunday;
    private TextView monday;
    private TextView tuesday;
    private TextView wednesday;
    private TextView thursday;
    private TextView friday;



    public TimetableView(Context context) {
        super(context);
        this.context = context;
    }
    public TimetableView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        draw();

    }

    private void draw() {

        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.timetable_view, this, false);
        addView(view);
        times = new ArrayList<>();

        final float scale = context.getResources().getDisplayMetrics().density;
        hourHeight = (int) (70 * scale + 0.5f);
        lineSize = (int) (1 * scale + 0.5f) * 2;
        headerHeight = (int) (45 * scale + 0.5f);

        gridLayout = view.findViewById(R.id.griddy);
        relativeLayout = view.findViewById(R.id.rel);
        saturday = gridLayout.findViewById(R.id.saturday);
        sunday = gridLayout.findViewById(R.id.sunday);
        monday = gridLayout.findViewById(R.id.monday);
        tuesday = gridLayout.findViewById(R.id.tuesday);
        wednesday = gridLayout.findViewById(R.id.wednesday);
        thursday = gridLayout.findViewById(R.id.thursday);
        friday = gridLayout.findViewById(R.id.friday);
    }

    public void addTime(List<DayEvent> events, Activity activity){
        int i = 1;
        int[] values = findValues(events);
        for (int s = values[0]; s <= values[1] && s <= 24; s++) {
            int start = 0;
            TextView tv = new TextView(context);
            tv.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
            String se = String.valueOf(s).concat(":00");
            tv.setText(se);
            tv.setTextSize(9.0f);
            tv.setTypeface(null, Typeface.BOLD);
            tv.setWidth(0);
            tv.setHeight(hourHeight);
            times.add(s);
            tv.setBackgroundColor(Color.WHITE);
            GridLayout.Spec rowSpec = GridLayout.spec(i, 0.0f);
            GridLayout.Spec columnSpec = GridLayout.spec(start, 1.0f);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowSpec, columnSpec);
            tv.setLayoutParams(params);
            gridLayout.addView(tv);
            i++;
        }
        addHorizontal();
        addVertical(activity);
        Collections.sort(times);
        HighlightToday();
    }
    public void addVertical(Activity activity) {
        int temp;
        int width = WidthPerCell(activity);
        for (int i=1; i < 8; i++) {
            temp = i * width;
            View view = new View(context);
            view.setBackgroundColor(Color.WHITE);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(lineSize, (times.size() * hourHeight) + headerHeight);
            params.setMarginStart(temp);
            view.setLayoutParams(params);
            relativeLayout.addView(view);
        }
    }
    public void addHorizontal() {
        int length = times.size();
        View viewFixed = new View(context);
        viewFixed.setBackgroundColor(Color.WHITE);
        RelativeLayout.LayoutParams paramsFixed = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, lineSize);
        paramsFixed.setMargins(0, headerHeight, 0, 0);
        viewFixed.setLayoutParams(paramsFixed);
        relativeLayout.addView(viewFixed);
        for(int i=1; i < length; i++) {
            View view = new View(context);
            view.setBackgroundColor(Color.WHITE);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, lineSize);
            params.setMargins(0, headerHeight + (hourHeight * i), 0, 0);
            view.setLayoutParams(params);
            relativeLayout.addView(view);
        }
    }

    private int WidthPerCell(Activity activity){
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return (size.x-getPaddingLeft() - getPaddingRight()) / (8);
    }

    public void showEvents(List<DayEvent> events, Activity activity) {
        clearAll();
        addTime(events, activity);
        int width = WidthPerCell(activity);
        for (final DayEvent event : events) {
            final EventView eventView = new EventView(context, width - (lineSize), eventLength(event.getBeginning(), event.getEnding()), (width * (event.getDay() + 1)) + (lineSize), getY(event.getBeginning()), event.getColor(), event.getTask());
            relativeLayout.addView(eventView);
            eventView.setOnClickListener(v -> {
                if (eventSelectedListener != null) { eventSelectedListener.eventSelected(event); }
            });
        }
    }


    public int eventLength(EventTime start, EventTime end) {

        int startHour = start.getHour();
        int startMin = start.getMinute();
        int endHour = end.getHour();
        int endMin = end.getMinute();

        int hours = endHour - startHour;
        int mins = endMin - startMin;
        int minsLen = (HEIGHT_PER_MINUTE) * mins;
        int eventLength = (hours * hourHeight) + minsLen; //in case of 6:30 start, 7:00 end (for eg) minslen will be negative to balance event length
        if (hours > 0) { eventLength -= lineSize; }
        return eventLength;
    }

    public int[] findValues (List<DayEvent> events) {
        int smallest = 0;
        int highest = 0;
        for (int i = 0; i < events.size(); i++) {
            int start = events.get(i).getBeginning().getHour();
            int end = events.get(i).getEnding().getHour();

            if (smallest != 0 || highest != 0) {
                if (start < smallest) smallest = start;
                if (start > highest) highest = start;
                if (end < smallest) smallest = end;
                if (end > highest) highest = end;
            } else {
                smallest = start;
                highest = end;
            }

        }
        return new int[]{smallest, highest};
    }

    public int getY(EventTime startTime) {
        int hour = startTime.getHour();
        int mins = startTime.getMinute();

        int minsLen = HEIGHT_PER_MINUTE * mins;
        return headerHeight + (hourHeight * (times.indexOf((hour)))) + minsLen + lineSize;
    }

    public void HighlightToday() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int color = getResources().getColor(R.color.colorPrimary);
        if (times.contains(hour)) {
            TextView currentHour = (TextView) gridLayout.getChildAt(times.indexOf(hour) + 8);
            currentHour.setTextColor(color);
        }
        switch (day) {
            case Calendar.SUNDAY:
                sunday.setTextColor(color);
                break;
            case Calendar.MONDAY:
                monday.setTextColor(color);
                break;
            case Calendar.TUESDAY:
                tuesday.setTextColor(color);
                break;
            case Calendar.WEDNESDAY:
                wednesday.setTextColor(color);
                break;
            case Calendar.THURSDAY:
                thursday.setTextColor(color);
                break;
            case Calendar.SATURDAY:
                saturday.setTextColor(color);
                break;
            case Calendar.FRIDAY:
                friday.setTextColor(color);
                break;
        }
    }

    public void setOnEventSelected(OnEventSelectedListener listener) {
        eventSelectedListener = listener;
    }
    public interface OnEventSelectedListener {
        void eventSelected(DayEvent event);
    }

    @Override
    public void invalidate() {
        super.invalidate();
    }
    public void clearAll() {
        int childCount = gridLayout.getChildCount();

        for(int i =9; i < childCount; i++) {
            View currentChild = gridLayout.getChildAt(i);

            gridLayout.removeView(currentChild);


        }
        relativeLayout.removeAllViews();
        times.clear();
    }
}
