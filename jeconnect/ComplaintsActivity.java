package com.pranavamrute.jeconnect;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amitthakare.jeconnect.Adapters.ComplaintsAdapter;
import com.amitthakare.jeconnect.Models.ComplaintsModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ComplaintsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ComplaintsAdapter complaintsAdapter;
    private List<ComplaintsModel> complaintList;

    private FloatingActionButton floatButton;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaints);

        toolbar = findViewById(R.id.complaints_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setTitle("Complaints");

        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        floatButton = findViewById(R.id.floatingActionButton);

        recyclerView = findViewById(R.id.complaintsRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        complaintList = new ArrayList<>();
        complaintsAdapter = new ComplaintsAdapter(this, complaintList);

        //getDataFromDataBase();


        Log.d("MyData-ComplaintView", complaintList + "");

        //complaintsAdapter = new ComplaintsAdapter(this, complaintList);
        //recyclerView.setAdapter(complaintsAdapter);

        complaintsAdapter.setOnRecyclerClickListener(new ComplaintsAdapter.OnRecyclerClickListener() {
            @Override
            public void onRecyclerItemClick(int position) {
                Toast.makeText(ComplaintsActivity.this, position + "", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ComplaintsActivity.this, ViewComplaintActivity.class);
                intent.putExtra("title", complaintList.get(position).getTitle());
                intent.putExtra("date", complaintList.get(position).getDate());
                intent.putExtra("status", complaintList.get(position).getStatus());
                intent.putExtra("description", complaintList.get(position).getDescription());
                intent.putExtra("complaintID", complaintList.get(position).getComplaintID());
                startActivity(intent);

            }
        });

       /* ComplaintsModel complaintsModel = new ComplaintsModel("Feedback - 2023", "16/12/1999", "Submitted", "Description", "");
        ComplaintsModel complaintsModel1 = new ComplaintsModel("Feedback - 2023", "16/12/1999", "Submitted", "Description", "");
        ComplaintsModel complaintsModel2 = new ComplaintsModel("Feedback - 2023", "16/12/1999", "Submitted", "Description", "");
        complaintList.add(complaintsModel);
        complaintList.add(complaintsModel1);
        complaintList.add(complaintsModel2);*/


        floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ComplaintsActivity.this, NewComplaintActivity.class);
                startActivity(intent);
            }
        });

    }

    private void getDataFromDataBase() {
        FirebaseDatabase.getInstance().getReference("Complaints")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        try {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                if (data != null) {
                                    Map<String, String> db_data = (Map<String, String>) data.getValue();
                                    Log.d("MyData-ComplaintView", db_data.get("ComplaintID"));
                                    Log.d("MyData-ComplaintView", db_data.get("ComplaintID").contains(MyData.USERNAME) + "");
                                    if (db_data.get("ComplaintID").contains(MyData.USERNAME)) {
                                        Log.d("MyData-ComplaintView", db_data.get("Title") + " : " + db_data.get("Date") + " : " + db_data.get("Status") + " : " + db_data.get("Description") + " : " + db_data.get("ComplaintID"));
                                        ComplaintsModel complaintsModel = new ComplaintsModel(db_data.get("Title"), db_data.get("Date"), db_data.get("Status"), db_data.get("Description"), db_data.get("ComplaintID"));
                                        complaintList.add(complaintsModel);
                                        complaintsAdapter.notifyDataSetChanged();
                                    } else {
                                        Log.d("MyData-ComplaintView", "Not mathced");
                                    }
                                }
                            }
                        } catch (Exception e) {
                            Log.e("MyData-Exception : ", e.getLocalizedMessage());
                        }
                    }
                });
    }

    private void getTeacherDataFromDataBase() {
        FirebaseDatabase.getInstance().getReference("Complaints")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        try {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                if (data != null) {
                                    Map<String, String> db_data = (Map<String, String>) data.getValue();
                                    Log.d("MyData-ComplaintView", db_data.get("ComplaintID"));
                                    Log.d("MyData-ComplaintView", db_data.get("ComplaintID").contains(MyData.USERNAME) + "");

                                    Log.d("MyData-ComplaintView", db_data.get("Title") + " : " + db_data.get("Date") + " : " + db_data.get("Status") + " : " + db_data.get("Description") + " : " + db_data.get("ComplaintID"));
                                    ComplaintsModel complaintsModel = new ComplaintsModel(db_data.get("Title"), db_data.get("Date"), db_data.get("Status"), db_data.get("Description"), db_data.get("ComplaintID"));
                                    complaintList.add(complaintsModel);
                                    complaintsAdapter.notifyDataSetChanged();

                                }
                            }
                        } catch (Exception e) {
                            Log.e("MyData-Exception : ", e.getLocalizedMessage());
                        }
                    }
                });
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (MyData.TYPE_OF_USER.equals("parent")) {
            floatButton.setVisibility(View.VISIBLE);
            complaintList.clear();
            getDataFromDataBase();
        } else {
            complaintList.clear();
            getTeacherDataFromDataBase();
        }
        recyclerView.setAdapter(complaintsAdapter);

    }
}