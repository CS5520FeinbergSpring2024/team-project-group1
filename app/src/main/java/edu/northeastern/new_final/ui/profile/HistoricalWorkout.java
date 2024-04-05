package edu.northeastern.new_final.ui.profile;

public class HistoricalWorkout {

    private String activity;
    private String date;
    private String amount;

    private String amountCategory;
    private String energyPoints;

    public HistoricalWorkout() {
        // Default constructor required for Firebase deserialization
    }

    public HistoricalWorkout(String activity, String date, String amount, String amountCategory, String energyPoints) {
        this.activity = activity;
        this.date = date;
        this.amount = amount;
        this.amountCategory = amountCategory;
        this.energyPoints = energyPoints;
    }

    // Getters
    public String getActivity() {
        return activity;
    }

    public String getDate() {
        return date;
    }

    public String getAmount() {
        return amount;
    }

    public String getAmountCategory() {
        return amountCategory;
    }

    public String getEnergyPoints() {
        return energyPoints;
    }

    // Setters
    public void setActivity(String activity) {
        this.activity = activity;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setAmount(String metricAmount) {
        this.amount = amount;
    }

    public void setAmountCategory(String metricType) {
        this.amountCategory = amountCategory;
    }

    public void setEnergyPoints(String energyPoints) {this.energyPoints = energyPoints;}
}



