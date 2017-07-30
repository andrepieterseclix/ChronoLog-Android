package za.co.vitalsoft.chronolog.services.timesheet;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import za.co.vitalsoft.chronolog.services.PathDate;
import za.co.vitalsoft.chronolog.services.timesheet.models.GetTimesheetResponse;
import za.co.vitalsoft.chronolog.services.timesheet.models.PostCapturedTimeItem;
import za.co.vitalsoft.chronolog.services.timesheet.models.PostTimesheetResponse;

/**
 * Created by Andre on 2017-07-29.
 */

public interface TimesheetService {

    @GET("timesheet")
    Call<GetTimesheetResponse> getTimesheet(@Query("fromDate") PathDate fromDate, @Query("toDate") PathDate toDate);

    @POST("timesheet")
    Call<PostTimesheetResponse> postTimesheet(@Body PostCapturedTimeItem[] items);
}
