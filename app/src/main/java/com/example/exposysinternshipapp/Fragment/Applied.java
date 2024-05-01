package com.example.exposysinternshipapp.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exposysinternshipapp.Adapters.AppliedAdapter;
import com.example.exposysinternshipapp.Models.AppliedModel;
import com.example.exposysinternshipapp.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Applied extends Fragment {
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    private List<AppliedModel> appliedInternships;
    ImageView noContent;
    RecyclerView recommendedRv;
    ShimmerFrameLayout appliedShimmer;
    ImageView backbtn;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        appliedInternships = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_applied, container, false);
         noContent = view.findViewById(R.id.applied_fragment_no_content);
         recommendedRv = view.findViewById(R.id.rv_applied);
         appliedShimmer = view.findViewById(R.id.applied_shimmer);
         backbtn = view.findViewById(R.id.backbtn_applied);

         backbtn.setOnClickListener(e->exitApp());

         appliedShimmer.startShimmer();

         recommendedRv.setLayoutManager(new LinearLayoutManager(getContext()));
         recommendedRv.setNestedScrollingEnabled(false);

         fetchAppliedInternship();
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
    private void fetchAppliedInternship(){
        databaseReference.child("users").child(auth.getCurrentUser().getUid()).child("appliedInternship").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot child :  snapshot.getChildren()){
                    AppliedModel appliedModel = child.getValue(AppliedModel.class);
                    appliedInternships.add(appliedModel);
                }
                updateUi();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateUi() {

        if(appliedInternships.size() == 0){
            noContent.setVisibility(View.VISIBLE);
        }else {
            recommendedRv.setAdapter(new AppliedAdapter(appliedInternships));
        }
        makeUiVisible();
    }
    private void makeUiVisible(){
        appliedShimmer.stopShimmer();
        appliedShimmer.setVisibility(View.GONE);
        recommendedRv.setVisibility(View.VISIBLE);
    }
}