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
import space.jay.bingle.data.Player
import space.jay.bingle.data.Tile
import space.jay.bingle.data.Token
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
    private var mMapTile = HashMap<String, Tile>()
    private var mMapToken = HashMap<String, Token>()
    private lateinit var mConstraintLayout: ConstraintLayout

    private lateinit var mPlayer: Player
    private var mSelectedToken: Token? = null
    private var mMovableTileList = ArrayList<Tile>() // 내 토큰이 이동 가능한 타일 리스트
    private val mDirectionOfToken = HashMap<String, Queue<Tile>>() //내 토큰이 이동할 경로 저장 맵

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
        mViewModelGame.mMoveToken.observe(this, Observer { liveDataObject ->
            liveDataObject.bundle?.also {bundle ->
                when (liveDataObject.event){
                    Constants.Event.MY_TOKEN -> {
                        //내 토큰의 이동 숫자 들어왔을 때
                        bundle.getInt(Constants.Key.TOLD_NUMBER).also { number ->
                            mPlayer.mToldNumber.add(number)
                            mPlayer.mTokens.forEach { token ->
                                if (token.mMovedTileName.last() != token.endLocation) {
                                    token.tokenView.isClickable = true
                                }
                            }
                        }
                    }
                    Constants.Event.OTHER_TOKEN -> {
                        bundle.getString(Constants.Key.TOKEN)?.also {
                            mSelectedToken = mMapToken[it]
                            //토큰이 이동하기 전 타일에서 저장되어 있는 현재 토큰 삭제
                            removeTokenAtTile()
                            //토큰 이동하기
                            bundle.getString(Constants.Key.TILE)?.let {tileName ->
                                moveToTile(mMapTile[tileName]!!, true)
                            }
                        }

                    }
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
            val token = Token(getTokenName(it.tag), it, values[0], values[1], values[2], values[3]) //토큰 이름, 토큰 뷰, 플레이어 넘버, 토큰 넘버, 시작 위치, 종료 위치
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
                mViewModelGame.mMoveToken.value?.bundle?.getInt(Constants.Key.TOLD_NUMBER)?.also {moveNumber ->
                    showMovableTile(token.mMovedTileName.last(), moveNumber)
                }
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
                //토큰이 이동하기 전 타일에서 저장되어 있는 현재 토큰 삭제
                removeTokenAtTile()
                //내 토큰 이동 시작
                moveToTile(mMapTile[getTileName(view.tag)]!!, false)
            }

            //타일맵에 타일 넣기
            val tile = Tile(tileName, it, beforeTileNames, nextTileNames)
            mMapTile[tileName] = tile
        }
    }

    private fun removeTokenAtTile() {
        mSelectedToken?.mMovedTileName?.last()?.also { tileName ->
            mMapTile[tileName]?.token = null
        }
    }

    private fun moveToTile(destinationTile: Tile, isMovingOnce: Boolean) {
        mSelectedToken?.also { selectedToken ->

            var nextTile = if (isMovingOnce) {
                destinationTile
            } else {
                mDirectionOfToken[destinationTile.tileName]!!.poll()
            }
            if (nextTile.tileName == "90") {
                //버켓엔드존으로 가는 토큰은 각 토큰에 맞는 자리 넣기
                nextTile = mMapTile[selectedToken.endLocation]!!
            }

            //목적지 까지 한번에 가는 애니메이션과 목적지 까지 다른 타일 거쳐 가는 애니매이션 두가지
            val transition = if (isMovingOnce) {
                getOtherTokenTransition(selectedToken, destinationTile)
            } else {
                getMyTokenTransition(selectedToken, nextTile, destinationTile)
            }

            //애니메이션 세팅
            val set = ConstraintSet()
            set.clone(mConstraintLayout)
            set.centerVertically(selectedToken.tokenView.id, nextTile.tileView.id)
            set.centerHorizontallyRtl(selectedToken.tokenView.id, nextTile.tileView.id)

            TransitionManager.beginDelayedTransition(mConstraintLayout, transition)
            set.applyTo(mConstraintLayout)
        }
    }

    private fun getMyTokenTransition(selectedToken: Token, nextTile: Tile, destinationTile: Tile) : AutoTransition {
        return AutoTransition().apply {
            this.duration = 200
            this.addListener(
                onEnd = {
                    selectedToken.mMovedTileName.push(nextTile.tileName)
                    if (mDirectionOfToken[destinationTile.tileName]!!.isNotEmpty()) {
                        moveToTile(destinationTile, false)
                    } else {
                        setDestinationTile(selectedToken, nextTile)
                        //이동 경로 초기화
                        mDirectionOfToken.clear()
                    }
                }
            )
        }
    }

    private fun getOtherTokenTransition(selectedToken: Token, destinationTile: Tile) : AutoTransition {
        return AutoTransition().apply {
            this.duration = 100
            this.addListener(
                onEnd = {
                    selectedToken.mMovedTileName.push(destinationTile.tileName)
                    setDestinationTile(selectedToken, destinationTile)
                }
            )
        }
    }

    /**
     * 토큰 목적지 도착시 그 뒤 해야 할 일 세팅
     * 1.목적지에 다른 토큰이 있는지 있으면 해당 토큰 버켓스타트존으로 되돌리기
     * 2.타일에 도착한 토큰 타일에 저장
     * 3.선택된 토큰 null 처리
     */
    private fun setDestinationTile(selectedToken: Token, destinationTile: Tile) {
        destinationTile.token?.also {
            if (destinationTile.tileName != it.endLocation) {
                //목적지 타일에 다른 토큰이 있을 경우 기존에 있던 타일은 버켓스타트존으로 이동 시키기
                mSelectedToken = mMapToken[it.tokenName]
                moveToTile(mMapTile[it.startLocation]!!,true)
                it.mMovedTileName.removeAllElements()
            }
        }
        //타일에 도착한 토큰 저장
        destinationTile.token = selectedToken
        //선택된 토큰 널처리
        mSelectedToken = null

        //todo 실제 운영할땐 해당 로그 지울 것
        printLog()
    }

    private fun showMovableTile(tileName: String, move: Int) {
        clearMovableTileList()
        mDirectionOfToken.clear()

        //이동 가능한 타일 찾기
        mMapTile[tileName]?.also { tile ->
            if (move > 0) {
                //양수일때
                for (t in tile.nextTileNames) {
                    val direction = LinkedList<Tile>()
                    addForwardTile(direction, t, move, isShortcut(tileName))
                }
            } else if (move < 0) {
                //음수일때
                for (t in tile.beforeTileNames) {
                    val direction = LinkedList<Tile>()
                    addBackTile(direction, t, move)
                }
            }
        }

        //이동 가능한 타일 enabled 설정
        mMovableTileList.forEach {
            var isEnabled = true
            for (token in mPlayer.mTokens) {
                if (token.mMovedTileName.last() == it.tileName) {
                    isEnabled = false
                }
            }

            it.tileView.isEnabled = isEnabled
        }
    }

    private fun clearMovableTileList() {
        mMovableTileList.forEach {
            it.tileView.isEnabled = false
        }
        mMovableTileList.clear()
    }

    private fun addForwardTile(direction: LinkedList<Tile>, tileName: String, move: Int, shortcut: Boolean) {
        val tile: Tile = mMapTile[tileName]!!
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

    private fun addBackTile(direction: LinkedList<Tile>, tileName: String, move: Int) {
        val tile: Tile = mMapTile[tileName]!!
        direction.offer(tile)

        if (move + 1 == 0) {
            mDirectionOfToken[tileName] = direction
            mMovableTileList.add(tile)
        } else {
            for (t in tile.beforeTileNames) {
                val newDirection = LinkedList<Tile>()
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

    private fun printLog() {
        Log.i("tile & token info", "===================================================")
        mMapTile.forEach {
            it.value.token?.let {token ->
                Log.i("tile info", "${it.key} : ${token.tokenName}")
            }
        }

        mMapToken.forEach {
            if (it.value.mMovedTileName.isNotEmpty()) {
                Log.i("token info", "${it.key} : ${it.value.mMovedTileName.last()}")
            }
        }
    }
}