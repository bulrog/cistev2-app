package com.bulrog59.ciste2dot0.gamedata

import java.util.*

class Inventory {
    private val items = mutableListOf<Item>()

    val size get() = items.size

    fun addItem(item: Item) {
        if (items.map { it.id }.none { it == item.id }) {
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
        return items[position]
    }

    fun removeItem(id: Int) {
        val idToRemove=items.map { it.id }.indexOf(id)
        if (idToRemove>0){
            items.removeAt(idToRemove)
        }

    }

    fun copy(): Inventory {
        val inventory=Inventory()
        items.forEach{inventory.addItem(it)}
        return inventory
    }


}
