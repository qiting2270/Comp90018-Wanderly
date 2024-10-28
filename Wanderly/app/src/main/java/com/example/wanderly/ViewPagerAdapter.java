package com.example.wanderly;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {

    Context context;
    List<HomeSchedule> scheduleList;
    LayoutInflater inflater;

    int[] day = {
        R.string.today,
        R.string.tomorrow,
        R.string.day_after
    };

    public ViewPagerAdapter (Context context, List<HomeSchedule> scheduleList) {
        this.context = context;
        this.scheduleList = scheduleList;
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
        ImageView schedule_icon = (ImageView) view.findViewById(R.id.imageView34);
        TextView schedule_name = (TextView) view.findViewById(R.id.day_details_name);
        TextView schedule_time = (TextView) view.findViewById(R.id.day_details_time);

        TextView slideDay = (TextView) view.findViewById(R.id.home_day);
//        TextView slideDescription = (TextView) view.findViewById(R.id.home_description);

        slideDay.setText(day[position]);
//        slideDay.setText(description[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }
}

