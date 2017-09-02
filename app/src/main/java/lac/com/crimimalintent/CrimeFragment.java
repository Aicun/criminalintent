package lac.com.crimimalintent;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.UUID;

import lac.com.crimimalintent.utils.PictureUtils;

/**
 * Created by Aicun on 8/17/2017.
 */

public class CrimeFragment extends Fragment {

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "dialog_date";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_IMAGE = 3;

    private Crime mCrime;
    private File mCrimePhoto;

    private Callbacks mCallback;

    private EditText mCrimeTitle;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mCallSuspectButton;
    private ImageView mCrimePhotoView;
    private ImageButton mCrimePhotoButton;

    private String phoneNo;

    public interface Callbacks {
        void onCrimeUpdated(Crime crime);
    }

    public CrimeFragment() {
    }

    public static CrimeFragment newInstance(UUID crimeId) {
        CrimeFragment crimeFragment = new CrimeFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_CRIME_ID,crimeId);
        crimeFragment.setArguments(bundle);
        return crimeFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (Callbacks) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mCrime = new Crime();
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeManager.getInstance(getActivity()).getCrime(crimeId);
        mCrimePhoto = CrimeManager.getInstance(getActivity()).getCrimePhoto(mCrime);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime,container,false);

        mCrimeTitle = (EditText) view.findViewById(R.id.crime_title);
        mCrimeTitle.setText(mCrime.getmTitle());
        mCrimeTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setmTitle(s.toString());
                updateCrime();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDateButton = (Button)view.findViewById(R.id.crime_date);
        String dateString = DateFormat.format("EEE, MMM dd, yyyy",mCrime.getmDate()).toString();
        mDateButton.setText(dateString);
        //mDateButton.setEnabled(false);
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*FragmentManager fragmentManager = getFragmentManager();
                DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(mCrime.getmDate());
                datePickerFragment.setTargetFragment(CrimeFragment.this,REQUEST_DATE);
                datePickerFragment.show(fragmentManager,DIALOG_DATE);*/

                Intent intent = DatePickerActivity.newIntent(getActivity(),mCrime.getmDate());
                startActivityForResult(intent,REQUEST_DATE);
            }
        });

        mSolvedCheckBox = (CheckBox) view.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.ismSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setmSolved(isChecked);
                updateCrime();
            }
        });

        mReportButton = (Button) view.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,getCrimeReport());
                intent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.crime_report_subject));
                intent.createChooser(intent,getString(R.string.send_report));*/

                Intent intent = ShareCompat.IntentBuilder.from(getActivity()).getIntent()
                        .setType("text/plain")
                        .putExtra(Intent.EXTRA_TEXT,getCrimeReport())
                        .putExtra(Intent.EXTRA_SUBJECT,getString(R.string.crime_report_subject));
                //activity chooser, if there are more than one activity, a list will be shown
                intent.createChooser(intent,getString(R.string.send_report));

                startActivity(intent);
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        //no actual meaning, just disable contact app to be matched
        //pickContact.addCategory(Intent.CATEGORY_HOME);
        mSuspectButton = (Button) view.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact,REQUEST_CONTACT);
            }
        });

        if(mCrime.getmSuspect() != null) {
            mSuspectButton.setText(mCrime.getmSuspect());
        }
        //if cannot find default app
        PackageManager manager = getActivity().getPackageManager();
        if(manager.resolveActivity(pickContact,PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }

        mCallSuspectButton = (Button) view.findViewById(R.id.call_suspect);
        mCallSuspectButton.setEnabled(false);
        mCallSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri number = Uri.parse(phoneNo);
                Intent intent = new Intent(Intent.ACTION_DIAL,number);
                startActivity(intent);
            }
        });

        mCrimePhotoView = (ImageView) view.findViewById(R.id.crime_photo);

        //if(mCrimePhotoView.getDrawable() != null) {
            mCrimePhotoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent crimePhotoIntent = CrimePhotoActivity.newIntent(getActivity(),mCrime.getmId());
                    startActivity(crimePhotoIntent);
                }
            });
       // }

        mCrimePhotoButton = (ImageButton) view.findViewById(R.id.crime_camera);

        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePicture = (mCrimePhoto != null) && (captureImage.resolveActivity(manager) != null);
        mCrimePhotoButton.setEnabled(canTakePicture);

        if(canTakePicture) {
            Uri uri = Uri.fromFile(mCrimePhoto);
            //store picture in extra file store
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT,uri);
        }
        mCrimePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureImage,REQUEST_IMAGE);
            }
        });



        ViewTreeObserver observer = mCrimePhotoView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mCrimePhotoView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width = mCrimePhotoView.getWidth();
                int height = mCrimePhotoView.getHeight();
                updateCrimePhotoView(width,height);
            }
        });

        returnResult();
        return view;
    }


    private void returnResult() {
        getActivity().setResult(Activity.RESULT_OK,null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK) return;
        if(requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.RETUEN_DATE);
            mCrime.setmDate(date);
            mDateButton.setText(date.toString());
            updateCrime();
        }
        if(requestCode == REQUEST_CONTACT && data != null) {
            Uri uri = data.getData();
            String[] queryFiled = new String[] {ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts._ID};
            Cursor cursor = getActivity().getContentResolver().query(uri,queryFiled,null,null,null);

            if(cursor.getCount() == 0) return;
            cursor.moveToFirst();
            String suspect = cursor.getString(0);
            String contactID = cursor.getString(1);

            Cursor queryPhoneNo = getActivity().getContentResolver().query(CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    CommonDataKinds.Phone.CONTACT_ID +" = ?",
                    new String[]{contactID},
                    null );
            queryPhoneNo.moveToFirst();
            phoneNo = queryPhoneNo.getString(queryPhoneNo.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phoneNo = String.format("tel:%s",phoneNo);
            mCallSuspectButton.setEnabled(true);

            mCrime.setmSuspect(suspect);
            mSuspectButton.setText(suspect);
            updateCrime();
        }

        if(requestCode == REQUEST_IMAGE) {
            updateCrimePhotoView(mCrimePhotoView.getWidth(),mCrimePhotoView.getHeight());
            updateCrime();
        }
    }

    private void updateCrime() {
        CrimeManager.getInstance(getActivity()).updateCrime(mCrime);
        mCallback.onCrimeUpdated(mCrime);
    }

    private void updateCrimePhotoView(int width, int height) {
        if(mCrimePhoto == null || !mCrimePhoto.exists()) {
            mCrimePhotoView.setImageDrawable(null);
        } else {
            //Bitmap bitmap = PictureUtils.getScaledBitmap(mCrimePhoto.getPath(),getActivity());
            Bitmap bitmap = PictureUtils.getScaledBitmap(mCrimePhoto.getPath(),width,height);
            //Bitmap bitmap = PictureUtils.getOriginBitmap(mCrimePhoto.getPath());
            mCrimePhotoView.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_detail,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete_crime:
                CrimeManager crimeManager = CrimeManager.getInstance(getActivity());
                crimeManager.deleteCrime(mCrime);
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeManager.getInstance(getActivity()).updateCrime(mCrime);
    }

    private String getCrimeReport() {
        String solvedString = null;
        if(mCrime.ismSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormate = "EEE, MMM dd yyyy";
        String crimeDate = DateFormat.format(dateFormate,mCrime.getmDate()).toString();

        String suspect = mCrime.getmSuspect();
        if(suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect,suspect);
        }

        String crimeReposrt = getString(R.string.crime_report,mCrime.getmTitle(),crimeDate,solvedString,suspect);
        return crimeReposrt;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }
}
