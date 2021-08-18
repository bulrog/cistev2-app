package com.bulrog59.ciste2dot0.scenes.inventory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bulrog59.ciste2dot0.R
import com.bulrog59.ciste2dot0.Util
import com.bulrog59.ciste2dot0.gamedata.Inventory

class InventoryAdapter(private val inventory: Inventory, private val util: Util) :
    RecyclerView.Adapter<InventoryAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName = itemView.findViewById<TextView>(R.id.itemDescription)
        val itemIcon=itemView.findViewById<ImageView>(R.id.itemIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val itemView = inflater.inflate(R.layout.item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item=inventory.getItem(position)
        holder.itemName.text=item.description
        holder.itemIcon.setImageURI(util.getUri(item.picName))
    }

    override fun getItemCount(): Int {
        return inventory.size()
    }
}