package com.bulrog59.ciste2dot0.game.management

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bulrog59.ciste2dot0.CisteActivity
import com.bulrog59.ciste2dot0.R
import java.util.*
import kotlin.math.roundToInt


class GameListAdapter(private val cisteActivitiy: Activity) :
    RecyclerView.Adapter<GameListAdapter.ViewHolder>() {
    private var gamesMetaData: List<GameMetaData> = listOf()

    private val gameDataManager = GameDataManager(cisteActivitiy)

    private fun getListOfGames(){
        GameSearch().getGames({
            Toast.makeText(
                cisteActivitiy,
                "${cisteActivitiy.getText(R.string.error_searching_game)}:${it.message}",
                Toast.LENGTH_LONG
            ).show()
        }) {
            gamesMetaData = it
            gameDataManager.addLocalGames(gamesMetaData as MutableList<GameMetaData>)
            notifyDataSetChanged()
        }
    }

    init {
        getListOfGames()

    }


    inner class ViewHolder(gameDetail: View) : RecyclerView.ViewHolder(gameDetail) {
        val gameNameText = gameDetail.findViewById<TextView>(R.id.game_name)
        var gameName: String = ""
        var remoteGame = true
        val progressBar=gameDetail.findViewById<ProgressBar>(R.id.load_progress)
        val startButton = gameDetail.findViewById<ImageButton>(R.id.start_game)
        val loadDeleteButton = gameDetail.findViewById<ImageButton>(R.id.download_delete)
        val detailButton = gameDetail.findViewById<ImageButton>(R.id.detail_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameListAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val gameView = inflater.inflate(R.layout.game_management_row, parent, false)
        return ViewHolder(gameView)
    }

    private fun startGame(id: UUID?) {
        val intent = Intent(cisteActivitiy, CisteActivity::class.java)
        intent.putExtra(CisteActivity.GAME_ID, id?.toString())
        cisteActivitiy.startActivity(intent)

    }


    private fun downloadGameButtons(holder: GameListAdapter.ViewHolder, gameMetaData: GameMetaData) {
        holder.startButton.visibility = View.INVISIBLE
        if (holder.remoteGame) {
            holder.loadDeleteButton.visibility = View.VISIBLE
            holder.loadDeleteButton.setOnClickListener {
                gameDataManager.loadGame(gameMetaData.id,
                    { transfer, total ->
                        holder.progressBar.visibility=View.VISIBLE
                        val progressValue=transfer * 100.0f / total
                        holder.progressBar.progress=progressValue.roundToInt()
                    }, { e ->
                        Toast.makeText(
                            cisteActivitiy,
                            "${cisteActivitiy.getText(R.string.error_downloading_game)}:${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        downloadGameButtons(holder, gameMetaData)
                    }
                ) { loadedGameButtons(holder, gameMetaData) }
                holder.loadDeleteButton.visibility = View.INVISIBLE
            }
            holder.loadDeleteButton.setImageResource(R.drawable.ic_download)
        }


    }


    private fun loadedGameButtons(holder: GameListAdapter.ViewHolder, gameMetaData: GameMetaData) {
        holder.startButton.visibility = View.VISIBLE
        holder.progressBar.visibility=View.INVISIBLE
        if (holder.remoteGame) {
            holder.loadDeleteButton.setImageResource(R.drawable.ic_delete)
            holder.loadDeleteButton.visibility = View.VISIBLE
            holder.loadDeleteButton.setOnClickListener {
                deletionWithConfirmation(gameMetaData, holder)
            }
        }
        holder.startButton.setOnClickListener { startGame(gameMetaData.id) }
    }

    private fun deletionWithConfirmation(
        gameMetaData: GameMetaData,
        holder: ViewHolder
    ) {
        AlertDialog.Builder(cisteActivitiy)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setMessage(cisteActivitiy.resources.getString(R.string.delete_game_message))
            .setPositiveButton(
                cisteActivitiy.resources.getString(R.string.confirmation)
            ) { _, _ ->
                gameDataManager.eraseLocalGame(gameMetaData.id)
                getListOfGames()
            }
            .setNegativeButton(cisteActivitiy.resources.getString(R.string.denial), null)
            .show()
    }

    override fun onBindViewHolder(holder: GameListAdapter.ViewHolder, position: Int) {
        val game = gamesMetaData[position]
        holder.gameNameText.text = game.name
        holder.remoteGame = game.id != null

        if (!holder.remoteGame || gameDataManager.gameIsAvailable(game.id!!)) {
            loadedGameButtons(holder, game)

        } else {
            downloadGameButtons(holder, game)
        }
        holder.detailButton.setOnClickListener {

            val detailText = cisteActivitiy.findViewById<TextView>(R.id.game_details)
            detailText.visibility = View.VISIBLE
            detailText.text = game.gameDetails()
            detailText.setOnClickListener { it.visibility = View.GONE }
        }
    }

    override fun getItemCount(): Int {
        return gamesMetaData.size
    }
}