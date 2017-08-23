package lac.com.crimimalintent;

import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;

/**
 * Created by Aicun on 8/17/2017.
 */

public class CrimeListActivity extends SingleFragmentActivity{
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
