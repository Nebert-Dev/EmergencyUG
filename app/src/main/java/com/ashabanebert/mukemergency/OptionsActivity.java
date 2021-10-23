package com.ashabanebert.mukemergency;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.wafflecopter.multicontactpicker.ContactResult;
import com.wafflecopter.multicontactpicker.LimitColumn;
import com.wafflecopter.multicontactpicker.MultiContactPicker;

import java.util.List;

public class OptionsActivity extends AppCompatActivity {

    Toolbar toolbar;
    MaterialButton pairedContacts, appGuide, policeDirButton, ambulancesBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Options");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        pairedContacts = findViewById(R.id.pairedContactsBtn);
        pairedContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OptionsActivity.this, ContactsActivity.class));
            }
        });

        appGuide = findViewById(R.id.appGuideButton);
        appGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OptionsActivity.this, GuideActivity.class));
            }
        });

        policeDirButton = findViewById(R.id.policeDirectoryButton);
        policeDirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(OptionsActivity.this, PoliceListActivity.class));
                startActivity(new Intent(OptionsActivity.this, StationsActivity.class));
            }
        });

        ambulancesBtn = findViewById(R.id.ambulanceButton);
        ambulancesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OptionsActivity.this, AmbulancesActivity.class));
            }
        });

    }


}
