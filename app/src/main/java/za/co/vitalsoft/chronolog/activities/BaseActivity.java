package za.co.vitalsoft.chronolog.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import za.co.vitalsoft.chronolog.Configuration;
import za.co.vitalsoft.chronolog.Identity;
import za.co.vitalsoft.chronolog.R;
import za.co.vitalsoft.chronolog.dialogs.HtmlErrorDialog;
import za.co.vitalsoft.chronolog.fragments.BusyFragment;
import za.co.vitalsoft.chronolog.interfaces.BaseActivityContract;
import za.co.vitalsoft.chronolog.utilities.ApiUtils;

/**
 * Created by Andre on 2017-07-23.
 */

public abstract class BaseActivity extends AppCompatActivity implements BaseActivityContract {

    private static final String HTML_ERROR_DIALOG_FRAGMENT = "HTML_ERROR_DIALOG_FRAGMENT";
    private static final String BUSY_FRAGMENT = "BUSY_FRAGMENT";

    protected Menu mMenu;

    public static void restartApp(Context context, @Nullable String message) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (message != null && message.length() > 0) {
            intent.putExtra(LoginActivity.STARTUP_MESSAGE, message);
        }

        context.startActivity(intent);

        if (context instanceof Activity) {
            ((Activity) context).finish();
        }

        Runtime.getRuntime().exit(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;

        return super.onCreateOptionsMenu(menu);
    }

    public void showBusyFragment(@Nullable String message) {
        BusyFragment loggingOutFragment = BusyFragment.create(message);

        getFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_holder, loggingOutFragment, BUSY_FRAGMENT)
                .commit();

        if (mMenu != null)
            mMenu.setGroupEnabled(R.id.menu_group, false);
    }

    public void finishBusyFragment() {
        if (mMenu != null)
            mMenu.setGroupEnabled(R.id.menu_group, true);

        getFragmentManager()
                .beginTransaction()
                .remove(getFragmentManager().findFragmentByTag(BUSY_FRAGMENT))
                .commit();
    }

    public Retrofit buildRetrofit(final boolean isSecure) {

        Gson gson = new GsonBuilder()
                .setLenient()
                .setDateFormat(Configuration.REST_DATE_FORMAT)
                .create();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Configuration.SERVICE_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson));

        // For a secure call, add the user session info to the http header
        if (isSecure) {
            final Identity identity = Identity.getInstance();

            OkHttpClient okHttpClient = new OkHttpClient()
                    .newBuilder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public okhttp3.Response intercept(@NonNull Chain chain) throws IOException {
                            Request originalRequest = chain.request();
                            Request.Builder builder = originalRequest
                                    .newBuilder()
                                    .header("Set-Cookie", String.format(
                                            "%s/%s/%s",
                                            identity.getUserId(),
                                            identity.getSessionId(),
                                            identity.getSessionKey()));

                            Request newRequest = builder.build();
                            return chain.proceed(newRequest);
                        }
                    })
                    .build();

            builder.client(okHttpClient);
        }

        return builder.build();
    }

    public <T> T interpretServiceResponse(@NonNull Response<T> response) {
        try {
            if (!response.isSuccessful()) {

                if (response.code() == 401) {
                    Log.w("warn", "Server session has expired.");

                    restartApp(this, getString(R.string.server_session_expired_message));
                }

                ResponseBody errorBody = response.errorBody();

                if (errorBody == null) {
                    throw new RuntimeException(getString(R.string.service_no_error_body_message));
                }

                String errorMessage = errorBody.string();
                MediaType mediaType = errorBody.contentType();

                if (mediaType == null) {
                    throw new RuntimeException(getString(R.string.service_no_error_body_message));
                }

                if (mediaType.subtype().equals("json")) {
                    errorMessage = ApiUtils.tryExtractErrorMessage(errorMessage);
                    Log.e("error", errorMessage);
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                } else {
                    HtmlErrorDialog
                            .create(errorMessage)
                            .show(getFragmentManager(), HTML_ERROR_DIALOG_FRAGMENT);
                }
            }
        } catch (IOException | NullPointerException ex) {
            Log.e("error", getString(R.string.service_read_error_message), ex);
            Toast.makeText(this, getString(R.string.service_read_error_message), Toast.LENGTH_LONG).show();
        }

        // Caller should always check for null!
        return response.body();
    }
}
