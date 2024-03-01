package com.pranavamrute.jeconnect;

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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NotesUploadSection extends AppCompatActivity {

    private AutoCompleteTextView acTextViewSemester, acTextViewSubject, acTextViewUnits;
    private TextInputLayout semesterLayout, subjectLayout, unitsLayout;


    private ArrayList<String> semestersList, subjectList, unitList;

    private Button uploadNotesBtn;

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private ActivityResultLauncher<Intent> startActivityForResult;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_upload);

        toolbar = findViewById(R.id.notes_upload_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setTitle("Notes");

        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("Notes");

        acTextViewSemester = findViewById(R.id.acTextViewUploadNotesSemester);
        acTextViewSubject = findViewById(R.id.acTextViewUploadNotesSubject);
        acTextViewUnits = findViewById(R.id.acTextViewUploadNotesUnits);

        semesterLayout = findViewById(R.id.semesterUploadNotesLayout);
        subjectLayout = findViewById(R.id.subjectUploadNotesLayout);
        unitsLayout = findViewById(R.id.unitsUploadNotesLayout);

        uploadNotesBtn = findViewById(R.id.uploadNotesBtn);

        subjectLayout.setVisibility(View.INVISIBLE);
        unitsLayout.setVisibility(View.INVISIBLE);
        uploadNotesBtn.setVisibility(View.INVISIBLE);

        subjectList = new ArrayList<>();
        semestersList = new ArrayList<>();
        unitList = new ArrayList<>();

        startActivityForResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                        Intent data = result.getData();
                        uploadMyNotes(data.getData());
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
                getSubjects();
            }
        });


        uploadNotesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectNotes();
            }
        });

    }

    void getSubjects() {

        switch (acTextViewSemester.getText().toString()) {
            case "1st Sem":
                subjectList.add("M1");
                subjectList.add("C Programming");
                subjectList.add("Physics");
                subjectList.add("Mechanics");
            case "2nd Sem":
                subjectList.add("M2");
                subjectList.add("Chemistry");
            case "3rd Sem":
                subjectList.add("OOP");
                subjectList.add("STLD");
        }

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, R.layout.dropdown_item, subjectList);
        acTextViewSubject.setAdapter(adapter1);

        subjectLayout.setVisibility(View.VISIBLE);

        acTextViewSubject.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "" + acTextViewSubject.getText().toString(), Toast.LENGTH_SHORT).show();
                getUnits();
            }
        });
    }

    void getUnits() {

        unitList.add("Unit 1");
        unitList.add("Unit 2");
        unitList.add("Unit 3");
        unitList.add("Unit 4");
        unitList.add("Unit 5");
        unitList.add("Unit 6");

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, R.layout.dropdown_item, unitList);
        acTextViewUnits.setAdapter(adapter2);

        unitsLayout.setVisibility(View.VISIBLE);

        acTextViewUnits.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "" + acTextViewUnits.getText().toString(), Toast.LENGTH_SHORT).show();
                uploadNotesBtn.setVisibility(View.VISIBLE);
            }
        });
    }

    void selectNotes() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult.launch(intent);
    }

    private void uploadMyNotes(Uri data) {

        ProgressDialog progressDialog = new ProgressDialog(NotesUploadSection.this);
        progressDialog.setTitle(acTextViewUnits.getText() + ".pdf");
        progressDialog.setMessage("Uploading Please Wait!");
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        StorageReference reference = storageReference.child(acTextViewSemester.getText().toString() + "/" + acTextViewSubject.getText().toString() + "/" + acTextViewUnits.getText().toString() + "/" + acTextViewUnits.getText().toString() + ".pdf");
        reference.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete()) ;
                        Uri url = uriTask.getResult();

                        Map<String, String> data = new HashMap<>();
                        data.put(acTextViewUnits.getText().toString(), url.toString());

                        FirebaseDatabase.getInstance().getReference("Notes")
                                .child(acTextViewSemester.getText().toString())
                                .child(acTextViewSubject.getText().toString())
                                .child(acTextViewUnits.getText().toString())
                                .setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(NotesUploadSection.this, "Notes uploaded!!", Toast.LENGTH_SHORT).show();
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