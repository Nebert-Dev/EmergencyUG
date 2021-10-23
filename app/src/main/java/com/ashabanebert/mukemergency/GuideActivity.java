package com.ashabanebert.mukemergency;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class GuideActivity extends AppCompatActivity {

    Toolbar toolbar;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("App Guide");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        listView = (ListView) findViewById(R.id.guidelist);

        String[] values = new String[]{

                "When you open the App, it will prompt you to turn on gps location on your phone hence the app will not work unless user’s gps location is turned ON\n",
                "Enable internet connection on your phone in order to get a more accurate GPS location \n",
                "Always have a minimum of 300 uganda shillings since the three panic messages sent are charged that amount or less especially if you have an SMS bundle\n ",
                "You can send the default panic message or customize the message that you wish to send to the selected emergency contacts\n",
                "The App will automatically get your current GPS location each time you  open the App in order to provide real time, accurate  location data to the emergency contacts\n",
                "When you finally press the panic button ,a message that is made up of the type of Emergency, the panic message that was pre-set or customized by you and a Uniform Resource Locator (URL) that shows the current location of you .\n ",
                "For your own security, after the message is sent, the app closes itself that is it destroys itself\n ",
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.activity_list_item, android.R.id.text1, values){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view.findViewById(android.R.id.text1);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,14);
                return view;
            }
        };

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {



            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                int itemPosition1 = position;

                String itemValue = (String) listView.getItemAtPosition(position);

                /**Toast.makeText(getApplicationContext(),
                        "Position :" + itemPosition1 + "  ListItem : " + itemValue, Toast.LENGTH_LONG)
                        .show();**/

            }
        });
    }
}
