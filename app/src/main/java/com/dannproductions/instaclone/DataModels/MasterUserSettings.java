package com.dannproductions.instaclone.DataModels;

public class MasterUserSettings {

    private User user;
    private UserAccountSettings accountSettings;

    public MasterUserSettings(User user, UserAccountSettings accountSettings) {
        this.user = user;
        this.accountSettings = accountSettings;
    }

    public MasterUserSettings() { }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserAccountSettings getAccountSettings() {
        return accountSettings;
    }

    public void setAccountSettings(UserAccountSettings accountSettings) {
        this.accountSettings = accountSettings;
    }

    @Override
    public String toString() {
        return "MasterUserSettings{" +
                "user=" + user +
                ", accountSettings=" + accountSettings +
                '}';
    }
}
