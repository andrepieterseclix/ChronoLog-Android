package za.co.vitalsoft.chronolog.utilities;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by Andre on 2017-07-23.
 */

public class ApiUtils {

    private static final String ERROR_RESPONSE_MESSAGE_FIELD_NAME = "Message";

    public static String tryExtractErrorMessage(final String responseBody) {
        try {
            JSONObject obj = (JSONObject) new JSONTokener(responseBody).nextValue();
            return obj.getString(ERROR_RESPONSE_MESSAGE_FIELD_NAME);
        } catch (JSONException jex) {
            return responseBody;
        } catch (Exception e) {
            return responseBody;
        }
    }
}