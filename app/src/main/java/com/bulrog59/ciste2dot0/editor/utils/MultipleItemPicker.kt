package com.bulrog59.ciste2dot0.editor.utils

import android.app.Activity
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bulrog59.ciste2dot0.R

class MultipleItemPicker(val activity: Activity) {

    fun init(
        titleText: Int,
        items: List<String>,
        previousSelection:List<Int>,
        onSelect: (itemsPosition: List<Int>) -> Unit
    ) {
        activity.setContentView(R.layout.editor_item_selection)
        activity.findViewById<TextView>(R.id.title_unused_resource).setText(titleText)
        val recyclerView = activity.findViewById<RecyclerView>(R.id.item_deletion_list)
        val menuSelectorAdapter = MultipleMenuSelectorAdapter(
            items,
            mutableListOf<Int>().apply { addAll(previousSelection) }) { p -> onSelect(p) }
        recyclerView.adapter = menuSelectorAdapter
        recyclerView.layoutManager = LinearLayoutManager(activity)
    }
}