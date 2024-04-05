package edu.northeastern.new_final.ui.profile;

public class PersonalGoal {
    private String activity;
    private String date;
    private String metricAmount;
    private String metricType;

    public PersonalGoal() {
        // Default constructor required for Firebase deserialization
    }

    public PersonalGoal(String activity, String date, String metricAmount, String metricType) {
        this.activity = activity;
        this.date = date;
        this.metricAmount = metricAmount;
        this.metricType = metricType;
    }

    // Getters
    public String getActivity() {
        return activity;
    }

    public String getDate() {
        return date;
    }

    public String getMetricAmount() {
        return metricAmount;
    }

    public String getMetricType() {
        return metricType;
    }

    // Setters
    public void setActivity(String activity) {
        this.activity = activity;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setMetricAmount(String metricAmount) {
        this.metricAmount = metricAmount;
    }

    public void setMetricType(String metricType) {
        this.metricType = metricType;
    }
}

