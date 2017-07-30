package za.co.vitalsoft.chronolog.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import za.co.vitalsoft.chronolog.R;

/**
 * Created by Andre on 2017-07-22.
 */

public class BusyFragment extends Fragment {

    public static final String BUSY_TEXT_FIELD = "BUSY_TEXT";

    private String mText;

    public static BusyFragment create(@Nullable String message) {
        BusyFragment busyFragment = new BusyFragment();
        Bundle bundle = new Bundle(1);

        if (message != null)
            bundle.putString(BusyFragment.BUSY_TEXT_FIELD, message);

        busyFragment.setArguments(bundle);

        return busyFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mText = getArguments().getString(BUSY_TEXT_FIELD, getString(R.string.status_please_wait));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.fragment_busy, container, false);

        TextView textView = view.findViewById(R.id.busy_text_view);
        textView.setText(mText);

        return view;
    }
}
