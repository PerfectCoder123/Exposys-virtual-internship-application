package com.example.exposysinternshipapp.Fragment;

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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exposysinternshipapp.Adapters.InternshipAdapter;
import com.example.exposysinternshipapp.MainActivity;
import com.example.exposysinternshipapp.Models.Internship;
import com.example.exposysinternshipapp.Models.User;
import com.example.exposysinternshipapp.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Home extends Fragment {

    FirebaseAuth auth;
    DatabaseReference databaseReference;
    TextView username, seeAll, seeAll2;
    ImageView userImage;
    RecyclerView recommendedRv;
    RecyclerView newRv;

    ConstraintLayout homeVisible;
    ShimmerFrameLayout homeShimmer;
    TextView readMore;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        userImage = view.findViewById(R.id.home_profile_image);
        username = view.findViewById(R.id.home_username);
        seeAll = view.findViewById(R.id.home_see_all);
        seeAll2 = view.findViewById(R.id.home_see_all2);
        recommendedRv = view.findViewById(R.id.home_rv);
        newRv = view.findViewById(R.id.home_new_internship_rv);
        homeVisible = view.findViewById(R.id.home_visible);
        readMore = view.findViewById(R.id.readMore);
        homeShimmer = view.findViewById(R.id.home_shimmerview);

        homeShimmer.startShimmer();
        readMore.setOnClickListener(e->redirect());
        userImage.setOnClickListener(v->((MainActivity)getActivity()).replaceFragment(new Profile()));
        seeAll.setOnClickListener(v ->((MainActivity)getActivity()).replaceFragment(new Explore()));
        seeAll2.setOnClickListener(v ->((MainActivity)getActivity()).replaceFragment(new Explore()));
        recommendedRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        newRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        updateUi();
        updateInternships();


        return view;
    }

    private void redirect(){
        String websiteUrl = "http://www.exposysdata.in/";

        // Create an intent with the ACTION_VIEW action and the website URL
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(websiteUrl));

        // Check if there is a browser app available to handle the intent
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Start the activity with the intent
            startActivity(intent);
        } else {
            // No browser app is available, handle this case
            Toast.makeText(getActivity(), "No browser app found", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateInternships() {

        List<Internship> recommendInternships = new ArrayList<>();
        List<Internship> newInternships = new ArrayList<>();

        databaseReference.child("internships").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()){
                    Internship internship = child.getValue(Internship.class);
                    if(recommendInternships.size() < newInternships.size()) recommendInternships.add(internship);
                    else newInternships.add(internship);
                }
                recommendedRv.setAdapter(new InternshipAdapter(recommendInternships, getActivity()));
                newRv.setAdapter(new InternshipAdapter(newInternships,getActivity()));
                makeUiVisible();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateUi() {
        databaseReference.child("users").child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                Picasso.get().load(user.getProfileImage()).placeholder(R.drawable.user1).into(userImage);
                String firstName = user.getUserName().split(" ")[0];
                username.setText(firstName+"!");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void makeUiVisible(){
        homeShimmer.stopShimmer();
        homeShimmer.setVisibility(View.GONE);
        homeVisible.setVisibility(View.VISIBLE);
    }
}