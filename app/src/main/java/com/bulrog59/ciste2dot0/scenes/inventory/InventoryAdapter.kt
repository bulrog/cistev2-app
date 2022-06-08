package com.bulrog59.ciste2dot0.scenes.inventory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bulrog59.ciste2dot0.CisteActivity
import com.bulrog59.ciste2dot0.R
import com.bulrog59.ciste2dot0.gamedata.Inventory
import com.bulrog59.ciste2dot0.gamedata.Item
import java.lang.IllegalStateException

class InventoryAdapter(
    private val inventoryOptions: InventoryOptions,
    private val cisteActivity: CisteActivity
) :
    RecyclerView.Adapter<InventoryAdapter.ViewHolder>() {
    private var firstObject: Item? = null
    private val inventoryCopy: Inventory = cisteActivity.inventory.copy()

    companion object {
        val BAD_COMBINATIONS_MESSAGES = listOf(
            R.string.invalid_combination,
            R.string.invalid_combination,
            R.string.invalid_combination2,
            R.string.invalid_combination3,
            R.string.invalid_combination4
        )
    }

    fun matchOneId(combination: Combination, id: Int): Boolean {
        return combination.id1 == id || combination.id2 == id
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val itemName: TextView = itemView.findViewById(R.id.itemDescription)
        val itemIcon: ImageView = itemView.findViewById(R.id.itemIcon)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if (firstObject == null) {
                setFirstObject()
            } else {
                resolveAction()

            }
        }

        private fun setFirstObject() {
            val item = inventoryCopy.getItem(adapterPosition)
            cisteActivity.findViewById<TextView>(R.id.inventory_message)
                .setText(R.string.second_object_use_message)
            val itemSelected = cisteActivity.findViewById<ImageView>(R.id.itemSelected)
            itemSelected.visibility = View.VISIBLE
            itemSelected.setImageURI(cisteActivity.gameDataLoader.getUri(item.picture))
            firstObject = item
            inventoryCopy.removeItem(item.id)
            notifyItemRemoved(adapterPosition)
        }

        private fun resolveAction() {
            val secondObject = inventoryCopy.getItem(adapterPosition)
            val matchingCombinations =
                inventoryOptions.combinations.filter { matchOneId(it, firstObject!!.id) }
                    .filter { matchOneId(it, secondObject.id) }
            if (matchingCombinations.size > 1) {
                throw IllegalStateException("found 2 matching combinations:$matchingCombinations when use object: $firstObject with $secondObject, this is not allowed. Please fix the game settings.")
            }

            if (matchingCombinations.isNotEmpty()) {
                cisteActivity.setScene(matchingCombinations.first().nextScene)
                return
            }

            cisteActivity.findViewById<TextView>(R.id.inventory_message)
                .apply { setText(BAD_COMBINATIONS_MESSAGES.random()) }


            cisteActivity.findViewById<ImageView>(R.id.itemSelected)
                .apply { visibility = View.GONE }
            inventoryCopy.addItem(firstObject!!)
            notifyDataSetChanged()
            firstObject = null

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val itemView = inflater.inflate(R.layout.scene_inventory_row, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = inventoryCopy.getItem(position)
        holder.itemName.text = item.name
        holder.itemIcon.setImageURI(cisteActivity.gameDataLoader.getUri(item.picture))
    }

    override fun getItemCount(): Int {
        return inventoryCopy.size
    }
}