package com.rtmillerprojects.giftideareminder.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.rtmillerprojects.giftideareminder.R;
import com.rtmillerprojects.giftideareminder.adapter.TagAdapter;
import com.rtmillerprojects.giftideareminder.model.Contact;
import com.rtmillerprojects.giftideareminder.util.DatabaseHelper;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Ryan on 6/11/2016.
 */
public class TagDialog extends DialogFragment{

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ArrayList<Contact> allContacts;
    private ArrayList<Contact> selectedContacts;
    private ArrayList<NameValueCheck> tagPairs;
    private ArrayList<NameValueCheck> selectedTagPairs;
    private DatabaseHelper db;
    private String str;
    private long recordId;
    private String tagType;
    private ArrayList<Integer> selectedTagIds;
    public TagDialog(){/*required public constructor*/}
    @SuppressLint("ValidFragment")
    public TagDialog(String tagType, long recordId){
        this.tagType = tagType;
        this.recordId = recordId;
    };
    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.edit_tag_list, null);
        recyclerView = (RecyclerView) dialogLayout.findViewById(R.id.recycler_view_tags);

        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        //ABSTRACT THIS STUFF
        db = DatabaseHelper.getInstance(getActivity());
        tagPairs = new ArrayList<>();
        if(tagType == "contactTagForEvent"){
            allContacts = db.getAllContacts();
            selectedContacts = db.getContactsTagsForEvent(recordId);
            //Compare against selected list
            for (Contact contact : allContacts) {
                NameValueCheck tempNVC = new NameValueCheck(contact.getName(), (int) contact.getId());
                for(Contact selContact : selectedContacts) {
                    if(contact.getId() == selContact.getId()) {
                        tempNVC.isChecked=true;
                        break;
                    }
                }
                tagPairs.add(tempNVC);
            }

        }

        //FINISH ABSTRACTING STUFF
        final TagAdapter tagAdapter = new TagAdapter(tagPairs, getActivity()); //Maybe need to pass in a bunch of contact names here?
        recyclerView.setAdapter(tagAdapter);
        recyclerView.setLayoutManager(layoutManager);
        str = "";


        builder .setView(dialogLayout)
                //.setMessage("This is my dialog")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        View view = getView();
                        //RecyclerView recyclerView = (RecyclerView) (view != null ? view.findViewById(R.id.recycler_view_tags) : null);
                        int count = recyclerView.getAdapter().getItemCount();
                        CheckBox chkbox;
                        selectedTagIds = new ArrayList<>();
                        for(int i=0;i<count;i++){
                            chkbox = (CheckBox) recyclerView.findViewHolderForAdapterPosition(i).itemView.findViewById(R.id.tagCheckBox);
                            if(chkbox.isChecked()) {
                                selectedTagIds.add(tagAdapter.getTagPair(i).value);
                                str = str + tagAdapter.getTagPair(i).value+ ", ";
                            }
                        }
                        str=str.substring(0,str.length()-2);
                        Toast.makeText(getActivity(),str,Toast.LENGTH_SHORT).show();
                        if(tagType=="contactTagForEvent"){
                            db.deleteContactTagsForEvent(recordId);
                            db.insertContactTagsForEvent(recordId, selectedTagIds);
                        }
                        if(tagType=="contactTagForGift"){
                            db.deleteContactTagsForGift(recordId);
                            db.insertContactTagsForGift(recordId, selectedTagIds);
                        }
                        if(tagType=="eventTagForContact"){
                            db.deleteEventTagsForContact(recordId);
                            db.insertEventTagsForContact(recordId, selectedTagIds);
                        }
                        if(tagType=="eventTagForGift"){
                            db.deleteEventTagsForGift(recordId);
                            db.insertEventTagsForGift(recordId, selectedTagIds);
                        }
                        if(tagType=="giftTagForContact"){
                            db.deleteGiftTagsForContact(recordId);
                            db.insertGiftTagsForContact(recordId, selectedTagIds);
                        }
                        if(tagType=="giftTagForEvent"){
                            db.deleteGiftTagsForEvent(recordId);
                            db.insertGiftTagsForEvent(recordId, selectedTagIds);
                        }
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog dialog = builder.create();

        return dialog;
    }
}
