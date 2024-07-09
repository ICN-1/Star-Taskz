package com.theteam.taskz;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.theteam.taskz.adapters.ViewPagerAdapter;
import com.theteam.taskz.data.ViewPagerDataHolder;

import java.util.ArrayList;


public class RegisterActivity extends AppCompatActivity {

    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager2 viewPager;

    // Inorder to specify the views we want to show
    private final ArrayList<Fragment> views = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // We're only using NameSection and EmailSection
        views.add(new NameSection());
        views.add(new CategorySection());
        views.add(new EmailSection());

        viewPager = (ViewPager2) findViewById(R.id.view_pager);
        viewPagerAdapter = new ViewPagerAdapter(this, views);
        viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        viewPager.setUserInputEnabled(false);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setPageTransformer( new CustomPageTransformer());

        ViewPagerDataHolder.viewPager = viewPager;

    }
}