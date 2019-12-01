package com.yarinov.lma.Notification

open class Notification(var meetingId: String,
                        var fromId: String,
                        var date: String,
                        var time: String,
                        var place: String,
                        var datePosted: String) {

    override fun toString(): String {
        return "Notification(meetingId='$meetingId', fromId='$fromId', date='$date', time='$time', place='$place', datePosted='$datePosted')"
    }
}