package com.ashabanebert.mukemergency;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.room.Room;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ashabanebert.mukemergency.db.ContactDB;
import com.ashabanebert.mukemergency.model.Contact;
import com.ashabanebert.mukemergency.model.EditedContact;
import com.google.android.material.textfield.TextInputEditText;

import org.greenrobot.eventbus.EventBus;

import static com.ashabanebert.mukemergency.helper.Config.SQLITE_DATABASE_NAME;

public class EditContact extends AppCompatActivity {

    Toolbar toolbar;
    String name, phone;
    int id, position;
    TextInputEditText nameET, phoneET;
    Button updateBtn;
    ContactDB db;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);

        progressDialog = new ProgressDialog(this);

        db = Room.databaseBuilder(this, ContactDB.class, SQLITE_DATABASE_NAME).fallbackToDestructiveMigration().build();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Edit Paired Contact");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        name = getIntent().getExtras().getString("name");
        phone = getIntent().getExtras().getString("number");
        id = getIntent().getExtras().getInt("id");
        position = getIntent().getExtras().getInt("pos");

        nameET = findViewById(R.id.nameET);
        phoneET = findViewById(R.id.phoneET);

        nameET.setText(name);
        phoneET.setText(phone);

        updateBtn = findViewById(R.id.btnUpdate);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ename = nameET.getText().toString();
                String ephone = phoneET.getText().toString();
                if(ename.isEmpty() || ephone.isEmpty()){
                    Toast.makeText(EditContact.this, "All Fields are required", Toast.LENGTH_LONG).show();
                } else {
                    Contact contact = new Contact();
                    contact.setPhone(ephone);
                    contact.setName(ename);
                    contact.setId(id);
                    EditedContact editedContact = new EditedContact();
                    editedContact.setContact(contact);
                    editedContact.setIndex(position);
                    EventBus.getDefault().post(editedContact);
                    new UpdateContact(contact).execute();
                }
            }
        });
    }

    public class UpdateContact extends AsyncTask<Void, Void, Void> {

        Contact contact;

        public UpdateContact(Contact contact){
            this.contact = contact;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            db.contactDAO().updateConatct(contact);
            return null;
        }

        @Override
        protected void onPostExecute(Void avoid) {
            super.onPostExecute(avoid);
            progressDialog.dismiss();
            Toast.makeText(EditContact.this, "Contact Info Updated", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
