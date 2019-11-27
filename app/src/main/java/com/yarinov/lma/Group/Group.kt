package com.yarinov.lma.Group

class Group(
    var groupId: String,
    var groupName: String,
    var groupDesc: String
) {

    override fun toString(): String {
        return "Group{" +
                "groupId='" + groupId + '\'' +
                ", groupName='" + groupName + '\'' +
                ", groupDesc='" + groupDesc + '\'' +
                '}'
    }

}