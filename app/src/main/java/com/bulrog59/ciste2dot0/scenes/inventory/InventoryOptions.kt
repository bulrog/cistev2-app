package com.bulrog59.ciste2dot0.scenes.inventory

data class InventoryOptions(val combinations:List<Combination>)

data class Combination(val id1:Int,val id2:Int,val nextScene:Int)
