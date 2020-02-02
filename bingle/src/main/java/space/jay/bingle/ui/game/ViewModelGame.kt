package space.jay.bingle.ui.game

import android.app.Application
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import space.jay.bingle.data.TypeBoard
import space.jay.bingle.databinding.ActivityGameBinding
import space.jay.bingle.ui.game.board.FragmentBoardBasic

class ViewModelGame(application: Application) : AndroidViewModel(application) {

    private lateinit var mFragmentManager: FragmentManager
    private lateinit var mBinding: ActivityGameBinding
    private lateinit var mTypeBoard: TypeBoard

    var mMovingNumber = MutableLiveData<Int>()

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

    fun setMovingNumber(number: Int) {
        mMovingNumber.value = number
    }
}