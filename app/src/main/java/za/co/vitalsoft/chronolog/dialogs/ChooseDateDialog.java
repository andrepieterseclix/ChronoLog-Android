package za.co.vitalsoft.chronolog.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CalendarView;

import java.util.Calendar;
import java.util.GregorianCalendar;

import za.co.vitalsoft.chronolog.R;
import za.co.vitalsoft.chronolog.interfaces.ChooseDateDialogListener;

/**
 * Created by Andre on 2017-07-24.
 */

public class ChooseDateDialog extends DialogFragment implements CalendarView.OnDateChangeListener {

    private Calendar mCalendar;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_choose_date, null);
        CalendarView mCalendarView = view.findViewById(R.id.calendarView);
        mCalendarView.setOnDateChangeListener(this);
        mCalendar = GregorianCalendar.getInstance();
        mCalendar.setTimeInMillis(mCalendarView.getDate());

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setPositiveButton(R.string.action_ok_dialog, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ChooseDateDialogListener listener = (ChooseDateDialogListener) ChooseDateDialog.this.getActivity();
                        if (listener != null) {
                            listener.setSelectedDate(mCalendar.getTime());
                        }
                    }
                })
                .setNegativeButton(R.string.action_cancel_dialog, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .create();
    }

    @Override
    public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
        mCalendar.set(year, month, dayOfMonth);
    }
}
