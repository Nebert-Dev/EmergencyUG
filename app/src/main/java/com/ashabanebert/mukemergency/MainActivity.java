package com.ashabanebert.mukemergency;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Date;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.NetworkError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.ashabanebert.mukemergency.db.ContactDB;
import com.ashabanebert.mukemergency.helper.NetworkSingleton;
import com.ashabanebert.mukemergency.helper.PrefManager;
import com.ashabanebert.mukemergency.model.Contact;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;
import com.wafflecopter.multicontactpicker.ContactResult;
import com.wafflecopter.multicontactpicker.LimitColumn;
import com.wafflecopter.multicontactpicker.MultiContactPicker;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.ashabanebert.mukemergency.helper.Config.MY_SOCKET_TIMEOUT_MS;
import static com.ashabanebert.mukemergency.helper.Config.SQLITE_DATABASE_NAME;
import static com.ashabanebert.mukemergency.helper.Config.TOKEN;
import static com.ashabanebert.mukemergency.helper.Config.UPDATE;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    Button btnOpts;
    PrefManager prefManager;
    ImageView emergency_button;
    TextView em_text;
    ContactDB db;
    MaterialBetterSpinner materialBetterSpinner;
    private static final int REQUEST_CONTACT_PERMISSION = 20;
    private static final int CONTACT_PICKER_REQUEST = 21;
    private static final int REQUEST_SEND_SMS_PERMISSION = 22;

    private ArrayList<ContactResult> results = new ArrayList<>();
    ImageView gpsImage;
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_ACCESS_FINE_LOCATION = 5445;
    List<Contact> contacts = new ArrayList<>();
    String[] emergencies = {"GENERAL EMERGENCY","ROBBERY","MURDER","ACCIDENT","CHILD ABUSE","DOMESTIC VIOLENCE","RAPE","MEDICAL EMERGENCY"};
    String emergency = "KIDNAP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefManager = new PrefManager(this);
        if(prefManager.getUserid() == 0){
            int userId = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
            prefManager.setUserid(userId);
        }

        if(prefManager.getUid() == 0){
            postUserId(prefManager.getUserid());
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Emergency EA");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);
        gpsImage = findViewById(R.id.icGps);

        db = Room.databaseBuilder(this, ContactDB.class, SQLITE_DATABASE_NAME).fallbackToDestructiveMigration().build();

        try {
            contacts = new getAllSavedContacts().execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        materialBetterSpinner = findViewById(R.id.material_spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, emergencies);

        materialBetterSpinner.setAdapter(adapter);
        materialBetterSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                emergency = parent.getItemAtPosition(position).toString();
            }
        });

        emergency_button = findViewById(R.id.img);
        em_text = findViewById(R.id.emText);
        emergency_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.SEND_SMS) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.SEND_SMS},
                            REQUEST_SEND_SMS_PERMISSION);
                } else {
                    sendMessages(emergency);
                }
            }
        });



        btnOpts = findViewById(R.id.btnOpts);
        btnOpts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent options = new Intent(MainActivity.this, OptionsActivity.class);
                startActivity(options);
            }
        });
    }


    private final LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult.getLastLocation() == null)
                return;
            currentLocation = locationResult.getLastLocation();
            shareLocation(currentLocation);
        }
    };

    private void shareLocation(Location currentLocation) {

        JSONObject theobj = new JSONObject();

        try {
            theobj.put("user", prefManager.getUserid());
            theobj.put("id", (prefManager.getUid()) == 0 ? null : prefManager.getUid());
            theobj.put("lat", currentLocation.getLatitude());
            theobj.put("lng", currentLocation.getLongitude());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, UPDATE, theobj, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int code = response.getInt("code");
                            if(code == 200){
                                Log.d("ZZZZZ", "Location updated");
                                gpsImage.setVisibility(View.VISIBLE);
                                Thread thread = new Thread() {
                                    @Override
                                    public void run() {
                                        try {
                                            Thread.sleep(2000);
                                        } catch (InterruptedException e) {
                                        }

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                gpsImage.setVisibility(View.INVISIBLE);
                                            }
                                        });
                                    }
                                };
                                thread.start();
                            } else {
                                Log.d("ZZZZZ", "Location update failed");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        if(error instanceof NetworkError){

                        }

                    }
                });

        NetworkSingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

    }

    private void postUserId(final int id) {

        JSONObject theobj = new JSONObject();

        try {
            theobj.put("userId", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, TOKEN, theobj, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            int code = response.getInt("code");
                            if(code == 200){
                                //prefManager.setUserToken(token);
                                prefManager.setUid(response.getInt("id"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });


        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        NetworkSingleton.getInstance(MainActivity.this).addToRequestQueue(jsObjRequest);

    }

    private void startCurrentLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(3000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_ACCESS_FINE_LOCATION);
                return;
            }
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
    }

    public class getAllSavedContacts extends AsyncTask<Void, Void, List<Contact>> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected List<Contact> doInBackground(Void... voids) {
            List<Contact> contacts = db.contactDAO().getallContacts();
            return contacts;
        }

        @Override
        protected void onPostExecute(List<Contact> contacts) {
            super.onPostExecute(contacts);
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        if (isGooglePlayServicesAvailable()) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            startCurrentLocationUpdates();
        }

        contacts.clear();
        try {
            contacts = new getAllSavedContacts().execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status)
            return true;
        else {
            if (googleApiAvailability.isUserResolvableError(status))
                Toast.makeText(this, "Please Install google play services to use this application", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CONTACT_PERMISSION){
            for(int i = 0; i < permissions.length; i++){
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (permission.equals(Manifest.permission.READ_CONTACTS)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        showContactsPicker();
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.READ_CONTACTS},
                                REQUEST_CONTACT_PERMISSION);
                    }
                }
            }
        } else if(requestCode == REQUEST_SEND_SMS_PERMISSION){
            if (permissions[0].equalsIgnoreCase
                    (Manifest.permission.SEND_SMS)
                    && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                 sendMessages(emergency);
            } else {
                Toast.makeText(this, "You need to allow this permission to enable the app to send SMS",
                        Toast.LENGTH_LONG).show();
            }
        } else if(requestCode == REQUEST_ACCESS_FINE_LOCATION){
            if (permissions[0].equalsIgnoreCase
                    (Manifest.permission.ACCESS_FINE_LOCATION)
                    && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                startCurrentLocationUpdates();
            } else {
                Toast.makeText(this, "You need to allow this permission to enable the app to send location updates",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public void showContactsPicker(){
        new MultiContactPicker.Builder(MainActivity.this)
                .theme(R.style.MyCustomPickerTheme)
                .hideScrollbar(false)
                .showTrack(true)
                .searchIconColor(Color.WHITE)
                .setChoiceMode(MultiContactPicker.CHOICE_MODE_MULTIPLE)
                .handleColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary))
                .bubbleColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary))
                .bubbleTextColor(Color.WHITE)
                .setTitleText("Pair with Contacts")
                .setLoadingType(MultiContactPicker.LOAD_ASYNC)
                .limitToColumn(LimitColumn.NONE)
                .setActivityAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                        android.R.anim.fade_in,
                        android.R.anim.fade_out)
                .showPickerForResult(CONTACT_PICKER_REQUEST);
    }

    public void sendMessages(String em){
        if(currentLocation == null){
            final Snackbar snackbar = Snackbar
                    .make(em_text, "No Location, Turn On GPS", Snackbar.LENGTH_INDEFINITE)
                    .setAction("TURN ON", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    });
            snackbar.setAnchorView(R.id.btnOpts);
            snackbar.show();

            return;
        }

        int user = (prefManager.getUid() == 0) ? prefManager.getUserid() : prefManager.getUid();

        String message =  "Am in danger ["+em+"] and in need of immediate help at this location http://maps.google.com/?q=" + currentLocation.getLatitude() +","+ currentLocation.getLongitude()+"&u="+user;

        SmsManager smsManager = SmsManager.getDefault();
        StringBuffer smsBody = new StringBuffer();
        smsBody.append(Uri.parse(message));
        if(contacts.size() > 0){
            for(Contact contact : contacts){

                String phoneNumber = contact.getPhone();android.telephony.SmsManager.getDefault().sendTextMessage(phoneNumber, null, smsBody.toString(), null, null);

            }

            String notify = "";
            if(contacts.size() == 1){
                notify = "Distress Message has been sent";
            } else {
                notify = "Distress Messages have been sent";
            }

            final Snackbar snackbar = Snackbar
                    .make(em_text, notify, Snackbar.LENGTH_SHORT)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
            snackbar.setAnchorView(R.id.btnOpts);
            snackbar.show();

        } else {
            final Snackbar snackbar = Snackbar
                    .make(em_text, "You have no paired contacts", Snackbar.LENGTH_INDEFINITE)
                    .setAction("PAIR NOW", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (ContextCompat
                                    .checkSelfPermission(MainActivity.this,
                                            Manifest.permission.READ_CONTACTS)
                                    != PackageManager.PERMISSION_GRANTED) {
                                if (
                                        ActivityCompat.shouldShowRequestPermissionRationale
                                                (MainActivity.this, Manifest.permission.READ_CONTACTS)) {
                                    Snackbar.make(findViewById(android.R.id.content),
                                            "Please Grant Permissions",
                                            Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                                            new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    ActivityCompat.requestPermissions(MainActivity.this,
                                                            new String[]{Manifest.permission.READ_CONTACTS},
                                                            REQUEST_CONTACT_PERMISSION);
                                                }
                                            }).show();
                                } else {
                                    ActivityCompat.requestPermissions(MainActivity.this,
                                            new String[]{Manifest.permission.READ_CONTACTS},
                                            REQUEST_CONTACT_PERMISSION);
                                }
                            } else {
                                showContactsPicker();

                            }

                        }
                    });
            snackbar.setAnchorView(R.id.btnOpts);
            snackbar.show();
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CONTACT_PICKER_REQUEST){
            if(resultCode == RESULT_OK) {
                results.addAll(MultiContactPicker.obtainResult(data));
                if(results.size() > 0) {
                    Log.d("EMERGENCY_UG", results.get(0).getPhoneNumbers().get(0).getNumber());
                    for(ContactResult contactResult : results){
                        final Contact contact = new Contact();
                        contact.setName(contactResult.getDisplayName());
                        contact.setPhone(contactResult.getPhoneNumbers().get(0).getNumber());

                        boolean exists = false;
                        if(contacts.size() > 0){
                            for(Contact con : contacts){
                                if(con.phone.equalsIgnoreCase(contactResult.getPhoneNumbers().get(0).getNumber())){
                                    exists = true;
                                }
                            }

                            if(exists){
                                Toast.makeText(MainActivity.this, "You already paired with "+contactResult.getDisplayName(), Toast.LENGTH_LONG).show();
                            } else {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        db.contactDAO().addContact(contact);
                                    }
                                }).start();
                                contacts.add(contact);
                                Toast.makeText(MainActivity.this, "Pairing Complete", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    db.contactDAO().addContact(contact);
                                }
                            }).start();
                            contacts.add(contact);
                            Toast.makeText(MainActivity.this, "Pairing Complete", Toast.LENGTH_SHORT).show();

                        }

                    }

                }
            } else if(resultCode == RESULT_CANCELED){
                if(contacts.size() == 0){
                    Toast.makeText(MainActivity.this, "You did not select any contacts to pair with", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
