package za.co.vitalsoft.chronolog.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import za.co.vitalsoft.chronolog.Identity;
import za.co.vitalsoft.chronolog.R;
import za.co.vitalsoft.chronolog.fragments.CaptureTimeFragment;
import za.co.vitalsoft.chronolog.interfaces.ChooseDateDialogListener;
import za.co.vitalsoft.chronolog.services.access.AccessService;
import za.co.vitalsoft.chronolog.services.access.models.LogoutResponse;

public class MainActivity extends BaseActivity implements Callback<LogoutResponse>, ChooseDateDialogListener {

    private static final String CAPTURE_FRAGMENT_TAG = "CAPTURE_FRAGMENT_TAG";

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putBoolean(CaptureTimeFragment.CAN_RECREATE, true);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // If the orientation was changed, or coming back from another activity, let android recreate the fragment
        if (savedInstanceState != null) {
            boolean canRecreate = savedInstanceState.getBoolean(CaptureTimeFragment.CAN_RECREATE, false);

            if (canRecreate)
                return;
        }

        CaptureTimeFragment captureTimeFragment = new CaptureTimeFragment();
        Log.d(CaptureTimeFragment.INSTANCE_TAG, "Constructed from Activity:  " + captureTimeFragment.hashCode());

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_holder, captureTimeFragment, CAPTURE_FRAGMENT_TAG)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.log_out_item:
                logOut();
                break;
            case R.id.user_profile_item:
                Intent userProfileIntent = new Intent(this, ModifyProfileActivity.class);
                startActivity(userProfileIntent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResponse(@NonNull Call<LogoutResponse> call, @NonNull Response<LogoutResponse> response) {
        LogoutResponse logoutResponse = interpretServiceResponse(response);

        if (logoutResponse == null) {
            finishBusyFragment();
            return;
        }

        // Quit if logged out successfully
        System.exit(0);
    }

    @Override
    public void onFailure(@NonNull Call<LogoutResponse> call, @NonNull Throwable t) {
        finishBusyFragment();

        Log.e("error", "Could not log out.", t);
        System.exit(1);
    }

    @Override
    public void setSelectedDate(Date value) {
        CaptureTimeFragment captureTimeFragment = (CaptureTimeFragment) getFragmentManager().findFragmentByTag(CAPTURE_FRAGMENT_TAG);

        if (captureTimeFragment != null)
            captureTimeFragment.setSelectedDate(value);
    }

    private void logOut() {
        showBusyFragment(getString(R.string.status_logging_out));

        Retrofit retrofit = buildRetrofit(true);
        AccessService accessService = retrofit.create(AccessService.class);
        Identity identity = Identity.getInstance();
        Call<LogoutResponse> call = accessService.postLogout(
                identity.getUserId(),
                identity.getSessionId(),
                identity.getSessionKey());

        call.enqueue(this);
    }
}
