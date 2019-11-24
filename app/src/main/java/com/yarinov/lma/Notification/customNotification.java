package com.yarinov.lma.Notification;

public class customNotification {

    private  String meetingId, fromId, fromName, toId, toName, date, time,  place, type, status;

    public customNotification(String meetingId, String fromId, String fromName, String toId, String toName, String date, String time,  String place, String type, String status) {
        this.meetingId = meetingId;
        this.fromId = fromId;
        this.fromName = fromName;
        this.toId = toId;
        this.toName = toName;
        this.date = date;
        this.time = time;
        this.place = place;
        this.type = type;
        this.status = status;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "customNotification{" +
                "meetingId='" + meetingId + '\'' +
                ", fromId='" + fromId + '\'' +
                ", fromName='" + fromName + '\'' +
                ", toId='" + toId + '\'' +
                ", toName='" + toName + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", place='" + place + '\'' +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
