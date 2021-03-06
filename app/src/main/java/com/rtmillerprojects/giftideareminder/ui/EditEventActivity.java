package com.rtmillerprojects.giftideareminder.ui;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.rtmillerprojects.giftideareminder.R;
import com.rtmillerprojects.giftideareminder.adapter.AgendaItemAdapter;
import com.rtmillerprojects.giftideareminder.model.AgendaItem;
import com.rtmillerprojects.giftideareminder.util.DatabaseHelper;

import java.util.ArrayList;

/**
 * Created by Ryan on 6/5/2016.
 */
public class EditEventActivity extends AppCompatActivity{

    Toolbar toolbar;
    Spinner spinner;
    Button btnSave;
    Button btnCancel;
    EditText eventTitle;
    EditText selectedDate;
    Switch recurringSwitch;
    LinearLayout editContactTag;
    LinearLayout editGiftTag;
    AgendaItem event;
    boolean isNew;
    int recordId;
    ArrayList<Integer> contactTagIds;
    ArrayList<Integer> giftTagIds;
    Context mContext;
    Activity mActivity;
    TagDialog tagDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_event);
        Intent intent = getIntent();
        recordId = intent.getIntExtra("recordId",0);
        isNew = intent.getBooleanExtra("isNew",true);
        if(recordId==0){
            isNew=true;
        }
        else{
            event = intent.getParcelableExtra("data");
        }

        spinner = (Spinner) findViewById(R.id.recurrence_spinner);
        toolbar = (Toolbar) findViewById(R.id.editEventToolbar);
        btnSave = (Button) findViewById(R.id.btn_save_event);
        btnCancel = (Button) findViewById(R.id.btn_cancel_event);
        eventTitle = (EditText) findViewById(R.id.agenda_item_title);
        selectedDate = (EditText) findViewById(R.id.selectedDate);
        recurringSwitch = (Switch) findViewById(R.id.recurring_switch);
        editContactTag = (LinearLayout) findViewById(R.id.edit_contact_tag);
        editGiftTag = (LinearLayout) findViewById(R.id.edit_gift_tag);
        ((TextView) editContactTag.findViewById(R.id.edit_tag_text)).setText("Tag some contacts");
        ((TextView) editGiftTag.findViewById(R.id.edit_tag_text)).setText("Tag some gifts");
        editContactTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showContactTags();
            }
        });
        DatabaseHelper db = DatabaseHelper.getInstance(this);

        Bundle extras = getIntent().getExtras();
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            assert getSupportActionBar() != null;
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if(isNew){
                getSupportActionBar().setTitle("Add an event");
            }
            else{
                getSupportActionBar().setTitle("Edit event");
                eventTitle.setText(event.getTitle());

            }
        }
        ArrayAdapter<CharSequence> recurrenceOptionAdapter = ArrayAdapter.createFromResource(this,
                R.array.recurrence_options, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        recurrenceOptionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(recurrenceOptionAdapter);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEvent();
            }
        });


    }
    public void onStart(){
        super.onStart();

        selectedDate =(EditText) findViewById(R.id.selectedDate);

        if(selectedDate!=null){
            selectedDate.setOnFocusChangeListener(new View.OnFocusChangeListener(){
                public void onFocusChange(View view, boolean hasfocus){
                    if(hasfocus){
                        DateDialog dialog=new DateDialog(view);
                        FragmentTransaction ft =getFragmentManager().beginTransaction();
                        dialog.show(ft, "DatePicker");

                    }
                }

            });
        }
    }
    public void saveEvent(){
        Toast.makeText(this, "isNew: "+isNew, Toast.LENGTH_SHORT).show();
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        AgendaItem ai = new AgendaItem();
        ai.setTitle(eventTitle.getText().toString());
        ai.setId(recordId);
        ai.setRecurring(recurringSwitch.isActivated());
        ai.setRecurRate(spinner.getSelectedItem().toString());
        if(isNew) {
            db.insertAgendaItem(ai);
        }
        else{
            db.updateEvents(recordId,ai);
        }
        //Tags
        if(contactTagIds != null && contactTagIds.size()>0){
            String str = "";
            for(int ints : contactTagIds){
                str+=" "+ints;
            }
            Toast.makeText(EditEventActivity.this, str, Toast.LENGTH_SHORT).show();
            db.deleteContactTagsForEvent(recordId);
            db.insertContactTagsForEvent(recordId, contactTagIds);
        }
        if(giftTagIds != null && giftTagIds.size()>0){
            db.deleteContactTagsForEvent(recordId);
            db.insertContactTagsForEvent(recordId, giftTagIds);
        }
        this.finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showContactTags(){

        //Now need to create a dummy record for new events
        if(isNew){

        }
        TagDialog tagDialog = new TagDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("recordId", recordId);
        bundle.putString("tagType", "Contact");
        bundle.putString("recordType", "Event");
        tagDialog.setArguments(bundle);
                //"Contact","Event",recordId);
        tagDialog.show(this.getFragmentManager(),"my_dialog_tag");
        tagDialog.setDialogResult(new TagDialog.OnMyDialogResult(){
            @Override
            public void dialogFinish(ArrayList<Integer> selectedTagIds) {
                contactTagIds = selectedTagIds;

            }

        });
        //
    }

}
