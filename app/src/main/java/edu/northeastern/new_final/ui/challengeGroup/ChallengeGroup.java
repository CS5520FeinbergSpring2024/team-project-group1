package edu.northeastern.new_final.ui.challengeGroup;

import android.util.Log;

import java.util.ArrayList;

public class ChallengeGroup {
    private String groupName;
    private String description;
    private String groupProfileImg;
    private String activityType;
    private double amount;
    private String amountCategory;
    private String dueDate;
    private ArrayList<String> members;

    public ChallengeGroup() {}
    public ChallengeGroup(String groupName,
                          String description,
                          String groupProfileImg,
                          String activityType,
                          String amountStr,
                          String amountCategory,
                          String dueDate,
                          ArrayList<String> members) {
        this.groupName = groupName;
        this.description = description;
        this.groupProfileImg = groupProfileImg;
        this.activityType = activityType;
        this.amountCategory = amountCategory;
        this.dueDate = dueDate;
        this.members = members;

        try {
            this.amount = !amountStr.equals("") ? Double.parseDouble(amountStr) : -1;
        } catch (Exception e) {
            Log.e("amount conversion to double error", String.valueOf(e));
        }
    }

    public String getGroupName() {
        return this.groupName;
    }

    public String getDescription() {
        return this.description;
    }

    public String getGroupProfileImg() {
        return this.groupProfileImg;
    }

    public String getActivityType() {
        return this.activityType;
    }

    public double getAmount() {
        return this.amount;
    }

    public String getAmountCategory() {
        return this.amountCategory;
    }

    public String getDueDate() {
        return this.dueDate;
    }

    public ArrayList<String> getMembers() {
        return this.members;
    }

    public void setGroupName(String newGroupName) {
        this.groupName = newGroupName;
    }

    public void setDescription(String newDescription) {
        this.description = newDescription;
    }

    public void setGroupProfileImg(String newGroupProfileImg) {
        this.groupProfileImg = newGroupProfileImg;
    }

    public void setActivityType(String newActivityType) {
        this.activityType = newActivityType;
    }

    public void setAmount(double newAmount) {
        this.amount = newAmount;
    }

    public void setAmountCategory(String newAmountCategory) {
        this.amountCategory = newAmountCategory;
    }

    public void setDueDate(String newDueDate) {
        this.dueDate = newDueDate;
    }

    public void setMembers(ArrayList<String> newMembers) {
        this.members = newMembers;
    }


}
