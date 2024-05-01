package com.example.exposysinternshipapp.Models;
import android.os.Parcel;
import android.os.Parcelable;

public class Internship implements Parcelable {
    private String title;
    private String date;
    private String applicants;
    private String imageUrl;
    private String description;
    private String internshipUrl;

    public Internship() {
    }


    public String getInternshipUrl() {
        return internshipUrl;
    }

    public void setInternshipUrl(String internshipUrl) {
        this.internshipUrl = internshipUrl;
    }


    protected Internship(Parcel in) {
        title = in.readString();
        date = in.readString();
        applicants = in.readString();
        imageUrl = in.readString();
        description = in.readString();
        internshipUrl = in.readString();
    }

    public static final Creator<Internship> CREATOR = new Creator<Internship>() {
        @Override
        public Internship createFromParcel(Parcel in) {
            return new Internship(in);
        }

        @Override
        public Internship[] newArray(int size) {
            return new Internship[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getApplicants() {
        return applicants;
    }

    public void setApplicants(String applicants) {
        this.applicants = applicants;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(date);
        dest.writeString(applicants);
        dest.writeString(imageUrl);
        dest.writeString(description);
        dest.writeString(internshipUrl);
    }
}
