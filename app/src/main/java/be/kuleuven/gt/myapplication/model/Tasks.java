package be.kuleuven.gt.myapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class Tasks implements Parcelable {

    //instantiation
    private List<Task> tasks;

    //Constructors

    public Tasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public Tasks(Parcel in) {
        tasks = in.createTypedArrayList(Task.CREATOR);
    }

    public static final Creator<Tasks> CREATOR = new Creator<Tasks>() {
        @Override
        public Tasks createFromParcel(Parcel in) {
            return new Tasks(in);
        }

        @Override
        public Tasks[] newArray(int size) {
            return new Tasks[size];
        }
    };

    //Parcel

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int flags) {
        parcel.writeTypedList(tasks);
    }


    //Getter
    public List<Task> getTasks() {
        return tasks;
    }
}
