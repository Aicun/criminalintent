package lac.com.crimimalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Aicun on 8/17/2017.
 */

public class CrimeListFragment extends Fragment {

    private static final int REQUEST_CRIME = 1;
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    private RecyclerView myCrimeRecyclerView;
    private CrimeAdapter crimeAdapter;

    private int updatedPosition = 0;
    private boolean mSubtitleVisible;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        myCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
        myCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }
        updateUI();
        return view;
    }

    private void updateUI() {
        CrimeManager cm = CrimeManager.getInstance(getActivity());
        List<Crime> crimes = cm.getmCrimes();

        /*if(crimes.size() != 0) {

            if (crimeAdapter == null) {
                crimeAdapter = new CrimeAdapter(crimes);
                myCrimeRecyclerView.setAdapter(crimeAdapter);
            } else {
                crimeAdapter.notifyItemChanged(updatedPosition);
            }
        }else {
            if(emptyAdapter == null) {
                emptyAdapter = new EmptyAdapter();
            }
            myCrimeRecyclerView.setAdapter(emptyAdapter);
        }*/
        if (crimeAdapter == null) {
            crimeAdapter = new CrimeAdapter(crimes);
            myCrimeRecyclerView.setAdapter(crimeAdapter);
        } else {
            crimeAdapter.setCrimes(crimes);
            //crimeAdapter.notifyItemChanged(updatedPosition);
            crimeAdapter.notifyDataSetChanged();
        }
        updateSubtitle();
    }


    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private CheckBox mSolvedCheckBox;
        private Crime mCrime;

        public CrimeHolder(View itemView) {
            super(itemView);
            mTitleTextView = (TextView) itemView.findViewById(R.id.crime_title_text_view);
            mDateTextView = (TextView) itemView.findViewById(R.id.crime_date_text_view);
            mSolvedCheckBox = (CheckBox) itemView.findViewById(R.id.crime_solved_check_box);
            itemView.setOnClickListener(this);
        }

        public void bindCrime(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(crime.getmTitle());
            mDateTextView.setText(crime.getmDate().toString());
            mSolvedCheckBox.setChecked(crime.ismSolved());
        }

        @Override
        public void onClick(View v) {
            //Toast.makeText(getActivity(),mCrime.getmTitle() + " clicked!",Toast.LENGTH_SHORT).show();
            Intent intent = CrimeActivity.newIntent(getActivity(),mCrime.getmId());
            //startActivity(intent);
            //Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getmId());
            startActivityForResult(intent, REQUEST_CRIME);
            updatedPosition = getAdapterPosition();
        }
    }

    private class EmptyHoder extends RecyclerView.ViewHolder{

        private TextView emptyListTextView;

        public EmptyHoder(View itemView) {
            super(itemView);
            emptyListTextView = (TextView) itemView.findViewById(R.id.list_crime_empty_view);
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter {

        public static final int VIEW_TYPE_ITEM = 1;
        public static final int VIEW_TYPE_EMPTY = 0;

        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            if(viewType == VIEW_TYPE_EMPTY) {
                View view = layoutInflater.inflate(R.layout.list_empty_crime,parent,false);
                return new EmptyHoder(view);
            }
            View view = layoutInflater.inflate(R.layout.list_item_crime, parent, false);
            return new CrimeHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if(holder instanceof CrimeHolder) {
                Crime crime = mCrimes.get(position);
                CrimeHolder crimeHolder = (CrimeHolder) holder;
                crimeHolder.bindCrime(crime);
            }
        }

        @Override
        public int getItemCount() {
            if(mCrimes.size() == 0) return 1;
            return mCrimes.size();
        }

        @Override
        public int getItemViewType(int position) {
            if(mCrimes.size() == 0) return VIEW_TYPE_EMPTY;
            return VIEW_TYPE_ITEM;
        }

        public void setCrimes(List<Crime> crimes) {
            this.mCrimes = crimes;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CRIME) {
            Toast.makeText(getActivity(), "Data changed!!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);

        MenuItem menuItem = menu.findItem(R.id.menu_item_show_subtitle);
        if (mSubtitleVisible) {
            menuItem.setTitle(R.string.hide_subtitle);
        } else {
            menuItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_crime:
                Crime crime = new Crime();
                CrimeManager.getInstance(getActivity()).addCrime(crime);
                Intent intent = CrimeActivity.newIntent(getActivity(), crime.getmId());
                startActivity(intent);
                return true;
            case R.id.menu_item_show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void updateSubtitle() {
        CrimeManager crimeManager = CrimeManager.getInstance(getActivity());
        int size = crimeManager.getmCrimes().size();
        String subtitle =getResources().getQuantityString(R.plurals.subtitle_plural,size,size);
        if (!mSubtitleVisible) subtitle = null;
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        appCompatActivity.getSupportActionBar().setSubtitle(subtitle);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE,mSubtitleVisible);
    }
}
