package com.pranavamrute.jeconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

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

public class ResultsDownloadSection extends AppCompatActivity {

    private AutoCompleteTextView acTextViewSemester, acTextViewPapers;

    private TextInputLayout papersLayout;

    private ArrayList<String> semestersList, papersList;

    private Button downloadResultsBtn;

    private FirebaseFirestore db;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_download_section);

        toolbar = findViewById(R.id.results_download_toolbar);
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

        db = FirebaseFirestore.getInstance();


        acTextViewSemester = findViewById(R.id.acTextViewSemesterDownloadResults);
        acTextViewPapers = findViewById(R.id.acTextViewPaperDownloadResults);

        papersLayout = findViewById(R.id.papersLayoutDownloadResults);

        downloadResultsBtn = findViewById(R.id.downloadResultsBtnDownloadResults);

        papersLayout.setVisibility(View.INVISIBLE);
        downloadResultsBtn.setVisibility(View.INVISIBLE);



        semestersList = new ArrayList<>();
        papersList = new ArrayList<>();

        semestersList.clear();

        FirebaseDatabase.getInstance().getReference("Results")
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
                getPapers();
            }
        });

        downloadResultsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadResults();
            }
        });


    }

    void getPapers() {

        FirebaseDatabase.getInstance().getReference("Results")
                .child(acTextViewSemester.getText().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                                Log.d("MyData-ResultsList", dataSnapshot.getKey());
                                papersList.add(dataSnapshot.getKey());
                            }

                        } else {
                            Log.d("MyData-ResultsList", "Failed to get data.");
                        }
                    }
                });

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, R.layout.dropdown_item, papersList);
        acTextViewPapers.setAdapter(adapter2);

        papersLayout.setVisibility(View.VISIBLE);

        acTextViewPapers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "" + acTextViewPapers.getText().toString(), Toast.LENGTH_SHORT).show();
                downloadResultsBtn.setVisibility(View.VISIBLE);
            }
        });
    }

    void downloadResults() {

        FirebaseDatabase.getInstance().getReference("Results")
                .child(acTextViewSemester.getText().toString())
                .child(acTextViewPapers.getText().toString())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {

                        String url = "";
                        for(DataSnapshot data : dataSnapshot.getChildren()){
                            Log.d("MyData-StorageUrl", data.getValue().toString());
                            url = data.getValue().toString();
                        }

                        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(url);

                        ProgressDialog pd = new ProgressDialog(ResultsDownloadSection.this);
                        pd.setTitle(acTextViewPapers.getText() + ".pdf");
                        pd.setMessage("Downloading Please Wait!");
                        pd.setIndeterminate(true);
                        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        pd.show();

                        File rootFile = new File(MyData.MAIN_DIR + "/" + "Results/" +acTextViewSemester.getText() + "/" + acTextViewPapers.getText() + "/");
                        if (!rootFile.exists()) {
                            rootFile.mkdirs();
                        }

                        Log.d("MyData-LocalStoragePath", rootFile.getAbsolutePath());

                        File localFile = new File(rootFile, acTextViewPapers.getText() + ".pdf");

                        reference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                //Log.e("MyData-PdfLocalStorage", ";local tem file created  created " + localFile.toString());

                                if (localFile.canRead()) {

                                    pd.dismiss();
                                }

                                Toast.makeText(ResultsDownloadSection.this, "Download Completed! : " + localFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                                Log.e("MyData-PdfLocalStorage", "Download Completed! -- " + localFile.toString());
                                finish();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("MyData-File ", ";local tem file not created  created " + e.toString());
                                Toast.makeText(ResultsDownloadSection.this, "Download Failed!", Toast.LENGTH_LONG).show();
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