package com.example.wanderly;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DateUtils {

    /**
     * Calculates the number of days from today to the specified date.
     * @param futureDateStr The future date in "dd/MM/yyyy" format.
     * @return The number of days until the future date, or -1 if there is an error parsing the date.
     */
    public static long daysUntil(String futureDateStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        try {
            // Parse the future date string
            Date futureDate = dateFormat.parse(futureDateStr);

            // Get today's date without time
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            // Get the difference in milliseconds
            long diff = futureDate.getTime() - today.getTimeInMillis();

            // Convert milliseconds to days
            return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
