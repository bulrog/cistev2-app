package com.bulrog59.ciste2dot0.gamedata

import java.util.*

class Inventory {
    private val items = mutableListOf<Item>()

    fun addItem(item: Item) {
        items.add(item)
    }

    fun contains(ids: List<Int>):Boolean{
        return items.map { it.id }.containsAll(ids)
    }

    fun all(ids:List<Int>):Boolean {
        val itemIds=items.map { it.id }
        Collections.sort(itemIds)
        Collections.sort(ids)
        return ids.equals(itemIds)
    }


}
