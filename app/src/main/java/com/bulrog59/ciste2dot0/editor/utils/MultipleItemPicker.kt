package com.bulrog59.ciste2dot0.editor.utils

import android.app.Activity
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bulrog59.ciste2dot0.R

class MultipleItemPicker(val activity: Activity) {

    private var selectedItems: List<Int> = emptyList()
    fun init(
        titleText: Int,
        items: List<String>,
        previousSelection: List<Int>,
        done: (itemsPosition: List<Int>) -> Unit
    ) {
        activity.setContentView(R.layout.editor_item_selection)
        activity.findViewById<TextView>(R.id.title_unused_resource).setText(titleText)
        val recyclerView = activity.findViewById<RecyclerView>(R.id.item_deletion_list)
        val menuSelectorAdapter = MultipleMenuSelectorAdapter(
            items,
            mutableListOf<Int>().apply { addAll(previousSelection) }) { p -> selectedItems=p }
        recyclerView.adapter = menuSelectorAdapter
        recyclerView.layoutManager = LinearLayoutManager(activity)
        val nextButton = activity.findViewById<Button>(R.id.next_item_selection)
        //TODO: when single item then turn it off:
        nextButton.visibility = View.VISIBLE
        nextButton.setOnClickListener {
            done(selectedItems)
        }
    }
}