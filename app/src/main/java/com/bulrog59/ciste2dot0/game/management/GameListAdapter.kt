package com.bulrog59.ciste2dot0.game.management

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bulrog59.ciste2dot0.CisteActivity
import com.bulrog59.ciste2dot0.R
import java.util.*


class GameListAdapter(private val context: Context) :
    RecyclerView.Adapter<GameListAdapter.ViewHolder>() {
    private var games: List<Game> = listOf()

    private val gameDataLoader = GameDataManager(context)

    init {
        GameSearch().getGames {
            games = it
            notifyDataSetChanged()
        }

    }


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
        intent.putExtra(CisteActivity.GAME_ID, id?.toString())
        context.startActivity(intent)

    }


    private fun downloadGameButtons(holder: GameListAdapter.ViewHolder, game: Game) {
        holder.loadStartButton.setText(R.string.load_game_button)
        holder.loadStartButton.isEnabled = true
        holder.loadStartButton.setOnClickListener {
            gameDataLoader.loadGame(game.id,
                { transfer, total ->
                    val loadingMessage = context.resources.getString(R.string.busy_game_button)
                    holder.loadStartButton.text =
                        "$loadingMessage (" + "%.1f".format(transfer * 100.0f / total) + "%)"
                }, { e ->
                    Toast.makeText(
                        context,
                        "${context.getText(R.string.error_downloading_game)}:${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    downloadGameButtons(holder, game)
                }
            ) { loadedGameButtons(holder, game) }
            holder.loadStartButton.isEnabled = false
            holder.loadStartButton.setText(R.string.busy_game_button)
        }
        holder.deleteButton.isEnabled = false
    }


    private fun loadedGameButtons(holder: GameListAdapter.ViewHolder, game: Game) {
        holder.loadStartButton.setText(R.string.start_game_button)
        holder.deleteButton.isEnabled = true
        holder.loadStartButton.isEnabled = true
        holder.deleteButton.setOnClickListener {
            deletionWithConfirmation(game, holder)
        }
        holder.loadStartButton.setOnClickListener { startGame(game.id) }
    }

    private fun deletionWithConfirmation(
        game: Game,
        holder: ViewHolder
    ) {
        AlertDialog.Builder(context)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setMessage(context.resources.getString(R.string.delete_game_message))
            .setPositiveButton(
                context.resources.getString(R.string.confirmation)
            ) { _, _ ->
                gameDataLoader.eraseLocalGame(game.id)
                downloadGameButtons(holder, game)
            }
            .setNegativeButton(context.resources.getString(R.string.denial), null)
            .show()
    }

    override fun onBindViewHolder(holder: GameListAdapter.ViewHolder, position: Int) {
        val game = games[position]
        holder.gameName.text = game.name
        if (game.id == null) {
            holder.deleteButton.visibility = View.INVISIBLE
        }
        if (game.id == null || gameDataLoader.gameIsAvailable(game.id)) {
            loadedGameButtons(holder, game)

        } else {
            downloadGameButtons(holder, game)
        }
    }

    override fun getItemCount(): Int {
        return games.size
    }
}