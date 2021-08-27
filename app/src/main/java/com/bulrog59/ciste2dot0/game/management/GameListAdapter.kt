package com.bulrog59.ciste2dot0.game.management

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bulrog59.ciste2dot0.R
import java.util.*

class GameListAdapter : RecyclerView.Adapter<GameListAdapter.ViewHolder>() {
    //TODO: generate real UUID to give it to the game
    val games: List<Game> = listOf(
        Game("A GAME", UUID.fromString("e32ca765-c6fc-46d1-9ec7-9ab7c68a15ad")),
        Game("Alien rescue", UUID.fromString("e32ca765-c6fc-46d1-9ec7-9ab7c68a15ad")),
        Game("Lutchi s'est echappe!", UUID.fromString("d2194327-184f-4270-ba2b-001b610186a6"))
    )


    inner class ViewHolder(gameDetail: View) : RecyclerView.ViewHolder(gameDetail) {
        val gameName = gameDetail.findViewById<TextView>(R.id.game_name)
        val loadStartButton = gameDetail.findViewById<Button>(R.id.load_start_game)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameListAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val gameView = inflater.inflate(R.layout.game_management_row, parent, false)
        return ViewHolder(gameView)
    }

    override fun onBindViewHolder(holder: GameListAdapter.ViewHolder, position: Int) {
        holder.gameName.text=games[position].name
        holder.loadStartButton.setText(R.string.load_game_button)
    }

    override fun getItemCount(): Int {
        return games.size
    }
}