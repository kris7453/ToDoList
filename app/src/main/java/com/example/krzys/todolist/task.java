package com.example.krzys.todolist;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by krzys on 02.01.2018.
 */

public class task implements Parcelable{

    private int id;
    private String name;
    private String date; // yyyy-mm-dd
    private priorityLevel priority;
    private boolean done;
    private boolean attachments;


    public enum priorityLevel
    {
      low(0),
      averange(1),
      high(2);

        private int value;

        priorityLevel(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value%3;
        }
    }

    public task(int id, String name, String date, priorityLevel priority, boolean done, boolean attachments) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.priority = priority;
        this.done = done;
        this.attachments = attachments;
    }

    protected task(Parcel in) {
        id = in.readInt();
        name = in.readString();
        date = in.readString();
        priority = priorityLevel.values()[in.readInt()];
        done = in.readByte() != 0;
        attachments = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(date);
        dest.writeInt(priority.ordinal());
        dest.writeByte((byte) (done ? 1 : 0));
        dest.writeByte((byte) (attachments ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    public static final Creator<task> CREATOR = new Creator<task>() {
        @Override
        public task createFromParcel(Parcel in) {
            return new task(in);
        }

        @Override
        public task[] newArray(int size) {
            return new task[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public priorityLevel getPriority() {
        return priority;
    }

    public void setPriority(priorityLevel priority) {
        this.priority = priority;
    }

    public void setPriority(int priority) {
        this.priority.setValue(priority);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public boolean isAttachments() {
        return attachments;
    }

    public void setAttachments(boolean attachments) {
        this.attachments = attachments;
    }

    @Override
    public String toString() {
        return id + " " + name + " " + date + " " + Integer.toString(priority.getValue()) + " " + (done ? "true" : "false") + " " + (attachments ? "true" : "false") + " ";
    }
}

