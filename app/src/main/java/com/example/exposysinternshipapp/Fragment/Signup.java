package com.example.exposysinternshipapp.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.exposysinternshipapp.MainActivity;
import com.example.exposysinternshipapp.Models.User;
import com.example.exposysinternshipapp.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Signup extends Fragment {

    private TextView changeToLogin;
    private EditText username,password,email;
    private Button signUpButton, googleSignUpButton;
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInOptions gso;
    private DatabaseReference usersDatabaseReference;
    private ProgressBar progressBar;
    private View layoutView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        changeToLogin = view.findViewById(R.id.signup_fragment_login);
        email = view.findViewById(R.id.signup_fragment_email);
        username = view.findViewById(R.id.signup_fragment_username);
        password = view.findViewById(R.id.signup_fragment_password);
        signUpButton = view.findViewById(R.id.signup_fragment_signup_btn);
        googleSignUpButton = view.findViewById(R.id.signup_fragment_signup_google);
        layoutView  = view.findViewById(R.id.signup_dark_overlay);
        progressBar = view.findViewById(R.id.signup_progress_bar);

        password.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                int drawableRightWidth = password.getCompoundDrawables()[2].getBounds().width();
                if (event.getRawX() >= (password.getRight() - drawableRightWidth)) {
                    if (password.getCompoundDrawables()[2].getConstantState().equals(getResources().getDrawable(R.drawable.password).getConstantState())) {
                        password.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.visibility), null);
                        password.setTransformationMethod(null);
                    } else {
                        password.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.password), null);
                        password.setTransformationMethod(new PasswordTransformationMethod());
                    }
                    return true;
                }
            }
            return false;
        });
        changeToLogin.setOnClickListener(v -> changeToLogIn());
        signUpButton.setOnClickListener(v -> signUp());
        googleSignUpButton.setOnClickListener(v -> googleSignUp());

        return view;
    }
    public void changeToLogIn(){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.authorization_container, new Login()).commit();
    }

    public void signUp() {
        String userEmail = email.getText().toString().trim();
        String pass = password.getText().toString().trim();
        String userName = username.getText().toString().trim();
        if (TextUtils.isEmpty(userEmail)) {
            email.setError("Enter your email");
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            password.setError("Enter password");
            return;
        }
        if (TextUtils.isEmpty(userName)) {
            username.setError("Enter your username");
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        layoutView.setVisibility(View.VISIBLE);
        if(email.getText().toString().equals(""))
        mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUi(user);
                        Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(requireActivity(), MainActivity.class));
                        requireActivity().finish();
                    } else {
                        progressBar.setVisibility(View.GONE);
                        layoutView.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Failed to create account.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    layoutView.setVisibility(View.GONE);
                    if (e instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(getContext(), "Email is already registered.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to create account.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void googleSignUp(){
        progressBar.setVisibility(View.VISIBLE);
        layoutView.setVisibility(View.VISIBLE);

        // Build a GoogleApiClient with access to the Google Sign-In API and the options specified by gso
        mGoogleApiClient = new GoogleApiClient.Builder(requireContext())
                .enableAutoManage(requireActivity(), connectionResult -> {
                    // Handle GoogleApiClient connection failure
                    Toast.makeText(getContext(), "Google Sign-In failed", Toast.LENGTH_SHORT).show();
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Launch Google Sign In activity
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Google Sign In activity
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed
                Toast.makeText(getContext(), "Google Sign-In failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {

                        String userId = mAuth.getCurrentUser().getUid();

                        // Check if the user already exists in the database
                        usersDatabaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.exists()) {
                                    updateUi(account);
                                }
                                Toast.makeText(getContext(), "Google Sign-In successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(requireActivity(), MainActivity.class));
                                requireActivity().finish();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(getContext(), "Database error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        progressBar.setVisibility(View.GONE);
                        layoutView.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Google Sign-In failed", Toast.LENGTH_SHORT).show();
                    }
                });
        }

        private void updateUi(GoogleSignInAccount account){
            User newUser = new User();
            newUser.setUserName(account.getDisplayName());
            newUser.setEmail(account.getEmail());
            newUser.setProfileImage("https://cdn-icons-png.flaticon.com/512/149/149071.png");
            usersDatabaseReference.child(mAuth.getCurrentUser().getUid()).setValue(newUser);
        }
        private void updateUi(FirebaseUser user){
            User newUser = new User();
            newUser.setUserName(username.getText().toString());
            newUser.setEmail(user.getEmail());
            newUser.setProfileImage("https://cdn-icons-png.flaticon.com/512/149/149071.png");
            usersDatabaseReference.child(mAuth.getCurrentUser().getUid()).setValue(newUser);
        }
    }
