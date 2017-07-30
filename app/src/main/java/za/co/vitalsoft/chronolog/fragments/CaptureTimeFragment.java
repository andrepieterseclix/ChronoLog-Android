package za.co.vitalsoft.chronolog.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import za.co.vitalsoft.chronolog.Configuration;
import za.co.vitalsoft.chronolog.R;
import za.co.vitalsoft.chronolog.activities.BaseActivity;
import za.co.vitalsoft.chronolog.dialogs.ChooseDateDialog;
import za.co.vitalsoft.chronolog.models.CapturedTimeItem;
import za.co.vitalsoft.chronolog.services.PathDate;
import za.co.vitalsoft.chronolog.services.timesheet.TimesheetService;
import za.co.vitalsoft.chronolog.services.timesheet.models.GetTimesheetResponse;
import za.co.vitalsoft.chronolog.services.timesheet.models.PostCapturedTimeItem;
import za.co.vitalsoft.chronolog.services.timesheet.models.PostTimesheetResponse;

/**
 * Created by Andre on 2017-07-22.
 */

public class CaptureTimeFragment extends Fragment {

    public static final String INSTANCE_TAG = "INSTANCE_TAG";
    public static final String CAN_RECREATE = "CAN_RECREATE";
    private static final String CHOOSE_DATE_DIALOG_TAG = "CHOOSE_DATE_DIALOG_TAG";
    private static final String FROM_DATE = "FROM_DATE";
    private static final String TO_DATE = "TO_DATE";
    private static final String SELECTED_DATE_STRING = "SELECTED_DATE_STRING";
    private static final String CAPTURED_TIME_ITEMS = "CAPTURED_TIME_ITEMS";

    private Button mChangeDateButton;
    private TextView mTotalHoursText;

    private Date mFromDate;
    private Date mToDate;
    private String mSelectedDateString;
    private CapturedTimeItem[] mCapturedTimeItems;
    private boolean mCanRecreate;

    private BaseActivity mParent;
    private CaptureTimeItemsAdapter mCaptureTimeItemsAdapter;

    private PostTimesheetHandler mPostTimesheetHandler;
    private GetTimesheetHandler mGetTimesheetHandler;

    public CaptureTimeFragment() {
        super();

        Log.d(INSTANCE_TAG, "Constructor:  " + hashCode());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mParent = (BaseActivity) context;
        Log.d(INSTANCE_TAG, "onAttach:  " + hashCode());
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mParent = null;
        Log.d(INSTANCE_TAG, "onDetach:  " + hashCode());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(INSTANCE_TAG, "onCreate:  " + hashCode());

        mCaptureTimeItemsAdapter = new CaptureTimeItemsAdapter(getActivity());
        mPostTimesheetHandler = new PostTimesheetHandler();
        mGetTimesheetHandler = new GetTimesheetHandler();

        // Check if the orientation was changed or if we are coming back from another activity
        if (savedInstanceState != null) {
            mCanRecreate = savedInstanceState.getBoolean(CAN_RECREATE, false);

            if (mCanRecreate) {
                mFromDate = (Date) savedInstanceState.getSerializable(FROM_DATE);
                mToDate = (Date) savedInstanceState.getSerializable(TO_DATE);
                mSelectedDateString = savedInstanceState.getString(SELECTED_DATE_STRING);
                mCapturedTimeItems = (CapturedTimeItem[]) savedInstanceState.getSerializable(CAPTURED_TIME_ITEMS);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.d(INSTANCE_TAG, "onCreateView:  " + hashCode());

        View view = inflater.inflate(R.layout.fragment_capture_time_alias, container, false);
        mChangeDateButton = view.findViewById(R.id.change_date_button);
        mChangeDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseDateDialog chooseDateDialog = new ChooseDateDialog();
                chooseDateDialog.show(getFragmentManager(), CHOOSE_DATE_DIALOG_TAG);
            }
        });

