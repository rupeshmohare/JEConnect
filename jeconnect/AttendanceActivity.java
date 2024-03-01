package com.pranavamrute.jeconnect;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AttendanceActivity extends AppCompatActivity {

    private Button uploadAttendaceBtnMain, downloadAttendaceBtnMain;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        toolbar = findViewById(R.id.attendance_toolbar);
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

        uploadAttendaceBtnMain = findViewById(R.id.uploadAttendanceMainBtn);
        downloadAttendaceBtnMain = findViewById(R.id.downloadAttendaceMainBtn);

        downloadAttendaceBtnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AttendanceActivity.this, AttendanceDownloadSection.class);
                startActivity(intent);
            }
        });

        uploadAttendaceBtnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AttendanceActivity.this,AttendanceUploadSection.class);
                startActivity(intent);
            }
        });

    }
}