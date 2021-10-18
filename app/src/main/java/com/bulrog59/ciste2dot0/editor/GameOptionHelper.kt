package com.bulrog59.ciste2dot0.editor

import com.bulrog59.ciste2dot0.gamedata.GameData
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.treeToValue

class GameOptionHelper {
    companion object {
        val om = ObjectMapper().apply { registerModule(KotlinModule()) }
        inline fun <reified T> getSceneOptions(gameData:GameData, scenePosition:Int): T? {
            val options = gameData.scenes[scenePosition].options
            return if (options.isEmpty) null else om.treeToValue<T>(options)
        }

        inline fun <T, reified O> gamePreviousElement(gameData:GameData, scenePosition:Int,getterFunction: (O?) -> T?): T? {
            return getterFunction(getSceneOptions(gameData,scenePosition))
        }
    }
}