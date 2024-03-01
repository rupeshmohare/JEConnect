package com.pranavamrute.jeconnect;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

public class NotesDownloadSection extends AppCompatActivity {

    private AutoCompleteTextView acTextViewSemester, acTextViewSubject, acTextViewUnits;
    private TextInputLayout semesterLayout, subjectLayout, unitsLayout;

    private ArrayList<String> semestersList, subjectList, unitList;

    private Button downloadNotesBtn;

    private FirebaseFirestore db;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_download);

        toolbar = findViewById(R.id.notes_download_toolbar);
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


        acTextViewSemester = findViewById(R.id.acTextViewSemesterDownloadNotes);
        acTextViewSubject = findViewById(R.id.acTextViewSubjectDownloadNotes);
        acTextViewUnits = findViewById(R.id.acTextViewUnitsDownloadNotes);

        semesterLayout = findViewById(R.id.semesterLayoutDownloadNotes);
        subjectLayout = findViewById(R.id.subjectLayoutDownloadNotes);
        unitsLayout = findViewById(R.id.unitsLayoutDownloadNotes);

        downloadNotesBtn = findViewById(R.id.downloadNotesBtnDownloadNotes);

        subjectLayout.setVisibility(View.INVISIBLE);
        unitsLayout.setVisibility(View.INVISIBLE);
        downloadNotesBtn.setVisibility(View.INVISIBLE);


        subjectList = new ArrayList<>();
        semestersList = new ArrayList<>();
        unitList = new ArrayList<>();

        semestersList.clear();

        FirebaseDatabase.getInstance().getReference("Notes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                                Log.d("MyData-SemesterList", dataSnapshot.getKey());
                                semestersList.add(dataSnapshot.getKey());
                            }

                        } else {
                            Log.d("MyData-SemesterList", "Failed to get data.");
                        }
                    }
                });


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dropdown_item, semestersList);
        acTextViewSemester.setAdapter(adapter);

        acTextViewSemester.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "" + acTextViewSemester.getText().toString(), Toast.LENGTH_SHORT).show();
                subjectList.clear();
                getSubjects();
            }
        });

        downloadNotesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadNotes();
            }
        });


    }

    void getSubjects() {

        FirebaseDatabase.getInstance().getReference("Notes")
                .child(acTextViewSemester.getText().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                                Log.d("MyData-SubjectsList", dataSnapshot.getKey());
                                subjectList.add(dataSnapshot.getKey());
                            }

                        } else {
                            Log.d("MyData-SubjectsList", "Failed to get data.");
                        }
                    }
                });

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, R.layout.dropdown_item, subjectList);
        acTextViewSubject.setAdapter(adapter1);

        subjectLayout.setVisibility(View.VISIBLE);

        acTextViewSubject.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "" + acTextViewSubject.getText().toString(), Toast.LENGTH_SHORT).show();
                unitList.clear();
                getUnits();
            }
        });
    }

    void getUnits() {

        FirebaseDatabase.getInstance().getReference("Notes")
                .child(acTextViewSemester.getText().toString())
                .child(acTextViewSubject.getText().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                                Log.d("MyData-UnitsList", dataSnapshot.getKey());
                                unitList.add(dataSnapshot.getKey());
                            }

                        } else {
                            Log.d("MyData-UnitsList", "Failed to get data.");
                        }
                    }
                });

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, R.layout.dropdown_item, unitList);
        acTextViewUnits.setAdapter(adapter2);

        unitsLayout.setVisibility(View.VISIBLE);

        acTextViewUnits.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "" + acTextViewUnits.getText().toString(), Toast.LENGTH_SHORT).show();
                downloadNotesBtn.setVisibility(View.VISIBLE);
            }
        });
    }

    void downloadNotes() {

        final String[] url = new String[1];

        FirebaseDatabase.getInstance().getReference("Notes")
                .child(acTextViewSemester.getText().toString())
                .child(acTextViewSubject.getText().toString())
                .child(acTextViewUnits.getText().toString())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {

                        //Toast.makeText(NotesActivity.this, documentSnapshot.get(acTextViewUnits.getText().toString()).toString(), Toast.LENGTH_SHORT).show();
                        //url[0] = dataSnapshot.getKey();
                        String url = "";
                        for(DataSnapshot data : dataSnapshot.getChildren()){
                            Log.d("MyData-StorageUrl", data.getValue().toString());
                            url = data.getValue().toString();
                        }

                        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(url);

                        ProgressDialog pd = new ProgressDialog(NotesDownloadSection.this);
                        pd.setTitle(acTextViewUnits.getText() + ".pdf");
                        pd.setMessage("Downloading Please Wait!");
                        pd.setIndeterminate(true);
                        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        pd.show();

                        File rootFile = new File(MyData.MAIN_DIR + "/" + acTextViewSemester.getText() + "/" + acTextViewSubject.getText() + "/" + acTextViewUnits.getText() + "/");
                        if (!rootFile.exists()) {
                            rootFile.mkdirs();
                        }

                        Log.d("MyData-LocalStoragePath", rootFile.getAbsolutePath());

                        File localFile = new File(rootFile, acTextViewUnits.getText() + ".pdf");

                        reference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                //Log.e("MyData-PdfLocalStorage", ";local tem file created  created " + localFile.toString());

                                if (localFile.canRead()) {

                                    pd.dismiss();
                                }

                                Toast.makeText(NotesDownloadSection.this, "Download Completed! : " + localFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                                Log.e("MyData-PdfLocalStorage", "Download Completed! -- " + localFile.toString());
                                finish();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("MyData-File ", ";local tem file not created  created " + e.toString());
                                Toast.makeText(NotesDownloadSection.this, "Download Failed!", Toast.LENGTH_LONG).show();
                            }
                        }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull FileDownloadTask.TaskSnapshot snapshot) {
                                double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                                pd.setMessage("Downloading in progress :" + (int) progress);
                            }
                        });


                    }
                });
    }
}