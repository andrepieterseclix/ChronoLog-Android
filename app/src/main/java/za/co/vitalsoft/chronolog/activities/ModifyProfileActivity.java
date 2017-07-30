package za.co.vitalsoft.chronolog.activities;

import android.app.FragmentManager;
import android.os.Bundle;

import za.co.vitalsoft.chronolog.R;
import za.co.vitalsoft.chronolog.fragments.ModifyProfileFragment;

public class ModifyProfileActivity extends BaseActivity {

    private static final String MODIFY_PROFILE_FRAGMENT_TAG = "MODIFY_PROFILE_FRAGMENT_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_profile);

        // When changing orientation, this gets called, adding a second fragment if only using add...
        FragmentManager fragmentManager = getFragmentManager();

        if (fragmentManager.findFragmentByTag(MODIFY_PROFILE_FRAGMENT_TAG) == null) {
            fragmentManager
                    .beginTransaction()
                    .add(R.id.fragment_holder, new ModifyProfileFragment(), MODIFY_PROFILE_FRAGMENT_TAG)
                    .commit();
        }
    }
}
