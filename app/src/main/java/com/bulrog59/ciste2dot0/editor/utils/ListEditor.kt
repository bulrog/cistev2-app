package com.bulrog59.ciste2dot0.editor.utils

import android.app.Activity
import android.app.AlertDialog
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bulrog59.ciste2dot0.R

class ListEditor<T>(
    private val activity: Activity,
    private var items: List<T>,
    private val getItemText: (List<T>) -> List<String>,
    private val editMenuItem: (
        T?,
        done: (T) -> Unit
    ) -> Unit,
    private val done: (List<T>) -> Unit
) {
    var validatorItemCanBeRemoved: (T) -> String = { "" }

    private fun updateMenuItems(
        item: T?,
        updater: (MutableList<T>, T) -> Unit
    ) {
        editMenuItem(item) { i ->
            items =
                mutableListOf<T>().apply { addAll(items) }.also { updater(it, i) }
            init()
        }
    }

    private fun deleteMenuItem() {
        if (items.isEmpty()) {
            Toast.makeText(activity, R.string.no_item_to_select, Toast.LENGTH_LONG).show()
            return
        }

        ItemPicker(activity).init(
            R.string.select_element_to_delete,
            getItemText(items)
        ) {
            val error = validatorItemCanBeRemoved(items[it])
            if (error.isNotEmpty()) {
                Toast.makeText(activity, error, Toast.LENGTH_LONG).show()
                init()
            } else {
                AlertDialog.Builder(activity)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage(R.string.delete_item_message)
                    .setPositiveButton(R.string.confirmation) { _, _ ->
                        items = mutableListOf<T>().apply {
                            addAll(items)
                            removeAt(it)
                        }
                        init()
                    }
                    .setNegativeButton(R.string.denial) { _, _ -> init() }
                    .show()
            }


        }

    }

    fun init() {
        activity.setContentView(R.layout.editor_menu)
        val recyclerView = activity.findViewById<RecyclerView>(R.id.item_menu_selection)
        recyclerView.adapter =
            MenuSelectorAdapter(getItemText(items), RecyclerView.NO_POSITION) { p ->
                updateMenuItems(items[p]) { l, m ->
                    l[p] = m
                }
            }
        recyclerView.layoutManager = LinearLayoutManager(activity)
        activity.findViewById<Button>(R.id.add_menu_button).setOnClickListener {
            updateMenuItems(null) { l, m ->
                l.add(m)
            }
        }

        activity.findViewById<Button>(R.id.delete_menu_item_button).setOnClickListener {
            deleteMenuItem()
        }
        activity.findViewById<Button>(R.id.next_button_entity).setOnClickListener {
            done(items)
        }

    }
}