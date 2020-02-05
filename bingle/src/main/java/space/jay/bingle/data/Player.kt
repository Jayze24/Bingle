package space.jay.bingle.data

class Player(val mPlayerID: String, val mPlayerName: String, val mPlayerNumber: String) {

    var mTokens = ArrayList<Token>()
    val mGotNumber = ArrayList<Int>()
    val mToldNumber = ArrayList<Int>()

}