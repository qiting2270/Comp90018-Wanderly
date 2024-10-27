package com.example.wanderly;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateConverter {

    /**
     * Converts a date string from "dd/MM/yyyy" format to "MM/dd" format.
     * @param dateStr The date string in "dd/MM/yyyy" format.
     * @return A string representing the date in "MM/dd" format, or the original string if parsing fails.
     */
    public static String convertDate(String dateStr) {
        // The format of the incoming date string
        SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        // The desired format
        SimpleDateFormat targetFormat = new SimpleDateFormat("MM/dd", Locale.getDefault());

        try {
            // Parse the original date string
            Date date = originalFormat.parse(dateStr);
            // Format the date to the new format and return
            return targetFormat.format(date);
        } catch (ParseException e) {
            // If there is an error in parsing, print the stack trace for debugging
            e.printStackTrace();
            // Return the original date string if parsing fails
            return dateStr;
        }
    }
}
