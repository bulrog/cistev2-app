package com.bulrog59.ciste2dot0.game.management

import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bulrog59.ciste2dot0.*
import com.google.firebase.auth.FirebaseAuth
import java.util.*
import kotlin.math.roundToInt


class GameListAdapter(private val gameMgtActivity: GameMgtActivity) :
    RecyclerView.Adapter<GameListAdapter.ViewHolder>() {


    private val gameDao = GameDao()
    private var gamesMetaData: List<GameMetaData> = listOf()

    private val gameDataManager = GamesDataManager(gameMgtActivity)

    fun getListOfGames() {
        val gamesList = gameDataManager.loadLocalGames()
        gameDao.getGames({
            Toast.makeText(
                gameMgtActivity,
                "${gameMgtActivity.getText(R.string.error_searching_game)}:${it.message}",
                Toast.LENGTH_LONG
            ).show()
        }) { games ->
            games.forEach { game ->
                if (!gamesList.map { it.id }.contains(game.id))
                    gamesList.add(game)
            }


            gamesMetaData = gamesList

            notifyDataSetChanged()
            gameMgtActivity.findViewById<RelativeLayout>(R.id.loadingPanel).visibility = View.GONE
        }
    }

    init {
        getListOfGames()


    }


    inner class ViewHolder(gameDetail: View) : RecyclerView.ViewHolder(gameDetail) {
        val gameNameText: TextView = gameDetail.findViewById(R.id.game_name)
        var remoteGame = true
        val progressBar: ProgressBar = gameDetail.findViewById(R.id.load_progress)
        val startButton: ImageButton = gameDetail.findViewById(R.id.start_game)
        val loadDeleteButton: ImageButton = gameDetail.findViewById(R.id.download_delete)
        val detailButton: ImageButton = gameDetail.findViewById(R.id.detail_button)
        val editButton: ImageButton = gameDetail.findViewById(R.id.edit_game)
        val shareButton: ImageButton = gameDetail.findViewById(R.id.share_button)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameListAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val gameView = inflater.inflate(R.layout.game_management_row, parent, false)
        return ViewHolder(gameView)
    }

    private fun <T> launchActivity(clazz: Class<T>, id: UUID?) {
        val intent = Intent(gameMgtActivity, clazz)
        intent.putExtra(ResourceManager.GAME_ID, id?.toString())
        gameMgtActivity.startActivity(intent)
    }

    private fun isGameOk(id: UUID?): Boolean {
        val error = gameDataManager.verifyGame(id)
        if (error != null) {
            Toast.makeText(gameMgtActivity, error, Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun startGame(id: UUID?) {
        if (isGameOk(id)) {
            gameMgtActivity.reviewIfAbortPossibleTransfer {
                launchActivity(
                    CisteActivity::class.java,
                    id
                )
            }
        }
    }

    private fun editGame(id: UUID?) {
        gameMgtActivity.reviewIfAbortPossibleTransfer {
            launchActivity(
                EditActivity::class.java,
                id
            )
        }

    }

    private fun displayError(errorMessage: Int, ex: Exception) {
        Toast.makeText(
            gameMgtActivity,
            "${gameMgtActivity.getText(errorMessage)}:${ex.message}",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun shareGame(gameMetaData: GameMetaData, holder: ViewHolder) {
        if (isGameOk(gameMetaData.id)){
            gameMgtActivity.increaseGameUnderTransfer()
            gameDataManager.shareGame(
                gameMetaData,
                { transfer, total ->
                    transferUpdate(holder.progressBar, transfer, total)
                },
                { e ->
                    gameMgtActivity.decreaseGameUnderTransfer()
                    displayError(R.string.error_uploading_game, e)
                    loadedGameButtons(holder, gameMetaData)
                }
            ) {
                gameMgtActivity.decreaseGameUnderTransfer()
                gameDao.updateGameEntry(it, gameMetaData, { e ->
                    displayError(R.string.error_uploading_game, e)
                    loadedGameButtons(holder, gameMetaData)
                }) {
                    Toast.makeText(gameMgtActivity, R.string.success_uploading_game, Toast.LENGTH_LONG)
                        .show()
                    loadedGameButtons(holder, gameMetaData)
                }

            }

        }
    }

    private fun transferUpdate(progressBar: ProgressBar, transferBytes: Long, totalBytes: Long) {
        progressBar.visibility = View.VISIBLE
        val progressValue = transferBytes * 100.0f / totalBytes
        progressBar.progress = progressValue.roundToInt()
    }

    private fun downloadGameButtons(
        holder: GameListAdapter.ViewHolder,
        gameMetaData: GameMetaData
    ) {
        holder.startButton.visibility = View.INVISIBLE
        holder.editButton.visibility = View.INVISIBLE
        holder.shareButton.visibility = View.INVISIBLE
        if (holder.remoteGame) {

            holder.loadDeleteButton.visibility = View.VISIBLE
            holder.loadDeleteButton.setOnClickListener {
                gameMgtActivity.increaseGameUnderTransfer()
                gameDataManager.loadGame(gameMetaData.id, gameMetaData.userId,
                    { transfer, total ->
                        transferUpdate(holder.progressBar, transfer, total)
                    }, { e ->
                        gameMgtActivity.decreaseGameUnderTransfer()
                        displayError(R.string.error_downloading_game, e)
                        downloadGameButtons(holder, gameMetaData)
                    }
                ) {
                    gameMgtActivity.decreaseGameUnderTransfer()
                    loadedGameButtons(holder, gameMetaData)
                }
                holder.loadDeleteButton.visibility = View.INVISIBLE
            }
            holder.loadDeleteButton.setImageResource(R.drawable.ic_download)
        }


    }

    private fun canUserShareTheGame(userId: String?): Boolean {
        val user = FirebaseAuth.getInstance().currentUser
        return userId == user?.uid && user?.displayName != null
    }

    private fun loadedGameButtons(holder: GameListAdapter.ViewHolder, gameMetaData: GameMetaData) {
        holder.startButton.visibility = View.VISIBLE
        holder.progressBar.visibility = View.INVISIBLE

        if (canUserShareTheGame(gameMetaData.userId)) {
            holder.shareButton.visibility = View.VISIBLE
        }

        if (holder.remoteGame) {
            holder.loadDeleteButton.setImageResource(R.drawable.ic_delete)
            holder.loadDeleteButton.visibility = View.VISIBLE
            holder.loadDeleteButton.setOnClickListener {
                deletionWithConfirmation(gameMetaData)
            }
            holder.editButton.visibility = View.VISIBLE

        } else {
            holder.editButton.visibility = View.INVISIBLE
        }
        holder.startButton.setOnClickListener { startGame(gameMetaData.id) }
        holder.editButton.setOnClickListener { editGame(gameMetaData.id) }
        holder.shareButton.setOnClickListener { shareGame(gameMetaData, holder) }

    }

    private fun deletionWithConfirmation(
        gameMetaData: GameMetaData
    ) {
        AlertDialog.Builder(gameMgtActivity)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setMessage(R.string.delete_item_message)
            .setPositiveButton(R.string.confirmation) { _, _ ->
                gameDataManager.eraseLocalGame(gameMetaData.id)
                getListOfGames()
            }
            .setNegativeButton(R.string.denial, null)
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

            val detailText = gameMgtActivity.findViewById<TextView>(R.id.game_details)
            detailText.visibility = View.VISIBLE
            detailText.text = game.gameDetails()
            detailText.setOnClickListener { it.visibility = View.GONE }
        }
    }

    override fun getItemCount(): Int {
        return gamesMetaData.size
    }
}