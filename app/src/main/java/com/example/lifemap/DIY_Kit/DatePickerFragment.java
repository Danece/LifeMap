package com.example.lifemap.DIY_Kit;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.lifemap.R;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private String TAG = DatePickerFragment.class.getCanonicalName();
    private String resule_date = "";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Log.e(TAG, "year:"+year+";monthOfYear:"+(monthOfYear+1)+";dayOfMonth:"+dayOfMonth);
        getDateFormat(year, monthOfYear, dayOfMonth);
    }

    private void getDateFormat(int year,int monthOfYear,int dayOfMonth){
        String result =  String.valueOf(year) + "-"
                + String.valueOf(monthOfYear + 1) + "-"
                + String.valueOf(dayOfMonth);

        TextView date = (TextView) getActivity().findViewById(R.id.tvDate_csv);
        date.setText(result);
    }


}
