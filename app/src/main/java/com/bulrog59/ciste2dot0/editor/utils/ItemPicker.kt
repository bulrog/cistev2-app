package com.bulrog59.ciste2dot0.editor.utils

import android.app.Activity
import androidx.recyclerview.widget.RecyclerView

class ItemPicker(val activity: Activity) {


    var previousSelection = RecyclerView.NO_POSITION

    fun init(
        titleText: Int,
        items: List<String>,
        doneCallBack: (itemPosition: Int) -> Unit
    ) {

        val multi = MultipleItemPicker(activity)
        multi.singleItem = true
        val previousSelection=if (previousSelection==RecyclerView.NO_POSITION) emptyList() else listOf(previousSelection)
        multi.init(titleText, items, previousSelection) { r -> doneCallBack(r[0]) }
    }
}