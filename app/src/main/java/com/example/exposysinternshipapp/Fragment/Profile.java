package com.example.exposysinternshipapp.Fragment;

import static android.app.Activity.RESULT_OK;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.exposysinternshipapp.Activity.Authentication;
import com.example.exposysinternshipapp.MainActivity;
import com.example.exposysinternshipapp.Models.User;
import com.example.exposysinternshipapp.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class Profile extends Fragment {

    TextView location,name,bio,phone,email,resumeFile;
    ImageView profileImage,popup,file;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    View resume;
    ShimmerFrameLayout profileShimmer;
    ConstraintLayout profileView;
    private static final int REQUEST_CODE_UPLOAD_RESUME = 123;
    ImageView backbtn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        location = view.findViewById(R.id.profile_location);
        name = view.findViewById(R.id.profile_name);
        bio = view.findViewById(R.id.profile_bio);
        phone = view.findViewById(R.id.profile_phone);
        email = view.findViewById(R.id.profile_email);
        resume = view.findViewById(R.id.profile_resume_upload);
        profileImage = view.findViewById(R.id.profile_image);
        popup = view.findViewById(R.id.profile_setting);
        profileView =view.findViewById(R.id.profileView);
        resumeFile = view.findViewById(R.id.profile_resume_filename);
        profileShimmer = view.findViewById(R.id.profile_shimmerview);
        file = view.findViewById(R.id.profile_pdf);
        backbtn = view.findViewById(R.id.profile_backbtn);

        profileShimmer.startShimmer();
        
        backbtn.setOnClickListener(e ->exitApp());
        resume.setOnClickListener(e->checkResume());
        popup.setOnClickListener(e->popUp());

        fetchData();
        return view;
    }

    private void exitApp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Exit App")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Exit the app
                    getActivity().finish();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // Dismiss the dialog
                    dialog.dismiss();
                })
                .show();
    }

    private void uploadResume() {
        // Create an intent to select a file from the device storage
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf"); // Set the MIME type to PDF files
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select Resume"), REQUEST_CODE_UPLOAD_RESUME);
    }

    private void checkResume(){

            String existingResumeText = resumeFile.getText().toString();
            if (existingResumeText.equals("resume.pdf")) {
                // Show a dialog box to provide options for uploading a new resume or opening the existing one
                showResumeOptionsDialog();
            } else {
                // No resume uploaded yet, proceed with selecting a new resume
                uploadResume();
            }

    }
    private void showResumeOptionsDialog() {
        // Display a dialog to let the user choose an option
        // For example, you can use an AlertDialog with options to upload a new resume or open the existing one
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Resume Options")
                .setMessage("A resume is already uploaded. What would you like to do?")
                .setPositiveButton("Upload New", (dialog, which) -> uploadResume())
                .setNegativeButton("Open Existing", (dialog, which) -> openExistingResume())
                .setCancelable(true)
                .show();
    }
    private void openExistingResume() {
        // Get the current user ID
        String currentUserId = auth.getCurrentUser().getUid();

        // Create a Firebase Storage reference for the resume file
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        String filename = "resume.pdf"; // Assuming the resume file has a fixed filename
        StorageReference resumeRef = storageRef.child("profileResume/" + currentUserId + "/" + filename);

        // Get the URL of the resume file
        resumeRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String resumeUrl = uri.toString();

            // Launch a PDF viewer app with the resume file
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(resumeUrl), "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                // Handle the case where a PDF viewer app is not available on the device
                Toast.makeText(getActivity(), "No PDF viewer app found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(exception -> {
            // Handle any errors that occur while retrieving the resume URL
            Toast.makeText(getActivity(), "Failed to retrieve resume URL", Toast.LENGTH_SHORT).show();
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_UPLOAD_RESUME && resultCode == RESULT_OK) {
            if (data != null) {
                Uri resumeUri = data.getData();
                if (resumeUri != null) {
                    // Get the current user ID
                    String currentUserId = auth.getCurrentUser().getUid();

                    // Create a Firebase Storage reference for the resume file
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                    String filename = "resume.pdf"; // Set a fixed filename for the resume file
                    StorageReference resumeRef = storageRef.child("profileResume/" + currentUserId + "/" + filename);

                    // Upload the resume file to Firebase Storage
                    UploadTask uploadTask = resumeRef.putFile(resumeUri);
                    uploadTask.addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            databaseReference.child("users").child(auth.getCurrentUser().getUid()).child("resumeStatus").setValue(true);
                            file.setImageResource(R.drawable.pdf);
                            resumeFile.setText("resume.pdf");
                            Toast.makeText(getActivity(), "Resume uploaded successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            // Resume upload failed
                            Toast.makeText(getActivity(), "Failed to upload resume", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }
    }

    private void popUp() {
        PopupMenu popupMenu = new PopupMenu(getActivity(), popup);
        popupMenu.getMenuInflater().inflate(R.menu.menu_edit_profile, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_edit_profile:
                     ((MainActivity)getActivity()).replaceFragment(new EditProfile());
                    return true;
                case R.id.menu_privacy_policy:
                    Toast.makeText(getContext(),"privacy policy of this app is still not defined", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.menu_sign_out:
                     FirebaseAuth.getInstance().signOut();
                     Intent intent = new Intent(getActivity(), Authentication.class);
                     startActivity(intent);
                     getActivity().finish();
                    return true;
                default:
                    return false;
            }
        });

        popupMenu.show();
    }

    private void updateUi(User user) {
        location.setText(user.getLocation());
        phone.setText(user.getPhoneNumber());
        name.setText(user.getUserName());
        email.setText(user.getEmail());
        bio.setText(user.getBio());
        Picasso.get().load(user.getProfileImage()).placeholder(R.drawable.user1).into(profileImage);
        if(user.isResumeStatus()){
            file.setImageResource(R.drawable.pdf);
            resumeFile.setText("resume.pdf");
        }
        makeUiVisible();
    }

    private void fetchData(){
        databaseReference.child("users").child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user  =  snapshot.getValue(User.class);
                updateUi(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }
    private void makeUiVisible(){
        profileShimmer.stopShimmer();
        profileShimmer.setVisibility(View.GONE);
        profileView.setVisibility(View.VISIBLE);
    }
}