package com.bulrog59.ciste2dot0.scenes.menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.bulrog59.ciste2dot0.CisteActivity
import com.bulrog59.ciste2dot0.R

class MenuAdapter(private val menuOptions: MenuOptions, private val cisteActivity: CisteActivity):RecyclerView.Adapter<MenuAdapter.ViewHolder>() {


    inner class ViewHolder(menuRow: View) : RecyclerView.ViewHolder(menuRow),View.OnClickListener {
        val button=menuRow.findViewById<Button>(R.id.menu_button)
        init {
            button.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            cisteActivity.setScene(menuOptions.menuItems[adapterPosition].nextScene)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val menuRow = inflater.inflate(R.layout.scene_menu_row, parent, false)
        return ViewHolder(menuRow)
    }

    override fun onBindViewHolder(holder: MenuAdapter.ViewHolder, position: Int) {
        holder.button.text=menuOptions.menuItems[position].buttonText
    }

    override fun getItemCount(): Int {
        return menuOptions.menuItems.size
    }


}