package be.kuleuven.gt.myapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class Groups implements Parcelable {

    //instantiations

    private List<String> groups;
    private String groupName = "";
    private int groupId;

    //Constructors

    public Groups(List<String> groups) {
        this.groups = groups;
    }

    public Groups(List<String> groups, String groupName, int groupId) {
        this.groups = groups;
        this.groupName = groupName;
        this.groupId = groupId;
    }

    protected Groups(Parcel in) {
        groups = in.createStringArrayList();
        groupName = in.readString();
        groupId = in.readInt();
    }

    public static final Creator<Groups> CREATOR = new Creator<Groups>() {
        @Override
        public Groups createFromParcel(Parcel in) {
            return new Groups(in);
        }

        @Override
        public Groups[] newArray(int size) {
            return new Groups[size];
        }
    };

    //Setters and Getters

    public List<String> getGroups() {
        return groups;
    }

    public String getGroupName() {
        return groupName;
    }

    public int getGroupId() {
        return groupId;
    }

    //Parcel

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int flags) {
        parcel.writeStringList(groups);
        parcel.writeString(groupName);
        parcel.writeInt(groupId);
    }
}
