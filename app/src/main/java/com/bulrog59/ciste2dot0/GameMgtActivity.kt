package com.bulrog59.ciste2dot0

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bulrog59.ciste2dot0.game.management.GameListAdapter
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class GameMgtActivity : AppCompatActivity() {

    fun loadFileFireStore(){
        println("trying firestore")
        val storage = Firebase.storage
        val referenceData =
            storage.getReferenceFromUrl("https://firebasestorage.googleapis.com/v0/b/cistes2dot0.appspot.com/o/test.txt?alt=media&token=5cd75cda-78a6-4385-8591-10398423ac42")
        referenceData.getBytes(1000)
            .addOnSuccessListener {
                println("here is the downloaded data:${String(it)}") }
            .addOnFailureListener {
                println("something went wrong") }
        println("done")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler(this));
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        setContentView(R.layout.game_management)
        val recyclerView = findViewById<RecyclerView>(R.id.games_list)
        recyclerView.adapter = GameListAdapter()
        recyclerView.layoutManager =LinearLayoutManager(this)



    }
}