package space.jay.bingle.ui.game.board

import android.content.Context
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.transition.addListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import dagger.android.support.AndroidSupportInjection
import space.jay.bingle.data.BoardTile
import space.jay.bingle.data.BoardToken
import space.jay.bingle.data.Player
import space.jay.bingle.modules.BoxUser
import space.jay.bingle.ui.game.ActivityGame
import space.jay.bingle.ui.game.ViewModelGame
import javax.inject.Inject

open class FragmentBoard : Fragment() {

    @Inject
    lateinit var mBoxUser: BoxUser
    private val mViewModelGame: ViewModelGame by lazy {
        activity?.run {
            (this as ActivityGame).getViewModelGame()
        } ?: throw Exception("Invalid ActivityGame")
    }

    //view
    private var mMapTile = HashMap<String, BoardTile>()
    private var mMapToken = HashMap<String, BoardToken>()
    private lateinit var mConstraintLayout: ConstraintLayout

    lateinit var mPlayer: Player
    private var mSelectedToken: BoardToken? = null
    private var mMovableTileList = ArrayList<BoardTile>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPlayer = Player(mBoxUser.getData().uid, mBoxUser.getData().name, "1")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mViewModelGame.mMovingNumber.observe(this, Observer {
            mPlayer.mToldNumber.add(it)
            mPlayer.mTokens.forEach { token ->
                if (token.tokenLocation != token.endLocation) {
                    token.tokenView.isClickable = true
                }
            }
        })
    }

    fun setInit(
        constraintLayout: ConstraintLayout,
        tileViews: Array<View>,
        tokenViews: Array<ImageButton>
    ) {
        mConstraintLayout = constraintLayout
        addTiles(tileViews)
        addTokens(tokenViews)
    }

    private fun getTileName(tag: Any): String {
        return tag.toString().split("_")[1]
    }

    private fun getTokenName(tag: Any): String {
        val values = tag.toString().split("_")
        return values[0] + values[1]
    }

    private fun addTokens(tokenViews: Array<ImageButton>) {
        tokenViews.forEach {
            val values = it.tag.toString().split("_")
            val token = BoardToken(it, "0", values[0], values[1], values[2], values[3])
            if (values[0] == mPlayer.mPlayerNumber) {
                mPlayer.mTokens.add(token)
                setTokenClickListener(it)
            }
            mMapToken[getTokenName(it.tag)] = token
            it.isClickable = false
        }
    }

    private fun setTokenClickListener(view: ImageButton) {
        view.setOnClickListener {
            mSelectedToken = mMapToken[getTokenName(view.tag)]!!
            mSelectedToken?.also { token ->
                showMovableTile(getLocation(token), mViewModelGame.mMovingNumber.value!!)
            } ?: Log.e(this.toString(), "No SelectedToken")
        }
    }

    private fun getLocation(token : BoardToken): String {
        return if (token.tokenLocation == "0"){
            token.startLocation
        } else {
            token.tokenLocation
        }
    }

    private fun addTiles(tileViews: Array<View>) {
        tileViews.forEach {
            //타일은 기본적으로 enabled false로 시작
            it.isEnabled = false

            //타일 태그 분기
            val values = it.tag.toString().split("_")
            val beforeTileNames = values[0].split(",")
            val tileName = values[1]
            val nextTileNames = values[2].split(",")

            //이동할 타일 클릭 리스너
            it.setOnClickListener { view ->
                //토큰 클릭 못하게 변경
                mPlayer.mTokens.forEach {token ->
                    token.tokenView.isClickable = false
                }
                //이동 가능한 타일 원복
                clearMovableTileList()
                //이동 시작
                moveToTile(mMapTile[getTileName(view.tag)]!!)
            }

            //타일맵에 타일 넣기
            val tile = BoardTile(tileName, it, beforeTileNames, nextTileNames)
            mMapTile[tileName] = tile
        }
    }

    private fun moveToTile(destinationTile: BoardTile) {

        mSelectedToken?.also { selectedToken ->

            var nowTile = mMapTile[getLocation(selectedToken)]!!
            var nextTileName = if (nowTile.nextTileNames.size == 1){
                nowTile.nextTileNames[0]
            } else {
                if (nowTile.tileName.toInt() / 10 < destinationTile.tileName.toInt() / 10) {
                    nowTile.nextTileNames[0]
                } else {
                    nowTile.nextTileNames[1]
                }
            }

            if (nextTileName == "90") {
                nextTileName = selectedToken.endLocation
            }
            var nextTile = mMapTile[nextTileName]!!

            val set = ConstraintSet()
            set.clone(mConstraintLayout)
            set.centerVertically(selectedToken.tokenView.id, nextTile.tileView.id)
            set.centerHorizontallyRtl(selectedToken.tokenView.id, nextTile.tileView.id)
            val transition = AutoTransition()
            transition.duration = 500
            transition.addListener (
                onEnd = {
                    selectedToken.tokenLocation = nextTileName
                    if (nextTileName != destinationTile.tileName
                        && !nowTile.tileName.startsWith("end")){
                        moveToTile(destinationTile)
                    }
                }
            )
            TransitionManager.beginDelayedTransition(mConstraintLayout, transition)
            set.applyTo(mConstraintLayout)
        }
    }

    private fun showMovableTile(tileName: String, move: Int) {
        clearMovableTileList()

        //이동 가능한 타일 찾기
        mMapTile[tileName]?.also { tile ->
            if (move > 0) {
                for (t in tile.nextTileNames) {
                    addMovableTile(t, move)
                }
            } else {
                if (mPlayer.mMoved.isNotEmpty()) {
                    addMovableTile(tile.tileName, move)
                } else {
                    for (t in tile.beforeTileNames) {
                        addMovableTile(t, move)
                    }
                }
            }
        }

        //이동 가능한 타일 enabled 설정
        mMovableTileList.forEach {
            it.tileView.isEnabled = true
        }
    }

    private fun clearMovableTileList() {
        mMovableTileList.forEach {
            it.tileView.isEnabled = false
        }
        mMovableTileList.clear()
    }

    private fun addMovableTile(tileName: String, move: Int) {
        var tile = mMapTile[tileName]!!
        val isPositive = move > 0
        var count = if (isPositive) move - 1 else move + 1

        while (count != 0) {
            if (isPositive) {
                //양수 일때 앞으로 이동
                tile = mMapTile[tile.nextTileNames[0]]!!
                --count
            } else {
                //음수 일때 뒤로 이동
                tile = if (mPlayer.mMoved.isNotEmpty()) {
                    mMapTile[mPlayer.mMoved.pop()]!!
                } else {
                    mMapTile[tile.beforeTileNames[0]]!!
                }
                ++count
            }
        }

        mMovableTileList.add(tile)
    }
}