package com.bulrog59.ciste2dot0.scenes.inventory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bulrog59.ciste2dot0.CisteActivity
import com.bulrog59.ciste2dot0.R
import com.bulrog59.ciste2dot0.Util
import com.bulrog59.ciste2dot0.gamedata.Inventory

class InventoryAdapter(private val inventory: Inventory, private val cisteActivity: CisteActivity) :
    RecyclerView.Adapter<InventoryAdapter.ViewHolder>() {

    val util=Util(cisteActivity.packageName)
    var firstObject:Int?=null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val itemName = itemView.findViewById<TextView>(R.id.itemDescription)
        val itemIcon = itemView.findViewById<ImageView>(R.id.itemIcon)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if (firstObject==null){
                val item=inventory.getItem(adapterPosition)
                cisteActivity.findViewById<TextView>(R.id.inventory_message).text="Please select the other item to use with:"
                //TODO: if select an item then should remove it from the list (=> need to get a copy of the item list)
                val itemSelected=cisteActivity.findViewById<ImageView>(R.id.itemSelected)
                itemSelected.visibility=View.VISIBLE
                itemSelected.setImageURI(util.getUri(item.picture))
                firstObject=item.id
            }
            else {
                //TODO: add trigger of another scene
                Toast.makeText(cisteActivity,"you use the item:",Toast.LENGTH_LONG)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val itemView = inflater.inflate(R.layout.item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = inventory.getItem(position)
        holder.itemName.text = item.name
        holder.itemIcon.setImageURI(util.getUri(item.picture))
    }

    override fun getItemCount(): Int {
        return inventory.size()
    }
}