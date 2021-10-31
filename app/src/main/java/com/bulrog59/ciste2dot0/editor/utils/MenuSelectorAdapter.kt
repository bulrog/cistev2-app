package com.bulrog59.ciste2dot0.editor.utils

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bulrog59.ciste2dot0.R

class MenuSelectorAdapter(private val choices: List<String>, private val callBack: (Int) -> Unit) :
    RecyclerView.Adapter<MenuSelectorAdapter.ViewHolder>() {

    var positionSelected = RecyclerView.NO_POSITION

    inner class ViewHolder(parentView: View) : RecyclerView.ViewHolder(parentView) {
        val textElement: TextView = parentView.findViewById(R.id.single_line_text)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val singleTextView = inflater.inflate(R.layout.single_line_text, parent, false)
        return ViewHolder(singleTextView)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textElement.text = choices[position]
        if (position == positionSelected) {
            holder.itemView.setBackgroundColor(Color.GRAY)
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT)
        }
        holder.itemView.setOnClickListener {
            positionSelected = position
            notifyDataSetChanged()
            callBack(position)
        }
    }

    override fun getItemCount(): Int {
        return choices.size
    }
}