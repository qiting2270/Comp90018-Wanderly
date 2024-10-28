package com.example.wanderly;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateFormatter {

    /**
    // Example usage of the DateFormatter
    String departureDate = "30/10/2024";
    String[] formattedDeparture = DateFormatter.getDayAndMonth(departureDate);
    String departureDay = formattedDeparture[0];
    String departureMonth = formattedDeparture[1];*/

    private static final SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
    private static final SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.US);
    private static final SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.US);

    // Method to get formatted day and month
    public static String[] getDayAndMonth(String dateString) {
        try {
            Date date = inputFormat.parse(dateString);
            String day = dayFormat.format(date);
            String month = monthFormat.format(date).toUpperCase();
            return new String[] { day, month };
        } catch (ParseException e) {
            e.printStackTrace();
            return new String[] { "Error", "Error" };
        }
    }
}
