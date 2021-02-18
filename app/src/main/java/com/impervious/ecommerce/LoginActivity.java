package com.impervious.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.impervious.ecommerce.Model.User;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private EditText phoneId, passId;
    private Button loginButton;
    private CheckBox rememberBox;
    private TextView forgot_pass;

    private ProgressDialog progressDialog;
    private String phone, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        phoneId = findViewById(R.id.phoneId);
        passId = findViewById(R.id.passId);
        loginButton = findViewById(R.id.loginButton);
        rememberBox = findViewById(R.id.rememberBox);
        forgot_pass = findViewById(R.id.forgot_pass);
        progressDialog = new ProgressDialog(this);
        Paper.init(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                phone = phoneId.getText().toString();
                pass = passId.getText().toString();

                if (phone.isEmpty()) {
                    phoneId.setError("This field can't be Empty!");
                } else if (pass.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "enter a password first.\nplease try again...",
                            Toast.LENGTH_SHORT).show();
                } else if (pass.length() < 6) {
                    Toast.makeText(LoginActivity.this, "password must be at least 6 character.\nplease try again...",
                            Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.setTitle("Logging in to the Account...");
                    progressDialog.setMessage("please wait, while we are checking the credentials...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    checkRemember();
                    loginAccount();
                }

            }
        });

    }

    //storing data for checked remember_me box
    private void checkRemember() {
        if (rememberBox.isChecked()) {

            Paper.book().write(Constants.BOOK_USER_PHONE_KEY, phone);
            Paper.book().write(Constants.BOOK_USER_PASS_KEY, pass);

        }
    }


    private void loginAccount() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constants.PARENT_DB_NAME);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child(phone).exists()) {

                    User currentUser = snapshot.child(phone).getValue(User.class);

                    if (currentUser != null) {
                        if (currentUser.getPhone().equals(phone)) {
                            if (currentUser.getPassword().equals(pass)) {

                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Logged in successfully...", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);


                            }
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Network Issue: something went wrong\n" +
                                "please try again...", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(LoginActivity.this, "Account whit this " + phone + " number doesn't exits.",
                            Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "please, create a new Account.\n" +
                            "or try again...", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });

    }


}