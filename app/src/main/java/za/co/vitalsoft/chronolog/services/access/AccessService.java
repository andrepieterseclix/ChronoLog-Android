package za.co.vitalsoft.chronolog.services.access;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import za.co.vitalsoft.chronolog.services.access.models.LoginResponse;
import za.co.vitalsoft.chronolog.services.access.models.LogoutResponse;
import za.co.vitalsoft.chronolog.services.access.models.UpdateUserRequest;
import za.co.vitalsoft.chronolog.services.access.models.UpdateUserResponse;

/**
 * Created by Andre on 2017-07-20.
 */

public interface AccessService {

    @GET("access/login")
    Call<LoginResponse> getLogin(@Query("userName") String userName, @Query("password") String password);

    @POST("access/logout")
    Call<LogoutResponse> postLogout(@Query("userName") String userName, @Query("sessionId") String sessionId, @Query("sessionKey") String sessionKey);

    @PUT("access/users/{username}")
    Call<UpdateUserResponse> putUser(@Path("username") String userName, @Body UpdateUserRequest request);
}
