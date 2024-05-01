package com.example.exposysinternshipapp.Fragment;

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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.exposysinternshipapp.MainActivity;
import com.example.exposysinternshipapp.Models.User;
import com.example.exposysinternshipapp.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends Fragment {

    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    private TextView changeToSignup,forgotPassword;
    private EditText email, password;
    private Button loginButton, googleLoginButton;
    private GoogleSignInOptions gso;
    private ProgressBar progressBar;
    private View layoutView;
    private DatabaseReference usersDatabaseReference;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        changeToSignup = view.findViewById(R.id.login_fragment_signup);
        email = view.findViewById(R.id.login_fragment_email);
        forgotPassword = view.findViewById(R.id.login_forgot_password);
        password = view.findViewById(R.id.login_fragment_password);
        loginButton = view.findViewById(R.id.login_fragment_login_btn);
        googleLoginButton = view.findViewById(R.id.login_fragment_login_google);
        layoutView  = view.findViewById(R.id.dark_overlay);
        progressBar = view.findViewById(R.id.progress_bar);


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

        forgotPassword.setOnClickListener( v-> forgotPassword());
        loginButton.setOnClickListener(v -> loginWithEmail());
        googleLoginButton.setOnClickListener(v -> googleSignIn());

        changeToSignup.setOnClickListener(v -> changeToSignUp());
        return view;
    }

    private void forgotPassword() {
        String userEmail = email.getText().toString().trim();
        if (TextUtils.isEmpty(userEmail)) {
            email.setError("Enter your email");
            return;
        }
        mAuth.sendPasswordResetEmail(userEmail)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Reset password link sent to your email", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to send reset password link", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void changeToSignUp() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.authorization_container, new Signup()).commit();
    }

    private void loginWithEmail() {
        String emailText = email.getText().toString().trim();
        String passwordText = password.getText().toString();

        if (TextUtils.isEmpty(emailText)) {
            email.setError("Enter your email");
            return;
        }
        if (TextUtils.isEmpty(passwordText)) {
            password.setError("Enter password");
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        layoutView.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {

                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // User is logged in, start MainActivity or perform any desired actions
                            startActivity(new Intent(requireActivity(), MainActivity.class));
                            requireActivity().finish();
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        layoutView.setVisibility(View.GONE);

                        Toast.makeText(requireContext(), "Authentication failed. Please check your credentials.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void googleSignIn() {
        progressBar.setVisibility(View.VISIBLE);
        layoutView.setVisibility(View.VISIBLE);

        // Build the GoogleApiClient with access to the Google Sign-in API and the options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(requireContext())
                .enableAutoManage(requireActivity(), new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(requireContext(), "Google Play Services error.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Start Google Sign-in intent
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google sign-in was successful, authenticate with Firebase
                authenticateWithFirebase(result.getSignInAccount());
            } else {
                // Google sign-in failed, display a message to the user
                Snackbar.make(requireView(), "Google sign-in failed.", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void authenticateWithFirebase(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {

                        String userId = mAuth.getCurrentUser().getUid();

                        // Check if the user already exists in the database
                        usersDatabaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    // User already exists, skip updating the database
                                    Toast.makeText(getContext(), "Google Sign-In successful", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(requireActivity(), MainActivity.class));
                                    requireActivity().finish();
                                } else {
                                    // User doesn't exist, update the database with new user data
                                    updateUi(account);
                                    Toast.makeText(getContext(), "Google Sign-In successful", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(requireActivity(), MainActivity.class));
                                    requireActivity().finish();

                                }
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
}
