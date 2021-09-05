package com.bulrog59.ciste2dot0.game.management

import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.lang.IllegalArgumentException
import java.util.*

class GameSearch {
    val db = Firebase.firestore

    fun mapToGame(document: QueryDocumentSnapshot): Game {
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
        return Game(name, id)
    }

    fun getGames(onFailure:(e:Exception)-> Unit,onSuccess:(List<Game>)-> Unit) {
        val games = mutableListOf<Game>()
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