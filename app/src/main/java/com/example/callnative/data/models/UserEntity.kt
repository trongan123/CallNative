package com.example.callnative.data.models

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class UserEntity(
    @Id var id: Long = 0,
    val userId: String,
    val avatarImageUrl: String,
    val displayName: String
) {
    companion object {
        val default = UserEntity(
            id = 0,
            userId = "",
            avatarImageUrl = "",
            displayName = ""
        )
    }
}