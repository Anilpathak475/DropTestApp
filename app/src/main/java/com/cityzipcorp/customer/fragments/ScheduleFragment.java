package com.cityzipcorp.customer.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.cityzipcorp.customer.R;
import com.cityzipcorp.customer.activities.EditEventActivity;
import com.cityzipcorp.customer.adapter.ScheduleAdapter;
import com.cityzipcorp.customer.base.BaseFragment;
import com.cityzipcorp.customer.callbacks.ScheduleAdapterChildCallback;
import com.cityzipcorp.customer.callbacks.ScheduleCallback;
import com.cityzipcorp.customer.model.Schedule;
import com.cityzipcorp.customer.model.TimeUpdate;
import com.cityzipcorp.customer.store.ScheduleStore;
import com.cityzipcorp.customer.utils.CalenderUtil;
import com.cityzipcorp.customer.utils.Constants;
import com.cityzipcorp.customer.utils.LinearLayoutManagerWithSmoothScroller;
import com.cityzipcorp.customer.utils.NetworkUtils;
import com.cityzipcorp.customer.utils.SharedPreferenceManagerConstant;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by anilpathak on 02/11/17.
 */

public class ScheduleFragment extends BaseFragment implements ScheduleAdapterChildCallback {


    public static boolean isBulkModeActive = false;
    private static String TAG = ScheduleFragment.class.getCanonicalName();
    @BindView(R.id.table_layout)
    TableLayout tableLayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindColor(R.color.disabel_backgroud)
    int disableBackground;
    @BindColor(R.color.disabel_date)
    int disableDateTextColor;
    @BindColor(R.color.edited_date)
    int editedColor;
    @BindColor(R.color.current_date_background)
    int currentDateBackground;
    @BindColor(R.color.cancelled_bg)
    int cancelledBg;
    @BindColor(R.color.green_bg)
    int greenBg;
    @BindColor(R.color.white)
    int white;

