package com.bulrog59.ciste2dot0.game.management

import java.lang.StringBuilder
import java.util.*

data class Game(
    val name: String,
    val id: UUID?,
    val description: String?,
    val location: String,
    val language: String?,
    val sizeInMB: Long?
) {
    fun gameDetails(): String {
        val detailBuilder = StringBuilder()

        description?.apply { detailBuilder.append("\n\nDescription:\n$description\n\n") }
        detailBuilder.append("Location:\n$location\n\n")
        language?.apply { detailBuilder.append("Language:\n$language\n\n") }
        sizeInMB?.apply { detailBuilder.append("Size:\n${sizeInMB}MB\n\n") }
        return detailBuilder.toString()
    }
}
