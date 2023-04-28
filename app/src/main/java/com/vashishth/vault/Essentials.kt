package com.vashishth.vault

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object Essentials {
    val db = Firebase.firestore
    val passCollection = db.collection("password")

}