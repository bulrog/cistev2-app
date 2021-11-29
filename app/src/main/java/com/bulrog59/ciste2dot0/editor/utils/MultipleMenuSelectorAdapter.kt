package com.bulrog59.ciste2dot0.editor.utils

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bulrog59.ciste2dot0.R

class MultipleMenuSelectorAdapter(private val choices: List<String>, private var positionsSelected:MutableList<Int>,private val callBack: (List<Int>) -> Unit) :
    RecyclerView.Adapter<MultipleMenuSelectorAdapter.ViewHolder>() {

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
        if (positionsSelected.contains(position)) {
            holder.itemView.setBackgroundColor(Color.GRAY)
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT)
        }
        holder.itemView.setOnClickListener {
            if (positionsSelected.contains(position)){
                positionsSelected= mutableListOf<Int>().apply{ addAll(positionsSelected.filter { it!=position }))}
            }else{
                positionsSelected.add(position)
            }
            notifyDataSetChanged()
            callBack(positionsSelected)
        }
    }

    override fun getItemCount(): Int {
        return choices.size
    }
}