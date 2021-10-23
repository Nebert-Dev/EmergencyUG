package com.ashabanebert.mukemergency.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.ashabanebert.mukemergency.model.Contact;

import java.util.List;
//Database Access Objects for the database
@Dao
public interface ContactDAO {
    @Query("SELECT * FROM contact")
    List<Contact> getallContacts();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addContact(Contact contact);

    @Query("DELETE FROM contact")
    void deleteAllContacts();

    @Delete
    void deleteContact (Contact contact);

    @Update
    void updateConatct (Contact contact);

    @Query("UPDATE contact SET globalId=:globalId WHERE phone = :phone")
    void updateByPhone(int globalId, String phone);

}