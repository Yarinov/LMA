package com.yarinov.lma.Notification

class CustomNotification(
    var meetingId: String,
    var fromId: String,
    var fromName: String,
    var toId: String,
    var toName: String,
    var date: String,
    var time: String,
    var place: String,
    var type: String,
    var status: String,
    var mood: String,
    var datePosted: String
) {

    override fun toString(): String {
        return "CustomNotification(meetingId='$meetingId', fromId='$fromId', fromName='$fromName', toId='$toId', toName='$toName', date='$date', time='$time', place='$place', type='$type', status='$status', mood='$mood', datePosted='$datePosted')"
    }
}