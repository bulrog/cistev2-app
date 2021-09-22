package com.bulrog59.ciste2dot0.gamedata

import com.bulrog59.ciste2dot0.game.management.GameMetaData

data class GameData(val starting:Int,val scenes:List<SceneData>, val backButtonScene:Int, val gameMetaData: GameMetaData?)
