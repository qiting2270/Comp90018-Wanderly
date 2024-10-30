package com.example.wanderly;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {

    Context context;
    LayoutInflater inflater;

    List<String> day1List;
    List<String> day2List;
    List<String> day3List;

    int[] day = {
        R.string.today,
        R.string.tomorrow,
        R.string.day_after
    };

    public ViewPagerAdapter (Context context, List<String> day1List, List<String> day2List, List<String> day3List) {
        this.context = context;
        this.day1List = day1List;
        this.day2List = day2List;
        this.day3List = day3List;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return day.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (RelativeLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slider_layout, container, false);
        TextView noPlan = view.findViewById(R.id.day_details_none);

        LinearLayout sliderLayout = view.findViewById(R.id.slider_linearlayout);

        // first page
        if (position == 0){
            if (!day1List.isEmpty()) {
                noPlan.setVisibility(View.GONE);
                insertData(container, sliderLayout, day1List);
            }
            else{
                noPlan.setVisibility(View.VISIBLE);
            }
        }
        // second page
        else if (position == 1){
            if (!day2List.isEmpty()) {
                noPlan.setVisibility(View.GONE);
                insertData(container, sliderLayout, day2List);
            }
            else{
                noPlan.setVisibility(View.VISIBLE);
            }
        }
        // third page
        if (position == 2){
            if (!day3List.isEmpty()) {
                noPlan.setVisibility(View.GONE);
                insertData(container, sliderLayout, day3List);
            }
            else{
                noPlan.setVisibility(View.VISIBLE);
            }
        }



        TextView slideDay = (TextView) view.findViewById(R.id.home_day);
        slideDay.setText(day[position]);


        //HomeSchedule schedule = scheduleList.get(position); // Ensure this does not throw IndexOutOfBoundsException


        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }

    private void insertData(ViewGroup container, LinearLayout sliderLayout, List<String> dayList){
        for (String data: dayList){
            // Split the entry into the place name and the time part
            String[] parts = data.split(" from ");
            String placeName = parts[0]; // "Billy's Central"
            String timeRange = parts[1]; // "1 AM - 3 AM"

            View itemView = inflater.inflate(R.layout.slider_inside_layout, container, false);
            TextView dayDetailsName = itemView.findViewById(R.id.day_details_name);
            TextView dayDetailsTime = itemView.findViewById(R.id.day_details_time);
            ImageView imageView = itemView.findViewById(R.id.imageView34);

            dayDetailsName.setText(placeName);
            dayDetailsTime.setText(timeRange);
            // Set the image if needed
            imageView.setImageResource(R.drawable.home_desc);

            sliderLayout.addView(itemView);  // Add the dynamically created itemView to the sliderLayout
        }
    }
}

