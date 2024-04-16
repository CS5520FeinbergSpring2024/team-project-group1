package edu.northeastern.new_final.ui.challengeGroup;

public class Contender {

    private String profileImageUrl;

    private String memberName;

    private String memberAmount;

    public Contender() {
        //Default for firebase
    }

    public Contender(String profileImageUrl, String memberName, String memberAmount) {
        this.profileImageUrl = profileImageUrl;
        this.memberName = memberName;
        this.memberAmount = memberAmount;
    }


    public String getProfileImageUrl() { return profileImageUrl;}

    public String getMemberName() { return memberName;}

    public String getMemberAmount() { return memberAmount;}

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public void setMemberAmount(String memberAmount) {
        this.memberAmount = memberAmount;
    }

}
