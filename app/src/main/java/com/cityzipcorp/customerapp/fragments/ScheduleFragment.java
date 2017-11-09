package com.cityzipcorp.customerapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.cityzipcorp.customerapp.BuildConfig;
import com.cityzipcorp.customerapp.R;
import com.cityzipcorp.customerapp.activities.EditEventActivity;
import com.cityzipcorp.customerapp.adapter.ScheduleAdapter;
import com.cityzipcorp.customerapp.callbacks.ScheduleAdapterChildCallback;
import com.cityzipcorp.customerapp.callbacks.ScheduleCallback;
import com.cityzipcorp.customerapp.model.Schedule;
import com.cityzipcorp.customerapp.store.ScheduleStore;
import com.cityzipcorp.customerapp.utils.CalenderUtil;
import com.cityzipcorp.customerapp.utils.Constants;
import com.cityzipcorp.customerapp.utils.LinearLayoutManagerWithSmoothScroller;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.prolificinteractive.materialcalendarview.format.CalendarWeekDayFormatter;
import com.prolificinteractive.materialcalendarview.format.WeekDayFormatter;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by anilpathak on 02/11/17.
 */

public class ScheduleFragment extends Fragment implements OnDateSelectedListener, OnMonthChangedListener,ScheduleAdapterChildCallback {

    private static String TAG = ScheduleFragment.class.getCanonicalName();

