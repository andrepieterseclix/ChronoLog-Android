package za.co.vitalsoft.chronolog.services;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import za.co.vitalsoft.chronolog.Configuration;

/**
 * Created by Andre on 2017-07-29.
 */

public class PathDate {

    private static final ThreadLocal<DateFormat> DATE_FORMAT = new ThreadLocal<DateFormat>() {
        @Override
        public DateFormat initialValue() {
            return new SimpleDateFormat(Configuration.DATE_FORMAT);
        }
    };

    private final Date mDate;

    public PathDate(Date date) {
        mDate = date;
    }

    @Override
    public String toString() {
        return DATE_FORMAT.get().format(mDate);
    }
}
