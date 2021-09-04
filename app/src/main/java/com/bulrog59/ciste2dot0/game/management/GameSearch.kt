package com.bulrog59.ciste2dot0.game.management

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class GameSearch {
    val db = Firebase.firestore
    fun getGames():List<Game> {
        db.collection("games")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    println("${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                println("Error getting documents.$exception")
            }
        return listOf()
    }

}