package com.example.exposysinternshipapp.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.exposysinternshipapp.Models.AppliedModel;
import com.example.exposysinternshipapp.Models.Internship;
import com.example.exposysinternshipapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ApplyInternship extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Spinner durationSpinner;
    ImageView icon;
    TextView description, title;
    Button apply;
    DatabaseReference appliedInternshipReference;
    ImageView backbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.internship_detail);
        durationSpinner = findViewById(R.id.durationSpinner);
        apply = findViewById(R.id.internship_detail_button);
        icon = findViewById(R.id.internship_detail_image);
        description = findViewById(R.id.internship_detail_description);
        backbtn = findViewById(R.id.backbtn);
        title = findViewById(R.id.internship_detail_name);

        backbtn.setOnClickListener(e->onBackPressed());

        Internship internship = (Internship)getIntent().getParcelableExtra("internship");

        setDurationSpinner();
        updateUi(internship);
        apply.setOnClickListener( e ->applyInternship(internship));

    }

    private void setDurationSpinner(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.duration_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        durationSpinner.setAdapter(adapter);
        durationSpinner.setOnItemSelectedListener(this);
    }

    private void applyInternship(Internship internship) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userReference = databaseReference.child("users").child(currentUserId);
        appliedInternshipReference = userReference.child("appliedInternship");

        String internshipUrl = internship.getInternshipUrl();

        appliedInternshipReference.child(internshipUrl).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // The user has already applied for this internship
                    Toast.makeText(ApplyInternship.this, "You have already applied for this internship", Toast.LENGTH_SHORT).show();
                } else {
                    DatabaseReference resumeStatusReference = userReference.child("resumeStatus");
                    resumeStatusReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                boolean resumeStatus = dataSnapshot.getValue(Boolean.class);
                                if (resumeStatus) {
                                    // User has resume status as true, proceed with application
                                    AppliedModel appliedModel = new AppliedModel();
                                    appliedModel.setStatus("pending");
                                    appliedModel.setApplicationDate(getCurrentDate());
                                    appliedModel.setInternshipUrl(internshipUrl);
                                    appliedModel.setDuration(durationSpinner.getSelectedItem().toString());

                                    appliedInternshipReference.child(internshipUrl).setValue(appliedModel)
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(ApplyInternship.this, "Internship applied successfully", Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(ApplyInternship.this, "Failed to apply for internship", Toast.LENGTH_SHORT).show();
                                            });
                                } else {
                                    // User has resume status as false, show dialog
                                    showResumeStatusDialog(internshipUrl);
                                }
                            } else {
                                // User has no resume status, show dialog
                                showResumeStatusDialog(internshipUrl);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(ApplyInternship.this, "Failed to check resume status", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ApplyInternship.this, "Failed to check application status", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showResumeStatusDialog(String internshipUrl) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Resume Status")
                .setMessage("Do you want to apply for the internship without a resume?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // User selected "Yes", proceed with application
                    AppliedModel appliedModel = new AppliedModel();
                    appliedModel.setStatus("pending");
                    appliedModel.setApplicationDate(getCurrentDate());
                    appliedModel.setInternshipUrl(internshipUrl);
                    appliedModel.setDuration(durationSpinner.getSelectedItem().toString());

                    appliedInternshipReference.child(internshipUrl).setValue(appliedModel)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(ApplyInternship.this, "Internship applied successfully", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(ApplyInternship.this, "Failed to apply for internship", Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // User selected "No", do nothing
                })
                .show();
    }

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    private void updateUi(Internship internship) {
        Picasso.get().load(internship.getImageUrl()).into(icon);
        title.setText(internship.getTitle());
        description.setText(internship.getDescription());

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedItem = parent.getItemAtPosition(position).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}