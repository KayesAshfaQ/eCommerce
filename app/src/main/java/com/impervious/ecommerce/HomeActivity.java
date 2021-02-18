package com.impervious.ecommerce;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Paper.init(this);
    }

    public void Logout(View view) {

        Paper.book().delete(Constants.BOOK_USER_PHONE_KEY);
        Paper.book().delete(Constants.BOOK_USER_PASS_KEY);
        startActivity(new Intent(getApplicationContext(), MainActivity.class));

    }
}