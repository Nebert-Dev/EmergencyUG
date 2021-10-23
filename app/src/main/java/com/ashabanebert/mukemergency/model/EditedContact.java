package com.ashabanebert.mukemergency.model;

public class EditedContact {

    public EditedContact(){

    }

    int index;
    Contact contact;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }
}
