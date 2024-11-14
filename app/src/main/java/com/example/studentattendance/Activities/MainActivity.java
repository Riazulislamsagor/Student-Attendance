package com.example.studentattendance.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.studentattendance.Adapters.FragmentAdapter;
import com.example.studentattendance.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
       // getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
       // getSupportActionBar().setCustomView(R.layout.actionbar_layout);

        setContentView(binding.getRoot());




        binding.viewpagerId.setAdapter(new FragmentAdapter(getSupportFragmentManager()));
        binding.tablayoutId.setupWithViewPager(binding.viewpagerId);



    }

}