        Button mSaveButton = view.findViewById(R.id.save_button);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPostTimesheetHandler.saveCapturedTime();
            }
        });

        ListView listView = view.findViewById(R.id.listView);
        listView.setAdapter(mCaptureTimeItemsAdapter);

        mTotalHoursText = view.findViewById(R.id.hours_total_text);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mCanRecreate) {
            restoreState();
            return;
        }

        setSelectedDate(new Date());
        Log.d(INSTANCE_TAG, "onResume:  " + hashCode());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        // Happens on rotation and on navigating to another activity
        outState.putSerializable(FROM_DATE, mFromDate);
        outState.putSerializable(TO_DATE, mToDate);
        outState.putString(SELECTED_DATE_STRING, mChangeDateButton.getText().toString());
        outState.putSerializable(CAPTURED_TIME_ITEMS, mCaptureTimeItemsAdapter.getAllItems());

        outState.putBoolean(CAN_RECREATE, true);

        super.onSaveInstanceState(outState);
        Log.d(INSTANCE_TAG, "onSaveInstanceState:  " + hashCode());
    }

    private void restoreState() {
        mCanRecreate = false;

        if (mSelectedDateString != null)
            mChangeDateButton.setText(mSelectedDateString);
        mSelectedDateString = null;

        if (mCapturedTimeItems != null) {
            mCaptureTimeItemsAdapter.clear();
            mCaptureTimeItemsAdapter.addAll(mCapturedTimeItems);
            mCapturedTimeItems = null;
        }

        updateTotalHours();
    }

    public void setSelectedDate(Date value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }

        String stringDate = new SimpleDateFormat(Configuration.LONG_DATE_FORMAT).format(value);
        mChangeDateButton.setText(stringDate);

        boolean sameWeekSelected =
                (mFromDate != null && mToDate != null) &&
                        (value.getTime() >= mFromDate.getTime() && value.getTime() <= mToDate.getTime());

        if (sameWeekSelected && mCaptureTimeItemsAdapter.getCount() > 0) {
            Toast.makeText(getActivity(), R.string.notify_same_week_selected, Toast.LENGTH_SHORT).show();
            return;
        }

        mFromDate = getStartOfWeek(value);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mFromDate);
        calendar.add(Calendar.DATE, 6);
        mToDate = calendar.getTime();

        mGetTimesheetHandler.updateCaptureItems(mFromDate, mToDate);
    }

    private Date getStartOfWeek(Date value) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(value);

        while (Calendar.MONDAY != calendar.get(Calendar.DAY_OF_WEEK)) {
            calendar.add(Calendar.DATE, -1);
        }

        return calendar.getTime();
    }

    private void updateTotalHours() {
        int total = 0;

        for (int i = 0; i < mCaptureTimeItemsAdapter.getCount(); i++) {
            CapturedTimeItem item = mCaptureTimeItemsAdapter.getItem(i);
            total += item.getHoursWorked();
        }

        mTotalHoursText.setText(String.format(getString(R.string.display_hours_total_format), total));
    }

    private class CaptureTimeItemsAdapter extends ArrayAdapter<CapturedTimeItem> implements AdapterView.OnItemSelectedListener {

        CaptureTimeItemsAdapter(@NonNull Context context) {
            super(context, 0);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            CapturedTimeItem item = getItem(position);

            if (convertView == null) {
                LayoutInflater layoutInflater = getActivity().getLayoutInflater();
                convertView = layoutInflater.inflate(R.layout.item_capture_time_alias, parent, false);
            }

            if (item == null) {
                return convertView;
            }

            TextView dateText = convertView.findViewById(R.id.date_text);
            TextView dayText = convertView.findViewById(R.id.day_of_week_text);
            Spinner hoursSpinner = convertView.findViewById(R.id.hours_spinner);
            ImageView holidayImage = convertView.findViewById(R.id.holiday_image);
            ImageView lockedImage = convertView.findViewById(R.id.locked_image);

            Date date = item.getDate();

            if (date != null) {
                dateText.setText(new SimpleDateFormat(Configuration.DATE_FORMAT).format(date));
                dayText.setText(new SimpleDateFormat(Configuration.DAY_OF_WEEK_FORMAT).format(date));
                hoursSpinner.setOnItemSelectedListener(this);
                hoursSpinner.setSelection(item.getHoursWorked());
                // Use prompt to keep the position (is there a better way?)
                hoursSpinner.setPrompt(String.valueOf(position));
                Boolean isEnabled = item.getIsEnabled();
                hoursSpinner.setEnabled(isEnabled != null && isEnabled);

                // Locked image
                Boolean isLocked = item.getIsLocked();
                lockedImage.setVisibility(isLocked != null && isLocked ? View.VISIBLE : View.GONE);

                // Holiday image
                Boolean isHoliday = item.getIsPublicHoliday();
                holidayImage.setVisibility(isHoliday == null || !isHoliday ? View.GONE : View.VISIBLE);
            }

            return convertView;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Spinner hoursSpinner = (Spinner) parent;
            int itemIndex = Integer.valueOf(hoursSpinner.getPrompt().toString());
            CapturedTimeItem item = getItem(itemIndex);
            String selectedValue = (String) hoursSpinner.getSelectedItem();

            if (item != null)
                item.setHoursWorked(Integer.valueOf(selectedValue));

            updateTotalHours();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            updateTotalHours();
        }

        @NonNull
        public CapturedTimeItem[] getAllItems() {
            CapturedTimeItem[] items = new CapturedTimeItem[getCount()];
            for (int i = 0; i < items.length; i++) {
                items[i] = getItem(i);
            }

            return items;
        }
    }

    private class GetTimesheetHandler implements Callback<GetTimesheetResponse> {

        private void updateCaptureItems(Date fromDate, Date toDate) {
            if (mParent == null)
                return;

            mParent.showBusyFragment(null);

            Retrofit retrofit = mParent.buildRetrofit(true);
            TimesheetService timesheetService = retrofit.create(TimesheetService.class);
            Call<GetTimesheetResponse> call = timesheetService.getTimesheet(new PathDate(fromDate), new PathDate(toDate));
            call.enqueue(this);
        }

        @Override
        public void onResponse(@NonNull Call<GetTimesheetResponse> call, @NonNull Response<GetTimesheetResponse> response) {
            mParent.finishBusyFragment();

            GetTimesheetResponse getTimesheetResponse = mParent.interpretServiceResponse(response);

            if (getTimesheetResponse == null) {
                return;
            }

            mCaptureTimeItemsAdapter.clear();
            mCaptureTimeItemsAdapter.addAll(getTimesheetResponse.getCapturedTimeItems());
        }

        @Override
        public void onFailure(@NonNull Call<GetTimesheetResponse> call, @NonNull Throwable t) {
            mParent.finishBusyFragment();
            Log.e("error", getString(R.string.get_logged_time_error), t);
            Toast.makeText(getActivity(), getString(R.string.get_logged_time_error), Toast.LENGTH_LONG).show();
        }
    }

    private class PostTimesheetHandler implements Callback<PostTimesheetResponse> {

        private void saveCapturedTime() {
            if (mParent == null)
                return;

            mParent.showBusyFragment(null);

            Retrofit retrofit = mParent.buildRetrofit(true);
            TimesheetService timesheetService = retrofit.create(TimesheetService.class);
            PostCapturedTimeItem[] items = new PostCapturedTimeItem[mCaptureTimeItemsAdapter.getCount()];

            for (int i = 0; i < mCaptureTimeItemsAdapter.getCount(); i++) {

                CapturedTimeItem item = mCaptureTimeItemsAdapter.getItem(i);
                PostCapturedTimeItem postItem = new PostCapturedTimeItem();

                if (item != null) {
                    postItem.setHoursWorked(item.getHoursWorked());
                    postItem.setDate(item.getDate());
                    postItem.setUserName(item.getUserName());
                }

                items[i] = postItem;
            }

            Call<PostTimesheetResponse> call = timesheetService.postTimesheet(items);
            call.enqueue(this);
        }

        @Override
        public void onResponse(@NonNull Call<PostTimesheetResponse> call, @NonNull Response<PostTimesheetResponse> response) {
            mParent.finishBusyFragment();

            PostTimesheetResponse postTimesheetResponse = mParent.interpretServiceResponse(response);

            if (postTimesheetResponse == null) {
                return;
            }

            Log.i("info", "Saved successfully!");
            Toast.makeText(getActivity(), getString(R.string.info_post_logged_time), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFailure(@NonNull Call<PostTimesheetResponse> call, @NonNull Throwable t) {
            mParent.finishBusyFragment();
            Log.e("error", getString(R.string.post_logged_time_error), t);
            Toast.makeText(getActivity(), getString(R.string.post_logged_time_error), Toast.LENGTH_LONG).show();
        }
    }
}
