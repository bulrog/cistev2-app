package com.bulrog59.ciste2dot0.gamedata

import com.fasterxml.jackson.databind.JsonNode

data class SceneData(val sceneId:Int,val sceneType:SceneType,val options: JsonNode, val name:String?)
