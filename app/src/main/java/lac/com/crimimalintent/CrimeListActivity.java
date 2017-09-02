package lac.com.crimimalintent;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;

/**
 * Created by Aicun on 8/17/2017.
 */

public class CrimeListActivity extends SingleFragmentActivity implements CrimeListFragment.Callbacks, CrimeFragment.Callbacks{
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    protected int getLayoutResId() {
        //return super.getLayoutResId();
        return R.layout.activity_twopane;
        //return R.layout.activity_masterdetail;
    }

    @Override
    public void onCrimeSeleted(Crime crime) {
        if(findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = CrimePagerActivity.newIntent(this,crime.getmId());
            startActivity(intent);
        } else {
            Fragment crimeFragment = CrimeFragment.newInstance(crime.getmId());
            getSupportFragmentManager().beginTransaction().replace(R.id.detail_fragment_container,crimeFragment).commit();
        }
    }

    @Override
    public void onCrimeUpdated(Crime crime) {
        CrimeListFragment crimeListFragment = (CrimeListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        crimeListFragment.updateUI();
    }
}
