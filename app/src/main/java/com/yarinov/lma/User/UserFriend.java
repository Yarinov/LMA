package com.yarinov.lma.User;

public class UserFriend {

    private String userFriendId;
    private Boolean userFriendFlag;

    public UserFriend(String userFriendId, Boolean userFriendFlag) {
        this.userFriendId = userFriendId;
        this.userFriendFlag = userFriendFlag;
    }

    public String getUserFriendId() {
        return userFriendId;
    }

    public void setUserFriendId(String userFriendId) {
        this.userFriendId = userFriendId;
    }

    public Boolean getUserFriendFlag() {
        return userFriendFlag;
    }

    public void setUserFriendFlag(Boolean userFriendFlag) {
        this.userFriendFlag = userFriendFlag;
    }
}
