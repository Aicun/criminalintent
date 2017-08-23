package lac.com.crimimalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Aicun on 8/21/2017.
 */

public class DatePickerActivity extends SingleFragmentActivity {

    private static final String EXTRA_CRIME_DATE = "com.lac.criminalintent.crime_date";

    @Override
    protected Fragment createFragment() {
        Intent intent = getIntent();
        Date date = (Date) intent.getSerializableExtra(EXTRA_CRIME_DATE);
        return DatePicker2Fragment.newInstance(date);
    }

    public static Intent newIntent(Context context, Date date) {
        Intent intent = new Intent(context,DatePickerActivity.class);
        intent.putExtra(EXTRA_CRIME_DATE,date);
        return intent;
    }
}
