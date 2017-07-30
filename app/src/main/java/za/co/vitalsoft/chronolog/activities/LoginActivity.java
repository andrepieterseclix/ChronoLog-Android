package za.co.vitalsoft.chronolog.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.MediaType;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import za.co.vitalsoft.chronolog.Configuration;
import za.co.vitalsoft.chronolog.Identity;
import za.co.vitalsoft.chronolog.R;
import za.co.vitalsoft.chronolog.services.access.AccessService;
import za.co.vitalsoft.chronolog.services.access.models.LoginResponse;
import za.co.vitalsoft.chronolog.utilities.ApiUtils;

public class LoginActivity extends AppCompatActivity implements Callback<LoginResponse> {

    public static final String STARTUP_MESSAGE = "STARTUP_MESSAGE";

    private View mLoginFormView;
    private View mProgressView;
    private EditText mUserIdText;
    private EditText mPasswordText;

    private Call<LoginResponse> mCall = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(R.string.title_login_activity);

        // Widgets
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mUserIdText = (EditText) findViewById(R.id.user_id_text);
        mPasswordText = (EditText) findViewById(R.id.password_text);

        // Close Button
        Button closeButton = (Button) findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.exit(0);
            }
        });

        // Login Button
        Button loginButton = (Button) findViewById(R.id.log_in_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hide keyboard
                View focused = getCurrentFocus();
                if (focused != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                // Login
                attemptLogin();
            }
        });

        // Cancel Button
        Button cancelButton = (Button) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCall != null) {
                    mCall.cancel();
                    mCall = null;
                    showProgress(false);
                }
            }
        });

        Intent intent = getIntent();
        String message;

        if (intent != null && (message = intent.getStringExtra(STARTUP_MESSAGE)) != null) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }

        // Development Code
        final int appFlags = getApplicationInfo().flags;
        final boolean isDebug = (appFlags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        if (isDebug) {
            mUserIdText.setText("Tester");
            mPasswordText.setText("P@ssw0rd");
        }
    }

    private void attemptLogin() {
        if (mCall != null) {
            return;
        }

        // Clear Errors
        mUserIdText.setError(null);

        // Get Input
        String userId = mUserIdText.getText().toString();
        String password = mPasswordText.getText().toString();

        // Validation
        if (userId.trim().length() < 1) {
            mUserIdText.setError(getString(R.string.must_enter_id_error));
            mUserIdText.requestFocus();
            return;
        }

        if (password.trim().length() < 1) {
            mPasswordText.setError(getString(R.string.must_enter_password_error));
            mPasswordText.requestFocus();
            return;
        }

        // Trigger the service call to perform the authentication
        showProgress(true);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Configuration.SERVICE_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AccessService accessService = retrofit.create(AccessService.class);
        mCall = accessService.getLogin(userId, password);
        mCall.enqueue(this);
    }

    private void showProgress(final boolean show) {
        mLoginFormView.setEnabled(!show);
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
        // TODO:  refactor into utility class

        try {
            mCall = null;
            LoginResponse loginResponse;

            if (!response.isSuccessful()) {

                String errorMessage = response.errorBody().string();
                boolean isJsonResponse = response.errorBody().contentType().equals(MediaType.parse("text/json"));

                if (errorMessage == null || errorMessage.length() == 0 || !isJsonResponse) {
                    Log.e("error", getString(R.string.service_read_error_not_available));
                    Toast.makeText(this, getString(R.string.service_read_error_not_available), Toast.LENGTH_LONG).show();
                } else {
                    errorMessage = ApiUtils.tryExtractErrorMessage(errorMessage);
                    Log.e("error", errorMessage);
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                }

                showProgress(false);
                mPasswordText.setText("");
                mUserIdText.setError(errorMessage);
                mPasswordText.requestFocus();

            } else if ((loginResponse = response.body()).getIsLoggedIn()) {
                Intent mainIntent = new Intent(this, MainActivity.class);

                Identity identity = Identity.getInstance();
                identity.setUserId(loginResponse.getUser().getUserName());
                identity.setSessionId(loginResponse.getSession().getId());
                identity.setSessionKey(loginResponse.getSession().getSessionKey());
                identity.setUser(loginResponse.getUser());

                finish();
                startActivity(mainIntent);
            }
        } catch (IOException | NullPointerException ex) {
            Log.e("error", getString(R.string.service_read_error_message), ex);
            Toast.makeText(this, getString(R.string.service_read_error_message), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
        // Check if cancelled
        if (mCall == null) {
            return;
        }

        mCall = null;
        Log.e("error", getString(R.string.login_error), t);
        Toast.makeText(this, getString(R.string.login_error), Toast.LENGTH_LONG).show();

        showProgress(false);
    }
}
