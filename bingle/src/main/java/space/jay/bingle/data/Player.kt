package space.jay.bingle.data

import java.util.*
import kotlin.collections.ArrayList

class Player(val mPlayerID: String, val mPlayerName: String, val mPlayerNumber: String) {

    var mTokens = ArrayList<BoardToken>()
    val mMoved = Stack<String>()
    val mGotNumber = ArrayList<Int>()
    val mToldNumber = ArrayList<Int>()

}