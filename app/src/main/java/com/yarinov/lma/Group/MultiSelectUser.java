package com.yarinov.lma.Group;

import java.io.Serializable;

public class MultiSelectUser implements Serializable {

    private String userName, userId;
    private boolean isSelected;

    public MultiSelectUser(String userName, String userId, boolean isSelected) {
        this.userName = userName;
        this.userId = userId;
        this.isSelected = isSelected;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public String toString() {
        return "MultiSelectUser{" +
                "userName='" + userName + '\'' +
                ", userId='" + userId + '\'' +
                ", isSelected=" + isSelected +
                '}';
    }
}
