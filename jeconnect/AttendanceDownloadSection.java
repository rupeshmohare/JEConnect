package com.pranavamrute.jeconnect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AttendanceDownloadSection extends AppCompatActivity {

    private Button uploadAttendanceBtn;

    private CalendarView calendar;

    private TextView date_view;
    private String newDate;

    private ActivityResultLauncher<Intent> startActivityForResult;

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_download_section);

        toolbar = findViewById(R.id.attendance_download_toolbar);
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

        calendar = findViewById(R.id.calendarViewDownloadSection);
        uploadAttendanceBtn = findViewById(R.id.downloadAttendanceBtn);
        date_view = findViewById(R.id.date_viewDownloadAttendance);

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

                                //Toast.makeText(AttendanceDownloadSection.this, newDate, Toast.LENGTH_SHORT).show();

                                Log.d("MyData-SelectedDate", newDate);
                            }
                        });

        uploadAttendanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadAttendance();
            }
        });

    }

    void downloadAttendance() {

        String tempDate = newDate.replaceAll("-", "");

        FirebaseDatabase.getInstance().getReference("Attendance")
                .child(newDate)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        try {

                          
                            String url = "";
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                Log.d("MyData-StorageUrl", data.getValue().toString());
                                url = data.getValue().toString();
                            }


                            StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(url);

                            ProgressDialog pd = new ProgressDialog(AttendanceDownloadSection.this);
                            pd.setTitle("Downloading " + ".pdf");
                            pd.setMessage("Downloading Please Wait!");
                            pd.setIndeterminate(true);
                            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            pd.show();

                            File rootFile = new File(MyData.MAIN_DIR + "/" + "Attendance/" + tempDate + "/");
                            if (!rootFile.exists()) {
                                rootFile.mkdirs();
                            }

                            Log.d("MyData-LocalStoragePath", rootFile.getAbsolutePath());

                            File localFile = new File(rootFile, newDate + ".pdf");

                            reference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    //Log.e("MyData-PdfLocalStorage", ";local tem file created  created " + localFile.toString());

                                    if (localFile.canRead()) {

                                        pd.dismiss();
                                    }

                                    Toast.makeText(AttendanceDownloadSection.this, "Download Completed! : " + localFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                                    Log.e("MyData-PdfLocalStorage", "Download Completed! -- " + localFile.toString());
                                    finish();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("MyData-File ", ";local tem file not created  created " + e.toString());
                                    Toast.makeText(AttendanceDownloadSection.this, "Download Failed!", Toast.LENGTH_LONG).show();
                                }
                            }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(@NonNull FileDownloadTask.TaskSnapshot snapshot) {
                                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                                    pd.setMessage("Downloading in progress :" + (int) progress);
                                }
                            });


                        }catch (Exception e){
                            Log.e("MyData-Attendance","No Attendance : "+e.getLocalizedMessage());
                            if(e.getLocalizedMessage().equals("location must not be null or empty")){
                                Toast.makeText(AttendanceDownloadSection.this, "Attendance is not present.", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(AttendanceDownloadSection.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

}