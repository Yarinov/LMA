package com.yarinov.lma.Notification

class HomeNotification(
    meetingId: String,
    fromId: String,
    var fromName: String,
    var toId: String,
    var toName: String,
    date: String,
    time: String,
    place: String,
    var type: String,
    var status: String,
    var mood: String,
    datePosted: String
) : Notification(meetingId, fromId, date, time, place, datePosted) {


    override fun toString(): String {
        return "CustomNotification(meetingId='$meetingId', fromId='$fromId', fromName='$fromName', toId='$toId', toName='$toName', date='$date', time='$time', place='$place', type='$type', status='$status', mood='$mood', datePosted='$datePosted')"
    }
}