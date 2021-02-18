package com.impervious.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameId, phoneId, passId;
    private Button registerButton;
    private TextView forgot_pass;

    private String name, phone, pass;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameId = findViewById(R.id.nameId);
        phoneId = findViewById(R.id.phoneId);
        passId = findViewById(R.id.passId);
        registerButton = findViewById(R.id.registerButton);
        forgot_pass = findViewById(R.id.forgot_pass);
        progressDialog = new ProgressDialog(this);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = nameId.getText().toString();
                phone = phoneId.getText().toString();
                pass = passId.getText().toString();

                if (name.isEmpty()) {
                    nameId.setError("This field can't be Empty!");
                } else if (phone.isEmpty()) {
                    phoneId.setError("This field can't be Empty!");
                } else if (pass.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "enter a password first.\nplease try again...",
                            Toast.LENGTH_SHORT).show();
                } else if (pass.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "password must be at least 6 character.\nplease try again...",
                            Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.setTitle("Creating Account...");
                    progressDialog.setMessage("please wait, while we are checking the credentials...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    createNewAccount();
                }

            }
        });

    }

    private void createNewAccount() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constants.PARENT_DB_NAME);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child(phone).exists()) {
                    Toast.makeText(RegisterActivity.this, "This " + phone + " id is already exists.", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "please try again with a different email...", Toast.LENGTH_SHORT).show();
                }else {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("username", name);
                    map.put("phone", phone);
                    map.put("password", pass);
                    reference.child(phone).setValue(map)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(RegisterActivity.this, "Congratulation...\n Your account is created.",
                                            Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                    RegisterActivity.this.finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), StartActivity.class));
                                    RegisterActivity.this.finish();
                                    Toast.makeText(RegisterActivity.this, "please try again...", Toast.LENGTH_SHORT).show();
                                }
                            });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public void goToLoginAction(View view) {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        RegisterActivity.this.finish();
    }

}