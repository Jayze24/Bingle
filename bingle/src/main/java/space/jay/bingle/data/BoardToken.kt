package space.jay.bingle.data

import android.widget.ImageButton

data class BoardToken (
    val tokenView: ImageButton,
    var tokenLocation: String,
    val playerNumber: String,
    val tokenNumber: String,
    val startLocation: String,
    val endLocation: String
)