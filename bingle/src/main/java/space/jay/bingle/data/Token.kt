package space.jay.bingle.data

import android.widget.ImageButton
import java.util.*

data class Token (
    val tokenView: ImageButton,
    val playerNumber: String,
    val tokenNumber: String,
    val startLocation: String,
    val endLocation: String
) {
    val mMovedTileName = Stack<String>()
}