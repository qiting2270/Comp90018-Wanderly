package com.example.wanderly;

import static com.bumptech.glide.load.resource.bitmap.TransformationUtils.circleCrop;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import android.app.DatePickerDialog;
import android.widget.TextView;
import android.graphics.Typeface;
import androidx.constraintlayout.widget.ConstraintSet;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.Calendar;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
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
    ConstraintLayout Day2AddStopBtn;
    ConstraintLayout addStopPopup;
    ConstraintLayout addMemberPopup;
    ImageView popUpCloseBtn;
    ImageView addMembersCloseBtn;
    private Spinner timeFromSpinner, timeToSpinner;
    private ArrayList<String> timeValues;

    private String selectedPlace = "";
    TextView restaurant_ThaiTown;
    TextView restaurant_Billy;
    TextView restaurant_Bornga;
    TextView restaurant_sweetCanteen;

    TextView attraction_ngv;
    TextView attraction_library;
    TextView attraction_QVM;
    TextView attraction_Gaol;

    LinearLayout stopsLinearLayoutDay1;
    LinearLayout stopsLinearLayoutDay2;

    private String addstop_selectedTimeFrom;
    private String addstop_selectedTimeTo;

    private DatabaseReference databaseReference;
    private String uniqueTripId;

    HashMap<String, Object> TripDetailsHashMap;
    HashMap<String, Object> DayHashMap;
    private int tripDay;

    TextView addTripDoneBtn;
    EditText memberEmail;
    Button addMemberEmailBtn;

    private FirebaseAuth auth;
    private String currentUserId;
    private String currentUserEmail;

    LinearLayout addMemberLinearLayout;
    private Boolean noProfileImg = false;




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
        addMemberPopup = findViewById(R.id.add_trip_addmembers_popup_layout);
        popUpCloseBtn = findViewById(R.id.add_trip_pop_up_close_btn);
        addMembersCloseBtn = findViewById(R.id.add_trip_addmembers_closeBtn);
        Day1 = findViewById(R.id.add_trip_day_1);
        Day2 = findViewById(R.id.add_trip_day_2);
        Day1AddStopBtn = findViewById(R.id.addtrip__day1_inside_add_stopBtn);
        Day2AddStopBtn = findViewById(R.id.addtrip__day2_inside_add_stopBtn);

        timeFromSpinner = findViewById(R.id.add_trip_popup_spinner_time_from);
        timeToSpinner = findViewById(R.id.add_trip_popup_spinner_time_to);

        restaurant_ThaiTown = findViewById(R.id.stop_restaurant_thaiTown);
        restaurant_Billy = findViewById(R.id.stop_restaurant_BillyCentral);
        restaurant_Bornga = findViewById(R.id.stop_restaurant_Bornga);
        restaurant_sweetCanteen = findViewById(R.id.stop_restaurant_sweetcanteen);
        attraction_ngv = findViewById(R.id.stop_attraction_ngv);
        attraction_library = findViewById(R.id.stop_attraction_statelibrary);
        attraction_QVM = findViewById(R.id.stop_attraction_queenVictoriaMarket);
        attraction_Gaol = findViewById(R.id.stop_attraction_oldMelGaol);
        stopsLinearLayoutDay1 = findViewById(R.id.stops_linear_layout_day1);
        stopsLinearLayoutDay2 = findViewById(R.id.stops_linear_layout_day2);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        uniqueTripId = databaseReference.child("Trips").push().getKey();
        TripDetailsHashMap = new HashMap<>();
        DayHashMap = new HashMap<>();
        addTripDoneBtn = findViewById(R.id.add_trip_done);
        memberEmail = findViewById(R.id.add_trip_addmembers_email);
        addMemberEmailBtn = findViewById(R.id.addtrip_add_members_Btn);

        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();

        addMemberLinearLayout = findViewById(R.id.add_member_linearlayout);

        // get current user email by its id.
        databaseReference.child("User Information").child(currentUserId).child("email")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Retrieve the email value from the database
                        if (dataSnapshot.exists()) {
                            currentUserEmail = dataSnapshot.getValue(String.class);
                            // store current user email first.
                            checkEmailAndStore(currentUserEmail);
                        }
                        else {
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle possible errors
                        Toast.makeText(AddTripActivity.this, "Failed to retrieve email: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


        //back icon logic
        ImageView backIcon = (ImageView) findViewById(R.id.back_icon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.child("Trips").child(uniqueTripId).removeValue();

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
                tripDay = 1;
                addStopPopup.setVisibility(View.VISIBLE);
            }
        });
        Day2AddStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tripDay = 2;
                addStopPopup.setVisibility(View.VISIBLE);
            }
        });

        popUpCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addStopPopup.setVisibility(View.GONE);
                // Reset the color of each item to black
                restaurant_ThaiTown.setTextColor(Color.BLACK);
                restaurant_Billy.setTextColor(Color.BLACK);
                restaurant_Bornga.setTextColor(Color.BLACK);
                restaurant_sweetCanteen.setTextColor(Color.BLACK);
                attraction_ngv.setTextColor(Color.BLACK);
                attraction_library.setTextColor(Color.BLACK);
                attraction_QVM.setTextColor(Color.BLACK);
                attraction_Gaol.setTextColor(Color.BLACK);

                //selectedPlace = null;
                timeFromSpinner.setSelection(0);
                timeToSpinner.setSelection(0);
            }
        });


        restaurant_ThaiTown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedPlace = restaurant_ThaiTown.getText().toString();
                restaurant_ThaiTown.setTextColor(Color.RED);
                restaurant_Billy.setTextColor(Color.BLACK);
                restaurant_Bornga.setTextColor(Color.BLACK);
                restaurant_sweetCanteen.setTextColor(Color.BLACK);
                attraction_ngv.setTextColor(Color.BLACK);
                attraction_library.setTextColor(Color.BLACK);
                attraction_QVM.setTextColor(Color.BLACK);
                attraction_Gaol.setTextColor(Color.BLACK);
            }
        });

        restaurant_Billy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedPlace = restaurant_Billy.getText().toString();
                restaurant_Billy.setTextColor(Color.RED);
                restaurant_ThaiTown.setTextColor(Color.BLACK);
                restaurant_Bornga.setTextColor(Color.BLACK);
                restaurant_sweetCanteen.setTextColor(Color.BLACK);
                attraction_ngv.setTextColor(Color.BLACK);
                attraction_library.setTextColor(Color.BLACK);
                attraction_QVM.setTextColor(Color.BLACK);
                attraction_Gaol.setTextColor(Color.BLACK);
            }
        });
        restaurant_Bornga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedPlace = restaurant_Bornga.getText().toString();
                restaurant_Bornga.setTextColor(Color.RED);
                restaurant_ThaiTown.setTextColor(Color.BLACK);
                restaurant_Billy.setTextColor(Color.BLACK);
                restaurant_sweetCanteen.setTextColor(Color.BLACK);
                attraction_ngv.setTextColor(Color.BLACK);
                attraction_library.setTextColor(Color.BLACK);
                attraction_QVM.setTextColor(Color.BLACK);
                attraction_Gaol.setTextColor(Color.BLACK);
            }
        });

        restaurant_sweetCanteen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedPlace = restaurant_sweetCanteen.getText().toString();
                restaurant_sweetCanteen.setTextColor(Color.RED);
                restaurant_ThaiTown.setTextColor(Color.BLACK);
                restaurant_Billy.setTextColor(Color.BLACK);
                restaurant_Bornga.setTextColor(Color.BLACK);
                attraction_ngv.setTextColor(Color.BLACK);
                attraction_library.setTextColor(Color.BLACK);
                attraction_QVM.setTextColor(Color.BLACK);
                attraction_Gaol.setTextColor(Color.BLACK);
            }
        });

        attraction_ngv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedPlace = attraction_ngv.getText().toString();
                attraction_ngv.setTextColor(Color.RED);
                restaurant_ThaiTown.setTextColor(Color.BLACK);
                restaurant_Billy.setTextColor(Color.BLACK);
                restaurant_Bornga.setTextColor(Color.BLACK);
                restaurant_sweetCanteen.setTextColor(Color.BLACK);
                attraction_library.setTextColor(Color.BLACK);
                attraction_QVM.setTextColor(Color.BLACK);
                attraction_Gaol.setTextColor(Color.BLACK);
            }
        });

        attraction_library.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedPlace = attraction_library.getText().toString();
                attraction_library.setTextColor(Color.RED);
                restaurant_ThaiTown.setTextColor(Color.BLACK);
                restaurant_Billy.setTextColor(Color.BLACK);
                restaurant_Bornga.setTextColor(Color.BLACK);
                restaurant_sweetCanteen.setTextColor(Color.BLACK);
                attraction_ngv.setTextColor(Color.BLACK);
                attraction_QVM.setTextColor(Color.BLACK);
                attraction_Gaol.setTextColor(Color.BLACK);
            }
        });

        attraction_QVM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedPlace = attraction_QVM.getText().toString();
                attraction_QVM.setTextColor(Color.RED);
                restaurant_ThaiTown.setTextColor(Color.BLACK);
                restaurant_Billy.setTextColor(Color.BLACK);
                restaurant_Bornga.setTextColor(Color.BLACK);
                restaurant_sweetCanteen.setTextColor(Color.BLACK);
                attraction_ngv.setTextColor(Color.BLACK);
                attraction_library.setTextColor(Color.BLACK);
                attraction_Gaol.setTextColor(Color.BLACK);
            }
        });

        attraction_Gaol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedPlace = attraction_Gaol.getText().toString();
                attraction_Gaol.setTextColor(Color.RED);
                restaurant_ThaiTown.setTextColor(Color.BLACK);
                restaurant_Billy.setTextColor(Color.BLACK);
                restaurant_Bornga.setTextColor(Color.BLACK);
                restaurant_sweetCanteen.setTextColor(Color.BLACK);
                attraction_ngv.setTextColor(Color.BLACK);
                attraction_library.setTextColor(Color.BLACK);
                attraction_QVM.setTextColor(Color.BLACK);
            }
        });

        addMembersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMemberPopup.setVisibility(View.VISIBLE);
            }
        });

        addMembersCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMemberPopup.setVisibility(View.GONE);
            }
        });

        addMemberEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email_txt = memberEmail.getText().toString();
                checkEmailAndStore(email_txt);

            }
        });




        setUpSpinners();

        addTripDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storeTripDetails();
                Intent intent = new Intent(AddTripActivity.this, MyTripsActivity.class);
                startActivity(intent);
            }
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


    private void setUpSpinners() {
        //time value
        timeValues = new ArrayList<>();
        timeValues.add("Select Time"); // Default selection option
        timeValues.add("12 AM");
        timeValues.add("1 AM");
        timeValues.add("2 AM");
        timeValues.add("3 AM");
        timeValues.add("4 AM");
        timeValues.add("5 AM");
        timeValues.add("6 AM");
        timeValues.add("7 AM");
        timeValues.add("8 AM");
        timeValues.add("9 AM");
        timeValues.add("10 AM");
        timeValues.add("11 AM");
        timeValues.add("12 PM");
        timeValues.add("1 PM");
        timeValues.add("2 PM");
        timeValues.add("3 PM");
        timeValues.add("4 PM");
        timeValues.add("5 PM");
        timeValues.add("6 PM");
        timeValues.add("7 PM");
        timeValues.add("8 PM");
        timeValues.add("9 PM");
        timeValues.add("10 PM");
        timeValues.add("11 PM");

        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, timeValues);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        timeFromSpinner.setAdapter(timeAdapter);
        timeToSpinner.setAdapter(timeAdapter);

        timeFromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                addstop_selectedTimeFrom = parent.getItemAtPosition(position).toString();
                checkAndAddNewPlaceLayout();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        timeToSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                addstop_selectedTimeTo = parent.getItemAtPosition(position).toString();
                checkAndAddNewPlaceLayout();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // Function to add a new place layout if both time spinners are selected and a place is selected
    private void checkAndAddNewPlaceLayout() {
        if (!selectedPlace.isEmpty() && timeFromSpinner.getSelectedItemPosition() > 0 && timeToSpinner.getSelectedItemPosition() > 0) {
            // Add a new layout to the parent layout
            if (tripDay == 1){
                addNewPlaceLayout(selectedPlace, stopsLinearLayoutDay1);
            }
            else if (tripDay == 2){
                addNewPlaceLayout(selectedPlace, stopsLinearLayoutDay2);
            }

        }
    }

    // Function to add a new ConstraintLayout to the parent layout
    private void addNewPlaceLayout(String placeName, LinearLayout parentLayout) {

        DayHashMap.put("placeName", placeName);
        DayHashMap.put("type", (Objects.equals(placeName, "Thai Town") || Objects.equals(placeName, "Billy‘s Central") || Objects.equals(placeName, "Bornga") || Objects.equals(placeName, "Sweet Canteen")) ? "restaurant" : "attraction");
        DayHashMap.put("timeFrom", addstop_selectedTimeFrom);
        DayHashMap.put("timeTo", addstop_selectedTimeTo);

        // Save the activity details immediately to Firebase under the specified trip and day
        databaseReference.child("Trips").child(uniqueTripId).child("activities").child("Day"+tripDay).push().setValue(DayHashMap)
                .addOnSuccessListener(aVoid -> Toast.makeText(AddTripActivity.this, "Activity saved successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(AddTripActivity.this, "Failed to save activity", Toast.LENGTH_SHORT).show());



        // Create a new ConstraintLayout
        ConstraintLayout newLayout = new ConstraintLayout(this);
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.bottomMargin = (int) (15 * getResources().getDisplayMetrics().density); // Convert dp to pixels
        newLayout.setLayoutParams(layoutParams);

        // Create an ImageView
        ImageView imageView = new ImageView(this);
        int imageViewId = View.generateViewId();
        imageView.setId(imageViewId);
        if (Objects.equals(placeName, "Thai Town") || Objects.equals(placeName, "Billy‘s Central")
        || Objects.equals(placeName, "Bornga") || Objects.equals(placeName, "Sweet Canteen")){
            imageView.setImageResource(R.drawable.dining_icon);
        }
        else{
            imageView.setImageResource(R.drawable.attraction_icon);
        }

        ConstraintLayout.LayoutParams imageLayoutParams = new ConstraintLayout.LayoutParams(
                (int) (30 * getResources().getDisplayMetrics().density), // Convert dp to pixels
                (int) (30 * getResources().getDisplayMetrics().density)  // Convert dp to pixels
        );
        imageView.setLayoutParams(imageLayoutParams);

        // Add ImageView to ConstraintLayout
        newLayout.addView(imageView);

        // Create a TextView for place name
        TextView placeTextView = new TextView(this);
        int placeTextViewId = View.generateViewId();
        placeTextView.setId(placeTextViewId);
        placeTextView.setText(placeName);
        placeTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        placeTextView.setTextColor(Color.parseColor("#3B5667"));
        Typeface typeface = ResourcesCompat.getFont(this, R.font.kulim_park); // Use ResourceCompat for compatibility
        placeTextView.setTypeface(typeface);
        ConstraintLayout.LayoutParams textLayoutParams = new ConstraintLayout.LayoutParams(
                (int) (120 * getResources().getDisplayMetrics().density),
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        textLayoutParams.setMargins((int) (16 * getResources().getDisplayMetrics().density), 0, 0, 0); // Left margin
        textLayoutParams.startToEnd = imageViewId;
        textLayoutParams.topToTop = imageViewId;
        textLayoutParams.bottomToBottom = imageViewId;
        placeTextView.setLayoutParams(textLayoutParams);

        // Add TextView to ConstraintLayout
        newLayout.addView(placeTextView);

        // Create the second TextView for the time
        TextView timeTextView = new TextView(this);
        int timeTextViewId = View.generateViewId();
        timeTextView.setId(timeTextViewId);
        timeTextView.setText(addstop_selectedTimeFrom + " - " + addstop_selectedTimeTo);
        timeTextView.setTextColor(Color.parseColor("#3B5667"));
        timeTextView.setTextSize(12);  // This uses SP
        ConstraintLayout.LayoutParams timeLayoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        timeTextView.setLayoutParams(timeLayoutParams);


        newLayout.addView(timeTextView);

        // Set constraints using ConstraintSet
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(newLayout);
        constraintSet.connect(imageViewId, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSet.connect(imageViewId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        constraintSet.connect(placeTextViewId, ConstraintSet.START, imageViewId, ConstraintSet.END, (int) (16 * getResources().getDisplayMetrics().density));
        constraintSet.connect(placeTextViewId, ConstraintSet.TOP, imageViewId, ConstraintSet.TOP);
        constraintSet.connect(placeTextViewId, ConstraintSet.BOTTOM, imageViewId, ConstraintSet.BOTTOM);
        constraintSet.connect(timeTextViewId, ConstraintSet.START, placeTextViewId, ConstraintSet.END);
        constraintSet.connect(timeTextViewId, ConstraintSet.TOP, placeTextViewId, ConstraintSet.TOP);
        constraintSet.connect(timeTextViewId, ConstraintSet.BOTTOM, placeTextViewId, ConstraintSet.BOTTOM);
        constraintSet.connect(timeTextViewId, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);  // Add 0 margin
        constraintSet.applyTo(newLayout);


        // Add the new ConstraintLayout to the parent layout
        parentLayout.addView(newLayout);
        // close the pop up
        popUpCloseBtn.performClick();
    }


    private void storeTripDetails() {
        //String uniqueTripId = databaseReference.child("Trips").push().getKey();  // Generates a unique ID for the trip

        TripDetailsHashMap.put("departureDate", departureDate.getText().toString());
        TripDetailsHashMap.put("returnDate", returnDate.getText().toString());
        TripDetailsHashMap.put("tripDuration", tripDuration);
        TripDetailsHashMap.put("departure", trip_from);
        TripDetailsHashMap.put("destination", "Melbourne");

        databaseReference.child("Trips").child(uniqueTripId).updateChildren(TripDetailsHashMap)
                .addOnSuccessListener(aVoid -> Toast.makeText(AddTripActivity.this, "Trip details saved successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(AddTripActivity.this, "Failed to save trip details", Toast.LENGTH_SHORT).show());

    }

    public void checkEmailAndStore(String email) {
        //check if email(member) already exists in db to avoid duplicate members
        databaseReference.child("Trips").child(uniqueTripId).child("Members").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Email already exists in trip members, show a dialog or Toast
                    memberAlreadyExistDialog();
                } else {
                    // Email(member) not found in db, proceed to add to database
                    databaseReference.child("User Information").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // Email found, store it under the trip
                                storeEmailUnderTrip(email);
                                updateMemberImage();

                                if (!Objects.equals(email, currentUserEmail)){
                                    addEmailSuccessDialog();
                                }

                            } else {
                                addEmailNotFoundDialog();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle possible errors
                            Toast.makeText(getApplicationContext(), "Error checking email: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("AddTripActivity", "Failed to check if email exists: " + databaseError.getMessage());
            }
        });



    }

    private void storeEmailUnderTrip(String email) {
        HashMap<String, Object> emailDetails = new HashMap<>();
        emailDetails.put("email", email);

        databaseReference.child("Trips").child(uniqueTripId).child("Members").push().setValue(emailDetails)
                .addOnSuccessListener(aVoid -> Toast.makeText(getApplicationContext(), "Email added to trip successfully", Toast.LENGTH_LONG).show())
                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Failed to add email to trip", Toast.LENGTH_LONG).show());
    }

    private void addEmailSuccessDialog(){
        //store email successful dialog
        AlertDialog.Builder dialog = new AlertDialog.Builder(AddTripActivity.this);
        dialog.setMessage("Add Member Successful!");
        dialog.setCancelable(true);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog.show();
    }

    private void addEmailNotFoundDialog(){
        //store email successful dialog
        AlertDialog.Builder dialog = new AlertDialog.Builder(AddTripActivity.this);
        dialog.setMessage("Email Not Found, Please Try Again!");
        dialog.setCancelable(true);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog.show();
    }

    private void memberAlreadyExistDialog(){
        //store email successful dialog
        AlertDialog.Builder dialog = new AlertDialog.Builder(AddTripActivity.this);
        dialog.setMessage("Member already exists in the Trip! No need to add it again!");
        dialog.setCancelable(true);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog.show();
    }

    private void updateMemberImage(){
        addMemberLinearLayout.removeAllViews();
        // get the users' email first
        databaseReference.child("Trips").child(uniqueTripId).child("Members")
            .orderByChild("email").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    // Get the email field of each child
                    String email_txt = childSnapshot.child("email").getValue().toString();
                    Log.d("Member List", email_txt);
                    if (email_txt != null){
                        // then get profile pic for every user
                        fetchUserProfilePicByEmail(email_txt);
                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fetchUserProfilePicByEmail(String email) {
        databaseReference.child("User Information").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        // Safely obtain the profile picture URL, check if it's null first
                        Object profilePicObj = child.child("profile_pic").getValue();
                        String profilePicUrl = profilePicObj != null ? profilePicObj.toString() : null;

                        //String profilePicUrl = child.child("profile_pic").getValue().toString();

                        // Check if the URL is null or empty and use a default image
                        if (profilePicUrl == null || profilePicUrl.isEmpty()) {
                            noProfileImg = true;
                            //random text
                            String text = "abc";
                            addImageViewToLayout(text);
                        }
                        else{
                            addImageViewToLayout(profilePicUrl);
                        }

                    }
                } else {
                    Log.d("Firebase", "Email not found in user database");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Error fetching user data", databaseError.toException());
            }
        });
    }

    private void addImageViewToLayout(String imageUrl) {
        ImageView imageView = new ImageView(this);
        imageView.setId(View.generateViewId());
        // Set both width and height to 49dp converted to pixels
        int sizeInPixels = convertDpToPx(49);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(
                sizeInPixels, // Width in pixels
                sizeInPixels  // Height in pixels
        ));
        imageView.setPadding(
                convertDpToPx(5), 0, convertDpToPx(5), 0
        );

        if (noProfileImg){
            Glide.with(this).load(R.drawable.vector).circleCrop().into(imageView);
            noProfileImg = false;
        }
        else{
            // Use Glide or Picasso to load the image from URL
            Glide.with(this)
                    .load(imageUrl).circleCrop()
                    .into(imageView);
        }


        addMemberLinearLayout.addView(imageView);
    }

    private int convertDpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

}




