package edu.northeastern.new_final.ui.logWorkout;

public class Workout {
        private String activity;
        private double amount;
        private String date;
        private String amountCategory;
        private int energyPoints;

        public Workout() {
        }

        public Workout(String activity, double amount, String date, String amountCategory) {
            this.activity = activity;
            this.amount = amount;
            this.date = date;
            this.amountCategory = amountCategory;
            this.energyPoints = calculateEnergyPoints(amount, amountCategory);
        }

        private int calculateEnergyPoints(double amount, String amountCategory) {
            if (amountCategory.equals("time")) {
                return Math.round((float) amount / 10);
            } else {
                return Math.round((float) amount);
            }
        }

        public int getEnergyPoints() {
            return this.energyPoints;
        }
        public String getActivity() {
            return activity;
        }

        public void setActivity(String activity) {
            this.activity = activity;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getAmountCategory() {
            return amountCategory;
        }

        public void setAmountCategory(String amountCategory) {
            this.amountCategory = amountCategory;
        }

}
