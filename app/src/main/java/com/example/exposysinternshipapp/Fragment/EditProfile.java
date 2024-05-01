package com.example.exposysinternshipapp.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.exposysinternshipapp.MainActivity;
import com.example.exposysinternshipapp.Models.User;
import com.example.exposysinternshipapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditProfile extends Fragment {
    EditText name, location, bio, email, phone;
    ImageView profileImage, changeImage;
    Button submit;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    private Uri selectedImageUri;
    private ActivityResultLauncher<Intent> imageSelectionLauncher;
    ImageView backbtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        imageSelectionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            selectedImageUri = data.getData();
                            try {
                                Bitmap selectedImageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                                profileImage.setImageBitmap(selectedImageBitmap);
                                changeProfileImage();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Toast.makeText(getActivity(), "Image selection cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        name = view.findViewById(R.id.edit_profile_name);
        bio = view.findViewById(R.id.edit_profile_bio);
        location = view.findViewById(R.id.edit_profile_location);
        phone = view.findViewById(R.id.edit_profile_phone);
        email = view.findViewById(R.id.edit_profile_email);
        profileImage = view.findViewById(R.id.edit_profile_image);
        changeImage = view.findViewById(R.id.edit_profile_change_image);
        submit = view.findViewById(R.id.edit_profile_submit);
        backbtn = view.findViewById(R.id.backbtn_edit);

        backbtn.setOnClickListener(v->((MainActivity)getActivity()).replaceFragment(new Profile()));
        changeImage.setOnClickListener(e -> openImageSelection());
        submit.setOnClickListener(e->submit());
        updateUi();

        return view;
    }

    private void updateUi() {
        String currentUserId = auth.getCurrentUser().getUid();
        DatabaseReference userRef = databaseReference.child("users").child(currentUserId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        name.setText(user.getUserName());
                         location.setText(user.getLocation());
                         bio.setText(user.getBio());
                         email.setText(user.getEmail());
                         phone.setText(user.getPhoneNumber());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void submit() {
        String userEmail = email.getText().toString().trim();
        String userName = name.getText().toString().trim();
        String userLocation = location.getText().toString().trim();
        String userBio = bio.getText().toString().trim();
        String userPhone = phone.getText().toString().trim();

        if (userEmail.isEmpty() || userName.isEmpty()) {
            Toast.makeText(getContext(), "Some important fields are missing!", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference userRef = databaseReference.child("users").child(auth.getCurrentUser().getUid());
        userRef.child("userName").setValue(userName);
        userRef.child("location").setValue(userLocation);
        userRef.child("bio").setValue(userBio);
        userRef.child("phoneNumber").setValue(userPhone);
        userRef.child("email").setValue(userEmail);
        Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();

    }


    private void openImageSelection() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imageSelectionLauncher.launch(intent);
    }

    private void changeProfileImage() {
        if (selectedImageUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            String userId = auth.getCurrentUser().getUid();
            String filename = "profile.jpg"; // Fixed filename

            StorageReference imageRef = storageRef.child("profileImages/" + userId + "/" + filename);

            try {
                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);

                // Compress the image to reduce size
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
                byte[] imageData = baos.toByteArray();

                UploadTask uploadTask = imageRef.putBytes(imageData);

                uploadTask.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            saveImageUrlToProfile(imageUrl);
                        });
                    } else {
                        Exception exception = task.getException();
                        Toast.makeText(getActivity(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImageUrlToProfile(String imageUrl) {
       databaseReference.child("users").child(auth.getCurrentUser().getUid()).child("profileImage").setValue(imageUrl);
    }
}
