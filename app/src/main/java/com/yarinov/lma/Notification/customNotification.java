package com.yarinov.lma.Notification;

public class customNotification {

    private  String fromId, fromName, toId, toName, date, place, type;

    public customNotification(String fromId, String fromName, String toId, String toName, String date, String place, String type) {
        this.fromId = fromId;
        this.fromName = fromName;
        this.toId = toId;
        this.toName = toName;
        this.date = date;
        this.place = place;
        this.type = type;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "customNotification{" +
                "fromId='" + fromId + '\'' +
                ", fromName='" + fromName + '\'' +
                ", toId='" + toId + '\'' +
                ", toName='" + toName + '\'' +
                ", date='" + date + '\'' +
                ", place='" + place + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
