package lac.com.crimimalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Aicun on 8/21/2017.
 */

public class CrimePhotoActivity extends SingleFragmentActivity {

    private static final String EXTRA_CRIME_PHOTO = "com.lac.criminalintent.crime_photo";

    @Override
    protected Fragment createFragment() {
        Intent intent = getIntent();
        UUID crimeId = (UUID) intent.getSerializableExtra(EXTRA_CRIME_PHOTO);
        return CrimePhotoFragment.getInstance(crimeId);
    }

    public static Intent newIntent(Context context, UUID crimeId) {
        Intent intent = new Intent(context,CrimePhotoActivity.class);
        intent.putExtra(EXTRA_CRIME_PHOTO,crimeId);
        return intent;
    }
}
