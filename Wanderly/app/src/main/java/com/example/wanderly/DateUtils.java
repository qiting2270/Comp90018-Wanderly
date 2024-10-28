package com.example.wanderly;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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


    /**
     // Get all dates including departure and subsequent ones based on the trip duration
     List<String> allDates = DateUtils.calculateSubsequentDates(departureDate, tripDuration);
     */
    // Convert the date from "dd/MM/yyyy" to "yyyy/MM/dd"
    public static String convertDate(String inputDate) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat newFormat = new SimpleDateFormat("yyyy/MM/dd");
        try {
            Date date = originalFormat.parse(inputDate);
            return newFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Calculate and return all dates including and following the departure, up to the trip duration
    public static List<String> getSubsequentDates(String departureDate, int tripDuration) {
        List<String> dates = new ArrayList<>();
        SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy/MM/dd");

        try {
            Date date = originalFormat.parse(departureDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            // Adding all dates from the departure date up to the trip duration
            for (int i = 0; i < tripDuration; i++) {
                dates.add(targetFormat.format(calendar.getTime()));
                calendar.add(Calendar.DATE, 1); // Increment the date by one day
            }
        } catch (ParseException e) {
            e.printStackTrace();
            dates.add("Invalid date format");
        }

        return dates;
    }


}
