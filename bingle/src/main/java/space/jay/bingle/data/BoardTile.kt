package space.jay.bingle.data

import android.view.View

data class BoardTile(
    val tileName: String,
    val tileView: View,
    val beforeTileNames: List<String>,
    val nextTileNames: List<String>
)