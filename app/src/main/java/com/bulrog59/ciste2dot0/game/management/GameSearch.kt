package com.bulrog59.ciste2dot0.game.management

import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.lang.IllegalArgumentException
import java.util.*

class GameSearch {
    private val db = Firebase.firestore

    fun mapToGame(document: QueryDocumentSnapshot): GameMetaData {
        val uuidAsString = document.getString("id")
        val name = document.getString("name")
        if (name.isNullOrEmpty()) {
            throw IllegalArgumentException("the name of a game cannot be empty")
        }
        val id = if (uuidAsString.isNullOrEmpty()) {
            null
        } else {
            UUID.fromString(uuidAsString)
        }

        return GameMetaData(
            name=name,
            id=id,
            description = document.getString("description"),
            location = document.getString("location")!!,
            language = document.getString("language"),
            sizeInMB = document.getLong("sizeInMB"),
            userId = document.getString("userID"),
            author = document.getString("author")

        )
    }

    fun getGames(onFailure: (e: Exception) -> Unit, onSuccess: (MutableList<GameMetaData>) -> Unit) {
        val games = mutableListOf<GameMetaData>()
        db.collection("games")
            .get()
            .addOnSuccessListener { result ->
                result.forEach { games.add(mapToGame(it)) }
                onSuccess(games)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

}