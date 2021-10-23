package com.ashabanebert.mukemergency;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.ashabanebert.mukemergency.adapter.ContactAdapter;
import com.ashabanebert.mukemergency.db.ContactDB;
import com.ashabanebert.mukemergency.helper.NetworkSingleton;
import com.ashabanebert.mukemergency.model.Contact;
import com.ashabanebert.mukemergency.model.EditedContact;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.wafflecopter.multicontactpicker.ContactResult;
import com.wafflecopter.multicontactpicker.LimitColumn;
import com.wafflecopter.multicontactpicker.MultiContactPicker;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.ashabanebert.mukemergency.helper.Config.MY_SOCKET_TIMEOUT_MS;
import static com.ashabanebert.mukemergency.helper.Config.SQLITE_DATABASE_NAME;
import static com.ashabanebert.mukemergency.helper.Config.SYNC_USER;
import static com.ashabanebert.mukemergency.helper.Config.TOKEN;

public class ContactsActivity extends AppCompatActivity {

    Toolbar toolbar;
    FloatingActionButton fab;
    ContactDB db;
    private static final int CONTACT_PICKER_REQUEST = 991;
    private static final int REQUEST_CONTACT_PERMISSION = 20;

    private ArrayList<ContactResult> results = new ArrayList<>();
    RecyclerView recyclerView, recyclerViewEm, recyclerViewTwo, recyclerViewThree;
    ProgressBar progressBar;
    List<Contact> contactList = new ArrayList<>();
    List<Contact> emList = new ArrayList<>();
    List<Contact> hospList = new ArrayList<>();
    List<Contact> ambList = new ArrayList<>();
    ContactAdapter contactAdapter, emAdapter, hospAdapter, ambAdapter;
    LinearLayoutManager layoutManager;
    RelativeLayout relativeLayout;
    HashMap<String, String> pairs = new HashMap<>();
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        progressDialog = new ProgressDialog(this);

        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }

        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progressBar);
        relativeLayout = findViewById(R.id.relContacts);

        contactAdapter = new ContactAdapter(ContactsActivity.this, contactList);
        recyclerView.setAdapter(contactAdapter);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        recyclerViewEm = findViewById(R.id.recycler_view_one);
        emAdapter = new ContactAdapter(ContactsActivity.this, emList);
        recyclerViewEm.setAdapter(emAdapter);
        recyclerViewEm.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewEm.setItemAnimator(new DefaultItemAnimator());
        recyclerViewEm.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        Contact police = new Contact();
        police.setName("Police Uganda");
        police.setPhone("999");
        emList.add(police);
        emAdapter.notifyDataSetChanged();

        Contact loc = new Contact();
        loc.setName("Hospital");
        loc.setPhone("0414444444");
        recyclerViewTwo = findViewById(R.id.recycler_view_two);
        hospAdapter = new ContactAdapter(ContactsActivity.this, hospList);
        recyclerViewTwo.setAdapter(hospAdapter);
        recyclerViewTwo.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTwo.setItemAnimator(new DefaultItemAnimator());
        recyclerViewTwo.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        hospList.add(loc);
        hospAdapter.notifyDataSetChanged();

        Contact ambulance = new Contact();
        ambulance.setName("Ambulance");
        ambulance.setPhone("222");
        recyclerViewThree = findViewById(R.id.recycler_view_three);
        ambAdapter = new ContactAdapter(ContactsActivity.this, ambList);
        recyclerViewThree.setAdapter(ambAdapter);
        recyclerViewThree.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewThree.setItemAnimator(new DefaultItemAnimator());
        recyclerViewThree.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        ambList.add(ambulance);
        ambAdapter.notifyDataSetChanged();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Paired Contacts");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = Room.databaseBuilder(this, ContactDB.class, SQLITE_DATABASE_NAME).fallbackToDestructiveMigration().build();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        fetchContacts();

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat
                        .checkSelfPermission(ContactsActivity.this,
                                Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (
                            ActivityCompat.shouldShowRequestPermissionRationale
                                    (ContactsActivity.this, Manifest.permission.READ_CONTACTS)) {
                        Snackbar.make(findViewById(android.R.id.content),
                                "Please Grant Permissions",
                                Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ActivityCompat.requestPermissions(ContactsActivity.this,
                                                new String[]{Manifest.permission.READ_CONTACTS},
                                                REQUEST_CONTACT_PERMISSION);
                                    }
                                }).show();
                    } else {
                        ActivityCompat.requestPermissions(ContactsActivity.this,
                                new String[]{Manifest.permission.READ_CONTACTS},
                                REQUEST_CONTACT_PERMISSION);
                    }
                } else {
                    showContactsPicker();

                }


            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CONTACT_PICKER_REQUEST){
            if(resultCode == RESULT_OK) {
                results.addAll(MultiContactPicker.obtainResult(data));
                if(results.size() > 0) {
                    Log.d("MyTag", results.get(0).getPhoneNumbers().get(0).getNumber());
                    for(ContactResult contactResult : results){
                        final Contact contact = new Contact();
                        contact.setName(contactResult.getDisplayName());
                        contact.setPhone(contactResult.getPhoneNumbers().get(0).getNumber());

                        boolean exists = false;
                        if(contactList.size() > 0){
                            for(Contact con : contactList){
                                if(con.phone.equalsIgnoreCase(contactResult.getPhoneNumbers().get(0).getNumber())){
                                    exists = true;
                                }
                            }

                            if(exists){
                                //Toast.makeText(ContactsActivity.this, "You already paired with "+contactResult.getDisplayName(), Toast.LENGTH_LONG).show();
                            } else {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        db.contactDAO().addContact(contact);
                                    }
                                }).start();
                                contactList.add(contact);
                            }
                        } else {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    db.contactDAO().addContact(contact);
                                }
                            }).start();
                            contactList.add(contact);
                        }

                    }

                    contactAdapter.setItems(contactList);
                    contactAdapter.notifyDataSetChanged();

                }
            } else if(resultCode == RESULT_CANCELED){
                if(contactList.size() == 0){
                    Toast.makeText(ContactsActivity.this, "You did not select any contacts to pair with", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void fetchContacts() {



        try {
            contactList = new getAllSavedContacts().execute().get();
            progressBar.setVisibility(View.INVISIBLE);
            contactAdapter = new ContactAdapter(ContactsActivity.this, contactList);
            recyclerView.setAdapter(contactAdapter);
            layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

            if(contactList.size() == 0){

                Snackbar snackbar = Snackbar
                        .make(relativeLayout, "You have no paired contacts", Snackbar.LENGTH_LONG)
                        .setAction("PAIR NOW", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (ContextCompat
                                        .checkSelfPermission(ContactsActivity.this,
                                                Manifest.permission.READ_CONTACTS)
                                        != PackageManager.PERMISSION_GRANTED) {
                                    if (
                                            ActivityCompat.shouldShowRequestPermissionRationale
                                                    (ContactsActivity.this, Manifest.permission.READ_CONTACTS)) {
                                        Snackbar.make(findViewById(android.R.id.content),
                                                "Please Grant Permissions",
                                                Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                                                new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        ActivityCompat.requestPermissions(ContactsActivity.this,
                                                                new String[]{Manifest.permission.READ_CONTACTS},
                                                                REQUEST_CONTACT_PERMISSION);
                                                    }
                                                }).show();
                                    } else {
                                        ActivityCompat.requestPermissions(ContactsActivity.this,
                                                new String[]{Manifest.permission.READ_CONTACTS},
                                                REQUEST_CONTACT_PERMISSION);
                                    }
                                } else {
                                    showContactsPicker();

                                }
                            }
                        });
                snackbar.show();
                progressBar.setVisibility(View.INVISIBLE);

            }


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onEvent(EditedContact editedContact) {
        contactList.set(editedContact.getIndex(), editedContact.getContact());
        contactAdapter.notifyItemChanged(editedContact.getIndex());
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

    public  void sync(final Contact c, final int index){
        JSONObject theobj = new JSONObject();

        try {
            theobj.put("name", c.getName());
            theobj.put("phone", c.getPhone());
            theobj.put("pair_id", "1605648571");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, SYNC_USER, theobj, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            if(response.getInt("code") == 200){
                                c.setGlobalId(response.getJSONObject("user").getInt("id"));
                                contactList.set(index, c);
                                contactAdapter.notifyItemChanged(index);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        db.contactDAO().updateConatct(c);
                                    }
                                }).start();

                                Toast.makeText(ContactsActivity.this, "Sync Complete", Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ContactsActivity.this, "Errr", Toast.LENGTH_LONG).show();
                    }
                });


        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        NetworkSingleton.getInstance(ContactsActivity.this).addToRequestQueue(jsObjRequest);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contacts, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_sync) {
            Toast.makeText(ContactsActivity.this, "Syncing", Toast.LENGTH_SHORT).show();
            if(contactList.size() > 0){
                int j = 0;
                for(Contact c: contactList){
                    if(c.getGlobalId() == 0){
                        sync(c, j);
                    }
                    j++;
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void showContactsPicker(){
        new MultiContactPicker.Builder(ContactsActivity.this)
                .theme(R.style.MyCustomPickerTheme)
                .hideScrollbar(false)
                .showTrack(true)
                .searchIconColor(Color.WHITE)
                .setChoiceMode(MultiContactPicker.CHOICE_MODE_MULTIPLE)
                .handleColor(ContextCompat.getColor(ContactsActivity.this, R.color.colorPrimary))
                .bubbleColor(ContextCompat.getColor(ContactsActivity.this, R.color.colorPrimary))
                .bubbleTextColor(Color.WHITE)
                .setTitleText("Pair with Contacts")
                .setLoadingType(MultiContactPicker.LOAD_ASYNC)
                .limitToColumn(LimitColumn.NONE)
                .setActivityAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                        android.R.anim.fade_in,
                        android.R.anim.fade_out)
                .showPickerForResult(CONTACT_PICKER_REQUEST);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CONTACT_PERMISSION) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (permission.equals(Manifest.permission.READ_CONTACTS)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        showContactsPicker();
                    } else {
                        ActivityCompat.requestPermissions(ContactsActivity.this,
                                new String[]{Manifest.permission.READ_CONTACTS},
                                REQUEST_CONTACT_PERMISSION);
                    }
                }
            }
        }
    }



}
