package com.ashabanebert.mukemergency.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ashabanebert.mukemergency.R;
import com.google.android.material.snackbar.Snackbar;

public class MediaFragment extends Fragment {

    RelativeLayout relativeLayout;

    public static MediaFragment newInstance() {
        Bundle args = new Bundle();
        MediaFragment mediaFragment = new MediaFragment();
        //mapFragment.setArguments(args);
        return mediaFragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.media_fragment, container, false);
        relativeLayout = getActivity().findViewById(R.id.liveRel);
        Snackbar snackbar = Snackbar
                .make(relativeLayout, "There are no pictures and videos yet", Snackbar.LENGTH_LONG);

        snackbar.show();

        return root;

    }
}
