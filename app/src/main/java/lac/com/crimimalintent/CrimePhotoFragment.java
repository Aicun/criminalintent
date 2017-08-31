package lac.com.crimimalintent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.UUID;

import lac.com.crimimalintent.utils.PictureUtils;

/**
 * Created by Aicun on 8/31/2017.
 */

public class CrimePhotoFragment extends DialogFragment {

    private static final String ARG_CRIME_ID = "crimeId";

    private TextView crimePhotoTitle;
    private ImageView crimePhotoImage;
    private Button crimeButton;

    public static CrimePhotoFragment getInstance(UUID crimeId) {
        CrimePhotoFragment fragment = new CrimePhotoFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_CRIME_ID,crimeId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_crime_photo,null);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);

        Crime crime = CrimeManager.getInstance(getActivity()).getCrime(crimeId);

        crimePhotoTitle = (TextView) view.findViewById(R.id.crime_photo_title);
        crimePhotoTitle.setText(crime.getmTitle());

        crimePhotoImage = (ImageView) view.findViewById(R.id.crime_photo_image_view);
        String photoPath = CrimeManager.getInstance(getActivity()).getCrimePhoto(crime).getPath();
        Bitmap crimePhoto = PictureUtils.getOriginBitmap(photoPath);
        crimePhotoImage.setImageBitmap(crimePhoto);

        crimeButton = (Button) view.findViewById(R.id.crime_photo_button);
        crimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        return view;
    }
}
