package com.pranavamrute.jeconnect;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ResultsUploadSection extends AppCompatActivity {

    private AutoCompleteTextView acTextViewSemester, acTextViewPapers;

    private TextInputLayout papersLayout;

    private ArrayList<String> semestersList, paperList;

    private Button UploadResultsBtn;

    private FirebaseStorage storage;

    private StorageReference storageReference;

    private ActivityResultLauncher<Intent> startActivityForResult;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_upload_section);

        toolbar = findViewById(R.id.results_upload_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setTitle("Results");

        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("Results");

        acTextViewSemester = findViewById(R.id.acTextViewUploadResultsSemester);
        acTextViewPapers = findViewById(R.id.acTextViewUploadResultsPaper);

        papersLayout = findViewById(R.id.papersUploadResultsLayout);

        UploadResultsBtn = findViewById(R.id.UploadResultsBtn);
        
        papersLayout.setVisibility(View.INVISIBLE);
        UploadResultsBtn.setVisibility(View.INVISIBLE);
        
        semestersList = new ArrayList<>();
        paperList = new ArrayList<>();

        startActivityForResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                        Intent data = result.getData();
                        uploadMyResults(data.getData());
                    }
                }
        );

        semestersList.add("1st Sem");
        semestersList.add("2nd Sem");
        semestersList.add("3rd Sem");
        semestersList.add("4th Sem");
        semestersList.add("5th Sem");
        semestersList.add("6th Sem");
        semestersList.add("7th Sem");
        semestersList.add("8th Sem");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dropdown_item, semestersList);
        acTextViewSemester.setAdapter(adapter);

        acTextViewSemester.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "" + acTextViewSemester.getText().toString(), Toast.LENGTH_SHORT).show();
                papersLayout.setVisibility(View.VISIBLE);
                getPapers();
            }
        });


        UploadResultsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectResults();
            }
        });
        
    }


    void getPapers() {

        paperList.add("1st Sessional");
        paperList.add("2nd Sessional");
        paperList.add("3rd Sessional");
        paperList.add("1st PUT");
        paperList.add("2nd PUT");

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, R.layout.dropdown_item, paperList);
        acTextViewPapers.setAdapter(adapter1);


        acTextViewPapers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "" + acTextViewPapers.getText().toString(), Toast.LENGTH_SHORT).show();
                UploadResultsBtn.setVisibility(View.VISIBLE);
            }
        });
    }

    void selectResults() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult.launch(intent);
    }

    private void uploadMyResults(Uri data) {

        ProgressDialog progressDialog = new ProgressDialog(ResultsUploadSection.this);
        progressDialog.setTitle(acTextViewPapers.getText() + ".pdf");
        progressDialog.setMessage("Uploading Please Wait!");
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        StorageReference reference = storageReference.child(acTextViewSemester.getText().toString() + "/" + acTextViewPapers.getText().toString() + "/" + acTextViewPapers.getText().toString() + ".pdf");
        reference.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete()) ;
                        Uri url = uriTask.getResult();

                        Map<String, String> data = new HashMap<>();
                        data.put(acTextViewPapers.getText().toString(), url.toString());

                        FirebaseDatabase.getInstance().getReference("Results")
                                .child(acTextViewSemester.getText().toString())
                                .child(acTextViewPapers.getText().toString())
                                .setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ResultsUploadSection.this, "Results uploaded!!", Toast.LENGTH_SHORT).show();
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