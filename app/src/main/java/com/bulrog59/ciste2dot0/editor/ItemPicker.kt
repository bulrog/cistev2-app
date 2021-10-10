package com.bulrog59.ciste2dot0.editor

import android.app.Activity
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bulrog59.ciste2dot0.R

class ItemPicker(val activity:Activity) {

    var doneCallBack:((String)-> Unit)?=null
    fun init(
        titleText: Int,
        items:List<String>,
        doneCallBack: (itemName: String) -> Unit
    ) {
        this.doneCallBack = doneCallBack
        activity.setContentView(R.layout.editor_item_selection)
        activity.findViewById<TextView>(R.id.editor_item_selection_title).setText(titleText)
        val recyclerView = activity.findViewById<RecyclerView>(R.id.editor_item_selection_list)
        recyclerView.adapter = MenuSelectorAdapter(items) { p -> doneCallBack(items[p]) }
        recyclerView.layoutManager = LinearLayoutManager(activity)
    }
}