package be.kuleuven.gt.myapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Task implements Parcelable {

    //instantiations

    private String name;
    private String deadline;
    private String comment;
    private boolean select;
    private boolean pin;
    private boolean done;
    private String creationdate;
    private int accountid;
    private int groupid;
    private String color = "#87C8CF";

    //Constructors

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    public Task(String name, String deadline, String comment, boolean pin, boolean done, String creationdate, int accountid, int groupid) {
        this.name = name;
        this.deadline = deadline;
        this.comment = comment;
        this.pin = pin;
        this.done = done;
        this.creationdate = creationdate;
        this.accountid = accountid;
        this.groupid = groupid;
    }

    public Task(String name, String deadline, String comment, boolean pin, String group, int accountid) {
        this.name = name;
        this.deadline = deadline;
        this.comment = comment;
        select = false;
        Date currentDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yy-MM-dd");//dd-MM-yy
        creationdate = formatter.format(currentDate);
        this.pin = pin;
        this.accountid = accountid;
        this.groupid = Integer.parseInt(group);
        done = false;
    }

    //JSON setup and Parcel

    public Task(JSONObject object) {
        try {
            String name = object.getString("name");
            if (name.contains("-_-_-")) {
                name = name.replace("-_-_-", " ");
            }
            this.name = name;
            deadline = object.getString("deadline");
            comment = object.getString("comment");
            pin = object.getString("pin").contains("true");
            done = object.getString("done").contains("true");
            creationdate = object.getString("createdate");
            accountid = object.getInt("accountid");
            groupid = object.getInt("groupid");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Task(Parcel in) {
        this(
                in.readString(),
                in.readString(),
                in.readString(),
                Boolean.parseBoolean(in.readString()),
                Boolean.parseBoolean(in.readString()),
                in.readString(),
                Integer.parseInt(in.readString()),
                Integer.parseInt(in.readString())
        );
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(deadline);
        parcel.writeString(comment);
        parcel.writeString(Boolean.valueOf(pin).toString());
        parcel.writeString(Boolean.valueOf(done).toString());
        parcel.writeString(creationdate);
        parcel.writeString(String.valueOf(accountid));
        parcel.writeString(String.valueOf(groupid));
    }

    public Map<String, String> getPostParameters() {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("deadline", deadline);
        params.put("comment", comment);
        params.put("pin", String.valueOf(pin));
        params.put("done", String.valueOf(done));
        params.put("createdate", creationdate);
        params.put("accountid", String.valueOf(accountid));
        params.put("groupid", String.valueOf(groupid));
        return params;
    }

    public Map<String, String> getPostParametersDelete(int accountid) {
        Map<String, String> params = new HashMap<>();

        if (name.contains(" ")) {
            name = name.replace(" ", "-_-_-");
        }
        params.put("name", name);
        params.put("accountid", accountid + "");

        return params;
    }

    public Map<String, String> getPostParametersEdit(String original_name, int accountid) {
        Map<String, String> params = new HashMap<>();

        if (name.contains(" ")) {
            name = name.replace(" ", "-_-_-");
        }

        if (original_name.contains(" ")) {
            original_name = original_name.replace(" ", "-_-_-");
        }

        params.put("newname", name);
        params.put("groupid", groupid + "");
        params.put("deadline", deadline);
        params.put("comment", comment);
        params.put("pin", pin + "");
        params.put("name", original_name);
        params.put("accountid", accountid + "");

        return params;
    }

    @Override
    public String toString() {
        return "Task{" +
                "accountid=" + accountid +
                ", groupid=" + groupid +
                ", comment='" + comment + '\'' +
                ", pin=" + pin +
                ", name='" + name + '\'' +
                ", deadline='" + deadline + '\'' +
                ", creationdate='" + creationdate + '\'' +
                ", done=" + done +
                '}';
    }

    //Setters and Getters

    public void setSelect(boolean select) {
        this.select = select;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public String getDeadline() {
        return deadline;
    }

    public boolean isSelect() {
        return select;
    }

    public int getGroupid() {
        return groupid;
    }

    public int getAccountid() {
        return accountid;
    }

    public String getCreationDate() {
        return creationdate;
    }

    public String getComment() {
        return comment;
    }

    public boolean isPin() {
        return pin;
    }

    public void setGroupid(int groupid) {
        this.groupid = groupid;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setPin(boolean pin) {
        this.pin = pin;
    }

    public boolean isDone() {
        return done;
    }
}