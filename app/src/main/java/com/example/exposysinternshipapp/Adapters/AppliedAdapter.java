package com.example.exposysinternshipapp.Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exposysinternshipapp.Models.AppliedModel;
import com.example.exposysinternshipapp.Models.Internship;
import com.example.exposysinternshipapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AppliedAdapter extends RecyclerView.Adapter<AppliedAdapter.Holder> {
    List<AppliedModel> internshipList;
    DatabaseReference databaseReference;

    public AppliedAdapter(List<AppliedModel> internshipList) {
        this.internshipList = internshipList;
        databaseReference = FirebaseDatabase.getInstance().getReference();

    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_intership_applied_single_design, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        databaseReference.child("internships").child(internshipList.get(position).getInternshipUrl()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Internship internship = snapshot.getValue(Internship.class);
                holder.title.setText(internship.getTitle());
                holder.date.setText(internship.getDate());
                Picasso.get().load(internship.getImageUrl()).into(holder.icon);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.status.setText(internshipList.get(position).getStatus());
        switch (internshipList.get(position).getStatus()){
            case "pending": holder.status.setBackgroundColor(Color.parseColor("#fabc04"));
                break;
            case "accepted": holder.status.setBackgroundColor(Color.parseColor("#23b23b"));
                break;
            case "rejected" : holder.status.setBackgroundColor(Color.parseColor("#dc4744"));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return internshipList.size();
    }

    class Holder extends RecyclerView.ViewHolder{
        TextView date,title,status;
        ImageView icon;
        public Holder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.rv_applied_date);
            title = itemView.findViewById(R.id.rv_applied_name);
            status = itemView.findViewById(R.id.rv_applied_status);
            icon = itemView.findViewById(R.id.rv_applied_image);

        }
    }
}
