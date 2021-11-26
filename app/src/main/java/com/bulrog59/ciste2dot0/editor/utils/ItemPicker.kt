package com.bulrog59.ciste2dot0.editor.utils

import android.app.Activity
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bulrog59.ciste2dot0.R

class ItemPicker(val activity:Activity) {

    var previousSelection=RecyclerView.NO_POSITION

    fun init(
        titleText: Int,
        items:List<String>,
        doneCallBack: (itemPosition: Int) -> Unit
    ) {
        activity.setContentView(R.layout.editor_item_selection)
        activity.findViewById<TextView>(R.id.title_unused_resource).setText(titleText)
        val recyclerView = activity.findViewById<RecyclerView>(R.id.item_deletion_list)
        val menuSelectorAdapter= MenuSelectorAdapter(items) { p -> doneCallBack(p) }
        menuSelectorAdapter.positionSelected=previousSelection
        recyclerView.adapter = menuSelectorAdapter
        recyclerView.layoutManager = LinearLayoutManager(activity)
    }
}