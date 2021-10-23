package com.ashabanebert.mukemergency.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.ashabanebert.mukemergency.model.Contact;
//creation of the contacts database
@Database(
        entities = {Contact.class},
        version = 1
)
public abstract class ContactDB extends RoomDatabase {
    public abstract ContactDAO contactDAO();

}
