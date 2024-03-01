package com.pranavamrute.jeconnect;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    LinearLayout notesLayout, resultsLayout, resultsLayout2, attendanceLayout, complaintsLayout;
    TextView complaintsTextView;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    private long pressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Navigation View and Toolbar
        navigationView = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.navigationToolbar);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(MainActivity.this);
        navigationView.setCheckedItem(R.id.nav_home);

        notesLayout = findViewById(R.id.linearLayoutNotes);
        resultsLayout = findViewById(R.id.linearLayoutResults);
        resultsLayout2 = findViewById(R.id.linearLayoutResults2);
        attendanceLayout = findViewById(R.id.linearLayoutAttendance);
        complaintsLayout = findViewById(R.id.linearLayoutComplaints);

        complaintsTextView = findViewById(R.id.textViewComplaints);

        if (MyData.TYPE_OF_USER.equals("parent")) {
            notesLayout.setVisibility(View.GONE);
            resultsLayout.setVisibility(View.GONE);
            resultsLayout2.setVisibility(View.VISIBLE);
            complaintsLayout.setVisibility(View.VISIBLE);

        } else if (MyData.TYPE_OF_USER.equals("student")) {
            notesLayout.setVisibility(View.VISIBLE);
            complaintsLayout.setVisibility(View.GONE);
        } else if (MyData.TYPE_OF_USER.equals("teacher")) {
            notesLayout.setVisibility(View.VISIBLE);
            complaintsLayout.setVisibility(View.VISIBLE);
            complaintsTextView.setText("Complaints/Feedback");
        }

        resultsLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyData.TYPE_OF_USER.equals("teacher")) {
                    Intent intent = new Intent(MainActivity.this, ResultsActivity.class);
                    startActivity(intent);
                } else if (MyData.TYPE_OF_USER.equals("student")) {
                    Intent intent = new Intent(MainActivity.this, ResultsDownloadSection.class);
                    startActivity(intent);
                } else if (MyData.TYPE_OF_USER.equals("parent")) {
                    Intent intent = new Intent(MainActivity.this, ResultsDownloadSection.class);
                    startActivity(intent);
                }
            }
        });

        notesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyData.TYPE_OF_USER.equals("teacher")) {
                    Intent intent = new Intent(MainActivity.this, NotesActivity.class);
                    startActivity(intent);
                } else if (MyData.TYPE_OF_USER.equals("student")) {
                    Intent intent = new Intent(MainActivity.this, NotesDownloadSection.class);
                    startActivity(intent);
                }
            }
        });

        attendanceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyData.TYPE_OF_USER.equals("teacher")) {
                    Intent intent = new Intent(MainActivity.this, AttendanceActivity.class);
                    startActivity(intent);
                } else if (MyData.TYPE_OF_USER.equals("student")) {
                    Intent intent = new Intent(MainActivity.this, ResultsDownloadSection.class);
                    startActivity(intent);
                } else if (MyData.TYPE_OF_USER.equals("parent")) {
                    Intent intent = new Intent(MainActivity.this, ResultsDownloadSection.class);
                    startActivity(intent);
                }
            }
        });


        resultsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyData.TYPE_OF_USER.equals("teacher")) {
                    Intent intent = new Intent(MainActivity.this, ResultsActivity.class);
                    startActivity(intent);
                } else if (MyData.TYPE_OF_USER.equals("student")) {
                    Intent intent = new Intent(MainActivity.this, ResultsDownloadSection.class);
                    startActivity(intent);
                } else if (MyData.TYPE_OF_USER.equals("parent")) {
                    Intent intent = new Intent(MainActivity.this, ResultsDownloadSection.class);
                    startActivity(intent);
                }
            }
        });


        attendanceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyData.TYPE_OF_USER.equals("teacher")) {
                    Intent intent = new Intent(MainActivity.this, AttendanceActivity.class);
                    startActivity(intent);
                } else if (MyData.TYPE_OF_USER.equals("student")) {
                    Intent intent = new Intent(MainActivity.this, AttendanceDownloadSection.class);
                    startActivity(intent);
                } else if (MyData.TYPE_OF_USER.equals("parent")) {
                    Intent intent = new Intent(MainActivity.this, AttendanceDownloadSection.class);
                    startActivity(intent);
                }
            }
        });

        complaintsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ComplaintsActivity.class);
                startActivity(intent);
            }
        });



    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            finish();
            Intent intent = new Intent(MainActivity.this,Login.class);
            startActivity(intent);
        }else if(id == R.id.nav_website){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.jcoet.ac.in/"));
            startActivity(browserIntent);
        }else if(id == R.id.nav_bug_report){
            Intent intent = new Intent(Intent.ACTION_SEND);
            String[] recipients = {"amrutepranav2571@gmail.com"};
            intent.putExtra(Intent.EXTRA_EMAIL, recipients);
            intent.putExtra(Intent.EXTRA_SUBJECT, "Reporting Bug Regarding Tiny Academy");
            intent.setType("text/html");
            intent.setPackage("com.google.android.gm");
            startActivity(Intent.createChooser(intent, "Send mail"));
        }

        return false;
    }

    @Override
    public void onBackPressed() {
        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            System.exit(0);
        } else {

            Toast.makeText(this, "Press back again to exit!", Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();
    }
}