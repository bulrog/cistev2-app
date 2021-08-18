package com.bulrog59.ciste2dot0.gamedata

import java.util.*

class Inventory {
    private val items = mutableListOf<Item>()

    fun addItem(item: Item) {
        if (items.map { it.id }.filter { it == item.id }.isEmpty()) {
            items.add(item)
        }
    }

    fun contains(ids: List<Int>): Boolean {
        return items.map { it.id }.containsAll(ids)
    }

    fun all(ids: List<Int>): Boolean {
        val itemIds = items.map { it.id }
        Collections.sort(itemIds)
        Collections.sort(ids)
        return ids == itemIds
    }

    override fun toString(): String {
        return items.toString()
    }

    fun getItem(position: Int): Item {
        return items.get(position)
    }

    fun size():Int {
        return items.size
    }


}
