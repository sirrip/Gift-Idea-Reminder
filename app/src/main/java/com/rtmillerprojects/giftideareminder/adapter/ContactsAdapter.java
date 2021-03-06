package com.rtmillerprojects.giftideareminder.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rtmillerprojects.giftideareminder.R;
import com.rtmillerprojects.giftideareminder.model.Contact;

import java.util.ArrayList;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Ryan on 5/29/2016.
 */
public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> {
    private ArrayList<Contact> contacts;
    private Context context;


    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.name) TextView name;
        @Bind(R.id.relationship) TextView relationship;
        @Bind(R.id.profile_photo) ImageView profilePhoto;

        public ContactViewHolder(View v) {
            super(v);
            profilePhoto = (ImageView) v.findViewById(R.id.profile_photo);
            ButterKnife.bind(this, v);
        }
    }

    public ContactsAdapter(ArrayList<Contact> contacts, Context context) {
        this.contacts = contacts;
        this.context = context;
    }

    @Override public ContactsAdapter.ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_contact, parent, false);
        ContactViewHolder vh = new ContactViewHolder(v);
        return vh;
    }

    @Override public void onBindViewHolder(ContactViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        holder.name.setText(contact.getName());
        holder.relationship.setText(contact.getRelationship());
        if (contact.getProfilePhoto()==null) {
            Drawable d = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_person_grey600_24dp, null);
            holder.profilePhoto.setImageDrawable(d);
        } else {
            holder.profilePhoto.setImageBitmap(contact.getProfilePhoto());
        }
    }

    @Override public int getItemCount() {
        return contacts.size();
    }
}