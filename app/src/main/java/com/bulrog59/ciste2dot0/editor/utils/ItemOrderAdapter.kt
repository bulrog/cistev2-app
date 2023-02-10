package com.bulrog59.ciste2dot0.editor.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bulrog59.ciste2dot0.R
import java.util.*

class ItemOrderAdapter<T>(var choices:List<T>, val getItemText:(T)->String): RecyclerView.Adapter<ItemOrderAdapter<T>.ViewHolder>() {

    inner class ViewHolder(parentView: View) : RecyclerView.ViewHolder(parentView) {

        val textElement: TextView = parentView.findViewById(R.id.single_line_text)
    }

    fun onItemMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(choices, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemOrderAdapter<T>.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val singleTextView = inflater.inflate(R.layout.single_line_text, parent, false)
        return ViewHolder(singleTextView)

    }

    override fun onBindViewHolder(holder: ItemOrderAdapter<T>.ViewHolder, position: Int) {
        holder.textElement.text=getItemText(choices[position])
    }

    override fun getItemCount(): Int {
        return choices.size
    }




}