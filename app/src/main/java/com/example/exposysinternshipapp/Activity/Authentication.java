package com.example.exposysinternshipapp.Activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.exposysinternshipapp.Fragment.Login;
import com.example.exposysinternshipapp.MainActivity;
import com.example.exposysinternshipapp.R;
import com.google.firebase.auth.FirebaseAuth;

public class Authentication extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.authorization_container, new Login()).commit();

    }

    @Override
    protected void onStart() {
        super.onStart();
       if(FirebaseAuth.getInstance().getCurrentUser() != null){
           Intent intent = new Intent(Authentication.this, MainActivity.class);
           startActivity(intent);
           finish();
       }
    }
}