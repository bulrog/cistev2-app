package com.bulrog59.ciste2dot0.game.management

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bulrog59.ciste2dot0.CisteActivity
import com.bulrog59.ciste2dot0.CrashActivity
import com.bulrog59.ciste2dot0.R
import java.util.*

class GameListAdapter(private val context: Context) :
    RecyclerView.Adapter<GameListAdapter.ViewHolder>() {
    private val games: List<Game> = listOf(
        Game("Alien rescue", null),
        Game("Lutchi s'est echappé!", UUID.fromString("d2194327-184f-4270-ba2b-001b610186a6"))
    )

    private val gameDataLoader = GameDataLoader(context)


    inner class ViewHolder(gameDetail: View) : RecyclerView.ViewHolder(gameDetail) {
        val gameName = gameDetail.findViewById<TextView>(R.id.game_name)
        val loadStartButton = gameDetail.findViewById<Button>(R.id.load_start_game)
        val deleteButton = gameDetail.findViewById<Button>(R.id.delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameListAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val gameView = inflater.inflate(R.layout.game_management_row, parent, false)
        return ViewHolder(gameView)
    }

    private fun startGame(id: UUID?) {
        val intent = Intent(context, CisteActivity::class.java)
        intent.putExtra("GAME_ID", id)
        context.startActivity(intent)

    }

    override fun onBindViewHolder(holder: GameListAdapter.ViewHolder, position: Int) {
        val game = games[position]
        holder.gameName.text = game.name
        if (game.id == null) {
            holder.deleteButton.isEnabled = false
        }
        if (game.id == null || gameDataLoader.gameIsAvailable(game.id)) {
            holder.loadStartButton.setText(R.string.start_game_button)
            holder.loadStartButton.setOnClickListener { startGame(game.id) }
        } else {
            holder.loadStartButton.setText(R.string.load_game_button)
            holder.deleteButton.isEnabled = false
        }
    }

    override fun getItemCount(): Int {
        return games.size
    }
}