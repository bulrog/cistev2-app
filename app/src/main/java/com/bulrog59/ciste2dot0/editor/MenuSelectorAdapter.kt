package com.bulrog59.ciste2dot0.editor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bulrog59.ciste2dot0.R

class MenuSelectorAdapter(val choices:List<String>) : RecyclerView.Adapter<MenuSelectorAdapter.ViewHolder>(){

    inner class ViewHolder(parentView: View):RecyclerView.ViewHolder(parentView){
        val textElement=parentView.findViewById<TextView>(R.id.single_line_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val singleTextView = inflater.inflate(R.layout.single_line_text, parent, false)
        return ViewHolder(singleTextView)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textElement.text=choices[position]
    }

    override fun getItemCount(): Int {
        return choices.size
    }
}