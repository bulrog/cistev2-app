package com.bulrog59.ciste2dot0.game.management

import com.bulrog59.ciste2dot0.R
import java.lang.StringBuilder
import java.util.*

data class GameMetaData(
    val name: String,
    val id: UUID?,
    val description: String?,
    val location: String,
    val language: String?,
    val sizeInMB: Long?,
    val userId: String?,
    val author: String?,
    val playable: Boolean
) {
    fun gameDetails(getResourceText: (Int) -> String, gameIsStoredLocally: Boolean): String {
        val detailBuilder = StringBuilder()

        description?.apply { detailBuilder.append("\n\n${getResourceText(R.string.game_description)}:\n$description\n\n") }
        detailBuilder.append("${getResourceText(R.string.game_location)}:\n$location\n\n")
        language?.apply { detailBuilder.append("${getResourceText(R.string.game_language)}:\n$language\n\n") }
        sizeInMB?.apply { detailBuilder.append("${getResourceText(R.string.game_size)}:\n${sizeInMB}MB\n\n") }
        author?.apply { detailBuilder.append("${getResourceText(R.string.game_author)}:\n${author}\n\n") }
        if (!playable && !gameIsStoredLocally) {
            detailBuilder.append("${getResourceText(R.string.game_under_review)}\n\n")
        }
        return detailBuilder.toString()
    }
}
