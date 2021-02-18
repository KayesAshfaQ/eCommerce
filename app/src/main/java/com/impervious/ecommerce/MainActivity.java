package com.impervious.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.impervious.ecommerce.Model.User;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private LottieAnimationView lottieAnimationView;
    private TextView app_slogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isLoggedIn();

        lottieAnimationView = findViewById(R.id.animationView);
        app_slogan = findViewById(R.id.app_slogan);
        Button register_btn = findViewById(R.id.register_btn);
        Button login_btn = findViewById(R.id.login_btn);


        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, RegisterActivity.class));

            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, LoginActivity.class));

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        lottieAnimationView.playAnimation();
        lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                app_slogan.setText("");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                app_slogan.setText(R.string.app_slogan);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }


    private void isLoggedIn() {

        Paper.init(this);

        String phone = Paper.book().read(Constants.BOOK_USER_PHONE_KEY); //user false
        String pass = Paper.book().read(Constants.BOOK_USER_PASS_KEY); //true

        if (phone != "" && pass != "") {
            if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(pass)) {

                loginAccount(phone, pass);

            }
        }

    }

    private void loginAccount(String phone, String pass) {

        ProgressDialog progressDialog = new ProgressDialog(this);
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
                                Toast.makeText(MainActivity.this, "Logged in successfully...", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MainActivity.this, HomeActivity.class));

                            }
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Network Issue: something went wrong\n" +
                                "please try again...", Toast.LENGTH_SHORT).show();
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}