package com.bulrog59.ciste2dot0.editor

import android.app.Activity
import android.app.AlertDialog
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bulrog59.ciste2dot0.R
import com.bulrog59.ciste2dot0.scenes.menu.MenuItem

class ListEditor<T>(
    val activity: Activity, val items: List<T>, val getItemText: (List<T>) -> List<String>,
    val updateMenuItems: (
        T?,
        (MutableList<T>, T) -> Unit
    ) -> Unit,
    val deleteMenuItem: (Int) -> Unit,
    val done: () -> Unit
) {
    private fun deleteMenuItem() {
        ItemPicker(activity).init(
            R.string.select_element_to_delete,
            getItemText(items)
        ) {
            AlertDialog.Builder(activity)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(R.string.delete_item_message)
                .setPositiveButton(R.string.confirmation) { _, _ ->
                    deleteMenuItem(it)
                }
                .setNegativeButton(R.string.denial) { _, _ -> init() }
                .show()

        }

    }

    fun init() {
        activity.setContentView(R.layout.editor_menu)
        val recyclerView = activity.findViewById<RecyclerView>(R.id.item_menu_selection)
        recyclerView.adapter =
            MenuSelectorAdapter(getItemText(items)) { p ->
                updateMenuItems(items[p]) { l, m -> l[p] = m }
            }
        recyclerView.layoutManager = LinearLayoutManager(activity)
        activity.findViewById<Button>(R.id.add_menu_button).setOnClickListener {
            updateMenuItems(null) { l, m -> l.add(m) }
        }

        activity.findViewById<Button>(R.id.delete_menu_item_button).setOnClickListener {
            deleteMenuItem()
        }
        activity.findViewById<Button>(R.id.exit_button_menu_title).setOnClickListener {
            done()
        }

    }
}