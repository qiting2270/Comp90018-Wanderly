package com.example.wanderly;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Gravity;
import android.graphics.Typeface;



import org.w3c.dom.Text;

import java.util.Calendar;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AddTripActivity extends AppCompatActivity {
    private Spinner whereFrom;
    private Spinner whereTo;
    private TextView departureDate;
    private TextView returnDate;
    private TextView addMembersBtn;
    private HorizontalScrollView horizontalScrollView;
    private HorizontalScrollView addTripMemberScrollView;
    private TextView nothingSelectedText;

    private String trip_from;
    private String trip_to;
    private int fromSelectionIndex = 0;
    private int toSelectionIndex = 0;

    Calendar departureCalendar;
    Calendar returnCalendar;

    private boolean isDepartureDateSet = false;
    private boolean isReturnDateSet = false;

    LinearLayout addTripLinearLayout;
    private int tripDuration;

    ConstraintLayout Day1;
    ConstraintLayout Day2;

    TextView day1DateText;
    TextView day2DateText;

    ConstraintLayout Day1AddStopBtn;

    ConstraintLayout addStopPopup;
    private Spinner timeFromSpinner, timeToSpinner;
    private ArrayList<String> timeValues;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_trip);


        whereFrom = findViewById(R.id.add_trip_spinner_wherefrom);
        whereTo = findViewById(R.id.add_trip_spinner_whereTo);
        departureDate = findViewById(R.id.add_trip_departure_date);
        returnDate = findViewById(R.id.add_trip_return_date);
        addMembersBtn = findViewById(R.id.add_trip_addmembers);
        horizontalScrollView = findViewById(R.id.add_trip_Horizontal_scroll_view);
        addTripMemberScrollView = findViewById(R.id.add_trip_member_scroll_view);

        // Initialize Calendar objects
        departureCalendar = Calendar.getInstance();
        returnCalendar = Calendar.getInstance();

        addTripLinearLayout = findViewById(R.id.add_trip_Horizontal_scroll_view_linearlayout);
        nothingSelectedText = findViewById(R.id.add_trip_nothing_selected_text);
        //addStopBtn = insideHorizontalLayout.findViewById(R.id.addtrip_inside_add_stopBtn);
        addStopPopup = findViewById(R.id.addtrip_inside_popup_layout);
        Day1 = findViewById(R.id.add_trip_day_1);
        Day2 = findViewById(R.id.add_trip_day_2);
        Day1AddStopBtn = findViewById(R.id.addtrip__day1_inside_add_stopBtn);

        timeFromSpinner = findViewById(R.id.add_trip_popup_spinner_time_from);
        timeToSpinner = findViewById(R.id.add_trip_popup_spinner_time_to);

        //back icon logic
        ImageView backIcon = (ImageView) findViewById(R.id.back_icon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // go back to previous activity
                finish();
            }
        });


        whereFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                trip_from = adapterView.getItemAtPosition(i).toString();
                // update spinner selection index
                fromSelectionIndex = i;
                updateVisibility();

                Toast.makeText(AddTripActivity.this,"Selected item" + trip_from, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        whereTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                trip_to = adapterView.getItemAtPosition(i).toString();
                toSelectionIndex = i;
                updateVisibility();
                Toast.makeText(AddTripActivity.this,"Selected item" + trip_to, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        // Where From
        ArrayList<String> arrayListWhereFrom = new ArrayList<>();
        arrayListWhereFrom.add("Where From");
        arrayListWhereFrom.add("Melbourne");
        arrayListWhereFrom.add("Sydney");
        arrayListWhereFrom.add("Canberra");
        arrayListWhereFrom.add("Perth");
        arrayListWhereFrom.add("Gold Coast");
        arrayListWhereFrom.add("Brisbane");

        ArrayAdapter<String> adapterWhereFrom = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayListWhereFrom);
        adapterWhereFrom.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        whereFrom.setAdapter(adapterWhereFrom);

        // Where To
        ArrayList<String> arrayListWhereTo = new ArrayList<>();
        arrayListWhereTo.add("Where To");
        arrayListWhereTo.add("Melbourne");
        arrayListWhereTo.add("Sydney");
        arrayListWhereTo.add("Canberra");
        arrayListWhereTo.add("Perth");
        arrayListWhereTo.add("Gold Coast");
        arrayListWhereTo.add("Brisbane");

        ArrayAdapter<String> adapterWhereTo = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayListWhereTo);
        adapterWhereTo.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        whereTo.setAdapter(adapterWhereTo);


        // Set onClickListener for departure date picker
        departureDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // true for departure
                showDatePickerDialog(departureDate,true);
            }
        });

        returnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // false for return
                showDatePickerDialog(returnDate, false);
            }
        });



        /*
        LayoutInflater inflater = LayoutInflater.from(this);
        View constraintLayout = inflater.inflate(R.layout.addtrip_inside_horizontal_layout, addTripLinearLayout, false);
        addTripLinearLayout.addView(constraintLayout);*/

        Day1AddStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addStopPopup.setVisibility(View.VISIBLE);
            }
        });

        //time value
        timeValues = new ArrayList<>();
        timeValues.add("Select Time"); // Default selection option
        timeValues.add("12:00 AM");
        timeValues.add("1:00 AM");
        timeValues.add("2:00 AM");
        timeValues.add("3:00 AM");
        timeValues.add("4:00 AM");
        timeValues.add("5:00 AM");
        timeValues.add("6:00 AM");
        timeValues.add("7:00 AM");
        timeValues.add("8:00 AM");
        timeValues.add("9:00 AM");
        timeValues.add("10:00 AM");
        timeValues.add("11:00 AM");
        timeValues.add("12:00 PM");
        timeValues.add("1:00 PM");
        timeValues.add("2:00 PM");
        timeValues.add("3:00 PM");
        timeValues.add("4:00 PM");
        timeValues.add("5:00 PM");
        timeValues.add("6:00 PM");
        timeValues.add("7:00 PM");
        timeValues.add("8:00 PM");
        timeValues.add("9:00 PM");
        timeValues.add("10:00 PM");
        timeValues.add("11:00 PM");

        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, timeValues);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        timeFromSpinner.setAdapter(timeAdapter);
        timeToSpinner.setAdapter(timeAdapter);


        timeFromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedFromTime = parent.getItemAtPosition(position).toString();
                if (!selectedFromTime.equals("Select Time")) {
                    Toast.makeText(AddTripActivity.this, "Selected From Time: " + selectedFromTime, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        timeToSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedToTime = parent.getItemAtPosition(position).toString();
                if (!selectedToTime.equals("Select Time")) {
                    Toast.makeText(AddTripActivity.this, "Selected To Time: " + selectedToTime, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

      




    }

    private void showDatePickerDialog(final TextView textView, final boolean isDeparture) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                AddTripActivity.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Format the selected date
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    textView.setText(selectedDate);

                    // Update the respective Calendar object
                    if (isDeparture) {
                        departureCalendar.set(selectedYear, selectedMonth, selectedDay);
                        isDepartureDateSet = true;
                    }
                    else {
                        returnCalendar.set(selectedYear, selectedMonth, selectedDay);
                        isReturnDateSet = true;

                        // Calculate and show the duration when both dates are selected
                        if (departureCalendar != null && returnCalendar != null) {
                            calculateDuration();
                        }
                    }
                    // the moment user select the date
                    updateVisibility();
                    updateInsideLayout();
                },
                year, month, day);

        datePickerDialog.show();
    }

    private void updateInsideLayout(){
        Calendar currentDay = (Calendar) departureCalendar.clone();

        day1DateText = findViewById(R.id.addtrip_day1_inside_date_text);
        day2DateText = findViewById(R.id.addtrip_day2_inside_date_text);

        String formattedDate_Day1 = String.format(Locale.getDefault(), "%d-%02d-%02d",
                currentDay.get(Calendar.YEAR),
                currentDay.get(Calendar.MONTH) + 1,
                currentDay.get(Calendar.DAY_OF_MONTH));

        if (tripDuration == 1){
            Day1.setVisibility(View.VISIBLE);
            Day2.setVisibility(View.GONE);

            day1DateText.setText(formattedDate_Day1);
        }
        else if (tripDuration == 2){
            Day1.setVisibility(View.VISIBLE);
            Day2.setVisibility(View.VISIBLE);

            day1DateText.setText(formattedDate_Day1);

            currentDay.add(Calendar.DAY_OF_MONTH, 1);

            String formattedDate_Day2 = String.format(Locale.getDefault(), "%d-%02d-%02d",
                    currentDay.get(Calendar.YEAR),
                    currentDay.get(Calendar.MONTH) + 1,
                    currentDay.get(Calendar.DAY_OF_MONTH));
            day2DateText.setText(formattedDate_Day2);
        }

    }


