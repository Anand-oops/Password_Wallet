package com.anand.android.passwordwallet;

public class EntryClass {
    int id;
    String name, user, pass, note;

    public EntryClass(int id, String name, String user, String pass, String note) {
        this.id = id;
        this.name = name;
        this.user = user;
        this.pass = pass;
        this.note = note;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }

    public String getNote() {
        return note;
    }
}
