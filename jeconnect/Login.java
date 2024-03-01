package com.pranavamrute.jeconnect;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {

    FirebaseFirestore db;
    TextInputEditText username, password;
    Button signInBtn;
    FirebaseAuth firebaseAuth;

    private long pressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        username = findViewById(R.id.title_complaint);
        password = findViewById(R.id.signin_password);
        signInBtn = findViewById(R.id.signin_btn);

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Toast.makeText(Login.this, username.getText(), Toast.LENGTH_SHORT).show();
                //Toast.makeText(Login.this, password.getText(), Toast.LENGTH_SHORT).show();

                if (!TextUtils.isEmpty(username.getText()) && !TextUtils.isEmpty(password.getText())) {
                    db.collection("Users").document(String.valueOf(username.getText()))
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    Log.i("MYData-Credentials", documentSnapshot.get("Username") + " => " + documentSnapshot.get("Password") + "--" + password.getText());

                                    if (String.valueOf(password.getText()).equals(documentSnapshot.get("Password"))) {
                                        Log.i("MYData-Credentials", "Login Successfully!!");
                                        Toast.makeText(Login.this, "Login Successful!", Toast.LENGTH_SHORT).show();

                                        firebaseAuth.signInAnonymously()
                                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                        Log.d("MyData-FirebaseAuth","Login Successfully!!");
                                                    }
                                                });

                                        /*Store the details in public class*/
                                        MyData.USERNAME = documentSnapshot.get("Username").toString();
                                        MyData.TYPE_OF_USER = documentSnapshot.get("TypeOfUser").toString();
                                        MyData.PASSWORD = documentSnapshot.get("Password").toString();

                                        //Shift the screen from login to main
                                        Intent intent = new Intent(Login.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Log.e("MYData-Credentials", "Failed to login!!");
                                        Toast.makeText(Login.this, "Password is incorrect!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("MYData-Credential", "Failed to get db data : " + e.getLocalizedMessage());
                                    Toast.makeText(Login.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(Login.this, "Please fill all details!", Toast.LENGTH_LONG).show();
                }

            }
        });


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