//    @SuppressLint("SetTextI18n")
//    private void updateInsideLayout() {
//        addTripLinearLayout.removeAllViews();
//
//        // Create a clone of the departure date to iterate through days
//        Calendar currentDay = (Calendar) departureCalendar.clone();
//
//        for (int i = 1; i <= tripDuration; i++) {
//            // Inflate the layout for each day
//            LayoutInflater inflater = LayoutInflater.from(this);
//            View insideHorizontalLayout = inflater.inflate(R.layout.addtrip_inside_horizontal_layout, addTripLinearLayout, false);
//
//            // Find the TextView for displaying day and date inside the newly inflated view
//            TextView dayText = insideHorizontalLayout.findViewById(R.id.addtrip_inside_day_text);
//            TextView dateText = insideHorizontalLayout.findViewById(R.id.addtrip_inside_date_text);
//
//            String formattedDate = String.format(Locale.getDefault(), "%d-%02d-%02d",
//                    currentDay.get(Calendar.YEAR),
//                    currentDay.get(Calendar.MONTH) + 1,
//                    currentDay.get(Calendar.DAY_OF_MONTH));
//
//            dayText.setText("Day " + i + " ");
//            dateText.setText(formattedDate);
//
//
//
//            addTripLinearLayout.addView(insideHorizontalLayout);
//
//            // Move to the next day
//            currentDay.add(Calendar.DAY_OF_MONTH, 1);
//        }
//    }



    private void calculateDuration() {
        long departureTimeInMillis = departureCalendar.getTimeInMillis();
        long returnTimeInMillis = returnCalendar.getTimeInMillis();

        // Calculate the difference in milliseconds
        long durationInMillis = returnTimeInMillis - departureTimeInMillis;

        // Convert milliseconds to days
        int daysBetween = (int) TimeUnit.MILLISECONDS.toDays(durationInMillis);

        // make duration inclusive
        tripDuration = daysBetween + 1;

        if (tripDuration >= 0) {
            // Show the duration in a toast
            Toast.makeText(AddTripActivity.this, "Duration: " + tripDuration + " days", Toast.LENGTH_SHORT).show();
        } else {
            // If the return date is before the departure date
            Toast.makeText(AddTripActivity.this, "Return date cannot be before departure date!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateVisibility() {
        // all top info selected
        if (fromSelectionIndex != 0 && toSelectionIndex != 0 && isDepartureDateSet && isReturnDateSet) {
            addMembersBtn.setVisibility(View.VISIBLE);
            horizontalScrollView.setVisibility(View.VISIBLE);
            addTripMemberScrollView.setVisibility(View.VISIBLE);
            nothingSelectedText.setVisibility(View.GONE);
        }
        else {
            // nothing is selected
            addMembersBtn.setVisibility(View.GONE);
            horizontalScrollView.setVisibility(View.GONE);
            nothingSelectedText.setVisibility(View.VISIBLE);
            addTripMemberScrollView.setVisibility(View.GONE);
        }
    }




}