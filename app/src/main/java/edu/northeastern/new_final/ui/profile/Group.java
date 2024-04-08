package edu.northeastern.new_final.ui.profile;

public class Group {

    private String groupName;
    private String imageName;
   private String energyPoints;

    public Group() {
        // Default constructor required for Firebase deserialization
    }

    public Group(String groupName, String imageName, String energyPoints) {
        this.groupName = groupName;
        this.imageName = imageName;
        this.energyPoints = energyPoints;
    }

    // Getters
    public String getGroupName() {
        return groupName;
    }

    public String getImageName() {
        return imageName;
    }

    public String getEnergyPoints() {
        return energyPoints;
    }

    // Setters
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public void setEnergyPoints(String energyPoints) {this.energyPoints = energyPoints;}
}


