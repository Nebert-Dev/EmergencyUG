package com.ashabanebert.mukemergency;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ashabanebert.mukemergency.fragment.AudioFragment;
import com.ashabanebert.mukemergency.fragment.MapFragment;
import com.ashabanebert.mukemergency.fragment.MediaFragment;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.net.URLEncoder;

public class LiveActivity extends AppCompatActivity {

    Toolbar toolbar;
    MaterialButtonToggleGroup toggleGroup;
    String[] coordinates = {};
    String u = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Uri data = this.getIntent().getData();
        if (data != null && data.isHierarchical()) {
            String uri = this.getIntent().getDataString();
            String loc = data.getQueryParameter("q");
            coordinates = loc.split(",");
            u = data.getQueryParameter("u");
        }

        getSupportActionBar().setTitle("Live Location");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        toggleGroup = findViewById(R.id.toggleGroup);

        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        MapFragment mapFragment = MapFragment.newInstance();
        if(coordinates.length != 0){
            Bundle args = new Bundle();
            args.putStringArray("coordinates", coordinates);
            args.putString("u", u);
            mapFragment.setArguments(args);
            transaction.replace(R.id.content_layout, mapFragment);
            transaction.commit();
        } else {
            transaction.replace(R.id.content_layout, mapFragment);
            transaction.commit();
        }

    }
}
