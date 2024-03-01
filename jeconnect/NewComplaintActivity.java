package com.pranavamrute.jeconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NewComplaintActivity extends AppCompatActivity {

    private Button submitComplaintBtn;

    private TextInputEditText titleComplaint, descriptionComplaint;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_complaint);

        toolbar = findViewById(R.id.new_complaints_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setTitle("New Complaints");

        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        submitComplaintBtn = findViewById(R.id.submit_complaint_btn);

        titleComplaint = findViewById(R.id.title_complaint);
        descriptionComplaint = findViewById(R.id.manual_status_edittext);

        submitComplaintBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onComplaintSubmit();
            }
        });

    }


    private void onComplaintSubmit(){

        if(!TextUtils.isEmpty(titleComplaint.getText()) && !TextUtils.isEmpty(descriptionComplaint.getText())){
            //store data base

            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            String newDate = formatter.format(date);

            long random = System.currentTimeMillis();
            String complaintID = MyData.USERNAME+random;

            Map<String, String> data = new HashMap<>();
            data.put("Title",titleComplaint.getText().toString());
            data.put("Date",newDate);
            data.put("Status","Submitted");
            data.put("Description",descriptionComplaint.getText().toString());
            data.put("ComplaintID",complaintID);


            FirebaseDatabase.getInstance().getReference("Complaints")
                    .child(complaintID)
                    .setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(NewComplaintActivity.this, "Complaint Submitted!!", Toast.LENGTH_SHORT).show();
                                Log.d("MyData-Complaint", "Data stored successfully!!");
                                finish();
                            } else {
                                Log.d("MyData-Complaint", "Failed to save data!!");
                            }
                        }
                    });

        }else{
            Toast.makeText(this, "Please fill all details!!", Toast.LENGTH_SHORT).show();
        }

    }

}