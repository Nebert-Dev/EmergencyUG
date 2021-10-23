package com.ashabanebert.mukemergency.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ashabanebert.mukemergency.ContactActivity;
import com.ashabanebert.mukemergency.ContactsActivity;
import com.ashabanebert.mukemergency.EditContact;
import com.ashabanebert.mukemergency.LiveActivity;
import com.ashabanebert.mukemergency.R;
import com.ashabanebert.mukemergency.db.ContactDB;
import com.ashabanebert.mukemergency.model.Contact;

import java.util.List;

import static com.ashabanebert.mukemergency.helper.Config.SQLITE_DATABASE_NAME;


public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyViewHolder> {
    private Context context;
    private List<Contact> contactList;
    ProgressDialog pd;
    ContactDB db;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, phone, icon;
        ImageView imgProfile;
        RelativeLayout viewForeGround;
        ImageView opts;
        LinearLayout viewContact;


        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            phone = view.findViewById(R.id.phone);
            icon = view.findViewById(R.id.icon_text);
            imgProfile = view.findViewById(R.id.icon_profile);
            viewForeGround = view.findViewById(R.id.icon_front);
            viewContact = view.findViewById(R.id.lContact);
            opts = view.findViewById(R.id.ovf);
        }
    }


    public ContactAdapter(Context context, List<Contact> contactList) {
        this.context = context;
        this.contactList = contactList;

        db = Room.databaseBuilder(context,
               ContactDB.class, SQLITE_DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final Contact contact = contactList.get(position);
        holder.name.setText(contact.getName());
        holder.icon.setText(contact.getName().substring(0, 1));
        holder.phone.setText(contact.getPhone());
        holder.imgProfile.setImageResource(R.drawable.bg_circle);
        holder.imgProfile.setColorFilter(getRandomColor("400"));

        holder.opts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(holder.opts, contact, position);
            }
        });

        holder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(contact.getGlobalId() == 0){
                    Toast.makeText(holder.icon.getContext(), "Contact not synced, Please sync to view", Toast.LENGTH_SHORT).show();
                } else {
                    Intent view = new Intent(context, ContactActivity.class);
                    Bundle args = new Bundle();
                    args.putString("name", contact.getName());
                    args.putInt("userId", contact.getGlobalId());
                    args.putInt("id", contact.getId());
                    args.putString("phone", contact.getPhone());
                    view.putExtras(args);
                    context.startActivity(view);
                }

            }
        });

        holder.viewContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.icon.performClick();
            }
        });

        holder.viewForeGround.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.icon.performClick();
            }
        });

    }

    private void showPopupMenu(View view, Contact contact, int position) {

        PopupMenu popup = new PopupMenu(context, view, Gravity.RIGHT);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_contact, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(contact, position));
        popup.show();

    }

    private int getRandomColor(String typeColor) {
        int returnColor = Color.GRAY;
        int arrayId = context.getResources().getIdentifier("mdcolor_" + typeColor, "array", context.getPackageName());

        if (arrayId != 0) {
            TypedArray colors = context.getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.GRAY);
            colors.recycle();
        }
        return returnColor;
    }


    public void setItems(List<Contact> contacts) {
        this.contactList = contacts;
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    private class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        Contact selectedContact;
        int position;

        public MyMenuItemClickListener(Contact contact, int pos) {
            this.selectedContact = contact;
            this.position = pos;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {

                case R.id.action_edit:
                    Bundle args = new Bundle();
                    args.putString("name", selectedContact.getName());
                    args.putString("number", selectedContact.getPhone());
                    args.putInt("id", selectedContact.getId());
                    args.putInt("pos", position);
                    Intent edit = new Intent(context, EditContact.class);
                    edit.putExtras(args);
                    context.startActivity(edit);
                    return true;
                case R.id.action_delete:
                    new MaterialDialog.Builder(context)
                            .title("Unpair with this contact")
                            .content("This contact will no longer receive Emergency texts from you.")
                            .positiveText("UNPAIR")
                            .negativeText("CANCEL")
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();
                                }
                            })
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    doDelete(selectedContact, position);
                                }
                            })
                            .show();
                    //doDelete(selectedEvent.getId());
                    return true;
                default:
            }
            return false;
        }
    }

    private void doDelete(final Contact contact, int position) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                db.contactDAO().deleteContact(contact);
            }
        }) .start();

        contactList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,contactList.size());

    }


}