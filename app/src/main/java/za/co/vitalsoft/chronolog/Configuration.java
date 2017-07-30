package za.co.vitalsoft.chronolog;

/**
 * Created by Andre on 2017-07-22.
 */

public class Configuration {

    public static final String REST_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String LONG_DATE_FORMAT = "EEE, MMM d, yyyy";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DAY_OF_WEEK_FORMAT = "EEEE";
    private static final String DEV_SERVICE_BASE_URL = "http://192.168.8.100:49934";
    public static final String SERVICE_BASE_URL = DEV_SERVICE_BASE_URL;
    private static final String PRD_SERVICE_BASE_URL = "unset";
}
