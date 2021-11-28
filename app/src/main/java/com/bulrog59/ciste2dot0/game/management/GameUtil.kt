package com.bulrog59.ciste2dot0.game.management

import android.app.Activity
import android.widget.EditText
import android.widget.TextView
import com.bulrog59.ciste2dot0.R
import com.bulrog59.ciste2dot0.editor.utils.FieldValidator
import com.bulrog59.ciste2dot0.gamedata.SceneData
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.treeToValue
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class GameUtil(private val activity: Activity) {

    companion object {
        val languages =
            HashSet(Locale.getAvailableLocales().map { it.displayLanguage }).sorted()

        val mapper = ObjectMapper().apply { registerModule(KotlinModule()) }

        inline fun <reified T> retrieveOption(sceneData: SceneData): T {
            return mapper.treeToValue<T>(sceneData.options)
                ?: throw IllegalArgumentException("options is null and cannot for a video type for the scene: $sceneData")
        }


    }

    private val fieldValidator = FieldValidator(activity)

    fun errorInGameMetaFields(): Boolean {
        var error = fieldValidator.notEmptyField(R.id.menu_title_input)
        error = fieldValidator.maxSizeField(R.id.menu_title_input) || error
        error = fieldValidator.notEmptyField(R.id.game_location_input) || error
        error = fieldValidator.inList(R.id.game_language_input, languages) || error
        return error
    }

    fun createGameMetaDataForMetaDataEditScreen(gameID: UUID?): GameMetaData {
        val name = activity.findViewById<EditText>(R.id.menu_title_input).text.toString()
        val language = activity.findViewById<TextView>(R.id.game_language_input).text.toString()
        val description =
            activity.findViewById<TextView>(R.id.description_text).text.toString()
        val location = activity.findViewById<TextView>(R.id.game_location_input).text.toString()
        val id = gameID ?: UUID.randomUUID()
        return GameMetaData(
            name = name,
            language = language,
            description = description,
            location = location,
            id = id,
            sizeInMB = null,
            userId = FirebaseAuth.getInstance().currentUser?.uid,
            author = FirebaseAuth.getInstance().currentUser?.displayName

        )
    }


}