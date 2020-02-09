package space.jay.bingle.ui.game

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import space.jay.bingle.Constants
import space.jay.bingle.data.LiveDataObject
import space.jay.bingle.data.TypeBoard
import space.jay.bingle.databinding.ActivityGameBinding
import space.jay.bingle.ui.game.board.FragmentBoardBasic

class ViewModelGame(application: Application) : AndroidViewModel(application) {

    private lateinit var mFragmentManager: FragmentManager
    private lateinit var mBinding: ActivityGameBinding
    private lateinit var mTypeBoard: TypeBoard

    var mMoveToken = MutableLiveData<LiveDataObject>()

    fun setInit(manager: FragmentManager, binding: ActivityGameBinding, board: TypeBoard) {
        mFragmentManager = manager
        mBinding = binding
        setFragment(board)
    }

    public fun setFragment(board: TypeBoard) {
        mTypeBoard = board

        val fragment = when(mTypeBoard) {
            TypeBoard.BASIC -> FragmentBoardBasic.newInstance()
        }

        mFragmentManager.beginTransaction().replace(mBinding.frameLayoutGameContainerBoard.id, fragment).commit()
    }

    fun setMyTokenNumber(number: Int) {
        val bundle = Bundle().apply {
            this.putInt(Constants.Key.TOLD_NUMBER, number)
        }
        mMoveToken.value = LiveDataObject(Constants.Event.MY_TOKEN, bundle)
    }

    fun setOtherToken(token: String, tile: String) {
        val bundle = Bundle().apply {
            this.putString(Constants.Key.TOKEN, token)
            this.putString(Constants.Key.TILE, tile)
        }
        mMoveToken.value = LiveDataObject(Constants.Event.OTHER_TOKEN, bundle)
    }
}