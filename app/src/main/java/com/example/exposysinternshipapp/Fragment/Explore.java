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

import com.example.exposysinternshipapp.Adapters.ExploreAdapter;
import com.example.exposysinternshipapp.Models.Internship;
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


public class Explore extends Fragment {
    RecyclerView exploreRv;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    ShimmerFrameLayout exploreShimmer;
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

        View view = inflater.inflate(R.layout.fragment_explore, container, false);
        exploreShimmer =view.findViewById(R.id.explore_shimmer);
        exploreRv = view.findViewById(R.id.explore_rv);
        exploreRv.setNestedScrollingEnabled(false);
        exploreRv.setLayoutManager(new LinearLayoutManager(getContext()));
        backbtn = view.findViewById(R.id.backbtn_internship);

        backbtn.setOnClickListener(e->exitApp());
        exploreShimmer.startShimmer();

         updateInternships();

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
    private void updateInternships(){
        List<Internship> internships = new ArrayList<>();
        databaseReference.child("internships").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()){
                    Internship internship = child.getValue(Internship.class);
                    internships.add(internship);

                }
                exploreRv.setAdapter(new ExploreAdapter(internships,getActivity()));
                makeUiVisible();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void makeUiVisible(){
        exploreShimmer.stopShimmer();
        exploreShimmer.setVisibility(View.GONE);
        exploreRv.setVisibility(View.VISIBLE);
    }
}