package edu.northeastern.new_final.ui.challengeGroup;

public class GroupMessage {
    private String username;
    private String date;
    private String message;

    public GroupMessage() {
    }

    public GroupMessage(String username, String date, String message) {
        this.username = username;
        this.date = date;
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}