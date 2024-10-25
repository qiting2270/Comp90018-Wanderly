package com.example.wanderly;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;


import org.w3c.dom.Text;

import java.util.Calendar;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class AddTripActivity extends AppCompatActivity {
    private Spinner whereFrom;
    private Spinner whereTo;
    private TextView departureDate;
    private TextView returnDate;
    private TextView addFlightBtn;
    private TextView addMembersBtn;
    private HorizontalScrollView horizontalScrollView;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_trip);


        whereFrom = findViewById(R.id.add_trip_spinner_wherefrom);
        whereTo = findViewById(R.id.add_trip_spinner_whereTo);
        departureDate = findViewById(R.id.add_trip_departure_date);
        returnDate = findViewById(R.id.add_trip_return_date);
        addFlightBtn = findViewById(R.id.add_trip_addflight);
        addMembersBtn = findViewById(R.id.add_trip_addmembers);
        horizontalScrollView = findViewById(R.id.add_trip_Horizontal_scroll_view);

        // Initialize Calendar objects
        departureCalendar = Calendar.getInstance();
        returnCalendar = Calendar.getInstance();

        addTripLinearLayout = findViewById(R.id.add_trip_Horizontal_scroll_view_linearlayout);
        nothingSelectedText = findViewById(R.id.add_trip_nothing_selected_text);

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

        // Inflate the ConstraintLayout from XML
        LayoutInflater inflater = LayoutInflater.from(this);
        View constraintLayout = inflater.inflate(R.layout.addtrip_inside_horizontal_layout, addTripLinearLayout, false);

        // Now add the inflated ConstraintLayout to the LinearLayout
        addTripLinearLayout.addView(constraintLayout);





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
                    updateVisibility();
                },
                year, month, day);

        datePickerDialog.show();
    }

    private void calculateDuration() {
        long departureTimeInMillis = departureCalendar.getTimeInMillis();
        long returnTimeInMillis = returnCalendar.getTimeInMillis();

        // Calculate the difference in milliseconds
        long durationInMillis = returnTimeInMillis - departureTimeInMillis;

        // Convert milliseconds to days
        int daysBetween = (int) TimeUnit.MILLISECONDS.toDays(durationInMillis);

        if (daysBetween >= 0) {
            // Show the duration in a toast
            Toast.makeText(AddTripActivity.this, "Duration: " + daysBetween + " days", Toast.LENGTH_SHORT).show();
        } else {
            // If the return date is before the departure date
            Toast.makeText(AddTripActivity.this, "Return date cannot be before departure date!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateVisibility() {
        if (fromSelectionIndex != 0 && toSelectionIndex != 0 && isDepartureDateSet && isReturnDateSet) {
            addMembersBtn.setVisibility(View.VISIBLE);
            addFlightBtn.setVisibility(View.VISIBLE);
            horizontalScrollView.setVisibility(View.VISIBLE);
            nothingSelectedText.setVisibility(View.GONE);

        } else {
            addMembersBtn.setVisibility(View.GONE);
            addFlightBtn.setVisibility(View.GONE);
            horizontalScrollView.setVisibility(View.GONE);
            nothingSelectedText.setVisibility(View.VISIBLE);

        }
    }




}