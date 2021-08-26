package com.bulrog59.ciste2dot0

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class GameMgtActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
}