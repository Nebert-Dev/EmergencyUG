package com.ashabanebert.mukemergency;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ashabanebert.mukemergency.fragment.AudioFragment;
import com.ashabanebert.mukemergency.fragment.ContactMapFragment;
import com.ashabanebert.mukemergency.fragment.MapFragment;
import com.ashabanebert.mukemergency.fragment.MediaFragment;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.net.URLEncoder;

public class ContactActivity extends AppCompatActivity {

    Toolbar toolbar;
    MaterialButtonToggleGroup toggleGroup;
    String u = "";
    Bundle args;
    String name, phone;
    int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        args = getIntent().getExtras();
        name = args.getString("name");
        phone = args.getString("phone");
        id = args.getInt("userId");


        getSupportActionBar().setTitle(name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        toggleGroup = findViewById(R.id.toggleGroup);


        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        ContactMapFragment contactMapFragment = ContactMapFragment.newInstance();

        Bundle args = new Bundle();
        args.putInt("u", id);
        contactMapFragment.setArguments(args);
        transaction.replace(R.id.content_layout, contactMapFragment);
        transaction.commit();
    }

}
