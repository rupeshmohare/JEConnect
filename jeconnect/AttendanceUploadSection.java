package com.pranavamrute.jeconnect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AttendanceUploadSection extends AppCompatActivity {

    private Button uploadAttendanceBtn;

    private CalendarView calendar;

    private TextView date_view;

    private ActivityResultLauncher<Intent> startActivityForResult;

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private String newDate;

    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_upload_section);

        toolbar = findViewById(R.id.attendance_upload_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setTitle("Attendance");

        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        newDate = "";

        calendar = findViewById(R.id.calendarView);
        uploadAttendanceBtn = findViewById(R.id.uploadAttendanceBtn);
        date_view = findViewById(R.id.date_view);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("Attendance");

        Timestamp ts = new Timestamp(calendar.getDate());
        Date date = new Date(ts.getTime());
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        newDate = formatter.format(date);
        date_view.setText(newDate);

        calendar
                .setOnDateChangeListener(
                        new CalendarView
                                .OnDateChangeListener() {
                            @Override
                            public void onSelectedDayChange(
                                    @NonNull CalendarView view,
                                    int year,
                                    int month,
                                    int dayOfMonth) {
                                int i = String.valueOf(dayOfMonth).length();
                                if (i == 1) {
                                    newDate = "0" + dayOfMonth + "-";
                                } else {
                                    newDate = dayOfMonth + "-";
                                }

                                i = String.valueOf(month).length();
                                if (i == 1) {
                                    newDate = newDate + "0" + (month + 1) + "-";
                                } else {
                                    newDate = newDate + (month + 1) + "-";
                                }

                                newDate = newDate + year;

                                date_view.setText(newDate);

                                //Toast.makeText(AttendanceUploadSection.this, newDate, Toast.LENGTH_SHORT).show();

                                Log.d("MyData-SelectedDate", newDate);
                            }
                        });

        uploadAttendanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectAttendance();
            }
        });

        startActivityForResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                        Intent data = result.getData();
                        uploadMyAttendance(data.getData());
                    }
                }
        );

    }


    void selectAttendance() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult.launch(intent);
    }

    private void uploadMyAttendance(Uri data) {

        String tempDate = newDate.replaceAll("-", "");

        ProgressDialog progressDialog = new ProgressDialog(AttendanceUploadSection.this);
        progressDialog.setTitle("Uploading " + tempDate + ".pdf");
        progressDialog.setMessage("Uploading Please Wait!");
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        StorageReference reference = storageReference.child(newDate + ".pdf");
        reference.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete()) ;
                        Uri url = uriTask.getResult();

                        Map<String, String> data = new HashMap<>();
                        data.put(newDate, url.toString());

                        FirebaseDatabase.getInstance().getReference("Attendance")
                                .child(newDate)
                                .setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(AttendanceUploadSection.this, "Attendance uploaded!!", Toast.LENGTH_SHORT).show();
                                            Log.d("MyData-DatabaseRealtime", "Data stored successfully!!");
                                            finish();
                                        } else {
                                            Log.d("MyData-DatabaseRealtime", "Failed to save data!!");
                                        }
                                    }
                                });

                        progressDialog.dismiss();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                        progressDialog.setMessage("Uploading in progress : " + (int) progress);
                    }
                });
    }

}