package lac.com.crimimalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity {

    private static final String EXTRA_CRIME_ID = "com.lac.criminalintent.crime_id";

    private ViewPager viewPager;
    private List<Crime> mCrimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        FragmentManager fragmentManager = getSupportFragmentManager();
        mCrimes = CrimeManager.getInstance(this).getmCrimes();
        viewPager = (ViewPager) findViewById(R.id.activity_crime_pager_view_pager);
        viewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Crime crime = mCrimes.get(position);
                CrimeFragment crimeFragment = CrimeFragment.newInstance(crime.getmId());
                return crimeFragment;
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });

        Intent intent = getIntent();
        UUID crimeId = (UUID) intent.getSerializableExtra(EXTRA_CRIME_ID);
        for(int i=0;i<mCrimes.size();i++) {
            if(mCrimes.get(i).getmId().equals(crimeId)) {
                viewPager.setCurrentItem(i);
                break;
            }
        }
    }

    public static Intent newIntent(Context context, UUID crimeId) {
        Intent intent = new Intent(context,CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID,crimeId);
        return intent;
    }
}
