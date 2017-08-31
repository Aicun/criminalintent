package lac.com.crimimalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Aicun on 8/20/2017.
 */

public class DatePicker2Fragment extends DialogFragment {

    public static final String RETUEN_DATE = "com.lac.criminalintent.date";

    private static final String ARG_DATE = "date";

    private TextView mTitle;
    private DatePicker mDatePicker;
    private Button mCrimeDateBtn;

    public static DatePicker2Fragment newInstance(Date date) {
        DatePicker2Fragment datePickerFragment = new DatePicker2Fragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_DATE,date);
        datePickerFragment.setArguments(bundle);
        return datePickerFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_dialog_date,null);
        View view = inflater.inflate(R.layout.fragment_dialog_date,null);
        mTitle = (TextView) view.findViewById(R.id.crime_date_title);
        mCrimeDateBtn = (Button) view.findViewById(R.id.crime_date_button);
        mDatePicker = (DatePicker) view.findViewById(R.id.dialog_date_date_picker);
        Date date = (Date) getArguments().getSerializable(ARG_DATE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        mDatePicker.init(year,month,day,null);

        mCrimeDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = mDatePicker.getYear();
                int month = mDatePicker.getMonth();
                int day = mDatePicker.getDayOfMonth();
                Date date = new GregorianCalendar(year,month,day).getTime();
                sendResult(Activity.RESULT_OK,date);
                getActivity().onBackPressed();
            }
        });
        return view;
    }

    private void sendResult(int resultCode, Date date) {
        Intent intent = new Intent();
        intent.putExtra(RETUEN_DATE,date);
        if (getTargetFragment() == null) {
            getActivity().setResult(resultCode,intent);
        }
        else {
            getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,intent);
        }
    }
}
