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
import space.jay.bingle.Constants
import space.jay.bingle.data.BoardTile
import space.jay.bingle.data.BoardToken
import space.jay.bingle.data.Player
import space.jay.bingle.modules.BoxUser
import space.jay.bingle.ui.game.ActivityGame
import space.jay.bingle.ui.game.ViewModelGame
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

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

    private lateinit var mPlayer: Player
    private var mSelectedToken: BoardToken? = null
    private var mMovableTileList = ArrayList<BoardTile>()
    private val mDirectionOfToken = HashMap<String, Queue<BoardTile>>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //todo player number 넣는 로직 구현 할 것!!
        mPlayer = Player(mBoxUser.getData().uid, mBoxUser.getData().name, "1")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //이동 할 넘버 들어오는 곳
        mViewModelGame.mMovingNumber.observe(this, Observer {
            mPlayer.mToldNumber.add(it)
            mPlayer.mTokens.forEach { token ->
                if (token.mMovedTileName.last() != token.endLocation) {
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

    private fun addTokens(tokenViews: Array<ImageButton>) {
        tokenViews.forEach {
            //토큰 초기화 세팅
            val values = it.tag.toString().split(Constants.Split.UNDERSCORE)
            val token = BoardToken(it, values[0], values[1], values[2], values[3]) //토큰 뷰, 플레이어 넘버, 토큰 넘버, 시작 위치, 종료 위치
            token.mMovedTileName.push(values[2])

            //플레이어 토큰이면 저장
            if (values[0] == mPlayer.mPlayerNumber) {
                //플레이어 토큰 추가
                mPlayer.mTokens.add(token)
                //토큰 클릭 리스너 달기
                setTokenClickListener(it)
            }

            //맵에 토큰 일괄로 넣기
            mMapToken[getTokenName(it.tag)] = token
            //기본적으로 클릭 불가능해야 함. 숫자 나올때만 토큰 뷰 클릭 할 수 있음
            //뷰의 클릭 리스너 보다 늦게 클릭어블이 세팅되어야 정상 작동함
            it.isClickable = false
        }
    }

    private fun setTokenClickListener(view: ImageButton) {
        view.setOnClickListener {
            mSelectedToken = mMapToken[getTokenName(view.tag)]!!
            mSelectedToken?.also { token ->
                showMovableTile(token.mMovedTileName.last(), mViewModelGame.mMovingNumber.value!!)
            } ?: Log.e(this.toString(), "No SelectedToken")
        }
    }

    private fun getTokenName(tag: Any): String {
        val values = tag.toString().split(Constants.Split.UNDERSCORE)
        return values[0] + values[1]
    }

    private fun addTiles(tileViews: Array<View>) {
        tileViews.forEach {
            //타일은 기본적으로 enabled false로 시작
            it.isEnabled = false

            //타일 태그 분기
            val values = it.tag.toString().split(Constants.Split.UNDERSCORE)
            val beforeTileNames = values[0].split(Constants.Split.COMMA)
            val tileName = values[1]
            val nextTileNames = values[2].split(Constants.Split.COMMA)

            //이동할 타일 클릭 리스너
            it.setOnClickListener { view ->
                //토큰 클릭 못하게 변경
                mPlayer.mTokens.forEach { token ->
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

            var nextTile = mDirectionOfToken[destinationTile.tileName]!!.poll()
            if (nextTile.tileName == "90") {
                nextTile = mMapTile[selectedToken.endLocation]!!
            }

            val set = ConstraintSet()
            set.clone(mConstraintLayout)
            set.centerVertically(selectedToken.tokenView.id, nextTile.tileView.id)
            set.centerHorizontallyRtl(selectedToken.tokenView.id, nextTile.tileView.id)
            val transition = AutoTransition()
            transition.duration = 200
            transition.addListener(
                onEnd = {
                    selectedToken.mMovedTileName.push(nextTile.tileName)
                    if (mDirectionOfToken[destinationTile.tileName]!!.isNotEmpty()) {
                        moveToTile(destinationTile)
                    } else {
                        mSelectedToken = null
                        mDirectionOfToken.clear()
                    }
                }
            )
            TransitionManager.beginDelayedTransition(mConstraintLayout, transition)
            set.applyTo(mConstraintLayout)
        }
    }

    private fun showMovableTile(tileName: String, move: Int) {
        clearMovableTileList()
        mDirectionOfToken.clear()

        //이동 가능한 타일 찾기
        mMapTile[tileName]?.also { tile ->
            if (move > 0) {
                //양수일때
                for (t in tile.nextTileNames) {
                    val direction = LinkedList<BoardTile>()
                    addForwardTile(direction, t, move, isShortcut(tileName))
                }
            } else if (move < 0) {
                //음수일때
                for (t in tile.beforeTileNames) {
                    val direction = LinkedList<BoardTile>()
                    addBackTile(direction, t, move)
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

    private fun addForwardTile(direction: LinkedList<BoardTile>, tileName: String, move: Int, shortcut: Boolean) {
        val tile: BoardTile = mMapTile[tileName]!!
        direction.offer(tile)

        if (move - 1 == 0) {
            mDirectionOfToken[tileName] = direction
            mMovableTileList.add(tile)
        } else {
            val nextTile = if (shortcut) {
                tile.nextTileNames[1]
            } else {
                tile.nextTileNames[0]
            }
            addForwardTile(direction, nextTile, move - 1, isShortcut(tileName))
        }
    }

    private fun isShortcut(tileName: String) : Boolean {
        return tileName == "22"
    }

    private fun addBackTile(direction: LinkedList<BoardTile>, tileName: String, move: Int) {
        val tile: BoardTile = mMapTile[tileName]!!
        direction.offer(tile)

        if (move + 1 == 0) {
            mDirectionOfToken[tileName] = direction
            mMovableTileList.add(tile)
        } else {
            for (t in tile.beforeTileNames) {
                val newDirection = LinkedList<BoardTile>()
                for (d in direction) {
                    newDirection.offer(d)
                }
                addBackTile(newDirection, t, move + 1)
            }
        }
    }

    private fun getTileName(tag: Any): String {
        return tag.toString().split(Constants.Split.UNDERSCORE)[1]
    }
}