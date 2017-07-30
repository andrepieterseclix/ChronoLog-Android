package za.co.vitalsoft.chronolog.interfaces;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Andre on 2017-07-23.
 */

public interface BaseActivityContract {

    void showBusyFragment(@Nullable String message);

    void finishBusyFragment();

    Retrofit buildRetrofit(final boolean isSecure);

    <T> T interpretServiceResponse(@NonNull Response<T> response);
}