    @BindView(R.id.calendarView)
    MaterialCalendarView widget;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private Handler handler = new Handler();
    private ScheduleAdapter scheduleAdapter;
    private Activity activity;
    private boolean isCalenderClicked = false;
    private SelectedDayDecorator selectedDayDecorator;
    private List<Schedule> scheduleList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, null);
        ButterKnife.bind(this, view);

        initializeCalendarWithDecorators();
        try {
            getSchedule();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return view;
    }

    private void initializeCalendarWithDecorators() {
        selectedDayDecorator = new SelectedDayDecorator(activity);

        widget.setOnDateChangedListener(this);
        widget.setShowOtherDates(MaterialCalendarView.SHOW_OTHER_MONTHS);

        final Calendar instance = Calendar.getInstance();
        widget.setSelectedDate(instance.getTime());

        Calendar instance1 = Calendar.getInstance();
        instance1.set(instance1.get(Calendar.YEAR), Calendar.JANUARY, 1);

        Calendar instance2 = Calendar.getInstance();
        instance2.set(instance2.get(Calendar.YEAR), Calendar.DECEMBER, 31);


        widget.state().edit()
                .setMinimumDate(instance1.getTime())
                .setMaximumDate(instance2.getTime())
                .commit();

        widget.setDynamicHeightEnabled(true);
        widget.setWeekDayFormatter(new WeekDayFormatter() {
            @Override
            public CharSequence format(int dayOfWeek) {
                return null;
            }
        });
        widget.setTopbarVisible(false);
        widget.setShowOtherDates(MaterialCalendarView.SHOW_DECORATED_DISABLED);
        widget.setPagingEnabled(false);
        widget.addDecorators(
                selectedDayDecorator,
                new CurrentDateDecorator(activity)
        );
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle("Schedule");
    }

    private void getSchedule() throws IOException {
        try {
            String authToken = BuildConfig.token;
            ScheduleStore.getInstance().getSchedule(authToken, new ScheduleCallback() {

                @Override
                public void onSuccess(List<Schedule> schedules) {
                    if (!schedules.isEmpty()) {
                        scheduleList = schedules;
                        createEventListForAdapter(scheduleList);
                    }
                }

                @Override
                public void onFailure(Error error) {
                    Log.d("Logger ", error.getLocalizedMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void setAdapter(final List<Schedule> dayEventList) {
        scheduleAdapter = new ScheduleAdapter(activity, dayEventList,this);
        recyclerView.setLayoutManager(new LinearLayoutManagerWithSmoothScroller(activity));
        recyclerView.setAdapter(scheduleAdapter);

//        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(activity,
//                new RecyclerItemClickListener.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(View view, int position) {
//
//                    }
//                }));

        recyclerView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (isCalenderClicked) {
                    final Runnable r = new Runnable() {
                        public void run() {
                            isCalenderClicked = false;
                        }
                    };
                    handler.postDelayed(r, 1000);
                } else {
                    LinearLayoutManager layoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                    int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
                    Log.e("Y: ", firstVisiblePosition + "");
                    Schedule schedule = dayEventList.get(firstVisiblePosition);
                    Date date = schedule.getDate();
                    widget.setSelectedDate(date);
                }
            }
        });
        RecyclerSectionItemDecoration sectionItemDecoration =
                new RecyclerSectionItemDecoration(getResources().getDimensionPixelSize(R.dimen.header_height),
                        true,
                        getSectionCallback(dayEventList));
        recyclerView.addItemDecoration(sectionItemDecoration);
        widget.addDecorator(new CurrentDateDecorator(activity));
    }

    @Override
    public void onResume() {
        super.onResume();
        widget.invalidateDecorators();
    }

    @Override
    public void onPause() {
        super.onPause();
        widget.invalidateDecorators();
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        selectedDayDecorator.setDate(date.getDate());
        isCalenderClicked = true;
        Log.d("selected date ", "" + date.getDay());
        this.widget.invalidateDecorators();
        try {
            scrollRecyclerViewWithCalenderSelection(date.getDay());
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
    }

    private void scrollRecyclerViewWithCalenderSelection(int day) throws ParseException {
        int i = 0;
        for (Schedule schedule : scheduleList) {
            Date date = schedule.getDate();
            int eventDate = date.getDate();
            if (eventDate - day == 0) {
                recyclerView.smoothScrollToPosition(i);
                break;
            }
            i++;
        }
    }

    public void createEventListForAdapter(List<Schedule> dayEventList) {
        List<Schedule> dates = new ArrayList<>();
        List<CalendarDay> calendarDays = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            Calendar calendar = new GregorianCalendar();
            calendar.add(Calendar.DATE, i);
            Date date = calendar.getTime();
            Schedule schedule = new Schedule(date);
            calendarDays.add(CalendarDay.from(date));
            try {
                Schedule scheduleFromList = dayEventList.get(i);
                if (schedule.getDate().getDate() == scheduleFromList.getDate().getDate()) {
                    boolean validInTime = false;
                    boolean validOutTime = false;
                    if (scheduleFromList.getInTimeUpdate() != null) {
                        validInTime = true;
                    }
                    if (scheduleFromList.getOutTimeUpdate() != null) {
                        validOutTime = true;
                    }
                    if (validInTime || validOutTime) {
                        dates.add(i, scheduleFromList);
                    } else {
                        dates.add(i, schedule);
                    }
                } else {
                    dates.add(i, schedule);
                }

            } catch (Exception e) {
                e.printStackTrace();
                dates.add(i, schedule);
            }
        }
        setAdapter(dates);
        widget.addDecorator(new EventDaysDecorator(Color.BLACK, calendarDays));
        widget.addDecorator(new DisabledDaysDecorator(activity, CalenderUtil.getAllDisabeldDay(dates.get(0).getDate())));
        widget.setWeekDayFormatter(new CalendarWeekDayFormatter());
        setDecoratorsToEventDays(dates);
    }


    public void setDecoratorsToEventDays(List<Schedule> dayEventList) {
        List<CalendarDay> cancelledDays = new ArrayList<>();
        List<CalendarDay> updatedDays = new ArrayList<>();
        for (Schedule schedule : dayEventList) {
            try {
                if (schedule.getInTimeUpdate().isCancelled()) {
                    cancelledDays.add(CalendarDay.from(schedule.getDate()));
                } else if (schedule.getOutTimeUpdate().isCancelled()) {
                    cancelledDays.add(CalendarDay.from(schedule.getDate()));
                } else if (schedule.getId() != null) {
                    updatedDays.add(CalendarDay.from(schedule.getDate()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        widget.addDecorators(new CancelDecorator(activity, cancelledDays), new UpdateDecorators(activity, updatedDays));
        widget.addDecorator(new CurrentDateDecorator(activity));

    }

    private SectionCallback getSectionCallback(final List<Schedule> scheduleList) {
        return new SectionCallback() {
            @Override
            public boolean isSection(int position) {
                return position == 0
                        || scheduleList.get(position).getDate().toString()
                        != scheduleList.get(position - 1)
                        .getDate().toString();
            }

            @Override
            public CharSequence getSectionHeader(int position) {
                Date eventDate = scheduleList.get(position).getDate();
                return CalenderUtil.getDay(eventDate) + ", " + CalenderUtil.getMonthAndDate(eventDate);
            }
        };
    }

    @Override
    public void inTimeClick(int position) {
        Log.d(TAG, String.valueOf( position));
        Schedule schedule = scheduleList.get(position);
        Intent intent = new Intent(activity, EditEventActivity.class);
        intent.putExtra(Constants.EDIT_INTENT_EXTRA_DATA, schedule);
        intent.putExtra(Constants.EDIT_INTENT_EXTRA_POSITION, position);
        startActivity(intent);
    }

    @Override
    public void outTimeClick(int position) {
        Log.d(TAG, String.valueOf( position));
        Schedule schedule = scheduleList.get(position);
        Intent intent = new Intent(activity, EditEventActivity.class);
        intent.putExtra(Constants.EDIT_INTENT_EXTRA_DATA, schedule);
        intent.putExtra(Constants.EDIT_INTENT_EXTRA_POSITION, position);
        startActivity(intent);
    }

    private class CancelDecorator implements DayViewDecorator {

        private HashSet<CalendarDay> dates;
        private final Drawable highlightDrawable;
        private Context context;

        public CancelDecorator(Context context, Collection<CalendarDay> dates) {
            this.dates = new HashSet<>(dates);
            this.context = context;
            highlightDrawable = new ColorDrawable(context.getResources().getColor(R.color.cancelled_bg));
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setBackgroundDrawable(highlightDrawable);
            view.addSpan(new DotSpan(5, R.color.cancelled_font));
            view.addSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.cancelled_font)));
        }
    }

    private class CurrentDateDecorator implements DayViewDecorator {

        private Drawable highlightDrawable;
        private Context context;

        private CurrentDateDecorator(Context context) {
            this.context = context;
            highlightDrawable = new ColorDrawable(context.getResources().getColor(R.color.current_date_background));
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return day.equals(CalendarDay.today());
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.current_date_font)));
            view.setBackgroundDrawable(highlightDrawable);
        }
    }

    private class DisabledDaysDecorator implements DayViewDecorator {
        private HashSet<CalendarDay> dates;
        private Context context;
        private Drawable highlightDrawable;

        public DisabledDaysDecorator(Context context, Collection<CalendarDay> dates) {
            this.dates = new HashSet<>(dates);
            this.context = context;
            highlightDrawable = this.context.getResources().getDrawable(R.drawable.rectangle);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setDaysDisabled(true);
            view.setBackgroundDrawable(highlightDrawable);
            view.addSpan(new StyleSpan(Typeface.NORMAL));
            view.addSpan(new RelativeSizeSpan(0.6f));

        }
    }

    private class EventDaysDecorator implements DayViewDecorator {

        private int color;
        private HashSet<CalendarDay> dates;

        private EventDaysDecorator(int color, Collection<CalendarDay> dates) {
            this.color = color;
            this.dates = new HashSet<>(dates);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new DotSpan(5, color));
        }
    }

    private interface SectionCallback {

        boolean isSection(int position);

        CharSequence getSectionHeader(int position);
    }

    private class SelectedDayDecorator implements DayViewDecorator {

        private final Drawable drawable;
        private CalendarDay date;

        private SelectedDayDecorator(Activity context) {
            drawable = context.getResources().getDrawable(R.drawable.my_selector);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return true;
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setSelectionDrawable(drawable);
            view.addSpan(new ForegroundColorSpan(Color.BLACK));
            view.addSpan(new StyleSpan(Typeface.BOLD));
            view.addSpan(new RelativeSizeSpan(1.4f));

        }

        private void setDate(Date date) {
            this.date = CalendarDay.from(date);
        }
    }

    private class UpdateDecorators implements DayViewDecorator {

        private HashSet<CalendarDay> dates;
        private final Drawable highlightDrawable;
        private Context context;

        private UpdateDecorators(Context context, Collection<CalendarDay> dates) {
            this.dates = new HashSet<>(dates);
            this.context = context;
            highlightDrawable = new ColorDrawable(context.getResources().getColor(R.color.edited_date));
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setBackgroundDrawable(highlightDrawable);
            view.addSpan(new DotSpan(5, R.color.edited_date_font));
            view.addSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.edited_date_font)));
        }
    }

    private class RecyclerSectionItemDecoration extends RecyclerView.ItemDecoration {

        private final int headerOffset;
        private final boolean sticky;
        private final SectionCallback sectionCallback;

        private View headerView;
        private TextView header;

        private RecyclerSectionItemDecoration(int headerHeight, boolean sticky, @NonNull SectionCallback sectionCallback) {
            headerOffset = headerHeight;
            this.sticky = sticky;
            this.sectionCallback = sectionCallback;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);

            int pos = parent.getChildAdapterPosition(view);
            if (sectionCallback.isSection(pos)) {
                outRect.top = headerOffset;
            }
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            super.onDrawOver(c, parent, state);

            if (headerView == null) {
                headerView = inflateHeaderView(parent);
                header = (TextView) headerView.findViewById(R.id.txt_date);
                fixLayoutSize(headerView, parent);
            }

            CharSequence previousHeader = "";
            for (int i = 0; i < parent.getChildCount(); i++) {
                View child = parent.getChildAt(i);
                final int position = parent.getChildAdapterPosition(child);

                CharSequence title = sectionCallback.getSectionHeader(position);
                header.setText(title);
                if (!previousHeader.equals(title) || sectionCallback.isSection(position)) {
                    drawHeader(c, child, headerView);
                    previousHeader = title;
                }
            }
        }

        private void drawHeader(Canvas c, View child, View headerView) {
            c.save();
            if (sticky) {
                c.translate(0, Math.max(0, child.getTop() - headerView.getHeight()));
            } else {
                c.translate(0, child.getTop() - headerView.getHeight());
            }
            headerView.draw(c);
            c.restore();
        }

        private View inflateHeaderView(RecyclerView parent) {
            return LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_section_header, parent, false);
        }

        /**
         * Measures the header view to make sure its size is greater than 0 and will be drawn
         * https://yoda.entelect.co.za/view/9627/how-to-android-recyclerview-item-decorations
         */
        private void fixLayoutSize(View view, ViewGroup parent) {
            int widthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(),
                    View.MeasureSpec.EXACTLY);
            int heightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight(),
                    View.MeasureSpec.UNSPECIFIED);

            int childWidth = ViewGroup.getChildMeasureSpec(widthSpec,
                    parent.getPaddingLeft() + parent.getPaddingRight(),
                    view.getLayoutParams().width);
            int childHeight = ViewGroup.getChildMeasureSpec(heightSpec,
                    parent.getPaddingTop() + parent.getPaddingBottom(),
                    view.getLayoutParams().height);

            view.measure(childWidth, childHeight);

            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        }


    }

}

