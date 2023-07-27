package com.app.youtubers.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.youtubers.BuildConfig;
import com.app.youtubers.R;

public class FragmentAbout extends Fragment {

    public FragmentAbout() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        ((TextView) view.findViewById(R.id.tv_version)).setText(String.format(getString(R.string.version), BuildConfig.VERSION_NAME));
        return view;
    }

}
