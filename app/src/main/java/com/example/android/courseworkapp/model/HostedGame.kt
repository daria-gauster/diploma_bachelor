package com.example.android.courseworkapp.model

import java.time.LocalDateTime

data class HostedGame(
    var title: String,
    var dateTime: LocalDateTime,
    var gameType: String,
    var maxPlayers: Int,
    val place: Place
)