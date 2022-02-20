package com.bulrog59.ciste2dot0.game.management

import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.lang.IllegalArgumentException
import java.lang.Math.round
import java.util.*
import kotlin.math.roundToInt

class GameDao {
    private val db = Firebase.firestore

    private val collectionName = "games"

    private val id = "id"
    private val name = "name"
    private val description = "description"
    private val location = "location"
    private val language = "language"
    private val sizeInMB = "sizeInMB"
    private val userID = "userID"
    private val author = "author"
    private val visibility = "visibility"


    fun mapToGame(document: QueryDocumentSnapshot): GameMetaData {
        val uuidAsString = document.getString(id)
        val name = document.getString(name)
        if (name.isNullOrEmpty()) {
            throw IllegalArgumentException("the name of a game cannot be empty")
        }
        val id = if (uuidAsString.isNullOrEmpty()) {
            null
        } else {
            UUID.fromString(uuidAsString)
        }

        return GameMetaData(
            name = name,
            id = id,
            description = document.getString(description),
            location = document.getString(location)!!,
            language = document.getString(language),
            sizeInMB = document.getLong(sizeInMB),
            userId = document.getString(userID),
            author = document.getString(author),
            visibility = document.getBoolean(visibility) ?: false

        )
    }

    private fun mapGameMetaToField(gameMetaData: GameMetaData, gameSize: Long): Map<String, Any?> {
        val map = mutableMapOf<String, Any?>()
        map[name] = gameMetaData.name
        map[id] = gameMetaData.id.toString()
        map[description] = gameMetaData.description
        map[location] = gameMetaData.location
        map[language] = gameMetaData.language
        map[sizeInMB] = (gameSize / 1e6).roundToInt()
        map[userID] = gameMetaData.userId
        map[author] = gameMetaData.author
        map[visibility] = gameMetaData.visibility
        return map
    }

    fun getGames(
        onFailure: (e: Exception) -> Unit,
        onSuccess: (MutableList<GameMetaData>) -> Unit
    ) {
        val games = mutableListOf<GameMetaData>()
        db.collection(collectionName)
            .whereEqualTo(visibility, true)
            .get()
            .addOnSuccessListener { result ->
                result.forEach { games.add(mapToGame(it)) }
                onSuccess(games)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun updateGameEntry(
        gameSize: Long,
        gameMetaData: GameMetaData,
        onFailure: (e: Exception) -> Unit,
        onSuccess: () -> Unit
    ) {
        db.collection(collectionName).document(gameMetaData.id.toString()).get()
            .addOnSuccessListener {
                if (it.get(id) == null) {
                    db.collection(collectionName).document(gameMetaData.id.toString())
                        .set(mapGameMetaToField(gameMetaData, gameSize))
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { e -> onFailure(e) }
                } else {
                    db.collection(collectionName).document(gameMetaData.id.toString())
                        .update(mapGameMetaToField(gameMetaData, gameSize))
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { e -> onFailure(e) }
                }
            }
            .addOnFailureListener {
                onFailure(it)

            }

    }

}