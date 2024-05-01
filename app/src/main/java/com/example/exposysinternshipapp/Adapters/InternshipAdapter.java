package com.example.exposysinternshipapp.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exposysinternshipapp.Activity.ApplyInternship;
import com.example.exposysinternshipapp.Models.Internship;
import com.example.exposysinternshipapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class InternshipAdapter extends RecyclerView.Adapter<InternshipAdapter.Holder> {
    List<Internship> internshipList;
    Activity activity;
    public InternshipAdapter(List<Internship> internshipList, Activity activity) {
        this.internshipList = internshipList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_internship_single_design, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.title.setText(internshipList.get(position).getTitle());
        holder.date.setText(internshipList.get(position).getDate());
        holder.totalParticipant.setText(internshipList.get(position).getApplicants());
        Picasso.get().load(internshipList.get(position).getImageUrl()).into(holder.imageView);
        holder.cardView.setOnClickListener(e->internshipDetail(internshipList.get(position)));

    }

    private void internshipDetail(Internship internship) {
        Intent intent = new Intent(activity, ApplyInternship.class);
        intent.putExtra("internship",internship);
        activity.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return internshipList.size();
    }

    class Holder extends RecyclerView.ViewHolder{
        TextView date,title,totalParticipant;
        ImageView imageView;
        CardView cardView;
        public Holder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.intenship_single_design_date);
            title = itemView.findViewById(R.id.intenship_single_design_title);
            totalParticipant = itemView.findViewById(R.id.intenship_single_design_total_applicant);
            imageView = itemView.findViewById(R.id.intenship_single_design_image);
            cardView = itemView.findViewById(R.id.sinlge_internship_cardview);
        }
    }

}
