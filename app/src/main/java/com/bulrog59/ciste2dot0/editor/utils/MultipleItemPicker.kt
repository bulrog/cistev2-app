package com.bulrog59.ciste2dot0.editor.utils

import android.app.Activity
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bulrog59.ciste2dot0.R

class MultipleItemPicker(val activity: Activity) {

    var singleItem = false

    private var selectedItems: List<Int> = emptyList()

    private fun onSelectedItem(positions: List<Int>, done: (itemsPosition: List<Int>) -> Unit) {
        if (singleItem) {
            done(positions)
        } else {
            selectedItems = positions
        }

    }

    fun init(
        titleText: Int,
        items: List<String>,
        previousSelection: List<Int>,
        done: (itemsPosition: List<Int>) -> Unit
    ) {
        activity.setContentView(R.layout.editor_item_selection_order)
        activity.findViewById<TextView>(R.id.title_editor_multipleitems).setText(titleText)
        val recyclerView = activity.findViewById<RecyclerView>(R.id.item_deletion_list)
        if (singleItem && previousSelection.size > 1) {
            throw IllegalArgumentException("you cannot have more than 1 item selected when you set to select single itme")
        }
        selectedItems = previousSelection
        val menuSelectorAdapter = MultipleMenuSelectorAdapter(
            items,
            mutableListOf<Int>().apply { addAll(previousSelection) },
            singleItem
        ) { onSelectedItem(it, done) }

        recyclerView.adapter = menuSelectorAdapter
        recyclerView.layoutManager = LinearLayoutManager(activity)
        val nextButton = activity.findViewById<Button>(R.id.next_item_selection)
        if (!singleItem) {
            nextButton.visibility = View.VISIBLE
            nextButton.setOnClickListener {
                done(selectedItems)
            }

        }
    }
}