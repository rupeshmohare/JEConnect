package com.pranavamrute.jeconnect;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ViewComplaintActivity extends AppCompatActivity {

    private TextView titleTV, descriptionTV, statusTV, orTV, changeStatusHeadingTV, dateTV;
    private Button acknowledgeBtn, completedBtn, setStatusBtn;
    private TextInputEditText manualStatus;
    private TextInputLayout manualStatusLayout;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_complaint);

        toolbar = findViewById(R.id.view_complaint_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setTitle("View Complaints");

        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent = getIntent();

        String title = intent.getStringExtra("title");
        String date = intent.getStringExtra("date");
        String status = intent.getStringExtra("status");
        String description = intent.getStringExtra("description");

        titleTV = findViewById(R.id.titleTextView);
        statusTV = findViewById(R.id.statusTextView);
        dateTV = findViewById(R.id.dateTextView);
        descriptionTV = findViewById(R.id.descriptionTextView);
        changeStatusHeadingTV = findViewById(R.id.changeStatusHeadingTextView);
        acknowledgeBtn = findViewById(R.id.acknowledgeBtn);
        completedBtn = findViewById(R.id.completedBtn);
        orTV = findViewById(R.id.or_text);
        manualStatusLayout = findViewById(R.id.statusTextInputLayout);
        setStatusBtn = findViewById(R.id.setStatusBtn);
        manualStatus = findViewById(R.id.manual_status_edittext);

        titleTV.setText(title);
        statusTV.setText(status);
        descriptionTV.setText(description);
        dateTV.setText(date);

        if (MyData.TYPE_OF_USER.equals("parent")) {
            changeStatusHeadingTV.setVisibility(View.GONE);
            acknowledgeBtn.setVisibility(View.GONE);
            completedBtn.setVisibility(View.GONE);
            orTV.setVisibility(View.GONE);
            manualStatus.setVisibility(View.GONE);
            setStatusBtn.setVisibility(View.GONE);
            manualStatusLayout.setVisibility(View.GONE);
        }

        acknowledgeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusTV.setText("Acknowledged");
                setComplaintStatus("Acknowledged");
            }
        });

        completedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusTV.setText("Completed");
                setComplaintStatus("Completed");
            }
        });

        setStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(manualStatus.getText().toString())) {
                    statusTV.setText(manualStatus.getText().toString());
                    setComplaintStatus(manualStatus.getText().toString());
                } else {
                    Toast.makeText(ViewComplaintActivity.this, "Enter manual status!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void setComplaintStatus(String status) {
        Intent intent = getIntent();

        String complaintID = intent.getStringExtra("complaintID");
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        String date = intent.getStringExtra("date");

        Map<String, String> data = new HashMap<>();
        data.put("Title", title);
        data.put("Date", date);
        data.put("Status", status);
        data.put("Description", description);
        data.put("ComplaintID", complaintID);

        FirebaseDatabase.getInstance().getReference("Complaints")
                .child(complaintID)
                .setValue(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(ViewComplaintActivity.this, "Status Updated!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ViewComplaintActivity.this, "Failed to update status!", Toast.LENGTH_SHORT).show();
                    }
                });

    }

}