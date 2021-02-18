package com.impervious.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.impervious.ecommerce.Model.User;

import io.paperdb.Paper;

public class StartActivity extends AppCompatActivity {

    private LottieAnimationView lottieAnimationView;
    private TextView app_slogan;
    private Button register_btn, login_btn;
    private int check_auto_logIn_onCreate = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        lottieAnimationView = findViewById(R.id.animationView);
        app_slogan = findViewById(R.id.app_slogan);
        register_btn = findViewById(R.id.register_btn);
        login_btn = findViewById(R.id.login_btn);

        //auto login after animation if remembered
        playAnimation();

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(StartActivity.this, RegisterActivity.class));

            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(StartActivity.this, LoginActivity.class));

            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();

        if (check_auto_logIn_onCreate != 0) {
            playAnimation();
        }
    }

    private void playAnimation() {
        app_slogan.animate().alpha(0).setDuration(0);
        lottieAnimationView.playAnimation();

        lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                app_slogan.setVisibility(View.VISIBLE);
                app_slogan.animate().alpha(1.0f).setDuration(250);

                if (check_auto_logIn_onCreate == 0) {

                    if (!rememberLogIn()) {
                        register_btn.setVisibility(View.VISIBLE);
                        login_btn.setVisibility(View.VISIBLE);
                    }

                    check_auto_logIn_onCreate = 1;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }

    private boolean rememberLogIn() {
        Paper.init(this);

        String phone = Paper.book().read(Constants.BOOK_USER_PHONE_KEY); //user false
        String pass = Paper.book().read(Constants.BOOK_USER_PASS_KEY); //true

        if (phone != "" && pass != "") {
            if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(pass)) {

                loginAccount(phone, pass);
                return true;

            } else
                return false;
        } else
            return false;
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
                                Toast.makeText(StartActivity.this, "Logged in successfully...", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);

                            }
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(StartActivity.this, "Network Issue: something went wrong\n" +
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