    @BindDrawable(R.drawable.small_circle_cancel_date)
    Drawable smallCircleCancelDate;
    @BindDrawable(R.drawable.small_circle_current_date)
    Drawable smallCircleCurrentDate;
    @BindDrawable(R.drawable.small_circle_updated)
    Drawable smallCircleUpdated;
    private LayoutInflater layoutInflater;
    private RelativeLayout previousClickedLayout;
    private Date previousDate = null;
    private Calendar c;
    private Handler handler = new Handler();
    private boolean isCalenderClicked = false;
    private List<Schedule> scheduleList = new ArrayList<>();
    private LinkedHashMap<Date, RelativeLayout> dateMapList = new LinkedHashMap<>();
    private ColorDrawable editedBackgroundColor;
    private ColorDrawable currentBackgroundColor;
    private ColorDrawable cancelledBackgroundColor;
    private HashMap<String, Schedule> bulkDateList = new HashMap<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, null);
        ButterKnife.bind(this, view);
        init();
        getSchedule();
        return view;
    }

    private void init() {
        activity.backAllowed = false;
        c = Calendar.getInstance();
        layoutInflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);

        editedBackgroundColor = new ColorDrawable(editedColor);
        currentBackgroundColor = new ColorDrawable(currentDateBackground);
        cancelledBackgroundColor = new ColorDrawable(cancelledBg);
        recyclerView.addItemDecoration(new RecyclerSectionItemDecoration(33, true, getSectionCallback()));
    }

    private void getSchedule() {
        if (NetworkUtils.isNetworkAvailable(activity)) {
            uiUtils.showProgressDialog();
            ScheduleStore.getInstance(sharedPreferenceUtils.getValue(SharedPreferenceManagerConstant.BASE_URL)).
                    getSchedule(sharedPreferenceUtils.getValue(SharedPreferenceManagerConstant.ACCESS_TOKEN),
                            new ScheduleCallback() {
                                @Override
                                public void onSuccess(List<Schedule> schedules) {
                                    if (schedules.size() > 0) {
                                        scheduleList = schedules;
                                        createCalenderFromList();
                                        setAdapter();
                                        uiUtils.dismissDialog();
                                    } else {
                                        setDefaultSchedule();
                                        uiUtils.dismissDialog();
                                    }

                                }

                                @Override
                                public void onFailure(Error error) {
                                    Log.d("Logger ", error.getLocalizedMessage());
                                    setDefaultSchedule();
                                    uiUtils.dismissDialog();
                                }
                            });
        } else {
            uiUtils.noInternetDialog();
        }
    }

    private void setDefaultSchedule() {
        uiUtils.shortToast("Unable to get Schedule");
        Schedule schedule = new Schedule();
        schedule.setDate(Calendar.getInstance().getTime());
        scheduleList.add(schedule);
        createCalenderFromList();
    }

    private void setAdapter() {
        recyclerView.removeAllViews();
        ScheduleAdapter scheduleAdapter = new ScheduleAdapter(activity, scheduleList, this);
        recyclerView.setLayoutManager(new LinearLayoutManagerWithSmoothScroller(activity));
        recyclerView.setAdapter(scheduleAdapter);
        recyclerView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (isCalenderClicked) {
                    final Runnable r = new Runnable() {
                        public void run() {
                            isCalenderClicked = false;
                        }
                    };
                    handler.postDelayed(r, 2000);
                } else {
                    if (scheduleList.size() != 0) {
                        try {
                            LinearLayoutManager layoutManager = ((LinearLayoutManager)
                                    recyclerView.getLayoutManager());
                            setSelectedDateInTableFromScroll(scheduleList.get(layoutManager.findFirstVisibleItemPosition()).getDate());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

    }

    private void scrollRecyclerViewWithCalenderSelection(int day) {
        int i = 0;
        for (Schedule schedule : scheduleList) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(schedule.getDate());
            if (calendar.get(Calendar.DATE) - day == 0) {
                recyclerView.smoothScrollToPosition(i);
                break;
            }
            i++;
        }
    }

    public void createEventListForAdapter(List<Schedule> scheduleList) {
        this.scheduleList.clear();
        HashMap<Integer, Integer> hashMap = CalenderUtil.getAllDatesFromList(scheduleList);
        for (int i = 0; i < 14; i++) {
            Calendar calendar = new GregorianCalendar();
            calendar.add(Calendar.DATE, i);
            Schedule schedule = new Schedule();
            schedule.setDate(calendar.getTime());
            if (hashMap.containsKey(calendar.get(Calendar.DATE))) {
                this.scheduleList.add(i, scheduleList.get(hashMap.get(calendar.get(Calendar.DATE))));
            } else {
                this.scheduleList.add(i, schedule);
            }
        }

    }

    @Override
    public void inTimeClick(int position) {
        Log.d(TAG, String.valueOf(position));
        TimeUpdate inTime = scheduleList.get(position).getInTimeUpdate();
        Calendar calendar = Calendar.getInstance();
        if (!CalenderUtil.getDifferenceBetweenDates(calendar.getTime(), inTime.getTimestamp())) {
            startEditActivity(position, "InTime");
        } else {
            uiUtils.shortToast("Cooling period started");
        }
    }

    @Override
    public void outTimeClick(int position) {
        TimeUpdate outTime = scheduleList.get(position).getOutTimeUpdate();
        Calendar calendar = Calendar.getInstance();
        if (!CalenderUtil.getDifferenceBetweenDates(calendar.getTime(), outTime.getTimestamp())) {
            startEditActivity(position, "OutTime");
        } else {
            uiUtils.shortToast("Cooling period started");
        }
    }

    @Override
    public void onNoTripClick(int position) {
        startEditActivity(position, "NoTrip");
    }

    private void startEditActivity(int position, String timeValue) {
        if (!NetworkUtils.isNetworkAvailable(activity)) {
            uiUtils.noInternetDialog();
            return;
        }
        Log.d(TAG, String.valueOf(position));
        Schedule schedule = scheduleList.get(position);
        Intent intent = new Intent(activity, EditEventActivity.class);
        intent.putExtra(Constants.EDIT_INTENT_EXTRA_DATA, schedule);
        intent.putExtra(Constants.EDIT_INTENT_EXTRA_TIME, timeValue);
        activity.startActivityForResult(intent, 101);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            getSchedule();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void createCalenderFromList() {
        List<List<Date>> mapList = CalenderUtil.getAllDays(scheduleList.get(0).getDate());
        tableLayout.removeAllViews();
        bulkDateList.clear();
        dateMapList.clear();
        addWeekDaysInTable();
        int position = 0;
        for (final List<Date> dates : mapList) {
            TableRow tableRow = new TableRow(activity);
            for (final Date date : dates) {
                final Calendar dateFromString = Calendar.getInstance();
                dateFromString.setTime(date);
                RelativeLayout relativeLayout = (RelativeLayout) layoutInflater.inflate(R.layout.calender_cell, null);
                final RelativeLayout mainLayout = relativeLayout.findViewById(R.id.main_layout);
                final TextView textDate = mainLayout.findViewById(R.id.txt_date);
                final TextView txtMonthName = mainLayout.findViewById(R.id.txt_month_name);
                final ImageView imgSelector = mainLayout.findViewById(R.id.img_state);
                final View eventView = mainLayout.findViewById(R.id.event_view);

                mainLayout.setVisibility(View.VISIBLE);
                textDate.setText(String.valueOf(dateFromString.get(Calendar.DATE)));
                textDate.setTextSize(14);
                if (position == 0) {
                    txtMonthName.setVisibility(View.VISIBLE);
                    txtMonthName.setText(CalenderUtil.getMonth(dateFromString.getTime()));
                    if (activity != null)
                        activity.setTitle(CalenderUtil.getFullMonthName(dateFromString.getTime()));

                }
                final Schedule schedule = getScheduleFromDate(date);
                if (schedule.getDate() != null) {
                    mainLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if (previousDate != null && !(previousDate == date)) {
                                clearPreviousSelection();
                            }

                            imgSelector.setVisibility(View.VISIBLE);
                            imgSelector.setBackgroundResource(R.drawable.ic_action_circle);
                            textDate.setTextColor(white);
                            previousClickedLayout = mainLayout;
                            textDate.setTextSize(16);
                            previousDate = date;
                            try {
                                isCalenderClicked = true;
                                scrollRecyclerViewWithCalenderSelection(Integer.parseInt(textDate.getText().toString()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    });
                    dateMapList.put(date, mainLayout);
                    addColorsToCalender(schedule, dateFromString.getTime(), mainLayout, textDate, eventView);
                } else {
                    mainLayout.setEnabled(false);
                    mainLayout.setBackground(new ColorDrawable(disableBackground));
                    textDate.setTextColor(disableDateTextColor);
                    textDate.setTextSize(9);
                    eventView.setBackgroundColor(disableBackground);
                }
                position++;
                tableRow.addView(relativeLayout);

            }
            tableLayout.addView(tableRow);
        }
    }

    private void addColorsToCalender(Schedule schedule, Date date, RelativeLayout mainLayout, TextView textDate, View eventView) {

        boolean cancelled = false;
        boolean updated = false;
        int editedFontColor = R.color.edited_date_font;
        int cancelledFontColor = R.color.cancelled_font;

        if (schedule.getInTimeUpdate() != null) {
            if (schedule.getInTimeUpdate().isCancelled()) {
                cancelled = true;
            }
        }
        if (schedule.getOutTimeUpdate() != null) {
            if (schedule.getOutTimeUpdate().isCancelled()) {
                cancelled = true;
            }
        }

        if (schedule.getId() != null) {
            if (!schedule.getId().equalsIgnoreCase("")) {
                if (schedule.getInTimeUpdate() != null) {
                    if (schedule.getInTimeUpdate().isCancelled()) {
                        cancelled = true;
                    } else {
                        if (schedule.getInTimeUpdate().getId() != null) {
                            updated = true;
                        }
                    }
                }
                if (schedule.getOutTimeUpdate() != null) {
                    if (schedule.getOutTimeUpdate().isCancelled()) {
                        cancelled = true;
                    } else {
                        if (schedule.getOutTimeUpdate().getId() != null) {
                            updated = true;
                        }
                    }
                }
            }
        }

        try {
            if (cancelled) {
                mainLayout.setBackground(cancelledBackgroundColor);
                textDate.setTextColor(this.getResources().getColor(cancelledFontColor));
                eventView.setBackground(smallCircleCancelDate);

            }
            if (updated) {
                mainLayout.setBackground(editedBackgroundColor);
                textDate.setTextColor(this.getResources().getColor(editedFontColor));
                eventView.setBackground(smallCircleUpdated);
            }
            if (updated && cancelled) {
                mainLayout.setBackground(editedBackgroundColor);
                textDate.setTextColor(this.getResources().getColor(editedFontColor));
                eventView.setBackground(smallCircleUpdated);
            }

            if (date.getDate() == c.get(Calendar.DATE)) {
                mainLayout.setBackground(currentBackgroundColor);
                textDate.setTextColor(white);
                eventView.setBackground(smallCircleCurrentDate);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void addWeekDaysInTable() {
        TableRow tableRow = new TableRow(activity);
        TextView txtSun = new TextView(activity);
        txtSun.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        txtSun.setText("Sun");
        tableRow.addView(txtSun);
        TextView txtM = new TextView(activity);
        txtM.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        txtM.setText("M");
        tableRow.addView(txtM);
        TextView txtT = new TextView(activity);
        txtT.setText("T");
        txtT.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tableRow.addView(txtT);
        TextView txtW = new TextView(activity);
        txtW.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        txtW.setText("W");
        tableRow.addView(txtW);
        TextView txtTh = new TextView(activity);
        txtTh.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        txtTh.setText("T");
        tableRow.addView(txtTh);
        TextView txtF = new TextView(activity);
        txtF.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        txtF.setText("F");
        tableRow.addView(txtF);
        TextView txtS = new TextView(activity);
        txtS.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        txtS.setText("S");
        tableRow.addView(txtS);
        tableLayout.addView(tableRow);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void clearPreviousSelection() {
        final TextView textView = previousClickedLayout.findViewById(R.id.txt_date);
        final ImageView imgSelector = previousClickedLayout.findViewById(R.id.img_state);
        textView.setTextColor(this.getResources().getColor(R.color.black));
        imgSelector.setVisibility(View.INVISIBLE);
        textView.setTextSize(13);
        previousDate = null;
        previousClickedLayout = null;
    }


    public void startBulkMode() {
        isBulkModeActive = true;
    }


    public void doneBulkDone() {
        isBulkModeActive = false;
        Bundle extras = new Bundle();
        extras.putSerializable("dateMap", bulkDateList);
        Intent intent = new Intent(activity, EditEventActivity.class);
        intent.putExtras(extras);
        activity.startActivityForResult(intent, 101);
    }

    private void setSelectedDateInTableFromScroll(Date dateInTableFromScroll) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateInTableFromScroll);
            final RelativeLayout mainLayout = dateMapList.get(dateInTableFromScroll);
            final TextView textView = mainLayout.findViewById(R.id.txt_date);
            final ImageView imgSelector = mainLayout.findViewById(R.id.img_state);
            if (textView.getText().toString().equalsIgnoreCase(String.valueOf(calendar.get(Calendar.DATE)))) {
                if (previousDate != null && !(previousDate == dateInTableFromScroll)) {
                    clearPreviousSelection();
                }
                imgSelector.setVisibility(View.VISIBLE);
                imgSelector.setBackgroundResource(R.drawable.ic_action_circle);
                textView.setTextColor(activity.getResources().getColor(R.color.white));
                previousClickedLayout = mainLayout;
                textView.setTextSize(15);
                previousDate = dateInTableFromScroll;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private SectionCallback getSectionCallback() {
        return new SectionCallback() {
            @Override
            public boolean isSection(int position) {
                return position == 0 || !(scheduleList.get(position).getDate().getTime() == scheduleList.get(position - 1).getDate().getTime());
            }

            @Override
            public Schedule getSectionData(int position) {
                return scheduleList.get(position);
                /*return CalenderUtil.getDay(eventDate) + ", " + CalenderUtil.getMonthAndDate(eventDate);*/
            }
        };
    }


    private interface SectionCallback {
        boolean isSection(int position);

        Schedule getSectionData(int position);

    }

    private Schedule getScheduleFromDate(Date date) {
        for (Schedule schedule : scheduleList) {
            if (schedule.getDate().getDate() == date.getDate()) {
                return schedule;
            }
        }
        return new Schedule();
    }

    private class RecyclerSectionItemDecoration extends RecyclerView.ItemDecoration {

        private final int headerOffset;
        private final boolean sticky;
        private final SectionCallback sectionCallback;

        private View headerView;
        private TextView header;
        private TextView txtHoliday;

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
                txtHoliday = headerView.findViewById(R.id.txt_holiday);
                txtHoliday.setText("");
                header = headerView.findViewById(R.id.txt_date);
                fixLayoutSize(headerView, parent);
            }
            txtHoliday.setText("");
            CharSequence previousHeader = "";
            for (int i = 0; i < parent.getChildCount(); i++) {
                View child = parent.getChildAt(i);
                final int position = parent.getChildAdapterPosition(child);

                Schedule schedule = sectionCallback.getSectionData(position);
                String title = String.format("%s, %s", CalenderUtil.getDay(schedule.getDate()), CalenderUtil.getMonthAndDate(schedule.getDate()));
                header.setText(title);
                if (schedule.getOffType() != null && !schedule.getOffType().equalsIgnoreCase("")) {
                    txtHoliday.setVisibility(View.VISIBLE);
                    if (!schedule.getOffReason().equalsIgnoreCase("")) {
                        txtHoliday.setText(schedule.getOffReason());
                    } else {
                        txtHoliday.setText(R.string.week_off);
                    }
                } else {
                    txtHoliday.setText("");
                }
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
            return LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_section_header, parent, false);
        }

        private void fixLayoutSize(View view, ViewGroup parent) {
            int widthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(), View.MeasureSpec.EXACTLY);
            int heightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight(), View.MeasureSpec.UNSPECIFIED);

            int childWidth = ViewGroup.getChildMeasureSpec(widthSpec, parent.getPaddingLeft() + parent.getPaddingRight(), view.getLayoutParams().width);
            int childHeight = ViewGroup.getChildMeasureSpec(heightSpec, parent.getPaddingTop() + parent.getPaddingBottom(), view.getLayoutParams().height);

            view.measure(childWidth, childHeight);
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        }
    }
}
